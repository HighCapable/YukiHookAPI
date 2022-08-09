## YukiHookFactory [kt]

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

合并到 `IYukiHookXposedInit`，将方法体进行 inline

**功能描述**

> 这是 `YukiHookAPI` 相关 `lambda` 方法的封装类以及部分 API 用法。

### configs [method]

```kotlin
inline fun IYukiHookXposedInit.configs(initiate: YukiHookAPI.Configs.() -> Unit)
```

**变更记录**

`v1.0.1` `新增`

`v1.0.80` `修改`

合并到 `IYukiHookXposedInit`

**功能描述**

> 在 `IYukiHookXposedInit` 中配置 `Configs`。

### encase [method]

```kotlin
fun IYukiHookXposedInit.encase(initiate: PackageParam.() -> Unit)
```

```kotlin
fun IYukiHookXposedInit.encase(vararg hooker: YukiBaseHooker)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

合并到 `IYukiHookXposedInit`

**功能描述**

> 在 `IYukiHookXposedInit` 中调用 `YukiHookAPI`。

### modulePrefs [field]

```kotlin
val Context.modulePrefs: YukiHookModulePrefs
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取模块的存取对象。

### modulePrefs [method]

```kotlin
fun Context.modulePrefs(name: String): YukiHookModulePrefs
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取模块的存取对象，可设置 `name` 为自定义 Sp 存储名称。

### dataChannel [method]

```kotlin
fun Context.dataChannel(packageName: String): YukiHookDataChannel.NameSpace
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 获取模块的数据通讯桥命名空间对象。

### processName [field]

```kotlin
val Context.processName: String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前进程名称。

### injectModuleAppResources [method]

```kotlin
fun Context.injectModuleAppResources()
```

```kotlin
fun Resources.injectModuleAppResources()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 向 Hook APP (宿主) `Context` 或 `Resources` 注入当前 Xposed 模块的资源。

注入成功后，你就可以直接使用例如 `ImageView.setImageResource` 或 `Resources.getString` 装载当前 Xposed 模块的资源 ID。

注入的资源作用域仅限当前 `Context` 或 `Resources`，你需要在每个用到宿主 `Context` 或 `Resources` 的地方重复调用此方法进行注入才能使用。

为防止资源 ID 互相冲突，你需要在当前 Xposed 模块项目的 `build.gradle` 中修改资源 ID。

- Kotlin Gradle DSL

```kotlin
androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
```

- Groovy

```groovy
aaptOptions.additionalParameters '--allow-reserved-package-id', '--package-id', '0x64'
```

!> 提供的示例资源 ID 值仅供参考，为了防止当前宿主存在多个 Xposed 模块，建议自定义你自己的资源 ID。

!> 只能在 (Xposed) 宿主环境使用此功能，其它环境下使用将不生效且会打印警告信息。

**功能示例**

在 Hook 宿主之后，我们可以直接在 Hooker 中得到的 `Context` 注入当前模块资源。

> 示例如下

```kotlin
injectMember {
    method {
        name = "onCreate"
        param(BundleClass)
    }
    afterHook {
        instance<Activity>().also {
            // <方案1> 通过 Context 注入模块资源
            it.injectModuleAppResources()
            // <方案2> 直接得到宿主 Resources 注入模块资源
            it.resources.injectModuleAppResources()
            // 直接使用模块资源 ID
            it.getString(R.id.app_name)
        }
    }
}
```

你还可以直接在 `AppLifecycle` 中注入当前模块资源。

> 示例如下

```kotlin
onAppLifecycle {
    onCreate {
        // 全局注入模块资源，但仅限于全局生命周期，类似 ImageView.setImageResource 这样的方法在 Activity 中需要单独注入
        // <方案1> 通过 Context 注入模块资源
        injectModuleAppResources()
        // <方案2> 直接得到宿主 Resources 注入模块资源
        resources.injectModuleAppResources()
        // 直接使用模块资源 ID
        getString(R.id.app_name)
    }
}
```

### ~~isSupportResourcesHook [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.80` `新增`

`v1.0.91` `移除`

请转移到 `YukiHookAPI.Status.isSupportResourcesHook`

### ~~isModuleActive [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.6` `新增`

`v1.0.91` `移除`

请转移到 `YukiHookAPI.Status.isModuleActive`

### ~~isXposedModuleActive [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.6` `新增`

`v1.0.91` `移除`

请转移到 `YukiHookAPI.Status.isXposedModuleActive`

### ~~isTaiChiModuleActive [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.91` `移除`

请转移到 `YukiHookAPI.Status.isTaiChiModuleActive`

## ~~YukiHookModuleStatus [class]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.91` `作废`

请转移到 `YukiHookAPI.Status`