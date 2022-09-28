# Changelog

> The version update history of `YukiHookAPI` is recorded here.

::: danger

We will only maintain the latest API version, if you are using an outdate API version, you voluntarily renounce any possibility of maintenance.

:::

::: warning

To avoid translation time consumption, Changelog will use **Google Translation** from **Chinese** to **English**, please refer to the original text for actual reference.

Time zone of version release date: **UTC+8**

:::

### 1.1.1 | 2022.09.28 &ensp;<Badge type="tip" text="latest" vertical="middle" />

- Fixed the problem of wrong document link in [Basic Knowledge](../guide/knowledge) page
- Fixed document `favicon` not showing up
- Fixed bug in `DexClassFinder` search conditions

### 1.1.0 | 2022.09.28 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- This is a major version update, please refer to [API Document](../api/home) and [Special Features](../api/special-features/reflection) for the changes and usage mentioned in the changelog
- Change the help documentation framework to [VuePress](https://v2.vuepress.vuejs.org)
- Unify and standardize the terms and nouns in the document, for example, "query" is always changed to "find", `XposedHelper` is misspelled and changed to `XposedHelpers`
- Documentation [Basic Knowledge](../guide/knowledge) page to add a link, Simplified Chinese only
- Convert `Class` and `Method` of Hook App Demo to Java to provide better demo effect
- Fixed code comment naming in Hook Module Demo
- Refactored a lot of low-level Hook logic and the docking method of Xposed API
- Removed `HookParamWrapper`, it now interfaces directly with `YukiBridgeFactory`
- Moved methods in section `YukiHookBridge` to `AppParasitics`
- Removed `HookParam.args` and the underlying direct connection method `setArgs`, directly get and set the object of the current array
- Optimized automatic handler to merge referenced `jar` into `stub` project
- Fix the problem that the module package name cannot be correctly matched when multi-project packaging, and modify the module package name matching logic of the automatic handler, thanks to [5ec1cff](https://github.com/5ec1cff) for the feedback and solutions provided
- Internal closure processing for the methods of API private tool classes to avoid polluting the top-level namespace
- Fixed `Creater` naming to `Creator` for all reflection and Hook classes
- Added `YukiHookAPI.Status.compiledTimestamp` function, which can get the compilation completion timestamp when used as an Xposed module
- Added `YukiHookAPI.Status.isXposedEnvironment` function, which can determine whether the current (Xposed) host environment or module environment is
- The debug logging function has been overhauled, and functions such as `YukiHookAPI.Configs.debugTag` have been merged into `YukiHookLogger.Configs`
- The debug log can be added to specify the printing method as `XposedBridge.log` or `Logd`
- The package name of the current host and the current user ID are added to the debug log by default for debugging, you can change it yourself in the `debugLog` configuration
- Added `generic` function to reflect and call generics, you can use it in `Class` or `CurrentClass`
- obsolete the `buildOfAny` method, now use the `buildOf` method directly (without generics) to use the constructor to create a new object and get the result `Any`
- Fixed the issue of null pointer exception when using `hasExtends`
- `CurrentClass` added non-`lambda` method of calling
- `CurrentClass` adds `name` and `simpleName` functions
- Completely rewrite the core method of `ReflectionTool`, sorting and classifying different search conditions
- Fix the problem that `Member` obtained by directly calling `declared` in `ReflectionTool` throws an exception
- Fix `UndefinedType` in `ReflectionTool` is not correctly judged in `Method` and `Constructor` conditions
- Added a friendly prompt method when the reflection search result is abnormal, which can specifically locate the problem that `Member` cannot be found under specified conditions
- Added `VagueType` condition in `Method` and `Constructor` for reflection search, which can be used in `param` condition to ignore parameters you don't want to fill in
- Added `paramCount { ... }` condition in `Method` and `Constructor` of reflection search, now you can directly get `it` in it to customize your judgment condition
- The `current` method is added to the `FieldFinder` result, which can directly create a call space for the result instance
- Modified the `modifiers` condition and `name` condition in the reflection lookup function, now you need to return a `Boolean` at the end of the method body to make the condition true
- `as*` function in `ModifierRules` renamed to `is*`, thanks to [Kitsune](https://github.com/KyuubiRan) suggestion
- Added `RemedyPlan` feature in `FieldFinder`
- Added `Class` fuzzy search function (Beta) in `Dex`, you can now directly use `searchClass` function to fuzzy search `Class` with specified conditions
- Added multiple search function in reflection search, you can use relative search conditions to obtain multiple search results at the same time, thanks to **AA** and [Kitsune](https://github.com/KyuubiRan) for suggestions
- Fix the problem that the object obtained by `appClassLoader` is incorrect in system applications in some systems, thanks to [Luckyzyx](https://github.com/luckyzyx) for the feedback
- Modified the calling method of `XposedBridge.invokeOriginalMethod` and added `original` function in `MethodFinder.Result.Instance`
- Fix the problem of wrong value of `getStringSet` method in `YukiHookModulePrefs` and optimize the code style, thanks to [Teddy_Zhu](https://github.com/Teddy-Zhu) [PR](https://github.com/fankes/YukiHookAPI/pull/19)
- Modify `YukiHookModulePrefs` to intercept exceptions that may not exist in `XSharePreference`
- Fixed the problem that `YukiHookDataChannel` could not be successfully registered in some third-party ROM system frameworks
- Secured `YukiHookDataChannel`, now it can only communicate between modules from the specified package name and the host
- Added automatic hook `SharedPreferences` to fix the problem that file permissions are not `0664` in some systems, thanks to [5ec1cff](https://github.com/5ec1cff) for the feedback and implementation code provided
- Added `YukiHookAPI.Configs.isEnableHookSharedPreferences` function, which is disabled by default and can be enabled if the permission of `SharedPreferences` is incorrect
- Fixed the bug that the no-parameter construction method cannot be found when searching for `Constructor` without filling in the search conditions, thanks **B5 KAKA** for the feedback
- Detach `Result` instances located in `method`, `constructor` in `injectMember` to `Process`
- Added the `useDangerousOperation` method in the Hook process, which will automatically stop the Hook and print an error after the function in the Hook Dangerous List is not declared
- Added module resource injection and `Activity` proxy functions, you can call `injectModuleAppResources` and `registerModuleAppActivities` to use
- Added `ModuleContextThemeWrapper` function, you can call `applyModuleTheme` to create the `Context` of a module in any `Activity`
- Added `ClassLoader.onLoadClass` function, which can be used to listen for events when the `loadClass` method of `ClassLoader` is called
- obsolete `classOf` and `clazz` extension methods, add `toClass` and `toClassOrNull` usage, please move to the new method now
- `VariousClass` adds a `getOrNull` method, which can return `null` instead of throwing an exception when it can't match `Class`
- Removed `isUseAppClassLoader` parameter in `PackageParam.hook`, changed it to `isForceUseAbsolute` and automatically matched the target `Class`
- `PackageParam` adds `systemContext` function, you can call this function at any time to get a persistent `Context`
- no longer expose any methods in `HookClass`
- Added `throwToApp` function in `HookParam`, which can throw exceptions directly to the host
- The `onFailureThrowToApp` function is added to the Hook callback, which can be directly thrown to the host when an exception occurs
- Modified the printing logic of the debug log, the time-consuming records in the reflection search function will only be printed during the Hook process
- Added the function of removing Hook in the Hook process, you can use the `remove` and `removeSelf` methods to remove the hook
- Fixed the issue that caused the host to throw an exception when ReplaceHook failed, and now it is modified to call the original method to ensure the normal operation of the host function
- Added the function of checking the return value of the method in the Hook process. If the return value does not match, it will automatically throw an exception or print an error according to the situation
- Added `array` type to Resources Hook, thanks to [PR](https://github.com/fankes/YukiHookAPI/pull/12) of [GSWXXN](https://github.com/GSWXXN)
- Moved `me.weishu.reflection` to `thirdparty` to prevent conflicting dependencies of the same name introduced at the same time
- Remove the exception thrown when the Hook method body is empty, and modify it to print the warning log
- Modify the exception handling logic of `AppLifecycle` and throw it directly to the host when an exception occurs
- Updated Demo API version to 33

### 1.0.92 | 2022.05.31 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed the naming method of callback in a large number of methods
- Changed the solution to fix the problem that `YukiHookDataChannel` cannot call back the current `Activity` broadcast on devices lower than **Android 12**
- The `InjectYukiHookWithXposed` annotation adds the `isUsingResourcesHook` function, now you can selectively disable the dependency interface that automatically generates `IXposedHookInitPackageResources`

### 1.0.91 | 2022.05.29 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed the `ClassLoader` error when the customized system of some devices is booted in the LSPosed environment, thanks to [Luckyzyx](https://github.com/luckyzyx) for the feedback
- Fixed `YukiHookDataChannel` not being able to call back the current `Activity` broadcast on **ZUI** and systems below **Android 12**
- Integrate the `YukiHookModuleStatus` function into `YukiHookAPI.Status`, rewrite a lot of methods, now you can judge the status information such as module activation in the module and the host in both directions

### 1.0.90 | 2022.05.27 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed `YukiHookDataChannel` crashing when the module sets the listener callback
- Fixed `YukiHookDataChannel` still calling back when not in current `Activity`
- Remove the default value of `YukiHookDataChannel` callback event, no callback
- Removed `YukiHookModulePrefs` warning printed if XShare is unreadable
- Added the `isXSharePrefsReadable` method in `YukiHookModulePrefs` to determine whether the current XShare is available

### 1.0.89 | 2022.05.26 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed the problem that `YukiHookDataChannel` cannot be repeatedly set to monitor, and added the function of repeating response in different `Activity` modules and automatically following `Activity` to destroy the monitor function
- Added `YukiHookDataChannel` repeated listening use case description document
- Add the `onAlreadyHooked` method to determine whether the current method is repeated Hook
- Modify part of the logic of repeatedly adding HashMap, remove the `putIfAbsent` method, allow to override the addition
- Fixed several possible bugs

### 1.0.88 | 2022.05.25 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fully decoupled from Xposed API
- Added `android` type in `type`
- Separate `YukiHookModuleStatus` from auto-generated code and add `isEnableHookModuleStatus` switch, it is up to you to enable or not
- Internal closure processing for the constructors of a large number of classes in the API
- Set `YukiHookModulePrefs` to run as a singleton to prevent repeated creation and waste of system resources
- Fix the bug that Hook cannot be nested since version `1.0.80`, and optimize the related functions of nested Hook
- Modify the Hooker storage scheme from HashSet to HashMap to prevent the problem of repeatedly adding Hookers
- Modify the core implementation method of Hook, add duplicate checking to avoid repeating the Hook multiple callbacks to the `HookParam` method
- `MethodFinder` and `FieldFinder` add the function of finding fuzzy methods and variable names, you can call `name { ... }` to set search conditions, and support regular expressions
- Optimize and modify the way to get `appContext` to reduce the possibility of getting empty
- Modify the print `TAG` of `logger` in the automatically generated code to default to your custom name, which is convenient for debugging
- Optimize the `Hooker` implementation of `YukiHookBridge` to improve Hook performance
- `PackageParam` adds the `onAppLifecycle` method, which can natively monitor the life cycle of the host and implement the registration system broadcast function
- Added `YukiHookDataChannel` function to communicate using system out-of-order broadcast while the module and the host remain alive
- `YukiHookDataChannel` adds the `checkingVersionEquals` method, which can be monitored to verify that the host has not updated the version mismatch problem after the module is updated
- Added Java version example in the example code of `demo-module` for reference only

### 1.0.87 | 2022.05.10 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Added `refreshModuleAppResources` function to adapt Resources refresh when the language region, font size, resolution changes, etc.
- Added `isEnableModuleAppResourcesCache` function, you can set whether to automatically cache the resources of the current module

### 1.0.86 | 2022.05.06 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed the problem of continuous error reporting during `initZygote` when Resources Hook is not supported, reproduced in **ZUI**/**LSPosed CI(1.8.3-6550)**
- Optimize and handle exceptions for Resources Hook, only print errors and warnings if they are used and not supported

### 1.0.85 | 2022.05.04 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed a serious problem of not being able to hook the system framework, since `1.0.80`
- Added in the debug log to distinguish the package name loaded by `initZygote` as `android-zygote`, `packageName` keeps `android` unchanged

### 1.0.83 | 2022.05.04 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed `YukiHookModuleStatus` reporting a lot of errors after `loadSystem`
- Added `android` type in `type`
- Updated example descriptions in help documentation

### 1.0.82 | 2022.05.04 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed a concept confusion error, distinguishing the relationship between `initZygote` and the system framework, there are problems with the previous comments and documentation, I am very sorry
- `PackageParam` adds `loadSystem` method, no need to write `loadApp(name = "android")` to hook the system framework

### 1.0.81 | 2022.05.04 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fix the problem that the method and constructor that cannot be found in the Hook method body still output the error log after setting the condition using the `by` method
- Added a global log to display the package name of the current Hook APP during the execution of the Hook, and fixed a problem with the printing style of the error log

### 1.0.80 | 2022.05.01 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- The `InjectYukiHookWithXposed` annotation adds the `entryClassName` function, which can customize the generated `xposed_init` entry class name
- ~~`YukiHookXposedInitProxy`~~ renamed to `IYukiHookXposedInit`, the original interface name has been invalidated and will be deleted directly in subsequent versions
- Added `initZygote` and Resources Hook functions to support Hook Layout
- Added `onXposedEvent` method to listen to all events of native Xposed API
- Perform `inline` processing on the `lambda` of the Hook function to avoid generating excessively broken anonymous classes and improve the running performance after compilation
- Fix `PrefsData` compiled method body copy is too large
- Added `XSharePreference` readability test, which will automatically print a warning log if it fails
- `PackageParam` adds `appResources`, `moduleAppResources`, `moduleAppFilePath` functions
- `loadApp` of `PackageParam` adds the function of not writing `name`, and all APPs are filtered by default
- `PackageParam` adds the `loadZygote` method, which can directly hook the system framework
- `PackageParam` added `resources().hook` function
- Optimization method, construction method, variable search function, the error log that cannot be found will display the set query conditions first
- Added `hasExtends` extension method to determine whether the current `Class` has an inheritance relationship
- Added `isSupportResourcesHook` function to determine whether resource hooks are currently supported (Resources Hook)
- `current` function adds `superClass` method to call superclass
- New `superClass` query conditions for search methods, construction methods and variables, you can continue to search in the parent class
- `YukiHookAPI` lots of methods are decoupled from Xposed API
- Added native Hook priority function of Xposed API
- Fix the problem that `isFirstApplication` may be inaccurate
- Block the problem that MiuiCatcherPatch repeatedly calls the Hook entry method on the MIUI system
- Optimize Hook entry calling method to avoid multiple calls due to Hook Framework issues
- Fix the problem that Hook `ClassLoader` causes Hook to freeze, thanks to [WankkoRee](https://github.com/WankkoRee) for the feedback
- Improve the performance after the `XC_Callback` interface is connected
- Java `type` added `ClassLoader` type
- Optimize the API help documentation, fix the problem that the page may be continuously cached

### 1.0.78 | 2022.04.18 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- `YukiHookModulePrefs` adds `isRunInNewXShareMode` method, which can be used to determine whether the module is currently in `New XSharePreference` mode
- Fixed `YukiHookModulePrefs` working in `New XSharePreference` mode
- Added `ModulePreferenceFragment`, now you can completely replace `PreferenceFragmentCompat` and start using the new functionality
- Adapt the Sp data storage solution of `PreferenceFragmentCompat`, thanks to [mahoshojoHCG](https://github.com/mahoshojoHCG) for feedback
- Update autohandlers and `Kotlin` dependencies to the latest version
- Fixed some bugs in documentation and code comments

### 1.0.77 | 2022.04.15 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- `YukiHookModulePrefs` added `clear` method, thanks to [WankkoRee](https://github.com/WankkoRee) for the suggestion
- `YukiHookModulePrefs` added `getStringSet`, `putStringSet`, `all` methods
- Added `any` method to `args` of `HookParam`
- Added `ModuleApplication`, which can be inherited in modules to achieve more functions
- Connect all `findClass` functions to the Xposed API, and continue to use native `ClassLoader` in non-hosted environments
- Fixed some possible bugs

### 1.0.75 | 2022.04.13 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Corrected the logic recognition part of the automatic handler, thanks to [ApeaSuperz](https://github.com/ApeaSuperz) contribution
- Fixed an issue where the reference to a doc comment was not changed
- `firstArgs` and `lastArgs` methods have been removed from `HookParam`, now you can use `args().first()` and `args().last()` instead of it
- Removed default parameter `index = 0` in `args()` in `HookParam`, now you can use `args().first()` or `args(index = 0)` to replace it
- The `result` function in `HookParam` adds generic matching, now you can use `result<T>` to match the known return value type of your target method
- The `emptyParam` condition is added to the method and constructor query function, and the misunderstanding of the query condition that needs to be paid attention to in the document has been improved
- Added `android` type in `type`

### 1.0.73 | 2022.04.10 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed some Chinese translation errors in documents, thanks to [WankkoRee](https://github.com/WankkoRee) for their contributions
- Fix the problem that `XC_LoadPackage.LoadPackageParam` throws an exception when the content is empty in some cases, thanks to [Luckyzyx](https://github.com/luckyzyx) for the feedback
- Fix some known bugs and improve Hook stability

### 1.0.72 | 2022.04.09 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Update API documentation to new address
- Add `appContext` function to `PackageParam`
- Fix some known bugs and improve Hook stability

### 1.0.71 | 2022.04.04 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed a serious issue that would stop the Hook from throwing an exception when VariousClass could not be matched

### 1.0.70 | 2022.04.04 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed `instanceClass` reporting an error after being called in a static instance
- Add `isUseAppClassLoader` function in Hook process, thanks to [WankkoRee](https://github.com/WankkoRee) for feedback
- Added the `withProcess` function, which can be hooked according to the currently specified process of the APP
- Fixed critical logic errors in lookup methods, constructor classes and variables
- Fixed the problem that the abnormal output cannot be ignored when the Hook target class does not exist
- Fixed the problem that the Hook could not take effect due to the fast loading of the APP startup method in some cases
- Fixed `allMethods` not throwing an exception when it is not hooked to a method, thanks to [WankkoRee](https://github.com/WankkoRee) for the feedback
- Added Hook status monitoring function, thanks to [WankkoRee](https://github.com/WankkoRee) for the suggestion
- Modify the way the Xposed entry is injected into the class, and redefine the definition domain of the API
- Added obfuscated method and variable lookup function, you can use different types of filter `index` to locate the specified method and variable, thanks to [WankkoRee](https://github.com/WankkoRee) for the ideas provided
- When looking for methods and variables, multiple types are allowed, such as the class name declared by `String` and `VariousClass`
- Add a new `current` function, which can build a reflection method operation space for any class, and easily call and modify the methods and variables in it
- Fixed a lot of bugs in the hook process, thanks to [WankkoRee](https://github.com/WankkoRee) for contributing to this project

### 1.0.69 | 2022.03.30 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Added and improved annotations for some method functions
- Added more example Hook content in Demo
- Fixed the issue that only the last one takes effect when `allMethods` is used multiple times in a Hook instance, thanks to [WankkoRee](https://github.com/WankkoRee) for the feedback

### 1.0.68 | 2022.03.29 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Added new use case and LSPosed scope in Demo
- Added `Member` lookup cache and lookup cache configuration switches
- Removed and modified `MethodFinder`, `FieldFinder` and `HookParam` related method calls
- Add more `cast` types in `Finder` and support `cast` as array
- Overall performance and stability improvements
- Fix bugs that may exist in the previous version

### 1.0.67 | 2022.03.27 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Added three `modifiers` functions in `Finder`, which can filter `static`, `native`, `public`, `abstract` and many other description types
- When searching for methods and constructors, the method parameter type can be blurred to a specified number for searching
- Added `hasModifiers` extension for `Member`
- Added `give` method in `MethodFinder` and `ConstructorFinder` to get primitive types
- Added `PrefsData` template function in `YukiHookModulePrefs`
- Completely refactored method, constructor and variable lookup scheme
- Optimized code comments and fixed possible bugs

### 1.0.66 | 2022.03.25 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed a serious bug in `MethodFinder`
- Added `args` call method in `hookParam`
- Fix other possible problems and fix some class annotation problems

### 1.0.65 | 2022.03.25 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Republished version to fix the incorrect new version of the Maven repository due to cache issues
- Added `MethodFinder` and `FieldFinder` new return value calling methods
- Fix possible problems and fix possible problems during the use of Tai Chi
- Fixed possible problems with auto-generated Xposed entry classes
- Added `android` type and `java` type in `type`

### 1.0.6 | 2022.03.20 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed `YukiHookModulePrefs` being ignored every time after using `direct` once to ignore cache
- Added new API, abolished the traditional usage of `isActive` to judge module activation
- Fixed the issue of printing debug logs when using the API in a non-Xposed environment
- Fixed log output issue and unintercepted exception issue when looking for `Field`
- Decoupling Xposed API in `ReflectionUtils`
- Added `YukiHookModuleStatus` method name confusion to reduce the size of module generation
- The welcome message will no longer be printed when loading the module's own Hook
- Fix some bugs that still exist in the previous version

### 1.0.55 | 2022.03.18 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed an annotation error
- Temporarily fix a bug
- Added a large number of `android` types in `type` and a small number of `java` types
- Fix compatibility issues between new and old Kotlin APIs

### 1.0.5 | 2022.03.18 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed the problem that the welcome message was printed multiple times in the case of the old version of the LSPosed framework
- Added `onInit` method to configure `YukiHookAPI`
- Added `executorName` and `executorVersion` to get the name and version number of the current hook framework
- Added `by` method to set the timing and condition of Hook
- `YukiHookModulePrefs` adds a controllable key-value cache, which can dynamically update data when the host is running
- Fixed some possible bugs

### 1.0.4 | 2022.03.06 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fix LSPosed cannot find `XposedBridge` after enabling "Only module classloader can use Xposed API" option in latest version
- Added constant version name and version number for `YukiHookAPI`
- Added `hasField` method and `isAllowPrintingLogs` configuration parameter
- Added `isDebug` to enable the API to automatically print the welcome message to test whether the module is valid

### 1.0.3 | 2022.03.02 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fixed a potential exception not intercepted BUG
- Added `ignoredError` function
- Added `android` type in `type`
- Added `ClassNotFound` function after listening to `hook`

### 1.0.2 | 2022.02.18 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- Fix the problem that the project path cannot be found under Windows
- Remove part of reflection API, merge into `BaseFinder` for integration
- Add a method to create Hook directly using string

### 1.0.1 | 2022.02.15 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- `RemedyPlan` adds `onFind` function
- Integrate and modify some reflection API code
- Added `java` type in `type`
- Fixed the issue that ignored errors still output in the console

### 1.0 | 2022.02.14 &ensp;<Badge type="danger" text="outdate" vertical="middle" />

- The first version is submitted to Maven