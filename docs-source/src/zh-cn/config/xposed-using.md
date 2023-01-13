# 作为 Xposed 模块使用的相关配置

> 这里介绍了 `YukiHookAPI` 作为 Xposed 模块使用的相关配置方法。

## 依赖配置

> 作为 Xposed 模块，`YukiHookAPI` 提供了一个自动处理程序。

你需要在你的 `build.gradle` 中集成 `com.highcapable.yukihookapi:ksp-xposed` 依赖的最新版本。

## 自定义处理程序

> 你可以对 `YukiHookAPI` 将如何生成 `xposed_init` 入口进行相关配置。

### InjectYukiHookWithXposed 注解

```kotlin:no-line-numbers
annotation class InjectYukiHookWithXposed(
    val sourcePath: String,
    val modulePackageName: String,
    val entryClassName: String,
    val isUsingResourcesHook: Boolean
)
```

`@InjectYukiHookWithXposed` 注解是一个标记模块 Hook 入口的重要注解。

::: danger

**@InjectYukiHookWithXposed** 注解的 **Class** 必须实现 **IYukiHookXposedInit** 接口。

在你当前项目中的所有 **Class** 标记中**只能存在一次**，若存在多个声明自动处理程序<u>**会在编译时抛出异常**</u>，你可以自定义其相关参数。

:::

#### sourcePath 参数

`sourcePath` 参数决定了自动处理程序自动查找并匹配你当前项目路径的重要标识，此参数的内容为相对路径匹配，默认参数为 `src/main`。

::: danger

如果你的项目不在 **../src/main..** 或你手动使用 **sourceSets** 设置了项目路径，你就需要手动设置 **sourcePath** 参数，否则自动处理程序将无法识别你的项目路径并<u>**会在编译时抛出异常**</u>。

:::

> 示例如下

```kotlin
@InjectYukiHookWithXposed(sourcePath = "src/custom")
```

`sourcePath` 使用的文件路径分隔符写法根据 `Windows` 和 `Unix` 将自动进行识别，使用 `/` 或 `\` 均可。

#### modulePackageName 参数

`modulePackageName` 是你当前项目的 `applicationId`，也就是你的模块包名 (最终生成的应用包名)，留空或不填时自动处理程序将对当前项目文件进行分析并生成。

::: warning

若你想使用模块包名自动生成，你需要确保你的项目命名空间在 **AndroidManifest.xml**、**build.gradle** 或 **build.gradle.kts** 中存在如下任意定义方式。

:::

示例命名空间 `com.example.demo`，以下定义方式任选其一。

以下定义方式仅供参考，通常情况下**只要你的项目能够正常生成 `BuildConfig.java` 文件，就不需要做额外操作**。

> `AndroidManifest.xml` 示例

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.demo">
```

> `build.gradle` 示例

```groovy
android {
    namespace 'com.example.demo'
}
```

> `build.gradle.kts` 示例

```kotlin
android {
    namespace = "com.example.demo"
}
```

若你的模块包名是非常规手段进行自动生成的，或你认为有必要手动定义模块包名，那么你可以直接设置 `modulePackageName` 的参数。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(modulePackageName = "com.example.demo")
```

只要你自定义了 `modulePackageName` 的参数，你就会在编译时收到警告。

> 示例如下

```:no-line-numbers
You set the customize module package name to "com.example.demo", please check for yourself if it is correct
```

::: warning

手动定义的模块包名除了格式之外，自动处理程序将不会再检查模块包名是否正确，需要你自行确认其有效性。

:::

#### entryClassName 参数

`entryClassName` 决定了自动处理程序如何生成 `xposed_init` 中的入口类名，默认会使用你的入口类包名插入 `_YukiHookXposedInit` 后缀进行生成。

假设这是你的入口类。

> 示例如下

```kotlin
@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit
```

Xposed 入口类处理如下。

> 示例如下

```kotlin:no-line-numbers
class HookEntry_YukiHookXposedInit : IXposedHookZygoteInit, IXposedHookLoadPackage, ...
```

编译后的类名结构如下。

> 示例如下

```:no-line-numbers
...hook.HookEntry ← 你的入口类
...hook.HookEntry_Impl ← 自动生成的 Impl 类
...hook.HookEntry_YukiHookXposedInit ← 自动生成的 Xposed 入口类
```

我们现在定义入口类名称为 `HookXposedEntry`。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(entryClassName = "HookXposedEntry")
object HookEntry : IYukiHookXposedInit
```

