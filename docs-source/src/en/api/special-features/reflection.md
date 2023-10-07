# Reflection Extensions

> `YukiHookAPI` encapsulates a set of reflection API with near-zero reflection writing for developers, which can almost completely replace the usage of reflection API in Java.

The core part of this functionality has been decoupled into the [YukiReflection](https://github.com/fankes/YukiReflection) project, which can be used independently in any Java or Android project.

Now `YukiReflection` is integrated into `YukiHookAPI` as a core dependency.

`YukiHookAPI` adds related extensions for Hook functions on the basis of `YukiReflection`, and there is no need to introduce this dependency to use `YukiHookAPI`.

## Class Extensions

> Here are the extension functions related to the **Class** object itself.

### Object Conversion

Suppose we want to get a `Class` that cannot be called directly.

Normally, we can use the standard reflection API to find this `Class`.

> The following example

```kotlin
// Class in the default ClassLoader environment
var instance = Class.forName("com.demo.Test")
// Specify the Class in the ClassLoader environment
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
var instance = customClassLoader?.loadClass("com.demo.Test")
```

This is probably not very friendly, and `YukiHookAPI` provides you with a syntactic sugar that can be used anywhere.

The above writing can be written as `YukiHookAPI` as follows.

> The following example

```kotlin
// Get this Class directly
// If you are currently in the PackageParam environment, then you don't need to consider ClassLoader
var instance = "com.demo.Test".toClass()
// ClassLoader where the custom Class is located
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
var instance = "com.demo.Test".toClass(customClassLoader)
```

If the current `Class` does not exist, using the above method will throw an exception.

If you are not sure whether the `Class` exists, you can refer to the following solutions.

> The following example

```kotlin
// Get this Class directly
// If you are currently in the PackageParam environment, then you don't need to consider ClassLoader
// If not available, the result will be null but no exception will be thrown
var instance = "com.demo.Test".toClassOrNull()
// ClassLoader where the custom Class is located
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
// If not available, the result will be null but no exception will be thrown
var instance = "com.demo.Test".toClassOrNull(customClassLoader)
```

We can also get an existing `Class` object by mapping.

> The following example

```kotlin
// Assume this Class can be obtained directly
var instance = classOf<Test>()
// We can also customize the ClassLoader where the Class is located, which is very effective for stubs
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
var instance = classOf<Test>(customClassLoader)
```

::: tip

For more functions, please refer to [classOf](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#classof-method), [String.toClass](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#string-toclass-ext-method), [String.toClassOrNull](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#string-toclassornull-ext-method), [PackageParam → String+ VariousClass.toClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#string-variousclass-toclass-i-ext-method), [PackageParam → String+VariousClass.toClassOrNull](../public/com/highcapable/yukihookapi/hook/param/PackageParam#string-variousclass-toclassornull-i-ext-method) methods.

:::

### Lazy Loading

Suppose we want to get a `Class` that cannot be called directly, but we do not need this `Class` immediately.

At this time, you can use `lazyClass` to complete this function.

> The following example

```kotlin
// Lazy loading of this Class
// If you are currently in a PackageParam environment, then you do not need to consider ClassLoader
val instance by lazyClass("com.demo.Test")
// Customize the ClassLoader where the Class is located
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
val instance by lazyClass("com.demo.Test") { customClassLoader }
// Call this Class at the appropriate time
instance.method {
    // ...
}
```

If the current `Class` does not exist, using the above method will throw an exception.

If you are not sure whether `Class` exists, you can refer to the following solution.

> The following example

```kotlin
// Lazy loading of this Class
// If you are currently in a PackageParam environment, then you do not need to consider ClassLoader
// If not available, the result will be null but no exception will be thrown
val instance by lazyClassOrNull("com.demo.Test")
// Customize the ClassLoader where the Class is located
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
// If not available, the result will be null but no exception will be thrown
val instance by lazyClassOrNull("com.demo.Test") { customClassLoader }
// Call this Class at the appropriate time
instance?.method {
    // ...
}
```

::: tip

For more functions, please refer to [lazyClass](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#lazyclass-method), [lazyClassOrNull](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#lazyclassornull-method), [PackageParam → lazyClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#lazyclass-method), [PackageParam → lazyClassOrNull](../public/com/highcapable/yukihookapi/hook/param/PackageParam#lazyclassornull-method) methods.

:::

### Existential Judgment

Suppose we want to determine whether a `Class` exists.

Usually, we can use the standard reflection API to find this `Class` to determine whether it exists by exception.

> The following example

```kotlin
// Class in the default ClassLoader environment
var isExist = try {
    Class.forName("com.demo.Test")
    true
} catch (_: Throwable) {
    false
}
// Specify the Class in the ClassLoader environment
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
var isExist = try {
    customClassLoader?.loadClass("com.demo.Test")
    true
} catch (_: Throwable) {
    false
}
```

This is probably not very friendly, and `YukiHookAPI` provides you with a syntactic sugar that can be used anywhere.

The above writing can be written as `YukiHookAPI` as follows.

> The following example

```kotlin
// Check if this class exists
// If you are currently in the PackageParam environment, then you don't need to consider ClassLoader
var isExist = "com.demo.Test".hasClass()
// ClassLoader where the custom Class is located
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
var isExist = "com.demo.Test".hasClass(customClassLoader)
```

::: tip

For more functions, please refer to [String.hasClass](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#string-hasclass-ext-method), [PackageParam → String.hasClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#string-hasclass-i-ext-method) methods.

:::

### Vague Search&ensp;<Badge type="tip" text="Beta" vertical="middle" />

The `Class` name in the Host App's **Dex** after being obfuscated by tools such as R8 will be difficult to distinguish.

Its correct position is uncertain, and cannot be obtained directly through [Object Conversion](#object-conversion).

At this point, there is `DexClassFinder`, its role is to determine the instance of this `Class` by the bytecode features in the `Class` that need to be searched.

::: warning

At present, the function of **DexClassFinder** is still in the experimental stage.

Since the search function is only implemented through the Java layer, the performance may not reach the optimal level when there are too many Host App's **Class**.

If something got wrong welcome to feedback.

Since it is a reflection-level API, currently it can only locate the specified **Class** through the characteristics of **Class and Member**, and cannot locate it by specifying the string and method content characteristics in the bytecode.

The speed of searching **Class** depends on the performance of the current device.

At present, the mainstream mobile processors are in the **3~10s** range when the conditions are not complicated in the **10~15w** number of **Class**, the fastest speed can reach within **25s** under slightly complex conditions.

Please note that the more the same type **Class** is matched, the slower the speed.

:::

::: danger

After **YukiHookAPI** **2.0.0** released, this function will be deprecated and will no longer be migrated to [YukiReflection](https://github.com/fankes/YukiReflection).

We welcome all developers to start using [DexKit](https://github.com/LuckyPray/DexKit), which is a high-performance runtime parsing library for **Dex** implemented in C++, which is more efficient than the Java layer in terms of performance, efficient and excellent, it is still in the development stage, your valuable suggestions are welcome.

:::

#### Get Started

Below is a simple usage example.

Suppose the following `Class` is what we want, the names are obfuscated and may be different in each version.

> The following example

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

At this point, we want to get this `Class`, you can use the `ClassLoader.searchClass` method directly.

In `PackageParam` you can use the `searchClass` method directly and it will automatically specify the `appClassLoader`.

Each of the conditions demonstrated below is optional, and the more complex the conditions, the more accurate the positioning and the worse the performance.

> The following example

```kotlin
searchClass {
    // Start the search from the specified package name range
    // In actual use, you can specify multiple package name ranges at the same time
    from("com.demo")
    // Specify the result of getSimpleName of the current Class
    // You can directly make logical judgments on this string
    // Here we are not sure whether its name is a, we can only judge the length of the string
    simpleName { it.length == 1 }
    // Specify the inherited parent class object
    // If it is an existing stub, it can be directly represented by generics
    extends<Activity>()
    // Specify the inherited parent class object
    // Which can be written directly as the full class name
    // And you can also specify multiple objects at the same time
    extends("android.app.Activity")
    // Specify the implemented interface
    // If it exists stub, can be directly represented by generics
    implements<Serializable>()
    // Specify the implemented interface
    // Which can be written directly as a full class name, or you can specify multiple at the same time
    implements("java.io.Serializable")
    // Specify the type and style of the constructor
    // And the number count that exists in the current class
    constructor { param(StringClass) }.count(num = 1)
    // Specify the type and style of the variable
    // And the number that exists in the current class count
    field { type = StringClass }.count(num = 2)
    // Specify the type and style of the variable
    // And the number that exists in the current class count
    field { type = BooleanType }.count(num = 1)
    // Directly specify the number of all variables that exist in the current class count
    field().count(num = 3)
    // If you think the number of variables is indeterminate
    // You can also use the following custom conditions
    field().count(1..3)
    field().count { it >= 3 }
    // Specify the type and style of the method
    // And the number that exists in the current class count
    method {
        name = "onCreate"
        param(BundleClass)
    }.count(num = 1)
    // Specify the type and style of the method
    // Specify the modifier, and the number count in the current class
    method {
        modifiers { isStatic && isPrivate }
        param(StringClass)
        returnType = UnitType
    }.count(num = 1)
    // Specify the type and style of the method
    // Specify the modifier, and the number count in the current class
    method {
        modifiers { isPrivate && isStatic.not() }
        param(BooleanType, StringClass)
        returnType = StringClass
    }.count(num = 1)
    // Specify the type and style of the method
    // Specify the modifier, and the number count in the current class
    method {
        modifiers { isPrivate && isStatic.not() }
        emptyParam()
        returnType = UnitType
    }.count(num = 1)
    // Specify the type and style of the method
    // As well as the modifier and VagueType
    // And the number count that exists in the current class
    method {
        modifiers { isPrivate && isStatic.not() }
        param(BooleanType, VagueType, VagueType, StringClass)
        returnType = UnitType
    }.count(num = 1)
    // Directly specify the number of all methods that exist in the current class count
    method().count(num = 5)
    // If you think the number of methods is uncertain, you can also use the following custom conditions
    method().count(1..5)
    method().count { it >= 5 }
    // Directly specify the number of all members existing in the current class count
    // Members include: Field, Method, Constructor
    member().count(num = 9)
    // There must be a static modifier in all members, you can add this condition like this
    member {
        modifiers { isStatic }
    }
}.get() // Get the instance of this Class itself, if not found, it will return null
```

::: tip

The conditional usage of **Field**, **Method**, **Constructor** in the above usage is consistent with the related usage in [Member Extensions](#member-extensions), with only minor differences.

For more functions, please refer to [MemberRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/MemberRules), [FieldRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/FieldRules), [MethodRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/MethodRules), [ConstructorRules](../public/com/highcapable/yukihookapi/hook/core/finder/classes/rules/ConstructorRules).

:::

#### Asynchronous Search

By default, `DexClassFinder` will use synchronous mode to search `Class`, which will block the current thread until it finds or finds an exception.

If the search takes too long, it may cause **ANR** problems to the Host App.

In response to the above problems, we can enable asynchronous, just add the parameter `async = true`, which will not require you to start a thread again, the API has already handled the related problems for you.

::: warning

For the asynchronous case you need to use the **wait** method to get the result, the **get** method will no longer work.

:::

> The following example

```kotlin
searchClass(async = true) {
    // ...
}.wait { class1 ->
    // Get asynchronous result
}
searchClass(async = true) {
    // ...
}.wait { class2 ->
    // Get asynchronous result
}
```

In this way, our search process runs asynchronously, it will not block the main thread, and each search will be performed in a separate thread at the same time, which can achieve the effect of parallel tasks.

#### Local Cache

Since the search is performed again every time the Host App is reopened, this is a waste of repetitive performance when the Host App's version is unchanged.

At this point, we can locally cache the search results of the current Host App's version by specifying the `name` parameter.

Next time, the found class name will be directly read from the local cache.

The local cache uses `SharedPreferences`, which will be saved to the Host App's data directory and will be re-cached after the Host App's version is updated.

After enabling the local cache, `async = true` will be set at the same time, you don't need to set it manually.

> The following example

```kotlin
searchClass(name = "com.demo.class1") {
    // ...
}.wait { class1 ->
    // Get asynchronous result
}
searchClass(name = "com.demo.class2") {
    // ...
}.wait { class2 ->
    // Get asynchronous result
}
```

If you want to clear the local cache manually, you can use the following method to clear the current version of the Host App's cache.

> The following example

```kotlin
// Call it directly
// It may fail when the Host App's appContext is null, and a warning message will be printed on failure
DexClassFinder.clearCache()
// Called after listening to the lifecycle of the Host App
onAppLifecycle {
    onCreate {
        DexClassFinder.clearCache(context = this)
    }
}
```

You can also clear the Host App's cache for a specific version.

> The following example

```kotlin
// Call it directly
// It may fail when the Host App's appContext is null, and a warning message will be printed on failure
DexClassFinder.clearCache(versionName = "1.0", versionCode = 1)
// Called after listening to the lifecycle of the Host App
onAppLifecycle {
    onCreate {
        DexClassFinder.clearCache(context = this, versionName = "1.0", versionCode = 1)
    }
}
```

#### Multiple Search

If you need to search a set of `Class` at the same time using a fixed condition, then you only need to use the `all` or `waitAll` method to get the result.

```kotlin
// Synchronous search, use all to get all the results found by the conditions
searchClass {
    // ...
}.all().forEach { clazz ->
    // Get each result
}
// Synchronous search, using all { ... } to iterate over each result
searchClass {
    // ...
}.all { clazz ->
    // Get each result
}
// Asynchronous search, use waitAll to get all the results found by the conditions
searchClass(async = true) {
    // ...
}.waitAll { classes ->
    classes.forEach {
        // Get each result
    }
}
```

::: tip

For more functions, please refer to [ClassLoader.searchClass](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#classloader-searchclass-ext-method), [PackageParam.searchClass](../public/com/highcapable/yukihookapi/hook/param/PackageParam#searchclass-method) methods.

:::

## Member Extensions

> Here are the extension functions related to the **Class** bytecode member variables **Field**, **Method**, **Constructor**.

::: tip

**Member** is the interface description object of **Field**, **Method**, **Constructor**, which is the general term for the bytecode members in **Class** in Java reflection.

:::

Suppose there is such a `Class`.

> The following example

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

### Find and Reflection

Suppose we want to get the `doTask` method of `Test` and execute it.

Normally, we can use the standard reflection API to find this method.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using reflection API
Test::class.java
    .getDeclaredMethod("doTask", String::class.java)
    .apply { isAccessible = true }
    .invoke(instance, "task_name")
```

This is probably not very friendly, and `YukiHookAPI` provides you with a syntactic sugar that can be used anywhere.

The above writing can be written as `YukiHookAPI` as follows.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "doTask"
    param(StringClass)
}.get(instance).call("task_name")
```

::: tip

For more features, please refer to [MethodFinder](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder).

:::

Similarly, we need to get the `isTaskRunning` field can also be written as follows.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.field {
    name = "isTaskRunning"
    type = BooleanType
}.get(instance).any() // Any instantiates an object of any type of Field
```

::: tip

For more features, please refer to [FieldFinder](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder).

:::

Maybe you also want to get the current `Class` constructor, the same can be achieved.

> The following example

```kotlin
Test::class.java.constructor {
    param(BooleanType)
}.get().call(true) // Can create a new instance
```

If you want to get the no-argument constructor of `Class`, you can write it as follows.

> The following example

```kotlin
Test::class.java.constructor().get().call() // Create a new instance
```

::: tip

For more features, please refer to [ConstructorFinder](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder).

:::

### Optional Find Conditions

Suppose we want to get the `getName` method in `Class`, which can be implemented as follows.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "getName"
    emptyParam()
    returnType = StringClass
}.get(instance).string() // Get the result of the method
```

Through observation, it is found that there is only one method named `getName` in this `Class`, so can we make it simpler?

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "getName"
    emptyParam()
}.get(instance).string() // Get the result of the method
```

Yes, you can refine your find criteria for methods that do not change exactly.

When using only `get` or `wait` methods to get results, `YukiHookAPI` **will match the first found result in bytecode order** by default.

The problem comes again, this `Class` has a `release` method, but its method parameters are very long, and some types may not be directly available.

Normally we would use `param(...)` to find this method, but is there an easier way.

At this point, after determining the uniqueness of the method, you can use `paramCount` to find the method.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "release"
    // At this point
    // We don't have to determine the specific type of method parameters, just write the number
    paramCount = 3
}.get(instance) // Get this method
```

Although the above example can be successfully matched, it is not accurate.

At this time, you can also use `VagueType` to fill in the method parameter type that you do not want to fill in.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "release"
    // Use VagueType to fill in the type you don't want to fill in
    // While ensuring that other types can match
    param(StringClass, VagueType, BooleanType)
}.get(instance) // Get this method
```

If you are not sure about the type of each parameter, you can create a conditional method body with the `param { ... }` method.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
     name = "release"
     // Get the it (Class) method parameter type array instance
     // To only determine the known type and its position
     param { it[0] == StringClass && it[2] == BooleanType }
}.get(instance) // Get this method
```

::: tip

Use **param { ... }** to create a conditional method body, where the variable **it** is the **Class** type array instance of the current method parameter, and you can freely use **Class** all objects and their methods in.

The condition at the end of the method body needs to return a **Boolean**, which is the final condition judgment result.

For more functions, please refer to [FieldFinder.type](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder#type-method-1), [MethodFinder.param](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#param-method-1), [MethodFinder.returnType](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#returntype-method-1), [ConstructorFinder.param](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#param-method-1) method.

:::

### Find in Super Class

You will notice that `Test` extends `BaseTest`, now we want to get the `doBaseTask` method of `BaseTest`, how do we do it without knowing the name of the super class?

Referring to the above find conditions, we only need to add a `superClass` to the find conditions to achieve this function.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "doBaseTask"
    param(StringClass)
    // Just add this condition
    superClass()
}.get(instance).call("task_name")
```

At this time, we can get this method in the super class.

`superClass` has a parameter `isOnlySuperClass`, when set to `true`, you can skip the current `Class` and only find the super class of the current `Class`.

Since we now know that the `doBaseTask` method only exists in the super class, this condition can be added to save finding time.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "doBaseTask"
    param(StringClass)
    // Add a find condition
    superClass(isOnlySuperClass = true)
}.get(instance).call("task_name")
```

At this time, we can also get this method in the super class.

Once `superClass` is set, it will automatically cycle backward to find out whether this method exists in all extends super classes, until it finds that the target has no super class (the extends is `java.lang.Object`).

::: tip

For more functions, please refer to [MethodFinder.superClass](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#superclass-method), [ConstructorFinder.superClass](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#superclass-method), [FieldFinder.superClass](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder#superclass-method) methods.

:::

::: danger

The currently founded **Method** can only find the **Method** of the current **Class** unless the **superClass** condition is specified, which is the default behavior of the Java Reflection API.

:::

### Vague Find

If we want to find a method name, but are not sure if it has changed in each release, we can use vague find.

Suppose we want to get the `doTask` method in `Class`, which can be implemented as follows.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name {
        // Set name is case insensitive
        it.equals("dotask", isIgnoreCase = true)
    }
    param(StringClass)
}.get(instance).call("task_name")
```

Knowing that there is currently only one `doTask` method in `Class`, we can also judge that the method name contains only the characters specified in it.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name {
        // Only contains oTas
        it.contains("oTas")
    }
    param(StringClass)
}.get(instance).call("task_name")
```

We can also judge based on the first and last strings.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name {
        // Contains do at the beginning and Task at the end
        it.startsWith("do") && it.endsWith("Task")
    }
    param(StringClass)
}.get(instance).call("task_name")
```

By observing that this method name contains only letters, we can add a precise search condition.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name {
        // Start with do, end with Task, just letters
        it.startsWith("do") && it.endsWith("Task") && it.isOnlyLetters()
    }
    param(StringClass)
}.get(instance).call("task_name")
```

::: tip

Use **name { ... }** to create a conditional method body, where the variable **it** is the string of the current name, and you can freely use it in the extension method of **NameRules** function.

The condition at the end of the method body needs to return a **Boolean**, which is the final condition judgment result.

For more functions, please refer to [FieldFinder.name](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder#name-method-1), [MethodFinder.name](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#name-method-1) methods and [NameRules](../public/com/highcapable/yukihookapi/hook/core/finder/base/rules/NameRules).

:::

### Multiple Find

Sometimes, we may need to find a set of methods, constructors, and fields with the same characteristics in a `Class`.

At this time, we can use relative condition matching to complete.

Based on the result of the find condition, we only need to replace `get` with `all` to get all the bytecodes that match the condition.

Suppose this time we want to get all methods in `Class` with the number of method parameters in the range `1..3`, you can use the following implementation.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    paramCount(1..3)
}.all(instance).forEach { instance ->
    // Call and execute each method
    instance.call(...)
}
```

The above example can be perfectly matched to the following 3 methods.

`private void doTask(String taskName)`

`private void release(String taskName, Function<boolean, String> task, boolean isFinish)`

`private void b(String a)`

If you want to define the conditions for the range of the number of parameters more freely, you can use the following implementation.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    paramCount { it < 3 }
}.all(instance).forEach { instance ->
    // Call and execute each method
    instance.call(...)
}
```

The above example can be perfectly matched to the following 6 methods.

`private static void init()`

`private void doTask(String taskName)`

`private void stop(String a)`

`private void getName(String a)`

`private void b()`

`private void b(String a)`

By observing that there are two methods named `b` in `Class`, you can use the following implementation.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "b"
}.all(instance).forEach { instance ->
    // Call and execute each method
    instance.call(...)
}
```

The above example can be perfectly matched to the following 2 methods.

`private void b()`

`private void b(String a)`

::: tip

Use **paramCount { ... }** to create a conditional method body, where the variable **it** is the integer of the current number of parameters, and you can use it freely in the extension method of **CountRules** function in it.

The condition at the end of the method body needs to return a **Boolean**, which is the final condition judgment result.

For more functions, please refer to [MethodFinder.paramCount](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#paramcount-method-2), [ConstructorFinder.paramCount](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#paramcount-method-2) methods and [CountRules](../public/com/highcapable/yukihookapi/hook/core/finder/base/rules/CountRules).

:::

### Static Bytecode

Some methods and fields are statically implemented in `Class`, at this time, we can call them without passing in an instance.

Suppose we want to get the contents of the static field `TAG` this time.

> The following example

```kotlin
Test::class.java.field {
    name = "TAG"
    type = StringClass
}.get().string() // The type of Field is string and can be cast directly
```

Assuming that there is a non-static `TAG` field with the same name in `Class`, what should I do at this time?

Just add a filter.

> The following example

```kotlin
Test::class.java.field {
    name = "TAG"
    type = StringClass
    // This field to identify the lookup needs to be static
    modifiers { isStatic }
}.get().string() // The type of Field is string and can be cast directly
```

We can also call a static method called `init`.

> The following example

```kotlin
Test::class.java.method {
    name = "init"
    emptyParam()
}.get().call()
```

Likewise, you can identify it as a static.

> The following example

```kotlin
Test::class.java.method {
    name = "init"
    emptyParam()
    // This method of identity find needs to be static
    modifiers { isStatic }
}.get().call()
```

::: tip

Use **modifiers { ... }** to create a conditional method body, at which point you can freely use its functionality in **ModifierRules**.

The condition at the end of the method body needs to return a **Boolean**, which is the final condition judgment result.

For more functions, please refer to [FieldFinder.modifiers](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder#modifiers-method), [MethodFinder.modifiers](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#modifiers-method), [ConstructorFinder.modifiers](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#modifiers-method) methods and [ModifierRules](../public/com/highcapable/yukihookapi/hook/core/finder/base/rules/ModifierRules).

:::

### Obfuscated Bytecode

You may have noticed that the example `Class` given here has two obfuscated field names, both of which are `a`, how do we get them at this time?

There are two options.

The first option is to determine the name and type of the field.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.field {
    name = "a"
    type = BooleanType
}.get(instance).any() // Get a field named a with type Boolean
```

The second option is to determine where the type of the field is located.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.field {
    type(BooleanType).index().first()
}.get(instance).any() // Get the first field of type Boolean
```

In the above two cases, the corresponding field `private boolean a` can be obtained.

Likewise, there are two obfuscated method names in this `Class`, both of which are `b`.

You can also have two options to get them.

The first option is to determine the method name and method parameters.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "b"
    param(StringClass)
}.get(instance).call("test_string") // Get the method whose name is b and whose parameter is [String]
```

The second option is to determine where the parameters of the method are located.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    param(StringClass).index().first()
}.get(instance).call("test_string") // Get the method whose first method parameter is [String]
```

Since it is observed that this method is last in `Class`, then we have an alternative.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    order().index().last()
}.get(instance).call("test_string") // Get the last method of the current Class
```

::: warning

Please try to avoid using **order** to filter bytecode subscripts, they may be indeterminate unless you are sure that its position in this **Class** must not change.

:::

### Directly Called

The methods of calling bytecode described above all need to use `get(instance)` to call the corresponding method.

Is there a simpler way?

At this point, you can use the `current` method on any instance to create a call space.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Assume this Class is not directly available
instance.current {
    // Execute the doTask method
    method {
        name = "doTask"
        param(StringClass)
    }.call("task_name")
    // Execute the stop method
    method {
        name = "stop"
        emptyParam()
    }.call()
    // Get name
    val name = method { name = "getName" }.string()
}
```

We can also use `superClass` to call methods of the current `Class` super class.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Assume this Class is not directly available
instance.current {
    // Execute the doBaseTask method of the parent class
    superClass().method {
        name = "doBaseTask"
        param(StringClass)
    }.call("task_name")
}
```

If you don't like to use a lambda to create the namespace of the current instance, you can use the `current()` method directly.

> The following example

```kotlin
// Assuming this is an instance of this Class, this Class cannot be obtained directly
val instance = Test()
// Execute the doTask method
instance
    .current()
    .method {
        name = "doTask"
        param(StringClass)
    }.call("task_name")
// Execute the stop method
instance
    .current()
    .method {
        name = "stop"
        emptyParam()
    }.call()
// Get name
val name = instance.current().method { name = "getName" }.string()
```

Likewise, consecutive calls can be made between them, but <u>**inline calls are not allowed**</u>.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Assume this Class is not directly available
instance.current {
    method {
        name = "doTask"
        param(StringClass)
    }.call("task_name")
}.current()
    .method {
        name = "stop"
        emptyParam()
    }.call()
//  Note that because current() returns the CurrentClass object itself
// It CANNOT BE CALLED like the following
instance.current().current()
```

For `Field` instances, there is also a convenience method that can directly get the object of the instance where `Field` is located.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Assume this Class is not directly available
instance.current {
    // <Plan 1>
    field {
        name = "baseInstance"
    }.current {
        method {
            name = "doBaseTask"
            param(StringClass)
        }.call("task_name")
    }
    // <Plan 2>
    field {
        name = "baseInstance"
    }.current()
        ?.method {
            name = "doBaseTask"
            param(StringClass)
        }?.call("task_name")
}
```

::: warning

The above **current** method is equivalent to calling the **field { ... }.any()?.current()** method in **CurrentClass** for you.

If there is no **CurrentClass** calling field, you need to use **field { ... }.get(instance).current()** to call it.

:::

The problem comes again, I want to use reflection to create the following instance and call the method in it, how to do it?

> The following example

```kotlin
Test(true).doTask("task_name")
```

Usually, we can use the standard reflection API to call.

> The following example

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

But I feel that this approach is very troublesome.

Is there a more concise way to call it?

At this time, we can also use the `buildOf` method to create an instance.

> The following example

```kotlin
"com.demo.Test".toClass().buildOf(true) { param(BooleanType) }?.current {
    method {
        name = "doTask"
        param(StringClass)
    }.call("task_name")
}
```

If you want the `buildOf` method to return the type of the current instance, you can include a type-generic declaration in it instead of using `as` to `cast` the target type.

In this case, the constructor of the instance itself is private, but the method inside is public, so we only need to create its constructor by reflection.

> The following example

```kotlin
// Assume this Class can be obtained directly
val test = Test::class.java.buildOf<Test>(true) { param(BooleanType) }
test.doTask("task_name")
```

::: tip

For more functions, please refer to [CurrentClass](../public/com/highcapable/yukihookapi/hook/bean/CurrentClass) and [Class.buildOf](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#class-buildof-ext-method) method.

:::

### Original Called

If you are using reflection to call a method that has been hooked, how do we call its original method?

The native `XposedBridge` provides us with a `XposedBridge.invokeOriginalMethod` function.

Now, in `YukiHookAPI` you can use the following method to implement this function conveniently.

Suppose below is the `Class` we want to demonstrate.

> The following example

```java:no-line-numbers
public class Test {

    public static String getString() {
        return "Original";
    }
}
```

Here's how the `getString` method in this `Class` Hooks.

> The following example

```kotlin
Test::class.java.method {
    name = "getString"
    emptyParam()
    returnType = StringClass
}.hook {
    replaceTo("Hooked")
}
```

At this point, we use reflection to call this method, and we will get the result of Hook `"Hooked"`.

> The following example

```kotlin
// Result will be "Hooked"
val result = Test::class.java.method {
    name = "getString"
    emptyParam()
    returnType = StringClass
}.get().string()
```

If we want to get the original method and result of this method without hooking, we just need to add `original` to the result.

> The following example

```kotlin
// Result will be "Original"
val result = Test::class.java.method {
    name = "getString"
    emptyParam()
    returnType = StringClass
}.get().original().string()
```

::: tip

For more functions, please refer to the [MethodFinder.Result.original](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#original-method) method.

:::

### Find Again

Suppose there are three different versions of `Class`, all of which are the same `Class` for different versions of this Host App.

There is also a method `doTask` in it, assuming they function the same.

> The following example of version A

```java:no-line-numbers
public class Test {

    public void doTask() {
        // ...
    }
}
```

> The following example of version B 

```java:no-line-numbers
public class Test {

    public void doTask(String taskName) {
        // ...
    }
}
```

> The following example of version C 

```java:no-line-numbers
public class Test {

    public void doTask(String taskName, int type) {
        // ...
    }
}
```

We need to get this same functionality of the `doTask` method in a different version, how do we do it?

At this point, you can use `RemedyPlan` to complete your needs.

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "doTask"
    emptyParam()
}.remedys {
    method {
        name = "doTask"
        param(StringClass)
    }.onFind {
        // Found logic can be implemented here
    }
    method {
        name = "doTask"
        param(StringClass, IntType)
    }.onFind {
        // Found logic can be implemented here
    }
}.wait(instance) {
    // Get the result of the method
}
```

::: danger

The method lookup result using **RemedyPlan** can no longer use **get** to get method instance, you should use **wait** method.

:::

Also, you can continue to use `RemedyPlan` while using [Multiple Find](#multiple-find).

> The following example

```kotlin
// Assume this is an instance of this Class
val instance = Test()
// Call and execute using YukiHookAPI
Test::class.java.method {
    name = "doTask"
    emptyParam()
}.remedys {
    method {
        name = "doTask"
        paramCount(0..1)
    }.onFind {
        // Found logic can be implemented here
    }
    method {
        name = "doTask"
        paramCount(1..2)
    }.onFind {
        // Found logic can be implemented here
    }
}.waitAll(instance) {
    // Get the result of the method
}
```

::: tip

For more functions, please refer to [MethodFinder.RemedyPlan](../public/com/highcapable/yukihookapi/hook/core/finder/members/MethodFinder#remedyplan-class), [ConstructorFinder.RemedyPlan](../public/com/highcapable/yukihookapi/hook/core/finder/members/ConstructorFinder#remedyplan-class), [FieldFinder.RemedyPlan](../public/com/highcapable/yukihookapi/hook/core/finder/members/FieldFinder#remedyplan-class) .

:::

### Relative Matching

Suppose there is a `Class` with the same function in different versions of the Host App but only the name of the `Class` is different.

> The following example of version A

```java:no-line-numbers
public class ATest {

    public static void doTask() {
        // ...
    }
}
```

> The following example of version B

```java:no-line-numbers
public class BTest {

    public static void doTask() {
        // ...
    }
}
```

At this time, what should we do if we want to call the `doTask` method in this `Class` in each version?

The usual practice is to check if `Class` exists.

> The following example

```kotlin
// First find this Class
val currentClass =
    if("com.demo.ATest".hasClass()) "com.demo.ATest".toClass() else "com.demo.BTest".toClass()
// Then look for this method and call
currentClass.method {
    name = "doTask"
    emptyParam()
}.get().call()
```

I feel that this solution is very inelegant and cumbersome, then `YukiHookAPI` provides you with a very convenient `VariousClass` to solve this problem.

Now, you can get this `Class` directly using the following methods.

> The following example

```kotlin
VariousClass("com.demo.ATest", "com.demo.BTest").get().method {
    name = "doTask"
    emptyParam()
}.get().call()
```

If the current `Class` exists in the specified `ClassLoader`, you can fill in your `ClassLoader` in `get`.

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
VariousClass("com.demo.ATest", "com.demo.BTest").get(customClassLoader).method {
    name = "doTask"
    emptyParam()
}.get().call()
```

If you are not sure that all `Class` will be matched, you can use the `getOrNull` method.

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // Assume this is your ClassLoader
VariousClass("com.demo.ATest", "com.demo.BTest").getOrNull(customClassLoader)?.method {
     name = "doTask"
     emptyParam()
}?.get()?.call()
```

If you are using the `Class` of the (Xposed) Host environment in `PackageParam`, you can use `toClass()` to set it directly.

> The following example

```kotlin
VariousClass("com.demo.ATest", "com.demo.BTest").toClass().method {
    name = "doTask"
    emptyParam()
}.get().call()
```

::: tip

For more functions, please refer to [VariousClass](../public/com/highcapable/yukihookapi/hook/bean/VariousClass).

:::

If it is used when creating a Hook, it can be more convenient, and it can also automatically intercept the exception that `Class` cannot be found.

You can define this `Class` as a constant type to use.

> The following example

```kotlin
// Define constant type
val ABTestClass = VariousClass("com.demo.ATest", "com.demo.BTest")
// Use directly
ABTestClass.hook {
    // Your code here.
}
```

### Calling Generics

In the process of reflection, we may encounter generic problems.

In the reflection processing of generics, `YukiHookAPI` also provides a syntactic sugar that can be used anywhere.

For example we have the following generic class.

> The following example

```kotlin
class TestGeneric<T, R> (t: T, r: R) {

    fun foo() {
        // ...
    }
}
```

When we want to get a `Class` instance of the generic `T` or `R` in the current `Class`, only the following implementation is required.

> The following example

```kotlin
class TestGeneric<T, R> (t: T, r: R) {

    fun foo() {
        // Get the operation object of the current instance
        // Get the Class instance of T, in the 0th position of the parameter
        // The default value can not be written
        val tClass = current().generic()?.argument()
        // Get the Class instance of R, in parameter 1
        val rClass = current().generic()?.argument(index = 1)
        // You can also use the following syntax
        current().generic {
             // Get the Class instance of T
             // In the 0th position of the parameter, the default value can be left blank
            val tClass = argument()
            // Get the Class instance of R, in parameter 1
            val rClass = argument(index = 1)
        }
    }
}
```

When we want to call this `Class` externally, it can be implemented as follows.

> The following example

```kotlin
// Assume this is the Class of T
class TI {

    fun foo() {
        // ...
    }
}
// Assume this is an instance of T
val tInstance: TI? = ...
// Get the Class instance of T
// In the 0th position of the parameter, the default value can be left blank
// And get the method foo and call it
TestGeneric::class.java.generic()?.argument()?.method {
    name = "foo"
    emptyParam()
}?.get(tInstance)?.invoke<TI>()
```

::: tip

For more functions, please refer to [CurrentClass.generic](../public/com/highcapable/yukihookapi/hook/bean/CurrentClass#generic-method), [Class.generic](../public/com/highcapable/yukihookapi/hook/factory/ReflectionFactory#class-generic-ext-method) methods and [GenericClass](../public/com/highcapable/yukihookapi/hook/bean/GenericClass).

:::

### Pay Attention of Trap

> Here are some misunderstandings that may be encountered during use for reference.

#### Restrictive Find Conditions

In find conditions you can <u>**only**</u> use `index` function once except `order`.

> The following example

```kotlin
method {
    name = "test"
    param(BooleanType).index(num = 2)
    //  Wrong usage, please keep only one index method
    returnType(StringClass).index(num = 1)
}
```

The following find conditions can be used without any problems.

> The following example

```kotlin
method {
    name = "test"
    param(BooleanType).index(num = 2)
    order().index(num = 1)
}
```

#### Necessary Find Conditions

In common method find conditions, <u>**even methods without parameters need to set find conditions**</u>.

Suppose we have the following `Class`.

> The following example

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

We want to get the `public void foo()` method, which can be written as follows.

> The following example

```kotlin
TestFoo::class.java.method {
    name = "foo"
}
```

However, the above example <u>**is wrong**</u>.

You will find two `foo` methods in this `Class`, one of which takes a method parameter.

Since the above example does not set the find conditions for `param`, the result will be the first method `public void foo(String string)` that matches the name and matches the bytecode order, not the last method we need.

This is a **frequent error**, **without method parameters, you will lose the use of method parameter find conditions**.

The correct usage is as follows.

> The following example

```kotlin
TestFoo::class.java.method {
    name = "foo"
    // ✅ Correct usage, add detailed filter conditions
    emptyParam()
}
```

At this point, the above example will perfectly match the `public void foo()` method.

::: tip Compatibility Notes

In the past historical versions of the API, it was allowed to match the method without writing the default matching no-parameter method, but the latest version has corrected this problem, please make sure that you are using the latest API version.

:::

In the find conditions for constructors, <u>**even constructors without parameters need to set find conditions**</u>.

Suppose we have the following `Class`.

> The following example

```java:no-line-numbers
public class TestFoo {

    public TestFoo() {
        // ...
    }
}
```

To get the `public TestFoo()` constructor, we must write it in the following form.

> The following example

```kotlin
TestFoo::class.java.constructor { emptyParam() }
```

The above example can successfully obtain the `public TestFoo()` constructor.

If you write `constructor()` and miss `emptyParam()`, the result found at this time will be the first one in bytecode order, <u>**may not be parameterless** </u>.

::: tip Compatibility Notes

In past historical versions of the API, if the constructor does not fill in any search parameters, the constructor will not be found directly. 

<u>**This is a BUG and has been fixed in the latest version**</u>, please make sure you are using the latest API version.

:::

::: danger API Behavior Changes

In **1.2.0** and later versions, the behavior of **constructor()** is no longer **constructor { emptyParam() }** but **constructor {}**, please pay attention to the behavior change reasonably adjust the find parameters.

:::

#### No Find Conditions

Without setting find conditions, using `field()`, `constructor()`, `method()` will return all members under the current `Class`.

Using `get(...)` or `give()` will only get the first bit in bytecode order.

> The following example

```kotlin
Test::class.java.field().get(...)
Test::class.java.method().give()
```

If you want to get all members, you can use `all(...)` or `giveAll()`

> The following example

```kotlin
Test::class.java.field().all(...)
Test::class.java.method().giveAll()
```

::: tip Compatibility Notes

In past historical versions of the API, failure to set find conditions will throw an exception.

This feature was added in **1.2.0** and later versions.

:::

#### Bytecode Type

In the bytecode call result, the **cast** method can <u>**only**</u> specify the type corresponding to the bytecode.

For example we want to get a field of type `Boolean` and cast it to `String`.

The following is the wrong way to use it.

> The following example

```kotlin
field {
    name = "test"
    type = BooleanType
}.get().string() //  Wrong usage, must be cast to the bytecode target type
```

The following is the correct way to use it.

> The following example

```kotlin
field {
    name = "test"
    type = BooleanType
}.get().boolean().toString() // ✅ The correct way to use, get the type and then convert
```

## Common Type Extensions

When find methods and fields, we usually need to specify the type in find conditions.

> The following example

```kotlin
field {
    name = "test"
    type = Boolean::class.javaPrimitiveType
}
```

Expressing the type of `Boolean::class.javaPrimitiveType` in Kotlin is very long and inconvenient.

Therefore, `YukiHookAPI` encapsulates common type calls for developers, including Android related types and Java common types and **primitive type keywords**.

At this time, the above type can be written in the following form.

> The following example

```kotlin
field {
    name = "test"
    type = BooleanType
}
```

The **primitive type keywords** in common Java types have been encapsulated as **Type(Class Name) + Type**, such as `IntType`, `FloatType` (their bytecode types are `int`, ` float`).

Correspondingly, array types also have convenient usage methods, assuming we want to get an array of type `String[]`.

You need to write `java.lang.reflect.Array.newInstance(String::class.java, 0).javaClass` to get this type.

Does it feel very troublesome, at this time we can use the method `ArrayClass(StringClass)` to get this type.

At the same time, since `String` is a common type, you can also directly use `StringArrayClass` to get this type.

Some common methods found in Hook have their corresponding encapsulation types for use, in the format **Type(Class Name) + Class**.

For example, the Hook `onCreate` method needs to look up the `Bundle::class.java` type.

> The following example

```kotlin
method {
    name = "onCreate"
    param(BundleClass)
}
```

The following are wrapper names for some special case types in Java represented in `YukiHookAPI`.

- `void` → `UnitType`

- `java.lang.Void` → `UnitClass`

- `java.lang.Object` → `AnyClass`

- `java.lang.Integer` → `IntClass`

- `java.lang.Character` → `CharClass`

::: warning

Encapsulating types with **Type(Class Name) + Type** will and only be represented as Java **primitive type keywords**.

Since the concept of **primitive types** does not exist in Kotlin, they will all be defined as **KClass**.

There are 9 **primitive type keywords** in Java, of which 8 are **primitive type**, namely **boolean**, **char**, **byte**, **short** , **int**, **float**, **long**, **double**, of which the **void** type is a special case.

At the same time, they all have their own corresponding package types in Java, such as **java.lang.Boolean**, **java.lang.Integer**, these types are <u>**unequal**</u>, Please note the distinction.

Similarly, arrays also have corresponding wrapper types, which also need to be distinguished from Java **primitive type keywords**.

For example, the encapsulation type of **byte[]** is **ByteArrayType** or **ArrayClass(ByteType)**, and the encapsulation type of **Byte[]** is **ByteArrayClass** or **ArrayClass(ByteClass)**, these types are also <u>**unequal**</u>.

:::

::: tip

For more types, see [ComponentTypeFactory](../public/com/highcapable/yukihookapi/hook/type/android/ComponentTypeFactory), [GraphicsTypeFactory](../public/com/highcapable/yukihookapi/hook/type/android/GraphicsTypeFactory), [ViewTypeFactory](../public/com/highcapable/yukihookapi/hook/type/android/ViewTypeFactory), [VariableTypeFactory](../public/com/highcapable/yukihookapi/hook/type/java/VariableTypeFactory).

:::

At the same time, you are welcome to contribute more commonly used types.