# API 基本配置

> 这里介绍了 `YukiHookAPI` 的基本配置方法。

## 功能配置

> 无论是 [作为 Xposed 模块使用](../config/xposed-using) 还是 [作为 Hook API 使用](../config/api-using)，你都可以在 API 装载之前或装载过程中对 `YukiHookAPI` 进行配置。

### configs 方法

```kotlin:no-line-numbers
fun configs(initiate: Configs.() -> Unit)
```

`configs` 方法对 `Configs` 类实现了一个 **lambda** 方法体，你可以轻松地调用它进行配置。

::: tip

更多功能请参考 [YukiHookAPI.configs](../api/public/com/highcapable/yukihookapi/YukiHookAPI#configs-method) 方法。

:::

## Hooker 配置

> 一个 Xposed 模块或 Hook API 最重要的地方就是 Hooker 的创建与使用，`YukiHookAPI` 提供了两种使用方法。

### 通过 lambda 创建

> 这种方案是最简单的，如果你的模块功能不多，代码数量不大，不需要进行分类处理，推荐使用这种方式进行创建。

#### encase 方法

```kotlin:no-line-numbers
fun encase(initiate: PackageParam.() -> Unit)
```

`encase` 方法是 Hook 一切生命的开始，在一个模块或一个 Hook 过程中，`encase` 方法只能作用一次，用于创建 Hooker。

`PackageParam` 为宿主(目标 APP)的重要实例对象，通过 `PackageParam` 来实现对当前 Hook 作用对象的全部 Hook 操作。

::: tip

更多功能请参考 [PackageParam](../api/public/com/highcapable/yukihookapi/hook/param/PackageParam)。

:::

`encase` 方法可以在 `onHook` 方法中使用两种方案创建。

> 示例代码 1

```kotlin
YukiHookAPI.encase {
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook { 
            // Your code here.
        }
    }
}
```

> 示例代码 2

```kotlin
encase {
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook { 
            // Your code here.
        }
    }
}
```

在 `encase` 方法中进行你的 Hook 操作。

### 通过自定义 Hooker 创建

> 这种方案更加适用于大型项目，例如需要对 Hooker 进行分类或对 Hook 的作用对象进行分类。

#### encase 方法

```kotlin:no-line-numbers
fun encase(vararg hooker: YukiBaseHooker)
```

同样为 `encase` 方法，这里的方法可变数组参数 `hooker` 为创建入口提供了一个对象，你可以将所有继承于 `YukiBaseHooker` 的 Hooker 一次性进行装载。

#### YukiBaseHooker 用法

`YukiBaseHooker` 继承于 `PackageParam`，你需要将你的子 Hooker 继承于 `YukiBaseHooker`。

::: tip

更多功能请参考 [YukiBaseHooker](../api/public/com/highcapable/yukihookapi/hook/entity/YukiBaseHooker)。

:::

> 示例如下

```kotlin
object CustomHooker : YukiBaseHooker() {

    override fun onHook() {
        // Your code here.
    }
}
```

子 Hooker **建议使用**单例 `object` 创建，你也可以使用 `class` 但一般情况下不推荐。

::: warning

你无需再在继承于 **YukiBaseHooker** 的 **onHook** 方法中重新调用 **encase**，这是错误的，且**不会生效**，你应该直接开始编写你的 Hook 代码。

:::

> 示例如下

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

作为子 Hooker 使用，你还可以在外部调用 `loadApp` 方法，然后在内部直接开始 Hook。

> 示例如下

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

你可以使用 `loadHooker` 方法在子 Hooker 中多层装载另一个 Hooker，请按照你的喜好进行即可。

> 示例如下

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

搭建完全部 Hooker 后，你就可以在你的 Hook 入口类中的 `onHook` 方法中装载你的 Hooker 了。

> 示例如下

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = 
        YukiHookAPI.encase(FirstHooker, SecondHooker, ThirdHooker ...)
}
```

当然，我们同样可以对其进行简写。

> 示例如下

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = encase(FirstHooker, SecondHooker, ThirdHooker ...)
}
```

#### 特殊情况

上面我们说到，在一般情况下不推荐使用 `class` 创建子 Hooker，但是有一种特殊情况，它可能依然需要保持你的 Hooker 支持多例。

有极少的可能性会出现在一个进程中存在多个包名的情况，这种情况下，`YukiHookAPI` 发现子 Hooker 为单例时，将会忽略并打印一条警告信息。

```:no-line-numbers
This Hooker "HOOKER" is singleton or reused, but the current process has multiple package name "NAME", the original is "NAME"
Make sure your Hooker supports multiple instances for this situation
The process with package name "NAME" will be ignored
```

遇到这种情况时，我们只需要修改 `object` 为 `class` 或者在装载时判断包名后再装载子 Hooker。

例如以上情况中可使用以下形式来装载。

> 示例如下

```kotlin
encase {
    // 假设这个就是你需要装载的 APP 包名和子 Hooker
    loadApp("com.example.demo", YourCustomHooker)
}
```

