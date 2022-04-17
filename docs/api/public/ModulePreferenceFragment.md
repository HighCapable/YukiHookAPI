## ModulePreferenceFragment [class]

```kotlin
abstract class ModulePreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener
```

**变更记录**

`v1.0.78` `新增`

**功能描述**

> 这是对使用 `YukiHookAPI` Xposed 模块实现中的一个扩展功能。

此类接管了 `PreferenceFragmentCompat` 并对其实现了 Sp 存储在 Xposed 模块中的全局可读可写。

在你使用 `PreferenceFragmentCompat` 的实例中，将继承对象换成此类。

然后请将重写方法由 `onCreatePreferences` 替换为 `onCreatePreferencesInModuleApp` 即可。

**功能示例**

使用 `ModulePreferenceFragment` 创建一个 `PreferenceFragmentCompat` 对象。

> 示例如下

```kotlin
class SettingsFragment : ModulePreferenceFragment() {

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        // Your code here.
    }
}
```

其余用法与 `PreferenceFragmentCompat` 保持一致。

### onCreatePreferencesInModuleApp [method]

```kotlin
abstract fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?)
```

**变更记录**

`v1.0.78` `新增`

**功能描述**

> 对接原始方法 `onCreatePreferences`。

### onSharedPreferenceChanged [method]

```kotlin
override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?)
```

**变更记录**

`v1.0.78` `新增`

**功能描述**

> 实现了 `SharedPreferences.OnSharedPreferenceChangeListener` 的原生监听功能。

**功能示例**

!> 在使用 `onSharedPreferenceChanged` 时请注意保留 super 方法。

> 示例如下

```kotlin
class SettingsFragment : ModulePreferenceFragment() {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        super.onSharedPreferenceChanged(sharedPreferences, key)
        // Your code here.
    }
}
```