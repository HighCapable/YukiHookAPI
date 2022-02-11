# Yuki Hook API (完善中)

![Eclipse Marketplace](https://img.shields.io/badge/build-passing-brightgreen)
![Eclipse Marketplace](https://img.shields.io/badge/license-MIT-blue)
![Eclipse Marketplace](https://img.shields.io/badge/version-v1.0-green)
<br/><br/>
<img src="https://github.com/fankes/YuKiHookAPI/blob/master/demo-app/src/main/res/mipmap-xxhdpi/ic_icon.png" width = "100" height = "100"/>
<br/>
<br/>
⛱️ An efficient Kotlin version of the Xposed Hook API.
<br/>

# What's this

- 这是一个使用 Kotlin 重新构建的高效 Xposed Hook API
- 名称取自 <a href='https://www.bilibili.com/bangumi/play/ss5016/?from=search&seid=313229405371562533&spm_id_from=333.337.0.0'>
  《ももくり》女主 栗原 雪(Yuki)</a>
- 前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源

# Function

- <strong>Xposed 模块开发</strong><br/>
  自动构建程序可以帮你快速创建一个 Xposed 模块，完全省去配置入口类和 xposed_init 文件。<br/>
- <strong>轻量优雅</strong><br/>
  拥有一套强大、优雅和人性化的 Kotlin Lambda Hook API，可以帮你快速实现 Method、Constructor、Field 的查找以及 Hook。<br/>
- <strong>高效调试</strong><br/>
  拥有丰富的调试日志功能，细到每个 Hook 方法的名称、所在类以及查找耗时，可进行快速调试和排错。<br/>
- <strong>方便移植</strong><br/>
  原生支持 Xposed API 用法，并原生对接 Xposed API，拥有 Xposed API 的 Hook 框架都能快速对接 Yuki Hook API。<br/>
- <strong>支持混淆</strong><br/>
  使用 Yuki Hook API 构建的 Xposed 模块原生支持 R8 压缩优化混淆，混淆不会破坏 Hook 入口点，R8 下无需任何其它配置。<br/>
- <strong>快速上手</strong><br/>
  简单易用，不需要繁琐的配置，不需要十足的开发经验，搭建环境集成依赖即可立即开始使用。

# Compatibility

| Hook Framework                                            | ST | Describe                                                                                 |
| --------------------------------------------------------- | -- | ---------------------------------------------------------------------------------------- |
| [LSPosed](https://github.com/LSPosed/LSPosed)             | ✅ | 多场景下稳定使用                                                                           |
| [EdXposed](https://github.com/ElderDrivers/EdXposed)      | ☑  | 部分兼容                                                                                  |
| [Pine](https://github.com/canyie/pine)                    | ⭕ | 可以使用                                                                                  |
| [SandHook](https://github.com/asLody/SandHook)            | ⭕ | 可以使用                                                                                  |
| [Whale](https://github.com/asLody/whale)                  | ⭕ | 需要 [xposed-hook-based-on-whale](https://github.com/WindySha/xposed-hook-based-on-whale) |
| [YAHFA](https://github.com/PAGalaxyLab/YAHFA)             | ❗ | 需要自行实现 Xposed API                                                                    |
| [FastHook](https://github.com/turing-technician/FastHook) | ❗ | 需要自行实现 Xposed API                                                                    |
| [Epic](https://github.com/tiann/epic)                     | ❗ | 需要自行对接 [Dexposed](https://github.com/alibaba/dexposed)                               |
| [TaiChi](https://github.com/taichi-framework/TaiChi)      | ⭕ | 可以作为模块使用                                                                           |
| [Xposed](https://github.com/rovo89/Xposed)                | ❎ | 未测试，不再推荐使用                                                                        |

# Get Started

- ❗相关依赖暂未上传 Maven，将在稍后完成。
- 你可以点击[快速开始](https://github.com/fankes/YukiHookAPI/wiki#%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B)，集成 `YukiHookAPI` 并开始使用。
- 更多使用教程及 API 文档请[前往 Wiki 主页](https://github.com/fankes/YukiHookAPI/wiki)进行查看。

# Donate

- 工作不易，无意外情况此项目将继续维护下去，提供更多可能，欢迎打赏。<br/>
  <img src="https://github.com/fankes/YuKiHookAPI/blob/master/imgSrc/wechat_code.jpg" width = "200" height = "200"/>

# License

- [MIT](https://choosealicense.com/licenses/mit)

```
MIT License

Copyright (C) 2019-2022 HighCapable

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```