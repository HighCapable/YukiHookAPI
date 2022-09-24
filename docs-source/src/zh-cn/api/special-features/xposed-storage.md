# Xposed 模块数据存储

> 这是一个自动对接 `SharedPreferences` 和 `XSharedPreferences` 的高效模块数据存储解决方案。

我们需要存储模块的数据，以供宿主调用，这个时候会遇到原生 `Sp` 存储的数据互通阻碍。

原生的 `Xposed` 给我们提供了一个 `XSharedPreferences` 用于读取模块的 `Sp` 数据。

## 在 Activity 中使用

> 这里描述了在 `Activity` 中装载 `YukiHookModulePrefs` 的场景。

通常情况下我们可以这样在 Hook APP (宿主) 内对其进行初始化。

> 示例如下

```kotlin
XSharedPreferences(BuildConfig.APPLICATION_ID)
```

有没有方便快捷的解决方案呢，此时你就可以使用 `YukiHookAPI` 的扩展能力快速实现这个功能。

当你在模块中存储数据的时候，若当前处于 `Activity` 内，可以使用如下方法。

> 示例如下

```kotlin
modulePrefs.putString("test_name", "saved_value")
```

当你在 Hook APP (宿主) 中读取数据时，可以使用如下方法。

> 示例如下

```kotlin
val testName = prefs.getString("test_name", "default_value")
```

你不需要考虑传入模块的包名以及一系列复杂的权限配置，一切都交给 `YukiHookModulePrefs` 来处理。

若要实现存储的区域划分，你可以指定每个 `prefs` 文件的名称。

在模块的 `Activity` 中这样使用。

> 示例如下

```kotlin
// 推荐用法
modulePrefs("specify_file_name").putString("test_name", "saved_value")
// 也可以这样用
modulePrefs.name("specify_file_name").putString("test_name", "saved_value")
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

::: tip

更多功能请参考 [YukiHookModulePrefs](../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookModulePrefs)、[PrefsData](../public/com/highcapable/yukihookapi/hook/xposed/prefs/data/PrefsData)。

:::

## 在 PreferenceFragment 中使用

> 这里描述了在 `PreferenceFragment` 中装载 `YukiHookModulePrefs` 的场景。

若你的模块使用了 `PreferenceFragmentCompat`，你现在可以将其继承类开始迁移到 `ModulePreferenceFragment`。

::: danger

你必须继承 **ModulePreferenceFragment** 才能实现 **YukiHookModulePrefs** 的模块存储功能。

:::

::: tip

更多功能请参考 [ModulePreferenceFragment](../public/com/highcapable/yukihookapi/hook/xposed/prefs/ui/ModulePreferenceFragment)。

:::