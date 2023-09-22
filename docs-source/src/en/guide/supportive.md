# Supportive

The following are the related functions, Xposed Frameworks, Hook Frameworks and Hook APIs supported by `YukiHookAPI`.

> Basic Functions

| Name                       | ST  | Description                                                                                                      |
| -------------------------- | --- | ---------------------------------------------------------------------------------------------------------------- |
| Xposed Module Auto Builder | ✅   | Will use [New Xposed Module Config Plan](https://github.com/fankes/YukiHookAPI/issues/49) on `YukiHookAPI` 2.x.x |
| ART Dynamic Method Hook    | ✅   | Stable use in multiple scenarios                                                                                 |
| Xposed Resources Hook      | ❗   | Supported, but will be deprecated on `YukiHookAPI` 2.x.x                                                         |

> Extended Functions

| Name                                                                                               | ST  | Description                                                                                          |
| -------------------------------------------------------------------------------------------------- | --- | ---------------------------------------------------------------------------------------------------- |
| [Reflection Extensions](../api/special-features/reflection)                                        | ⭕   | Will be merge into [YukiReflection](https://github.com/fankes/YukiReflection) on `YukiHookAPI` 2.x.x |
| [Xposed Module Data Storage](../api/special-features/xposed-storage)                               | ✅   | Normal use                                                                                           |
| [Xposed Module and Host Channel](../api/special-features/xposed-channel)                           | ✅   | Normal use                                                                                           |
| [Host Lifecycle Extension](../api/special-features/host-lifecycle)                                 | ✅   | Normal use                                                                                           |
| [Inject Module Apps Resources](../api/special-features/host-inject#inject-module-apps-resources)   | ✅   | Normal use                                                                                           |
| [Register Module Apps Activity](../api/special-features/host-inject#register-module-apps-activity) | ✅   | Normal use                                                                                           |

> Xposed Frameworks

| Name                                                 | ST  | Description                                                                 |
| ---------------------------------------------------- | --- | --------------------------------------------------------------------------- |
| [LSPosed](https://github.com/LSPosed/LSPosed)        | ✅   | Stable use in multiple scenarios                                            |
| [LSPatch](https://github.com/LSPosed/LSPatch)        | ⭕   | Support, API support will be gradually added after the project is completed |
| [EdXposed](https://github.com/ElderDrivers/EdXposed) | ❎   | Maintenance has stopped and is no longer recommended                        |
| [Dreamland](https://github.com/canyie/Dreamland)     | ⭕   | Theoretical support (not tested by developer)                               |
| [TaiChi](https://github.com/taichi-framework/TaiChi) | ⭕   | Hook functions normally (some functions have restrictions)                  |
| [Xposed](https://github.com/rovo89/Xposed)           | ❎   | Maintenance has stopped and is no longer recommended                        |

> Hook Frameworks

| Name                                                      | ST  | Description                                                                                  |
| --------------------------------------------------------- | --- | -------------------------------------------------------------------------------------------- |
| [Pine](https://github.com/canyie/pine)                    | ⭕   | Theoretical support (not tested by developer)                                                |
| [SandHook](https://github.com/asLody/SandHook)            | ❎   | The latests Android are not supported, you need to integrated the Rovo89 Xposed API yourself |
| [Whale](https://github.com/asLody/whale)                  | ❎   | The latests Android are not supported, you need to integrated the Rovo89 Xposed API yourself |
| [YAHFA](https://github.com/PAGalaxyLab/YAHFA)             | ❎   | The latests Android are not supported, you need to integrated the Rovo89 Xposed API yourself |
| [FastHook](https://github.com/turing-technician/FastHook) | ❎   | Maintenance has stopped and is no longer recommended                                         |
| [Epic](https://github.com/tiann/epic)                     | ❎   | Maintenance has stopped and is no longer recommended                                         |

> Hook APIs

| Name                                              | ST  | Description                              |
| ------------------------------------------------- | --- | ---------------------------------------- |
| [Rovo89 Xposed API](https://api.xposed.info/)     | ✅   | Stable use in multiple scenarios         |
| [Modern Xposed API](https://github.com/libxposed) | ❎   | Will be supported on `YukiHookAPI` 2.x.x |