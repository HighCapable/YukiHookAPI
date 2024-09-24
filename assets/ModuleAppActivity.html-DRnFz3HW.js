import{_ as e,o as s,c as o,a as t}from"./app-CE-JDe1L.js";const a={},n=t(`<div class="custom-container warning"><p class="custom-container-title">Notice</p><p>The English translation of this page has not been completed, you are welcome to contribute translations to us.</p><p>You can use the <strong>Chrome Translation Plugin</strong> to translate entire pages for reference.</p></div><h1 id="moduleappactivity-class" tabindex="-1"><a class="header-anchor" href="#moduleappactivity-class" aria-hidden="true">#</a> ModuleAppActivity <span class="symbol">- class</span></h1><div class="language-kotlin" data-ext="kt"><pre class="shiki github-dark-dimmed" style="background-color:#22272e;" tabindex="0"><code><span class="line"><span style="color:#F47067;">open</span><span style="color:#ADBAC7;"> </span><span style="color:#F47067;">class</span><span style="color:#ADBAC7;"> </span><span style="color:#F69D50;">ModuleAppActivity</span><span style="color:#ADBAC7;"> : </span><span style="color:#F69D50;">Activity</span><span style="color:#ADBAC7;">()</span></span>
<span class="line"></span></code></pre></div><p><strong>Change Records</strong></p><p><code>v1.1.0</code> <code>added</code></p><p><strong>Function Illustrate</strong></p><blockquote><p>代理 <code>Activity</code>。</p></blockquote><p>继承于此类的 <code>Activity</code> 可以同时在宿主与模块中启动。</p><p>在 (Xposed) 宿主环境需要在宿主启动时调用 <code>Context.registerModuleAppActivities</code> 进行注册。</p><h2 id="proxyclassname-field" tabindex="-1"><a class="header-anchor" href="#proxyclassname-field" aria-hidden="true">#</a> proxyClassName <span class="symbol">- field</span></h2><div class="language-kotlin" data-ext="kt"><pre class="shiki github-dark-dimmed" style="background-color:#22272e;" tabindex="0"><code><span class="line"><span style="color:#F47067;">open</span><span style="color:#ADBAC7;"> </span><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> proxyClassName: </span><span style="color:#F69D50;">String</span></span>
<span class="line"></span></code></pre></div><p><strong>Change Records</strong></p><p><code>v1.1.10</code> <code>added</code></p><p><strong>Function Illustrate</strong></p><blockquote><p>设置当前代理的 <code>Activity</code> 类名。</p></blockquote><p>留空则使用 <code>Context.registerModuleAppActivities</code> 时设置的类名</p><div class="custom-container danger"><p class="custom-container-title">Pay Attention</p><p>代理的 <strong>Activity</strong> 类名必须存在于宿主的 AndroidMainifest 清单中。</p></div>`,17),c=[n];function p(l,i){return s(),o("div",null,c)}const d=e(a,[["render",p],["__file","ModuleAppActivity.html.vue"]]);export{d as default};