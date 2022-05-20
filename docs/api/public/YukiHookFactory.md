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

### isSupportResourcesHook [field]

```kotlin
val Any?.isSupportResourcesHook: Boolean
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 判断当前 Hook Framework 是否支持资源钩子(Resources Hook)。

### isModuleActive [field]

```kotlin
val Context.isModuleActive: Boolean
```

**变更记录**

`v1.0.6` `新增`

**功能描述**

> 判断模块是否在 Xposed 或太极、无极中激活。

### isXposedModuleActive [field]

```kotlin
val Any?.isXposedModuleActive: Boolean
```

**变更记录**

`v1.0.6` `新增`

**功能描述**

> 仅判断模块是否在 Xposed 中激活。

### isTaiChiModuleActive [field]

```kotlin
val Context.isTaiChiModuleActive: Boolean
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 仅判断模块是否在太极、无极中激活。