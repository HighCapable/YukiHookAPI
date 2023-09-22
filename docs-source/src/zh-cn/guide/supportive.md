# 支持性

以下是 `YukiHookAPI` 支持的相关功能、Xposed 框架、Hook Frameworks、Hook APIs。

> 基本功能

| Name                             | ST  | Description                                                                                                   |
| -------------------------------- | --- | ------------------------------------------------------------------------------------------------------------- |
| 自动化 Xposed 模块构建           | ✅   | 计划 `YukiHookAPI` 2.x.x 版本使用 [新的 Xposed 模块配置方案](https://github.com/fankes/YukiHookAPI/issues/49) |
| ART 动态方法 Hook                | ✅   | 多场景下稳定使用                                                                                              |
| Xposed 资源钩子 (Resources Hook) | ❗   | 支持，但计划 `YukiHookAPI` 2.x.x 版本作废                                                                     |

> 扩展功能

| Name                                                                                   | ST  | Description                                                                                    |
| -------------------------------------------------------------------------------------- | --- | ---------------------------------------------------------------------------------------------- |
| [字节码与反射扩展](../api/special-features/reflection)                                 | ⭕   | 计划 `YukiHookAPI` 2.x.x 版本合并到 [YukiReflection](https://github.com/fankes/YukiReflection) |
| [Xposed 模块数据存储](../api/special-features/xposed-storage)                          | ✅   | 正常使用                                                                                       |
| [Xposed 模块与宿主通讯桥](../api/special-features/xposed-channel)                      | ✅   | 正常使用                                                                                       |
| [宿主生命周期扩展](../api/special-features/host-lifecycle)                             | ✅   | 正常使用                                                                                       |
| [注入模块资源 (Resources)](../api/special-features/host-inject#注入模块资源-resources) | ✅   | 正常使用                                                                                       |
| [注册模块 Activity](../api/special-features/host-inject#注册模块-activity)             | ✅   | 正常使用                                                                                       |

> Xposed 框架

| Name                                                 | ST  | Description                             |
| ---------------------------------------------------- | --- | --------------------------------------- |
| [LSPosed](https://github.com/LSPosed/LSPosed)        | ✅   | 多场景下稳定使用                        |
| [LSPatch](https://github.com/LSPosed/LSPatch)        | ⭕   | 支持，将在此项目完善后逐渐加入 API 支持 |
| [EdXposed](https://github.com/ElderDrivers/EdXposed) | ❎   | 已停止维护，不再推荐使用                |
| [Dreamland](https://github.com/canyie/Dreamland)     | ⭕   | 理论支持 (未经过开发者测试)             |
| [TaiChi](https://github.com/taichi-framework/TaiChi) | ⭕   | Hook 功能正常 (部分功能有限制)          |
| [Xposed](https://github.com/rovo89/Xposed)           | ❎   | 已停止维护，不再推荐使用                |

> Hook 框架 (Hook Frameworks)

| Name                                                      | ST  | Description                                              |
| --------------------------------------------------------- | --- | -------------------------------------------------------- |
| [Pine](https://github.com/canyie/pine)                    | ⭕   | 理论支持 (未经过开发者测试)                              |
| [SandHook](https://github.com/asLody/SandHook)            | ❎   | 不支持较新版本的 Android，需要自行对接 Rovo89 Xposed API |
| [Whale](https://github.com/asLody/whale)                  | ❎   | 不支持较新版本的 Android，需要自行对接 Rovo89 Xposed API |
| [YAHFA](https://github.com/PAGalaxyLab/YAHFA)             | ❎   | 不支持较新版本的 Android，需要自行对接 Rovo89 Xposed API |
| [FastHook](https://github.com/turing-technician/FastHook) | ❎   | 已停止维护，不再推荐使用                                 |
| [Epic](https://github.com/tiann/epic)                     | ❎   | 已停止维护，不再推荐使用                                 |

> Hook APIs

| Name                                              | ST  | Description                       |
| ------------------------------------------------- | --- | --------------------------------- |
| [Rovo89 Xposed API](https://api.xposed.info/)     | ✅   | 多场景下稳定使用                  |
| [Modern Xposed API](https://github.com/libxposed) | ❎   | 计划 `YukiHookAPI` 2.x.x 版本支持 |