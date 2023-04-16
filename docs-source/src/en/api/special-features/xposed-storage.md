# Xposed Module Data Storage

> This is an efficient Module App data storage solution that automatically connects `SharedPreferences` and `XSharedPreferences`.

We need to store the data of the Module App for the Host App to call.

At this time, we will encounter the data exchange obstacle of the native `Sp` storage.

The native `Xposed` provides us with a `XSharedPreferences` for reading the `Sp` data of the Module App.

## Use in Activity

> Loading `YukiHookPrefsBridge` in `Activity` is described here.

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
prefs().edit { putString("test_name", "saved_value") }
```

When you read data in a Host App, you can use the following methods.

> The following example

```kotlin
val testName = prefs.getString("test_name", "default_value")
```

You don't need to consider the module package name and a series of complicated permission configurations, everything is handled by `YukiHookPrefsBridge`.

To achieve localization of storage, you can specify the name of each `prefs` file.

This is used in the `Activity` of the Module App.

> The following example

```kotlin
// Recommended usage
prefs("specify_file_name").edit { putString("test_name", "saved_value") }
// Can also be used like this
prefs().name("specify_file_name").edit { putString("test_name", "saved_value") }
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

Through the above example, you can call the `edit` method to store data in batches in the following two ways.

> The following example

```kotlin
// <Scenario 1>
prefs().edit {
     putString("test_name_1", "saved_value_1")
     putString("test_name_2", "saved_value_2")
     putString("test_name_3", "saved_value_3")
}
// <Scenario 2>
prefs(). edit()
     .putString("test_name_1", "saved_value_1")
     .putString("test_name_2", "saved_value_2")
     .putString("test_name_3", "saved_value_3")
     .apply()
```

::: tip

For more functions, please refer to [YukiHookPrefsBridge](../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge), [PrefsData](../public/com/highcapable/yukihookapi/hook/xposed/prefs/data/PrefsData).

:::

## Use in PreferenceFragment

> Loading `YukiHookPrefsBridge` in `PreferenceFragment` is described here.

If your Module App uses `PreferenceFragmentCompat`, you can now start migrating its extends `ModulePreferenceFragment`.

::: danger

You must extends **ModulePreferenceFragment** to implement the module storage function of **YukiHookPrefsBridge**.

:::

::: tip

For more functions, please refer to [ModulePreferenceFragment](../public/com/highcapable/yukihookapi/hook/xposed/prefs/ui/ModulePreferenceFragment).

:::

## Use Native Storage

In the Module environment, `YukiHookPrefsBridge` will store data in the Module App's own private directory (or the shared directory provided by Hook Framework) by default.

Using `YukiHookPrefsBridge` in the Host environment will read the data in the Module App's own private directory (or the shared directory provided by Hook Framework) by default.

If you want to store data directly into a Module App or Host App's own private directory, you can use the `native` method.

For example, the directory of the Module App is `.../com.demo.test.module/shared_prefs`, and the directory of the Host App is `.../com.demo.test.host/shared_prefs`.

The following is the usage in `Activity`.

> The following example

```kotlin
// Store private data
prefs().native().edit { putBoolean("isolation_data", true) }
// Read private data
val privateData = prefs().native().getBoolean("isolation_data")
// Store shared data
prefs().edit { putBoolean("public_data", true) }
// Read shared data
val publicData = prefs().getBoolean("public_data")
```

The following is the usage in `PackageParam`.

> The following example

```kotlin
// Store private data
prefs.native().edit { putBoolean("isolation_data", true) }
// Read private data
val privateData = prefs.native().getBoolean("isolation_data")
// Read shared data
val publicData = prefs.getBoolean("public_data")
```

After using the `native` method, no matter in `Activity` or `PackageParam`, the data <u>**will be stored and read in the private directory of the corresponding environment**</u>, and the data will be isolated from each other.

::: tip

For more functions, please refer to [YukiHookPrefsBridge](../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge).

:::