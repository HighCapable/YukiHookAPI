---
pageClass: hidden-anchor-page
---

# API 异常处理

> 异常是在开发过程经常遇到的主要问题，这里介绍了 `YukiHookAPI` 在使用过程中可能遇到的常见异常以及处理方式。

这里的异常说明只会同步最新的 API 版本，较旧的 API 版本的异常将不会再进行说明，请始终保持 API 版本为最新。

## 非阻断异常

> 这些异常不会导致 APP 停止运行 (FC)，但是会在控制台打印 `E` 级别的日志，也可能会停止继续执行相关功能。

###### exception

::: danger loggerE

Could not found any available Hook APIs in current environment! Aborted

:::

**异常原因**

你的 Hook Framework 未在工作或并未成功装载当前 Hook API。

**解决方案**

请确认你在正确的地方装载了 `YukiHookAPI` 的 `encase` 方法，详情请参考 [作为 Xposed 模块使用的相关配置](../config/xposed-using) 以及 [作为 Hook API 使用的相关配置](../config/api-using)。

###### exception

::: danger loggerE

You cannot load a hooker in "onInit" or "onXposedEvent" method! Aborted

:::

**异常原因**

你尝试在继承 `IYukiHookXposedInit` 的 Hook 入口类的 `onInit` 或 `onXposedEvent` 方法中装载了 `encase` 方法。

> 示例如下

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onInit() {
        // 错误的使用方法
        YukiHookAPI.encase {
            // Your code here.
        }
    }

    override fun onXposedEvent() {
        // 错误的使用方法
        YukiHookAPI.encase {
            // Your code here.
        }
    }

    override fun onHook() {
        // Your code here.
    }
}
```

**解决方案**

请在 `onHook` 方法中装载 `encase` 方法。

> 示例如下

```kotlin
object HookEntry : IYukiHookXposedInit {

    override fun onInit() {
        // 这里只能装载 configs 方法
        YukiHookAPI.configs {
            // Your code here.
        }
    }

    override fun onHook() {
        // ✅ 正确的使用方法
        YukiHookAPI.encase {
            // Your code here.
        }
    }
}
```

###### exception

::: danger loggerE

An exception occurred in the Hooking Process of YukiHookAPI

:::

**异常原因**

`YukiHookAPI` 在装载 Xposed 入口方法时发生异常。

**解决方案**

这是一个异常汇总，如果你当前的 Hook 进程中发生了任何异常 (Hook 进程崩溃)，都会使用此方式打印到控制台，请追溯异常发生的堆栈以定位你的代码问题位置。

###### exception

::: danger loggerE

An exception occurred when hooking internal function

:::

**异常原因**

`YukiHookAPI` 在进行自身初始化 Hook 过程中发生异常。

**解决方案**

通常情况下这种错误不会轻易发生，若一旦发生此错误，可直接提交日志进行反馈。

###### exception

::: danger loggerE

YukiHookAPI try to load hook entry class failed

:::

**异常原因**

`YukiHookAPI` 在尝试装载 Hook 入口类 `onInit` 或 `onHook` 方法时发生了不能处理的异常或找不到入口类。

**解决方案**

通常情况下这种错误不会轻易发生，若一旦发生此错误，请自行查看控制台打印的日志定位问题，确定并非自己的代码发生的问题后，可提交日志进行反馈。

###### exception

::: danger loggerE

An exception occurred when YukiHookAPI loading Xposed Module

:::

**异常原因**

`YukiHookAPI` 在尝试使用 Xposed 原生接口装载 Xposed 模块时发生了不能处理的异常。

**解决方案**

通常情况下这种错误不会轻易发生，若一旦发生此错误，请自行查看控制台打印的日志定位问题，确定并非自己的代码发生的问题后，可提交日志进行反馈。

###### exception

::: danger loggerE

Failed to execute method "**NAME**", maybe your Hook Framework not support Resources Hook

:::

**异常原因**

`YukiHookAPI` 在尝试进行 Resources Hook 时发生错误。

**解决方案**

请仔细检查错误日志的详细信息。

若发生 `Resources$NotFoundException` 则可能为你查找的 Resources Id 不正确。

若发生 `ClassNotFound` 或 `NoClassDefFoundError` 可能是 Hook Framework 不支持 Resources Hook(资源钩子)。

###### exception

::: danger loggerE

HookClass \[**NAME**\] not found

:::

**异常原因**

当前被 Hook 的 `Class` 没有被找到。

**解决方案**

请检查目标 `Class` 是否存在，若想忽略此错误请使用 `ignoredHookClassNotFoundFailure` 方法。

###### exception

::: danger loggerE

Hook Member \[**NAME**\] failed

:::

**异常原因**

Hook 目标方法、构造方法时发生错误。

**解决方案**

此问题通常由 Hook Framework 产生，请检查对应的日志内容，若问题持续出现请携带详细日志进行反馈。

###### exception

::: danger loggerE

Hooked Member with a finding error / by **CLASS**

:::

**异常原因**

在 Hook 执行后被 Hook 的 `member` 为 `null` 且已经设置目标 Hook 方法、构造类。

**解决方案**

请检查此错误发生前的上一个错误日志，或许在查找方法、构造方法的时候发生了找不到方法、构造方法的错误。

###### exception

::: danger loggerE

Hooked Member cannot be null / by **CLASS**

:::

**异常原因**

在 Hook 执行后被 Hook 的 `member` 为 `null` 且没有设置目标 Hook 方法、构造类。

> 示例如下

```kotlin
injectMember {
    // 这里并没有设置需要 Hook 的方法、构造方法的查找条件
    after {
        // ...
    }
}
```

**解决方案**

请确认你已经在 Hook 之前正确设置了要 Hook 的方法、构造方法的查找方式。

> 示例如下

```kotlin
injectMember {
    // ✅ 正确的使用方法举例
    method {
        // Your code here.
    }
    after {
        // ...
    }
}
```

###### exception

::: danger loggerE

Hooked method return type match failed, required \[**TYPE**\] but got \[**TYPE**\]

:::

**异常原因**

在 Hook 回调方法体中设置了 `HookParam.result` 或使用了 `replaceHook` 但是被 Hook 的方法返回值类型与原返回值类型不匹配。

> 示例如下

假设这个是被 Hook 的方法。

```java
private boolean test()
```

下面是一个错误的案列。

```kotlin
method {
    name = "test"
    emptyParam()
}.hook {
    // <情景1> 设置了错误的类型，原类型为 Boolean
    before {
        result = 0
    }
    // <情景2> 返回了错误的类型，原类型为 Boolean
    replaceAny {
        0
    }
    // <情景3> 直接使用了错误的类型，原类型为 Boolean
    replaceTo(any = 0)
}
```

::: warning

若上述场景在 **before** 或 **after** 中发生，则会造成被 Hook 的 APP (宿主) 由 **XposedBridge** 抛出异常 (会对其暴露被 Hook 的事实)。

:::

**解决方案**

请确认当前被 Hook 方法的正确返回值类型，修改后再试一次。

###### exception

::: danger loggerE

Hook initialization failed because got an exception

:::

**异常原因**

在准备 Hook 时发生了任意的异常。

**解决方案**

这是一个准备 Hook 阶段就发生异常的提醒，请仔细查看具体的异常是什么以重新确定问题。

###### exception

::: danger loggerE

Try to hook **NAME**\[**NAME**\] got an exception

:::

**异常原因**

在 Hook 开始时发生了任意的异常。

**解决方案**

这是一个 Hook 开始就发生异常的提醒，请仔细查看具体的异常是什么以重新确定问题。

###### exception

::: danger loggerE

Method/Constructor/Field match type "**TYPE**" not allowed

:::

**异常原因**

在查找方法、构造方法以及变量时设置了不允许的参数类型。

> 示例如下

```kotlin
// 查找一个方法
method {
    // 设置了无效的类型举例
    param(false, 1, 0)
    // 设置了无效的类型举例
    returnType = false
}

