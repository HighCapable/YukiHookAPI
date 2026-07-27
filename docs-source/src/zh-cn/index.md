---
layout: home
hero:
  name: Yuki Hook API
  tagline: 一个使用 Kotlin 构建的高效 Hook API 与 Xposed 模块解决方案
  image:
    src: /images/logo.png
    alt: Yuki Hook API
  actions:
    - text: 立即开始
      link: /zh-cn/guide/home
      theme: brand
    - text: 更新日志
      link: /zh-cn/about/changelog
      theme: alt
features:
  - icon: 🧩
    title: Xposed 模块开发
    details: 自动构建程序可以帮你快速创建一个 Xposed 模块，完全省去配置入口类和 xposed_init 等文件。
  - icon: 🪶
    title: 轻量优雅
    details: 拥有一套强大、优雅、人性化、完全使用 Kotlin lambda 打造的 API，可以帮你快速实现方法 Hook 以及更多便捷功能。
  - icon: 🐞
    title: 高效调试
    details: 拥有丰富的调试日志功能，细到每个 Hook 方法的名称、所在类以及查找耗时，可进行快速调试和排错。
  - icon: 🔄
    title: 方便移植
    details: 原生支持多种 Xposed API 用法，并原生对接多种 Xposed API，支持范围内的 Hook Frameworks 都能进行快速对接。
  - icon: 🛡️
    title: 支持混淆
    details: 构建的 Xposed 模块原生支持 R8 压缩优化混淆，混淆不会破坏 Hook 入口点，R8 下无需任何其它配置。
  - icon: ⚡
    title: 快速上手
    details: 简单易用，不需要繁琐的配置，不需要十足的开发经验，搭建环境集成依赖即可立即开始使用。
---

### 所有 Hook 流程一步到位，拒绝繁琐

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