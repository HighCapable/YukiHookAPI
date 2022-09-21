# 字节码与反射扩展

> `YukiHookAPI` 为开发者封装了一套接近零反射写法的反射 API，它几乎可以完全取代原生 Java 的反射 API 相关用法。

## Class 扩展

> 这里是 **Class** 对象自身相关的扩展功能。

### 对象转换

假设我们要得到一个不能直接调用的 `Class`，通常情况下，我们可以使用标准的反射 API 去查找这个 `Class`。

> 示例如下

```kotlin
// 默认 ClassLoader 环境下的 Class
var instance = Class.forName("com.demo.Test")
// 指定 ClassLoader 环境下的 Class
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
var instance = customClassLoader?.loadClass("com.demo.Test")
```

这种写法大概不是很友好，此时 `YukiHookAPI` 就为你提供了一个可在任意地方使用的语法糖。

以上写法换做 `YukiHookAPI` 可写作如下形式。

> 示例如下

```kotlin
// 直接得到这个 Class
// 如果当前正处于 PackageParam 环境，那么你可以不需要考虑 ClassLoader
var instance = "com.demo.Test".toClass()
// 自定义 Class 所在的 ClassLoader
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
var instance = "com.demo.Test".toClass(customClassLoader)
```

如果当前 `Class` 并不存在，使用上述方法会抛出异常，如果你不确定 `Class` 是否存在，可以参考下面的解决方案。

> 示例如下

```kotlin
// 直接得到这个 Class
// 如果当前正处于 PackageParam 环境，那么你可以不需要考虑 ClassLoader
// 得不到时结果会为 null 但不会抛出异常
var instance = "com.demo.Test".toClassOrNull()
// 自定义 Class 所在的 ClassLoader
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
// 得不到时结果会为 null 但不会抛出异常
var instance = "com.demo.Test".toClassOrNull(customClassLoader)
```

我们还可以通过映射来得到一个存在的 `Class` 对象。

> 示例如下

```kotlin
// 假设这个 Class 是能够被直接得到的
var instance = classOf<Test>()
// 我们同样可以自定义 Class 所在的 ClassLoader，这对于 stub 来说非常有效
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
var instance = classOf<Test>(customClassLoader)
```

::: tip

