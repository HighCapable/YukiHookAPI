## BaseFinder.IndexTypeCondition [class]

```kotlin
inner class IndexTypeCondition(private val type: IndexConfigType)
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 字节码下标筛选实现类。

### index [method]

```kotlin
fun index(num: Int)
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 设置下标。

若 `index` 小于零则为倒序，此时可以使用 `IndexTypeConditionSort.reverse` 方法实现。

可使用 `IndexTypeConditionSort.first` 和 `IndexTypeConditionSort.last` 设置首位和末位筛选条件。

### index [method]

```kotlin
fun index(): IndexTypeConditionSort
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 得到下标。

### IndexTypeConditionSort [class]

```kotlin
inner class IndexTypeConditionSort
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 字节码下标排序实现类。

#### first [method]

```kotlin
fun first()
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 设置满足条件的第一个。

#### last [method]

```kotlin
fun last()
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 设置满足条件的最后一个。

#### reverse [method]

```kotlin
fun reverse(num: Int)
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 设置倒序下标。