// 查找一个变量
field {
    // 设置了无效的类型举例
    type = false
}
```

**解决方案**

在查找中 `param`、`returnType`、`type` 中仅接受 `Class`、`String`、`VariousClass` 类型的传值，不可传入参数实例。

> 示例如下

```kotlin
// 查找一个方法
method {
    // ✅ 正确的使用方法举例
    param(BooleanType, IntType, IntType)
    // ✅ 正确的使用方法举例
    returnType = BooleanType
    // ✅ 以下方案也是正确的
    returnType = "java.lang.Boolean"
}

// 查找一个变量
field {
    // ✅ 正确的使用方法举例
    type = BooleanType
}
```

###### exception

::: danger loggerE

NoSuchMethod/NoSuchConstructor/NoSuchField happend in \[**NAME**\]

:::

**异常原因**

在查找方法、构造方法以及变量时并未找到目标方法、构造方法以及变量。

**解决方案**

请确认你的查找条件是否能正确匹配到目标 `Class` 中的指定方法、构造方法以及变量。

###### exception

::: danger loggerE

Trying **COUNT** times and all failure by RemedyPlan

:::

**异常原因**

使用 `RemedyPlan` 重新查找方法、构造方法、变量时依然没有找到方法、构造方法、变量。

**解决方案**

请确认你设置的 `RemedyPlan` 参数以及宿主内存在的 `Class`，再试一次。

###### exception

::: danger loggerE

Can't find this Class in \[**CLASSLOADER**\]: **CONTENT** Generated by YukiHookAPI#ReflectionTool

:::

**异常原因**

通过 `ClassLoader.searchClass` 或 `PackageParam.searchClass` 找不到需要查找的 `Class` 对象。

> 示例如下

```kotlin
customClassLoader?.searchClass {
    from(...)
    // ...
}.get()
```

**解决方案**

这是一个安全异常，请检查你设置的条件，使用相关工具查看所在 **Dex** 中的 `Class` 以及字节码对象特征，并再试一次。

###### exception

::: danger loggerE

Can't find this Method/Constructor/Field in \[**CLASS**\]: **CONTENT** Generated by YukiHookAPI#ReflectionTool

:::

**异常原因**

通过指定条件找不到需要查找的方法、构造方法以及变量。

> 示例如下

```kotlin
TargetClass.method {
    name = "test"
    param(BooleanType)
}
```

**解决方案**

这是一个安全异常，请检查你设置的条件，使用相关工具查看所在 `Class` 中的字节码对象特征，并再试一次。

###### exception

::: danger loggerE

The number of VagueType must be at least less than the count of paramTypes

:::

**异常原因**

在 `Method`、`Constructor` 查找条件中错误地使用了 `VagueType`。

> 示例如下

```kotlin
TargetClass.method {
    name = "test"
    // <情景1>
    param(VagueType)
    // <情景2>
    param(VagueType, VagueType ...)
}
```

**解决方案**

`VagueType` 不能在方法、构造方法参数中完全填充，若存在这样的需求请使用 `paramCount`。

###### exception

::: danger loggerE

Field match type class is not found

:::

**异常原因**

在查找变量时所设置的查找条件中 `type` 的 `Class` 实例未被找到。

> 示例如下

```kotlin
field {
    name = "test"
    // 假设这里设置的 type 的 Class 并不存在
    type = "com.example.TestClass"
}
```

**解决方案**

请检查查找条件中 `type` 的 `Class` 是否存在，然后再试一次。

###### exception

::: danger loggerE

Method match returnType class is not found

:::

**异常原因**

在查找方法时所设置的查找条件中 `returnType` 的 `Class` 实例未被找到。

> 示例如下

```kotlin
method {
    name = "test"
    // 假设这里设置的 returnType 的 Class 并不存在
    returnType = "com.example.TestClass"
}
```

**解决方案**

请检查查找条件中 `returnType` 的 `Class` 是否存在，然后再试一次。

###### exception

::: danger loggerE

Method/Constructor match paramType\[**INDEX**\] class is not found

:::

**异常原因**

在查找方法、构造方法时所设置的查找条件中 `param` 的 `index` 号下标的 `Class` 实例未被找到。

```kotlin
method {
    name = "test"
    // 假设这里设置的 1 号下标的 Class 并不存在
    param(StringClass, "com.example.TestClass", BooleanType)
}
```

**解决方案**

请检查查找条件中 `param` 的 `index` 号下标的 `Class` 是否存在，然后再试一次。

###### exception

::: danger loggerE

Invoke original Member \[**MEMBER**\] failed

:::

**异常原因**

在使用 `HookParam.callOriginal`、`HookParam.invokeOriginal`、`method { ... }.get(...).original()` 调用未经 Hook 的原始方法时发生错误。

**解决方案**

一般情况下，此错误基本上不会发生，若发生此错误，可能为当前使用的 Hook Framework 问题，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

Failed to got Host Resources

:::

**异常原因**

在查找 Resources 时使用 `replaceTo { ... }` 或 `replaceToModuleResource { ... }` 时未能获取到宿主的原始 Resources 实例对象。

> 示例如下

```kotlin
conditions {
    name = "test"
    string()
}
replaceTo { "${it}_some_text" }
```

**解决方案**

一般情况下，此错误基本上不会发生，若发生此错误，可能为当前使用的 Hook Framework 问题，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

Resources Hook condition name/type cannot be empty \[**TAG**\]

:::

**异常原因**

在查找 Resources 时并未设置任何条件。

> 示例如下

```kotlin
// 情况 1
conditions {
    // 这里没有设置任何条件
}
// 情况 2
conditions {
    name = "test"
    // 这里缺少了 type 条件
}
```

**解决方案**

Resources 的 Hook 并非类似方法的 Hook，其必须拥有完整的名称和类型描述才能查找成功，请将查找条件补充完整并再试一次。

###### exception

::: danger loggerE

Resources Hook type is invalid \[**TAG**\]

:::

**异常原因**

在 Hook Resources 时发生了类型错误的异常。

**解决方案**

`YukiHookAPI` 会尝试在 `initZygote` 与 `handleInitPackageResources` 中装载 Resources Hook，若全部装载失败可能会发生此异常，当前 Hook Framework 需要支持并启用资源钩子 (Resources Hook) 功能，请检查后再试一次。

###### exception

::: danger loggerE

Resources Hook got an exception \[**TAG**\]

:::

**异常原因**

在 Hook Resources 时发生了任意的异常。

**解决方案**

这是一个异常汇总，请自行向下查看日志具体的异常是什么，例如找不到 Resources Id 的问题。

###### exception

::: danger loggerE

Received action "**ACTION**" failed

:::

**异常原因**

使用 `YukiHookDataChannel` 时回调广播事件异常。

**解决方案**

此异常多为一些关联性异常引发，请检查自身代码是否存在问题，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

Received data type **TYPE** is not a vailed YukiHookDataChannel's data

:::

**异常原因**

使用 `YukiHookDataChannel` 时回调广播收到了不属于 `YukiHookDataChannel` 的数据。

**解决方案**

为了确保数据安全性，`YukiHookDataChannel` 会对发送的数据进行包装，任何第三方广播事件均不能被 `YukiHookDataChannel` 接收，请检查你的代码是否正确。

###### exception

::: danger loggerE

Unsupported segments data key of \"**KEY**\"'s type

:::

**异常原因**

使用 `YukiHookDataChannel` 时回调广播收到了不支持的分段数据类型。

**解决方案**

一般情况下，此错误不可能发生，因为 `YukiHookDataChannel` 所支持的分段数据类型是固定的，不会动态改变，若发生这种情况，请检查是否改动了 API 相关代码。

###### exception

::: danger loggerE

YukiHookDataChannel cannot merge this segments data key of \"**KEY**\"

:::

**异常原因**

使用 `YukiHookDataChannel` 时回调广播收到了无法处理的分段数据导致无法合并分段数据。

**解决方案**

一般情况下，此错误基本上不会发生，除非收到连续发送或重复发送的广播 (时序异常) 或在接收数据时设置了错误的泛型类型，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

YukiHookDataChannel cannot calculate the byte size of the data key of \"**KEY**\" to be sent, so this data cannot be sent

If you want to lift this restriction, use the allowSendTooLargeData function when calling, but this may cause the app crash

:::

**异常原因**

使用 `YukiHookDataChannel` 发送广播数据时计算数据大小失败。

**解决方案**

一般情况下，此错误基本上不会发生，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

YukiHookDataChannel cannot send this data key of \"**KEY**\" type **TYPE**, because it is too large (total **TOTAL** KB, limit **LIMIT** KB) and cannot be segmented

**SUGGESTION_MESSAGE**

If you want to lift this restriction, use the allowSendTooLargeData function when calling, but this may cause the app crash

:::

**异常原因**

使用 `YukiHookDataChannel` 发送广播数据时数据过大，但是此数据类型并不支持被分段发送。

**解决方案**

当你发送的数据超出系统广播的上限时，`YukiHookDataChannel` 默认情况下会将此数据分段后依次发送，但仅支持处理 **List**、**Map**、**Set**、**String** 常见类型的自动分段功能。

::: tip

若你仍要使用此功能，请参考 [YukiHookDataChannel.NameSpace.allowSendTooLargeData](../api/public/com/highcapable/yukihookapi/hook/xposed/channel/YukiHookDataChannel#allowsendtoolargedata-method) 方法。

但是<u>**强烈建议不要这样做**</u>，<u>**这有可能会导致系统不允许过大的数据发送而造成应用崩溃**</u>。

:::

###### exception

::: danger loggerE

Failed to sendBroadcast like "**KEY**", because got null context in "**PACKAGENAME**"

:::

**异常原因**

使用 `YukiHookDataChannel` 时发送广播取到了空的上下文实例。

**解决方案**

一般情况下，此错误基本上不会发生，在最新版本中已经修复宿主使用时可能发生的问题，若最新版本依然发生错误，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

Failed to inject module resources into \[**RESOURCES**\]

:::

**异常原因**

在 (Xposed) 宿主环境中使用 `injectModuleAppResources` 注入模块资源时发生异常。

**解决方案**

一般情况下，此错误基本上不会发生，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

You cannot inject module resources into yourself

:::

**异常原因**

在 (Xposed) 宿主环境 (模块自身的 Xposed 环境) 中使用 `injectModuleAppResources` 将模块自身的资源注入自身。

**解决方案**

由于模块自身也可以被自身 Hook，但你并不可以在模块自身注入自己 (不能递归自身的资源)，若你一定要获取模块自身的资源，请直接使用即可，无需任何其它操作。

###### exception

::: danger loggerE

Activity Proxy initialization failed because got an exception

:::

**异常原因**

在 (Xposed) 宿主环境中使用 `registerModuleAppActivities` 注入模块 `Activity` 时发生异常。

**解决方案**

请检查此错误发生后的下一个错误日志，或许在配置参数上可能发生了一些问题，若找不到相关错误日志的说明，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

Activity Proxy got an exception in msg.what \[**WHAT**\]

:::

**异常原因**

在 (Xposed) 宿主环境中使用 `registerModuleAppActivities` 注入模块 `Activity` 时发生异常。

**解决方案**

一般情况下，此错误基本上不会发生，但根据系统版本差异性并未做详细测试，排除自身代码的问题后，请携带详细日志进行反馈。

###### exception

::: danger loggerE

This proxy \[**TYPE**\] type is not allowed

:::

**异常原因**

在 (Xposed) 宿主环境中使用 `registerModuleAppActivities` 注入模块 `Activity` 时填入了无效的参数。

> 示例如下

```kotlin
//  这里填入的内容仅为举例，其中 proxy 填入了不能理解的无效参数
registerModuleAppActivities(proxy = false)
```

**解决方案**

方法中的 `proxy` 参数只接受 `String`、`CharSequence`、`Class` 类型，请查看相关使用方法正确填入方法参数。

###### exception

::: danger loggerE

Cound not got launch intent for package "**NAME**"

:::

**异常原因**

在 (Xposed) 宿主环境中使用 `registerModuleAppActivities` 注入模块 `Activity` 时找不到宿主的启动 `Activity`。

> 示例如下

```kotlin
// 使用了默认参数直接进行注册
registerModuleAppActivities()
```

**解决方案**

默认参数 (无参) 只能用于可被启动的 APP，若 APP 并未声明启动入口 `Activity`，你就需要手动指定方法的 `proxy` 参数。

###### exception

::: danger loggerE

Could not found "**NAME**" or Class is not a type of Activity

:::

**异常原因**

在 (Xposed) 宿主环境中使用 `registerModuleAppActivities` 注入模块 `Activity` 时无法找到被填入参数 `proxy` 的 `Activity`。

> 示例如下

```kotlin
registerModuleAppActivities(proxy = "com.demo.test.TestActivity")
```

**解决方案**

请确认你填入的 `Activity` 名称真实有效地存在于宿主中，且目标 `Class` 继承于 `Activity`。

###### exception

::: danger loggerE

You cannot register Activity Proxy into yourself

:::

**异常原因**

在 (Xposed) 宿主环境 (模块自身的 Xposed 环境) 中使用 `registerModuleAppActivities` 将模块自身 `Activity` 注入自身。

**解决方案**

由于模块自身也可以被自身 Hook，但你并不可以在模块自身注入自己 (不能递归自身的资源)，若你一定要获取模块自身的资源，请直接使用即可，无需任何其它操作。

###### exception

::: danger loggerE

Activity Proxy only support for Android 7.0 (API 24) or higher

:::

**异常原因**

在 (Xposed) 宿主环境 (模块自身的 Xposed 环境) 中使用 `registerModuleAppActivities` 但是当前系统版本不满足 Android 7.0 (API 24) 最低要求。

**解决方案**

Activity Proxy 仅支持高于或等于 Android 7.0 (API 24) 的系统，请尝试升级你的系统或对模块 APP 最低系统版本兼容性做出要求，例如设置 Min API 为 24。

###### exception

::: danger loggerE

An exception occurred during AppLifecycle event

:::

**异常原因**

在 (Xposed) 宿主环境中使用 `onAppLifecycle` 监听宿主生命周期期间发生异常。

**解决方案**

此异常为 `onAppLifecycle` 中抛出，由于你设置了参数 `isOnFailureThrowToApp = false`，异常没有在宿主中被抛出而是在 (Xposed) 宿主环境中进行打印，这不属于 API 的异常，请仔细检查自身代码的问题。

## 阻断异常

> 这些异常会直接导致 APP 停止运行 (FC)，同时会在控制台打印 `E` 级别的日志，还会造成 Hook 进程“死掉”。

###### exception

::: danger IllegalStateException

YukiHookAPI cannot support current Hook API or cannot found any available Hook APIs in current environment

:::

**异常原因**

`YukiHookAPI` 不支持当前环境使用的 Hook API 或不存在 Hook API 可被调用。

**解决方案**

请确认你在正确的地方装载了 `YukiHookAPI` 的 `encase` 方法，详情请参考 [作为 Xposed 模块使用的相关配置](../config/xposed-using) 以及 [作为 Hook API 使用的相关配置](../config/api-using)。

###### exception

::: danger UnsupportedOperationException

!!!DANGEROUS!!! Hook \[**CLASS**\] Class is a dangerous behavior! \[**CONTENT**\] \[**SOLVE**\]

:::

**异常原因**

你尝试 Hook 了处于危险行为列表中的 `Class` 对象，例如 `Class`、`ClassLoader`、`Method`。

> 示例如下

```kotlin
// <情景1>
JavaClassLoader.hook {
    // ...
}
// <情景2>
JavaClass.hook {
    // ...
}
// <情景3>
JavaMethod.hook {
    // ...
}
// ...
```

**解决方案**

这些功能是系统内部的，<u>**它们不应该被 Hook，在部分 Hook Framework 上可能不被支持，还会引发其它错误**</u>，请尝试更换 Hook 点。

::: tip

若你仍要使用此功能，请参考 [YukiMemberHookCreator.useDangerousOperation](../api/public/com/highcapable/yukihookapi/hook/core/YukiMemberHookCreator#usedangerousoperation-method) 方法。

但是**强烈建议不要这样做，发生问题请不要反馈，<u>自行承担一切后果</u>**。

:::

###### exception

::: danger NoClassDefFoundError

Can't find this Class in \[**CLASSLOADER**\]: **CONTENT** Generated by YukiHookAPI#ReflectionTool

:::

**异常原因**

通过 `String.toClass(...)` 或 `classOf<...>()` 找不到需要查找的 `Class` 对象。

> 示例如下

```kotlin
"com.demo.Test".toClass()
```

**解决方案**

请检查当前字符串或实体匹配到的 `Class` 是否存在于当前 `ClassLoader`，并再试一次。

###### exception

::: danger IllegalStateException 

ClassLoader \[**CLASSLOADER**\] is not a DexClassLoader

:::

**异常原因**

使用 `ClassLoader.searchClass` 或 `PackageParam.searchClass` 查找 `Class` 但是当前 `ClassLoader` 并不继承于 `BaseDexClassLoader`。

**解决方案**

这种情况基本不存在，除非当前 APP 引用了非 ART 平台的可执行文件 (但是这种情况还是不会存在) 或当前 `ClassLoader` 为空。

###### exception

::: danger IllegalStateException

Failed to got SystemContext

:::

**异常原因**

在被 Hook 的宿主内调用了 `systemContext` 但并未成功获取到实例对象。

> 示例如下

```kotlin
encase {
    // 调用了此变量
    systemContext...
}
```

**解决方案**

这种情况不应该存在，由于 `systemContext` 通过反射从 `ActivityThread` 中得到，除非系统进程发生异常，否则获取到的对象不会为空。

###### exception

::: danger IllegalStateException 

App is dead, You cannot call to appContext

:::

**异常原因**

> 第一种情况

在被 Hook 的宿主内调用了 `ModuleApplication` 的 `appContext`。

> 示例如下

```kotlin
encase {
    // 调用了此变量
    ModuleApplication.appContext...
}
```

> 第二种情况

使用 `ModuleApplication` 时调用了 `appContext` 但是 APP 可能已经被销毁或没有正确启动。

> 示例如下

```kotlin
// 调用了此变量但是 APP 可能已被销毁或没有正确启动
ModuleApplication.appContext...
```

**解决方案**

> 第一种情况

你只能在模块内使用 `ModuleApplication` 的 `appContext`，在宿主内请使用 `PackageParam` 中的 `appContext`，请确认你使用的是否正确。

> 第二种情况

这种情况基本不存在，由于 `appContext` 是在 `onCreate` 中被赋值的，除非遇到多进程并发启动或 APP 没有启动完成前被反射调用了父类的 `onCreate` 方法。

###### exception

::: danger IllegalStateException 

YukiHookPrefsBridge not allowed in Custom Hook API

:::

**异常原因**

在 Hook 自身 APP(非 Xposed 模块) 中使用了 `YukiHookPrefsBridge`。

> 示例如下

```kotlin
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        YukiHookAPI.encase(base) {
            // 不能在这种情况下使用 prefs
            prefs.getBoolean("test_data")
        }
        super.attachBaseContext(base)
    }
}
```

**解决方案**

你只能在 [作为 Xposed 模块使用](../config/xposed-using) 时使用 `YukiHookPrefsBridge`，在 Hook 自身 APP 中请使用原生的 `Sp` 存储。

###### exception

::: danger IllegalStateException 

Cannot load the XSharedPreferences, maybe is your Hook Framework not support it

:::

**异常原因**

在 (Xposed) 宿主环境使用了 `YukiHookPrefsBridge` 但是无法得到 `XSharedPreferences` 对象。

> 示例如下

```kotlin
encase {
    // 调用了此变量
    prefs...
}
```

**解决方案**

一般情况下不会发生此问题，若持续无法获取 `XSharedPreferences` 对象则可能是你使用的 Hook Framework 不支持此功能或自身存在错误。

###### exception

::: danger IllegalStateException 

YukiHookDataChannel not allowed in Custom Hook API

:::

**异常原因**

在 Hook 自身 APP(非 Xposed 模块) 中使用了 `YukiHookDataChannel`。

> 示例如下

```kotlin
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        YukiHookAPI.encase(base) {
            // 不能在这种情况下使用 dataChannel
            dataChannel.wait(key = "test_data") {
                // ...
            }
        }
        super.attachBaseContext(base)
    }
}
```

**解决方案**

你只能在 [作为 Xposed 模块使用](../config/xposed-using) 时使用 `YukiHookDataChannel`。

###### exception

::: danger IllegalStateException 

Xposed modulePackageName load failed, please reset and rebuild it

:::

**异常原因**

在 Hook 过程中使用 `YukiHookPrefsBridge` 或 `YukiHookDataChannel` 时无法读取装载时的 `modulePackageName` 导致不能确定自身模块的包名。

**解决方案**

请仔细阅读 [这里](../config/xposed-using#modulepackagename-参数) 的帮助文档，正确配置模块的 Hook 入口类包名。

###### exception

::: danger IllegalStateException 

YukiHookPrefsBridge missing Context instance

:::

**异常原因**

在模块中使用了 `YukiHookPrefsBridge` 存储数据但并未传入 `Context` 实例。

> 示例如下

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 错误的使用方法
        // 构造方法已在 API 1.0.88 及以后的版本中设置为 private
        YukiHookPrefsBridge().getBoolean("test_data")
    }
}
```

