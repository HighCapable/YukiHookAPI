# Xposed Module and Host Channel

> This is a solution that uses system out-of-order broadcasting to send and receive data between the Module App and the Host App.

::: danger Condition that needs to be met

The Module App and the Host App need to remain alive, otherwise communication cannot be established.

:::

## Basic Usage

> The basic usage of the `wait` and `put` methods is described here.

By using `dataChannel` to realize the communication bridge between the Module App and the Host App, the principle is to send and receive system out-of-order broadcasts.

> The Module App example is as follows

```kotlin
// Get from the Host App of the specified package name
dataChannel(packageName = "com.example.demo").wait<String>(key = "key_from_host") { value ->
    // Your code here.
}
// Send to the Host App with the specified package name
dataChannel(packageName = "com.example.demo").put(key = "key_from_module", value = "I am module")
```

> The Host App example is as follows

```kotlin
// Get from the Module App
dataChannel.wait<String>(key = "key_from_module") { value ->
    // Your code here.
}
// Send to the Module App
dataChannel.put(key = "key_from_host", value = "I am host")
```

You can leave the `value` of `dataChannel` unset to only notify the Module App or Host App to call back the `wait` method.

> The Module App example is as follows

```kotlin
// Get from the Host App of the specified package name
dataChannel(packageName = "com.example.demo").wait(key = "listener_from_host") {
    // Your code here.
}
// Send to the Host App with the specified package name
dataChannel(packageName = "com.example.demo").put(key = "listener_from_module")
```

> The Host App example is as follows

```kotlin
// Get from the Module App
dataChannel.wait(key = "listener_from_module") {
    // Your code here.
}
// Send to the Module App
dataChannel.put(key = "listener_from_host")
```

::: danger

The receiver needs to stay alive to receive the communication data.

:::

::: tip

For more functions, please refer to [YukiHookDataChannel](../public/com/highcapable/yukihookapi/hook/xposed/channel/YukiHookDataChannel).

:::

## Determine Module App and Host App Version Match

> Through the communication bridge function, `YukiHookAPI` also provides a solution for you to determine whether the Module App matches the Host App version after the user updates the Module App.

We only need to call the `checkingVersionEquals` method to achieve this function.

Bidirectional judgment can be performed between the Module App and the Host App.

You can check in the Module App whether the Host App of the specified package name matches the version of the current Module App.

> The following example

```kotlin
// Get from the Host App of the specified package name
dataChannel(packageName = "com.example.demo").checkingVersionEquals { isEquals ->
    // Your code here.
}
```

You can also determine in the Host App whether it matches the current Module App version.

> The following example

```kotlin
// Get from the Module App
dataChannel.checkingVersionEquals { isEquals ->
    // Your code here.
}
```

::: warning Condition of method callback

The Host App and Module App must be stay alive, and after activating the Module App restart the Hook target Host App object in scope.

:::

::: tip

For more functions, please refer to [YukiHookDataChannel](../public/com/highcapable/yukihookapi/hook/xposed/channel/YukiHookDataChannel).

:::

## Rules for Callback Event Response

Only the examples used in Module App are listed here.

The same `key` in **same Host App** is always not allowed to be created repeatedly, but the same `key` is allowed in **different Host Apps**.

::: danger

In the Module App and Host App, each **key** callback event corresponding to **dataChannel** is not allowed to be repeatedly created, if repeated, the previous callback event will be replaced by the newly added callback event.

When used in the Module App, it cannot be repeated in the same **Activity**, and the same **key** in different **Activity** is allowed to be repeated.

:::

> The following example

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Callback event A
        dataChannel(packageName = "com.example.demo1").wait(key = "test_key") {
            // Your code here.
        }
        // Callback event B
        dataChannel(packageName = "com.example.demo1").wait(key = "test_key") {
            // Your code here.
        }
        // Callback event C
        dataChannel(packageName = "com.example.demo1").wait(key = "other_test_key") {
            // Your code here.
        }
        // Callback event D
        dataChannel(packageName = "com.example.demo2").wait(key = "other_test_key") {
            // Your code here.
        }
    }
}

class OtherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Callback event E
        dataChannel(packageName = "com.example.demo1").wait(key = "test_key") {
            // Your code here.
        }
        // Callback event F
        dataChannel(packageName = "com.example.demo2").wait(key = "test_key") {
            // Your code here.
        }
    }
}
```

In the above example, although callback events A and B are callback events in the same Host App, their `key` is the same, so callback event A will be replaced by callback event B.

The `key` of callback event C is not duplicated with others.

Although the `key` of callback event D is the same as that of callback event C, their Host Apps are different, so they can exist at the same time.

Callback event E is in another **Activity**, although the `key` of callback event F and callback event E is the same, but they are not the same Host App, so they can exist at the same time.

In summary, the final callback events B, C, D, E, and F can all be created successfully.

::: tip Compatibility Notes

Setting the same **key** on different Host Apps in previous historical versions of the API would result in only the last event callback, but the latest version has corrected this problem, please make sure you are using the latest API version.

:::

::: danger

A callback event with the same **key** will only call back the callback event registered in the **Activity** that the current Module App is displaying, such as **test_key** in the above, if **OtherActivity** is being displayed, then **test_key** in **MainActivity** will not be called back.

The same **key** registers **dataChannel** in the same **Activity** but different **Fragment**, they will still be called back in the current **Activity** at the same time.

In a Module App, you can use **dataChannel** in **Activity**, **Application** and **Service**, when used in places other than **Activity**, each callback event will instant callback, at which point you can use **ChannelPriority** to manage.

If you want to use **dataChannel** in **Fragment**, use **activity?.dataChannel(...)**.

:::

If you want to manually set the response priority (condition) of each callback event in the same **Activity**, you can use `ChannelPriority`.

For example, if you are using one **Activity** binding multiple **Fragment** cases, this will be able to solve this problem.

> The following example

```kotlin
open class BaseFragment : Fragment() {

    /** Identify that the current Fragment is in the onResume lifecycle */
    var isResume = false

    override fun onResume() {
        super. onResume()
        isResume = true
    }

    override fun onPause() {
        super. onPause()
        isResume = false
    }

    override fun onStop() {
        super. onStop()
        isResume = false
    }
}

class FragmentA : BaseFragment() {

    // Omit part of initialization code
    //...

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Use the custom isResume combined with the isVisible condition
        // To judge that the current Fragment is in the displayed state
        activity?.dataChannel(packageName = "com.example.demo1")
            ?.wait(key = "test_key", ChannelPriority { isResume && isVisible }) {
                // Your code here.
            }
    }
}

class FragmentB : BaseFragment() {

    // Omit part of initialization code
    //...

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Use the custom isResume combined with the isVisible condition
        // To judge that the current Fragment is in the displayed state
        activity?.dataChannel(packageName = "com.example.demo2")
            ?.wait(key = "test_key", ChannelPriority { isResume && isVisible }) {
                // Your code here.
            }
    }
}
```

## Security Instructions

In the module environment, you can only receive the communication data sent by <u>**the Host App of the specified package name**</u> and can only send to <u>**the Host App of the specified package name**</u>, except for System Framework.

::: danger

In order to further prevent broadcast abuse, the API in the communication data will automatically specify the package name of the Host App and Module App to prevent other apps from monitoring and using broadcast to make overrun behaviors.

:::