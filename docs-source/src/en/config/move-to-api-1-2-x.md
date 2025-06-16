# Migrate to YukiHookAPI 1.2.x

`YukiHookAPI` has undergone a lot of adjustments since version `1.2.0`, you can read on to see what are the notes and new features.

::: warning

If you are using the YukiHookAPI previous version of `1.2.x`, it is recommended to refer to this document to migrate to the `1.2.x` version first.

If you are using the `1.2.x` version of `YukiHookAPI`, please read directly [Migrate to YukiHookAPI 1.3.x](move-to-api-1-3-x) instead of this document.

:::

## Default Behavior Changes

Since version `1.2.0`, the `isUsingResourcesHook` function in `@InjectYukiHookWithXposed` is no longer enabled by default, please enable it manually if necessary.

::: warning

Resources Hook will be removed in version **2.0.0** and is now marked **LegacyResourcesHook**.

You can use **@OptIn(LegacyResourcesHook::class)** to eliminate the warning, continue to use version **1.x.x**.

:::

## New API

`YukiHookAPI` introduced the [New Hook Code Style](https://github.com/HighCapable/YukiHookAPI/issues/33) (New API) of `2.0.0` in the `1.2.0` version, it is now in the experimental stage.

You can before the `2.0.0` version is officially released, start migrating and experience the New API.

::: warning

All legacy APIs have been marked **LegacyHookApi**, you can use **@OptIn(LegacyHookApi::class)** to eliminate the warning and continue to use the legacy API.

:::

For example, we want to Hook the `test` method in the `com.example.Test` class.

> Legacy API

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

> New API

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

The Hook object of the New API has been migrated from `Class` to `Member`, which will be more intuitive.

## Differential Functions

The following are some of the different functions of connecting to the new version of the API.

### New Multi-Hook Usage

Previously we needed to hook all methods that match conditions like this.

> The following example

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

Now, you can use the following method instead.

> The following example

```kotlin
method {
    name { it.contains("some") }
}.hookAll {
    after {
        // Your code here.
    }
}
```

### New `allMembers(...)` Usage

Previously we needed to hook all methods and constructors like this.

> The following example

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

Now, you can use the following method instead.

> The following example

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

When the find conditions are not filled in, all members in the current `Class` are obtained by default.

If you want to hook `MembersType.ALL`, there is currently no direct method, but you can concatenate all members and then hook.

> The following example

```kotlin
(method().giveAll() + constructor().giveAll()).hookAll {
    after {
        // Your code here.
    }
}
```

But we do not recommend this approach, too many hook members at one time are uncontrollable and problems may occur.