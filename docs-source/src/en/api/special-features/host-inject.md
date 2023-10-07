# Host Resource Injection Extension

> This is an extension that injects Module App's Resources, `Activity` components, and `Context` topics into the Host App.

Before using the following functions, in order to prevent Resource Id from conflicting with each other, you need to modify the Resource Id in the `build.gradle` of the current Xposed Module project.

> Kotlin DSL

```kotlin
android {
    androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x64")
}
```

> Groovy DSL

```groovy
android {
    androidResources.additionalParameters += ['--allow-reserved-package-id', '--package-id', '0x64']
}
```

::: warning

**aaptOptions.additionalParameters** in previous versions has been deprecated, please refer to the above writing method and keep your **Android Gradle Plugin** to the latest version.

The sample Resource Id value provided is for reference only, **0x7f** cannot be used, the default is **0x64**.

In order to prevent the existence of multiple Xposed Modules in the current Host App, it is recommended to customize your own Resource Id.

:::

## Inject Module App's Resources

After the Host App is hooked, we can directly inject the `Context` obtained in the Hooker into the current Module App's Resources.

> The following example

```kotlin
method {
    name = "onCreate"
    param(BundleClass)
}.hook {
    after {
        instance<Activity>().also {
            // <Scenario 1> Inject Module App's Resources through Context
            it.injectModuleAppResources()
            // <Scenario 2> Get the Host App's Resources directly and inject the Module App's Resources
            it.resources.injectModuleAppResources()
            // Use the Module App's Resource Id directly
            it.getString(R.id.app_name)
        }
    }
}
```

You can also inject current Module App's Resources directly in `AppLifecycle`.

> The following example

```kotlin
onAppLifecycle {
    onCreate {
        // Globally inject Module App's Resources, but only in the global lifecycle
        // Methods like ImageView.setImageResource need to be injected separately in Activity
        // <Scenario 1> Inject Module App's Resources through Context
        injectModuleAppResources()
        // <Scenario 2> Get the Host App's Resources directly and inject the Module App's Resources
        resources.injectModuleAppResources()
        // Use the Module App's Resource Id directly
        getString(R.id.app_name)
    }
}
```

::: tip

