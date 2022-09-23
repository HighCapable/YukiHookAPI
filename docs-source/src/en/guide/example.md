# Usage Example

> Here is an introduction to the basic working method of `YukiHookAPI` and a list of simple Hook examples and common functions.

## Structure Diagram

> The structure below describes the basic working and principle of `YukiHookAPI`.

```:no-line-numbers
Host Environment
└── YukiMemberHookCreator
    └── Class
        └── MemberHookCreator
            └── Member
                ├── Before
                └── After
            MemberHookCreator
            └── Member
                ├── Before
                └── After
            ...
    YukiResourcesHookCreator
    └── Resources
        └── ResourcesHookCreator
            └── Drawable
                └── Replace
            ResourcesHookCreator
            └── Layout
                └── Inject
            ...
```

> The above structure can be written in the following form in code.

```kotlin
TargetClass.hook {
    injectMember {
        method {
            // Your code here.
        }
        beforeHook {
            // Your code here.
        }
        afterHook {
            // Your code here.
        }
    }
}
resources().hook {
    injectResource {
        conditions {
            // Your code here.
        }
        replaceTo(...)
    }
}
```

## Demo

> You can find the demo provided by the API below to learn how to use `YukiHookAPI`.

- Host App Demo [click here to view](https://github.com/fankes/YukiHookAPI/tree/master/demo-app)

- Module App Demo [click here to view](https://github.com/fankes/YukiHookAPI/tree/master/demo-module)

Install the Host App and Module App Demo at the same time, and test the hooked function in the Host App by activating the Module App.

## A Simple Hook Example

> Here are examples of Hook App, Hook System Framework and Hook Resources for reference.

### Hook App

Suppose, we want to hook the `onCreate` method in `com.android.browser` and show a dialog.

Add code in the body of the `encase` method.

> The following example

```kotlin
loadApp(name = "com.android.browser") {
    ActivityClass.hook {
        injectMember {
            method {
                name = "onCreate"
                param(BundleClass)
                returnType = UnitType
            }
            afterHook {
                AlertDialog.Builder(instance())
                    .setTitle("Hooked")
                    .setMessage("I am hook!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
```

At this point, the `onCreate` method will be successfully hooked and this dialog will show when every `Activity` in `com.android.browser` starts.

So, what should I do if I want to continue the Hook `onStart` method?

In the code just now, continue to insert an `injectMember` method body.

> The following example

```kotlin
loadApp(name = "com.android.browser") {
    ActivityClass.hook {
        injectMember {
            method {
                name = "onCreate"
                param(BundleClass)
                returnType = UnitType
            }
            afterHook {
                AlertDialog.Builder(instance())
                    .setTitle("Hooked")
                    .setMessage("I am hook!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
        injectMember {
            method {
                name = "onStart"
                emptyParam()
                returnType = UnitType
            }
            afterHook {
                // Your code here.
            }
        }
    }
}
```

For the `Class` that does not exist in the current project, you can use the `stub` method or the `findClass` method to get the class that needs to be hooked.

For example, I want to get `com.example.demo.TestClass`.

> The following example

```kotlin
findClass(name = "com.example.demo.TestClass").hook {
    injectMember {
        // Your code here.
    }
}
```

If `com.example.demo` is the app you want to hook, then the writing method can be simpler.

> The following example

```kotlin
findClass(name = "$packageName.TestClass").hook {
    injectMember {
        // Your code here.
    }
}
```

Some people may have started to say that `findClass` is a bit cumbersome in some scenarios.

Because some people may have the following needs.

> The following example

```kotlin
const val TestClass = "com.example.demo.TestClass"

TestClass.hook {
    injectMember {
        // Your code here.
    }
}
```

That's okay, you can also create a Hook directly using the string class name.

> The following example

```kotlin
"$packageName.TestClass".hook {
    injectMember {
        // Your code here.
    }
}
```

::: tip

For more functions, please refer to [MemberHookCreator](../api/public/com/highcapable/yukihookapi/hook/core/YukiMemberHookCreator#memberhookcreator-class).

:::

### Hook Zygote

The first event `initZygote` after the new process is forked when the app starts.

Suppose we want to globally Hook the `onCreate` event of an app `Activity`

Add code in the body of the `encase` method.

> The following example

```kotlin
loadZygote {
    ActivityClass.hook {
        injectMember {
            method {
                name = "onCreate"
                param(BundleClass)
                returnType = UnitType
            }
            afterHook {
                // Your code here.
            }
        }
    }
}
```

::: warning

The functionality performed in **loadZygote** is very limited, and the **loadZygote** method is rarely needed.

:::

### Hook System Framework

In `YukiHookAPI`, the implementation of the Hook System Framework is very simple.

Suppose, you want to get `ApplicationInfo` and `PackageInfo` and do something with them.

Add code in the body of the `encase` method.

> The following example

```kotlin
loadSystem {
    ApplicationInfoClass.hook {
        // Your code here.
    }
    PackageInfoClass.hook {
        // Your code here.
    }
}
```

::: danger

**loadZygote** is directly different from **loadSystem**, **loadZygote** will be loaded in **initZygote**, and the System Framework is regarded as **loadApp(name = "android")** and exists, To Hook the System Framework, you can use **loadSystem** directly.

:::

### Hook Resources

Suppose, we want to replace the content of `app_name` of type `string` in Hook `com.android.browser` with `123`.

Add code in the body of the `encase` method.

> The following example

```kotlin
loadApp(name = "com.android.browser") {
    resources().hook {
        injectResource {
            conditions {
                name = "app_name"
                string()
            }
            replaceTo("123")
        }
    }
}
```

If the current app has a title bar text set with `app_name`, it will become our `123`.

You can also replace the Hook App's Resources with the current Xposed Module's Resources.

Suppose, we want to continue to hook `ic_launcher` of type `mipmap` in `com.android.browser`.

> The following example

```kotlin
loadApp(name = "com.android.browser") {
    resources().hook {
        injectResource {
            conditions {
                name = "ic_launcher"
                mipmap()
            }
            replaceToModuleResource(R.mipmap.ic_launcher)
        }
    }
}
```

At this point, the icon of the target app will be replaced with the icon we set.

If you want to replace the Resources of the System Framework, you can do the same, just replace `loadApp` with `loadZygote`.

> The following example

```kotlin
loadZygote {
    resources().hook {
        // Your code here.
    }
}
```
::: tip

For more functions, please refer to [ResourcesHookCreator](../api/public/com/highcapable/yukihookapi/hook/core/YukiResourcesHookCreator#resourceshookcreator-class).

:::

### Remove Hook

The native Xposed provides us with a `XC_MethodHook.Unhook` function, which can remove the current Hook from the Hook queue, and `YukiHookAPI` can also implement this function.

The first way, save the `Result` instance of the current injected object, and call `remove` at the appropriate time and place to remove the injected object.

> The following example

```kotlin
// Set a variable to save the current instance
val hookResult = injectMember {
    method {
        name = "test"
        returnType = UnitType
    }
    afterHook {
        // ...
    }
}
// Call the following method when appropriate
hookResult.remove()
```

The second method, call `removeSelf` in the Hook callback method to remove itself.

> The following example

```kotlin
injectMember {
    method {
        name = "test"
        returnType = UnitType
    }
    afterHook {
        // Just call the following method directly
        removeSelf()
    }
}
```

::: tip

For more functions, please refer to [MemberHookCreator](../api/public/com/highcapable/yukihookapi/hook/core/YukiMemberHookCreator#memberhookcreator-class).

:::

## Exception Handling

> `YukiHookAPI` has redesigned the monitoring of exceptions, any exception will not be thrown during the hook process, to avoid interrupting the next hook process and causing the hook process to "die".

### Listen for Exceptions

You can handle exceptions that occur during the Hook method.

> The following example

```kotlin
injectMember {
    // Your code here.
}.result {
    // Handle the exception at the start of the hook
    onHookingFailure {}
    // Handle exceptions in the hook process
    onConductFailure { param, throwable -> }
    // Handle all exceptions
    onAllFailure {}
    // ...
}
```

This method also works in the Resources Hook.

> The following example

```kotlin
injectResource {
    // Your code here.
}.result {
    // Handle arbitrary exceptions when hooking
    onHookingFailure {}
    // ...
}
```

You can also handle exceptions that occur when the Hook's `Class` does not exist.

> The following example

```kotlin
TargetClass.hook {
    injectMember {
        // Your code here.
    }
}.onHookClassNotFoundFailure {
    // Your code here.
}
```

You can also handle exceptions when looking up methods.

> The following example

```kotlin
method {
    // Your code here.
}.onNoSuchMethod {
    // Your code here.
}
```

::: tip

For more functions, please refer to [MemberHookCreator.Result](../api/public/com/highcapable/yukihookapi/hook/core/YukiMemberHookCreator#result-class), [ResourcesHookCreator.Result](../api/public/com/highcapable/yukihookapi/hook/core/YukiResourcesHookCreator#result-class).

:::

Common exceptions that may occur are described here. For more information, please refer to [API Exception Handling](../config/api-exception).

### Throw an Exception

In some cases, you can **manually throw exceptions** to alert some functionality that there is a problem.

As mentioned above, the exception thrown in the `hook` method body will be taken over by the `YukiHookAPI` to avoid interrupting the next Hook process and causing the Hook process to "die".

Here's how these exceptions work when `YukiHookAPI` takes over.

> The following example

```kotlin
// <Scenario 1>
injectMember {
    method {
        throw RuntimeException("Exception Test")
    }
    afterHook {
        // ...
    }
}.result {
    // Can catch RuntimeException
    onHookingFailure {}
}
// <Scenario 2>
injectMember {
    method {
        // ...
    }
    afterHook {
        throw RuntimeException("Exception Test")
    }
}.result {
    // Can catch RuntimeException
    onConductFailure { param, throwable -> }
}
```

The above scenarios will only be processed in the (Xposed) Host App environment and will not have any impact on the host itself.

If we want to throw these exceptions directly to the Host App, the native Xposed provides us with the `param.throwable` method, and `YukiHookAPI` can also implement this function.

If you want to throw an exception directly to the Host App in the Hook callback method body, you can implement the following methods.

> The following example

```kotlin
injectMember {
    method {
        // ...
    }
    afterHook {
        RuntimeException("Exception Test").throwToApp()
    }
}
```

You can also throw exceptions directly in the Hook callback method body, and then mark the exception to be thrown to the Host App.

> The following example

```kotlin
injectMember {
    method {
        // ...
    }
    afterHook {
        throw RuntimeException("Exception Test")
    }.onFailureThrowToApp()
}
```

The above two methods can receive an exception at the Host App and cause the Host App process to crash.

::: warning

In order to ensure that the Hook calling domain and the calling domain within the Host App are isolated from each other, exceptions can only be thrown to the Host App in the **beforeHook** and **afterHook** callback method bodies.

:::

::: tip

For more functions, please refer to [Throwable.throwToApp](../api/public/com/highcapable/yukihookapi/hook/param/HookParam#throwable-throwtoapp-i-ext-method), [YukiMemberHookCreator.MemberMookCreator.HookCallback](../api/public/com/highcapable/yukihookapi/hook/core/YukiMemberHookCreator#hookcallback-class).

:::

## Status Monitor

People who use `XposedHelpers` often print `Unhook` after the Hook to determine whether the Hook is successful.

In `YukiHookAPI`, you can easily reimplement this functionality with the following methods.

First we can monitor that the Hook is ready to start.

> The following example

```kotlin
YourClass.hook {
    // Your code here.
}.onPrepareHook {
    loggerD(msg = "$instanceClass hook start")
}
```

::: danger

**instanceClass** is recommended to be used only in **onPrepareHook**, otherwise the Hook's **Class** does not exist and an uninterceptable exception will be thrown, causing the Hook process to "die".

:::

Then, we can also monitor the success of the method result of the Hook.

> The following example

```kotlin
injectMember {
    // Your code here.
}.onHooked { member ->
    loggerD(msg = "$member has hooked")
}
```

## Expansion Usage

> You can use the following methods to easily implement various judgments and functions in the Hook process.

### Multiple Hosts

If your Module App needs to handle Hook events of multiple apps at the same time, you can use the `loadApp` method body to distinguish the app you want to hook.

> The following example

```kotlin
loadApp(name = "com.android.browser") {
    // Your code here.
}
loadApp(name = "com.android.phone") {
    // Your code here.
}
```

::: tip

For more functions, please refer to [PackageParam.loadApp](../api/public/com/highcapable/yukihookapi/hook/param/PackageParam#loadapp-method).

:::

### Multiple Processes

If your Hook's Host App has multiple processes, you can use the `withProcess` method body to hook them separately.

> The following example

```kotlin
withProcess(mainProcessName) {
    // Your code here.
}
withProcess(name = "$packageName:tool") {
    // Your code here.
}
```

::: tip

For more functions, please refer to [PackageParam.withProcess](../api/public/com/highcapable/yukihookapi/hook/param/PackageParam#withprocess-method).

:::

## Writing Optimization

To make the code more concise, you can omit the name of `YukiHookAPI` and write your `onHook` entry as `lambda`.

> The following example

```kotlin
override fun onHook() = encase {
    // Your code here.
}
```

## Xposed Module own Active State

Usually, we choose to write a method that return `false`, and then Hook this method to return `true` to prove that the Hook has taken effect.

In `YukiHookAPI`, you don't need to do this at all. `YukiHookAPI` has already encapsulated this operation for you, and you can use it directly.

Now, you can use `YukiHookAPI.Status.isXposedModuleActive` directly in the Module App to determine whether it is active.

> The following example

```kotlin
if(YukiHookAPI.Status.isXposedModuleActive) {
    // Your code here.
}
```

Due to some special reasons, Module Apps in TaiChi and Wuji cannot use standard methods to detect the activation state.

At this point, you can use `YukiHookAPI.Status.isTaiChiModuleActive` to determine whether it is activated.

> The following example

```kotlin
if(YukiHookAPI.Status.isTaiChiModuleActive) {
    // Your code here.
}
```

If you want to use both judgment schemes, `YukiHookAPI` also encapsulates a convenient way for you.

At this point, you can use `YukiHookAPI.Status.isModuleActive` to determine whether you are activated in Xposed or TaiChi and Promise.

> The following example

```kotlin
if(YukiHookAPI.Status.isModuleActive) {
    // Your code here.
}
```

::: tip

For more functions, please refer to [YukiHookAPI.Status](../api/public/com/highcapable/yukihookapi/YukiHookAPI#status-object).

:::

::: warning

If the activation state of TaiChi and Wuji is included in the Module App activation judgment, the **Application** of the Module App must be extends **ModuleApplication** or **ModuleApplication** must be used directly;

The API after **1.0.91** has modified the activation logic judgment method, now you can use this API in the Module App and Host App at the same time;

Need to make sure **YukiHookAPI.Configs.isEnableHookModuleStatus** is enabled;

Except for Hook Frameworks that provide standard APIs, Module Apps may not be able to determine whether they are activated in other cases.

:::