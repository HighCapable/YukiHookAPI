# Xposed Module Data Storage

> This is an efficient Module App data storage solution that automatically connects `SharedPreferences` and `XSharedPreferences`.

We need to store the data of the Module App for the Host App to call.

At this time, we will encounter the data exchange obstacle of the native `Sp` storage.

The native `Xposed` provides us with a `XSharedPreferences` for reading the `Sp` data of the Module App.

## Use in Activity

> Loading `YukiHookModulePrefs` in `Activity` is described here.

Usually we can initialize it in Host App like this.

> The following example

```kotlin
XSharedPreferences(BuildConfig.APPLICATION_ID)
```

Is there a convenient and quick solution?

At this point, you can use the extension capability of `YukiHookAPI` to quickly implement this function.

When you store data in a Module App, you can use the following methods if you are currently in an `Activity`.

> The following example

```kotlin
modulePrefs.putString("test_name", "saved_value")
```

When you read data in a Host App, you can use the following methods.

> The following example

```kotlin
val testName = prefs.getString("test_name", "default_value")
```

You don't need to consider the module package name and a series of complicated permission configurations, everything is handled by `YukiHookModulePrefs`.

To achieve localization of storage, you can specify the name of each `prefs` file.

This is used in the `Activity` of the Module App.

> The following example

```kotlin
// Recommended usage
modulePrefs("specify_file_name").putString("test_name", "saved_value")
// Can also be used like this
modulePrefs.name("specify_file_name").putString("test_name", "saved_value")
```

Read like this in Host App.

> The following example

```kotlin
// Recommended usage
val testName = prefs("specify_file_name").getString("test_name", "default_value")
// Can also be used like this
val testName = prefs.name("specify_file_name").getString("test_name", "default_value")
```

If your project has a lot of fixed data that needs to be stored and read, it is recommended to use `PrefsData` to create templates.

::: tip

For more functions, please refer to [YukiHookModulePrefs](../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookModulePrefs), [PrefsData](../public/com/highcapable/yukihookapi/hook/xposed/prefs/data/PrefsData).

:::

## Use in PreferenceFragment

> Loading `YukiHookModulePrefs` in `PreferenceFragment` is described here.

If your Module App uses `PreferenceFragmentCompat`, you can now start migrating its extends `ModulePreferenceFragment`.

::: danger

You must extends **ModulePreferenceFragment** to implement the module storage function of **YukiHookModulePrefs**.

:::

::: tip

For more functions, please refer to [ModulePreferenceFragment](../public/com/highcapable/yukihookapi/hook/xposed/prefs/ui/ModulePreferenceFragment).

:::