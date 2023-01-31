---
pageClass: code-page
---

# ConstructorFinder <span class="symbol">- class</span>

```kotlin:no-line-numbers
class ConstructorFinder internal constructor(override val classSet: Class<*>) : MemberBaseFinder
```

**变更记录**

`v1.0` `添加`

`v1.0.2` `修改`

合并到 `BaseFinder`

`v1.1.0` `修改`

合并到 `MemberBaseFinder`

`v1.1.8` `修改`

移动 `hookInstance` 参数到 `MemberBaseFinder.MemberHookerManager`

**功能描述**

> `Constructor` 查找类。

可通过指定类型查找指定 `Constructor` 或一组 `Constructor`。

## paramCount <span class="symbol">- field</span>

```kotlin:no-line-numbers
var paramCount: Int
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 设置 `Constructor` 参数个数。

你可以不使用 `param` 指定参数类型而是仅使用此变量指定参数个数。

若参数个数小于零则忽略并使用 `param`。

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions): IndexTypeCondition
```

**变更记录**

`v1.0.67` `新增`

`v1.0.80` `修改`

将方法体进行 inline

`v1.1.0` `修改`

合并到 `ModifierConditions`

**功能描述**

> 设置 `Constructor` 标识符筛选条件。

可不设置筛选条件，默认模糊查找并取第一个匹配的 `Constructor`。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## emptyParam <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun emptyParam(): IndexTypeCondition
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 设置 `Constructor` 空参数、无参数。

## param <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun param(vararg paramType: Any): IndexTypeCondition
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置 `Constructor` 参数。

如果同时使用了 `paramCount` 则 `paramType` 的数量必须与 `paramCount` 完全匹配。

