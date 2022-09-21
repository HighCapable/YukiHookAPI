---
pageClass: code-page
---

# DexClassFinder <span class="symbol">- class</span>

```kotlin:no-line-numbers
class DexClassFinder internal constructor(
    internal var name: String,
    internal var async: Boolean,
    override val loaderSet: ClassLoader?
) : ClassBaseFinder
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `Class` 查找类。

可使用 `BaseDexClassLoader` 通过指定条件查找指定 `Class` 或一组 `Class`。

::: warning

此功能尚在试验阶段，性能与稳定性可能仍然存在问题，使用过程遇到问题请向我们报告并帮助我们改进。

:::

## companion object <span class="symbol">- object</span>

**变更记录**

`v1.1.0` `新增`

### clearCache <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun clearCache(context: Context?, versionName: String?, versionCode: Long?)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 清除当前 `DexClassFinder` 的 `Class` 缓存。

适用于全部通过 [ClassLoader.searchClass](../../../factory/ReflectionFactory#classloader-searchclass-ext-method) 或 [PackageParam.searchClass](../../../param/PackageParam#searchclass-method) 获取的 `DexClassFinder`。

## fullName <span class="symbol">- field</span>

```kotlin:no-line-numbers
var fullName: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 完整名称。

只会查找匹配到的 `Class.getName`。

例如 `com.demo.Test` 需要填写 `com.demo.Test`。

## simpleName <span class="symbol">- field</span>

```kotlin:no-line-numbers
var simpleName: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 简单名称。

只会查找匹配到的 `Class.getSimpleName`。

例如 `com.demo.Test` 只需要填写 `Test`。

对于匿名类例如 `com.demo.Test$InnerTest` 会为空，此时你可以使用 [singleName](#singlename-field)。

## singleName <span class="symbol">- field</span>

```kotlin:no-line-numbers
var singleName: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 独立名称。

设置后将首先使用 `Class.getSimpleName`，若为空则会使用 `Class.getName` 进行处理。

例如 `com.demo.Test` 只需要填写 `Test`。

对于匿名类例如 `com.demo.Test$InnerTest` 只需要填写 `Test$InnerTest`。

## from <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun from(vararg name: String): FromPackageRules
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置在指定包名范围查找当前 `Class`。

设置后仅会在当前 `name` 开头匹配的包名路径下进行查找，可提升查找速度。

例如 ↓

`com.demo.test`

`com.demo.test.demo`

::: warning

建议设置此参数指定查找范围，否则 **Class** 过多时将会非常慢。

:::

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 标识符筛选条件。

可不设置筛选条件。

## fullName <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun fullName(value: String): ClassNameRules
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 完整名称。

只会查找匹配到的 `Class.getName`。

例如 `com.demo.Test` 需要填写 `com.demo.Test`。

## simpleName <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun simpleName(value: String): ClassNameRules
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 简单名称。

只会查找匹配到的 `Class.getSimpleName`。

例如 `com.demo.Test` 只需要填写 `Test`。

