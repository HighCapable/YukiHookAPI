## HookClass [class]

```kotlin
class HookClass(var instance: Class<*>?, var name: String, var throwable: Throwable?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 创建一个当前 Hook 的 `Class` 接管类。

`instance` 为实例，`name` 为实例完整包名，`throwable` 为找不到实例的时候抛出的异常。