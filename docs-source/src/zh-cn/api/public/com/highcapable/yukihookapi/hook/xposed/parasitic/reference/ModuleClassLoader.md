---
pageClass: code-page
---

# ModuleClassLoader <span class="symbol">- class</span>

```kotlin:no-line-numbers
class ModuleClassLoader private constructor() : ClassLoader
```

**变更记录**

`v1.1.2` `新增`

**功能描述**

> 自动处理 (Xposed) 宿主环境与模块环境的 `ClassLoader`。

## companion object <span class="symbol">- object</span>

**变更记录**

`v1.1.2` `新增`

### excludeHostClasses <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun excludeHostClasses(vararg name: String)
```

**变更记录**

`v1.1.2` `新增`

**功能描述**

> 添加到 Hook APP (宿主) `Class` 排除列表。

排除列表中的 `Class` 将会使用宿主的 `ClassLoader` 进行装载。

::: danger

排除列表仅会在 (Xposed) 宿主环境生效。

:::

### excludeModuleClasses <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun excludeModuleClasses(vararg name: String)
```

**变更记录**

`v1.1.2` `新增`

**功能描述**

> 添加到模块 `Class` 排除列表。

排除列表中的 `Class` 将会使用模块 (当前宿主环境的模块注入进程) 的 `ClassLoader` 进行装载。

::: danger

排除列表仅会在 (Xposed) 宿主环境生效。

:::