更多功能请参考 [classOf](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#classof-method)、[String.toClass](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#string-toclass-ext-method)、[String.toClassOrNull](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#string-toclassornull-ext-method)、[PackageParam → String+VariousClass.toClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#string-variousclass-toclass-i-ext-method)、[PackageParam → String+VariousClass.toClassOrNull](../public/com/highcapable/yukihookapi/hook/param/PackageParam#string-variousclass-toclassornull-i-ext-method) 方法。

:::

### 存在判断

假设我们要判断一个 `Class` 是否存在，通常情况下，我们可以使用标准的反射 API 去查找这个 `Class` 通过异常来判断是否存在。

> 示例如下

```kotlin
// 默认 ClassLoader 环境下的 Class
var isExist = try {
    Class.forName("com.demo.Test")
    true
} catch (_: Throwable) {
    false
}
// 指定 ClassLoader 环境下的 Class
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
var isExist = try {
    customClassLoader?.loadClass("com.demo.Test")
    true
} catch (_: Throwable) {
    false
}
```

这种写法大概不是很友好，此时 `YukiHookAPI` 就为你提供了一个可在任意地方使用的语法糖。

以上写法换做 `YukiHookAPI` 可写作如下形式。

> 示例如下

```kotlin
// 判断这个 Class 是否存在
// 如果当前正处于 PackageParam 环境，那么你可以不需要考虑 ClassLoader
var isExist = "com.demo.Test".hasClass()
// 自定义 Class 所在的 ClassLoader
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
var isExist = "com.demo.Test".hasClass(customClassLoader)
```

::: tip

更多功能请参考 [String.hasClass](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#string-hasclass-ext-method)、[PackageParam → String.hasClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#string-hasclass-i-ext-method) 方法。

:::

### 模糊查找&ensp;<Badge type="tip" text="Beta" vertical="middle" />

在 R8 等工具混淆后的宿主 **Dex** 中的 `Class` 名称将会难以分辨，且不确定其正确位置，不能直接通过 [对象转换](#对象转换) 来得到。

此时就有了 `DexClassFinder`，它的作用是通过需要查找的 `Class` 中的字节码特征来确定这个 `Class` 的实例。

::: warning

目前 **DexClassFinder** 的功能尚在试验阶段，由于仅通过 Java 层实现查找功能，在宿主 **Class** 过多时性能可能不能达到最佳水平，如果发生查找不到、定位有误的问题欢迎向我们反馈。

由于是反射层面的 API，目前它只能通过**类与成员**的特征来定位指定的 **Class**，不能通过指定字节码中的字符串和方法内容特征来进行定位。

查找 **Class** 的速度取决于当前设备的性能，目前主流的移动端处理器在 **10~15w** 数量的 **Class** 中条件不算复杂的情况下大概在 **3~10s** 区间，条件稍微复杂的情况下最快速度能达到 **25s** 以内，匹配到的同类型 **Class** 越多速度越慢。

:::

#### 开始使用

下面是一个简单的用法示例。

假设下面这个 `Class` 是我们想要得到的，其中的名称经过了混淆，在每个版本可能都不一样。

> 示例如下

```java:no-line-numbers
package com.demo;

public class a extends Activity implements Serializable {

    public a(String var1) {
        // ...
    }

    private String a;

    private String b;

    private boolean a;

    protected void onCreate(Bundle var1) {
        // ...
    }

    private static void a(String var1) {
        // ...
    }

    private String a(boolean var1, String var2) {
        // ...
    }

    private void a() {
        // ...
    }

    public void a(boolean var1, a var2, b var3, String var4) {
        // ...
    }
}
```

此时，我们想得到这个 `Class`，可以直接使用 `ClassLoader.searchClass` 方法。

在 `PackageParam` 中，你可以直接使用 `searchClass` 方法，它将自动指定 `appClassLoader`。

下方演示的条件中每一个都是可选的，条件越复杂定位越精确，同时性能也会越差。

> 示例如下

```kotlin
searchClass {
    // 从指定的包名范围开始查找，实际使用时，你可以同时指定多个包名范围
    from("com.demo")
    // 指定当前 Class 的 getSimpleName 的结果，你可以直接对这个字符串进行逻辑判断
    // 这里我们不确定它的名称是不是 a，可以只判断字符串长度
    simpleName { it.length == 1 }
    // 指定继承的父类对象，如果是存在的 stub，可以直接用泛型表示
    extends<Activity>()
    // 指定继承的父类对象，可以直接写为完整类名，你还可以同时指定多个
    extends("android.app.Activity")
    // 指定实现的接口，如果是存在的 stub，可以直接用泛型表示
    implements<Serializable>()
    // 指定实现的接口，可以直接写为完整类名，你还可以同时指定多个
    implements("java.io.Serializable")
    // 指定构造方法的类型与样式，以及在当前类中存在的个数 count
    constructor { param(StringType) }.count(num = 1)
    // 指定变量的类型与样式，以及在当前类中存在的个数 count
    field { type = StringType }.count(num = 2)
    // 指定变量的类型与样式，以及在当前类中存在的个数 count
    field { type = BooleanType }.count(num = 1)
    // 直接指定所有变量在当前类中存在的个数 count
    field().count(num = 3)
    // 如果你认为变量的个数是不确定的，还可以使用如下自定义条件
    field().count(1..3)
    field().count { it >= 3 }
    // 指定方法的类型与样式，以及在当前类中存在的个数 count
    method {
        name = "onCreate"
        param(BundleClass)
    }.count(num = 1)
    // 指定方法的类型与样式，同时指定修饰符，以及在当前类中存在的个数 count
    method {
        modifiers { isStatic && isPrivate }
        param(StringType)
        returnType = UnitType
    }.count(num = 1)
    // 指定方法的类型与样式，同时指定修饰符，以及在当前类中存在的个数 count
    method {
        modifiers { isPrivate && isStatic.not() }
        param(BooleanType, StringType)
        returnType = StringType
    }.count(num = 1)
    // 指定方法的类型与样式，同时指定修饰符，以及在当前类中存在的个数 count
    method {
        modifiers { isPrivate && isStatic.not() }
        emptyParam()
        returnType = UnitType
    }.count(num = 1)
    // 指定方法的类型与样式，同时指定修饰符和模糊类型 VagueType，以及在当前类中存在的个数 count
    method {
        modifiers { isPrivate && isStatic.not() }
        param(BooleanType, VagueType, VagueType, StringType)
        returnType = UnitType
    }.count(num = 1)
    // 直接指定所有方法在当前类中存在的个数 count
    method().count(num = 5)
    // 如果你认为方法的个数是不确定的，还可以使用如下自定义条件
    method().count(1..5)
    method().count { it >= 5 }
    // 直接指定所有成员 (Member) 在当前类中存在的个数 count
    // 成员包括：Field (变量)、Method (方法)、Constructor (构造方法)
    member().count(num = 9)
    // 所有成员中一定存在一个 static 修饰符，可以这样加入此条件
    member {
        modifiers { isStatic }
    }
}.get() // 得到这个 Class 本身的实例，找不到会返回 null
```

::: tip

上述用法中对于 **Field**、**Method**、**Constructor** 的条件用法与 [Member 扩展](#member-扩展) 中的相关用法是一致的，仅有小部分区别。

更多功能请参考 [MemberRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/MemberRules)、[FieldRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/FieldRules)、[MethodRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/MethodRules)、[ConstructorRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/ConstructorRules)。

:::

#### 异步查找

默认情况下 `DexClassFinder` 会使用同步方式查找 `Class`，会阻塞当前线程直到找到或找不到发生异常为止，若查找消耗的时间过长，可能会导致宿主发生 **ANR** 问题。

针对上述问题，我们可以启用异步，只需要加入参数 `async = true`，这将不需要你再次启动一个线程，API 已帮你处理好相关问题。

::: warning

对于异步情况下你需要使用 **wait** 方法来得到结果，**get** 方法将不再起作用。

:::

> 示例如下

```kotlin
searchClass(async = true) {
    // ...
}.wait { class1 ->
    // 得到异步结果
}
searchClass(async = true) {
    // ...
}.wait { class2 ->
    // 得到异步结果
}
```

这样我们的查找过程就是异步运行了，它将不会阻塞主线程，每个查找都将在单独的线程同时进行，可达到并行任务的效果。

#### 本地缓存

由于每次重新打开宿主都会重新进行查找，在宿主版本不变的情况下这是一种重复性能浪费。

此时我们可以通过指定 `name` 参数来对当前宿主版本的查找结果进行本地缓存，下一次将直接从本地缓存中读取查找到的类名。

本地缓存使用的是 `SharedPreferences`，它将被保存到宿主的数据目录中，在宿主版本更新后会重新进行缓存。

启用本地缓存后，将同时设置 `async = true`，你可以不需要再手动进行设置。

> 示例如下

```kotlin
searchClass(name = "com.demo.class1") {
    // ...
}.wait { class1 ->
    // 得到异步结果
}
searchClass(name = "com.demo.class2") {
    // ...
}.wait { class2 ->
    // 得到异步结果
}
```

如果你想手动清除本地缓存，可以使用如下方法清除当前版本的宿主缓存。

> 示例如下

```kotlin
// 直接调用，在宿主的 appContext 为空时可能会失败，失败会打印警告信息
DexClassFinder.clearCache()
// 监听宿主的生命周期后调用
onAppLifecycle {
    onCreate {
        DexClassFinder.clearCache(context = this)
    }
}
```

你还可以清除指定版本的宿主缓存。

> 示例如下

```kotlin
// 直接调用，在宿主的 appContext 为空时可能会失败，失败会打印警告信息
DexClassFinder.clearCache(versionName = "1.0", versionCode = 1)
// 监听宿主的生命周期后调用
onAppLifecycle {
    onCreate {
        DexClassFinder.clearCache(context = this, versionName = "1.0", versionCode = 1)
    }
}
```

#### 多重查找

如果你需要使用固定的条件同时查找一组 `Class`，那么你只需要使用 `all` 或 `waitAll` 方法来得到结果。

```kotlin
// 同步查找，使用 all 得到条件全部查找到的结果
searchClass {
    // ...
}.all().forEach { clazz ->
    // 得到每个结果
}
// 同步查找，使用 all { ... } 遍历每个结果
searchClass {
    // ...
}.all { clazz ->
    // 得到每个结果
}
// 异步查找，使用 waitAll 得到条件全部查找到的结果
searchClass(async = true) {
    // ...
}.waitAll { classes ->
    classes.forEach {
        // 得到每个结果
    }
}
```

::: tip

更多功能请参考 [ClassLoader.searchClass](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#classloader-searchclass-ext-method)、[PackageParam.searchClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#searchclass-method) 方法。

:::

## Member 扩展

> 这里是 **Class** 字节码成员变量 **Field**、**Method**、**Constructor** 相关的扩展功能。

::: tip

**Member** 是 **Field**、**Method**、**Constructor** 的接口描述对象，它在 Java 反射中为 **Class** 中字节码成员的总称。

:::

假设有一个这样的 `Class`。

> 示例如下

```java:no-line-numbers
package com.demo;

public class BaseTest {

    public BaseTest() {
        // ...
    }

    public BaseTest(boolean isInit) {
        // ...
    }

    private void doBaseTask(String taskName) {
        // ...
    }
}
```

```java:no-line-numbers
package com.demo;

public class Test extends BaseTest {

    public Test() {
        // ...
    }

    public Test(boolean isInit) {
        // ...
    }

    private static TAG = "Test";

    private BaseTest baseInstance;

    private String a;

    private boolean a;

    private boolean isTaskRunning = false;

    private static void init() {
        // ...
    }

    private void doTask(String taskName) {
        // ...
    }

    private void release(String taskName, Function<boolean, String> task, boolean isFinish) {
        // ...
    }

    private void stop() {
        // ...
    }

    private String getName() {
        // ...
    }

    private void b() {
        // ...
    }

    private void b(String a) {
        // ...
    }
}
```

### 查找与反射调用

假设我们要得到 `Test`(以下统称“当前 `Class`”)的 `doTask` 方法并执行，通常情况下，我们可以使用标准的反射 API 去查找这个方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用反射 API 调用并执行
Test::class.java
    .getDeclaredMethod("doTask", String::class.java)
    .apply { isAccessible = true }
    .invoke(instance, "task_name")
```

这种写法大概不是很友好，此时 `YukiHookAPI` 就为你提供了一个可在任意地方使用的语法糖。

以上写法换做 `YukiHookAPI` 可写作如下形式。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doTask"
    param(StringType)
}.get(instance).call("task_name")
```

::: tip

更多功能请参考 [MethodFinder](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder)。

:::

同样地，我们需要得到 `isTaskRunning` 变量也可以写作如下形式。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.field {
    name = "isTaskRunning"
    type = BooleanType
}.get(instance).any() // any 为 Field 的任意类型实例化对象
```

::: tip

更多功能请参考 [FieldFinder](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder)。

:::

也许你还想得到当前 `Class` 的构造方法，同样可以实现。

> 示例如下

```kotlin
Test::class.java.constructor {
    param(BooleanType)
}.get().call(true) // 可创建一个新的实例
```

若想得到的是 `Class` 的无参构造方法，可写作如下形式。

> 示例如下

```kotlin
Test::class.java.constructor().get().call() // 可创建一个新的实例
```

::: tip

更多功能请参考 [ConstructorFinder](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder)。

:::

### 可选的查找条件

假设我们要得到 `Class` 中的 `getName` 方法，可以使用如下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "getName"
    emptyParam()
    returnType = StringType
}.get(instance).string() // 得到方法的结果
```

通过观察发现，这个 `Class` 中只有一个名为 `getName` 的方法，那我们可不可以再简单一点呢？

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "getName"
    emptyParam()
}.get(instance).string() // 得到方法的结果
```

是的，对于确切不会变化的方法，你可以精简查找条件。

在只使用 `get` 或 `wait` 方法得到结果时 `YukiHookAPI` **会默认按照字节码顺序匹配第一个查找到的结果**。

问题又来了，这个 `Class` 中有一个 `release` 方法，但是它的方法参数很长，而且部分类型可能无法直接得到。

通常情况下我们会使用 `param(...)` 来查找这个方法，但是有没有更简单的方法呢。

此时，在确定方法唯一性后，你可以使用 `paramCount` 来查找到这个方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "release"
    // 此时我们不必确定方法参数具体类型，写个数就好
    paramCount = 3
}.get(instance) // 得到这个方法
```

上述示例虽然能够匹配成功，但是不精确，此时你还可以使用 `VagueType` 来填充你不想填写的方法参数类型。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "release"
    // 使用 VagueType 来填充不想填写的类型，同时保证其它类型能够匹配
    param(StringType, VagueType, BooleanType)
}.get(instance) // 得到这个方法
```

### 在父类查找

你会注意到 `Test` 继承于 `BaseTest`，现在我们想得到 `BaseTest` 的 `doBaseTask` 方法，在不知道父类名称的情况下，要怎么做呢？

参照上面的查找条件，我们只需要在查找条件中加入一个 `superClass` 即可实现这个功能。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doBaseTask"
    param(StringType)
    // 只需要添加这个条件
    superClass()
}.get(instance).call("task_name")
```

这个时候我们就可以在父类中取到这个方法了。

`superClass` 有一个参数为 `isOnlySuperClass`，设置为 `true` 后，可以跳过当前 `Class` 仅查找当前 `Class` 的父类。

由于我们现在已知 `doBaseTask` 方法只存在于父类，可以加上这个条件节省查找时间。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doBaseTask"
    param(StringType)
    // 加入一个查找条件
    superClass(isOnlySuperClass = true)
}.get(instance).call("task_name")
```

这个时候我们同样可以得到父类中的这个方法。

`superClass` 一旦设置就会自动循环向后查找全部继承的父类中是否有这个方法，直到查找到目标没有父类(继承关系为 `java.lang.Object`)为止。

::: tip

更多功能请参考 [MethodFinder.superClass](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#superclass-method)、[ConstructorFinder.superClass](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#superclass-method)、[FieldFinder.superClass](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder#superclass-method) 方法。

:::

::: danger

当前查找的 **Method** 除非指定 **superClass** 条件，否则只能查找到当前 **Class** 的 **Method**，这是 Java 反射 API 的默认行为。

:::

### 模糊查找

如果我们想查找一个方法名称，但是又不确定它在每个版本中是否发生变化，此时我们就可以使用模糊查找功能。

假设我们要得到 `Class` 中的 `doTask` 方法，可以使用如下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 设置名称不区分大小写
        it.equals("dotask", isIgnoreCase = true)
    }
    param(StringType)
}.get(instance).call("task_name")
```

已知当前 `Class` 中仅有一个 `doTask` 方法，我们还可以判断方法名称仅包含其中指定的字符。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 仅包含 oTas
        it.contains("oTas")
    }
    param(StringType)
}.get(instance).call("task_name")
```

我们还可以根据首尾字符串进行判断。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 开头包含 do，结尾包含 Task
        it.startsWith("do") && it.endsWith("Task")
    }
    param(StringType)
}.get(instance).call("task_name")
```

