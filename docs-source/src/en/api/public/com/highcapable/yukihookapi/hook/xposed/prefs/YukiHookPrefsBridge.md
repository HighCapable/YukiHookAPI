---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiHookPrefsBridge <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiHookPrefsBridge private constructor(private var context: Context?)
```

**Change Records**

`v1.0` `first`

`v1.1.9` `modified`

~~`YukiHookModulePrefs`~~ 更名为 `YukiHookPrefsBridge`

**Function Illustrate**

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

**Optional Configuration**

若你不想将你的模块的 `xposedminversion` 最低设置为 `93`，你可以在 `AndroidManifest.xml` 中添加 `xposedsharedprefs` 来实现支持。

详见 [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module)。

> The following example

```xml
<meta-data
    android:name="xposedsharedprefs"
    android:value="true"/>
```

<h2 class="deprecated">isXSharePrefsReadable - field</h2>

**Change Records**

`v1.0.90` `added`

`v1.1.5` `deprecated`

请转移到 `isPreferencesAvailable`

<h2 class="deprecated">isRunInNewXShareMode - field</h2>

**Change Records**

`v1.0.78` `added`

`v1.1.5` `deprecated`

请转移到 `isPreferencesAvailable`

## isPreferencesAvailable <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isPreferencesAvailable: Boolean
```

**Change Records**

`v1.1.5` `added`

**Function Illustrate**

> 获取当前 `YukiHookPrefsBridge` 的可用状态。

在 (Xposed) 宿主环境中返回 `XSharedPreferences` 可用状态 (可读)。

在模块环境中返回当前是否处于 New XSharedPreferences 模式 (可读可写)。

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun name(name: String): YukiHookPrefsBridge
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 自定义 Sp 存储名称。

**Function Example**

在 `Activity` 中的使用方法。

> The following example

```kotlin
prefs("custom_name").getString("custom_key")
```

在 (Xposed) 宿主环境 `PackageParam` 中的使用方法。

> The following example

```kotlin
prefs("custom_name").getString("custom_key")
```

## direct <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun direct(): YukiHookPrefsBridge
```

**Change Records**

`v1.0.5` `added`

**Function Illustrate**

> 忽略缓存直接读取键值。

无论是否开启 `YukiHookAPI.Configs.isEnablePrefsBridgeCache`。

仅在 `XSharedPreferences` 下生效。

## native <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun native(): YukiHookPrefsBridge
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 忽略当前环境直接使用 `Context.getSharedPreferences` 存取数据。

## getString <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getString(key: String, value: String): String
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取 `String` 键值。

## getStringSet <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getStringSet(key: String, value: Set<String>): Set<String>
```

**Change Records**

`v1.0.77` `added`

**Function Illustrate**

> 获取 `Set<String>` 键值。

## getBoolean <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getBoolean(key: String, value: Boolean): Boolean
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取 `Boolean` 键值。

## getInt <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getInt(key: String, value: Int): Int
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取 `Int` 键值。

## getLong <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getLong(key: String, value: Long): Long
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取 `Long` 键值。

## getFloat <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getFloat(key: String, value: Float): Float
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取 `Float` 键值。

## contains <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun contains(key: String): Boolean
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 判断当前是否包含 `key` 键值的数据。

智能识别对应环境读取键值数据。

## all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(): HashMap<String, Any?>
```

**Change Records**

`v1.0.77` `added`

**Function Illustrate**

> 获取全部存储的键值数据。

智能识别对应环境读取键值数据。

::: danger

每次调用都会获取实时的数据，不受缓存控制，请勿在高并发场景中使用。

:::

<h2 class="deprecated">remove - method</h2>

**Change Records**

`v1.0` `first`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

<h2 class="deprecated">clear - method</h2>

**Change Records**

`v1.0.77` `added`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

<h2 class="deprecated">putString - method</h2>

**Change Records**

`v1.0` `first`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

<h2 class="deprecated">putStringSet - method</h2>

**Change Records**

`v1.0.77` `added`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

<h2 class="deprecated">putBoolean - method</h2>

**Change Records**

`v1.0` `first`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

<h2 class="deprecated">putInt - method</h2>

**Change Records**

`v1.0` `first`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

<h2 class="deprecated">putLong - method</h2>

**Change Records**

`v1.0` `first`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

<h2 class="deprecated">putFloat - method</h2>

**Change Records**

`v1.0` `first`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

## get <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> get(prefs: PrefsData<T>, value: T): T
```

**Change Records**

`v1.0.67` `added`

**Function Illustrate**

> 智能获取指定类型的键值。

<h2 class="deprecated">put - method</h2>

**Change Records**

`v1.0.67` `added`

`v1.1.9` `deprecated`

请转移到 `edit` 方法

## edit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun edit(): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 创建新的 `Editor`。

在模块环境中或启用了 `isUsingNativeStorage` 后使用。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## edit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun edit(initiate: Editor.() -> Unit)
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 创建新的 `Editor`。

自动调用 `Editor.apply` 方法。

在模块环境中或启用了 `isUsingNativeStorage` 后使用。

::: warning

在 (Xposed) 宿主环境下只读，无法使用。

:::

## clearCache <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun clearCache()
```

**Change Records**

`v1.0.5` `added`

**Function Illustrate**

> 清除 `YukiHookPrefsBridge` 中缓存的键值数据。

无论是否开启 `YukiHookAPI.Configs.isEnablePrefsBridgeCache`。

调用此方法将清除当前存储的全部键值缓存。

## Editor <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Editor internal constructor()
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

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

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 移除全部包含 `key` 的存储数据。

### remove <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> remove(prefs: PrefsData<T>): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 移除 `PrefsData.key` 的存储数据。

### clear <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun clear(): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 移除全部存储数据。

### putString <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putString(key: String, value: String): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 存储 `String` 键值。

### putStringSet <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putStringSet(key: String, value: Set<String>): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 存储 `Set<String>` 键值。

### putBoolean <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putBoolean(key: String, value: Boolean): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 存储 `Boolean` 键值。

### putInt <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putInt(key: String, value: Int): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 存储 `Int` 键值。

### putLong <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putLong(key: String, value: Long): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 存储 `Long` 键值。

### putFloat <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun putFloat(key: String, value: Float): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 存储 `Float` 键值。

### put <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> put(prefs: PrefsData<T>, value: T): Editor
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 智能存储指定类型的键值。

### commit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun commit(): Boolean
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 提交更改 (同步)。

### apply <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun apply()
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 提交更改 (异步)。