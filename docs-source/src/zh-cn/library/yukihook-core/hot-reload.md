# 模块热重载

> 这是一个在不重启宿主的情况下重新装载模块代码的功能。

YukiHook 为支持热重载的底座提供了完整的模块生命周期支持，在 YukiHook 中，你基本不需要自行处理新旧代际的交接工作，你的 Hook 业务代码能无缝衔接至新代运行。

::: danger

此功能需要目标 Hook 框架支持的 Xposed API 版本至少为 `102`，且需要 [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) 底座支持。

:::

## 语义版本

为保护热重载后数据结构的完整性，YukiHook 定义了自身语义版本（`schemaVersion`），它用于标识 API 的内部语义版本。

你可以通过 `YukiHook.SCHEMA_VERSION` 获取当前的语义版本，下方的表格记录了每次语义版本的对应版本，如果没有额外说明，则表示对应版本更新后语义版本未发生变化。

| 版本  | 对应版本 | 发布说明 |
| :---: | :------: | -------- |
|   1   |  1.5.0   | 首次发布 |

## 启用热重载

你需要使用 `YukiHookLibXposedEntry` 注解标记模块入口，热重载功能根据 [入口注解](../yukihook-runtime-libxposed.md#入口注解) 的策略决定其是否启用，然后在注解中添加 `hotReload` 参数，并将 `targetApiVersion` 设置为 `102` 及以上。

> 示例如下

```kotlin
@YukiHookLibXposedEntry(
    targetApiVersion = 102,
    // 启用热重载策略，参考下方的策略说明
    hotReload = YukiHookLibXposedEntry.HotReload.MANUAL
)
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        // Your code here.
    }
}
```

| 策略     | 说明                                                    |
| -------- | ------------------------------------------------------- |
| `NONE`   | 默认策略，不接受热重载请求                              |
| `MANUAL` | 仅接受手动发起的热重载请求                              |
| `AUTO`   | 接受手动发起的热重载请求，并在模块 APK 更新后自动热重载 |

热重载会移除旧的 Hook 和事件，然后使用新的模块实例重放 `onInit` 与 `onHook` 事件，已经完成的 [宿主生命周期](./host-lifecycle.md) 不会重放。

如果你需要在宿主的 `Application` 可用时执行初始化，请参考 [模块入口 → 处理作用域](./hook-entry.md#处理作用域) 中的 `withApplicationReady`。

::: danger

由于底座的限制，启用热重载时不能在入口注解中声明额外的 `javaEntries`，你可以参考 [框架事件](../yukihook-runtime-libxposed.md#框架事件) 来处理框架原生模块的额外入口逻辑。

模块自身创建的线程、Native Hook 以及注册在宿主中的外部回调需要手动进行释放，你可以参考下方的 [模块生命周期](#模块生命周期) 中的 `onDispose` 回调。

当通过 [模块资源注入](./resource-injection.md) 启动的 `Activity` 正在宿主中运行、注入的资源释放失败，或 YukiHook 管理的回调正在执行时，模块会拒绝热重载请求。

如果热重载中，进程在主线程上超过 15 秒没有响应，将会自动拒绝此次热重载请求。

在首次启用热重载功能后，安装模块 APK 后必须重启一次宿主，下一次热重载请求才能生效。

在 YukiHook 依赖版本更新后，模块依然可以正常热重载，如果本次更新同时更新了 [语义版本](#语义版本)，则必须重启宿主才能生效。

:::

## 发起热重载

在模块中，你可以使用 `YukiHook.Module.reload` 查询正在运行的宿主并发起热重载请求。

> 示例如下

```kotlin
val reload = YukiHook.Module.reload
if (reload.isAvailable) {
    // 查询可热重载的宿主进程
    reload.targets.forEach { target ->
        // 进程名、UID、PID 与当前状态
        val processName = target.processName
        val state = target.state
        // 发起热重载请求
        reload.request(target) { result ->
            // 回调运行在主线程
            when (result.status) {
                ModuleReloadStatus.SUCCEEDED -> {
                    // 热重载成功
                }
                ModuleReloadStatus.SKIPPED -> {
                    // 宿主已经在运行最新的代码
                }
                else -> YLog.warn(result.message)
            }
        }
    }
    // 你也可以直接通过进程名发起请求
    // 通常情况下，宿主的主进程名即包名
    reload.request("io.github.myapp") { result ->
        // 没有找到唯一匹配的进程时 result 为 null
    }
}
```

默认情况下，宿主已经在运行最新代码时将跳过请求并返回 `SKIPPED`，你可以设置 `skippedIfUpToDate = false` 强制重新发起请求。

::: tip

你还可以在请求中传入 `extras`，但是它只能包含基本类型、字符串、数组与嵌套的 `Bundle`，不能包含模块中定义的对象 (依赖特定 `ClassLoader` 加载的类)。

:::

## 模块生命周期

你可以在 `onInit` 中使用 `registerModuleLifecycle` 注册模块生命周期回调来处理模块持有需要在热重载时释放或交接的资源。

> 示例如下

```kotlin
registerModuleLifecycle {
    // 判断旧代是否可以退出，返回 false 将拒绝此次请求
    canReload { extras ->
        true
    }
    // 在重载之前保存状态，新代将在 `onHook` 之前收到它
    onSaveState {
        bundleOf("count" to count)
    }
    // 停止并释放旧代持有的资源
    onDispose {
        // 假设你有一个管理 Native Hook 的对象
        nativeHookManager.release()
    }
    // 在新代的 `onInit` 之后、`onHook` 之前恢复状态
    onRestoreState { state ->
        count = state?.getInt("count") ?: 0
    }
    // 新代完全装载后回调
    onReloaded { extras ->
        // Your code here.
    }
}
```

::: danger

`registerModuleLifecycle` 只能在 `onInit` 中注册，且只能注册一次。

保存与恢复的状态中不能包含模块中定义的对象 (依赖特定 `ClassLoader` 加载的类)。

回调运行在 Hook 框架选择的线程中，UI 相关的操作请切换到主线程。

:::

## 保留数据

热重载过程将完整重放 `onHook`，你可以使用 `remember` 在模块新旧代际之间保留成本较高的计算结果。

目前成本最高的是反射查找的成员对象，推荐优先对它们进行缓存。

> 示例如下

```kotlin
loadApp("io.github.myapp") {
    val method = remember("myapp-obfuscated-a-v1") {
        val obfuscated = hostClassLoader.loadClass("io.github.myapp.Obfuscated")
        obfuscated.declaredMethods.singleOrNull {
            it.name == "a"
        }
    }

    method?.hook {
        // Your code here.
    }
}
```

相同的 `key` 与声明的返回值类型将直接复用之前保存的结果，不会再次执行方法体。未启用热重载时，方法体在每次调用时都会执行。

当计算方式发生变化而返回值类型不变时，请修改 `key` 以在新代中重新执行方法体缓存新的结果。

::: danger

`remember` 只能在声明 Hook 时调用，例如 `onHook` 或 `loadApp` 中，它不能在 Hook 回调、生命周期回调或其他线程中调用。

`key` 在当前包名的作用域中必须唯一且不能为空。

保留的值不能包含模块中定义的对象 (依赖特定 `ClassLoader` 加载的类)，包括模块打包的依赖，例如 Kotlin 标准库中的 `Pair`、`Unit` 以及函数类型。系统 `ClassLoader` 装载的类例如 `android.*` 包名、宿主以及 `compileOnly` 依赖中的 `Class` 可以被保留。

集合与 `Map` 会被冻结为只读快照，请将其声明为只读的 `List`、`Set`、`Map`、`Collection` 或 `Iterable` 类型。数组、`Bundle` 以及 `WeakReference` 等引用容器将会被拒绝。

:::