通过观察发现这个方法名称中只包含字母，我们还可以再增加一个精确的查找条件。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 开头包含 do，结尾包含 Task，仅包含字母
        it.startsWith("do") && it.endsWith("Task") && it.isOnlyLetters()
    }
    param(StringType)
}.get(instance).call("task_name")
```

::: tip

使用 **name { ... }** 创建一个条件方法体，其中的变量 **it** 即当前名称的字符串，此时你就可以在 **NameRules** 的扩展方法中自由使用其中的功能。

方法体末尾条件需要返回一个 **Boolean**，即最终的条件判断结果。

更多功能请参考 [NameRules](../public/com/highcapable/yukihookapi/hook/core/finder/base/rules/NameRules)。

:::

### 多重查找

有些时候，我们可能需要查找一个 `Class` 中具有相同特征的一组方法、构造方法、变量，此时，我们就可以利用相对条件匹配来完成。

在查找条件结果的基础上，我们只需要把 `get` 换为 `all` 即可得到匹配条件的全部字节码。

假设这次我们要得到 `Class` 中方法参数个数范围在 `1..3` 的全部方法，可以使用如下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    paramCount(1..3)
}.all(instance).forEach { instance ->
    // 调用执行每个方法
    instance.call(...)
}
```

上述示例可完美匹配到如下 3 个方法。

