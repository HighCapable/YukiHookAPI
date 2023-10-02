---
home: true
title: Home
heroImage: /images/logo.png
actions:
  - text: Get Started
    link: /en/guide/home
    type: primary
  - text: Changelog
    link: /en/about/changelog
    type: secondary
features:
  - title: Xposed Module Develop
    details: The automatic builder can help you quickly create an Xposed Module, automatic configure the entry class and xposed_init files.
  - title: Light and Elegant
    details: A powerful, elegant, beautiful API built with Kotlin lambda can help you quickly implement method Hook and more convenient functions.
  - title: Debugging Efficient
    details: A rich debug log function, detailing the name of each hooked method, time-consuming to find the class can quickly debug and find errors.
  - title: Easy to Transplant
    details: Natively supports multiple Xposed API usages and natively connects to multiple Xposed APIs, Hook Frameworks within the supported range can be quickly integrated.
  - title: Obfuscate Support
    details: The built Xposed Module simply supports R8, obfuscate will not destroy the hook entry point, and no other configuration is required under R8.
  - title: Quickly Started
    details: Simple and easy to use it now! Do not need complex configuration and full development experience, Integrate dependencies and enjoy yourself.
footer: MIT License | Copyright (C) 2019-2023 HighCapable
---

### All Hook process in one step, everything is simplified

```kotlin
loadApp(name = "com.android.browser") {
    ActivityClass.method {
        name = "onCreate"
        param(BundleClass)
    }.hook {
        before {
          // Your code here.
        }
        after {
          // Your code here.
        }
    }
}
```