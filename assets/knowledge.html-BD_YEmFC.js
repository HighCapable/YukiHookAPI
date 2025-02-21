import{_ as s,r,o as i,c as d,b as e,d as o,e as n,a}from"./app-IYThtCMd.js";const l={},p=a(`<h1 id="basic-knowledge" tabindex="-1"><a class="header-anchor" href="#basic-knowledge" aria-hidden="true">#</a> Basic Knowledge</h1><blockquote><p>Here is a collection of Xposed-related introductions and the key points of knowledge that need to be grasped before start.</p><p>Anyone who already knows can skip it.</p></blockquote><p>The basic knowledge content <u><strong>not necessarily completely accurate</strong></u>, please read it according to your own opinion.</p><p>If you find <strong>any errors in this page, please correct it and help us improve</strong>.</p><h2 id="related-introduction" tabindex="-1"><a class="header-anchor" href="#related-introduction" aria-hidden="true">#</a> Related Introduction</h2><blockquote><p>Here&#39;s an introduction to Xposed and how Hooks work.</p></blockquote><h3 id="what-is-xposed" tabindex="-1"><a class="header-anchor" href="#what-is-xposed" aria-hidden="true">#</a> What is Xposed</h3><blockquote><p>Xposed Framework is a set of open source framework services that run in Android high-privilege mode. It can affect program operation (modify the system) without modifying the APK file. Based on it, many Powerful modules that operate simultaneously without conflicting functions.</p></blockquote><p>The above content is copied from Baidu Encyclopedia.</p><h3 id="what-can-xposed-do" tabindex="-1"><a class="header-anchor" href="#what-can-xposed-do" aria-hidden="true">#</a> What can Xposed do</h3><blockquote><p>The structure below describes the basic workings and principles of Xposed.</p></blockquote><div class="language-text" data-ext="text"><pre class="shiki github-dark-dimmed" style="background-color:#22272e;" tabindex="0"><code><span class="line"><span style="color:#adbac7;">Xposed Framework</span></span>
<span class="line"><span style="color:#adbac7;">└── App&#39;s Environment</span></span>
<span class="line"><span style="color:#adbac7;">    └── Hooker (Hooked)</span></span>
<span class="line"><span style="color:#adbac7;">        ...</span></span>
<span class="line"><span style="color:#adbac7;">    App&#39;s Environment</span></span>
<span class="line"><span style="color:#adbac7;">    └── Hooker (Hooked)</span></span>
<span class="line"><span style="color:#adbac7;">        ...</span></span>
<span class="line"><span style="color:#adbac7;">    ...</span></span>
<span class="line"><span style="color:#adbac7;"></span></span></code></pre></div><p>We can achieve the ultimate goal of controlling its behavior by injecting the <strong>Host (App)</strong> when the <strong>Host (App)</strong> is running.</p><p>This mode of operation of Xposed is called <strong>parasitism</strong>. The Xposed Module follows the lifecycle of the host and completes its own life course within the lifecycle of the <strong>Host</strong>.</p><p>We can call the <strong>Host</strong>&#39;s methods, fields, and constructors through reflection, and use the Hook operation provided by <code>XposedBridge</code> to dynamically insert our own code before and after the method to be executed by the <strong>Host (App)</strong>, or completely replace the target, or even intercept.</p><h3 id="development-process" tabindex="-1"><a class="header-anchor" href="#development-process" aria-hidden="true">#</a> Development Process</h3><p>Today&#39;s Xposed Manager has been completely replaced by its derivative works, and the era of <strong>SuperSU</strong> has ended, and now, with <strong>Magisk</strong>, everything behind is possible again.</p><blockquote><p>Its development history can be roughly divided into <strong>Xposed(Dalvik)</strong> → <strong>Xposed(ART)</strong> → <strong>Xposed(Magisk)</strong> → <strong>EdXposed(Riru)</strong>/<strong>LSPosed(Riru/ Zygisk)</strong></p></blockquote><h3 id="derivatives" tabindex="-1"><a class="header-anchor" href="#derivatives" aria-hidden="true">#</a> Derivatives</h3><blockquote><p>The structure below describes how and how the Xposed-like Hook Framework works.</p></blockquote><div class="language-text" data-ext="text"><pre class="shiki github-dark-dimmed" style="background-color:#22272e;" tabindex="0"><code><span class="line"><span style="color:#adbac7;">App&#39;s Environment</span></span>
<span class="line"><span style="color:#adbac7;">└── Hook Framework</span></span>
<span class="line"><span style="color:#adbac7;">    └── Hooker (Hooked)</span></span>
<span class="line"><span style="color:#adbac7;">        ...</span></span>
<span class="line"><span style="color:#adbac7;"></span></span></code></pre></div><p>Through the operation principle of Xposed, many frameworks of the same type have been derived. As mobile devices in today&#39;s era are more and more difficult to obtain Root permissions or even flash, and when they are not just needed, some Root-free frameworks are also produced, such as <strong>LSPatch</strong>、<strong>TaiChi</strong>.</p><p>These Hook Frameworks at the ART level can also complete the Hook process with the same principle as Xposed without using the Xposed API. The operating principle of Root-free is to modify the APK and inject the Hook process into the <strong>Host</strong>, and control it through external modules.</p><p>Another product is to use the existing functions of the Android operating environment to virtualize an environment that is completely the same as the current device system, and run App in it. This is the virtual App technology <strong>VirtualApp</strong>, which was later derived as <strong>VirtualXposed</strong> .</p>`,24),c={href:"https://github.com/LSPosed/LSPatch",target:"_blank",rel:"noopener noreferrer"},h={href:"https://taichi.cool/",target:"_blank",rel:"noopener noreferrer"},u={href:"https://github.com/asLody/VirtualApp",target:"_blank",rel:"noopener noreferrer"},g={href:"https://github.com/asLody/SandVXposed",target:"_blank",rel:"noopener noreferrer"},m=a('<h3 id="what-yukihookapi-does" tabindex="-1"><a class="header-anchor" href="#what-yukihookapi-does" aria-hidden="true">#</a> What YukiHookAPI does</h3><p>Since Xposed appeared until now, apart from <code>XposedHelpers</code>, which is well known to developers, there is still no set of syntactic sugar for Kotlin and API with complete usage encapsulation.</p><p>The birth of this API framework is to hope that in the current era of Xposed, more capable Xposed Module developers can avoid detours and complete the entire development process more easily and simply.</p><p>In the future, <code>YukiHookAPI</code> will adapt to more third-party Hook Frameworks based on the goal of using the Xposed API, so as to improve the entire ecosystem and help more developers make Xposed Module development simpler and easier to understand.</p><h2 id="let-s-started" tabindex="-1"><a class="header-anchor" href="#let-s-started" aria-hidden="true">#</a> Let&#39;s Started</h2><p>Before starting, you need to have the following basics to better use <code>YukiHookAPI</code>.</p>',6),f=e("li",null,[e("p",null,"Grasp and understand Android development and simple system operation principles")],-1),k={href:"https://github.com/skylot/jadx",target:"_blank",rel:"noopener noreferrer"},b={href:"https://github.com/iBotPeaches/Apktool",target:"_blank",rel:"noopener noreferrer"},y=e("li",null,[e("p",null,"Grasp and proficient in using Java reflection, understand simple Smali syntax, understand Dex file structure, and use reverse analysis to locate method locations")],-1),v={href:"https://api.xposed.info",target:"_blank",rel:"noopener noreferrer"},w={href:"https://blog.ketal.icu/en/Xposed%E6%A8%A1%E5%9D%97%E5%BC%80%E5%8F%91%E5%85%A5%E9%97%A8%E4%BF%9D%E5%A7%86%E7%BA%A7%E6%95%99%E7%A8%8B/",target:"_blank",rel:"noopener noreferrer"},_=e("strong",null,"(Friend Link)",-1),A=e("li",null,[e("p",null,[o("Grasp Kotlin language and learn to use "),e("strong",null,"Kotlin lambda")])],-1),x=e("li",null,[e("p",null,"Grasp and understand Kotlin and Java mixing, calling each other, and Java bytecode generated by Kotlin")],-1);function X(H,T){const t=r("ExternalLinkIcon");return i(),d("div",null,[p,e("p",null,[o("The Root-free frameworks mentioned above are "),e("a",c,[o("LSPatch"),n(t)]),o("、"),e("a",h,[o("TaiChi"),n(t)]),o("、"),e("a",u,[o("VirtualApp"),n(t)]),o("、"),e("a",g,[o("SandVXposed"),n(t)]),o(".")]),m,e("ul",null,[f,e("li",null,[e("p",null,[o("To grasp and understand the internal structure of Android APK and simple decompilation knowledge, you can refer to "),e("a",k,[o("Jadx"),n(t)]),o(" and "),e("a",b,[o("ApkTool"),n(t)])])]),y,e("li",null,[e("p",null,[o("Grasp the basic native "),e("a",v,[o("Xposed API"),n(t)]),o(" usage, understand the operation principle of Xposed, see "),e("a",w,[o("here"),n(t)]),o(),_])]),A,x])])}const P=s(l,[["render",X],["__file","knowledge.html.vue"]]);export{P as default};
