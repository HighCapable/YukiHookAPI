# 更新日志

> 这里记录了 `YukiHookAPI` 的版本更新历史。

::: danger

我们只会对最新的 API 版本进行维护，若你正在使用过时的 API 版本则代表你自愿放弃一切维护的可能性。

:::

### 1.0.92 | 2022.05.31 &ensp;<Badge type="tip" text="最新" vertical="middle" />

- 修正了大量方法中 callback 的命名方法
- 更换方案再次修复 `YukiHookDataChannel` 在低于 **Android 12** 的设备上不能回调当前 `Activity` 广播的问题
- `InjectYukiHookWithXposed` 注解新增 `isUsingResourcesHook` 功能，现在你可以选择性关闭自动生成 `IXposedHookInitPackageResources` 的依赖接口了

### 1.0.91 | 2022.05.29 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 修复部分设备的定制系统在 LSPosed 环境下开机启动获取的 `ClassLoader` 错误的问题，感谢 [Luckyzyx](https://github.com/luckyzyx) 的反馈
- 修复 `YukiHookDataChannel` 在 **ZUI** 以及低于 **Android 12** 的系统上不能回调当前 `Activity` 广播的问题
- 整合 `YukiHookModuleStatus` 功能到 `YukiHookAPI.Status`，重写了大量方法，现在你可以在模块与宿主中双向判断模块激活等状态信息

### 1.0.90 | 2022.05.27 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 `YukiHookDataChannel` 在模块设置监听回调时闪退的问题
- 修复 `YukiHookDataChannel` 在非当前 `Activity` 情况下依然会回调的问题
- 移除 `YukiHookDataChannel` 回调事件的默认值，没有即不回调
- 移除 `YukiHookModulePrefs` 在 XShare 不可读的情况下打印的警告
- 新增 `YukiHookModulePrefs` 中的 `isXSharePrefsReadable` 方法，可判断当前的 XShare 是否可用

### 1.0.89 | 2022.05.26 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 `YukiHookDataChannel` 不能重复设置监听的问题，并加入在模块不同 `Activity` 中重复响应和自动跟随 `Activity` 销毁监听功能
- 新增 `YukiHookDataChannel` 重复监听用例说明文档
- 加入 `onAlreadyHooked` 方法，可判断当前方法是否被重复 Hook
- 修改部分重复添加 HashMap 的逻辑，移除 `putIfAbsent` 方法，允许覆盖添加
- 修复了几处可能的 BUG

### 1.0.88 | 2022.05.25 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 对 Xposed API 完全解耦合
- 增加了 `type` 中的 `android` 类型
- 将 `YukiHookModuleStatus` 从自动生成代码中分离，并加入 `isEnableHookModuleStatus` 的开关，由你决定是否启用
- 对 API 大量类的构造方法进行了 internal 闭包处理
- 将 `YukiHookModulePrefs` 设置为单例运行，防止重复创建浪费系统资源
- 修复自 `1.0.80` 版本后无法嵌套 Hook 的 BUG，并优化嵌套 Hook 相关功能
- 修改 Hooker 存储方案由 HashSet 到 HashMap，防止重复添加 Hooker 的问题
- 修改 Hook 核心实现方法，加入查重，避免重复 Hook 多次回调 `HookParam` 方法
- `MethodFinder` 与 `FieldFinder` 加入查找模糊方法、变量名称功能，可调用 `name { ... }` 来设置查找条件，支持正则
- 优化并修改 `appContext` 的获取方式，降低会取到空的问题的可能性
- 修改自动生成的代码中 `logger` 的打印 `TAG` 默认为你自定义的名称，方便进行调试
- 优化 `YukiHookBridge` 的 `Hooker` 实现方式，提升 Hook 性能
- `PackageParam` 增加 `onAppLifecycle` 方法，可原生监听宿主的生命周期以及实现注册系统广播功能
- 新增 `YukiHookDataChannel` 功能，可在模块与宿主保持存活的情况下使用系统无序广播进行通讯
- `YukiHookDataChannel` 增加 `checkingVersionEquals` 方法，可通过监听来验证模块更新后宿主并未更新版本不匹配问题
- `demo-module` 的示例代码中新增 Java 版本的示例，仅供参考

### 1.0.87 | 2022.05.10 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 新增 `refreshModuleAppResources` 功能，以适配语言区域、字体大小、分辨率改变等情况下的 Resources 刷新
- 新增 `isEnableModuleAppResourcesCache` 功能，可自行设置是否自动缓存当前模块的 Resources

