## ReflectionFactory *- kt*

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是自定义 `Member` 和 `Class` 相关功能的查找匹配以及 `invoke` 的封装类。

### MembersType *- class*

```kotlin
enum class MembersType
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 定义一个 `Class` 中的 `Member` 类型

#### ALL *- enum*

```kotlin
ALL
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 全部 `Method` 与 `Constructor`。

#### METHOD *- enum*

```kotlin
METHOD
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 全部 `Method`。

#### CONSTRUCTOR *- enum*

```kotlin
CONSTRUCTOR
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 全部 `Constructor`。

### ClassLoader.onLoadClass *- ext-method*

```kotlin
fun ClassLoader.onLoadClass(result: (Class<*>) -> Unit)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 监听当前 `ClassLoader` 的 `ClassLoader.loadClass` 方法装载。

!> 请注意只有当前 `ClassLoader` 有主动使用 `ClassLoader.loadClass` 事件时才能被捕获。

!> 这是一个实验性功能，一般情况下不会用到此方法，不保证不会发生错误。

!> 只能在 (Xposed) 宿主环境使用此功能，其它环境下使用将不生效且会打印警告信息。

**功能示例**

针对一些使用特定 `ClassLoader` 装载 `Class` 的宿主应用，你可以使用此方法来监听 `Class` 加载情况。

!> 为了防止发生问题，你需要<u>**得到一个存在的 `ClassLoader` 实例**</u>来使用此功能。

比如我们在 `PackageParam` 中使用 `appClassLoader`。

> 示例如下

```kotlin
appClassLoader.onLoadClass { clazz ->
    // 得到 clazz 即加载对象
    clazz... // 这里进行你需要的操作
}
```

或使用你得到的存在的 `ClassLoader` 实例，可以通过 Hook 获取。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
customClassLoader?.onLoadClass { clazz ->
    // ...
}
```

在判断到这个 `Class` 被装载成功时，开始执行你的 Hook 功能。

> 示例如下

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

### ~~hookClass *- field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.1.0` `移除`

`HookClass` 相关功能不再对外开放

### ~~normalClass *- field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.1.0` `移除`

`HookClass` 相关功能不再对外开放

### ~~hasClass *- field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.1.0` `移除`

请直接使用 `hasClass()` 无参方法

### Class.hasExtends *- ext-field*

```kotlin
val Class<*>.hasExtends: Boolean
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 当前 `Class` 是否有继承关系，父类是 `Any` 将被认为没有继承关系。

### ~~classOf *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.1.0` `移除`

请转到 `toClass(...)` 方法

### String.toClass *- ext-method*

```kotlin
fun String.toClass(loader: ClassLoader?): Class<*>
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 通过字符串类名转换为 `loader` 中的实体类。

**功能示例**

你可以直接填写你要查找的目标 `Class`，必须在默认 `ClassLoader` 下存在。

> 示例如下

```kotlin
"com.example.demo.DemoClass".toClass()
```

你还可以自定义 `Class` 所在的 `ClassLoader`。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
"com.example.demo.DemoClass".toClass(customClassLoader)
```

### classOf *- method*

```kotlin
inline fun <reified T> classOf(loader: ClassLoader?): Class<*>
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 通过 `T` 得到其 `Class` 实例并转换为实体类。

**功能示例**

我们要获取一个 `Class` 在 `Kotlin` 下不通过反射时应该这样做。

> 示例如下
> 
```kotlin
DemoClass::class.java
```

现在，你可以直接 `cast` 一个实例并获取它的 `Class` 对象，必须在当前 `ClassLoader` 下存在。

> 示例如下

```kotlin
classOf<DemoClass>()
```

若目标存在的 `Class` 为 `stub`，通过这种方式，你还可以自定义 `Class` 所在的 `ClassLoader`。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
classOf<DemoClass>(customClassLoader)
```

### String.hasClass *- ext-method*

```kotlin
fun String.hasClass(loader: ClassLoader?): Boolean
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

支持直接使用空参数方法使用默认 `ClassLoader` 进行判断

**功能描述**

> 通过字符串类名使用指定的 `ClassLoader` 查找是否存在。

**功能示例**

你可以轻松的使用此方法判断字符串中的类是否存在，效果等同于直接使用 `Class.forName`。

> 示例如下

```kotlin
if("com.example.demo.DemoClass".hasClass()) {
    // Your code here.
}
```

填入方法中的 `loader` 参数可判断指定的 `ClassLoader` 中的 `Class` 是否存在。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
if("com.example.demo.DemoClass".hasClass(customClassloader)) {
    // Your code here.
}
```