如果 `Constructor` 中存在一些无意义又很长的类型，你可以使用 [VagueType](../../../type/defined/DefinedTypeFactory#vaguetype-field) 来替代它。

::: danger

无参 **Constructor** 请使用 **emptyParam** 设置查找条件。

有参 **Constructor** 必须使用此方法设定参数或使用 **paramCount** 指定个数。

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## param <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun param(conditions: ObjectsConditions): IndexTypeCondition
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 设置 `Constructor` 参数条件。

::: danger

无参 **Constructor** 请使用 **emptyParam** 设置查找条件。

有参 **Constructor** 必须使用此方法设定参数或使用 **paramCount** 指定个数。

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## paramCount <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun paramCount(num: Int): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

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

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Constructor` 参数个数范围。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数范围。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## paramCount <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun paramCount(conditions: CountConditions): IndexTypeCondition
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Constructor` 参数个数条件。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数条件。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## superClass <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun superClass(isOnlySuperClass: Boolean)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置在 `classSet` 的所有父类中查找当前 `Constructor`。

::: warning

若当前 **classSet** 的父类较多可能会耗时，API 会自动循环到父类继承是 **Any** 前的最后一个类。

:::

## RemedyPlan <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class RemedyPlan internal constructor()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> `Constructor` 重查找实现类，可累计失败次数直到查找成功。

### constructor <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun constructor(initiate: ConstructorConditions)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建需要重新查找的 `Constructor`。

你可以添加多个备选 `Constructor`，直到成功为止，若最后依然失败，将停止查找并输出错误日志。

### Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**变更记录**

`v1.0.1` `新增`

**功能描述**

> `RemedyPlan` 结果实现类。

#### onFind <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onFind(initiate: HashSet<Constructor<*>>.() -> Unit)
```

**变更记录**

`v1.0.1` `新增`

`v1.1.0` `修改`

`initiate` 参数 `Constructor` 变为 `HashSet<Constructor>`

**功能描述**

> 当在 `RemedyPlan` 中找到结果时。

**功能示例**

你可以方便地对重查找的 `Constructor` 实现 `onFind` 方法。

> 示例如下

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

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `Constructor` 查找结果处理类，为 `hookManager` 提供。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Process.() -> Unit): Process
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 创建监听结果事件方法体。

**功能示例**

你可以使用 `lambda` 形式创建 `Result` 类。

> 示例如下

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

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置全部查找条件匹配的多个 `Constructor` 实例结果到 `hookManager`。

### remedys <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun remedys(initiate: RemedyPlan.() -> Unit): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 创建 `Constructor` 重查找功能。

**功能示例**

当你遇到一种 `Constructor` 可能存在不同形式的存在时，可以使用 `RemedyPlan` 重新查找它，而没有必要使用 `onNoSuchConstructor` 捕获异常二次查找 `Constructor`。

若第一次查找失败了，你还可以在这里继续添加此方法体直到成功为止。

> 示例如下

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

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 监听找不到 `Constructor` 时。

只会返回第一次的错误信息，不会返回 `RemedyPlan` 的错误信息。

## Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor(internal val isNoSuch: Boolean, internal val throwable: Throwable?) : BaseResult
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

继承到接口 `BaseResult`

**功能描述**

> `Constructor` 查找结果实现类。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建监听结果事件方法体。

**功能示例**

你可以使用 `lambda` 形式创建 `Result` 类。

> 示例如下

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

**变更记录**

`v1.0.2` `新增`

**功能描述**

> 获得 `Constructor` 实例处理类。

若有多个 `Constructor` 结果只会返回第一个。

::: danger

若你设置了 **remedys** 请使用 **wait** 回调结果方法。

:::

**功能示例**

你可以通过获得方法所在实例来执行构造方法创建新的实例对象。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.get().call()
```

你可以 `cast` 构造方法为指定类型的实例对象。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.get().newInstance<TestClass>()
```

::: danger

若构造方法含有参数则后方参数必填。

:::

> 示例如下

```kotlin
constructor {
    // Your code here.
}.get().newInstance<TestClass>("param1", "param2")
```

### all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(): ArrayList<Instance>
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得 `Constructor` 实例处理类数组。

返回全部查找条件匹配的多个 `Constructor` 实例结果。

**功能示例**

你可以通过此方法来获得当前条件结果中匹配的全部 `Constructor`。

> 示例如下

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

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 得到 `Constructor` 本身。

若有多个 `Constructor` 结果只会返回第一个。

在查找条件找不到任何结果的时候将返回 `null`。

### giveAll <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun giveAll(): HashSet<Constructor<*>>
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 得到 `Constructor` 本身数组。

返回全部查找条件匹配的多个 `Constructor` 实例。

在查找条件找不到任何结果的时候将返回空的 `HashSet`。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun wait(initiate: Instance.() -> Unit)
```

**变更记录**

`v1.0.2` `新增`

**功能描述**

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

**变更记录**

`v1.1.0` `新增`

**功能描述**

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

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建 `Constructor` 重查找功能。

**功能示例**

当你遇到一种 `Constructor` 可能存在不同形式的存在时，可以使用 `RemedyPlan` 重新查找它，而没有必要使用 `onNoSuchConstructor` 捕获异常二次查找 `Constructor`。

若第一次查找失败了，你还可以在这里继续添加此方法体直到成功为止。

> 示例如下

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

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 监听找不到 `Constructor` 时。

只会返回第一次的错误信息，不会返回 `RemedyPlan` 的错误信息。

### ignored <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignored(): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 忽略异常并停止打印任何错误日志。

若 `MemberBaseFinder.MemberHookerManager.isNotIgnoredNoSuchMemberFailure` 为 `false` 则自动忽略。

::: warning

此时若要监听异常结果，你需要手动实现 **onNoSuchConstructor** 方法。

:::

<h3 class="deprecated">ignoredError - method</h3>

**变更记录**

`v1.0.3` `新增`

`v1.1.0` `作废`

请转移到新方法 `ignored()`

### Instance <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Instance internal constructor(private val constructor: Constructor<*>?)
```

**变更记录**

`v1.0.2` `新增`

`v1.1.0` `修改`

新增 `constructor` 参数

**功能描述**

> `Constructor` 实例处理类。

#### call <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun call(vararg args: Any?): Any?
```

**变更记录**

`v1.0.2` `新增`

`v1.1.6` `修改`

修改参数命名 `param` 为 `args`

**功能描述**

> 执行 `Constructor` 创建目标实例，不指定目标实例类型。

#### newInstance <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> newInstance(vararg args: Any?): T?
```

**变更记录**

`v1.0.2` `新增`

`v1.1.6` `修改`

修改参数命名 `param` 为 `args`

**功能描述**

> 执行 `Constructor` 创建目标实例 ，指定 `T` 目标实例类型。