### 扩展特性

如果你当前使用的 Hook Framework 支持并启用了资源钩子(Resources Hook)功能，你现在可以直接在 `encase` 中创建 Resources Hook。

你完全不需要与之前在使用 Xposed API 那样区分 `initZygote`、`handleLoadPackage`、`handleInitPackageResources` 方法来执行不同的功能。

在 `YukiHookAPI` 中，这些功能**是无缝的**。

> 示例如下

```kotlin
encase {
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook { 
            // Your code here.
        }
        // 创建一个 Resources Hook (固定用法)
        resources().hook {
            // Your code here.
        }
    }
}
```

你还可以同时使用 `loadZygote` 方法来装载新的进程被 fork 后的第一个事件 `initZygote`。

> 示例如下

```kotlin
encase {
    loadZygote {
        ActivityClass.hook { 
            // Your code here.
        }
        // 在 Zygote 中创建 Resources Hook
        resources().hook {
            // Your code here.
        }
    }
    loadApp(name = "com.example.demo") {
        findClass(name = "$packageName.DemoClass").hook { 
            // Your code here.
        }
        // 在 APP 中创建 Resources Hook
        resources().hook {
            // Your code here.
        }
    }
}
```

### 注意事项

直接装载 Hooker 或直接开始 Hook 是错误的，`encase` 事件在被 Hook Framework 装载后，会经历三次回调。

- 装载 `initZygote` → `encase`

- 装载 `handleLoadPackage` → `encase`

- 装载 `handleInitPackageResources` → `encase`

在这个过程中，你需要使用 `loadApp`、`loadSystem`、`loadZygote` 来区分每一次装载代码的调用域，否则你的代码就会被<u>**多次执行造成错误**</u>。

::: warning

无论使用 **encase** 创建 **lambda** 方法体还是直接使用 Hooker 形式，你都不应该直接在首个 **onHook** 事件中直接装载 Hooker 或直接开始 Hook。

:::

下面是两个**错误**示例。

> 示例代码 1

```kotlin
encase {
    // 错误的使用方法，不能直接开始 Hook
    findClass(name = "com.example.demo.DemoClass").hook {
        // ...
    }
    // 错误的使用方法，不能直接开始 Hook
    resources().hook {
        // ...
    }
}
```

> 示例代码 2

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() {
        // <情景1>
        encase {
            loadHooker(CustomHooker)
        }
        // <情景2>
        encase(CustomHooker)
    }
}

object CustomHooker : YukiBaseHooker() {

    override fun onHook() {
        // 错误的使用方法，由于外层没有任何判断对象，不能直接开始 Hook
        findClass(name = "com.example.demo.DemoClass").hook {
            // ...
        }
    }
}
```

下面是上述错误示例的**正确**示例。

> 示例代码 1

```kotlin
encase {
    // ✅ 正确的使用方法，在 Zygote 中装载
    loadZygote(CustomHooker)
     // ✅ 正确的使用方法，在 Zygote 中装载
    loadZygote {
        // ✅ 正确的使用方法，在 Zygote 内 Hook
        resources().hook {
            // ...
        }
    }
    // ✅ 正确的使用方法，使用 APP 作用域装载
    loadApp(/** name 参数可选 */, hooker = CustomHooker)
    // ✅ 正确的使用方法，判断 APP 作用域后再装载 Hooker
    loadApp(/** name 参数可选 */) {
        loadHooker(CustomHooker)
         // ✅ 正确的使用方法，在 APP 作用域内 Hook
        findClass(name = "com.example.demo.DemoClass").hook {
            // ...
        }
        // ✅ 正确的使用方法，在 APP 作用域内 Hook
        resources().hook {
            // ...
        }
    }
}
```

> 示例代码 2

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onHook() {
        encase(CustomHooker)
    }
}

object CustomHooker : YukiBaseHooker() {

    override fun onHook() {
        // ✅ 正确的使用方法，由于外层没有任何判断对象，需要判断 APP 作用域后再进行 Hook
        loadApp(/** name 参数可选 */) {
            findClass(name = "com.example.demo.DemoClass").hook {
                // ...
            }
        }
    }
}
```

## 作为 Hook API 使用需要注意的地方

若你作为 Hook API 使用，那么你只需要在入口处对 `encase` 方法进行区分。

::: warning 

**encase** 方法对作为 Hook API 使用提供了两个完全一样的方法，但是比前两者仅多出一个参数 **baseContext**。

:::

> 方法 1

```kotlin:no-line-numbers
fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit)
```

> 方法 2

```kotlin:no-line-numbers
fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker)
```

此处的 `baseContext` 只需填入你在 `attachBaseContext` 处得到的 `Context` 即可，其它用法与上述内容完全一致。

::: danger

切勿以 Xposed 方式使用 **encase** 方法而漏掉 **baseContext** 参数，否则你的 Hook 将完全不工作。

Resources Hook 功能不支持作为 Hook API 使用。

:::