### Class.hasField *- ext-method*

```kotlin
inline fun Class<*>.hasField(initiate: FieldCondition): Boolean
```

**变更记录**

`v1.0.4` `新增`

`v1.0.67` `修改`

合并到 `FieldFinder`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找变量是否存在。

### Class.hasMethod *- ext-method*

```kotlin
inline fun Class<*>.hasMethod(initiate: MethodConditions): Boolean
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

新增 `returnType` 参数

`v1.0.67` `修改`

合并到 `MethodFinder`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找方法是否存在。

### Class.hasConstructor *- ext-method*

```kotlin
inline fun Class<*>.hasConstructor(initiate: ConstructorConditions): Boolean
```

**变更记录**

`v1.0.2` `新增`

`v1.0.67` `修改`

合并到 `ConstructorFinder`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找构造方法是否存在。

### Member.hasModifiers *- ext-method*

```kotlin
inline fun Member.hasModifiers(conditions: ModifierConditions): Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.0.80` `修改`

将方法体进行 inline

`v1.1.0` `修改`

合并到 `ModifierConditions`

**功能描述**

> 查询 `Member` 中匹配的描述符。

### Class.hasModifiers *- ext-method*

```kotlin
inline fun Class<*>.hasModifiers(conditions: ModifierConditions): Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 查询 `Class` 中匹配的描述符。

### ~~obtainStaticFieldAny *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### ~~obtainFieldAny *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### ~~modifyStaticField *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### ~~modifyField *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### Class.field *- ext-method*

```kotlin
inline fun Class<*>.field(initiate: FieldConditions): FieldFinder.Result
```

**变更记录**

`v1.0.2` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找并得到变量。

### Class.method *- ext-method*

```kotlin
inline fun Class<*>.method(initiate: MethodConditions): MethodFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`obtainMethod`~~ 更名为 `method`

新增 `returnType` 参数

`v1.0.2` `修改`

合并到 `MethodFinder` 方法体

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找并得到方法。

### Class.constructor *- ext-method*

```kotlin
inline fun Class<*>.constructor(initiate: ConstructorConditions): ConstructorFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`obtainConstructor`~~ 更名为 `constructor`

`v1.0.2` `修改`

合并到 `ConstructorFinder` 方法体

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找并得到构造方法。

### ~~callStatic *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`invokeStatic`~~ 更名为 `callStatic`

`v1.0.2` `移除`

### ~~call *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`invokeAny`~~ 更名为 `call`

`v1.0.2` `移除`

### Any.current *- ext-method*

```kotlin
inline fun <reified T : Any> T.current(ignored: Boolean): CurrentClass
```

```kotlin
inline fun <reified T : Any> T.current(ignored: Boolean, initiate: CurrentClass.() -> Unit): T
```

**变更记录**

`v1.0.70` `新增`

`v1.1.0` `新增`

新增 `ignored` 参数，可以忽略在 `CurrentClass` 中出现的异常

新增不使用 `current { ... }` 调用域直接使用 `current()` 得到实例的类操作对象

**功能描述**

> 获得当前实例的类操作对象。

### ~~Class.buildOfAny *- ext-method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

`v1.1.0` `作废`

请转移到 `buildOf` 方法

### Class.buildOf *- ext-method*

```kotlin
inline fun Class<*>.buildOf(vararg param: Any?, initiate: ConstructorConditions): Any?
```

```kotlin
inline fun <T> Class<*>.buildOf(vararg param: Any?, initiate: ConstructorConditions): T?
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

`v1.1.0` `修改`

加入无泛型方法 `buildOf`

**功能描述**

> 通过构造方法创建新实例，指定类型 `T` 或任意类型 `Any`。

### Class.allMethods *- ext-method*

```kotlin
inline fun Class<*>.allMethods(result: (index: Int, method: Method) -> Unit)
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 遍历当前类中的所有方法。

### Class.allConstructors *- ext-method*

```kotlin
inline fun Class<*>.allConstructors(result: (index: Int, constructor: Constructor<*>) -> Unit)
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 遍历当前类中的所有构造方法。

### Class.allFields *- ext-method*

```kotlin
inline fun Class<*>.allFields(result: (index: Int, field: Field) -> Unit)
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 遍历当前类中的所有变量。