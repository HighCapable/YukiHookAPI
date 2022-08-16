## VariousClass *- class*

```kotlin
class VariousClass(vararg var name: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是一个不确定性 `Class` 类名装载器，通过 `name` 装载 `Class` 名称数组。

### get *- method*

```kotlin
fun get(loader: ClassLoader? = null): Class<*>
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 获取匹配的实体类。

使用当前 `loader` 装载目标 `Class`。