对于匿名类例如 `com.demo.Test$InnerTest 会为空`，此时你可以使用 [singleName](#singlename-method)。

## singleName <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun singleName(value: String): ClassNameRules
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 独立名称。

设置后将首先使用 `Class.getSimpleName`，若为空则会使用 `Class.getName` 进行处理。

例如 `com.demo.Test` 只需要填写 `Test`。

对于匿名类例如 `com.demo.Test$InnerTest` 只需要填写 `Test$InnerTest`。

## fullName <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun fullName(conditions: NameConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 完整名称条件。

只会查找匹配到的 `Class.getName`。

## simpleName <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun simpleName(conditions: NameConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 简单名称条件。

只会查找匹配到的 `Class.getSimpleName`。

## singleName <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun singleName(conditions: NameConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 独立名称条件。

设置后将首先使用 `Class.getSimpleName`，若为空则会使用 `Class.getName` 进行处理。

## extends <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> extends()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 继承的父类。

## extends <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun extends(vararg name: String)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 继承的父类。

会同时查找 `name` 中所有匹配的父类。

## implements <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> implements()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 实现的接口类。

## implements <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun implements(vararg name: String)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 实现的接口类。

会同时查找 `name` 中所有匹配的接口类。

## anonymous <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun anonymous()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 标识 `Class` 为匿名类。

例如 `com.demo.Test$1` 或 `com.demo.Test$InnerTest`。

标识后你可以使用 [enclosing](#enclosing-method) 来进一步指定匿名类的 (封闭类) 主类。

## noExtends <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun noExtends()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 没有任何继承。

此时 `Class` 只应该继承于 `Any`。

::: warning

设置此条件后 [extends](#extends-method) 将失效。

:::

## noImplements <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun noImplements()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 没有任何接口。

::: warning

设置此条件后 [implements](#implements-method) 将失效。

:::

## noSuper <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun noSuper()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 没有任何继承与接口。

此时 `Class` 只应该继承于 `Any`。

::: warning

设置此条件后 [extends](#extends-method) 与 [implements](#implements-method) 将失效。

:::

## enclosing <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> enclosing()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 匿名类的 (封闭类) 主类。

## enclosing <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun enclosing(vararg name: String)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 匿名类的 (封闭类) 主类。

会同时查找 `name` 中所有匹配的 (封闭类) 主类。

## FromPackageRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class FromPackageRules internal constructor(private val packages: ArrayList<ClassRulesData.PackageRulesData>)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 包名范围名称过滤匹配条件实现类。

### absolute <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun absolute()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置包名绝对匹配。

例如有如下包名 ↓

`com.demo.test.a`

`com.demo.test.a.b`

`com.demo.test.active`

若包名条件为 `com.demo.test.a` 则绝对匹配仅能匹配到第一个。

相反地，不设置以上示例会全部匹配。

## ClassNameRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class ClassNameRules internal constructor(private val name: ClassRulesData.NameRulesData)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 类名匹配条件实现类。

### optional <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun optional()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置类名可选。

例如有如下类名 ↓

`com.demo.Test` **fullName** / `Test` **simpleName**

`defpackage.a` **fullName** / `a` **simpleName**

这两个类名都是同一个类，但是在有些版本中被混淆有些版本没有。

此时可设置类名为 `com.demo.Test` **fullName** / `Test` **simpleName**。

这样就可在完全匹配类名情况下使用类名而忽略其它查找条件，否则忽略此条件继续使用其它查找条件。

## member <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun member(initiate: MemberRules.() -> Unit): MemberRulesResult
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 满足的 `Member` 条件。

## field <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun field(initiate: FieldRules.() -> Unit): MemberRulesResult
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 满足的 `Field` 条件。

## method <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun method(initiate: MethodRules.() -> Unit): MemberRulesResult
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 满足的 `Method` 条件。

## constructor <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun constructor(initiate: ConstructorRules.() -> Unit): MemberRulesResult
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Class` 满足的 `Constructor` 条件。

## Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor(internal var isNotFound: Boolean, internal var throwable: Throwable?) : BaseResult
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `Class` 查找结果实现类。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 创建监听结果事件方法体。

### get <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun get(): Class<*>?
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 得到 `Class` 本身。

若有多个 `Class` 结果只会返回第一个。

在查找条件找不到任何结果的时候将返回 `null`。

若你设置了 `async` 请使用 [wait](#wait-method) 方法。

### all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(): HashSet<Class<*>>
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 得到 `Class` 本身数组。

返回全部查找条件匹配的多个 `Class` 实例。

在查找条件找不到任何结果的时候将返回空的 `HashSet`。

若你设置了 `async` 请使用 [waitAll](#waitall-method) 方法。

### all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(result: (Class<*>) -> Unit): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 得到 `Class` 本身数组 (依次遍历)。

回调全部查找条件匹配的多个 `Class` 实例。

在查找条件找不到任何结果的时候将不会执行。

若你设置了 `async` 请使用 [waitAll](#waitall-method) 方法。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun wait(result: (Class<*>?) -> Unit): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 得到 `Class` 本身 (异步)。

若有多个 `Class` 结果只会回调第一个。

在查找条件找不到任何结果的时候将回调 null。

你需要设置 `async` 后此方法才会被回调，否则请使用 [get](#get-method) 方法。

### waitAll <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun waitAll(result: (HashSet<Class<*>>) -> Unit): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 得到 `Class` 本身数组 (异步)。

回调全部查找条件匹配的多个 `Class` 实例。

在查找条件找不到任何结果的时候将回调空的 `HashSet`。

你需要设置 `async` 后此方法才会被回调，否则请使用 [all](#all-method) 方法。

### onNoClassDefFoundError <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onNoClassDefFoundError(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 监听找不到 `Class` 时。

### ignored <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignored(): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 忽略异常并停止打印任何错误日志。

此时若要监听异常结果，你需要手动实现 [onNoClassDefFoundError](#onnoclassdeffounderror-method) 方法。