---
pageClass: code-page
---

# YukiHookModulePrefs <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiHookModulePrefs private constructor(private var context: Context?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 实现 Xposed 模块的数据存取，对接 `SharedPreferences` 和 `XSharedPreferences`。

在不同环境智能选择存取使用的对象。

::: danger

此功能为实验性功能，仅在 LSPosed 环境测试通过，EdXposed 理论也可以使用但不再推荐。

:::

使用 LSPosed 环境请在 `AndroidManifests.xml` 中将 `xposedminversion` 最低设置为 `93`。

详见 [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module)。

若你在按照规定配置后依然无法使用或出现文件权限错误问题，可以参考 [isEnableHookSharedPreferences](../../../YukiHookAPI#isenablehooksharedpreferences-field)。

未使用 LSPosed 环境请将你的模块 `API` 降至 `26` 以下，`YukiHookAPI` 将会尝试使用 `makeWorldReadable` 但仍有可能不成功。

太极请参阅 [文件权限/配置/XSharedPreference](https://taichi.cool/zh/doc/for-xposed-dev.html#文件权限-配置-xsharedpreference)。

::: danger

当你在 Xposed 模块中存取数据的时候 **context** 必须不能是空的。

:::

若你正在使用 `PreferenceFragmentCompat`，请迁移到 `ModulePreferenceFragment` 以适配上述功能特性。

**可选配置**

若你不想将你的模块的 `xposedminversion` 最低设置为 `93`，你可以在 `AndroidManifest.xml` 中添加 `xposedsharedprefs` 来实现支持。

详见 [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module)。

> 示例如下

```xml
<meta-data
    android:name="xposedsharedprefs"
    android:value="true"/>
```

<h2 class="deprecated">isXSharePrefsReadable - field</h2>

**变更记录**

`v1.0.90` `新增`

`v1.1.5` `作废`

请转移到 `isPreferencesAvailable`

<h2 class="deprecated">isRunInNewXShareMode - field</h2>

**变更记录**

`v1.0.78` `新增`

`v1.1.5` `作废`

请转移到 `isPreferencesAvailable`

## isPreferencesAvailable <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isPreferencesAvailable: Boolean
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 获取当前 `YukiHookModulePrefs` 的可用状态。

在 (Xposed) 宿主环境中返回 `XSharedPreferences` 可用状态 (可读)。

在模块环境中返回当前是否处于 New XSharedPreferences 模式 (可读可写)。

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
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

在 (Xposed) 宿主环境 `PackageParam` 中的使用方法。

> 示例如下

```kotlin
prefs("custom_name").getString("custom_key")
```

## direct <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun direct(): YukiHookModulePrefs
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 忽略缓存直接读取键值。

无论是否开启 `YukiHookAPI.Configs.isEnableModulePrefsCache`。

仅在 `XSharedPreferences` 下生效。

## getString <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getString(key: String, value: String): String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `String` 键值。

## getStringSet <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getStringSet(key: String, value: Set<String>): Set<String>
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 获取 `Set<String>` 键值。

## getBoolean <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getBoolean(key: String, value: Boolean): Boolean
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Boolean` 键值。

## getInt <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getInt(key: String, value: Int): Int
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Int` 键值。

## getLong <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getLong(key: String, value: Long): Long
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Long` 键值。

## getFloat <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getFloat(key: String, value: Float): Float
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取 `Float` 键值。

## all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(): HashMap<String, Any?>
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 获取全部存储的键值数据。

智能识别对应环境读取键值数据。

::: danger

每次调用都会获取实时的数据，不受缓存控制，请勿在高并发场景中使用。

:::

## remove <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun remove(key: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 移除全部包含 `key` 的存储数据。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## remove <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> remove(prefs: PrefsData<T>)
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 移除 `PrefsData.key` 的存储数据。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## clear <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun clear()
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 移除全部存储数据。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## putString <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putString(key: String, value: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `String` 键值。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## putStringSet <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putStringSet(key: String, value: Set<String>)
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 存储 `Set<String>` 键值。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## putBoolean <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putBoolean(key: String, value: Boolean)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Boolean` 键值。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## putInt <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putInt(key: String, value: Int)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Int` 键值。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## putLong <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putLong(key: String, value: Long)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Long` 键值。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## putFloat <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putFloat(key: String, value: Float)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 存储 `Float` 键值。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## get <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> get(prefs: PrefsData<T>, value: T): T
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 智能获取指定类型的键值。

## put <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> put(prefs: PrefsData<T>, value: T)
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 智能存储指定类型的键值。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## clearCache <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun clearCache()
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 清除 `XSharedPreferences` 中缓存的键值数据。

无论是否开启 `YukiHookAPI.Configs.isEnableModulePrefsCache`。

调用此方法将清除当前存储的全部键值缓存。

下次将从 `XSharedPreferences` 重新读取。

在 (Xposed) 宿主环境中使用。