**解决方案**

在 `Activity` 中推荐使用 `prefs(...)` 方法来装载 `YukiHookPrefsBridge`。

> 示例如下

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ 正确的使用方法
        prefs().getBoolean("test_data")
    }
}
```

###### exception

::: danger IllegalStateException 

The Host App's Context has not yet initialized successfully, the native function cannot be used at this time

:::

**异常原因**

在 (Xposed) 宿主环境 `PackageParam` 中使用了 `YukiHookPrefsBridge` 并调用了 `native` 方法但此时宿主的生命周期并未初始化。

> 示例如下

```kotlin
encase {
    // 调用了此方法
    prefs.native()
}
```

**解决方案**

`native` 方法需要一个存在的 `Context` 对象用于存储数据，你可以在监听宿主生命周期状态中使用此方法。

###### exception

::: danger IllegalStateException 

Key-Value type **TYPE** is not allowed

:::

**异常原因**

在使用 `YukiHookPrefsBridge` 的 `get` 或 `put` 方法或 `YukiHookDataChannel` 的 `wait` 或 `put` 方法时传入了不支持的存储类型。

**解决方案**

`YukiHookPrefsBridge` 支持的类型只有 `String`、`Set<String>`、`Int`、`Float`、`Long`、`Boolean`，请传入支持的类型。

`YukiHookDataChannel` 支持的类型为 `Intent.putExtra` 限制的类型，请传入支持的类型。

###### exception

::: danger IllegalStateException 

loadApp/loadZygote/loadSystem/withProcess method need a "**NAME**" param

:::

**异常原因**

在 `loadApp`、`loadZygote`、`loadSystem`、`withProcess` 中缺少了需要填写的可变数组变量参数。

> 示例如下

```kotlin
// <情景 1>
loadApp()
// <情景 2> 
loadZygote()
// <情景 3>
loadSystem()
// <情景 4>
withProcess()
```

**解决方案**

请查看 `PackageParam` 中的用法正确地使用此功能。

###### exception

::: danger IllegalStateException 

YukiHookDataChannel cannot used in zygote

:::

**异常原因**

在 `loadZygote` 中使用了 `YukiHookDataChannel`。

> 示例如下

```kotlin
loadZygote {
    // 调用了此变量
    dataChannel...
}
```

**解决方案**

`YukiHookDataChannel` 只能在 `loadSystem`、`loadApp` 中使用。

###### exception

::: danger IllegalStateException 

Custom Hooking Members is empty

:::

**异常原因**

在 `MemberHookCreator` 中调用 `members()` 但是未设置需要 Hook 的 `Member` 实例。

> 示例如下

```kotlin
injectMember {
    // 括号里的方法参数被留空了
    members()
    after {
        // ...
    }
}
```

**解决方案**

若要使用 `members()` 设置自定义 Hook 方法，你必须保证其方法参数里的 `Member` 数组对象不能为空。

###### exception

::: danger IllegalStateException 

HookParam Method args index must be >= 0

:::

**异常原因**

在 `HookParam` 中调用 `args().last()` 但是目标 `param` 为空或 `args` 中的 `index` 设置了小于 0 的数值。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 假设 param 是空的
        args().last()...
        // 设置了小于 0 的 index
        args(index = -5)...
    }
}
```