`private void doTask(String taskName)`

`private void release(String taskName, Function<boolean, String> task, boolean isFinish)`

`private void b(String a)`

如果你想更加自由地定义参数个数范围的条件，可以使用如下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    paramCount { it < 3 }
}.all(instance).forEach { instance ->
    // 调用执行每个方法
    instance.call(...)
}
```

上述示例可完美匹配到如下 6 个方法。

`private static void init()`

`private void doTask(String taskName)`

`private void stop(String a)`

`private void getName(String a)`

`private void b()`

`private void b(String a)`

通过观察 `Class` 中有两个名称为 `b` 的方法，可以使用如下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "b"
}.all(instance).forEach { instance ->
    // 调用执行每个方法
    instance.call(...)
}
```

上述示例可完美匹配到如下 2 个方法。

`private void b()`

`private void b(String a)`

::: tip

使用 **paramCount { ... }** 创建一个条件方法体，其中的变量 **it** 即当前参数个数的整数，此时你就可以在 **CountRules** 的扩展方法中自由使用其中的功能。

方法体末尾条件需要返回一个 **Boolean**，即最终的条件判断结果。

更多功能请参考 [CountRules](../public/com/highcapable/yukihookapi/hook/core/finder/base/rules/CountRules)。

:::

### 静态字节码

