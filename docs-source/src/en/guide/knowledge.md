# Basic Knowledge

> Here is a collection of Xposed-related introductions and the key points of knowledge that need to be grasped before start.
>
> Anyone who already knows can skip it.

The basic knowledge content <u>**not necessarily completely accurate**</u>, please read it according to your own opinion.

If you find **any errors in this page, please correct it and help us improve**.

## Related Introduction

> Here's an introduction to Xposed and how Hooks work.

### What is Xposed

> Xposed Framework is a set of open source framework services that run in Android high-privilege mode. It can affect program operation (modify the system) without modifying the APK file. Based on it, many Powerful modules that operate simultaneously without conflicting functions.

The above content is copied from Baidu Encyclopedia.

### What can Xposed do

> The structure below describes the basic workings and principles of Xposed.

```:no-line-numbers
Xposed Framework
└── App's Environment
    └── Hooker (Hooked)
        ...
    App's Environment
    └── Hooker (Hooked)
        ...
    ...
```

We can achieve the ultimate goal of controlling its behavior by injecting the **Host (App)** when the **Host (App)** is running.

This mode of operation of Xposed is called **parasitism**. The Xposed Module follows the lifecycle of the host and completes its own life course within the lifecycle of the **Host**.

We can call the **Host**'s methods, fields, and constructors through reflection, and use the Hook operation provided by `XposedBridge` to dynamically insert our own code before and after the method to be executed by the **Host (App)**, or completely replace the target, or even intercept.

### Development Process

Today's Xposed Manager has been completely replaced by its derivative works, and the era of **SuperSU** has ended, and now, with **Magisk**, everything behind is possible again.

> Its development history can be roughly divided into **Xposed(Dalvik)** → **Xposed(ART)** → **Xposed(Magisk)** → **EdXposed(Riru)**/**LSPosed(Riru/ Zygisk)**

### Derivatives

> The structure below describes how and how the Xposed-like Hook Framework works.

```:no-line-numbers
App's Environment
└── Hook Framework
    └── Hooker (Hooked)
        ...
```

Through the operation principle of Xposed, many frameworks of the same type have been derived. As mobile devices in today's era are more and more difficult to obtain Root permissions or even flash, and when they are not just needed, some Root-free frameworks are also produced, such as **LSPatch**、**TaiChi**.

These Hook Frameworks at the ART level can also complete the Hook process with the same principle as Xposed without using the Xposed API. The operating principle of Root-free is to modify the APK and inject the Hook process into the **Host**, and control it through external modules.

Another product is to use the existing functions of the Android operating environment to virtualize an environment that is completely the same as the current device system, and run App in it. This is the virtual App technology **VirtualApp**, which was later derived as **VirtualXposed** .

The Root-free frameworks mentioned above are [LSPatch](https://github.com/LSPosed/LSPatch)、[TaiChi](https://taichi.cool/)、[VirtualApp](https://github.com/asLody/VirtualApp)、[SandVXposed](https://github.com/asLody/SandVXposed).

### What YukiHookAPI does

Since Xposed appeared until now, apart from `XposedHelpers`, which is well known to developers, there is still no set of syntactic sugar for `Kotlin` and API with complete usage encapsulation.

The birth of this API framework is to hope that in the current era of Xposed, more capable Xposed Module developers can avoid detours and complete the entire development process more easily and simply.

In the future, `YukiHookAPI` will adapt to more third-party Hook Frameworks based on the goal of using the Xposed API, so as to improve the entire ecosystem and help more developers make Xposed Module development simpler and easier to understand.

## Let's Started

Before starting, you need to have the following basics to better use `YukiHookAPI`.

- Grasp and understand Android development and simple system operation principles

- To grasp and understand the internal structure of Android APK and simple decompilation knowledge, you can refer to [Jadx](https://github.com/skylot/jadx) and [ApkTool](https://github.com/iBotPeaches/Apktool)

- Grasp and proficient in using Java reflection, understand simple Smali syntax, understand Dex file structure, and use reverse analysis to locate method locations

- Grasp the basic native [Xposed API](https://api.xposed.info) usage, understand the operation principle of Xposed, see [here](https://blog.ketal.icu/en/Xposed%E6%A8%A1%E5%9D%97%E5%BC%80%E5%8F%91%E5%85%A5%E9%97%A8%E4%BF%9D%E5%A7%86%E7%BA%A7%E6%95%99%E7%A8%8B/) **(Friend Link)**

- Grasp Kotlin language and learn to use **Kotlin lambda**

- Grasp and understand Kotlin and Java mixing, calling each other, and Java bytecode generated by Kotlin