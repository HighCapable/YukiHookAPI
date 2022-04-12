# API 异常处理

> 异常是在开发过程经常遇到的主要问题，这里介绍了 `YukiHookAPI` 在使用过程中可能遇到的常见异常以及处理方式。

## 非阻断异常

> 这些异常不会导致 APP 停止运行(FC)，但是会在控制台打印 `E` 级别的日志，也可能会停止继续执行相关功能。

!> `loggerE` You cannot loading a hooker in "onInit" method! Aborted

<b>异常原因</b>

你尝试在继承 `YukiHookXposedInitProxy` 的 Hook 入口类的 `onInit` 方法中装载了 `encase` 方法。

> 示例如下

```kotlin
class HookEntry : YukiHookXposedInitProxy {

    override fun onInit() {
        // ❗错误的使用方法
        YukiHookAPI.encase {
            // Your code here.
        }
    }

    override fun onHook() {
        // Your code here.
    }
}
```

<b>解决方案</b>

请在 `onHook` 方法中装载 `encase` 方法。

> 示例如下

```kotlin
class HookEntry : YukiHookXposedInitProxy {

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

!> `loggerE` YukiHookAPI try to load HookEntryClass failed

<b>异常原因</b>

`YukiHookAPI` 在尝试装载 Hook 入口类 `onInit` 或 `onHook` 方法时发生了不能处理的异常或找不到入口类。

<b>解决方案</b>

通常情况下这种错误不会轻易发生，若一旦发生此错误，请自行查看控制台打印的日志定位问题，确定并非自己的代码发生的问题后，可提交日志进行反馈。

!> `loggerE` HookClass \[<b>NAME</b>\] not found

<b>异常原因</b>

当前被 Hook 的 `Class` 没有被找到。

<b>解决方案</b>

请检查目标 `Class` 是否存在，若想忽略此错误请使用 `ignoredHookClassNotFoundFailure` 方法。

!> `loggerE` Hook Member \[<b>NAME</b>\] failed

<b>异常原因</b>

Hook 目标方法、构造方法时发生错误。

<b>解决方案</b>

此问题通常由 Hook Framework 产生，请检查对应的日志内容，若问题持续出现请携带完整日志进行反馈。

!> `loggerE` Hooked Member with a finding error by <b>CLASS</b>

<b>异常原因</b>

在 Hook 执行后被 Hook 的 `member` 为 `null` 且已经设置目标 Hook 方法、构造类。

<b>解决方案</b>

请检查此错误发生前的上一个错误日志，或许在查找方法、构造方法的时候发生了找不到方法、构造方法的错误。

!> `loggerE` Hooked Member cannot be non-null by <b>CLASS</b>

<b>异常原因</b>

在 Hook 执行后被 Hook 的 `member` 为 `null` 且没有设置目标 Hook 方法、构造类。

> 示例如下

```kotlin
injectMember {
    // 这里并没有设置需要 Hook 的方法、构造方法的查询条件
    afterHook {
        // ...
    }
}
```

<b>解决方案</b>

请确认你已经在 Hook 之前正确设置了要 Hook 的方法、构造方法的查询方式。

> 示例如下

```kotlin
injectMember {
    // ✅ 正确的使用方法举例
    method {
        // Your code here.
    }
    afterHook {
        // ...
    }
}
```

!> `loggerE` No Method name "<b>NAME</b>" matched

<b>异常原因</b>

在使用 `allMethods` 查询需要 Hook 的方法时一个也没有找到。

<b>解决方案</b>

请确认当前 `Class` 中一定存在一个可以匹配此方法名称的方法。

!> `loggerE` No Constructor matched

<b>异常原因</b>

在使用 `allConstructors` 查询需要 Hook 的构造方法时一个也没有找到。

<b>解决方案</b>

请确认当前 `Class` 是否存在至少一个构造方法。

!> `loggerE` Hooked All Members with an error in Class \[<b>NAME</b>\]

<b>异常原因</b>

在 Hook 过程中发生了任意的异常。

<b>解决方案</b>

这是一个异常汇总提醒，只要 Hook 方法体内发生了异常就会打印此日志，请仔细查看从这里往上的具体异常是什么。

!> `loggerE` Try to hook <b>NAME</b>\[<b>NAME</b>\] got an Exception

<b>异常原因</b>

在 Hook 开始时发生了任意的异常。

<b>解决方案</b>

这是一个 Hook 开始就发生异常的提醒，请仔细查看具体的异常是什么以重新确定问题。

!> `loggerE` Method/Constructor/Field match type "<b>TYPE</b>" not allowed

<b>异常原因</b>

在查找方法、构造方法以及变量时设置了不允许的参数类型。

> 示例如下

```kotlin
// 查询一个方法
method {
    // ❗设置了无效的类型举例
    param(false, 1, 0)
    // ❗设置了无效的类型举例
    returnType = false
}