有些方法和变量在 `Class` 中是静态的实现，这个时候，我们不需要传入实例就可以调用它们。

假设我们这次要得到静态变量 `TAG` 的内容。

> 示例如下

```kotlin
Test::class.java.field {
    name = "TAG"
    type = StringType
}.get().string() // Field 的类型是字符串，可直接进行 cast
```

假设 `Class` 中存在同名的非静态 `TAG` 变量，这个时候怎么办呢？

加入一个筛选条件即可。

> 示例如下

```kotlin
Test::class.java.field {
    name = "TAG"
    type = StringType
    // 标识查找的这个变量需要是静态
    modifiers { isStatic }
}.get().string() // Field 的类型是字符串，可直接进行 cast
```

我们还可以调用名为 `init` 的静态方法。

> 示例如下

```kotlin
Test::class.java.method {
    name = "init"
    emptyParam()
}.get().call()
```

同样地，你可以标识它是一个静态。

> 示例如下

```kotlin
Test::class.java.method {
    name = "init"
    emptyParam()
    // 标识查找的这个方法需要是静态
    modifiers { isStatic }
}.get().call()
```

::: tip

使用 **modifiers { ... }** 创建一个条件方法体，此时你就可以在 **ModifierRules** 中自由使用其中的功能。

方法体末尾条件需要返回一个 **Boolean**，即最终的条件判断结果。

更多功能请参考 [ModifierRules](../public/com/highcapable/yukihookapi/hook/core/finder/base/rules/ModifierRules)。

:::

### 混淆的字节码

你可能已经注意到了，这里给出的示例 `Class` 中有两个混淆的变量名称，它们都是 `a`，这个时候我们要怎么得到它们呢？

有两种方案。

第一种方案，确定变量的名称和类型。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.field {
    name = "a"
    type = BooleanType
}.get(instance).any() // 得到名称为 a 类型为 Boolean 的变量
```

第二种方案，确定变量的类型所在的位置。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.field {
    type(BooleanType).index().first()
}.get(instance).any() // 得到第一个类型为 Boolean 的变量
```

以上两种情况均可得到对应的变量 `private boolean a`。

同样地，这个 `Class` 中也有两个混淆的方法名称，它们都是 `b`。

你也可以有两种方案来得到它们。

第一种方案，确定方法的名称和方法参数。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "b"
    param(StringType)
}.get(instance).call("test_string") // 得到名称为 b 方法参数为 [String] 的方法
```

第二种方案，确定方法的参数所在的位置。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    param(StringType).index().first()
}.get(instance).call("test_string") // 得到第一个方法参数为 [String] 的方法
```

由于观察到这个方法在 `Class` 的最后一个，那我们还有一个备选方案。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    order().index().last()
}.get(instance).call("test_string") // 得到当前 Class 的最后一个方法
```

::: warning

请尽量避免使用 **order** 来筛选字节码的下标，它们可能是不确定的，除非你确定它在这个 **Class** 中的位置一定不会变。

:::

### 直接调用

上面介绍的调用字节码的方法都需要使用 `get(instance)` 才能调用对应的方法，有没有简单一点的办法呢？

此时，你可以在任意实例上使用 `current` 方法来创建一个调用空间。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 假设这个 Class 是不能被直接得到的
instance.current {
    // 执行 doTask 方法
    method {
        name = "doTask"
        param(StringType)
    }.call("task_name")
    // 执行 stop 方法
    method {
        name = "stop"
        emptyParam()
    }.call()
    // 得到 name
    val name = method { name = "getName" }.string()
}
```

我们还可以用 `superClass` 调用当前 `Class` 父类的方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 假设这个 Class 是不能被直接得到的
instance.current {
    // 执行父类的 doBaseTask 方法
    superClass().method {
        name = "doBaseTask"
        param(StringType)
    }.call("task_name")
}
```

如果你不喜欢使用一个大括号的调用域来创建当前实例的命名空间，你可以直接使用 `current()` 方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例，这个 Class 是不能被直接得到的
val instance = Test()
// 执行 doTask 方法
instance
    .current()
    .method {
        name = "doTask"
        param(StringType)
    }.call("task_name")
// 执行 stop 方法
instance
    .current()
    .method {
        name = "stop"
        emptyParam()
    }.call()
// 得到 name
val name = instance.current().method { name = "getName" }.string()
```

同样地，它们之间可以连续调用，但<u>**不允许内联调用**</u>。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 假设这个 Class 是不能被直接得到的
instance.current {
    method {
        name = "doTask"
        param(StringType)
    }.call("task_name")
}.current()
    .method {
        name = "stop"
        emptyParam()
    }.call()
// ❗注意，因为 current() 返回的是 CurrentClass 自身对象，所以不能像下面这样调用
instance.current().current()
```

针对 `Field` 实例，还有一个便捷的方法，可以直接获取 `Field` 所在实例的对象。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 假设这个 Class 是不能被直接得到的
instance.current {
    // <方案1>
    field {
        name = "baseInstance"
    }.current {
        method {
            name = "doBaseTask"
            param(StringType)
        }.call("task_name")
    }
    // <方案2>
    field {
        name = "baseInstance"
    }.current()
        ?.method {
            name = "doBaseTask"
            param(StringType)
        }?.call("task_name")
}
```

