import type { DefaultTheme } from 'vitepress';

type Locale = 'en' | 'zh-cn';

interface PageLinkRefs {
    dev: Record<string, string>[];
    prod: Record<string, string>[];
}

interface NavigationLink {
    path: string;
    title: Record<Locale, string>;
}

interface NavigationGroup {
    items: NavigationItem[];
    path?: string;
    title: Record<Locale, string>;
}

type NavigationItem = NavigationLink | NavigationGroup;

const navigationSections: NavigationGroup[] = [{
    title: { en: 'Get Started', 'zh-cn': '入门' },
    items: [
        { path: '/guide/home', title: { en: 'Introduction', 'zh-cn': '介绍' } },
        { path: '/guide/quick-start', title: { en: 'Quick Start', 'zh-cn': '快速开始' } }
    ]
}, {
    title: { en: 'Libraries', 'zh-cn': '依赖' },
    items: [
        { path: '/library/yukihook-bom', title: { en: 'yukihook-bom', 'zh-cn': 'yukihook-bom' } },
        {
            path: '/library/yukihook-core',
            title: { en: 'yukihook-core', 'zh-cn': 'yukihook-core' },
            items: [
                {
                    path: '/library/yukihook-core/hook-entry',
                    title: { en: 'Module Entry', 'zh-cn': '模块入口' },
                    items: [
                        { path: '/library/yukihook-core/hook-entry/multi-entry-variants', title: { en: 'Multi-Entry Variants', 'zh-cn': '多入口变体' } }
                    ]
                },
                { path: '/library/yukihook-core/hook-usage', title: { en: 'Hook Usage', 'zh-cn': 'Hook 用法' } },
                { path: '/library/yukihook-core/logger', title: { en: 'Debug Logs', 'zh-cn': '调试日志' } },
                { path: '/library/yukihook-core/module-status', title: { en: 'Module Status', 'zh-cn': '模块状态' } },
                { path: '/library/yukihook-core/preferences', title: { en: 'Module Preferences', 'zh-cn': '模块偏好设置' } },
                { path: '/library/yukihook-core/shared-files', title: { en: 'Module Shared Files', 'zh-cn': '模块共享文件' } },
                { path: '/library/yukihook-core/data-channel', title: { en: 'Module and Host Channel', 'zh-cn': '模块与宿主通讯桥' } },
                { path: '/library/yukihook-core/host-lifecycle', title: { en: 'Host Lifecycle', 'zh-cn': '宿主生命周期' } },
                { path: '/library/yukihook-core/resource-injection', title: { en: 'Module Resource Injection', 'zh-cn': '模块资源注入' } },
                { path: '/library/yukihook-core/hot-reload', title: { en: 'Module Hot Reload', 'zh-cn': '模块热重载' } }
            ]
        },
        { path: '/library/yukihook-compiler', title: { en: 'yukihook-compiler', 'zh-cn': 'yukihook-compiler' } },
        { path: '/library/yukihook-extension-kavaref', title: { en: 'yukihook-extension-kavaref', 'zh-cn': 'yukihook-extension-kavaref' } },
        { path: '/library/yukihook-runtime-libxposed', title: { en: 'yukihook-runtime-libxposed', 'zh-cn': 'yukihook-runtime-libxposed' } },
        { path: '/library/yukihook-runtime-xposed82', title: { en: 'yukihook-runtime-xposed82', 'zh-cn': 'yukihook-runtime-xposed82' } }
    ]
}, {
    title: { en: 'Configs', 'zh-cn': '配置' },
    items: [
        { path: '/config/r8-proguard', title: { en: 'R8 & ProGuard Obfuscation', 'zh-cn': 'R8 与 Proguard 混淆' } },
        {
            path: '/config/migration',
            title: { en: 'Migration to YukiHook', 'zh-cn': '迁移至 YukiHook' },
            items: [
                { path: '/config/migration/xposed-framework-api', title: { en: 'Xposed Framework API', 'zh-cn': 'Xposed Framework API' } },
                { path: '/config/migration/libxposed-api', title: { en: 'libxposed API', 'zh-cn': 'libxposed API' } },
                { path: '/config/migration/ezxhelper', title: { en: 'EzXHelper', 'zh-cn': 'EzXHelper' } }
            ]
        },
        { path: '/config/migration-1-5-x', title: { en: 'Migration to YukiHook 1.5.x', 'zh-cn': '迁移至 YukiHook 1.5.x' } },
        { path: '/config/lint-rules', title: { en: 'Lint Rules', 'zh-cn': 'Lint 静态检查规范' } }
    ]
}, {
    title: { en: 'About', 'zh-cn': '关于' },
    items: [
        { path: '/about/changelog', title: { en: 'Changelog', 'zh-cn': '更新日志' } },
        { path: '/about/future', title: { en: 'Looking Toward the Future', 'zh-cn': '展望未来' } },
        { path: '/about/contacts', title: { en: 'Contact Us', 'zh-cn': '联系我们' } },
        { path: '/about/adopters', title: { en: 'Adopters', 'zh-cn': '谁在使用' } },
        { path: '/about/about', title: { en: 'About This Document', 'zh-cn': '关于此文档' } }
    ]
}];

