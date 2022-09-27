---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# ConstructorFinder <span class="symbol">- class</span>

```kotlin:no-line-numbers
class ConstructorFinder internal constructor(override val hookInstance: YukiMemberHookCreator.MemberHookCreator?, override val classSet: Class<*>) : MemberBaseFinder
```

**Change Records**

`v1.0` `first`

`v1.0.2` `modified`

合并到 `BaseFinder`

`v1.1.0` `modified`

合并到 `MemberBaseFinder`

**Function Illustrate**

> `Constructor` 查找类。

可通过指定类型查找指定 `Constructor` 或一组 `Constructor`。

## paramCount <span class="symbol">- field</span>

```kotlin:no-line-numbers
var paramCount: Int
```

**Change Records**

`v1.0.67` `added`

**Function Illustrate**

> 设置 `Constructor` 参数个数。

你可以不使用 `param` 指定参数类型而是仅使用此变量指定参数个数。

若参数个数小于零则忽略并使用 `param`。

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions): IndexTypeCondition
```

**Change Records**

`v1.0.67` `added`

`v1.0.80` `modified`

将方法体进行 inline

`v1.1.0` `modified`

合并到 `ModifierConditions`

**Function Illustrate**

> 设置 `Constructor` 标识符筛选条件。

可不设置筛选条件，默认模糊查找并取第一个匹配的 `Constructor`。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## emptyParam <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun emptyParam(): IndexTypeCondition
```

**Change Records**

`v1.0.75` `added`

**Function Illustrate**

> 设置 `Constructor` 空参数、无参数。

## param <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun param(vararg paramType: Any): IndexTypeCondition
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 设置 `Constructor` 参数。

如果同时使用了 `paramCount` 则 `paramType` 的数量必须与 `paramCount` 完全匹配。