::: warning

上述 **current** 方法相当于帮你调用了 **CurrentClass** 中的 **field { ... }.any()?.current()** 方法。

若不存在 **CurrentClass** 调用域，你需要使用 **field { ... }.get(instance).current()** 来进行调用。

:::

问题又来了，我想使用反射的方式创建如下的实例并调用其中的方法，该怎么做呢？

> 示例如下

```kotlin
Test(true).doTask("task_name")
```

通常情况下，我们可以使用标准的反射 API 来调用。

> 示例如下

```kotlin
"com.demo.Test".toClass()
    .getDeclaredConstructor(Boolean::class.java)
    .apply { isAccessible = true }
    .newInstance(true)
    .apply {
        javaClass
            .getDeclaredMethod("doTask", String::class.java)
            .apply { isAccessible = true }
            .invoke(this, "task_name")
    }
```

但是感觉这种做法好麻烦，有没有更简洁的调用方法呢？

这个时候，我们还可以借助 `buildOf` 方法来创建一个实例。

> 示例如下

```kotlin
"com.demo.Test".toClass().buildOf(true) { param(BooleanType) }?.current {
    method {
        name = "doTask"
        param(StringType)
    }.call("task_name")
}
```

若你希望 `buildOf` 方法返回当前实例的类型，你可以在其中加入类型泛型声明，而无需使用 `as` 来 `cast` 目标类型。

这种情况多用于实例本身的构造方法是私有的，但是里面的方法是公有的，这样我们只需要对其构造方法进行反射创建即可。

> 示例如下

```kotlin
// 假设这个 Class 是能够直接被得到的
val test = Test::class.java.buildOf<Test>(true) { param(BooleanType) }
test.doTask("task_name")
```

::: tip

