import { defineConfig } from 'vitepress';
import { configs, createThemeNavigation } from '../configs/template';

const navigation = createThemeNavigation('zh-cn');

/** Defines Simplified Chinese theme labels, navigation, and footer content. */
export default defineConfig({
    description: configs.website.locales['zh-cn'].description,
    lang: configs.website.locales['zh-cn'].lang,
    themeConfig: {
        nav: navigation.nav,
        sidebar: navigation.sidebar,
        outline: {
            level: [2, 3],
            label: '页面导航'
        },
        docFooter: {
            prev: '上一页',
            next: '下一页'
        },
        footer: {
            message: '基于 Apache-2.0 许可发布',
            copyright: '版权所有 © 2019 HighCapable'
        },
        notFound: {
            title: '页面未找到',
            quote: '看起来我们进入了错误的链接。',
            linkLabel: '回到首页',
            linkText: '返回首页'
        },
        langMenuLabel: '简体中文',
        returnToTopLabel: '回到顶部',
        sidebarMenuLabel: '菜单',
        darkModeSwitchLabel: '主题',
        lightModeSwitchTitle: '切换到浅色模式',
        darkModeSwitchTitle: '切换到深色模式',
        skipToContentLabel: '跳转到内容'
    }
});