// 查询一个变量
field {
    // ❗设置了无效的类型举例
    type = false
}
```

<b>解决方案</b>

在查询中 `param`、`returnType`、`type` 中仅接受 `Class`、`String`、`VariousClass` 类型的传值，不可传入参数实例。

> 示例如下

```kotlin
// 查询一个方法
method {
    // ✅ 正确的使用方法举例
    param(BooleanType, IntType, IntType)
    // ✅ 正确的使用方法举例
    returnType = BooleanType
    // ✅ 以下方案也是正确的
    returnType = "java.lang.Boolean"
}

// 查询一个变量
field {
    // ✅ 正确的使用方法举例
    type = BooleanType
}
```

!> `loggerE` NoSuchMethod/NoSuchConstructor/NoSuchField happend in \[<b>NAME</b>\]

<b>异常原因</b>

在查找方法、构造方法以及变量时并未找到目标方法、构造方法以及变量。

<b>解决方案</b>

请确认你的查询条件是否能正确匹配到目标 `Class` 中的指定方法、构造方法以及变量。

!> `loggerE` Trying <b>COUNT</b> times and all failure by RemedyPlan

<b>异常原因</b>

使用 `RemedyPlan` 重新查找方法、构造方法时依然没有找到方法、构造方法。

<b>解决方案</b>

请确认你设置的 `RemedyPlan` 参数以及宿主内存在的 `Class`，再试一次。

!> `loggerE` Try to get field instance failed

<b>异常原因</b>

在使用变量查询结果的 `get` 方法后并没有成功得到对应的实例。

> 示例如下

```kotlin
field {
    // ...
}.get(instance)...
```

<b>解决方案</b>

请确认当前变量所在的实例是静态的还是动态的，并查看错误日志检查传入的实例类型是否正确。

!> `loggerE` You must set a condition when finding a Method/Constructor/Field

<b>异常原因</b>

在查找方法、构造方法以及变量时并未设置任何条件。

> 示例如下

```kotlin
method {
    // 这里没有设置任何条件
}
```

<b>解决方案</b>

请将查询条件补充完整并再试一次。

!> `loggerE` Can't find this Method/Constructor/Field \[<b>NAME</b>\] because classSet is null

<b>异常原因</b>

在查找方法、构造方法以及变量时所设置的 `Class` 实例为 `null`。

> 示例如下

```kotlin
// 假设 TargetClass 的实例为 null
TargetClass.method {
    // ...
}
```

<b>解决方案</b>

这种情况比较少见，请检查你要查询的目标 `Class` 是否被正确赋值并检查整个 Hook 流程和使用范围。

!> `loggerE` Field match type class is not found

<b>异常原因</b>

在查找变量时所设置的查询条件中 `type` 的 `Class` 实例未被找到。

> 示例如下

```kotlin
field {
    name = "test"
    // 假设这里设置的 type 的 Class 并不存在
    type = "com.example.TestClass"
}
```

<b>解决方案</b>

请检查查询条件中 `type` 的 `Class` 是否存在，然后再试一次。

!> `loggerE` Method match returnType class is not found

<b>异常原因</b>

在查找方法时所设置的查询条件中 `returnType` 的 `Class` 实例未被找到。

> 示例如下

```kotlin
method {
    name = "test"
    // 假设这里设置的 returnType 的 Class 并不存在
    returnType = "com.example.TestClass"
}
```

<b>解决方案</b>

请检查查询条件中 `returnType` 的 `Class` 是否存在，然后再试一次。

!> `loggerE` Method/Constructor match paramType\[<b>INDEX</b>\] class is not found

<b>异常原因</b>

在查找方法、构造方法时所设置的查询条件中 `param` 的 `index` 号下标的 `Class` 实例未被找到。

```kotlin
method {
    name = "test"
    // 假设这里设置的 1 号下标的 Class 并不存在
    param(StringType, "com.example.TestClass", BooleanType)
}
```

<b>解决方案</b>

请检查查询条件中 `param` 的 `index` 号下标的 `Class` 是否存在，然后再试一次。

## 阻断异常

> 这些异常会直接导致 APP 停止运行(FC)，同时会在控制台打印 `E` 级别的日志，还会造成 Hook 进程“死掉”。

!> `IllegalStateException` YukiHookModulePrefs not allowed in Custom Hook API

<b>异常原因</b>

在 Hook 自身 APP(非 Xposed 模块) 中使用了 `YukiHookModulePrefs`。

> 示例如下

```kotlin
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        YukiHookAPI.encase(base) {
            // ❗不能在这种情况下使用 prefs
            prefs.getBoolean("test_data")
        }
        super.attachBaseContext(base)
    }
}
```

<b>解决方案</b>

你只能在 [作为 Xposed 模块使用](config/xposed-using) 时使用 `YukiHookModulePrefs`，在 Hook 自身 APP 中请使用原生的 `Sp` 存储。

!> `IllegalStateException` Xposed modulePackageName load failed, please reset and rebuild it

<b>异常原因</b>

在 Hook 过程中使用 `YukiHookModulePrefs` 时无法读取装载时的 `modulePackageName` 导致不能确定自身模块的包名。

<b>解决方案</b>

请仔细阅读 [这里](config/xposed-using?id=modulepackagename-参数) 的帮助文档，正确配置模块的 Hook 入口类包名。

!> `IllegalStateException` If you want to use module prefs, you must set the context instance first

<b>异常原因</b>

在模块中使用了 `YukiHookModulePrefs` 存储数据但并未传入 `Context` 实例。

> 示例如下

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ❗错误的使用方法
        YukiHookModulePrefs().getBoolean("test_data")
    }
}
```

