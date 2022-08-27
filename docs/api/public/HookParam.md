## HookParam *- class*

```kotlin
class HookParam internal constructor(private val createrInstance: YukiMemberHookCreater, private var param: YukiHookCallback.Param?)
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

移动 `HookParamWrapper` 到 `YukiHookCallback.Param`

**功能描述**

> Hook 方法、构造方法的目标对象实现类。

### args *- field*

```kotlin
val args: Array<Any?>
```

**变更记录**

在 `v1.0` 添加

**功能描述**

> 获取当前 Hook 对象 `member` 或 `constructor` 的参数对象数组。

这里的数组每项类型默认为 `Any`，你可以使用 `args` 方法来实现 `ArgsModifyer.cast` 功能。

### ~~firstArgs *- field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.75` `移除`

请使用 `args(index = 0)` 或 `args().first()`

### ~~lastArgs *- field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.75` `移除`

请使用 `args().last()`

### instance *- field*

```kotlin
val instance: Any
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 实例的对象。

!> 如果你当前 Hook 的对象是一个静态，那么它将不存在实例的对象。

### instanceClass *- field*

```kotlin
val instanceClass: Class<*>
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 实例的类对象。

### member *- field*

```kotlin
val member: Member
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获取当前 Hook 对象的 `Member`。

在不确定 `Member` 类型为 `Method` 或 `Constructor` 时可以使用此方法。

### method *- field*

```kotlin
val method: Method
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 对象的方法。

### constructor *- field*

```kotlin
val constructor: Constructor
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 对象的构造方法。

### result *- field*

```kotlin
var result: Any?
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取、设置当前 Hook 对象的 `method` 或 `constructor` 的返回值。

### hasThrowable *- field*

```kotlin
val hasThrowable: Boolean
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 判断是否存在设置过的方法调用抛出异常。

### throwable *- field*

```kotlin
val throwable: Throwable?
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获取设置的方法调用抛出异常。

### Throwable.throwToApp *- i-ext-method*

```kotlin
fun Throwable.throwToApp()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 向 Hook APP 抛出异常。

使用 `hasThrowable` 判断当前是否存在被抛出的异常。

使用 `throwable` 获取当前设置的方法调用抛出异常。

仅会在回调方法的 `MemberHookCreater.beforeHook` or `MemberHookCreater.afterHook` 中生效。

!> 设置后会同时执行 `resultNull` 方法并将异常抛出给当前 Hook APP。

**功能示例**

Hook 过程中的异常仅会作用于 (Xposed) 宿主环境，目标 Hook APP 不会受到影响。

若想将异常抛给 Hook APP，可以直接使用如下方法。

> 示例如下

```kotlin
injectMember {
    method {
        // ...
    }
    beforeHook {
        RuntimeException("Test Exception").throwToApp()
    }
}
```

!> 向 Hook APP 抛出异常<u>**会对其暴露被 Hook 的事实**</u>，是不安全的，容易被检测，请按实际场景合理使用。

### result *- method*

```kotlin
inline fun <reified T> result(): T?
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` 或 `constructor` 的返回值 `T`。

### ~~firstArgs *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.66` `新增`

`v1.0.75` `移除`

### ~~lastArgs *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.66` `新增`

`v1.0.75` `移除`

### instance *- method*

```kotlin
inline fun <reified T> instance(): T
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 实例的对象 `T`。

**功能示例**

你可以通过 `instance` 方法轻松使用泛型 `cast` 为目标对象的类型。

> 示例如下

```kotlin
instance<Activity>().finish()
```

### args *- method*

```kotlin
fun args(): ArgsIndexCondition
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` or `constructor` 的参数数组下标实例化类。

### args *- method*

```kotlin
fun args(index: Int): ArgsModifyer
```

**变更记录**

`v1.0` `添加`

`v1.0.75` `修改`

默认值 `index = 0` 移动到新的使用方法 `args().first()`

**功能描述**

> 获取当前 Hook 对象的 `method` 或 `constructor` 的参数实例化对象类。

**功能示例**

你可以通过 `args` 方法修改当前 Hook 实例的方法、构造方法的参数内容。

你可以直接使用 `set` 方法设置 `param` 为你的目标实例，接受 `Any` 类型。

!> 请确保 `param` 类型为你的目标实例类型。

> 示例如下

```kotlin
args(index = 0).set("modify the value")
```

你可以这样直接设置第一位 `param` 的值。

> 示例如下

```kotlin
args().first().set("modify the value")
```

你还可以直接设置最后一位 `param` 的值。

> 示例如下

```kotlin
args().last().set("modify the value")
```

你还可以使用 `setNull` 方法设置 `param` 为空。

> 示例如下

```kotlin
args(index = 1).setNull()
```

你还可以使用 `setTrue` 方法设置 `param` 为 `true`。

!> 请确保 `param` 类型为 `Boolean`。

> 示例如下

```kotlin
args(index = 1).setTrue()
```

你还可以使用 `setFalse` 方法设置 `param` 为 `false`。

!> 请确保 `param` 类型为 `Boolean`。

> 示例如下

```kotlin
args(index = 1).setFalse()
```

### callOriginal *- method*

```kotlin
fun callOriginal(): Any?
```

```kotlin
fun <T> callOriginal(): T?
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 执行原始 `Member`。

调用自身未进行 Hook 的原始 `Member` 并调用原始参数执行。

