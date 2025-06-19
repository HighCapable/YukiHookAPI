# 迁移至 YukiHookAPI 1.3.x

`YukiHookAPI` 从 `1.3.0` 版本开始弃用了自身的反射 API，你可以继续向下阅读以查看有哪些注意事项和新功能。

::: warning

如果你正在使用 `1.2.x` 及之前版本的 `YukiHookAPI`，建议先阅读 [迁移至 YukiHookAPI 1.2.x](move-to-api-1-2-x) 而不是此文档。

:::

## 自身反射 API 弃用

`YukiHookAPI` 从 `1.3.0` 版本开始弃用了自身的反射 API，现在我们推荐所有开发者迁移至全新开发的
[KavaRef](https://github.com/HighCapable/KavaRef)，我们不再推荐使用 `YukiHookAPI` 自身的反射 API，这些 API 已被标记为弃用。

请参考 [这里](https://highcapable.github.io/KavaRef/zh-cn/config/migration) 的迁移文档，这将跳转到 `KavaRef` 的文档。

`YukiHookAPI` 目前已经实现了反射 API 的完全解耦合，其内部 API 使用的反射 API 同样迁移至了 `KavaRef`，且已经稳定测试通过。

在后期的 `2.0.0` 版本中，自身反射 API 将被完全移除，在此期间，你将有足够的时间来学习和迁移至这套全新的反射 API。

## FreeReflection 弃用

`YukiHookAPI` 从 `1.3.0` 版本开始弃用了 [FreeReflection](https://github.com/tiann/FreeReflection) 并迁移至由 LSPosed 团队维护的
[AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass)。

在反射系统隐藏 API 时，你不可以像之前那样直接进行反射，而是需要进行一些操作。

`YukiHookAPI` 内置了 `KavaRef` 的 `第三方 Member 解析器` 中的 `AndroidHiddenApiBypassResolver`，现在你可以在需要反射系统隐藏 API 的地方这样去使用它。

> 示例如下

```kotlin
"android.app.ActivityThread".toClass()
    .resolve()
    // 添加自定义 Member 解析器
    .processor(AndroidHiddenApiBypassResolver.get())
    .firstMethod {
        name = "currentActivityThread"
        emptyParameters()
    }.invoke()
```

::: warning

`AndroidHiddenApiBypassResolver` 是暂定的功能，可能会在 `2.0.0` 版本迁移至单独的模块中，你也可以参考
[第三方 Member 解析器](https://highcapable.github.io/KavaRef/zh-cn/config/processor-resolvers) 自己实现一份，这将跳转到 `KavaRef` 的文档。

:::

## 方法原始调用

`Xposed` 提供了 `XposedBridge.invokeOriginalMethod` 功能，可以调用未经 Hook 的原始方法。

由于自身反射 API 的弃用，`method { ... }.get().original().call(...)` 的方式将不再可用。

所以，`YukiHookAPI` 为 `KavaRef` 添加了扩展功能，现在你依然可以实现这个功能。

`YukiHookAPI` 提供了以下方法来对接 `KavaRef` 的原始方法调用。

- `invokeOriginal(...)` → `invoke(...)`
- `invokeOriginalQuietly(...)` → `invokeQuietly(...)`

> 示例如下

```kotlin
// 假设这就是 Test 类的实例
val instance: Any
// 使用 KavaRef 的方法原始调用
"com.example.Test".toClass()
    .resolve()
    .firstMethod {
        name = "test"
        emptyParameters()
    }.of(instance).invokeOriginal()
```

## 重复 Hook 限制弃用

`YukiHookAPI` 从 `1.3.0` 版本开始弃用了重复 Hook 的限制，现在，`YukiHookAPI` 不再限制重复 Hook 同一个方法，你可以在同一个方法上多次 Hook。

`YukiHookAPI` 同时弃用了 `hook { ... }` 的 `onAlreadyHooked` 方法，现在此方法将无作用且不会被回调，如有需要，请手动处理重复 Hook 的相关逻辑。

## 注册模块 Activity 行为变更

`YukiHookAPI` 从 `1.3.0` 版本开始，注册模块 `Activity` 行为的方式发生了变更。

请阅读 [注册模块 Activity](../api/special-features/host-inject#注册模块-activity) 以了解更多信息。

## YLog 行为变更

`YukiHookAPI` 从 `1.3.0` 版本开始允许 `YLog` 的 `msg` 参数传入任意对象，它们都会自动使用 `toString()` 方法进行转换。