<b>解决方案</b>

在 `Activity` 中推荐使用 `modulePrefs` 方法来装载 `YukiHookModulePrefs`。

> 示例如下

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 可以使用但是不推荐
        YukiHookModulePrefs(this).getBoolean("test_data")
        // ✅ 推荐的使用方法
        modulePrefs.getBoolean("test_data")
    }
}
```

!> `IllegalStateException` Key-Value type <b>TYPE</b> is not allowed

<b>异常原因</b>

在使用 `YukiHookModulePrefs` 的 `get` 或 `put` 方法时传入了不支持的存储类型。

<b>解决方案</b>

`YukiHookModulePrefs` 支持的类型只有 `String`、`Int`、`Float`、`Long`、`Boolean`，请传入支持的类型。

!> `IllegalStateException` HookParam Method args index must be >= 0

<b>异常原因</b>

在 `HookParam` 中调用 `args().last()` 但是目标 `param` 为空或 `args` 中的 `index` 设置了小于 0 的数值。

> 示例如下

```kotlin
injectMember {
    // ...
    afterHook {
        // 假设 param 是空的
        args().last()...
        // 设置了小于 0 的 index
        args(index = -5)...
    }
}
```

<b>解决方案</b>

请确认你 Hook 的目标方法、构造方法的方法参数数量是否不为空，且不能对 `args` 的下标设置小于 0 的数值。

!> `IllegalStateException` HookParam instance got null! Is this a static member?

<b>异常原因</b>

在 `HookParam` 中调用 `instance` 变量或 `instance` 方法但获取不到当前实例的对象。

> 示例如下

```kotlin
injectMember {
    // ...
    afterHook {
        // 调用了此变量
        instance...
        // 调用了此方法
        instance<Any>()...
    }
}
```

<b>解决方案</b>

请确认你 Hook 的方法是否为静态类型，静态类型的方法没有实例，不能使用此功能，若非静态方法，请检查实例是否已经销毁。

!> `IllegalStateException` Current hook Method type is wrong or null

<b>异常原因</b>

在 `HookParam` 中调用 `method` 变量但获取不到当前实例的方法实例。

> 示例如下

```kotlin
injectMember {
    // ...
    afterHook {
        // 调用了此变量
        method...
    }
}
```

<b>解决方案</b>

请确认你 Hook 的方法是构造方法还是普通方法并使用对应类型的方法获取指定的实例。

!> `IllegalStateException` Current hook Constructor type is wrong or null

<b>异常原因</b>

在 `HookParam` 中调用 `constructor` 变量但获取不到当前实例的方法实例。

> 示例如下

```kotlin
injectMember {
    // ...
    afterHook {
        // 调用了此变量
        constructor...
    }
}
```

<b>解决方案</b>

请确认你 Hook 的方法是普通方法还是构造方法并使用对应类型的方法获取指定的实例。

!> `IllegalStateException` HookParam instance cannot cast to <b>TYPE</b>

<b>异常原因</b>

在 `HookParam` 中调用 `instance` 方法指定了错误的类型。

> 示例如下

```kotlin
injectMember {
    // ...
    afterHook {
        // 类型被 cast 为 Activity 但假设当前实例的类型并非此类型
        instance<Activity>()...
    }
}
```

<b>解决方案</b>

请确认当前 Hook 实例的正确类型并重新填写泛型中的类型，若不能确定请使用 `Any` 或直接使用 `instance` 变量。

!> `IllegalStateException` HookParam Method args is empty, mabe not has args

<b>异常原因</b>

在 `HookParam` 中调用 `ArgsModifyer.set` 方法但是当前实例的方法参数数组为空。

> 示例如下

```kotlin
injectMember {
    // ...
    afterHook {
        // 调用了此方法
        args(...).set(...)
    }
}
```

<b>解决方案</b>

请确认你 Hook 的目标方法、构造方法的方法参数数量是否不为空，否则你无法使用此功能。

!> `IllegalStateException` HookParam Method args index out of bounds, max is <b>NUMBER</b>

<b>异常原因</b>

在 `HookParam` 中调用 `ArgsModifyer.set` 方法指定了超出方法参数下标的数组序号。

> 示例如下

```kotlin
injectMember {
    // ...
    afterHook {
        // 下标从 0 开始，假设原始的参数下标是 5 个，但是这里填写了 6
        args(index = 6).set(...)
    }
}
```

<b>解决方案</b>

请确认你 Hook 的目标方法、构造方法的方法参数个数，并重新设置数组下标。

!> `IllegalStateException` PackageParam got null ClassLoader

<b>异常原因</b>

在 `PackageParam` 中调用了 `appClassLoader` 变量但是无法获取到实例对象。

> 示例如下

```kotlin
encase {
    // 调用了此变量
    appClassLoader...
}
```

<b>解决方案</b>

这种情况几乎不存在，除非模块被装载的宿主或目标 Xposed 框架自身存在问题，若真的发生了此问题，请携带详细日志进行反馈。

!> `IllegalStateException` PackageParam got null appContext

<b>异常原因</b>

在 `PackageParam` 中调用了 `appContext` 变量但是无法获取到实例对象。

> 示例如下

```kotlin
encase {
    // 调用了此变量
    appContext...
}
```

<b>解决方案</b>

这种情况几乎不存在，除非 Android 系统结构损坏或 Xposed 框架自身存在问题，若真的发生了此问题，请携带详细日志进行反馈。

!> `IllegalStateException` VariousClass match failed of those <b>CLASSES</b>

<b>异常原因</b>

在使用 `VariousClass` 创建不确定的 `Class` 对象时全部的 `Class` 都没有被找到。

<b>解决方案</b>

检查当前 Hook 的宿主内是否存在其中能够匹配的 `Class` 后，再试一次。

!> `IllegalStateException` Cannot get hook class "<b>NAME</b>" cause <b>THROWABLE</b>

<b>异常原因</b>

在 `hook` 方法体非 `onPrepareHook` 方法内调用了 `instanceClass` 变量且当前 Hook 的 `Class` 不存在。

> 示例如下

```kotlin
TargetClass.hook {
    // 可能的情况为在非 onPrepareHook 方法体内调用了 instanceClass 变量用于打印日志
    loggerD(msg = "$instanceClass hook start")
}
```

<b>解决方案</b>

在 `hook` 内直接使用 `instanceClass` 是很危险的，若 Class 不存在则会直接导致 Hook 进程“死掉”。

详情请参考 [状态监听](guide/example?id=状态监听)。

!> `IllegalStateException` Hook Members is empty, hook aborted

<b>异常原因</b>

使用了 `hook` 方法体但其中并没有填写内容。

> 示例如下

```kotlin
TargetClass.hook {
    // 这里没有填写任何内容
}
```

<b>解决方案</b>

你必须在 `hook` 方法体内加入至少一个 `injectMember` 方法。

!> `IllegalStateException` paramTypes is empty, please delete param() method

<b>异常原因</b>

在查找方法、构造方法时保留了空的 `param` 方法。

> 示例如下

```kotlin
method {
    // 没有填写任何参数
    param()
}
```

<b>解决方案</b>

若要标识此方法、构造方法没有参数，你可以什么都不写或设置 `paramCount = 0` 即可。