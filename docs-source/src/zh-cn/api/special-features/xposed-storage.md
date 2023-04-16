# Xposed 模块数据存储

> 这是一个自动对接 `SharedPreferences` 和 `XSharedPreferences` 的高效模块数据存储解决方案。

我们需要存储模块的数据，以供宿主调用，这个时候会遇到原生 `Sp` 存储的数据互通阻碍。

原生的 `Xposed` 给我们提供了一个 `XSharedPreferences` 用于读取模块的 `Sp` 数据。

## 在 Activity 中使用

> 这里描述了在 `Activity` 中装载 `YukiHookPrefsBridge` 的场景。

通常情况下我们可以这样在 Hook APP (宿主) 内对其进行初始化。

> 示例如下

```kotlin
XSharedPreferences(BuildConfig.APPLICATION_ID)
```

有没有方便快捷的解决方案呢，此时你就可以使用 `YukiHookAPI` 的扩展能力快速实现这个功能。

当你在模块中存储数据的时候，若当前处于 `Activity` 内，可以使用如下方法。

> 示例如下

```kotlin
prefs().edit { putString("test_name", "saved_value") }
```

当你在 Hook APP (宿主) 中读取数据时，可以使用如下方法。

> 示例如下

```kotlin
val testName = prefs.getString("test_name", "default_value")
```

你不需要考虑传入模块的包名以及一系列复杂的权限配置，一切都交给 `YukiHookPrefsBridge` 来处理。

若要实现存储的区域划分，你可以指定每个 `prefs` 文件的名称。

在模块的 `Activity` 中这样使用。

> 示例如下

```kotlin
// 推荐用法
prefs("specify_file_name").edit { putString("test_name", "saved_value") }
// 也可以这样用
prefs().name("specify_file_name").edit { putString("test_name", "saved_value") }
```

在 Hook APP (宿主) 中这样读取。

> 示例如下

```kotlin
// 推荐用法
val testName = prefs("specify_file_name").getString("test_name", "default_value")
// 也可以这样用
val testName = prefs.name("specify_file_name").getString("test_name", "default_value")
```

若你的项目中有大量的固定数据需要存储和读取，推荐使用 `PrefsData` 来创建模板。

通过上面的示例，你可以调用 `edit` 方法使用以下两种方式来批量存储数据。

> 示例如下

```kotlin
// <方案 1>
prefs().edit { 
    putString("test_name_1", "saved_value_1")
    putString("test_name_2", "saved_value_2")
    putString("test_name_3", "saved_value_3")
}
// <方案 2>
prefs().edit()
    .putString("test_name_1", "saved_value_1")
    .putString("test_name_2", "saved_value_2")
    .putString("test_name_3", "saved_value_3")
    .apply()
```

::: tip

更多功能请参考 [YukiHookPrefsBridge](../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge)、[PrefsData](../public/com/highcapable/yukihookapi/hook/xposed/prefs/data/PrefsData)。

:::

## 在 PreferenceFragment 中使用

> 这里描述了在 `PreferenceFragment` 中装载 `YukiHookPrefsBridge` 的场景。

若你的模块使用了 `PreferenceFragmentCompat`，你现在可以将其继承类开始迁移到 `ModulePreferenceFragment`。

::: danger

你必须继承 **ModulePreferenceFragment** 才能实现 **YukiHookPrefsBridge** 的模块存储功能。

:::

::: tip

更多功能请参考 [ModulePreferenceFragment](../public/com/highcapable/yukihookapi/hook/xposed/prefs/ui/ModulePreferenceFragment)。

:::

## 使用原生方式存储

在模块环境中 `YukiHookPrefsBridge` 默认会将数据存储到模块自己的私有目录 (或 Hook Framework 提供的共享目录) 中。

在宿主环境中使用 `YukiHookPrefsBridge` 默认会读取模块自己的私有目录 (或 Hook Framework 提供的共享目录) 中的数据。

如果你想直接将数据存储到模块或宿主当前环境自身的私有目录，你可以使用 `native` 方法。

例如模块的目录是 `.../com.demo.test.module/shared_prefs`，宿主的目录是 `.../com.demo.test.host/shared_prefs`。

以下是在 `Activity` 中的用法。

> 示例如下

```kotlin
// 存储私有数据
prefs().native().edit { putBoolean("isolation_data", true) }
// 读取私有数据
val privateData = prefs().native().getBoolean("isolation_data")
// 存储共享数据
prefs().edit { putBoolean("public_data", true) }
// 读取共享数据
val publicData = prefs().getBoolean("public_data")
```

以下是在 `PackageParam` 中的用法。

> 示例如下

```kotlin
// 存储私有数据
prefs.native().edit { putBoolean("isolation_data", true) }
// 读取私有数据
val privateData = prefs.native().getBoolean("isolation_data")
// 读取共享数据
val publicData = prefs.getBoolean("public_data")
```

使用 `native` 方法后，无论在 `Activity` 还是 `PackageParam` 中都会将数据<u>**在对应环境的私有目录中**</u>存储、读取，数据相互隔离。

::: tip

更多功能请参考 [YukiHookPrefsBridge](../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge)。

:::