**解决方案**

请确认你 Hook 的目标方法、构造方法的方法参数数量是否不为空，且不能对 `args` 的下标设置小于 0 的数值。

###### exception

::: danger IllegalStateException 

HookParam instance got null! Is this a static member?

:::

**异常原因**

在 `HookParam` 中调用 `instance` 变量或 `instance` 方法但获取不到当前实例的对象。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 调用了此变量
        instance...
        // 调用了此方法
        instance<Any>()...
    }
}
```

**解决方案**

请确认你 Hook 的方法是否为静态类型，静态类型的方法没有实例，不能使用此功能，若非静态方法，请检查实例是否已经销毁。

###### exception

::: danger IllegalStateException 

Current hooked Member args is null

:::

**异常原因**

在 `HookParam` 中调用 `args` 变量但获取不到当前实例方法、构造方法的参数数组。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 调用了此变量
        args...
    }
}
```

**解决方案**

这种问题一般不会发生，真的发生了此问题，请携带详细日志进行反馈。

###### exception

::: danger IllegalStateException 

Current hooked Member is null

:::

**异常原因**

在 `HookParam` 中调用 `member` 变量但获取不到当前实例的方法、构造方法实例。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 调用了此变量
        member...
    }
}
```

**解决方案**

这种问题一般不会发生，真的发生了此问题，请携带详细日志进行反馈。

###### exception

::: danger IllegalStateException 

Current hooked Member is not a Method

:::

**异常原因**

在 `HookParam` 中调用 `method` 变量但获取不到当前实例的方法实例。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 调用了此变量
        method...
    }
}
```

