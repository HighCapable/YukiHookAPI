# 迁移到 YukiHookAPI 1.2.x

`YukiHookAPI` 从 `1.2.0` 版本开始进行了大量调整，你可以继续向下阅读以查看有哪些注意事项和新功能。

## 默认行为变更

从 `1.2.0` 版本开始，`@InjectYukiHookWithXposed` 中 `isUsingResourcesHook` 功能默认不再启用，如有需要请手动启用。

::: warning

Resources Hook (资源钩子) 将在 **2.0.0** 版本被移除，现已被标记 **LegacyResourcesHook**，你可以使用 **@OptIn(LegacyResourcesHook::class)** 的方式消除警告以继续在 **1.x.x** 版本使用。

:::

## 新版 API

`YukiHookAPI` 在 `1.2.0` 版本引入了 `2.0.0` 准备实现的 [New Hook Code Style](https://github.com/HighCapable/YukiHookAPI/issues/33) (新版 API)，现处于实验性阶段，你可以在 `2.0.0` 版本正式发布前，开始迁移并体验新版 API。

::: warning

所有旧版 API 已被标记 **LegacyHookApi**，你可以使用 **@OptIn(LegacyHookApi::class)** 的方式消除警告以继续使用旧版 API。

:::

例如，我们要 Hook `com.example.Test` 类中的 `test` 方法。

> 旧版 API

```kotlin
findClass("com.example.Test").hook {
    injectMember {
        method {
            name = "test"
        }
        beforeHook {
            // Your code here.
        }
        afterHook {
            // Your code here.
        }
    }
}
```

> 新版 API

```kotlin
"com.example.Test".toClass()
    .method {
        name = "test"
    }.hook {
        before {
            // Your code here.
        }
        after {
            // Your code here.
        }
    }
```

新版 API 的 Hook 对象从 `Class` 迁移到了 `Member`，这种方式将更加直观。

## 差异性功能

下面是对接新版 API 的部分差异性功能。

### 新的多重 Hook 用法

之前我们需要这样去 Hook 所有匹配条件的方法。

> 示例如下

```kotlin
injectMembers {
    method {
        name { it.contains("some") }
    }.all()
    afterHook {
        // Your code here.
    }
}
```

现在，你可以改用下面这种方式。

> 示例如下

```kotlin
method {
    name { it.contains("some") }
}.hookAll {
    after {
        // Your code here.
    }
}
```

### 新的 `allMembers(...)` 用法

之前我们需要这样去 Hook 所有方法、构造方法。

> 示例如下

```kotlin
injectMembers {
    allMembers(MembersType.METHOD)
    afterHook {
        // Your code here.
    }
}
```

```kotlin
injectMembers {
    allMembers(MembersType.CONSTRUCTOR)
    afterHook {
        // Your code here.
    }
}
```

现在，你可以改用下面这种方式。

> 示例如下

```kotlin
method().hookAll {
    after {
        // Your code here.
    }
}
```

```kotlin
constructor().hookAll {
    after {
        // Your code here.
    }
}
```

当不填写查找条件时，默认获取当前 `Class` 中的所有成员对象。

如果你想 Hook `MembersType.ALL`，目前暂时没有可以直接对接的方法，但是你可以将所有成员对象拼接后进行 Hook。

> 示例如下

```kotlin
(method().giveAll() + constructor().giveAll()).hookAll {
    after {
        // Your code here.
    }
}
```

但我们并不推荐这种做法，一次性 Hook 过多的成员是不可控的，还会发生问题。