### 1.0.86 | 2022.05.06 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复不支持 Resources Hook(资源钩子) 的情况下在 `initZygote` 时持续报错的问题，复现在 **ZUI**/**LSPosed CI(1.8.3-6550)**
- 优化并对 Resources Hook 进行异常处理，只有被使用后不支持才会打印错误和警告

### 1.0.85 | 2022.05.04 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复无法 Hook 系统框架的严重问题，从 `1.0.80` 开始出现
- 调试日志中新增区分 `initZygote` 装载的包名为 `android-zygote`，`packageName` 保持 `android` 不变

### 1.0.83 | 2022.05.04 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 `YukiHookModuleStatus` 在 `loadSystem` 后大量报错的问题
- 新增 `type` 中的 `android` 类型
- 更新帮助文档的示例说明

### 1.0.82 | 2022.05.04 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复了一处概念混淆错误，区分 `initZygote` 与系统框架的关系，之前的注释和文档有问题，非常抱歉
- `PackageParam` 新增 `loadSystem` 方法，不需要再写 `loadApp(name = "android")` 即可 Hook 系统框架

### 1.0.81 | 2022.05.04 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复使用 `by` 方法设置条件后 Hook 方法体内查找不到的方法、构造方法依然输出错误日志的问题
- 在执行 Hook 过程中加入全局日志显示当前 Hook APP 的包名，并修复一处错误日志打印样式的问题

