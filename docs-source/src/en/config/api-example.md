# API Basic Configs

> The basic configuration method of `YukiHookAPI` is introduced here.

## Function Configs

> Either [Use as Xposed Module Configs](../config/xposed-using) or [Use as Hook API Configs](../config/api-using), you can specify `YukiHookAPI` for configuration.

### configs Method

```kotlin:no-line-numbers
fun configs(initiate: Configs.() -> Unit)
```

The `configs` method implements a **lambda** method body on the `Configs` class, which you can easily call for configuration.

::: tip

For more functions, please refer to the [YukiHookAPI.configs](../api/public/com/highcapable/yukihookapi/YukiHookAPI#configs-method) method.

:::

## Hooker Configs

> The most important part of an Xposed Module or Hook API is the creation and use of Hooker. `YukiHookAPI` provides two ways to use it.

### Created by lambda

> This solution is the simplest. If your module has few functions and a small amount of code, and does not need to be classified, it is recommended to create it in this way.

#### encase Method

```kotlin:no-line-numbers
fun encase(initiate: PackageParam.() -> Unit)
```

The `encase` method is the beginning of all Hook life. In a Module App or a Hook process, the `encase` method can only be used once to create a Hooker.

`PackageParam` is an important instance object of the Host App, and `PackageParam` is used to implement all Hook operations on the current Hook object.

::: tip

For more functions, please refer to [PackageParam](../api/public/com/highcapable/yukihookapi/hook/param/PackageParam).

:::

The `encase` method can be created in the `onHook` method using two schemes.

> Sample Code 1

```kotlin
YukiHookAPI.encase {
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook {
            // Your code here.
        }
    }
}
```

> Sample Code 2

```kotlin
encase {
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook {
            // Your code here.
        }
    }
}
```

Do your Hook operations in the `encase` method.

### Created by Custom Hooker

> This scheme is more suitable for large-scale projects, such as the need to classify Hooker or classify the role of Hook.

#### encase Method

```kotlin:no-line-numbers
fun encase(vararg hooker: YukiBaseHooker)
```

Also for the `encase` method, the variable array parameter `hooker` of the method here provides an object for creating an entry, you can load all Hookers extends `YukiBaseHooker` at one time.

#### YukiBaseHooker Usage

`YukiBaseHooker` extends `PackageParam`, you need to extends your child Hooker from `YukiBaseHooker`.

::: tip

For more functions, please refer to [YukiBaseHooker](../api/public/com/highcapable/yukihookapi/hook/entity/YukiBaseHooker).

:::

> The following example

```kotlin
object CustomHooker : YukiBaseHooker() {

    override fun onHook() {
        // Your code here.
    }
}
```

Child Hooker **recommended** to use singleton `object` to create, you can also use `class` but it is generally not recommended.

::: warning

You don't need to re-call **encase** in the **onHook** method extends **YukiBaseHooker**, this is wrong and **will not take effect**, you should start writing your Hook code directly .

:::

> The following example

```kotlin
object CustomHooker : YukiBaseHooker() {

    override fun onHook() {
        loadApp(name = "com.example.demo1") {
            findClass(name = "$packageName.DemoClass").hook {
                // Your code here.
            }
        }
        loadApp(name = "com.example.demo2") {
            findClass(name = "$packageName.CustomClass").hook {
                // Your code here.
            }
        }
    }
}
```

As a child hooker, you can also call the `loadApp` method externally, and then directly start the Hook internally.

> The following example

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = encase {
        loadApp(name = "com.example.demo", ChildCustomHooker)
    }
}

object ChildCustomHooker : YukiBaseHooker() {

    override fun onHook() {
        findClass(name = "$packageName.DemoClass").hook {
            // Your code here.
        }
    }
}
```

You can use the `loadHooker` method to load another Hooker in multiple layers in the child Hooker, please do as you like.

> The following example

```kotlin
object FirstHooker : YukiBaseHooker() {

    override fun onHook() {
        findClass(name = "$packageName.DemoClass").hook {
            // Your code here.
        }
        loadHooker(SecondHooker)
        loadHooker(ThirdHooker)
    }
}
```

Once all Hookers are set up, you can load your Hooker in the `onHook` method of your Hook entry class.

> The following example

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() =
        YukiHookAPI.encase(FirstHooker, SecondHooker, ThirdHooker ...)
}
```

Of course, we can also abbreviate it.

