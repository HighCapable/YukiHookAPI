# 更新日志

### 1.0.71 | 2022.04.04

- 修复 VariousClass 无法匹配时会停止 Hook 抛出异常的严重问题

### 1.0.70 | 2022.04.04

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

### 1.0.69 | 2022.03.30

- 添加并改进一些方法功能的注释
- 增加 Demo 中的更多示例 Hook 内容
- 修复在一个 Hook 实例中，`allMethods` 多次使用时只有最后一个生效的问题，感谢 [WankkoRee](https://github.com/WankkoRee) 的反馈

### 1.0.68 | 2022.03.29

- 增加 Demo 中的新用例和 LSPosed 作用域
- 增加 `Member` 查找缓存和查找缓存配置开关
- 移除和修改 `MethodFinder`、`FieldFinder` 以及 `HookParam` 相关方法的调用
- 增加更多 `Finder` 中的 `cast` 类型并支持 `cast` 为数组
- 整体的性能和稳定性提升
- 修复上一个版本可能存在的 BUG

### 1.0.67 | 2022.03.27

- 增加三个 `Finder` 中的 `modifiers` 功能，可筛选 `static`、`native`、`public`、`abstract` 等诸多描述类型
- 增加方法和构造方法查找时可模糊方法参数类型为指定个数进行查找
- 增加 `Member` 的 `hasModifiers` 扩展功能
- 增加 `MethodFinder` 和 `ConstructorFinder` 中的 `give` 方法，可获得原始类型
- 增加 `YukiHookModulePrefs` 中的 `PrefsData` 模板功能
- 彻底对方法、构造方法及变量的查找方案进行重构
- 优化代码注释，修复了可能产生的 BUG

### 1.0.66 | 2022.03.25

- 修复 `MethodFinder` 中的一个严重问题
- 增加 `hookParam` 中的 `args` 调用方法
- 修复其它可能存在的问题以及修复部分类的注释问题

### 1.0.65 | 2022.03.25

- 重新发布版本修复 Maven 仓库因为缓存问题新版本不正确的情况
- 增加 `MethodFinder` 与 `FieldFinder` 新的返回值调用方法
- 修复可能存在的问题，并修复太极使用过程中可能存在的问题
- 修复自动生成 Xposed 入口类可能发生的问题
- 增加了 `type` 中的 `android` 类型以及 `java` 类型

### 1.0.6 | 2022.03.20

- 修复 `YukiHookModulePrefs` 在使用一次 `direct` 忽略缓存后每次都忽略的 BUG
- 增加新的 API，作废了 `isActive` 判断模块激活的传统用法
- 修复非 Xposed 环境使用 API 时打印调试日志的问题
- 修复查找 `Field` 时的日志输出问题和未拦截的异常问题
- 解耦合 `ReflectionUtils` 中的 Xposed API
- 增加 `YukiHookModuleStatus` 方法名称的混淆，以精简模块生成的体积
- 装载模块自身 Hook 时将不再打印欢迎信息
- 修复上一个版本仍然存在的某些 BUG

### 1.0.55 | 2022.03.18

- 修正一处注释错误
- 临时修复一个 BUG
- 增加了 `type` 中的大量 `android` 类型以及少量 `java` 类型
- 修复新版与旧版 Kotlin APIs 的兼容性问题

### 1.0.5 | 2022.03.18

- 修复旧版本 LSPosed 框架情况下欢迎信息多次打印的问题
- 添加 `onInit` 方法来配置 `YukiHookAPI`
- 新增 `executorName` 和 `executorVersion` 来获取当前 Hook 框架的名称和版本号
- 新增 `by` 方法来设置 Hook 的时机和条件
- `YukiHookModulePrefs` 新增可控制的键值缓存，可在宿主运行时模块动态更新数据
- 修复了一些可能存在的 BUG

### 1.0.4 | 2022.03.06

- 修复 LSPosed 在最新版本中启用“只有模块classloader可以使用Xposed API”选项后找不到 `XposedBridge` 的问题
- 添加 `YukiHookAPI` 的常量版本名称和版本号
- 新增 `hasField` 方法以及 `isAllowPrintingLogs` 配置参数
- 新增 `isDebug` 开启的情况下 API 将自动打印欢迎信息测试模块是否生效

### 1.0.3 | 2022.03.02

- 修复一个潜在性的异常未拦截 BUG
- 增加 `ignoredError` 功能
- 增加了 `type` 中的 `android` 类型
- 增加监听 `hook` 后的 `ClassNotFound` 功能

### 1.0.2 | 2022.02.18

- 修复 Windows 下无法找到项目路径的问题
- 移除部分反射 API，合并至 `BaseFinder` 进行整合
- 增加直接使用字符串创建 Hook 的方法

### 1.0.1 | 2022.02.15

- `RemedyPlan` 增加 `onFind` 功能
- 整合并修改了部分反射 API 代码
- 增加了 `type` 中的 `java` 类型
- 修复忽略错误在控制台仍然输出的问题

### 1.0 | 2022.02.14

- 首个版本提交至 Maven
