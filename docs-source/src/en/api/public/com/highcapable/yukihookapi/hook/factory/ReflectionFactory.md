---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# ReflectionFactory <span class="symbol">- kt</span>

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 这是自定义 `Member` 和 `Class` 相关功能的查找匹配以及 `invoke` 的封装类。

## MembersType <span class="symbol">- class</span>

```kotlin:no-line-numbers
enum class MembersType
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 定义一个 `Class` 中的 `Member` 类型

### ALL <span class="symbol">- enum</span>

```kotlin:no-line-numbers
ALL
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 全部 `Method` 与 `Constructor`。

### METHOD <span class="symbol">- enum</span>

```kotlin:no-line-numbers
METHOD
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 全部 `Method`。

### CONSTRUCTOR <span class="symbol">- enum</span>

```kotlin:no-line-numbers
CONSTRUCTOR
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 全部 `Constructor`。

## ClassLoader.searchClass <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun ClassLoader.searchClass(name: String, async: Boolean, initiate: ClassConditions): DexClassFinder.Result
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过当前 `ClassLoader` 按指定条件查找并得到 **Dex** 中的 `Class`。

::: danger

此方法在 **Class** 数量过多及查找条件复杂时会非常耗时。

建议启用 **async** 或设置 **name** 参数，**name** 参数将在 Hook APP (宿主) 不同版本中自动进行本地缓存以提升效率。

此功能尚在试验阶段，性能与稳定性可能仍然存在问题，使用过程遇到问题请向我们报告并帮助我们改进。

:::

## ClassLoader.onLoadClass <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
fun ClassLoader.onLoadClass(result: (Class<*>) -> Unit)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 监听当前 `ClassLoader` 的 `ClassLoader.loadClass` 方法装载。

::: danger

只有当前 **ClassLoader** 有主动使用 **ClassLoader.loadClass** 事件时才能被捕获。

这是一个实验性功能，一般情况下不会用到此方法，不保证不会发生错误。

只能在 (Xposed) 宿主环境使用此功能，其它环境下使用将不生效且会打印警告信息。

:::

**Function Example**

针对一些使用特定 `ClassLoader` 装载 `Class` 的宿主应用，你可以使用此方法来监听 `Class` 加载情况。

::: warning

为了防止发生问题，你需要<u>**得到一个存在的 ClassLoader 实例**</u>来使用此功能。

:::

比如我们在 `PackageParam` 中使用 `appClassLoader`。

> The following example

```kotlin
appClassLoader.onLoadClass { clazz ->
    // 得到 clazz 即加载对象
    clazz... // 这里进行你需要的操作
}
```

或使用你得到的存在的 `ClassLoader` 实例，可以通过 Hook 获取。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
customClassLoader?.onLoadClass { clazz ->
    // ...
}
```

在判断到这个 `Class` 被装载成功时，开始执行你的 Hook 功能。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
customClassLoader?.onLoadClass { clazz ->
    if(clazz.name == /** 你需要的 Class 名称 */) {
        clazz.hook {
            // ...
        }
    }
}
```

<h2 class="deprecated">hookClass - field</h2>

**Change Records**

`v1.0` `first`

`v1.1.0` `removed`

`HookClass` 相关功能不再对外开放

<h2 class="deprecated">normalClass - field</h2>

**Change Records**

`v1.0` `first`

`v1.1.0` `removed`

`HookClass` 相关功能不再对外开放

<h2 class="deprecated">hasClass - field</h2>

**Change Records**

`v1.0` `first`

`v1.1.0` `removed`

请直接使用 `hasClass()` 无参方法

## Class.hasExtends <span class="symbol">- ext-field</span>