> The following example

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = encase(FirstHooker, SecondHooker, ThirdHooker ...)
}
```

#### Special Case

As we mentioned above, it is generally not recommended to use `class` to create child Hookers, but there is a special case where it may still be necessary to keep your Hooker supporting multiple instantiations.

There is a rare possibility that there are multiple package names in a process.

In this case, when `YukiHookAPI` finds that the child Hooker is a singleton, it will ignore it and print a warning message.

```: no-line-numbers
This Hooker "HOOKER" is singleton or reused, but the current process has multiple package name "NAME", the original is "NAME"
Make sure your Hooker supports multiple instances for this situation
The process with package name "NAME" will be ignored
```

In this case, we only need to modify `object` to `class` or determine the package name during loading and then load the child Hooker.

For example, in the above cases, the following forms can be used to load.

> The following example

```kotlin
encase {
    // Assume this is the app package name and child Hooker you need to load
    loadApp("com.example.demo", YourCustomHooker)
}
```

### Expansion Features

If your current Hook Framework supports and enables the Resources Hook feature, you can now create Resources Hooks directly in `encase`.

You don't need to separate the `initZygote`, `handleLoadPackage`, `handleInitPackageResources` methods to perform different functions as before using the Xposed API.

In `YukiHookAPI`, these functions **are seamless**.

> The following example

```kotlin
encase {
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook {
            // Your code here.
        }
        // Create a Resources Hook (fixed usage)
        resources().hook {
            // Your code here.
        }
    }
}
```

You can also use the `loadZygote` method to load the first event `initZygote` after a new process has been forked.

> The following example

```kotlin
encase {
    loadZygote {
        ActivityClass.hook {
            // Your code here.
        }
        // Create a Resources Hook in Zygote
        resources().hook {
            // Your code here.
        }
    }
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook {
            // Your code here.
        }
        // Create a Resources Hook in the app
        resources().hook {
            // Your code here.
        }
    }
}
```

### Precautions

It is wrong to load Hooker directly or start Hook directly, `encase` event will go through three callbacks after being loaded by Hook Framework.

- load `initZygote` → `encase`

- load `handleLoadPackage` → `encase`

- load `handleInitPackageResources` → `encase`

In this process, you need to use `loadApp`, `loadSystem`, `loadZygote` to distinguish the calling domain of each loading code,
otherwise your code will be executed <u>**multiple times and cause errors**</u>.

::: warning

Whether you use **encase** to create the **lambda** method body or use the Hooker form directly, you should not directly load the Hooker or start the Hook directly in the first **onHook** event.

:::

Below are two **error** examples.

> Sample Code 1

```kotlin
encase {
    //  Wrong usage, can't start Hook directly
    findClass(name = "com.example.demo.DemoClass").hook {
        // ...
    }
    //  Wrong usage, can't start Hook directly
    resources().hook {
        // ...
    }
}
```

> Sample Code 2

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() {
        // <Scenario 1>
        encase {
            loadHooker(CustomHooker)
        }
        // <Scenario 2>
        encase(CustomHooker)
    }
}

object CustomHooker : YukiBaseHooker() {

    override fun onHook() {
        //  Wrong method of use
        // Because there is no judgment object in the outer layer, you cannot start Hook directly
        findClass(name = "com.example.demo.DemoClass").hook {
            // ...
        }
    }
}
```

Below is a **correct** example of the wrong example above.

> Sample Code 1

```kotlin
encase {
    // ✅ Correct usage, load in Zygote
    loadZygote(CustomHooker)
     // ✅ Correct usage, load in Zygote
    loadZygote {
        // ✅ Correct usage, Hook in Zygote
        resources().hook {
            // ...
        }
    }
    // ✅ The correct way to use it, use the app scope to load
    loadApp(/** name parameter optional */, hooker = CustomHooker)
    // ✅ The correct way to use it, load the Hooker after judging the scope of the app
    loadApp(/** name parameter optional */) {
        loadHooker(CustomHooker)
         // ✅ Correct usage, Hook in app scope
        findClass(name = "com.example.demo.DemoClass").hook {
            // ...
        }
        // ✅ Correct usage, Hook in app scope
        resources().hook {
            // ...
        }
    }
}
```

> Sample Code 2

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() {
        encase(CustomHooker)
    }
}

object CustomHooker : YukiBaseHooker() {

    override fun onHook() {
        // ✅ The correct method of use, since there is no judgment object in the outer layer
        // it is necessary to judge the scope of the app before performing Hook
        loadApp(/** name parameter optional */) {
            findClass(name = "com.example.demo.DemoClass").hook {
                // ...
            }
        }
    }
}
```

## Precautions when using as Hook API

If you are using it as a Hook API, then you only need to differentiate the `encase` method at the entry point.

::: warning

The **encase** method provides two identical methods for use as a Hook API, but with only one more parameter **baseContext** than the previous two.

:::

> Method 1

```kotlin:no-line-numbers
fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit)
```

> Method 2

```kotlin:no-line-numbers
fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker)
```

The `baseContext` here only needs to fill in the `Context` you got at `attachBaseContext`, and other usages are exactly the same as the above.

::: danger

Never use the **encase** method in an Xposed way without omitting the **baseContext** parameter, this will lead to your Hook not work at all.

The Resources Hook feature is not supported as Hook API.

:::