**解决方案**

请确认你 Hook 的方法是构造方法还是普通方法并使用对应类型的方法获取指定的实例，若不知道字节码的类型可以直接使用 `member` 来获取。

###### exception

::: danger IllegalStateException 

Current hooked Member is not a Constructor

:::

**异常原因**

在 `HookParam` 中调用 `constructor` 变量但获取不到当前实例的方法实例。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 调用了此变量
        constructor...
    }
}
```

**解决方案**

请确认你 Hook 的方法是普通方法还是构造方法并使用对应类型的方法获取指定的实例，若不知道字节码的类型可以直接使用 `member` 来获取。

###### exception

::: danger IllegalStateException 

HookParam instance cannot cast to **TYPE**

:::

**异常原因**

在 `HookParam` 中调用 `instance` 方法指定了错误的类型。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 类型被 cast 为 Activity 但假设当前实例的类型并非此类型
        instance<Activity>()...
    }
}
```

**解决方案**

请确认当前 Hook 实例的正确类型并重新填写泛型中的类型，若不能确定请使用 `Any` 或直接使用 `instance` 变量。

###### exception

::: danger IllegalStateException 

HookParam Method args is empty, mabe not has args

:::

**异常原因**

在 `HookParam` 中调用 `ArgsModifyer.set` 方法但是当前实例的方法参数数组为空。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 调用了此方法
        args(...).set(...)
    }
}
```

**解决方案**

请确认你 Hook 的目标方法、构造方法的方法参数数量是否不为空，否则你无法使用此功能。

###### exception

::: danger IllegalStateException 

HookParam Method args index out of bounds, max is **NUMBER**

:::

**异常原因**

在 `HookParam` 中调用 `ArgsModifyer.set` 方法指定了超出方法参数下标的数组序号。

> 示例如下

```kotlin
injectMember {
    // ...
    after {
        // 下标从 0 开始，假设原始的参数下标是 5 个，但是这里填写了 6
        args(index = 6).set(...)
    }
}
```

**解决方案**

请确认你 Hook 的目标方法、构造方法的方法参数个数，并重新设置数组下标。

###### exception

::: danger IllegalStateException 

Current Hook Framework not support moduleAppResources

:::

**异常原因**

在 `PackageParam` 中调用了 `moduleAppResources` 变量但是无法获取到实例对象。

> 示例如下

```kotlin
encase {
    // 调用了此变量
    moduleAppResources...
}
```

**解决方案**

这种情况几乎不存在，除非目标 Hook Framework 自身存在问题，若真的发生了此问题，请携带详细日志进行反馈。

###### exception

::: danger IllegalStateException 

VariousClass match failed of those **CLASSES**

:::

**异常原因**

在使用 `VariousClass` 创建不确定的 `Class` 对象时全部的 `Class` 都没有被找到。

**解决方案**

检查当前 Hook 的宿主内是否存在其中能够匹配的 `Class` 后，再试一次。

###### exception

::: danger IllegalStateException 

Cannot get hook class "**NAME**" cause **THROWABLE**

:::

**异常原因**

在 `hook` 方法体非 `onPrepareHook` 方法内调用了 `instanceClass` 变量且当前 Hook 的 `Class` 不存在。

> 示例如下

```kotlin
TargetClass.hook {
    // 可能的情况为在非 onPrepareHook 方法体内调用了 instanceClass 变量用于打印日志
    loggerD(msg = "$instanceClass hook start")
}
```

**解决方案**

在 `hook` 内直接使用 `instanceClass` 是很危险的，若 Class 不存在则会直接导致 Hook 进程“死掉”。

详情请参考 [状态监听](../guide/example#状态监听)。

###### exception

::: danger IllegalStateException 

This hook instance is create by Members, not support any hook class instance

:::

**异常原因**

使用 `Member.hook { ... }` 并非 `Class.hook { ... }` 创建了 Hook 实例并使用了 `instanceClass`。

**解决方案**

你只能在使用 `Class.hook { ... }` 创建 Hook 实例的情况下使用 `instanceClass`。

###### exception

::: danger IllegalStateException 

Use of searchClass { ... }.hook { ... } is an error, please use like searchClass { ... }.get()?.hook { ... }

:::

**异常原因**

使用 `searchClass { ... }.hook { ... }` 方式直接创建了 Hook 实例。

**解决方案**

请使用 `searchClass { ... }.get()?.hook { ... }` 方式创建 Hook 实例。

###### exception

::: danger IllegalStateException 

This type \[**TYPE**\] not support to hook, supported are Constructors and Methods

:::

**异常原因**

使用 `Member.hook { ... }` 时创建了不支持 Hook 的成员对象，例如 `Field`。

**解决方案**

你只能 Hook `Constructor` 和 `Method`。

###### exception

::: danger IllegalStateException 

LayoutInflatedParam View instance got null

:::

**异常原因**

在布局 Hook 回调中调用了 `currentView` 但没取到实例对象。

> 示例如下

```kotlin
injectResource {
    conditions {
        name = "activity_main"
        layout()
    }
    injectAsLayout {
        // 调用了此变量
        currentView...
    }
}
```

**解决方案**

这种情况基本上不存在，除非被 Hook 的宿主当前 `Activity` 已经销毁或 Hook Framework 自身存在问题。

###### exception

::: danger IllegalStateException 

XResForwarder is invalid

:::

**异常原因**

在 `YukiResForwarder` 中调用了 `resources` 但没取到实例对象。

> 示例如下

```kotlin
// 调用了此变量
moduleAppResources.fwd(...).resources
```

**解决方案**

这种情况基本上不存在，除非 Hook Framework 自身存在问题。

###### exception

::: danger IllegalStateException 

paramTypes is empty, please use emptyParam() instead

:::

**异常原因**

在查找方法、构造方法时保留了空的 `param` 方法。

> 示例如下

```kotlin
method {
    name = "test"
    // 括号内没有填写任何参数
    param()
}
```

**解决方案**

若要标识此方法、构造方法没有参数，你可以有如下设置方法。

第一种，设置 `emptyParam` (推荐)

> 示例如下

```kotlin
method {
    name = "test"
    emptyParam()
}
```

第二种，设置 `paramCount = 0`

> 示例如下

```kotlin
method {
    name = "test"
    paramCount = 0
}
```

###### exception

::: danger IllegalStateException 

Invalid YukiHookCallback type

:::

**异常原因**

`YukiHookAPI` 的核心 Hook 功能发生故障。

**解决方案**

这种情况基本上不存在，若发生上述问题，确定并非自己的代码发生的问题后，可提交日志进行反馈。

###### exception

::: danger IllegalStateException 

ModuleContextThemeWrapper already loaded

:::

**异常原因**

在 `Context` 中使用 `applyModuleTheme` 方法时重复进行调用。

> 示例如下

```kotlin
// 假设这就是当前的 Context 对象
context.applyModuleTheme(R.style.Theme_AppCompat).applyModuleTheme(R.style.Theme_AppCompat)
```

**解决方案**

在 `Context` 中只能创建一次 `ModuleContextThemeWrapper`，请检查代码是否有循环调用问题。

###### exception

::: danger IllegalStateException 

Cannot create classes cache for "android", please remove "name" param

:::

**异常原因**

在系统框架 (android) 宿主使用了 `DexClassFinder` 的缓存功能 `searchClass(name = ...)`。

> 示例如下

```kotlin
loadSystem {
    searchClass(name = "test") {
        from(...)
        // ...
    }.get()
}
```

**解决方案**

由于缓存会将找到的 `Class` 名称存入 `SharedPreferences`，但是系统框架不存在 data 目录，所以请不要在系统框架中使用此功能。

###### exception

::: danger IllegalStateException 

Target Class type cannot cast to **TYPE**

:::

**异常原因**

使用 `Class.toClass`、`Class.toClassOrNull`、`GenericClass.argument` 方法将字符串类名转换为目标 `Class` 时声明了错误的类型。

以下使用 `Class.toClass` 方法来进行示例。

> 示例如下

```kotlin
// 假设目标类型是 Activity 但是被错误地转换为了 WrongClass 类型
val clazz = "android.app.Activity".toClass<WrongClass>()
```

**解决方案**

> 示例如下

```kotlin
// <解决方案 1> 填写正确的类型
val clazz1 = "android.app.Activity".toClass<Activity>()
// <解决方案 2> 不填写泛型声明
val clazz2 = "android.app.Activity".toClass()
```

请确保执行方法后声明的泛型是指定的目标 `Class` 类型，在不确定目标类型的情况下你可以不需要填写泛型声明。