### 1.0.80 | 2022.05.01 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- `InjectYukiHookWithXposed` 注解新增 `entryClassName` 功能，可自定义生成的 `xposed_init` 入口类名
- ~~`YukiHookXposedInitProxy`~~ 更名为 `IYukiHookXposedInit`，原接口名称已作废，将在后续版本中直接被删除
- 新增 `initZygote` 与 Resources Hook 功能，支持 Hook Layout
- 新增 `onXposedEvent` 方法，可监听原生 Xposed API 的全部事件
- 对 Hook 功能的 `lambda` 进行 `inline` 处理，避免生成过碎的匿名类，提升编译后的运行性能
- 修复 `PrefsData` 编译后的方法体复制过大的问题
- 增加 `XSharePreference` 可读性测试，失败后会自动打印警告日志
- `PackageParam` 新增 `appResources`、`moduleAppResources`、`moduleAppFilePath` 功能
- `PackageParam` 的 `loadApp` 新增不写 `name` 功能，默认筛选全部 APP
- `PackageParam` 新增 `loadZygote` 方法，可直接 Hook 系统框架
- `PackageParam` 新增 `resources().hook` 功能
- 优化方法、构造方法、变量查找功能，找不到的错误日志将优先显示已设置的查找条件
- 增加 `hasExtends` 扩展方法，可判断当前 `Class` 是否有继承关系
- 增加 `isSupportResourcesHook` 功能，判断当前是否支持资源钩子(Resources Hook)
- `current` 功能新增 `superClass` 方法调用父类
- 查找方法、构造方法、变量新增 `superClass` 查找条件，可继续在父类中查找
- `YukiHookAPI` 大量方法与 Xposed API 解耦合
- 新增 Xposed API 的原生 Hook 优先级功能
- 修复 `isFirstApplication` 可能判断不准确的问题
- 屏蔽 MIUI 系统上 MiuiCatcherPatch 重复调用 Hook 入口方法的问题
- 优化 Hook 入口调用方法，避免因为 Hook Framework 问题导致多次调用
- 修复 Hook `ClassLoader` 导致 Hook 卡死的问题，感谢 [WankkoRee](https://github.com/WankkoRee) 的反馈
- 提升 `XC_Callback` 接口对接后的性能
- Java `type` 新增 `ClassLoader` 类型
- 优化 API 帮助文档，修复可能持续缓存页面的问题

### 1.0.78 | 2022.04.18 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- `YukiHookModulePrefs` 新增 `isRunInNewXShareMode` 方法，可用于判断模块当前是否处于 `New XSharePreference` 模式
- 修复 `YukiHookModulePrefs` 在 `New XSharePreference` 模式下工作的部分问题
- 新增 `ModulePreferenceFragment`，现在，你可以完全替换掉 `PreferenceFragmentCompat` 并开始使用新的功能
- 适配 `PreferenceFragmentCompat` 的 Sp 数据存储解决方案，感谢 [mahoshojoHCG](https://github.com/mahoshojoHCG) 的反馈
- 更新自动处理程序以及 `Kotlin` 依赖到最新版本
- 修正部分文档和代码注释中的错误

### 1.0.77 | 2022.04.15 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- `YukiHookModulePrefs` 新增 `clear` 方法，感谢 [WankkoRee](https://github.com/WankkoRee) 的建议
- `YukiHookModulePrefs` 新增 `getStringSet`、`putStringSet`、`all` 方法
- `HookParam` 的 `args` 增加 `any` 方法
- 新增 `ModuleApplication`，可在模块中继承此类实现更多功能
- 对接全部的 `findClass` 功能到 Xposed API，在非宿主环境继续使用原生 `ClassLoader`
- 修复了一些可能存在的 BUG

### 1.0.75 | 2022.04.13 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 更正了自动处理程序的逻辑识别部分，感谢 [ApeaSuperz](https://github.com/ApeaSuperz) 的贡献
- 修正一处文档注释的引用未更改的问题
- `HookParam` 中删除了 `firstArgs` 与 `lastArgs` 方法，现在你可以使用 `args().first()` 与 `args().last()` 来取代它
- `HookParam` 中删除了 `args()` 中的默认参数 `index = 0`，现在你可以使用 `args().first()` 或 `args(index = 0)` 来取代它
- `HookParam` 中 `result` 功能增加了泛型匹配，现在你可以使用 `result<T>` 来匹配你的目标方法已知返回值类型了
- 方法、构造方法查找功能新增 `emptyParam` 条件，并完善了文档相关需要注意的查找条件误区
- 增加了 `type` 中的 `android` 类型

### 1.0.73 | 2022.04.10

- 修正几处文档的中文翻译错误，感谢 [WankkoRee](https://github.com/WankkoRee) 的贡献
- 修复在某些情况下 `XC_LoadPackage.LoadPackageParam` 内容为空抛出异常的问题，感谢 [Luckyzyx](https://github.com/luckyzyx) 的反馈
- 修复一些已知的 BUG，提升 Hook 稳定性

### 1.0.72 | 2022.04.09 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 更新 API 文档到新的地址
- `PackageParam` 中加入 `appContext` 功能
- 修复一些已知的 BUG，提升 Hook 稳定性

### 1.0.71 | 2022.04.04 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 VariousClass 无法匹配时会停止 Hook 抛出异常的严重问题

### 1.0.70 | 2022.04.04 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 `instanceClass` 在静态实例中调用后报错问题
- 在 Hook 过程中加入 `isUseAppClassLoader` 功能，感谢 [WankkoRee](https://github.com/WankkoRee) 的反馈
- 加入 `withProcess` 功能，可根据 APP 当前指定进程进行 Hook
- 修复查找方法、构造类和变量的严重逻辑错误问题
- 修复 Hook 目标类不存在的时候无法忽略异常输出的问题
- 修复部分情况下 APP 启动方法装载过快导致 Hook 不能生效的问题
- 修复 `allMethods` 未 Hook 到方法时不会抛出异常的问题，感谢 [WankkoRee](https://github.com/WankkoRee) 的反馈
- 加入 Hook 状态监听功能，感谢 [WankkoRee](https://github.com/WankkoRee) 的建议
- 修改 Xposed 入口注入类的方式，重新声明 API 的定义域
- 加入混淆的方法以及变量的查找功能，可使用不同类型筛选 `index` 定位指定的方法和变量，感谢 [WankkoRee](https://github.com/WankkoRee) 提供的思路
- 查找方法、变量时允许传入多种类型，例如 `String` 声明的类名和 `VariousClass`
- 加入全新的 `current` 功能，可对任意的类构建一个反射方法操作空间，方便地调用和修改其中的方法和变量
- 修复了 Hook 过程中的大量 BUG，感谢 [WankkoRee](https://github.com/WankkoRee) 对此项目所做出的贡献

### 1.0.69 | 2022.03.30 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 添加并改进一些方法功能的注释
- 增加 Demo 中的更多示例 Hook 内容
- 修复在一个 Hook 实例中，`allMethods` 多次使用时只有最后一个生效的问题，感谢 [WankkoRee](https://github.com/WankkoRee) 的反馈

### 1.0.68 | 2022.03.29 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 增加 Demo 中的新用例和 LSPosed 作用域
- 增加 `Member` 查找缓存和查找缓存配置开关
- 移除和修改 `MethodFinder`、`FieldFinder` 以及 `HookParam` 相关方法的调用
- 增加更多 `Finder` 中的 `cast` 类型并支持 `cast` 为数组
- 整体的性能和稳定性提升
- 修复上一个版本可能存在的 BUG

### 1.0.67 | 2022.03.27 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 增加三个 `Finder` 中的 `modifiers` 功能，可筛选 `static`、`native`、`public`、`abstract` 等诸多描述类型
- 增加方法和构造方法查找时可模糊方法参数类型为指定个数进行查找
- 增加 `Member` 的 `hasModifiers` 扩展功能
- 增加 `MethodFinder` 和 `ConstructorFinder` 中的 `give` 方法，可获得原始类型
- 增加 `YukiHookModulePrefs` 中的 `PrefsData` 模板功能
- 彻底对方法、构造方法及变量的查找方案进行重构
- 优化代码注释，修复了可能产生的 BUG

### 1.0.66 | 2022.03.25 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 `MethodFinder` 中的一个严重问题
- 增加 `hookParam` 中的 `args` 调用方法
- 修复其它可能存在的问题以及修复部分类的注释问题

### 1.0.65 | 2022.03.25 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 重新发布版本修复 Maven 仓库因为缓存问题新版本不正确的情况
- 增加 `MethodFinder` 与 `FieldFinder` 新的返回值调用方法
- 修复可能存在的问题，并修复太极使用过程中可能存在的问题
- 修复自动生成 Xposed 入口类可能发生的问题
- 增加了 `type` 中的 `android` 类型以及 `java` 类型

### 1.0.6 | 2022.03.20 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 `YukiHookModulePrefs` 在使用一次 `direct` 忽略缓存后每次都忽略的 BUG
- 增加新的 API，作废了 `isActive` 判断模块激活的传统用法
- 修复非 Xposed 环境使用 API 时打印调试日志的问题
- 修复查找 `Field` 时的日志输出问题和未拦截的异常问题
- 解耦合 `ReflectionUtils` 中的 Xposed API
- 增加 `YukiHookModuleStatus` 方法名称的混淆，以精简模块生成的体积
- 装载模块自身 Hook 时将不再打印欢迎信息
- 修复上一个版本仍然存在的某些 BUG

### 1.0.55 | 2022.03.18 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修正一处注释错误
- 临时修复一个 BUG
- 增加了 `type` 中的大量 `android` 类型以及少量 `java` 类型
- 修复新版与旧版 Kotlin APIs 的兼容性问题

### 1.0.5 | 2022.03.18 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复旧版本 LSPosed 框架情况下欢迎信息多次打印的问题
- 添加 `onInit` 方法来配置 `YukiHookAPI`
- 新增 `executorName` 和 `executorVersion` 来获取当前 Hook 框架的名称和版本号
- 新增 `by` 方法来设置 Hook 的时机和条件
- `YukiHookModulePrefs` 新增可控制的键值缓存，可在宿主运行时模块动态更新数据
- 修复了一些可能存在的 BUG

### 1.0.4 | 2022.03.06 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 LSPosed 在最新版本中启用“只有模块classloader可以使用Xposed API”选项后找不到 `XposedBridge` 的问题
- 添加 `YukiHookAPI` 的常量版本名称和版本号
- 新增 `hasField` 方法以及 `isAllowPrintingLogs` 配置参数
- 新增 `isDebug` 开启的情况下 API 将自动打印欢迎信息测试模块是否生效

### 1.0.3 | 2022.03.02 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复一个潜在性的异常未拦截 BUG
- 增加 `ignoredError` 功能
- 增加了 `type` 中的 `android` 类型
- 增加监听 `hook` 后的 `ClassNotFound` 功能

### 1.0.2 | 2022.02.18 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 修复 Windows 下无法找到项目路径的问题
- 移除部分反射 API，合并至 `BaseFinder` 进行整合
- 增加直接使用字符串创建 Hook 的方法

### 1.0.1 | 2022.02.15 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- `RemedyPlan` 增加 `onFind` 功能
- 整合并修改了部分反射 API 代码
- 增加了 `type` 中的 `java` 类型
- 修复忽略错误在控制台仍然输出的问题

### 1.0 | 2022.02.14 &ensp;<Badge type="danger" text="过期" vertical="middle" />

- 首个版本提交至 Maven