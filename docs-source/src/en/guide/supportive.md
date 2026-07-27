# Supportive

The following are the related functions, Xposed Frameworks, Hook Frameworks and Hook APIs supported by YukiHookAPI.

> Basic Functions

| Name                       | Availability | Description                                                                                                             |
| -------------------------- | :----------: | ----------------------------------------------------------------------------------------------------------------------- |
| Xposed Module Auto Builder | ✅   | Will use [New Xposed Module Config Plan](https://github.com/HighCapable/YukiHookAPI/issues/49) on YukiHookAPI `2.0.0` |
| ART Dynamic Method Hook    | ✅   | Stable use in multiple scenarios                                                                                        |
| Xposed Resources Hook      | ❗   | Supported, but will be removed on YukiHookAPI `2.0.0`                                                                 |

> Extended Functions

| Name                                                                                               | Availability | Description                                                                                                                                       |
| -------------------------------------------------------------------------------------------------- | :----------: | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| ~~[Reflection Extensions](../special-features/reflection)~~                                    | ❗   | Completely deprecated, recommended to migrate to [KavaRef](https://github.com/HighCapable/KavaRef), planned YukiHookAPI `2.0.0` version removed |
| [Xposed Module Data Storage](../special-features/xposed-storage)                               | ✅   | Normal use                                                                                                                                        |
| [Xposed Module and Host Channel](../special-features/xposed-channel)                           | ✅   | Normal use                                                                                                                                        |
| [Host Lifecycle Extension](../special-features/host-lifecycle)                                 | ✅   | Normal use                                                                                                                                        |
| [Inject Module Apps Resources](../special-features/host-inject#inject-module-apps-resources)   | ✅   | Normal use                                                                                                                                        |
| [Register Module Apps Activity](../special-features/host-inject#register-module-apps-activity) | ✅   | Normal use                                                                                                                                        |

> Xposed Frameworks

| Name                                                 | Availability | Description                                                                 |
| ---------------------------------------------------- | :----------: | --------------------------------------------------------------------------- |
| [LSPosed](https://github.com/LSPosed/LSPosed)        | ✅   | Stable use in multiple scenarios                                            |
| [LSPatch](https://github.com/LSPosed/LSPatch)        | ⭕   | Support, API support will be gradually added after the project is completed |
| [EdXposed](https://github.com/ElderDrivers/EdXposed) | ❎   | Maintenance has stopped and is no longer recommended                        |
| [Dreamland](https://github.com/canyie/Dreamland)     | ⭕   | Theoretical support (not tested by developer)                               |
| [TaiChi](https://github.com/taichi-framework/TaiChi) | ⭕   | Hook functions normally (some functions have restrictions)                  |
| [Xposed](https://github.com/rovo89/Xposed)           | ❎   | Maintenance has stopped and is no longer recommended                        |

> Hook Frameworks

| Name                                                      | Availability | Description                                                                                  |
| --------------------------------------------------------- | :----------: | -------------------------------------------------------------------------------------------- |
| [LSPlant](https://github.com/LSPosed/LSPlant)             | ⭕   | Please visit [AliuHook](https://github.com/Aliucord/hook)                                    |
| [Pine](https://github.com/canyie/pine)                    | ⭕   | Theoretical support (not tested by developer)                                                |
| [SandHook](https://github.com/asLody/SandHook)            | ❎   | The latests Android are not supported, you need to integrated the Rovo89 Xposed API yourself |
| [Whale](https://github.com/asLody/whale)                  | ❎   | The latests Android are not supported, you need to integrated the Rovo89 Xposed API yourself |
| [YAHFA](https://github.com/PAGalaxyLab/YAHFA)             | ❎   | The latests Android are not supported, you need to integrated the Rovo89 Xposed API yourself |
| [FastHook](https://github.com/turing-technician/FastHook) | ❎   | Maintenance has stopped and is no longer recommended                                         |
| [Epic](https://github.com/tiann/epic)                     | ❎   | Maintenance has stopped and is no longer recommended                                         |

> Hook APIs

| Name                                              | Availability | Description                                |
| ------------------------------------------------- | :----------: | ------------------------------------------ |
| [Rovo89 Xposed API](https://api.xposed.info/)     | ✅   | Stable use in multiple scenarios           |
| [Modern Xposed API](https://github.com/libxposed) | ❎   | Will be supported on YukiHookAPI `2.0.0` |