更多功能请参考 [CurrentClass](../public/com/highcapable/yukihookapi/hook/bean/CurrentClass) 以及 [Class.buildOf](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#class-buildof-ext-method) 方法。

:::

### 原始调用

若你正在使用反射调用的一个方法是被 Hook 过的，此时我们如何调用其原始方法呢？

原生的 `XposedBridge` 为我们提供了一个 `XposedBridge.invokeOriginalMethod` 功能。

现在，在 `YukiHookAPI` 中你可以使用如下方法便捷地实现这个功能。

假设下面是我们要演示的 `Class`。

> 示例如下

```java:no-line-numbers
public class Test {

    public static String getString() {
        return "Original";
    }
}
```

下面是 Hook 这个 `Class` 中 `getString` 方法的方式。

> 示例如下

```kotlin
Test::class.java.hook {
    injectMember {
        method {
            name = "getString"
            emptyParam()
            returnType = StringType
        }
        replaceTo("Hooked")
    }
}
```

此时，我们再使用反射调用这个方法，则会得到 Hook 后的结果 `"Hooked"`。

> 示例如下

```kotlin
// result 的结果会是 "Hooked"
val result = Test::class.java.method {
    name = "getString"
    emptyParam()
    returnType = StringType
}.get().string()
```

如果我们想得到这个方法未经 Hook 的原始方法及结果，只需要在结果中加入 `original` 即可。

> 示例如下

```kotlin
// result 的结果会是 "Original"
val result = Test::class.java.method {
    name = "getString"
    emptyParam()
    returnType = StringType
}.get().original().string()
```

::: tip

更多功能请参考 [MethodFinder.Result.original](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#original-method) 方法。

:::

### 再次查找

假设有三个不同版本的 `Class`，它们都是这个宿主不同版本相同的 `Class`。

这里面同样都有一个方法 `doTask`，假设它们的功能是一样的。

> 版本 A 示例如下

```java:no-line-numbers
public class Test {

    public void doTask() {
        // ...
    }
}
```

> 版本 B 示例如下

```java:no-line-numbers
public class Test {

    public void doTask(String taskName) {
        // ...
    }
}
```

> 版本 C 示例如下

```java:no-line-numbers
public class Test {

    public void doTask(String taskName, int type) {
        // ...
    }
}
```

我们需要在不同的版本中得到这个相同功能的 `doTask` 方法，要怎么做呢？

此时，你可以使用 `RemedyPlan` 完成你的需求。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doTask"
    emptyParam()
}.remedys {
    method {
        name = "doTask"
        param(StringType)
    }.onFind {
        // 可在这里实现找到的逻辑
    }
    method {
        name = "doTask"
        param(StringType, IntType)
    }.onFind {
        // 可在这里实现找到的逻辑
    }
}.wait(instance) {
    // 得到方法的结果
}
```

::: danger

使用了 **RemedyPlan** 的方法查找结果不能再使用 **get** 的方式得到方法实例，应当使用 **wait** 方法。

:::

另外，你还可以在使用 [多重查找](#多重查找) 的情况下继续使用 `RemedyPlan`。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doTask"
    emptyParam()
}.remedys {
    method {
        name = "doTask"
        paramCount(0..1)
    }.onFind {
        // 可在这里实现找到的逻辑
    }
    method {
        name = "doTask"
        paramCount(1..2)
    }.onFind {
        // 可在这里实现找到的逻辑
    }
}.waitAll(instance) {
    // 得到方法的结果
}
```

以当前 `Class` 举例，若 [多重查找](#多重查找) 结合 `RemedyPlan` 在创建 Hook 的时候使用，你需要稍微改变一下用法。

> 示例如下

```kotlin
injectMember {
    method {
        name = "doTask"
        emptyParam()
    }.remedys {
        method {
            name = "doTask"
            paramCount(0..1)
        }
        method {
            name = "doTask"
            paramCount(1..2)
        }
    }.all()
    beforeHook {}
    afterHook {}
}
```

::: tip

在创建 Hook 的时候使用可参考 [MethodFinder.Process.all](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#all-method)、[ConstructorFinder.Process.all](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#all-method)。

更多功能请参考 [MethodFinder.RemedyPlan](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#remedyplan-class)、[ConstructorFinder.RemedyPlan](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#remedyplan-class)、[FieldFinder.RemedyPlan](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder#remedyplan-class)。

:::

### 相对匹配

假设宿主中不同版本中存在功能相同的 `Class` 但仅有 `Class` 的名称不一样。

> 版本 A 示例如下

```java:no-line-numbers
public class ATest {

    public static void doTask() {
        // ...
    }
}
```

> 版本 B 示例如下

```java:no-line-numbers
public class BTest {

    public static void doTask() {
        // ...
    }
}
```

这个时候我们想在每个版本都调用这个 `Class` 里的 `doTask` 方法该怎么做呢？

通常做法是判断 `Class` 是否存在。

> 示例如下

```kotlin
// 首先查找到这个 Class
val currentClass = 
    if("com.demo.ATest".hasClass()) "com.demo.ATest".toClass() else "com.demo.BTest".toClass()
// 然后再查找这个方法并调用
currentClass.method {
    name = "doTask"
    emptyParam()
}.get().call()
```

感觉这种方案非常的不优雅且繁琐，那么此时 `YukiHookAPI` 就为你提供了一个非常方便的 `VariousClass` 专门来解决这个问题。

现在，你可以直接使用以下方式获取到这个 `Class`。

> 示例如下

```kotlin
VariousClass("com.demo.ATest", "com.demo.BTest").get().method {
    name = "doTask"
    emptyParam()
}.get().call()
```

若当前 `Class` 在指定的 `ClassLoader` 中存在，你可以在 `get` 中填入你的 `ClassLoader`。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
VariousClass("com.demo.ATest", "com.demo.BTest").get(customClassLoader).method {
    name = "doTask"
    emptyParam()
}.get().call()
```

若你正在 `PackageParam` 中操作 (Xposed) 宿主环境的 `Class`，可以直接使用 `toClass()` 进行设置。

> 示例如下

```kotlin
VariousClass("com.demo.ATest", "com.demo.BTest").toClass().method {
    name = "doTask"
    emptyParam()
}.get().call()
```

::: tip

更多功能请参考 [VariousClass](../public/com/highcapable/yukihookapi/hook/bean/VariousClass)。

:::

若在创建 Hook 的时候使用，可以更加方便，还可以自动拦截找不到 `Class` 的异常。

> 示例如下

```kotlin
findClass("com.demo.ATest", "com.demo.BTest").hook {
    // Your code here.
}
```

你还可以把这个 `Class` 定义为一个常量类型来使用。

> 示例如下

```kotlin
// 定义常量类型
val ABTestClass = VariousClass("com.demo.ATest", "com.demo.BTest")
// 直接使用
ABTestClass.hook {
    // Your code here.
}
```

::: tip

更多功能请参考 [PackageParam.findClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#findclass-method) 方法。

:::

### 调用泛型

在反射过程中，我们可能会遇到泛型问题，在泛型的反射处理上，`YukiHookAPI` 同样提供了一个可在任意地方使用的语法糖。

例如我们有如下的泛型类。

> 示例如下

```kotlin
class TestGeneric<T, R> (t: T, r: R) {

    fun foo() {
        // ...
    }
}
```

当我们想在当前 `Class` 中获得泛型 `T` 或 `R` 的 `Class` 实例，只需要如下实现。

> 示例如下

```kotlin
class TestGeneric<T, R> (t: T, r: R) {

    fun foo() {
        // 获得当前实例的操作对象
        // 获得 T 的 Class 实例，在参数第 0 位，默认值可以不写
        val tClass = current().generic()?.argument()
        // 获得 R 的 Class 实例，在参数第 1 位
        val rClass = current().generic()?.argument(index = 1)
        // 你还可以使用如下写法
        current().generic {
             // 获得 T 的 Class 实例，在参数第 0 位，默认值可以不写
            val tClass = argument()
            // 获得 R 的 Class 实例，在参数第 1 位
            val rClass = argument(index = 1)
        }
    }
}
```

当我们想在外部调用这个 `Class` 时，就可以有如下实现。

> 示例如下

```kotlin
// 假设这个就是 T 的 Class
class TI {

    fun foo() {
        // ...
    }
}
// 假设这个就是 T 的实例
val tInstance: TI? = ...
// 获得 T 的 Class 实例，在参数第 0 位，默认值可以不写，并获得其中的方法 foo 并调用
TestGeneric::class.java.generic()?.argument()?.method {
    name = "foo"
    emptyParam()
}?.get(tInstance)?.invoke<TI>()
```

::: tip

更多功能请参考 [CurrentClass.generic](../public/com/highcapable/yukihookapi/hook/bean/CurrentClass#generic-method)、[Class.generic](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#class-generic-ext-method) 方法以及 [GenericClass](../public/com/highcapable/yukihookapi/hook/bean/GenericClass)。

:::

### 注意误区

> 这里列举了使用时可能会遇到的误区部分，可供参考。

#### 限制性查找条件

::: danger

在查找条件中，除了 **order** 你只能使用一次 **index** 功能。

:::

> 示例如下

```kotlin
method {
    name = "test"
    param(BooleanType).index(num = 2)
    // ❗错误的使用方法，请仅保留一个 index 方法
    returnType(StringType).index(num = 1)
}
```

以下查找条件的使用是没有任何问题的。

> 示例如下

```kotlin
method {
    name = "test"
    param(BooleanType).index(num = 2)
    order().index(num = 1)
}
```

#### 必要的查找条件

::: danger

在普通方法查找条件中，<u>**即使是无参的方法也需要设置查找条件**</u>。

:::

假设我们有如下的 `Class`。

> 示例如下

```java:no-line-numbers
public class TestFoo {

    public void foo(String string) {
        // ...
    }

    public void foo() {
        // ...
    }
}
```

我们要得到其中的 `public void foo()` 方法，可以写作如下形式。

> 示例如下

```kotlin
TestFoo::class.java.method {
    name = "foo"
}
```

但是，上面的例子<u>**是错误的**</u>。

你会发现这个 `Class` 中有两个 `foo` 方法，其中一个带有方法参数。

由于上述例子没有设置 `param` 的查找条件，得到的结果将会是匹配名称且匹配字节码顺序的第一个方法 `public void foo(String string)`，而不是我们需要的最后一个方法。

这是一个**经常会出现的错误**，**没有方法参数就会丢失方法参数查找条件**的使用问题。

正确的使用方法如下。

> 示例如下

```kotlin
TestFoo::class.java.method {
    name = "foo"
    // ✅ 正确的使用方法，添加详细的筛选条件
    emptyParam()
}
```

至此，上述的示例将可以完美地匹配到 `public void foo()` 方法。

::: tip 兼容性说明

在过往历史版本的 API 中是允许匹配不写默认匹配无参方法的做法的，但是最新版本更正了这一问题，请确保你使用的是最新的 API 版本。

:::

#### 可简写查找条件

> 在构造方法查找条件中，<u>**无参的构造方法可以不需要填写查找条件**</u>。

假设我们有如下的 `Class`。

> 示例如下

```java:no-line-numbers
public class TestFoo {

    public TestFoo() {
        // ...
    }
}
```

我们要得到其中的 `public TestFoo()` 构造方法，可以写作如下形式。

> 示例如下

```kotlin
TestFoo::class.java.constructor { emptyParam() }
```

上面的例子可以成功获取到 `public TestFoo()` 构造方法，但是感觉有一些繁琐。

与普通方法不同，由于构造方法不需要考虑 `name` 名称，当构造方法没有参数的时候，我们可以省略 `emptyParam` 参数。

> 示例如下

```kotlin
TestFoo::class.java.constructor()
```

::: tip 兼容性说明

在过往历史版本的 API 中构造方法不填写任何查找参数会直接找不到构造方法，<u>**这是一个 BUG，最新版本已经进行修复**</u>，请确保你使用的是最新的 API 版本。

:::

#### 字节码类型

::: danger

在字节码调用结果中，**cast** 方法只能指定字节码对应的类型。

:::

例如我们想得到一个 `Boolean` 类型的变量，把他转换为 `String`。

以下是错误的使用方法。

> 示例如下

```kotlin
field {
    name = "test"
    type = BooleanType
}.get().string() // ❗错误的使用方法，必须 cast 为字节码目标类型
```

以下是正确的使用方法。

> 示例如下

```kotlin
field {
    name = "test"
    type = BooleanType
}.get().boolean().toString() // ✅ 正确的使用方法，得到类型后再进行转换
```

## 常用类型扩展

在查找方法、变量的时候我们通常需要指定所查找的类型。

> 示例如下

```kotlin
field {
    name = "test"
    type = Boolean::class.java
}
```

在 `Kotlin` 中表达出 `Boolean::class.java` 这个类型的写法很长，感觉并不方便。

因此，`YukiHookAPI` 为开发者封装了常见的类型调用，其中包含了 Android 的基本类型和 Java 的基本类型。

这个时候上面的类型就可以写作如下形式了。

> 示例如下

```kotlin
field {
    name = "test"
    type = BooleanType
}
```

在 Java 中常见的基本类型都已被封装为 **类型 + Type** 的方式，例如 `IntType`、`FloatType`。

相应地，数组类型也有方便的使用方法，假设我们要获得 `String[]` 类型的数组。

需要写做 `java.lang.reflect.Array.newInstance(String::class.java, 0).javaClass` 才能得到这个类型。

感觉是不是很麻烦，这个时候我们可以使用扩展方法 `ArrayClass(StringType)` 来得到这个类型。

同时由于 `String` 是常见类型，所以还可以直接使用 `StringArrayClass` 来得到这个类型。

一些常见的 Hook 中查找的方法，都有其对应的封装类型以供使用，格式为 **类型 + Class**。

例如 Hook `onCreate` 方法需要查找 `Bundle::class.java` 类型。

> 示例如下

```kotlin
method {
    name = "onCreate"
    param(BundleClass)
}
```

::: tip

更多类型可查看 [ComponentTypeFactory](../public/com/highcapable/yukihookapi/hook/type/android/ComponentTypeFactory)、[GraphicsTypeFactory](../public/com/highcapable/yukihookapi/hook/type/android/GraphicsTypeFactory)、[ViewTypeFactory](../public/com/highcapable/yukihookapi/hook/type/android/ViewTypeFactory)、[VariableTypeFactory](../public/com/highcapable/yukihookapi/hook/type/java/VariableTypeFactory)。 

:::

同时，欢迎你能贡献更多的常用类型。