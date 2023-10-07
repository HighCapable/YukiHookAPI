---
pageClass: code-page
---

# HookParam <span class="symbol">- class</span>

```kotlin:no-line-numbers
class HookParam private constructor(
    private val creatorInstance: YukiMemberHookCreator,
    private var paramId: String,
    private var param: YukiHookCallback.Param?
)
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

移动 `HookParamWrapper` 到 `YukiHookCallback.Param`

修正拼写错误的 **creater** 命名到 **creator**

`v1.1.5` `修改`

新增 `paramId` 参数

`v1.2.0` `修改`

不再开放构造方法

**功能描述**

> Hook 方法、构造方法的目标对象实现类。

## args <span class="symbol">- field</span>

```kotlin:no-line-numbers
val args: Array<Any?>
```

**变更记录**

在 `v1.0` 添加

**功能描述**

> 获取当前 Hook 对象 `member` 或 `constructor` 的参数对象数组。

这里的数组每项类型默认为 `Any`，你可以使用 `args` 方法来实现 `ArgsModifyer.cast` 功能。

<h2 class="deprecated">firstArgs - field</h2>

**变更记录**

`v1.0` `添加`

`v1.0.75` `移除`

请使用 `args(index = 0)` 或 `args().first()`

<h2 class="deprecated">lastArgs - field</h2>

**变更记录**

`v1.0` `添加`

`v1.0.75` `移除`

请使用 `args().last()`

## instance <span class="symbol">- field</span>

```kotlin:no-line-numbers
val instance: Any
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 实例的对象。

::: danger

如果你当前 Hook 的对象是一个静态，那么它将不存在实例的对象。

:::

如果你不确定当前实例的对象是否为 `null`，你可以使用 `instanceOrNull`。

## instanceOrNull <span class="symbol">- field</span>

```kotlin:no-line-numbers
val instanceOrNull: Any?
```

**变更记录**

`v1.1.8` `新增`

**功能描述**

> 获取当前 Hook 实例的对象。

::: danger

如果你当前 Hook 的对象是一个静态，那么它将不存在实例的对象。

:::

## instanceClass <span class="symbol">- field</span>

```kotlin:no-line-numbers
val instanceClass: Class<*>?
```

**变更记录**

`v1.0` `添加`

`v1.2.0` `修改`

加入可空类型 (空安全)

**功能描述**

> 获取当前 Hook 实例的类对象。

::: danger

如果你当前 Hook 的对象是一个静态，那么它将不存在实例的对象。

:::

## member <span class="symbol">- field</span>

```kotlin:no-line-numbers
val member: Member
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获取当前 Hook 对象的 `Member`。

在不确定 `Member` 类型为 `Method` 或 `Constructor` 时可以使用此方法。

## method <span class="symbol">- field</span>

```kotlin:no-line-numbers
val method: Method
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 对象的方法。

## constructor <span class="symbol">- field</span>

```kotlin:no-line-numbers
val constructor: Constructor
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook 对象的构造方法。

## result <span class="symbol">- field</span>

```kotlin:no-line-numbers
var result: Any?
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取、设置当前 Hook 对象的 `method` 或 `constructor` 的返回值。

## dataExtra <span class="symbol">- field</span>

```kotlin:no-line-numbers
val dataExtra: Bundle
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 获取当前回调方法体范围内的数据存储实例。

## hasThrowable <span class="symbol">- field</span>

```kotlin:no-line-numbers
val hasThrowable: Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 判断是否存在设置过的方法调用抛出异常。

## throwable <span class="symbol">- field</span>

```kotlin:no-line-numbers
val throwable: Throwable?
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获取设置的方法调用抛出异常。

## Throwable.throwToApp <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun Throwable.throwToApp()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 向 Hook APP 抛出异常。

使用 `hasThrowable` 判断当前是否存在被抛出的异常。

使用 `throwable` 获取当前设置的方法调用抛出异常。

仅会在回调方法的 `MemberHookCreator.before` 或 `MemberHookCreator.after` 中生效。

::: danger

设置后会同时执行 **resultNull** 方法并将异常抛出给当前 Hook APP。

:::

**功能示例**

Hook 过程中的异常仅会作用于 (Xposed) 宿主环境，目标 Hook APP 不会受到影响。

若想将异常抛给 Hook APP，可以直接使用如下方法。

> 示例如下

```kotlin
hook {
    before {
        RuntimeException("Test Exception").throwToApp()
    }
}
```

::: danger

向 Hook APP 抛出异常<u>**会对其暴露被 Hook 的事实**</u>，是不安全的，容易被检测，请按实际场景合理使用。

:::

## result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> result(): T?
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` 或 `constructor` 的返回值 `T`。

<h2 class="deprecated">firstArg - method</h2>

**变更记录**

`v1.0.66` `新增`

`v1.0.75` `移除`

<h2 class="deprecated">lastArgs - method</h2>

**变更记录**

`v1.0.66` `新增`

`v1.0.75` `移除`

## instance <span class="symbol">- method</span>

```kotlin:no-line-numbers
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

## instanceOrNull <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> instanceOrNull(): T?
```

**变更记录**

`v1.1.8` `新增`

**功能描述**

> 获取当前 Hook 实例的对象 `T`。

**功能示例**

