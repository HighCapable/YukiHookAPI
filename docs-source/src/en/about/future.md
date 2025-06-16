# Looking for Future

> The future is bright and uncertain, let us look forward to the future development space of `YukiHookAPI`.

## Unresolved Issues

> Here are the unresolved issues with `YukiHookAPI`.

### YukiHookPrefsBridge

Currently only supports LSPosed perfectly, other Xposed Framework need to downgrade the module target api.

TaiChi may not be supported at all, and TaiChi needs a lower target api to adapt on high-version systems.

Some Xposed Module developers currently choose the Hook target app self's SharedPreferences storage solution to solve the module settings sharing problem.

In the later period, the permissions of the Android system will become more and more strict, and `selinux` is a big problem currently facing, which needs to be discussed and studied.

::: tip Updated on 2023.10.06

LSPosed has now experimentally launched [Modern Xposed API](https://github.com/libxposed), which uses Service to communicate with modules, which will solve the problem of module data storage.

In order to ensure the compatibility of most modules, **YukiHookAPI** plans to use a customized ContentProvider to realize data exchange between the Module App and the Host App in the future, so stay tuned.

:::

## Future Plans

> Features that `YukiHookAPI` may add later are included here.

### Lite Version Supported for Standalone Use

If you like the Reflection API of `YukiHookAPI`, but your project may not need related Hook functions.

Well here is some good news for you:

~~The core Reflection API of `YukiHookAPI` has been decoupled into [YukiReflection](https://github.com/HighCapable/YukiReflection) project, which can now be used in any Android project.~~

The `YukiReflection` project has been deprecated due to many unsolved black box issues, so we no longer recommend anyone to use it.
Please now migrate to the brand new design [KavaRef](https://github.com/HighCapable/KavaRef).

::: tip To be Discussed

At present, the API only supports binding to **xposed_init** through the automatic builder.

If you don't like the automatic builder, you must implement the module loading entry yourself.

In the future, the Lite version with only API functions will be launched according to the number of people required.

You can submit **issues** with us.

:::

We have provided the Xposed native API listening interface, you can find or view the implementation method of the Demo [here](../config/xposed-using#native-xposed-api-events).

### Milestone Plan

The plans below have been published in `issues` on GitHub, and you can view the progress of each project.

All functions are expected to be completed in `2.0.0` version, so stay tuned.

- [New Xposed Module Config Plan](https://github.com/HighCapable/YukiHookAPI/issues/49)
- [New Hook Entry Class](https://github.com/HighCapable/YukiHookAPI/issues/48)
- ~~[New Hook Code Style](https://github.com/HighCapable/YukiHookAPI/issues/33)~~ (Replaced by [KavaRef](https://github.com/HighCapable/KavaRef))