import{_ as s,o as e,c as a,a as n}from"./app.c86510cc.js";const o={},l=n(`<h1 id="xposed-\u6A21\u5757\u6570\u636E\u5B58\u50A8" tabindex="-1"><a class="header-anchor" href="#xposed-\u6A21\u5757\u6570\u636E\u5B58\u50A8" aria-hidden="true">#</a> Xposed \u6A21\u5757\u6570\u636E\u5B58\u50A8</h1><blockquote><p>\u8FD9\u662F\u4E00\u4E2A\u81EA\u52A8\u5BF9\u63A5 <code>SharedPreferences</code> \u548C <code>XSharedPreferences</code> \u7684\u9AD8\u6548\u6A21\u5757\u6570\u636E\u5B58\u50A8\u89E3\u51B3\u65B9\u6848\u3002</p></blockquote><p>\u6211\u4EEC\u9700\u8981\u5B58\u50A8\u6A21\u5757\u7684\u6570\u636E\uFF0C\u4EE5\u4F9B\u5BBF\u4E3B\u8C03\u7528\uFF0C\u8FD9\u4E2A\u65F6\u5019\u4F1A\u9047\u5230\u539F\u751F <code>Sp</code> \u5B58\u50A8\u7684\u6570\u636E\u4E92\u901A\u963B\u788D\u3002</p><p>\u539F\u751F\u7684 <code>Xposed</code> \u7ED9\u6211\u4EEC\u63D0\u4F9B\u4E86\u4E00\u4E2A <code>XSharedPreferences</code> \u7528\u4E8E\u8BFB\u53D6\u6A21\u5757\u7684 <code>Sp</code> \u6570\u636E\u3002</p><h2 id="\u5728-activity-\u4E2D\u4F7F\u7528" tabindex="-1"><a class="header-anchor" href="#\u5728-activity-\u4E2D\u4F7F\u7528" aria-hidden="true">#</a> \u5728 Activity \u4E2D\u4F7F\u7528</h2><blockquote><p>\u8FD9\u91CC\u63CF\u8FF0\u4E86\u5728 <code>Activity</code> \u4E2D\u88C5\u8F7D <code>YukiHookPrefsBridge</code> \u7684\u573A\u666F\u3002</p></blockquote><p>\u901A\u5E38\u60C5\u51B5\u4E0B\u6211\u4EEC\u53EF\u4EE5\u8FD9\u6837\u5728 Hook APP (\u5BBF\u4E3B) \u5185\u5BF9\u5176\u8FDB\u884C\u521D\u59CB\u5316\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#F69D50;">XSharedPreferences</span><span style="color:#ADBAC7;">(</span><span style="color:#F69D50;">BuildConfig</span><span style="color:#ADBAC7;">.</span><span style="color:#F69D50;">APPLICATION_ID</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div><p>\u6709\u6CA1\u6709\u65B9\u4FBF\u5FEB\u6377\u7684\u89E3\u51B3\u65B9\u6848\u5462\uFF0C\u6B64\u65F6\u4F60\u5C31\u53EF\u4EE5\u4F7F\u7528 <code>YukiHookAPI</code> \u7684\u6269\u5C55\u80FD\u529B\u5FEB\u901F\u5B9E\u73B0\u8FD9\u4E2A\u529F\u80FD\u3002</p><p>\u5F53\u4F60\u5728\u6A21\u5757\u4E2D\u5B58\u50A8\u6570\u636E\u7684\u65F6\u5019\uFF0C\u82E5\u5F53\u524D\u5904\u4E8E <code>Activity</code> \u5185\uFF0C\u53EF\u4EE5\u4F7F\u7528\u5982\u4E0B\u65B9\u6CD5\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#ADBAC7;">prefs().edit { putString(</span><span style="color:#96D0FF;">&quot;test_name&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value&quot;</span><span style="color:#ADBAC7;">) }</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div><p>\u5F53\u4F60\u5728 Hook APP (\u5BBF\u4E3B) \u4E2D\u8BFB\u53D6\u6570\u636E\u65F6\uFF0C\u53EF\u4EE5\u4F7F\u7528\u5982\u4E0B\u65B9\u6CD5\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> testName </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> prefs.getString(</span><span style="color:#96D0FF;">&quot;test_name&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;default_value&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div></div></div><p>\u4F60\u4E0D\u9700\u8981\u8003\u8651\u4F20\u5165\u6A21\u5757\u7684\u5305\u540D\u4EE5\u53CA\u4E00\u7CFB\u5217\u590D\u6742\u7684\u6743\u9650\u914D\u7F6E\uFF0C\u4E00\u5207\u90FD\u4EA4\u7ED9 <code>YukiHookPrefsBridge</code> \u6765\u5904\u7406\u3002</p><p>\u82E5\u8981\u5B9E\u73B0\u5B58\u50A8\u7684\u533A\u57DF\u5212\u5206\uFF0C\u4F60\u53EF\u4EE5\u6307\u5B9A\u6BCF\u4E2A <code>prefs</code> \u6587\u4EF6\u7684\u540D\u79F0\u3002</p><p>\u5728\u6A21\u5757\u7684 <code>Activity</code> \u4E2D\u8FD9\u6837\u4F7F\u7528\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#768390;">// \u63A8\u8350\u7528\u6CD5</span></span>
<span class="line"><span style="color:#ADBAC7;">prefs(</span><span style="color:#96D0FF;">&quot;specify_file_name&quot;</span><span style="color:#ADBAC7;">).edit { putString(</span><span style="color:#96D0FF;">&quot;test_name&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value&quot;</span><span style="color:#ADBAC7;">) }</span></span>
<span class="line"><span style="color:#768390;">// \u4E5F\u53EF\u4EE5\u8FD9\u6837\u7528</span></span>
<span class="line"><span style="color:#ADBAC7;">prefs().name(</span><span style="color:#96D0FF;">&quot;specify_file_name&quot;</span><span style="color:#ADBAC7;">).edit { putString(</span><span style="color:#96D0FF;">&quot;test_name&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value&quot;</span><span style="color:#ADBAC7;">) }</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>\u5728 Hook APP (\u5BBF\u4E3B) \u4E2D\u8FD9\u6837\u8BFB\u53D6\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#768390;">// \u63A8\u8350\u7528\u6CD5</span></span>
<span class="line"><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> testName </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> prefs(</span><span style="color:#96D0FF;">&quot;specify_file_name&quot;</span><span style="color:#ADBAC7;">).getString(</span><span style="color:#96D0FF;">&quot;test_name&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;default_value&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#768390;">// \u4E5F\u53EF\u4EE5\u8FD9\u6837\u7528</span></span>
<span class="line"><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> testName </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> prefs.name(</span><span style="color:#96D0FF;">&quot;specify_file_name&quot;</span><span style="color:#ADBAC7;">).getString(</span><span style="color:#96D0FF;">&quot;test_name&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;default_value&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>\u82E5\u4F60\u7684\u9879\u76EE\u4E2D\u6709\u5927\u91CF\u7684\u56FA\u5B9A\u6570\u636E\u9700\u8981\u5B58\u50A8\u548C\u8BFB\u53D6\uFF0C\u63A8\u8350\u4F7F\u7528 <code>PrefsData</code> \u6765\u521B\u5EFA\u6A21\u677F\u3002</p><p>\u901A\u8FC7\u4E0A\u9762\u7684\u793A\u4F8B\uFF0C\u4F60\u53EF\u4EE5\u8C03\u7528 <code>edit</code> \u65B9\u6CD5\u4F7F\u7528\u4EE5\u4E0B\u4E24\u79CD\u65B9\u5F0F\u6765\u6279\u91CF\u5B58\u50A8\u6570\u636E\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#768390;">// &lt;\u65B9\u6848 1&gt;</span></span>
<span class="line"><span style="color:#ADBAC7;">prefs().edit { </span></span>
<span class="line"><span style="color:#ADBAC7;">    putString(</span><span style="color:#96D0FF;">&quot;test_name_1&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value_1&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#ADBAC7;">    putString(</span><span style="color:#96D0FF;">&quot;test_name_2&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value_2&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#ADBAC7;">    putString(</span><span style="color:#96D0FF;">&quot;test_name_3&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value_3&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#ADBAC7;">}</span></span>
<span class="line"><span style="color:#768390;">// &lt;\u65B9\u6848 2&gt;</span></span>
<span class="line"><span style="color:#ADBAC7;">prefs().edit()</span></span>
<span class="line"><span style="color:#ADBAC7;">    .putString(</span><span style="color:#96D0FF;">&quot;test_name_1&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value_1&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#ADBAC7;">    .putString(</span><span style="color:#96D0FF;">&quot;test_name_2&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value_2&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#ADBAC7;">    .putString(</span><span style="color:#96D0FF;">&quot;test_name_3&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#96D0FF;">&quot;saved_value_3&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#ADBAC7;">    .</span><span style="color:#6CB6FF;">apply</span><span style="color:#ADBAC7;">()</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><div class="custom-container tip"><p class="custom-container-title">\u5C0F\u63D0\u793A</p><p>\u66F4\u591A\u529F\u80FD\u8BF7\u53C2\u8003 <a href="../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge">YukiHookPrefsBridge</a>\u3001<a href="../public/com/highcapable/yukihookapi/hook/xposed/prefs/data/PrefsData">PrefsData</a>\u3002</p></div><h2 id="\u5728-preferencefragment-\u4E2D\u4F7F\u7528" tabindex="-1"><a class="header-anchor" href="#\u5728-preferencefragment-\u4E2D\u4F7F\u7528" aria-hidden="true">#</a> \u5728 PreferenceFragment \u4E2D\u4F7F\u7528</h2><blockquote><p>\u8FD9\u91CC\u63CF\u8FF0\u4E86\u5728 <code>PreferenceFragment</code> \u4E2D\u88C5\u8F7D <code>YukiHookPrefsBridge</code> \u7684\u573A\u666F\u3002</p></blockquote><p>\u82E5\u4F60\u7684\u6A21\u5757\u4F7F\u7528\u4E86 <code>PreferenceFragmentCompat</code>\uFF0C\u4F60\u73B0\u5728\u53EF\u4EE5\u5C06\u5176\u7EE7\u627F\u7C7B\u5F00\u59CB\u8FC1\u79FB\u5230 <code>ModulePreferenceFragment</code>\u3002</p><div class="custom-container danger"><p class="custom-container-title">\u7279\u522B\u6CE8\u610F</p><p>\u4F60\u5FC5\u987B\u7EE7\u627F <strong>ModulePreferenceFragment</strong> \u624D\u80FD\u5B9E\u73B0 <strong>YukiHookPrefsBridge</strong> \u7684\u6A21\u5757\u5B58\u50A8\u529F\u80FD\u3002</p></div><div class="custom-container tip"><p class="custom-container-title">\u5C0F\u63D0\u793A</p><p>\u66F4\u591A\u529F\u80FD\u8BF7\u53C2\u8003 <a href="../public/com/highcapable/yukihookapi/hook/xposed/prefs/ui/ModulePreferenceFragment">ModulePreferenceFragment</a>\u3002</p></div><h2 id="\u4F7F\u7528\u539F\u751F\u65B9\u5F0F\u5B58\u50A8" tabindex="-1"><a class="header-anchor" href="#\u4F7F\u7528\u539F\u751F\u65B9\u5F0F\u5B58\u50A8" aria-hidden="true">#</a> \u4F7F\u7528\u539F\u751F\u65B9\u5F0F\u5B58\u50A8</h2><p>\u5728\u6A21\u5757\u73AF\u5883\u4E2D <code>YukiHookPrefsBridge</code> \u9ED8\u8BA4\u4F1A\u5C06\u6570\u636E\u5B58\u50A8\u5230\u6A21\u5757\u81EA\u5DF1\u7684\u79C1\u6709\u76EE\u5F55 (\u6216 Hook Framework \u63D0\u4F9B\u7684\u5171\u4EAB\u76EE\u5F55) \u4E2D\u3002</p><p>\u5728\u5BBF\u4E3B\u73AF\u5883\u4E2D\u4F7F\u7528 <code>YukiHookPrefsBridge</code> \u9ED8\u8BA4\u4F1A\u8BFB\u53D6\u6A21\u5757\u81EA\u5DF1\u7684\u79C1\u6709\u76EE\u5F55 (\u6216 Hook Framework \u63D0\u4F9B\u7684\u5171\u4EAB\u76EE\u5F55) \u4E2D\u7684\u6570\u636E\u3002</p><p>\u5982\u679C\u4F60\u60F3\u76F4\u63A5\u5C06\u6570\u636E\u5B58\u50A8\u5230\u6A21\u5757\u6216\u5BBF\u4E3B\u5F53\u524D\u73AF\u5883\u81EA\u8EAB\u7684\u79C1\u6709\u76EE\u5F55\uFF0C\u4F60\u53EF\u4EE5\u4F7F\u7528 <code>native</code> \u65B9\u6CD5\u3002</p><p>\u4F8B\u5982\u6A21\u5757\u7684\u76EE\u5F55\u662F <code>.../com.demo.test.module/shared_prefs</code>\uFF0C\u5BBF\u4E3B\u7684\u76EE\u5F55\u662F <code>.../com.demo.test.host/shared_prefs</code>\u3002</p><p>\u4EE5\u4E0B\u662F\u5728 <code>Activity</code> \u4E2D\u7684\u7528\u6CD5\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#768390;">// \u5B58\u50A8\u79C1\u6709\u6570\u636E</span></span>
<span class="line"><span style="color:#ADBAC7;">prefs().native().edit { putBoolean(</span><span style="color:#96D0FF;">&quot;isolation_data&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#6CB6FF;">true</span><span style="color:#ADBAC7;">) }</span></span>
<span class="line"><span style="color:#768390;">// \u8BFB\u53D6\u79C1\u6709\u6570\u636E</span></span>
<span class="line"><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> privateData </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> prefs().native().getBoolean(</span><span style="color:#96D0FF;">&quot;isolation_data&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#768390;">// \u5B58\u50A8\u5171\u4EAB\u6570\u636E</span></span>
<span class="line"><span style="color:#ADBAC7;">prefs().edit { putBoolean(</span><span style="color:#96D0FF;">&quot;public_data&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#6CB6FF;">true</span><span style="color:#ADBAC7;">) }</span></span>
<span class="line"><span style="color:#768390;">// \u8BFB\u53D6\u5171\u4EAB\u6570\u636E</span></span>
<span class="line"><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> publicData </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> prefs().getBoolean(</span><span style="color:#96D0FF;">&quot;public_data&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>\u4EE5\u4E0B\u662F\u5728 <code>PackageParam</code> \u4E2D\u7684\u7528\u6CD5\u3002</p><blockquote><p>\u793A\u4F8B\u5982\u4E0B</p></blockquote><div class="language-kotlin ext-kt line-numbers-mode"><pre class="shiki" style="background-color:#22272e;"><code><span class="line"><span style="color:#768390;">// \u5B58\u50A8\u79C1\u6709\u6570\u636E</span></span>
<span class="line"><span style="color:#ADBAC7;">prefs.native().edit { putBoolean(</span><span style="color:#96D0FF;">&quot;isolation_data&quot;</span><span style="color:#ADBAC7;">, </span><span style="color:#6CB6FF;">true</span><span style="color:#ADBAC7;">) }</span></span>
<span class="line"><span style="color:#768390;">// \u8BFB\u53D6\u79C1\u6709\u6570\u636E</span></span>
<span class="line"><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> privateData </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> prefs.native().getBoolean(</span><span style="color:#96D0FF;">&quot;isolation_data&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"><span style="color:#768390;">// \u8BFB\u53D6\u5171\u4EAB\u6570\u636E</span></span>
<span class="line"><span style="color:#F47067;">val</span><span style="color:#ADBAC7;"> publicData </span><span style="color:#F47067;">=</span><span style="color:#ADBAC7;"> prefs.getBoolean(</span><span style="color:#96D0FF;">&quot;public_data&quot;</span><span style="color:#ADBAC7;">)</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><p>\u4F7F\u7528 <code>native</code> \u65B9\u6CD5\u540E\uFF0C\u65E0\u8BBA\u5728 <code>Activity</code> \u8FD8\u662F <code>PackageParam</code> \u4E2D\u90FD\u4F1A\u5C06\u6570\u636E<u><strong>\u5728\u5BF9\u5E94\u73AF\u5883\u7684\u79C1\u6709\u76EE\u5F55\u4E2D</strong></u>\u5B58\u50A8\u3001\u8BFB\u53D6\uFF0C\u6570\u636E\u76F8\u4E92\u9694\u79BB\u3002</p><div class="custom-container tip"><p class="custom-container-title">\u5C0F\u63D0\u793A</p><p>\u66F4\u591A\u529F\u80FD\u8BF7\u53C2\u8003 <a href="../public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge">YukiHookPrefsBridge</a>\u3002</p></div>`,47),p=[l];function t(c,r){return e(),a("div",null,p)}const d=s(o,[["render",t],["__file","xposed-storage.html.vue"]]);export{d as default};
