## YukiHookModulePrefs [class]

```kotlin
class YukiHookModulePrefs(private val context: Context?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 实现 Xposed 模块的数据存取，对接 `SharedPreferences` 和 `XSharedPreferences`。

在不同环境智能选择存取使用的对象。

!> 请注意此功能为实验性功能，仅在 LSPosed 环境测试通过，EdXposed 理论也可以使用但不再推荐。

使用 LSPosed 环境请在 `AndroidManifests.xml` 中将 `xposedminversion` 最低设置为 `93`。

详见 [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module)。

未使用 LSPosed 环境请将你的模块 `API` 降至 `26` 以下，`YukiHookAPI` 将会尝试使用 `makeWorldReadable` 但仍有可能不成功。

太极请参阅 [文件权限/配置/XSharedPreference](https://taichi.cool/zh/doc/for-xposed-dev.html#文件权限-配置-xsharedpreference)。

!> 当你在 Xposed 模块中存取数据的时候 `context` 必须不能是空的。

### name [method]

```kotlin
fun name(name: String): YukiHookModulePrefs
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 自定义 Sp 存储名称。

**功能示例**

在 `Activity` 中的使用方法。

> 示例如下

```kotlin
modulePrefs("custom_name").getString("custom_key")
```

在 Xposed 模块环境 `PackageParam` 中的使用方法。

> 示例如下

```kotlin
prefs("custom_name").getString("custom_key")
```

### direct [method]

```kotlin
fun direct(): YukiHookModulePrefs
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 忽略缓存直接读取键值。

无论是否开启 `YukiHookAPI.Configs.isEnableModulePrefsCache`。

仅在 `XSharedPreferences` 下生效。

### getString [method]

```kotlin
fun getString(key: String, value: String): String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `String` 键值。

### getStringSet [method]

```kotlin
fun getStringSet(key: String, value: Set<String>): Set<String>
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 获取 `Set<String>` 键值。

### getBoolean [method]

```kotlin
fun getBoolean(key: String, value: Boolean): Boolean
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Boolean` 键值。

### getInt [method]

```kotlin
fun getInt(key: String, value: Int): Int
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Int` 键值。

### getLong [method]

```kotlin
fun getLong(key: String, value: Long): Long
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Long` 键值。

### getFloat [method]

```kotlin
fun getFloat(key: String, value: Float): Float
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Float` 键值。

### all [method]

```kotlin
fun all(): HashMap<String, Any?>
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 获取全部存储的键值数据。

智能识别对应环境读取键值数据。

!> 每次调用都会获取实时的数据，不受缓存控制，请勿在高并发场景中使用。

### remove [method]

```kotlin
fun remove(key: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 移除全部包含 `key` 的存储数据。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### remove [method]

```kotlin
inline fun <reified T> remove(prefs: PrefsData<T>)
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 移除 `PrefsData.key` 的存储数据。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### clear [method]

```kotlin
fun clear()
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 移除全部存储数据。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### putString [method]

```kotlin
fun putString(key: String, value: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `String` 键值。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### putStringSet [method]

```kotlin
fun putStringSet(key: String, value: Set<String>)
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 存储 `Set<String>` 键值。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### putBoolean [method]

```kotlin
fun putBoolean(key: String, value: Boolean)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Boolean` 键值。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### putInt [method]

```kotlin
fun putInt(key: String, value: Int)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Int` 键值。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### putLong [method]

```kotlin
fun putLong(key: String, value: Long)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Long` 键值。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### putFloat [method]

```kotlin
fun putFloat(key: String, value: Float)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Float` 键值。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### get [method]

```kotlin
inline fun <reified T> get(prefs: PrefsData<T>, value: T): T
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 智能获取指定类型的键值。

### put [method]

```kotlin
inline fun <reified T> put(prefs: PrefsData<T>, value: T)
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 智能存储指定类型的键值。

!> 在 `XSharedPreferences` 环境下只读，无法使用。

### clearCache [method]

```kotlin
fun clearCache()
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 清除 `XSharedPreferences` 中缓存的键值数据。

无论是否开启 `YukiHookAPI.Configs.isEnableModulePrefsCache`。

调用此方法将清除当前存储的全部键值缓存。

下次将从 `XSharedPreferences` 重新读取。