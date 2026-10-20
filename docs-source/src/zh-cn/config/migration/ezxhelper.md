# EzXHelper

> 这里介绍了如何从 [EzXHelper](https://github.com/KyuubiRan/EzXHelper) 迁移至 `YukiHook`。

EzXHelper 不是完整的 Hook 框架，它是基于 Xposed API 的辅助库，模块入口仍由 Xposed API 提供。

这里按 EzXHelper 的主要版本给出常用写法的对照，其中 `2.x` 与 `3.x` 的用法基本一致，仅包名与初始化类不同，下方会合并展示，更多用法请参考对应章节的详细文档。

::: tip

EzXHelper `3.x` 的 `xposed-api-101` 对应 libxposed API，YukiHook 同样支持，请参考 [libxposed API](./libxposed-api.md)。

:::

## 迁移模块入口

### 初始化

EzXHelper 需要在入口中手动初始化，YukiHook 不需要，进入作用域后即可直接使用。

> 示例如下

::: code-group

```kotlin [YukiHook]
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        loadApp("io.github.myapp") {
            // Your code here.
        }
    }
}
```

```kotlin [EzXHelper 3.x]
class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXposed.initHandleLoadPackage(lpparam)

        if (lpparam.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

```kotlin [EzXHelper 2.x]
class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelper.initHandleLoadPackage(lpparam)

        if (lpparam.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

```kotlin [EzXHelper 1.x]
class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelperInit.initHandleLoadPackage(lpparam)

        if (lpparam.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

:::

详情请参考 [模块入口 → 创建入口类](../../library/yukihook-core/hook-entry.md#创建入口类)。

### 获取宿主信息

EzXHelper 中的 `ClassLoader`、`Context` 等信息由初始化后的全局属性提供，YukiHook 则直接在作用域中提供。

> 示例如下

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    // 得到当前宿主的包名
    packageName
    // 得到当前宿主的 `ClassLoader`
    hostClassLoader
    // 得到当前宿主的 `Application`，初次装载时可能为空
    hostApplication
}
```

```kotlin [EzXHelper 3.x]
// 得到当前宿主的包名
EzXposed.hookedPackageName
// 得到当前宿主的 `ClassLoader`
ClassLoaderProvider.classLoader
// 得到当前宿主的 `Context`
EzXposed.appContext
```

```kotlin [EzXHelper 2.x]
// 得到当前宿主的包名
EzXHelper.hostPackageName
// 得到当前宿主的 `ClassLoader`
EzXHelper.classLoader
// 得到当前宿主的 `Context`
EzXHelper.appContext
```

```kotlin [EzXHelper 1.x]
// 得到当前宿主的包名
hostPackageName
// 得到当前宿主的 `ClassLoader`
ezXClassLoader
// 得到当前宿主的 `Context`
appContext
```

:::

详情请参考 [模块入口 → 宿主信息](../../library/yukihook-core/hook-entry.md#宿主信息)。

## 迁移 Hook 用法

### 基本用法

EzXHelper 通过 `Method` 与 `Constructor` 的扩展方法进行 Hook，YukiHook 同样使用 `hook`，回调中的 `param` 对应 `HookParam`。

> 示例如下

::: code-group

```kotlin [YukiHook]
method.hook {
    before {
        // 得到方法参数的第一位 T
        firstArg().get<T>()
    }
    after {
        // 得到当前 Hook 的实例
        instance
        // 得到返回值
        result
    }
}
```

```kotlin [EzXHelper 2.x / 3.x]
method.createHook {
    before { param ->
        // 得到方法参数的第一位 T
        param.args[0] as T
    }
    after { param ->
        // 得到当前 Hook 的实例
        param.thisObject
        // 得到返回值
        param.result
    }
}
```

```kotlin [EzXHelper 1.x]
method.hookBefore { param ->
    // 得到方法参数的第一位 T
    param.args[0] as T
}
method.hookAfter { param ->
    // 得到当前 Hook 的实例
    param.thisObject
    // 得到返回值
    param.result
}
```

:::

详情请参考 [Hook 用法 → 基本用法](../../library/yukihook-core/hook-usage.md#基本用法)。

### 拦截方法

EzXHelper 的 `replace`、`returnConstant` 与 `interrupt` 在 YukiHook 中统一使用 `intercept`。

> 示例如下

::: code-group

```kotlin [YukiHook]
// 替换整个方法的逻辑
method.hook {
    intercept {
        // 需要返回方法对应的返回值
        0
    }
}
// 替换为一个固定的返回值
method.hook {
    intercept(true)
}
// 拦截方法并返回 null
method.hook {
    intercept()
}
```

```kotlin [EzXHelper 2.x / 3.x]
// 替换整个方法的逻辑
method.createHook {
    replace { param ->
        // 需要返回方法对应的返回值
        0
    }
}
// 替换为一个固定的返回值
method.createHook {
    returnConstant(true)
}
// 拦截方法并返回 null
method.createHook {
    interrupt()
}
```

```kotlin [EzXHelper 1.x]
// 替换整个方法的逻辑
method.hookReplace { param ->
    // 需要返回方法对应的返回值
    0
}
// 替换为一个固定的返回值
method.hookReturnConstant(true)
```

:::

详情请参考 [Hook 用法 → 拦截方法](../../library/yukihook-core/hook-usage.md#拦截方法)。

### 批量 Hook

EzXHelper 可以对 `Method` 与 `Constructor` 的集合直接 Hook，YukiHook 使用 `hookAll`。

> 示例如下

::: code-group

```kotlin [YukiHook]
methods.hookAll {
    after {
        // Your code here.
    }
}
```

```kotlin [EzXHelper 2.x / 3.x]
methods.createHooks {
    after { param ->
        // Your code here.
    }
}
```

```kotlin [EzXHelper 1.x]
methods.hookAfter { param ->
    // Your code here.
}
```

:::

详情请参考 [Hook 用法 → 基本用法](../../library/yukihook-core/hook-usage.md#基本用法)。

### 解除 Hook

EzXHelper 返回 `XC_MethodHook.Unhook`，YukiHook 返回 `Handle`。

> 示例如下

::: code-group

```kotlin [YukiHook]
val handle = method.hook {
    after {
        // Your code here.
    }
}

handle.remove()
```

```kotlin [EzXHelper 2.x / 3.x]
val unhook = method.createHook {
    after { param ->
        // Your code here.
    }
}

unhook.unhook()
```

```kotlin [EzXHelper 1.x]
val unhook = method.hookAfter { param ->
    // Your code here.
}

unhook.unhook()
```

:::

详情请参考 [Hook 用法 → 解除 Hook](../../library/yukihook-core/hook-usage.md#解除-hook)。

## 迁移反射用法

这里介绍了如何将 EzXHelper 中的反射用法迁移至 Java 原生反射与 KavaRef。

::: tip

我们推荐使用 [KavaRef](https://github.com/HighCapable/KavaRef) 作为现代的反射 API，YukiHook 为其提供了原生的 [扩展支持](../../library/yukihook-extension-kavaref.md)。

:::

下方的示例均假设 `clazz` 为需要反射的 `Class`，`instance` 为它的实例。

### 查找类

> 示例如下

::: code-group

```kotlin [KavaRef]
// 查找类
val clazz = "io.github.myapp.Demo".toClass()
// 查找类，找不到时返回 null
val clazzOrNull = "io.github.myapp.Demo".toClassOrNull()
// 在多个类名中查找第一个存在的类
val variousClazz = VariousClass("io.github.myapp.Demo", "io.github.myapp.NewDemo").toClass()
```

```kotlin [Java 原生反射]
// 查找类
val clazz = Class.forName("io.github.myapp.Demo", false, hostClassLoader)
// 查找类，找不到时返回 null
val clazzOrNull = runCatching {
    Class.forName("io.github.myapp.Demo", false, hostClassLoader)
}.getOrNull()
```

```kotlin [EzXHelper 3.x]
// 查找类
val clazz = ClassUtil.loadClass("io.github.myapp.Demo")
// 查找类，找不到时返回 null
val clazzOrNull = ClassUtil.loadClassOrNull("io.github.myapp.Demo")
// 在多个类名中查找第一个存在的类
val variousClazz = ClassUtil.loadFirstClass("io.github.myapp.Demo", "io.github.myapp.NewDemo")
```

```kotlin [EzXHelper 2.x]
// 查找类
val clazz = ClassUtils.loadClass("io.github.myapp.Demo")
// 查找类，找不到时返回 null
val clazzOrNull = ClassUtils.loadClassOrNull("io.github.myapp.Demo")
// 在多个类名中查找第一个存在的类
val variousClazz = ClassUtils.loadFirstClass("io.github.myapp.Demo", "io.github.myapp.NewDemo")
```

```kotlin [EzXHelper 1.x]
// 查找类
val clazz = loadClass("io.github.myapp.Demo")
// 查找类，找不到时返回 null
val clazzOrNull = loadClassOrNull("io.github.myapp.Demo")
// 在多个类名中查找第一个存在的类
val variousClazz = loadClassAny("io.github.myapp.Demo", "io.github.myapp.NewDemo")
```

:::

详情请参考 [yukihook-extension-kavaref → 装载宿主中的类](../../library/yukihook-extension-kavaref.md#装载宿主中的类)。

### 查找方法与构造方法

> 示例如下

::: code-group

```kotlin [KavaRef]
// 查找方法
val method = clazz.resolve().firstMethod {
    name = "foo"
    parameters(String::class)
}
// 查找构造方法
val constructor = clazz.resolve().firstConstructor {
    parameters(String::class)
}
```

```kotlin [Java 原生反射]
// 查找方法
val method = clazz.getDeclaredMethod("foo", String::class.java)
// 查找构造方法
val constructor = clazz.getDeclaredConstructor(String::class.java)
```

```kotlin [EzXHelper 2.x / 3.x]
// 查找方法
val method = MethodFinder.fromClass(clazz)
    .filterByName("foo")
    .filterByParamTypes(String::class.java)
    .first()
// 查找构造方法
val constructor = ConstructorFinder.fromClass(clazz)
    .filterByParamTypes(String::class.java)
    .first()
```

```kotlin [EzXHelper 1.x]
// 查找方法
val method = clazz.findMethod {
    name == "foo" && parameterTypes.contentEquals(arrayOf(String::class.java))
}
// 查找构造方法
val constructor = clazz.findConstructor {
    parameterTypes.contentEquals(arrayOf(String::class.java))
}
```

:::

### 读写字段与调用方法

> 示例如下

::: code-group

```kotlin [KavaRef]
// 查找字段
val field = clazz.resolve().firstField {
    name = "bar"
    type = String::class
}
// 读取实例字段
field.of(instance).get<String>()
// 修改实例字段
field.of(instance).set("some string")
// 调用实例方法
method.of(instance).invoke("some string")
// 调用静态方法
method.invoke("some string")
```

```kotlin [Java 原生反射]
// 查找字段
val field = clazz.getDeclaredField("bar")
// 调用前需要设置访问权限
field.isAccessible = true
method.isAccessible = true
// 读取实例字段
field.get(instance) as String?
// 修改实例字段
field.set(instance, "some string")
// 调用实例方法
method.invoke(instance, "some string")
// 调用静态方法
method.invoke(null, "some string")
```

```kotlin [EzXHelper 2.x / 3.x]
// 查找字段
val field = FieldFinder.fromClass(clazz)
    .filterByName("bar")
    .first()
// 读取实例字段
instance.objectHelper().getObjectOrNull("bar")
// 修改实例字段
instance.objectHelper().setObject("bar", "some string")
// 调用实例方法
instance.objectHelper().invokeMethodBestMatch("foo", null, "some string")
// 调用静态方法
clazz.classHelper().invokeStaticMethodBestMatch("foo", null, "some string")
```

```kotlin [EzXHelper 1.x]
// 查找字段
val field = clazz.findField {
    name == "bar"
}
// 读取实例字段
instance.getObjectOrNull("bar")
// 修改实例字段
instance.putObject("bar", "some string")
// 调用实例方法
instance.invokeMethod("foo", args("some string"), argTypes(String::class.java))
// 调用静态方法
clazz.invokeStaticMethod("foo", args("some string"), argTypes(String::class.java))
```

:::

### 创建实例

> 示例如下

::: code-group

```kotlin [KavaRef]
// 使用构造方法创建实例
constructor.create("some string")
// 根据传入的参数自动匹配公开的构造方法并创建实例
clazz.createInstance("some string")
```

```kotlin [Java 原生反射]
// 调用前需要设置访问权限
constructor.isAccessible = true
// 使用构造方法创建实例
constructor.newInstance("some string")
```

```kotlin [EzXHelper 2.x / 3.x]
// 使用构造方法创建实例
constructor.newInstance("some string")
// 根据传入的参数自动匹配构造方法并创建实例
clazz.classHelper().newInstanceBestMatch("some string")
```

```kotlin [EzXHelper 1.x]
// 使用构造方法创建实例
constructor.newInstance("some string")
// 根据传入的参数创建实例
clazz.newInstance(args("some string"), argTypes(String::class.java))
```

:::