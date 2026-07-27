---
layout: home
hero:
  name: Yuki Hook API
  tagline: An efficient Hook API and Xposed Module solution built in Kotlin
  image:
    src: /images/logo.png
    alt: Yuki Hook API
  actions:
    - text: Get Started
      link: /en/guide/home
      theme: brand
    - text: Changelog
      link: /en/about/changelog
      theme: alt
features:
  - icon: 🧩
    title: Xposed Module Develop
    details: The automatic builder can help you quickly create an Xposed Module, automatic configure the entry class and xposed_init files.
  - icon: 🪶
    title: Light and Elegant
    details: A powerful, elegant, beautiful API built with Kotlin lambda can help you quickly implement method Hook and more convenient functions.
  - icon: 🐞
    title: Debugging Efficient
    details: A rich debug log function, detailing the name of each hooked method, time-consuming to find the class can quickly debug and find errors.
  - icon: 🔄
    title: Easy to Transplant
    details: Natively supports multiple Xposed API usages and natively connects to multiple Xposed APIs, Hook Frameworks within the supported range can be quickly integrated.
  - icon: 🛡️
    title: Obfuscate Support
    details: The built Xposed Module simply supports R8, obfuscate will not destroy the hook entry point, and no other configuration is required under R8.
  - icon: ⚡
    title: Quick to Start
    details: Simple and easy to use it now! Do not need complex configuration and full development experience, Integrate dependencies and enjoy yourself.
---

### All Hook process in one step, everything is simplified

```kotlin
loadApp(name = "com.android.browser") {
    Activity::class.resolve().firstMethod {
        name = "onCreate"
        parameters(Bundle::class)
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