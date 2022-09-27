# Looking for Future

> The future is bright and uncertain, let us look forward to the future development space of `YukiHookAPI`.

## Unresolved Issues

> Here are the unresolved issues with `YukiHookAPI`.

### YukiHookModulePrefs

Currently only supports LSPosed perfectly, other Xposed Framework need to downgrade the module target api.

TaiChi may not be supported at all, and TaiChi needs a lower target api to adapt on high-version systems.

Some Xposed Module developers currently choose the Hook target app self's SharedPreferences storage solution to solve the module settings sharing problem.

In the later period, the permissions of the Android system will become more and more strict, and `selinux` is a big problem currently facing, which needs to be discussed and studied.

## Future Plans

> Features that `YukiHookAPI` may add later are included here.

### Lite Version Supported for Standalone Use

::: tip To be Discussed

At present, the API only supports binding to **xposed_init** through the automatic handler.

If you don't like the automatic handler, you must implement the module loading entry yourself.

In the future, the Lite version with only API functions will be launched according to the number of people required.

You can submit **issues** with us.

:::

We have provided the Xposed native API listening interface, you can find or view the implementation method of the Demo [here](../config/xposed-using#native-xposed-api-events).

### Support for More Hook Framework

As an API, currently only docking `XposedBridge` as a compatibility layer still has certain limitations.

Most `inline hook` do not have a `Java` compatibility layer, and the `Java` compatibility layer adaptation of `native hook` may be considered later.