Xposed 入口类处理如下。

> 示例如下

```kotlin:no-line-numbers
class HookXposedEntry : IXposedHookZygoteInit, IXposedHookLoadPackage, ...
```

编译后的类名结构如下。

> 示例如下

```:no-line-numbers
...hook.HookEntry ← 你的入口类
...hook.HookEntry_Impl ← 自动生成的 Impl 类
...hook.HookXposedEntry ← 自动生成的 Xposed 入口类
```

::: tip

入口类可以使用 **class** 或 **object** 定义，但是建议使用 **object** 定义来保证每一个注入的进程都是单例运行。

:::

::: danger

你定义的 **entryClassName** 不可与 **xposed_init** 中的类名相同，否则自动处理程序<u>**会在编译时抛出异常**</u>。

:::

#### isUsingResourcesHook 参数

`isUsingResourcesHook` 决定了自动处理程序是否生成针对 Resources Hook 的相关代码，此功能默认是启用的。

启用后生成的入口类将为如下所示。

> 示例如下

```kotlin:no-line-numbers
class _YukiHookXposedInit : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
        // ...
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        // ...
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        // ...
    }
}
```

若你当前的项目并不需要用到 Resources Hook，可以设置 `isUsingResourcesHook = false` 来关闭自动生成。

>  示例如下

```kotlin
@InjectYukiHookWithXposed(isUsingResourcesHook = false)
```

关闭后生成的入口类将为如下所示。

> 示例如下

```kotlin:no-line-numbers
class _YukiHookXposedInit : IXposedHookZygoteInit, IXposedHookLoadPackage {

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
        // ...
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        // ...
    }
}
```

### IYukiHookXposedInit 接口

`IYukiHookXposedInit` 接口为你的 Hook 入口类必须实现的接口，这是你的模块开始 Hook 的起点。

::: tip

更多功能请参考 [IYukiHookXposedInit](../api/public/com/highcapable/yukihookapi/hook/xposed/proxy/IYukiHookXposedInit)。

:::

当你的模块被 Xposed 装载后，`onHook` 方法将会被回调，你需要在此方法中开始使用 `YukiHookAPI`。

> 基本的调用流程为 `_YukiHookXposedInit` → `IYukiHookXposedInit.onXposedEvent` → `IYukiHookXposedInit.onInit` → `IYukiHookXposedInit.onHook`

详情请参考 [API 基本配置](../config/api-example)。

## 原生 Xposed API 事件

若你当前的 Xposed 模块使用了第三方的资源，但是短时间内可能无法转移它们，此时，你可以使用 `onXposedEvent` 实现监听原生 Xposed API 的全部装载事件。

> 示例如下

```kotlin
@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {

    override fun onHook() {
        // Your code here.
    }

    override fun onXposedEvent() {
        // 监听原生 Xposed API 的装载事件
        YukiXposedEvent.events {
            onInitZygote {
                // it 对象即 [StartupParam]
            }
            onHandleLoadPackage {
                // it 对象即 [LoadPackageParam]
            }
            onHandleInitPackageResources {
                // it 对象即 [InitPackageResourcesParam]
            }
        }
    }
}
```

`onXposedEvent` 与 `onHook` 方法完全独立存在，互不影响，你可以继续在 `onHook` 方法中使用 `YukiHookAPI`。

::: tip

更多功能请参考 [IYukiHookXposedInit.onXposedEvent](../api/public/com/highcapable/yukihookapi/hook/xposed/proxy/IYukiHookXposedInit#onxposedevent-method) 方法。

:::