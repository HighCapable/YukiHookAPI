---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# ModifierRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class ModifierRules private constructor()
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

新增 `Class` 的描述符判断

作为 lambda 整体判断条件使用

移动到 base 包名

私有化构造方法

**功能描述**

> 这是一个 `Class`、`Member` 描述符条件实现类。

可对 R8 混淆后的 `Class`、`Member` 进行更加详细的定位。

## isPublic <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isPublic: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `public`。

## isPrivate <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isPrivate: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `private`。

## isProtected <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isProtected: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `protected`。

## isStatic <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isStatic: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `static`。

对于任意的静态 `Class`、`Member` 可添加此描述进行确定。

::: warning

Kotlin → Jvm 后的 **object** 类中的方法并不是静态的。

:::

## isFinal <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isFinal: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `final`。

::: warning

Kotlin → Jvm 后没有 **open** 符号标识的 **Class**、**Member** 和没有任何关联的 **Class**、**Member** 都将为 **final**。

:::

## isSynchronized <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isSynchronized: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `synchronized`。

## isVolatile <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isVolatile: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Field` 类型是否包含 `volatile`。

## isTransient <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isTransient: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Field` 类型是否包含 `transient`。

## isNative <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isNative: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Method` 类型是否包含 `native`。

对于任意 JNI 对接的 `Method` 可添加此描述进行确定。

## isInterface <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isInterface: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class` 类型是否包含 `interface`。

## isAbstract <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isAbstract: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `abstract`。

对于任意的抽象 `Class`、`Member` 可添加此描述进行确定。

## isStrict <span class="symbol">- i-ext-field</span>

```kotlin:no-line-numbers
val isStrict: Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.1.0` `修改`

统一合并到扩展方法并改名

**功能描述**

> `Class`、`Member` 类型是否包含 `strictfp`。