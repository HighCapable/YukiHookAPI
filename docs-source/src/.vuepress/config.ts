import { defaultTheme } from 'vuepress';
import { shikiPlugin } from '@vuepress/plugin-shiki';
import { searchPlugin } from '@vuepress/plugin-search';
import { navBarItems, sideBarItems, configs } from './configs/template';

export default {
    dest: configs.dev.dest,
    port: configs.dev.port,
    base: configs.website.base,
    head: [['link', { rel: 'icon', href: configs.website.icon }]],
    title: configs.website.title,
    description: configs.website.locales['/en/'].description,
    locales: configs.website.locales,
    theme: defaultTheme({
        logo: configs.website.logo,
        repo: configs.github.repo,
        docsRepo: configs.github.repo,
        docsBranch: configs.github.branch,
        docsDir: configs.github.dir,
        editLinkPattern: ':repo/edit/:branch/:path',
        sidebar: sideBarItems,
        sidebarDepth: 2,
        locales: {
            '/en/': {
                navbar: navBarItems['/en/'],
                selectLanguageText: 'English (US)',
                selectLanguageName: 'English',
                editLinkText: 'Edit this page on GitHub',
                tip: 'Tips',
                warning: 'Notice',
                danger: 'Pay Attention',
            },
            '/zh-cn/': {
                navbar: navBarItems['/zh-cn/'],
                selectLanguageText: '简体中文 (CN)',
                selectLanguageName: '简体中文',
                editLinkText: '在 GitHub 上编辑此页',
                notFound: ['这里什么都没有', '我们怎么到这来了？', '这是一个 404 页面', '看起来我们进入了错误的链接'],
                backToHome: '回到首页',
                contributorsText: '贡献者',
                lastUpdatedText: '上次更新',
                tip: '小提示',
                warning: '注意',
                danger: '特别注意',
                openInNewWindow: '在新窗口中打开',
                toggleColorMode: '切换颜色模式'
            }
        },
    }),
    plugins: [
        shikiPlugin({ theme: 'github-dark-dimmed' }),
        searchPlugin({
            isSearchable: (page) => page.path !== '/',
            locales: {
                '/en/': { placeholder: 'Search' },
                '/zh-cn/': { placeholder: '搜索' }
            }
        })
    ]
};