**功能实例**

此方法可以 `invoke` 原始未经 Hook 的 `Member` 对象，取决于原始 `Member` 的参数。

调用自身原始的方法不会再经过当前 `beforeHook`、`afterHook` 以及 `replaceUnit`、`replaceAny`。

比如我们 Hook 的这个方法被这样调用 `test("test value")`，使用此方法会调用其中的 `"test value"` 作为参数。

> 示例如下

```kotlin
injectMember {
    method {
        name = "test"
        param(StringType)
        returnType = StringType
    }
    afterHook {
        // <方案1> 不使用泛型，不获取方法执行结果，调用将使用原方法传入的 args 自动传参
        callOriginal()
        // <方案2> 使用泛型，已知方法执行结果参数类型进行 cast，假设返回值为 String，失败会返回 null，调用将使用原方法传入的 args 自动传参
        val value = callOriginal<String>()
    }
}
```

### invokeOriginal *- method*

```kotlin
fun invokeOriginal(vararg args: Any?): Any?
```

```kotlin
fun <T> invokeOriginal(vararg args: Any?): T?
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

不再需要使用 `member.invokeOriginal` 进行调用

**功能描述**

> 执行原始 `Member`。

调用自身未进行 Hook 的原始 `Member` 并自定义 `args` 执行。

**功能实例**

此方法可以 `invoke` 原始未经 Hook 的 `Member` 对象，可自定义需要调用的参数内容。

调用自身原始的方法不会再经过当前 `beforeHook`、`afterHook` 以及 `replaceUnit`、`replaceAny`。

比如我们 Hook 的这个方法被这样调用 `test("test value")`，使用此方法可自定义其中的 `args` 作为参数。

> 示例如下

```kotlin
injectMember {
    method {
        name = "test"
        param(StringType)
        returnType = StringType
    }
    afterHook {
        // <方案1> 不使用泛型，不获取方法执行结果
        invokeOriginal("test value")
        // <方案2> 使用泛型，已知方法执行结果参数类型进行 cast，假设返回值为 String，失败会返回 null
        val value = invokeOriginal<String>("test value")
    }
}
```

### resultTrue *- method*

```kotlin
fun resultTrue()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 Hook 对象方法的 `result` 返回值为 `true`。

!> 请确保 `result` 类型为 `Boolean`。

### resultFalse *- method*

```kotlin
fun resultFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 Hook 对象方法的 `result` 返回值为 `false`。

!> 请确保 `result` 类型为 `Boolean`。

### resultNull *- method*

```kotlin
fun resultNull()
```

**变更记录**

`v1.0` `添加`

**功能描述**

!> 此方法将强制设置 Hook 对象方法的 `result` 为 `null`。

### ArgsIndexCondition *- class*

```kotlin
inner class ArgsIndexCondition internal constructor()
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 对方法参数的数组下标进行实例化类。

#### first *- method*

```kotlin
fun first(): ArgsModifyer
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` or `constructor` 的参数数组第一位。

#### last *- method*

```kotlin
fun last(): ArgsModifyer
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` or `constructor` 的参数数组最后一位。

### ArgsModifyer *- class*

```kotlin
inner class ArgsModifyer internal constructor(private val index: Int)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 对方法参数的修改进行实例化类。

#### cast *- method*

```kotlin
fun <T> cast(): T?
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`of`~~ 为 `cast`

**功能描述**

> 得到方法参数的实例对象 `T`。

#### byte *- method*

```kotlin
fun byte(): Byte?
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 Byte。

#### int *- method*

```kotlin
fun int(): Int
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofInt`~~ 为 `int`

**功能描述**

> 得到方法参数的实例对象 Int。

#### long *- method*

```kotlin
fun long(): Long
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofLong`~~ 为 `long`

**功能描述**

> 得到方法参数的实例对象 Long。

#### short *- method*

```kotlin
fun short(): Short
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofShort`~~ 为 `short`

**功能描述**

> 得到方法参数的实例对象 Short。

#### double *- method*

```kotlin
fun double(): Double
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofDouble`~~ 为 `double`

**功能描述**

> 得到方法参数的实例对象 Double。

#### float *- method*

```kotlin
fun float(): Float
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofFloat`~~ 为 `float`

**功能描述**

> 得到方法参数的实例对象 Float。

#### string *- method*

```kotlin
fun string(): String
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofString`~~ 为 `string`

**功能描述**

> 得到方法参数的实例对象 String。

#### char *- method*

```kotlin
fun char(): Char
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 Char。

#### boolean *- method*

```kotlin
fun boolean(): Boolean
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofBoolean`~~ 为 `boolean`

**功能描述**

> 得到方法参数的实例对象 Boolean。

#### any *- method*

```kotlin
fun any(): Any?
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 得到方法参数的实例对象 Any。

#### array *- method*

```kotlin
inline fun <reified T> array(): Array<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 Array。

#### list *- method*
```kotlin
inline fun <reified T> list(): List<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 List。

#### set *- method*

```kotlin
fun <T> set(any: T?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象。

#### setNull *- method*

```kotlin
fun setNull()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象为 `null`。

#### setTrue *- method*

```kotlin
fun setTrue()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象为 `true`。

!> 请确保目标对象的类型是 `Boolean`。

#### setFalse *- method*

```kotlin
fun setFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象为 `false`。

!> 请确保目标对象的类型是 `Boolean`。