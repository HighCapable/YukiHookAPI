# Yuki Hook API

![Eclipse Marketplace](https://img.shields.io/badge/build-passing-brightgreen)
![Eclipse Marketplace](https://img.shields.io/badge/license-MIT-blue)
![Eclipse Marketplace](https://img.shields.io/badge/version-v1.0-green)
<br/>
🔥 An efficient Kotlin version of the Xposed Hook API.
<br/>

# 项目介绍

- 这是一个使用 Kotlin 重新构建的高效 Xposed Hook API
- 名称取自 <a href='https://www.bilibili.com/bangumi/play/ss5016/?from=search&seid=313229405371562533&spm_id_from=333.337.0.0'>
  《ももくり》女主 栗原 雪(Yuki)</a>
- 前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源

# 它能做什么

- <strong>模块开发</strong>
  自动构建程序可以帮你快速创建一个 Xposed 模块，完全省去配置入口类和 xposed_init 文件<br/>
- <strong>轻量优雅</strong>
  拥有一套强大、优雅和人性的 Koltin lambda Hook API，可以帮你快速实现 Method、Constructor、Field 的查找以及 Hook<br/>
- <strong>高效调试</strong>
  拥有丰富的调试日志功能，细到每个 Hook 方法的名称、所在类以及查找耗时，可进行快速调试和排错<br/>
- <strong>方便移植</strong>
  原生支持 Xposed API 用法，并原生对接 Xposed API，拥有 Xposed API 的 Hook 框架都能快速对接 YuKiHookAPI<br/>
- <strong>快速上手</strong>
  简单易用，不需要繁琐的配置，不需要十足的开发经验，搭建环境集成依赖即可立即开始使用

# 开始使用

- 暂定 1.0 版本
- 可做学习参考但暂时不要 Fork 也不要使用
- 还差 Wiki 没有撰写 Demo 没有写完和 API 未提交至 Maven
- 敬请期待！

# 许可证

- [MIT](https://choosealicense.com/licenses/mit)

```
MIT License

Copyright (C) 2022 HighCapable

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