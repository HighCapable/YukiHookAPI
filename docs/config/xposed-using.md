# 作为 Xposed 模块使用的相关配置

> 这里介绍了 `YukiHookAPI` 作为 Xposed 模块使用的相关配置方法。

## 依赖配置

> 作为 Xposed 模块，`YukiHookAPI` 提供了一个自动处理程序。

你需要在你的 `build.gradle` 中集成 `com.highcapable.yukihookapi:ksp-xposed` 依赖的最新版本。

## 自定义处理程序

> 你可以对 `YukiHookAPI` 将如何生成 `xposed_init` 入口进行相关配置。

### InjectYukiHookWithXposed 注解

```kotlin
annotation class InjectYukiHookWithXposed(val sourcePath: String, val modulePackageName: String)
```

`@InjectYukiHookWithXposed` 注解是一个标记模块 Hook 入口的重要注解。

!> `@InjectYukiHookWithXposed` 注解的 `Class` 必须实现 `YukiHookXposedInitProxy` 接口。

!> 在你当前项目中的所有 `Class` 标记中**只能存在一次**，若**存在多个声明自动处理程序<u>会在编译时抛出异常</u>**，你可以自定义其相关参数。

#### sourcePath 参数

`sourcePath` 参数决定了自动处理程序自动查找并匹配你当前项目路径的重要标识，此参数的内容为相对路径匹配，默认参数为 `src/main`。

!> 如果你的项目不在 `...app/src/main...` 或你手动使用 `sourceSets` 设置了项目路径，你就需要手动设置 `sourcePath` 参数，**否则自动处理程序将无法识别你的项目路径并<u>会在编译时抛出异常</u>**。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(sourcePath = "src/custom")
```

`sourcePath` 使用的文件路径分隔符写法根据 `Windows` 和 `Unix` 将自动进行识别，使用 `/` 或 `\` 均可。

#### modulePackageName 参数

`modulePackageName` 是你当前项目的包名，也就是你的模块包名，默认留空自动处理程序将自动根据你注解的 `Class` 入口类的路径进行生成。

!> 若你想使用包名自动生成，你的 Hook 入口类 `HookEntryClass` 就要遵守包名的命名规范，格式为 `包名.hook.HookEntryClass` 或 `包名.hook.子包名.HookEntryClass`。

示例模块包名 `com.example.demo`

示例 1 `com.example.demo.hook.MainHook`

示例 2 `com.example.demo.hook.custom.CustomClass`

若你不想使用此格式定义入口类的包名，你可以直接设置 `modulePackageName` 的参数。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(modulePackageName = "com.example.demo")
```

你也可以直接设置为你的 `BuildConfig.APPLICATION_ID`。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(modulePackageName = BuildConfig.APPLICATION_ID)
```

!> 只要你自定义了 `modulePackageName` 的参数，你就会在编译时收到警告。

> 示例如下

```
You set the customize module package name to "com.example.demo", please check for yourself if it is correct
```

### YukiHookXposedInitProxy 接口

```kotlin
interface YukiHookXposedInitProxy {

    fun onInit()

    fun onHook()
}
```

`YukiHookXposedInitProxy` 接口为你的 `HookEntryClass` 必须实现的接口，这是你的模块开始 Hook 的起点。

若要了解更多可 [点击这里](api/document?id=yukihookxposedinitproxy-interface) 进行查看。

当你的模块被 Xposed 装载后，`onHook` 方法将会被回调，你需要在此方法中开始使用 `YukiHookAPI`。

> 基本的调用流程为 `_YukiHookXposedInit.handleLoadPackage` → `HookEntryClass.onInit` → `HookEntryClass.onHook` → `YukiHookAPI.onXposedLoaded`

详情请参考 [API 基本配置](config/api-example)。