```kotlin:no-line-numbers
val Class<*>.hasExtends: Boolean
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 当前 `Class` 是否有继承关系，父类是 `Any` 将被认为没有继承关系。

<h2 class="deprecated">classOf - method</h2>

**Change Records**

`v1.0` `first`

`v1.1.0` `deprecated`

请转到 `toClass(...)` 方法

## String.toClass <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
fun String.toClass(loader: ClassLoader?): Class<*>
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过字符串类名转换为 `loader` 中的实体类。

**Function Example**

你可以直接填写你要查找的目标 `Class`，必须在默认 `ClassLoader` 下存在。

> The following example

```kotlin
"com.example.demo.DemoClass".toClass()
```

你还可以自定义 `Class` 所在的 `ClassLoader`。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
"com.example.demo.DemoClass".toClass(customClassLoader)
```

## String.toClassOrNull <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
fun String.toClassOrNull(loader: ClassLoader?): Class<*>?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过字符串类名转换为 `loader` 中的实体类。

找不到 `Class` 会返回 `null`，不会抛出异常。

**Function Example**

用法请参考 [String.toClass](#string-toclass-ext-method) 方法。

## classOf <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> classOf(loader: ClassLoader?): Class<*>
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过 `T` 得到其 `Class` 实例并转换为实体类。

**Function Example**

我们要获取一个 `Class` 在 `Kotlin` 下不通过反射时应该这样做。

> The following example
> 
```kotlin
DemoClass::class.java
```

现在，你可以直接 `cast` 一个实例并获取它的 `Class` 对象，必须在当前 `ClassLoader` 下存在。

> The following example

```kotlin
classOf<DemoClass>()
```

若目标存在的 `Class` 为 `stub`，通过这种方式，你还可以自定义 `Class` 所在的 `ClassLoader`。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
classOf<DemoClass>(customClassLoader)
```

## String.hasClass <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
fun String.hasClass(loader: ClassLoader?): Boolean
```

**Change Records**

`v1.0` `first`

`v1.1.0` `modified`

支持直接使用空参数方法使用默认 `ClassLoader` 进行判断

**Function Illustrate**

> 通过字符串类名使用指定的 `ClassLoader` 查找是否存在。

**Function Example**

你可以轻松的使用此方法判断字符串中的类是否存在，效果等同于直接使用 `Class.forName`。

> The following example

```kotlin
if("com.example.demo.DemoClass".hasClass()) {
    // Your code here.
}
```

填入方法中的 `loader` 参数可判断指定的 `ClassLoader` 中的 `Class` 是否存在。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
if("com.example.demo.DemoClass".hasClass(customClassLoader)) {
    // Your code here.
}
```

## Class.hasField <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.hasField(initiate: FieldConditions): Boolean
```

**Change Records**

`v1.0.4` `added`

`v1.0.67` `modified`

合并到 `FieldFinder`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找变量是否存在。

## Class.hasMethod <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.hasMethod(initiate: MethodConditions): Boolean
```

**Change Records**

`v1.0` `first`

`v1.0.1` `modified`

新增 `returnType` 参数

`v1.0.67` `modified`

合并到 `MethodFinder`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找方法是否存在。

## Class.hasConstructor <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.hasConstructor(initiate: ConstructorConditions): Boolean
```

**Change Records**

`v1.0.2` `added`

`v1.0.67` `modified`

合并到 `ConstructorFinder`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找构造方法是否存在。

## Member.hasModifiers <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Member.hasModifiers(conditions: ModifierConditions): Boolean
```

**Change Records**

`v1.0.67` `added`

`v1.0.80` `modified`

将方法体进行 inline

`v1.1.0` `modified`

合并到 `ModifierConditions`

**Function Illustrate**

> 查找 `Member` 中匹配的描述符。

## Class.hasModifiers <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.hasModifiers(conditions: ModifierConditions): Boolean
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 查找 `Class` 中匹配的描述符。

<h2 class="deprecated">obtainStaticFieldAny - method</h2>

**Change Records**

`v1.0` `first`

`v1.0.1` `removed`

<h2 class="deprecated">obtainFieldAny - method</h2>

**Change Records**

`v1.0` `first`

`v1.0.1` `removed`

<h2 class="deprecated">modifyStaticField - method</h2>

**Change Records**

`v1.0` `first`

`v1.0.1` `removed`

<h2 class="deprecated">modifyField - method</h2>

**Change Records**

`v1.0` `first`

`v1.0.1` `removed`

## Class.field <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.field(initiate: FieldConditions): FieldFinder.Result
```

**Change Records**

`v1.0.2` `added`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找并得到变量。

## Class.method <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.method(initiate: MethodConditions): MethodFinder.Result
```

**Change Records**

`v1.0` `first`

`v1.0.1` `modified`

~~`obtainMethod`~~ 更名为 `method`

新增 `returnType` 参数

`v1.0.2` `modified`

合并到 `MethodFinder` 方法体

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找并得到方法。

## Class.constructor <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.constructor(initiate: ConstructorConditions): ConstructorFinder.Result
```

**Change Records**

`v1.0` `first`

`v1.0.1` `modified`

~~`obtainConstructor`~~ 更名为 `constructor`

`v1.0.2` `modified`

合并到 `ConstructorFinder` 方法体

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找并得到构造方法。

<h2 class="deprecated">callStatic - method</h2>

**Change Records**

`v1.0` `first`

`v1.0.1` `modified`

~~`invokeStatic`~~ 更名为 `callStatic`

`v1.0.2` `removed`

<h2 class="deprecated">call - method</h2>

**Change Records**

`v1.0` `first`

`v1.0.1` `modified`

~~`invokeAny`~~ 更名为 `call`

`v1.0.2` `removed`

## Class.generic <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
fun Class<*>.generic(): GenericClass?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前 `Class` 的泛型父类。

如果当前实例不存在泛型将返回 `null`。

## Class.generic <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.generic(initiate: GenericClass.() -> Unit): GenericClass?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前 `Class` 的泛型父类。

如果当前实例不存在泛型将返回 `null`。

## Any.current <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun <reified T : Any> T.current(ignored: Boolean): CurrentClass
```

```kotlin:no-line-numbers
inline fun <reified T : Any> T.current(ignored: Boolean, initiate: CurrentClass.() -> Unit): T
```

**Change Records**

`v1.0.70` `added`

`v1.1.0` `added`

新增 `ignored` 参数，可以忽略在 `CurrentClass` 中出现的异常

新增不使用 `current { ... }` 调用域直接使用 `current()` 得到实例的类操作对象

**Function Illustrate**

> 获得当前实例的类操作对象。

<h2 class="deprecated">Class.buildOfAny - ext-method</h2>

**Change Records**

`v1.0.70` `added`

`v1.0.80` `modified`

将方法体进行 inline

`v1.1.0` `deprecated`

请转移到 `buildOf` 方法

## Class.buildOf <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.buildOf(vararg param: Any?, initiate: ConstructorConditions): Any?
```

```kotlin:no-line-numbers
inline fun <T> Class<*>.buildOf(vararg param: Any?, initiate: ConstructorConditions): T?
```

**Change Records**

`v1.0.70` `added`

`v1.0.80` `modified`

将方法体进行 inline

`v1.1.0` `modified`

加入无泛型方法 `buildOf`

**Function Illustrate**

> 通过构造方法创建新实例，指定类型 `T` 或任意类型 `Any`。

## Class.allMethods <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.allMethods(result: (index: Int, method: Method) -> Unit)
```

**Change Records**

`v1.0.70` `added`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 遍历当前类中的所有方法。

## Class.allConstructors <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.allConstructors(result: (index: Int, constructor: Constructor<*>) -> Unit)
```

**Change Records**

`v1.0.70` `added`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 遍历当前类中的所有构造方法。

## Class.allFields <span class="symbol">- ext-method</span>

```kotlin:no-line-numbers
inline fun Class<*>.allFields(result: (index: Int, field: Field) -> Unit)
```

**Change Records**

`v1.0.70` `added`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 遍历当前类中的所有变量。