如果 `Constructor` 中存在一些无意义又很长的类型，你可以使用 [VagueType](../../../type/defined/DefinedTypeFactory#vaguetype-field) 来替代它。

::: danger

无参 **Constructor** 请使用 **emptyParam** 设置查找条件。

有参 **Constructor** 必须使用此方法设定参数或使用 **paramCount** 指定个数。

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## paramCount <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun paramCount(num: Int): IndexTypeCondition
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 设置 `Constructor` 参数个数。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数。

若参数个数小于零则忽略并使用 `param`。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## paramCount <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun paramCount(numRange: IntRange): IndexTypeCondition
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置 `Constructor` 参数个数范围。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数范围。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## paramCount <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun paramCount(conditions: CountConditions): IndexTypeCondition
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置 `Constructor` 参数个数条件。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数条件。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## superClass <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun superClass(isOnlySuperClass: Boolean)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置在 `classSet` 的所有父类中查找当前 `Constructor`。

::: warning

若当前 **classSet** 的父类较多可能会耗时，API 会自动循环到父类继承是 **Any** 前的最后一个类。

:::

## RemedyPlan <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class RemedyPlan internal constructor()
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> `Constructor` 重查找实现类，可累计失败次数直到查找成功。

### constructor <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun constructor(initiate: ConstructorConditions)
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 创建需要重新查找的 `Constructor`。

你可以添加多个备选 `Constructor`，直到成功为止，若最后依然失败，将停止查找并输出错误日志。

### Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**Change Records**

`v1.0.1` `added`

**Function Illustrate**

> `RemedyPlan` 结果实现类。

#### onFind <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onFind(initiate: HashSet<Constructor<*>>.() -> Unit)
```

**Change Records**

`v1.0.1` `added`

`v1.1.0` `modified`

`initiate` 参数 `Constructor` 变为 `HashSet<Constructor>`

**Function Illustrate**

> 当在 `RemedyPlan` 中找到结果时。

**Function Example**

你可以方便地对重查找的 `Constructor` 实现 `onFind` 方法。

> The following example

```kotlin
constructor {
    // Your code here.
}.onFind {
    // Your code here.
}
```

## Process <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Process internal constructor(internal val isNoSuch: Boolean, internal val throwable: Throwable?) : BaseResult
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> `Constructor` 查找结果处理类，为 `hookInstance` 提供。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Process.() -> Unit): Process
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 创建监听结果事件方法体。

**Function Example**

你可以使用 `lambda` 形式创建 `Result` 类。

> The following example

```kotlin
constructor {
    // Your code here.
}.result {
    all()
    remedys {}
    onNoSuchConstructor {}
}
```

### all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(): Process
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置全部查找条件匹配的多个 `Constructor` 实例结果到 `hookInstance`。

### remedys <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun remedys(initiate: RemedyPlan.() -> Unit): Result
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 创建 `Constructor` 重查找功能。

**Function Example**

当你遇到一种 `Constructor` 可能存在不同形式的存在时，可以使用 `RemedyPlan` 重新查找它，而没有必要使用 `onNoSuchConstructor` 捕获异常二次查找 `Constructor`。

若第一次查找失败了，你还可以在这里继续添加此方法体直到成功为止。

> The following example

```kotlin
constructor {
    // Your code here.
}.remedys {
    constructor {
        // Your code here.
    }
    constructor {
        // Your code here.
    }
}
```

### onNoSuchConstructor <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun onNoSuchConstructor(result: (Throwable) -> Unit): Result
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 监听找不到 `Constructor` 时。

只会返回第一次的错误信息，不会返回 `RemedyPlan` 的错误信息。

## Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor(internal val isNoSuch: Boolean, internal val throwable: Throwable?) : BaseResult
```

**Change Records**

`v1.0` `first`

`v1.1.0` `modified`

继承到接口 `BaseResult`

**Function Illustrate**

> `Constructor` 查找结果实现类。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 创建监听结果事件方法体。

**Function Example**

你可以使用 `lambda` 形式创建 `Result` 类。

> The following example

```kotlin
constructor {
    // Your code here.
}.result {
    get().call()
    all()
    remedys {}
    onNoSuchConstructor {}
}
```

### get <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun get(): Instance
```

**Change Records**

`v1.0.2` `added`

**Function Illustrate**

> 获得 `Constructor` 实例处理类。

若有多个 `Constructor` 结果只会返回第一个。

::: danger

若你设置了 **remedys** 请使用 **wait** 回调结果方法。

:::

**Function Example**

你可以通过获得方法所在实例来执行构造方法创建新的实例对象。

> The following example

```kotlin
constructor {
    // Your code here.
}.get().call()
```

你可以 `cast` 构造方法为指定类型的实例对象。

> The following example

```kotlin
constructor {
    // Your code here.
}.get().newInstance<TestClass>()
```

::: danger

若构造方法含有参数则后方参数必填。

:::

> The following example

```kotlin
constructor {
    // Your code here.
}.get().newInstance<TestClass>("param1", "param2")
```

### all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(): ArrayList<Instance>
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得 `Constructor` 实例处理类数组。

返回全部查找条件匹配的多个 `Constructor` 实例结果。

**Function Example**

你可以通过此方法来获得当前条件结果中匹配的全部 `Constructor`。

> The following example

```kotlin
constructor {
    // Your code here.
}.all().forEach { instance ->
    instance.call(...)
}
```

### give <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun give(): Constructor<*>?
```

**Change Records**

`v1.0.67` `added`

**Function Illustrate**

> 得到 `Constructor` 本身。

若有多个 `Constructor` 结果只会返回第一个。

在查找条件找不到任何结果的时候将返回 `null`。

### giveAll <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun giveAll(): HashSet<Constructor<*>>
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 得到 `Constructor` 本身数组。

返回全部查找条件匹配的多个 `Constructor` 实例。

在查找条件找不到任何结果的时候将返回空的 `HashSet`。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun wait(initiate: Instance.() -> Unit)
```

**Change Records**

`v1.0.2` `added`

**Function Illustrate**

> 获得 `Constructor` 实例处理类，配合 `RemedyPlan` 使用。

若有多个 `Constructor` 结果只会返回第一个。

::: danger

若你设置了 **remedys** 必须使用此方法才能获得结果。

若你没有设置 **remedys** 此方法将不会被回调。

:::

### waitAll <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun waitAll(initiate: ArrayList<Instance>.() -> Unit)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得 `Constructor` 实例处理类数组，配合 `RemedyPlan` 使用。

返回全部查找条件匹配的多个 `Constructor` 实例结果。

::: danger

若你设置了 **remedys** 必须使用此方法才能获得结果。

若你没有设置 **remedys** 此方法将不会被回调。

:::

### remedys <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun remedys(initiate: RemedyPlan.() -> Unit): Result
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 创建 `Constructor` 重查找功能。

**Function Example**

当你遇到一种 `Constructor` 可能存在不同形式的存在时，可以使用 `RemedyPlan` 重新查找它，而没有必要使用 `onNoSuchConstructor` 捕获异常二次查找 `Constructor`。

若第一次查找失败了，你还可以在这里继续添加此方法体直到成功为止。

> The following example

```kotlin
constructor {
    // Your code here.
}.remedys {
    constructor {
        // Your code here.
    }
    constructor {
        // Your code here.
    }
}
```

### onNoSuchConstructor <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun onNoSuchConstructor(result: (Throwable) -> Unit): Result
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 监听找不到 `Constructor` 时。

只会返回第一次的错误信息，不会返回 `RemedyPlan` 的错误信息。

### ignored <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignored(): Result
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 忽略异常并停止打印任何错误日志。

若 `isNotIgnoredHookingFailure` 为 `false` 则自动忽略。

::: warning

此时若要监听异常结果，你需要手动实现 **onNoSuchConstructor** 方法。

:::

<h3 class="deprecated">ignoredError - method</h3>

**Change Records**

`v1.0.3` `added`

`v1.1.0` `deprecated`

请转移到新方法 `ignored()`

### Instance <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Instance internal constructor(private val constructor: Constructor<*>?)
```

**Change Records**

`v1.0.2` `added`

`v1.1.0` `modified`

新增 `constructor` 参数

**Function Illustrate**

> `Constructor` 实例处理类。

#### call <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun call(vararg param: Any?): Any?
```

**Change Records**

`v1.0.2` `added`

**Function Illustrate**

> 执行 `Constructor` 创建目标实例，不指定目标实例类型。

#### newInstance <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> newInstance(vararg param: Any?): T?
```

**Change Records**

`v1.0.2` `added`

**Function Illustrate**

> 执行 `Constructor` 创建目标实例 ，指定 `T` 目标实例类型。