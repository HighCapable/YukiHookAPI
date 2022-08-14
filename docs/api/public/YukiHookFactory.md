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

### registerModuleAppActivities [method]

```kotlin
fun Context.registerModuleAppActivities(proxy: Any?)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 向 Hook APP (宿主) 注册当前 Xposed 模块的 `Activity`。

注册成功后，你就可以直接使用 `Context.startActivity` 来启动未在宿主中注册的 `Activity`。

你要将需要在宿主启动的 `Activity` 继承于 `ModuleAppActivity` 或 `ModuleAppCompatActivity`。

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

在 Hook 宿主之后，我们可以直接在 Hooker 中得到的 `Context` 注册当前模块的 `Activity` 代理。

> 示例如下

```kotlin
injectMember {
    method {
        name = "onCreate"
        param(BundleClass)
    }
    afterHook {
        instance<Activity>().registerModuleAppActivities()
    }
}
```

你还可以直接在 `AppLifecycle` 中注册当前模块的 `Activity` 代理。

> 示例如下

```kotlin
onAppLifecycle {
    onCreate {
        registerModuleAppActivities()
    }
}
```

如果没有填写 `proxy` 参数，API 将会根据当前 `Context` 自动获取当前宿主的启动入口 `Activity` 进行代理。

通常情况下，它是有效的，但是以上情况在一些 APP 中会失效，例如一些 `Activity` 会在注册清单上加入启动参数，那么我们就需要使用另一种解决方案。

若未注册的 `Activity` 不能被正确启动，我们可以手动拿到宿主的 `AndroidManifest.xml` 进行分析，来得到一个注册过的 `Activity` 标签，获取其中的 `name`。

你需要选择一个当前宿主可能用不到的、不需要的 `Activity` 作为一个“傀儡”将其进行代理，通常是有效的。

比如我们已经找到了能够被代理的合适 `Activity`。

> 示例如下

```xml
<activity
    android:name="com.demo.test.activity.TestActivity"
    ...>
```

根据其中的 `name`，我们只需要在方法中加入这个参数进行注册即可。

> 示例如下

```kotlin
registerModuleAppActivities(proxy = "com.demo.test.activity.TestActivity")
```

另一种情况，如果你对宿主的类编写了一个 `stub`，那么你可以直接通过 `Class` 对象来进行注册。

> 示例如下

```kotlin
registerModuleAppActivities(TestActivity::class.java)
```

注册完成后，请将你需要使用宿主启动的模块中的 `Activity` 继承于 `ModuleAppActivity` 或 `ModuleAppCompatActivity`。

这些 `Activity` 现在无需注册即可无缝存活于宿主中。

> 示例如下

```kotlin
class HostTestActivity : ModuleAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 模块资源已被自动注入，可以直接使用 xml 装载布局
        setContentView(R.layout.activity_main)
    }
}
```

若你需要继承于 `ModuleAppCompatActivity`，你需要手动设置 AppCompat 主题。

> 示例如下

```kotlin
class HostTestActivity : ModuleAppCompatActivity() {

    // 这里的主题名称仅供参考，请填写你模块中已有的主题名称
    override val moduleTheme get() = R.style.Theme_AppCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 模块资源已被自动注入，可以直接使用 xml 装载布局
        setContentView(R.layout.activity_main)
    }
}
```

以上步骤全部完成后，你就可以在 (Xposed) 宿主环境任意存在 `Context` 的地方愉快地调用 `startActivity` 了。

> 示例如下

```kotlin
val context: Context = ... // 假设这就是你的 Context
context.startActivity(context, HostTestActivity::class.java)
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