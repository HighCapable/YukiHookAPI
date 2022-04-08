# 基础知识

> 这里介绍了 Xposed 以及 Hook 的工作原理，已经了解的同学可以略过。

## Xposed 是什么

> Xposed 框架(Xposed Framework)是一套开源的、在 Android 高权限模式下运行的框架服务，可以在不修改 APK 文件的情况下影响程序运行（修改系统）的框架服务，基于它可以制作出许多功能强大的模块，且在功能不冲突的情况下同时运作。

上述内容复制自百度百科。

## Xposed 能做什么

> 下方的结构描述了 Xposed 的基本工作方式和原理。

```
Xposed Framework
└── App's Environment
    └── Hooker (Hooked)
        ...
    App's Environment
    └── Hooker (Hooked)
        ...
    ...
```

我们可以在宿主(APP)运行时通过注入宿主(APP)来达到控制其行为的最终目的。

Xposed 的这种运行方式被称为<b>寄生</b>，Xposed 模块跟随宿主的生命周期，在宿主的生命周期内完成自己的生命历程。

我们可以通过反射的方式调用宿主的方法、变量、构造方法，以及使用 `XposedBridge` 所提供的 Hook 操作动态地在宿主(APP)要执行的方法前后插入自己的代码，或完全替换目标，甚至是拦截。

## 发展过程

如今的 Xposed 管理器已完全被其衍生作品替代，而 <b>SuperSU</b> 的时代也已经落幕了，现在，借助 <b>Magisk</b> 使后面的一切又成为了可能。

> 其发展史大致可分为 <b>Xposed(Dalvik)</b> → <b>Xposed(ART)</b> → <b>Xposed(Magisk)</b> → <b>EdXposed(Riru)</b>/<b>LSPosed(Riru/Zygisk)</b>

## 衍生产品

> 下方的结构描述了类似 Xposed 的 Hook Framework 的工作方式和原理。

```
App's Environment
└── Hook Framework
    └── Hooker (Hooked)
        ...
```

通过 Xposed 的运行原理，从而衍生了很多同类型框架，随着当今时代的移动设备获取 Root 权限甚至刷机越来越困难且不是刚需的时候，一些免 Root 框架也随之产生，例如<b>太极</b>。

这些在 ART 层面上的 Hook 框架同样也可不借助 Xposed API 完成其和 Xposed 原理一样的 Hook 流程，免 Root 的运行原理为修改 APK 并将 Hook 进程注入宿主，通过外部模块对其进行控制。

另外一种产品就是利用 Android 运行环境现有的功能虚拟出一个完全与当前设备系统一样的环境，并在其中运行 APP，这个就是虚拟 APP 技术 <b>VirtualApp</b>，后来衍生为 <b>VirtualXposed</b>。

上述提到的免 Root 框架分别为<b>太极/无极</b>、<b>VirtualXposed/SandVXposed</b>。

## YukiHookAPI 做了什么

自从 Xposed 出现到现在为止，除了开发者人人皆知的 `XposedHelper`，依然没有一套针对 `Kotlin` 打造的语法糖以及用法封装十分完善的 API。

本 API 框架的诞生就是希望在 Xposed 的如今时代，能让更多有动手能力的 Xposed 模块开发者少走弯路，更容易、更简单地完成整个开发流程。

未来，`YukiHookAPI` 将在使用 Xposed API 的目标基础上适配更多第三方 Hook 框架，使得整个生态得到完善，并帮助更多开发者让 Xposed 模块开发变得更加简单和易懂。