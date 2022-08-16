## BaseFinder.IndexTypeCondition *- class*

```kotlin
inner class IndexTypeCondition internal constructor(private val type: IndexConfigType)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 字节码下标筛选实现类。

### index *- method*

```kotlin
fun index(num: Int)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置下标。

若 `index` 小于零则为倒序，此时可以使用 `IndexTypeConditionSort.reverse` 方法实现。

可使用 `IndexTypeConditionSort.first` 和 `IndexTypeConditionSort.last` 设置首位和末位筛选条件。

### index *- method*

```kotlin
fun index(): IndexTypeConditionSort
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 得到下标。

### IndexTypeConditionSort *- class*

```kotlin
inner class IndexTypeConditionSort internal constructor()
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 字节码下标排序实现类。

#### first *- method*

```kotlin
fun first()
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置满足条件的第一个。

#### last *- method*

```kotlin
fun last()
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置满足条件的最后一个。

#### reverse *- method*

```kotlin
fun reverse(num: Int)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置倒序下标。