For more functions, please refer to the [Context+Resources.injectModuleAppResources](../public/com/highcapable/yukihookapi/hook/factory/YukiHookFactory#context-resources-injectmoduleappresources-ext-method) method.

:::

## Register Module App's Activity

When the `Activity` of all applications in the Android system starts, it needs to be registered in `AndroidManifest.xml`.

During the Hook process, if we want to directly start the unregistered `Activity` in the Module App through the Host App, what should we do?

After the Host App is hooked, we can directly register the `Activity` proxy of the current Module App in the `Context` obtained in the Hooker.

> The following example

```kotlin
method {
    name = "onCreate"
    param(BundleClass)
}.hook {
    after {
        instance<Activity>().registerModuleAppActivities()
    }
}
```

You can also register the current Module App's `Activity` proxy directly in `AppLifecycle`.

> The following example

```kotlin
onAppLifecycle {
    onCreate {
        registerModuleAppActivities()
    }
}
```

If the `proxy` parameter is not filled in, the API will automatically obtain the current Host App's launching entry `Activity` for proxying according to the current `Context`.

Usually, it works, but the above situation will fail in some apps, for example, some `Activity` will add launching parameters to the registration list, so we need to use another solution.

If the unregistered `Activity` cannot be launched correctly, we can manually get the Host App's `AndroidManifest.xml` for analysis to get a registered `Activity` tag and get the `name`.

You need to choose an unneeded `Activity` that may not be used by the current Host App as a "puppet" to proxy it, which usually works.

For example, we have found a suitable `Activity` that can be proxied.

> The following example

```xml
<activity
    android:name="com.demo.test.activity.TestActivity"
    ...>
```

According to the `name`, we only need to add this parameter to the method for registration.

> The following example

```kotlin
registerModuleAppActivities(proxy = "com.demo.test.activity.TestActivity")
```

Alternatively, if you write a `stub` for the Host App's class, you can register it directly through the `Class` object.

> The following example

```kotlin
registerModuleAppActivities(TestActivity::class.java)
```

After the registration is complete, extends the `Activity` in the Module App you need to use the Host App to start by `ModuleAppActivity` or `ModuleAppCompatActivity`.

These `Activity` now live seamlessly in the Host App without registration.

> The following example

```kotlin
class HostTestActivity : ModuleAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Module App's Resources have been injected automatically
        // You can directly use xml to load the layout
        setContentView(R.layout.activity_main)
    }
}
```

If you need to extends `ModuleAppCompatActivity`, you need to set the AppCompat theme manually.

> The following example

```kotlin
class HostTestActivity : ModuleAppCompatActivity() {

    // The theme name here is for reference only
    // Please fill in the theme name already in your Module App
    override val moduleTheme get() = R.style.Theme_AppCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Module App's Resources have been injected automatically
        // You can directly use xml to load the layout
        setContentView(R.layout.activity_main)
    }
}
```

After all the above steps are completed, you can happily call `startActivity` anywhere in the (Xposed) Host environment where a `Context` exists.

> The following example

```kotlin
val context: Context = ... // Assume this is your Context
context.startActivity(context, HostTestActivity::class.java)
```

The `proxy` parameter we set in the `registerModuleAppActivities` method above is the default global proxy `Activity`.

If you need to specify a delegated `Activity` to use another Host App's `Activity` as a proxy, you can refer to the following method.

> The following example

```kotlin
class HostTestActivity : ModuleAppActivity() {

    // Specify an additional proxy Activity class name
    // Which must also exist in the Host App's AndroidManifest
    override val proxyClassName get() = "com.demo.test.activity.OtherActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Module App's Resources have been injected automatically
        // You can directly use xml to load the layout
        setContentView(R. layout. activity_main)
    }
}
```

::: tip

For more functions, please refer to the [Context.registerModuleAppActivities](../public/com/highcapable/yukihookapi/hook/factory/YukiHookFactory#context-registermoduleappactivities-ext-method) method.

:::

## Create ContextThemeWrapper Proxy

Sometimes, we need to use `MaterialAlertDialogBuilder` to beautify our own dialogs in the Host App, but we can't create them without the AppCompat theme.

- Will got the following exception

```:no-line-numbers
The style on this component requires your app theme to be Theme.AppCompat (or a descendant).
```

At this time, we want to use `MaterialAlertDialogBuilder` to create a dialog in the current `Activity` of the Host App being hooked, you can have the following methods.

> The following example

```kotlin
method {
    name = "onCreate"
    param(BundleClass)
}.hook {
    after {
        // Use applyModuleTheme to create a theme resource in the current Module App
        val appCompatContext = instance<Activity>().applyModuleTheme(R.style.Theme_AppCompat)
        // Directly use this Context that wraps the Module App's theme to create a dialog
        MaterialAlertDialogBuilder(appCompatContext)
            .setTitle("AppCompat Theme Dialog")
            .setMessage("I am an AppCompat theme dialog displayed in the Host App.")
            .setPositiveButton("OK", null)
            .show()
    }
}
```

You can also set the system (native) night mode and day mode on the current `Context` through `uiMode`.

Which requires at least Android 10 and above system version support and the current theme contains night mode related elements.

> The following example

```kotlin
method {
    name = "onCreate"
    param(BundleClass)
}.hook {
    after {
        // Define the theme resource in the current Module App
        var appCompatContext: ModuleContextThemeWrapper
        // <Scenario 1> Get the Configuration object directly to set
        appCompatContext = instance<Activity>()
            .applyModuleTheme(R.style.Theme_AppCompat)
            .applyConfiguration { uiMode = Configuration.UI_MODE_NIGHT_YES }
        // <Scenario 2> Create a new Configuration object
        // This solution will destroy the original font scaling and other settings in the current Host App
        // You need to manually re-pass parameters such as densityDpi
        appCompatContext = instance<Activity>().applyModuleTheme(
            theme = R.style.Theme_AppCompat,
            configuration = Configuration().apply { uiMode = Configuration.UI_MODE_NIGHT_YES }
        )
        // Directly use this Context that wraps the Module App's theme to create a dialog
        MaterialAlertDialogBuilder(appCompatContext)
            .setTitle("AppCompat Theme Dialog")
            .setMessage("I am an AppCompat theme dialog displayed in the Host App.")
            .setPositiveButton("OK", null)
            .show()
    }
}
```

This way, we can create dialogs in the Host App very simply using `MaterialAlertDialogBuilder`.

::: warning Possible Problems

Because some **androidx** dependent libraries or custom themes used by some apps may interfere with the actual style of the current **MaterialAlertDialog**, such as the button style of the dialog.

You can refer to the **Module App Demo** in this case and see [here is the sample code](https://github.com/HighCapable/YukiHookAPI/tree/master/samples/demo-module/src/main/java/com/highcapable/yukihookapi/demo_module/hook/factory/ComponentCompatFactory.kt) to fix this problem.

**ClassCastException** may occur when some apps are created, please manually specify a new **Configuration** instance to fix.

:::

::: tip

For more functions, please refer to the [Context.applyModuleTheme](../public/com/highcapable/yukihookapi/hook/factory/YukiHookFactory#context-applymoduletheme-ext-method) method.

:::

## ClassLoader Conflict Problem

The content introduced on this page is to directly inject the resources of the Module App into the Host App.

Since the Module App and the Host App are not in the same process (the same **APK**), there may be a `ClassLoader` conflict.

If a `ClassLoader` conflict occurs, you may encounter a `ClassCastException`.

`YukiHookAPI` has solved the problem of possible conflicts by default, and you need to configure the exclusion list by yourself in other cases.

The exclusion list determines whether these `Class` need to be loaded by the Module App or the Host App's `ClassLoader`.

> The following example

```kotlin
// Exclude Class names belonging to the Host App
// They will be loaded by the Host App's ClassLoader
// The following content is for demonstration only
// DO NOT USE IT DIRECTLY, please refer to your actual situation
ModuleClassLoader.excludeHostClasses(
    "androidx.core.app.ActivityCompat",
    "com.demo.Test"
)
// Exclude Class names belonging to the Module App
// They will be loaded by the ClassLoader of the Module App (the current Hook process)
// The following content is for demonstration only
// DO NOT USE IT DIRECTLY, please refer to your actual situation
ModuleClassLoader.excludeModuleClasses(
    "com.demo.entry.HookEntry",
    "com.demo.controller.ModuleController"
)
```

You need to set it before the method of injecting Module App's resources into the Host App is executed to take effect.

This function is only to solve the situation that **`Class` with the same name** may exist in the Host App and Module App, such as shared SDK and dependencies.

In most cases, you will not use this function.

::: tip

For more functions, please refer to [ModuleClassLoader](../public/com/highcapable/yukihookapi/hook/xposed/parasitic/reference/ModuleClassLoader).

:::