const topNavigationLinks: NavigationLink[] = [
    { path: '/', title: { en: 'Home', 'zh-cn': '首页' } },
    { path: '/guide/quick-start', title: { en: 'Quick Start', 'zh-cn': '快速开始' } },
    { path: '/about/changelog', title: { en: 'Changelog', 'zh-cn': '更新日志' } },
    { path: '/about/contacts', title: { en: 'Contact Us', 'zh-cn': '联系我们' } }
];

const isNavigationGroup = (item: NavigationItem): item is NavigationGroup => 'items' in item;

const localizedLink = (link: NavigationLink, locale: Locale) => ({
    text: link.title[locale],
    link: `/${locale}${link.path}`
});

const createSidebarItem = (item: NavigationItem, locale: Locale, isTopLevel = false): DefaultTheme.SidebarItem =>
    isNavigationGroup(item) ? {
        text: item.title[locale],
        link: item.path ? `/${locale}${item.path}` : undefined,
        collapsed: !isTopLevel,
        items: item.items.map((child) => createSidebarItem(child, locale))
    } : localizedLink(item, locale);

/** Creates the VitePress navigation and sidebar for the requested locale. */
export const createThemeNavigation = (locale: Locale) => ({
    nav: topNavigationLinks.map((link) => localizedLink(link, locale)),
    sidebar: {
        [`/${locale}/`]: navigationSections.map((section) => createSidebarItem(section, locale, true))
    }
});

/** Defines shared site, development server, and repository settings. */
export const configs = {
    dev: {
        dest: '../dist',
        port: 9000
    },
    website: {
        base: '/YukiHook/',
        icon: '/YukiHook/images/logo.svg',
        logo: '/images/logo.svg',
        title: 'Yuki Hook',
        locales: {
            en: {
                lang: 'en-US',
                description: 'A modern hook API and Xposed module solution'
            },
            'zh-cn': {
                lang: 'zh-CN',
                description: '一个现代化的 Hook API 与 Xposed 模块解决方案'
            }
        }
    },
    github: {
        repo: 'https://github.com/HighCapable/YukiHook',
        page: 'https://highcapable.github.io/YukiHook',
        branch: '1.x',
        dir: 'docs-source/src'
    }
};

/** Defines custom Markdown link protocol replacements for each build mode. */
export const pageLinkRefs: PageLinkRefs = {
    dev: [
        { 'repo://': `${configs.github.repo}/` },
        // Run ./build-dokka.sh and serve dist/KDoc on port 9001 for local KDoc debugging.
        { 'kdoc://': 'http://localhost:9001/' }
    ],
    prod: [
        { 'repo://': `${configs.github.repo}/` },
        { 'kdoc://': `${configs.github.page}/KDoc/` }
    ]
};