用法请参考 [instance](#instance-method) 方法。

## args <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun args(): ArgsIndexCondition
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` 或 `constructor` 的参数数组下标实例化类。

## args <span class="symbol">- method</span>

```kotlin:no-line-numbers
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

::: danger

请确保 **param** 类型为你的目标实例类型。

:::

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

::: danger

请确保 **param** 类型为 **Boolean**。

:::

> 示例如下

```kotlin
args(index = 1).setTrue()
```

你还可以使用 `setFalse` 方法设置 `param` 为 `false`。

::: danger

请确保 **param** 类型为 **Boolean**。

:::

> 示例如下

```kotlin
args(index = 1).setFalse()
```

## callOriginal <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun callOriginal(): Any?
```

```kotlin:no-line-numbers
fun <T> callOriginal(): T?
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 执行原始 `Member`。

调用自身未进行 Hook 的原始 `Member` 并调用原始参数执行。

**功能实例**

此方法可以 `invoke` 原始未经 Hook 的 `Member` 对象，取决于原始 `Member` 的参数。

调用自身原始的方法不会再经过当前 `before`、`after` 以及 `replaceUnit`、`replaceAny`。

比如我们 Hook 的这个方法被这样调用 `test("test value")`，使用此方法会调用其中的 `"test value"` 作为参数。

> 示例如下

```kotlin
method {
    name = "test"
     param(StringClass)
    returnType = StringClass
}.hook {
    after {
        // <方案1> 不使用泛型，不获取方法执行结果，调用将使用原方法传入的 args 自动传参
        callOriginal()
        // <方案2> 使用泛型，已知方法执行结果参数类型进行 cast
        // 假设返回值为 String，失败会返回 null，调用将使用原方法传入的 args 自动传参
        val value = callOriginal<String>()
    }
}
```

## invokeOriginal <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun invokeOriginal(vararg args: Any?): Any?
```

```kotlin:no-line-numbers
fun <T> invokeOriginal(vararg args: Any?): T?
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

不再需要使用 `member.invokeOriginal` 进行调用

**功能描述**

> 执行原始 `Member`。

调用自身未进行 Hook 的原始 `Member` 并自定义 `args` 执行。

**功能实例**

此方法可以 `invoke` 原始未经 Hook 的 `Member` 对象，可自定义需要调用的参数内容。

调用自身原始的方法不会再经过当前 `before`、`after` 以及 `replaceUnit`、`replaceAny`。

比如我们 Hook 的这个方法被这样调用 `test("test value")`，使用此方法可自定义其中的 `args` 作为参数。

> 示例如下

```kotlin
method {
    name = "test"
    param(StringClass)
    returnType = StringClass
}.hook {
    after {
        // <方案1> 不使用泛型，不获取方法执行结果
        invokeOriginal("test value")
        // <方案2> 使用泛型，已知方法执行结果参数类型进行 cast，假设返回值为 String，失败会返回 null
        val value = invokeOriginal<String>("test value")
    }
}
```

## resultTrue <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun resultTrue()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 Hook 对象方法的 `result` 返回值为 `true`。

::: danger

请确保 **result** 类型为 **Boolean**。

:::

## resultFalse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun resultFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 Hook 对象方法的 `result` 返回值为 `false`。

::: danger

请确保 **result** 类型为 **Boolean**。

:::

## resultNull <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun resultNull()
```

**变更记录**

`v1.0` `添加`

**功能描述**

::: warning

此方法将强制设置 Hook 对象方法的 **result** 为 **null**。

:::

## ArgsIndexCondition <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class ArgsIndexCondition internal constructor()
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 对方法参数的数组下标进行实例化类。

### first <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun first(): ArgsModifyer
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` 或 `constructor` 的参数数组第一位。

### last <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun last(): ArgsModifyer
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 获取当前 Hook 对象的 `method` 或 `constructor` 的参数数组最后一位。

## ArgsModifyer <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class ArgsModifyer internal constructor(private val index: Int)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 对方法参数的修改进行实例化类。

### cast <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> cast(): T?
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`of`~~ 为 `cast`

**功能描述**

> 得到方法参数的实例对象 `T`。

### byte <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun byte(): Byte?
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 Byte。

### int <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun int(): Int
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofInt`~~ 为 `int`

**功能描述**

> 得到方法参数的实例对象 Int。

### long <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun long(): Long
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofLong`~~ 为 `long`

**功能描述**

> 得到方法参数的实例对象 Long。

### short <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun short(): Short
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofShort`~~ 为 `short`

**功能描述**

> 得到方法参数的实例对象 Short。

### double <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun double(): Double
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofDouble`~~ 为 `double`

**功能描述**

> 得到方法参数的实例对象 Double。

### float <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun float(): Float
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofFloat`~~ 为 `float`

**功能描述**

> 得到方法参数的实例对象 Float。

### string <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun string(): String
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofString`~~ 为 `string`

**功能描述**

> 得到方法参数的实例对象 String。

### char <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun char(): Char
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 Char。

### boolean <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun boolean(): Boolean
```

**变更记录**

`v1.0.66` `新增`

`v1.0.68` `修改`

修改 ~~`ofBoolean`~~ 为 `boolean`

**功能描述**

> 得到方法参数的实例对象 Boolean。

### any <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun any(): Any?
```

**变更记录**

`v1.0.77` `新增`

**功能描述**

> 得到方法参数的实例对象 Any。

### array <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> array(): Array<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 Array。

### list <span class="symbol">- method</span>
```kotlin:no-line-numbers
inline fun <reified T> list(): List<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到方法参数的实例对象 List。

### set <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> set(any: T?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象。

### setNull <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun setNull()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象为 `null`。

### setTrue <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun setTrue()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象为 `true`。

::: danger

请确保目标对象的类型是 **Boolean**。

:::

### setFalse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun setFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置方法参数的实例对象为 `false`。

::: danger

请确保目标对象的类型是 **Boolean**。

:::