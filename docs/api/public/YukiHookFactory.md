## YukiHookFactory [kt]

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 这是 `YukiHookAPI` 相关 `lambda` 方法的封装类以及部分 API 用法。

### configs [method]

```kotlin
fun YukiHookXposedInitProxy.configs(initiate: YukiHookAPI.Configs.() -> Unit)
```

<b>变更记录</b>

`v1.0.1` `新增`

<b>功能描述</b>

> 在 `YukiHookXposedInitProxy` 中配置 `Configs`。

### encase [method]

```kotlin
fun YukiHookXposedInitProxy.encase(initiate: PackageParam.() -> Unit)
```

```kotlin
fun YukiHookXposedInitProxy.encase(vararg hooker: YukiBaseHooker)
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 在 `YukiHookXposedInitProxy` 中装载 `YukiHookAPI`。

### modulePrefs [field]

```kotlin
val Context.modulePrefs: YukiHookModulePrefs
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 获取模块的存取对象。

### modulePrefs [method]

```kotlin
fun Context.modulePrefs(name: String): YukiHookModulePrefs
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 获取模块的存取对象，可设置 `name` 为自定义 Sp 存储名称。

### processName [field]

```kotlin
val Context.processName: String
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 获取当前进程名称。

### isModuleActive [field]

```kotlin
val Context.isModuleActive: Boolean
```

<b>变更记录</b>

`v1.0.6` `新增`

<b>功能描述</b>

> 判断模块是否在 Xposed 或太极、无极中激活。

### isXposedModuleActive [field]

```kotlin
val Any?.isXposedModuleActive: Boolean
```

<b>变更记录</b>

`v1.0.6` `新增`

<b>功能描述</b>

> 仅判断模块是否在 Xposed 中激活。

### isTaiChiModuleActive [field]

```kotlin
val Context.isTaiChiModuleActive: Boolean
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 仅判断模块是否在太极、无极中激活。