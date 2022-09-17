# Changelog

> The version update history of `YukiHookAPI` is recorded here.

::: danger

We will only maintain the latest API version, if you are using an outdate API version, you voluntarily renounce any possibility of maintenance.

:::

::: warning

To avoid translation time consumption, Changelog will use **Google Translation** from **Chinese** to **English**, please refer to the original text for actual reference.

Time zone of version release date: **UTC+8**

:::

### 1.0.92 | 2022.05.31 &ensp;<Badge type="tip" text="latest" vertical="middle" />

- Fixed the naming method of callback in a large number of methods
- Changed the solution to fix the problem that `YukiHookDataChannel` cannot call back the current `Activity` broadcast on devices lower than **Android 12**
- The `InjectYukiHookWithXposed` annotation adds the `isUsingResourcesHook` function, now you can selectively disable the dependency interface that automatically generates `IXposedHookInitPackageResources`

### 1.0.91 | 2022.05.29 &ensp;<Badge type="warning" text="stale" vertical="middle" />

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