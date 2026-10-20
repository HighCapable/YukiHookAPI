# Module and Host Channel

> This is a solution that uses unordered system broadcasts to send and receive data between the module and hosts.

::: danger Conditions that need to be met

The module and the host need to stay alive, otherwise the channel cannot be established.

The `Application` of the module needs to inherit `ModuleApplication`, and `YukiHook.Config.dataChannel` needs to be enabled (enabled by default).

:::

## Basic Usage

Here describes the basic usage of the `wait` and `put` functions.

Use `dataChannel` to create a channel between the module and hosts, it works by sending and receiving unordered system broadcasts.

> Module example

```kotlin
// Get from the host with the given package name.
dataChannel("io.github.myapp").wait<String>("key-from-host") { value ->
    // Your code here.
}
// Send to the host with the given package name.
dataChannel("io.github.myapp").put("key-from-module", "I am module")
```

> Host example

```kotlin
// Get from the module.
dataChannel.wait<String>("key-from-module") { value ->
    // Your code here.
}
// Send to the module.
dataChannel.put("key-from-host", "I am host")
```

You can omit the `value` of `dataChannel` to only notify the `wait` callback of the module or host.

> Module example

```kotlin
// Get from the host with the given package name.
dataChannel("io.github.myapp").wait("listening-from-host") {
    // Your code here.
}
// Send to the host with the given package name.
dataChannel("io.github.myapp").put("listening-from-module")
```

> Host example

```kotlin
// Get from the module.
dataChannel.wait("listening-from-module") {
    // Your code here.
}
// Send to the module.
dataChannel.put("listening-from-host")
```

To communicate with the system framework, use `systemDataChannel` in the module, and use `dataChannel` inside `loadSystem` in the host.

> The following example

```kotlin
systemDataChannel().put("key-from-module", "I am module")
```

::: warning

Passing `android` or `system` to `dataChannel` is treated as `systemDataChannel`, please use `systemDataChannel` directly.

:::

If you need to send a lot of fixed data, it is recommended to create templates with `ChannelData`.

> The following example

```kotlin
object MyData {
    val Foo = ChannelData<String>("foo")
}
```

> The following example

```kotlin
// Send data.
dataChannel("io.github.myapp").put(MyData.Foo, "I am module")
// Receive data.
dataChannel.wait(MyData.Foo) { value ->
    // Your code here.
}
```

## Check Version Match

YukiHook provides an instant solution for checking whether the module matches the host version after the user updates the module.

Just call the `checkVersionMatch` function to do it.

The check works in both directions between the module and the host.

In the module, you can check whether the host with the given package name matches the current module version.

> The following example

```kotlin
// Get from the host with the given package name.
dataChannel("io.github.myapp").checkVersionMatch { isMatched ->
    // Your code here.
}
```

In the host, you can also check whether it matches the current module version.

> The following example

```kotlin
// Get from the module.
dataChannel.checkVersionMatch { isMatched ->
    // Your code here.
}
```

::: warning

If the conditions are not met, or the module has been deactivated without restarting the host, the callback will never be triggered.

:::

## Get Host Logs

You can get the debug logs recorded in a host from the module.

> The following example

```kotlin
dataChannel("io.github.myapp").obtainLoggerInMemoryData { data ->
    // Your code here.
}
```

::: warning

Both the module and the host need to enable debug log recording, for details please refer to [Debug Logs → Save Logs](./logger.md#save-logs).

:::

## Respond to Callback Events

Only examples in the module are listed here. The same `key` is never allowed to be created twice for **the same host**, but the same `key` is allowed for **different hosts**.

::: danger

In the module and hosts, the callback event of each `key` of a `dataChannel` **cannot be created twice**. If it is, the earlier callback is replaced by the new one.
In the module, it cannot be repeated in the same `Activity`, but the same `key` is allowed in different `Activity` instances.

:::

> The following example

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Callback event A.
        dataChannel("io.github.myapp1").wait("foo") {
            // Your code here.
        }
        // Callback event B.
        dataChannel("io.github.myapp1").wait("foo") {
            // Your code here.
        }
        // Callback event C.
        dataChannel("io.github.myapp1").wait("bar") {
            // Your code here.
        }
        // Callback event D.
        dataChannel("io.github.myapp2").wait("bar") {
            // Your code here.
        }
    }
}

class OtherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Callback event E.
        dataChannel("io.github.myapp1").wait("foo") {
            // Your code here.
        }
        // Callback event F.
        dataChannel("io.github.myapp2").wait("foo") {
            // Your code here.
        }
    }
}
```

In the example above, callback events A and B belong to the same host and have the same `key`, so callback event A is replaced by callback event B.

The `key` of callback event C is not repeated. Callback event D has the same `key` as C, but they belong to different hosts, so they can exist at the same time.

Callback event E is in another `Activity`, and callback event F has the same `key` as E, but they do not belong to the same host either, so they can exist at the same time.

In summary, callback events B, C, D, E and F can all be created.

::: danger

A callback event with the same `key` is only called in the `Activity` of the module that is currently showing. For example, for `foo` above, if `OtherActivity` is showing, `foo` in `MainActivity` is not called.

When the same `key` is registered with `dataChannel` in different `Fragment` instances of the same `Activity`, they are still called together in the current `Activity`.

In the module, you can use `dataChannel` in an `Activity`, `Application`, `Service` and so on. Outside an `Activity`, each callback event is called immediately, and you can use `ChannelPriority` to manage them.

To use `dataChannel` in a `Fragment`, use `activity?.dataChannel(...)` or `requireActivity().dataChannel(...)`.

:::

If you want to set the response priority (condition) of each callback event in the same `Activity` manually, you can use `ChannelPriority`.

For example, when one `Activity` hosts multiple `Fragment` instances, this solves the problem.

> The following example

```kotlin
open class BaseFragment : Fragment() {

    // Marks that the current Fragment has gone through the onResume lifecycle.
    var resumed = false

    override fun onResume() {
        super.onResume()
        resumed = true
    }

    override fun onPause() {
        super.onPause()
        resumed = false
    }
}

class MainFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Use the custom resumed with isVisible to check that the current Fragment is showing.
        activity?.dataChannel("io.github.myapp1")
            ?.wait("foo", ChannelPriority { resumed && isVisible }) {
                // Your code here.
            }
    }
}
```

## Data Size Limits

Android limits the size of data a broadcast can carry. YukiHook splits `List`, `Map`, `Set` and `String` data into segments automatically when the data exceeds `maxDataSizeBytes` (500 KB by default).

> The following example

```kotlin
dataChannel("io.github.myapp").with {
    // Adjust the threshold for segmented sending, the minimum is 100 KB.
    maxDataSizeBytes = 500 * 1024
    // Adjust the divisor for the size of each segment, the minimum is 2.
    segmentSizeDivisor = 3
}
```

::: danger

Adjust these values carefully, exceeding the system limit causes a `TransactionTooLargeException`.

You can also use `allowOversizedData` to remove the data size limit, do not use it unless you understand the consequences.

:::

## Security Notes

In the module environment, you can only receive data sent by <u>**the host with the given package name**</u> and only send data to <u>**the host with the given package name**</u>.

::: danger

YukiHook specifies the package names of the host and the module automatically, so other apps cannot receive or intercept this data.

:::