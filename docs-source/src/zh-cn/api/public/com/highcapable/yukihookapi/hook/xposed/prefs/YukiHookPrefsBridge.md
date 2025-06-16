---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YukiHookPrefsBridge <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiHookPrefsBridge private constructor(private var context: Context?)
```

**变更记录**

`v1.0` `添加`

`v1.1.9` `修改`

~~`YukiHookModulePrefs`~~ 更名为 `YukiHookPrefsBridge`

**功能描述**

> `YukiHookAPI` 对 `SharedPreferences`、`XSharedPreferences` 的扩展存储桥实现。

在不同环境智能选择存取使用的对象。

::: danger

模块与宿主之前共享数据存储为实验性功能，仅在 LSPosed 环境测试通过，EdXposed 理论也可以使用但不再推荐。

:::

使用 LSPosed 环境请在 `AndroidManifests.xml` 中将 `xposedminversion` 最低设置为 `93`。

详见 [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module)。

若你在按照规定配置后依然无法使用或出现文件权限错误问题，可以参考 [isEnableHookSharedPreferences](../../../YukiHookAPI#isenablehooksharedpreferences-field)。

未使用 LSPosed 环境请将你的模块 `API` 降至 `26` 以下，`YukiHookAPI` 将会尝试使用 `makeWorldReadable` 但仍有可能不成功。

太极请参阅 [文件权限/配置/XSharedPreference](https://taichi.cool/zh/doc/for-xposed-dev.html#文件权限-配置-xsharedpreference)。

对于在模块环境中使用 `PreferenceFragmentCompat`，`YukiHookAPI` 提供了 `ModulePreferenceFragment` 来实现同样的功能。

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

请迁移到 `isPreferencesAvailable`

<h2 class="deprecated">isRunInNewXShareMode - field</h2>

**变更记录**

`v1.0.78` `新增`

`v1.1.5` `作废`

请迁移到 `isPreferencesAvailable`

## isPreferencesAvailable <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isPreferencesAvailable: Boolean
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 获取当前 `YukiHookPrefsBridge` 的可用状态。

在 (Xposed) 宿主环境中返回 `XSharedPreferences` 可用状态 (可读)。

在模块环境中返回当前是否处于 New XSharedPreferences 模式 (可读可写)。

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun name(name: String): YukiHookPrefsBridge
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 自定义 Sp 存储名称。

**功能示例**

在 `Activity` 中的使用方法。

> 示例如下

```kotlin
prefs("custom_name").getString("custom_key")
```

在 (Xposed) 宿主环境 `PackageParam` 中的使用方法。

> 示例如下

```kotlin
prefs("custom_name").getString("custom_key")
```

<h2 class="deprecated">direct - method</h2>

**变更记录**

`v1.0.5` `新增`

`v1.1.11` `作废`

键值的直接缓存功能已被移除，因为其存在内存溢出 (OOM) 问题

## native <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun native(): YukiHookPrefsBridge
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 忽略当前环境直接使用 `Context.getSharedPreferences` 存取数据。

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

## contains <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun contains(key: String): Boolean
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 判断当前是否包含 `key` 键值的数据。

智能识别对应环境读取键值数据。

## all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(): MutableMap<String, Any?>
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 获取全部存储的键值数据。

智能识别对应环境读取键值数据。

::: danger

每次调用都会获取实时的数据，不受缓存控制，请勿在高并发场景中使用。

:::

<h2 class="deprecated">remove - method</h2>

**变更记录**

`v1.0` `添加`

`v1.1.9` `作废`

请迁移到 `edit` 方法

<h2 class="deprecated">clear - method</h2>

**变更记录**

`v1.0.77` `新增`

`v1.1.9` `作废`

请迁移到 `edit` 方法

<h2 class="deprecated">putString - method</h2>

**变更记录**

`v1.0` `添加`

`v1.1.9` `作废`

请迁移到 `edit` 方法

<h2 class="deprecated">putStringSet - method</h2>

**变更记录**

`v1.0.77` `新增`

`v1.1.9` `作废`

请迁移到 `edit` 方法

<h2 class="deprecated">putBoolean - method</h2>

**变更记录**

`v1.0` `添加`

`v1.1.9` `作废`

请迁移到 `edit` 方法

<h2 class="deprecated">putInt - method</h2>

**变更记录**

`v1.0` `添加`

`v1.1.9` `作废`

请迁移到 `edit` 方法

<h2 class="deprecated">putLong - method</h2>

**变更记录**

`v1.0` `添加`

`v1.1.9` `作废`

请迁移到 `edit` 方法

<h2 class="deprecated">putFloat - method</h2>

**变更记录**

`v1.0` `添加`

`v1.1.9` `作废`

请迁移到 `edit` 方法

## get <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> get(prefs: PrefsData<T>, value: T): T
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 智能获取指定类型的键值。

<h2 class="deprecated">put - method</h2>

**变更记录**

`v1.0.67` `新增`

`v1.1.9` `作废`

请迁移到 `edit` 方法

## edit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun edit(): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 创建新的 `Editor`。

在模块环境中或启用了 `isUsingNativeStorage` 后使用。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## edit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun edit(initiate: Editor.() -> Unit)
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 创建新的 `Editor`。

自动调用 `Editor.apply` 方法。

在模块环境中或启用了 `isUsingNativeStorage` 后使用。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

<h2 class="deprecated">clearCache - method</h2>

**变更记录**

`v1.0.5` `新增`

`v1.1.11` `作废`

键值的直接缓存功能已被移除，因为其存在内存溢出 (OOM) 问题

## Editor <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Editor internal constructor()
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> `YukiHookPrefsBridge` 的存储代理类。

请使用 `edit` 方法来获取 `Editor`。

在模块环境中或启用了 `isUsingNativeStorage` 后使用。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

### remove <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun remove(key: String): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 移除全部包含 `key` 的存储数据。

### remove <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> remove(prefs: PrefsData<T>): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 移除 `PrefsData.key` 的存储数据。

### clear <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun clear(): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 移除全部存储数据。

### putString <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putString(key: String, value: String): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 存储 `String` 键值。

### putStringSet <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putStringSet(key: String, value: Set<String>): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 存储 `Set<String>` 键值。

### putBoolean <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putBoolean(key: String, value: Boolean): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 存储 `Boolean` 键值。

### putInt <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putInt(key: String, value: Int): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 存储 `Int` 键值。

### putLong <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putLong(key: String, value: Long): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 存储 `Long` 键值。

### putFloat <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putFloat(key: String, value: Float): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 存储 `Float` 键值。

### put <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> put(prefs: PrefsData<T>, value: T): Editor
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 智能存储指定类型的键值。

### commit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun commit(): Boolean
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 提交更改 (同步)。

### apply <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun apply()
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 提交更改 (异步)。