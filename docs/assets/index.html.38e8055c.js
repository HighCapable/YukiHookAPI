import{_ as s,o as n,c as a,a as l}from"./app.280c9d96.js";const e={},p=l(`<h3 id="all-hook-process-in-one-step-everything-is-simplified" tabindex="-1"><a class="header-anchor" href="#all-hook-process-in-one-step-everything-is-simplified" aria-hidden="true">#</a> All Hook process in one step, everything is simplified</h3><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#ADBAC7;">loadApp(name </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> </span><span style="color:#96D0FF;">&quot;com.android.browser&quot;</span><span style="color:#ADBAC7;">) {</span></span>
<span class="line"><span style="color:#ADBAC7;">    </span><span style="color:#F69D50;">ActivityClass</span><span style="color:#ADBAC7;">.hook {</span></span>
<span class="line"><span style="color:#ADBAC7;">        injectMember {</span></span>
<span class="line"><span style="color:#ADBAC7;">            method {</span></span>
<span class="line"><span style="color:#ADBAC7;">                name </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> </span><span style="color:#96D0FF;">&quot;onCreate&quot;</span></span>
<span class="line"><span style="color:#ADBAC7;">                param(</span><span style="color:#F69D50;">BundleClass</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#ADBAC7;">            }</span></span>
<span class="line"><span style="color:#ADBAC7;">            beforeHook {</span></span>
<span class="line"><span style="color:#ADBAC7;">                </span><span style="color:#768390;">// Your code here.</span></span>
<span class="line"><span style="color:#ADBAC7;">            }</span></span>
<span class="line"><span style="color:#ADBAC7;">            afterHook {</span></span>
<span class="line"><span style="color:#ADBAC7;">                </span><span style="color:#768390;">// Your code here.</span></span>
<span class="line"><span style="color:#ADBAC7;">            }</span></span>
<span class="line"><span style="color:#ADBAC7;">        }</span></span>
<span class="line"><span style="color:#ADBAC7;">    }</span></span>
<span class="line"><span style="color:#ADBAC7;">    resources().hook {</span></span>
<span class="line"><span style="color:#ADBAC7;">        injectResource {</span></span>
<span class="line"><span style="color:#ADBAC7;">            conditions {</span></span>
<span class="line"><span style="color:#ADBAC7;">                name </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> </span><span style="color:#96D0FF;">&quot;ic_launcher&quot;</span></span>
<span class="line"><span style="color:#ADBAC7;">                mipmap()</span></span>
<span class="line"><span style="color:#ADBAC7;">            }</span></span>
<span class="line"><span style="color:#ADBAC7;">            replaceToModuleResource(</span><span style="color:#F69D50;">R</span><span style="color:#ADBAC7;">.mipmap.ic_launcher)</span></span>
<span class="line"><span style="color:#ADBAC7;">        }</span></span>
<span class="line"><span style="color:#ADBAC7;">    }</span></span>
<span class="line"><span style="color:#ADBAC7;">}</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,2),i=[p];function o(c,r){return n(),a("div",null,i)}const d=s(e,[["render",o],["__file","index.html.vue"]]);export{d as default};
