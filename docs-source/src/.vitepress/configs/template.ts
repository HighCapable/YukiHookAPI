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
    title: Record<Locale, string>;
}

type NavigationItem = NavigationLink | NavigationGroup;

const navigationSections: NavigationGroup[] = [{
    title: { en: 'Get Started', 'zh-cn': '入门' },
    items: [
        { path: '/guide/home', title: { en: 'Introduction', 'zh-cn': '介绍' } },
        { path: '/guide/supportive', title: { en: 'Supportive', 'zh-cn': '支持性' } },
        { path: '/guide/knowledge', title: { en: 'Basic Knowledge', 'zh-cn': '基础知识' } },
        { path: '/guide/quick-start', title: { en: 'Quick Start', 'zh-cn': '快速开始' } },
        { path: '/guide/example', title: { en: 'Usage Example', 'zh-cn': '用法示例' } },
        { path: '/guide/move-to-new-api', title: { en: 'Migrate from Other Hook APIs', 'zh-cn': '从其它 Hook API 迁移' } }
    ]
}, {
    title: { en: 'Configs', 'zh-cn': '配置' },
    items: [
        { path: '/config/api-example', title: { en: 'API Basic Configs', 'zh-cn': 'API 基本配置' } },
        { path: '/config/api-exception', title: { en: 'API Exception Handling', 'zh-cn': 'API 异常处理' } },
        { path: '/config/xposed-using', title: { en: 'Use as Xposed Module Configs', 'zh-cn': '作为 Xposed 模块使用的相关配置' } },
        { path: '/config/api-using', title: { en: 'Use as Hook API Configs', 'zh-cn': '作为 Hook API 使用的相关配置' } },
        { path: '/config/move-to-api-1-2-x', title: { en: 'Migrate to YukiHookAPI 1.2.x', 'zh-cn': '迁移至 YukiHookAPI 1.2.x' } },
        { path: '/config/move-to-api-1-3-x', title: { en: 'Migrate to YukiHookAPI 1.3.x', 'zh-cn': '迁移至 YukiHookAPI 1.3.x' } },
        { path: '/config/r8-proguard', title: { en: 'R8 & Proguard Obfuscate', 'zh-cn': 'R8 与 Proguard 混淆' } }
    ]
}, {
    title: { en: 'Tools', 'zh-cn': '工具' },
    items: [{
        path: '/tools/yukihookapi-projectbuilder',
        title: { en: 'YukiHookAPI Project Builder', 'zh-cn': 'YukiHookAPI 构建工具' }
    }]
}, {
    title: { en: 'Special Features', 'zh-cn': '特色功能' },
    items: [
        { path: '/special-features/reflection', title: { en: 'Reflection Extensions (Migrated)', 'zh-cn': '字节码与反射扩展 (已迁移)' } },
        { path: '/special-features/logger', title: { en: 'Debug Logs', 'zh-cn': '调试日志' } },
        { path: '/special-features/xposed-storage', title: { en: 'Xposed Module Data Storage', 'zh-cn': 'Xposed 模块数据存储' } },
        { path: '/special-features/xposed-channel', title: { en: 'Xposed Module and Host Channel', 'zh-cn': 'Xposed 模块与宿主通讯桥' } },
        { path: '/special-features/host-lifecycle', title: { en: 'Host Lifecycle Extension', 'zh-cn': '宿主生命周期扩展' } },
        { path: '/special-features/host-inject', title: { en: 'Host Resource Injection Extension', 'zh-cn': '宿主资源注入扩展' } }
    ]
}, {
    title: { en: 'About', 'zh-cn': '关于' },
    items: [
        { path: '/about/changelog', title: { en: 'Changelog', 'zh-cn': '更新日志' } },
        { path: '/about/future', title: { en: 'Looking Toward the Future', 'zh-cn': '展望未来' } },
        { path: '/about/contacts', title: { en: 'Contact Us', 'zh-cn': '联系我们' } },
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

const createSidebarItem = (item: NavigationItem, locale: Locale): DefaultTheme.SidebarItem =>
    isNavigationGroup(item)
        ? {
            text: item.title[locale],
            collapsed: false,
            items: item.items.map((child) => createSidebarItem(child, locale))
        }
        : localizedLink(item, locale);

/** Creates the VitePress navigation and sidebar for the requested locale. */
export const createThemeNavigation = (locale: Locale) => ({
    nav: topNavigationLinks.map((link) => localizedLink(link, locale)),
    sidebar: {
        [`/${locale}/`]: navigationSections.map((section) => createSidebarItem(section, locale))
    }
});

/** Defines shared site, development server, and repository settings. */
export const configs = {
    dev: {
        dest: '../dist',
        port: 9000
    },
    website: {
        base: '/YukiHookAPI/',
        icon: '/YukiHookAPI/images/logo.png',
        logo: '/images/logo.png',
        title: 'Yuki Hook API',
        locales: {
            en: {
                lang: 'en-US',
                description: 'An efficient Hook API and Xposed Module solution built in Kotlin'
            },
            'zh-cn': {
                lang: 'zh-CN',
                description: '一个使用 Kotlin 构建的高效 Hook API 与 Xposed 模块解决方案'
            }
        }
    },
    github: {
        repo: 'https://github.com/HighCapable/YukiHookAPI',
        page: 'https://highcapable.github.io/YukiHookAPI',
        branch: 'master',
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