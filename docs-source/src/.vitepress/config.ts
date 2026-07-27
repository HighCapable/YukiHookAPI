import {
    GitChangelog,
    GitChangelogMarkdownSection
} from '@nolebase/vitepress-plugin-git-changelog/vite';
import { defineConfig, type DefaultTheme, type UserConfigFn } from 'vitepress';
import { alignI18nAnchors } from './configs/anchors';
import {
    createHomepageAlternates,
    createLegacyRouteRedirect,
    createLegacyRouteRedirectPlugin,
    createRootLocaleRedirect
} from './configs/i18n';
import { configs, pageLinkRefs } from './configs/template';
import { markdown } from './configs/utils';
import locales from './locales';

/** Creates the documentation site configuration for the active VitePress command. */
const createConfig: UserConfigFn<DefaultTheme.Config> = ({ command }) => defineConfig({
    base: configs.website.base,
    title: configs.website.title,
    description: configs.website.locales.en.description,
    outDir: configs.dev.dest,
    cacheDir: '.vitepress/cache',
    vite: {
        css: {
            preprocessorOptions: {
                scss: {
                    // Vite 5 defaults to Sass's legacy JS API, which Sass 2 will remove.
                    api: 'modern-compiler'
                }
            }
        },
        optimizeDeps: {
            exclude: [
                '@nolebase/vitepress-plugin-enhanced-readabilities/client',
                '@nolebase/ui',
                'vitepress'
            ]
        },
        server: {
            port: configs.dev.port
        },
        plugins: [
            createLegacyRouteRedirectPlugin(),
            GitChangelog({
                repoURL: () => configs.github.repo
            }),
            GitChangelogMarkdownSection({
                excludes: [
                    'index.md',
                    'en/index.md',
                    'zh-cn/index.md'
                ],
                sections: {
                    disableContributors: true
                }
            })
        ],
        ssr: {
            noExternal: [
                '@nolebase/vitepress-plugin-enhanced-readabilities',
                '@nolebase/ui'
            ]
        }
    },
    head: [
        ['meta', { name: 'color-scheme', content: 'light dark' }],
        ['link', { rel: 'icon', href: configs.website.icon }],
        createLegacyRouteRedirect()
    ],
    transformHead: ({ page }) => [
        ...createHomepageAlternates(page),
        ...createRootLocaleRedirect(page)
    ],
    locales: locales.locales,
    markdown: {
        image: {
            lazyLoading: true
        },
        config: (md) => {
            md.use(alignI18nAnchors);
            markdown.localizeContainerTitles(md);
            markdown.injectLinks(
                md,
                command === 'serve' ? pageLinkRefs.dev : pageLinkRefs.prod,
                configs.website.base
            );
        }
    },
    themeConfig: {
        logo: configs.website.logo,
        socialLinks: [{
            icon: 'github',
            link: configs.github.repo
        }],
        search: {
            provider: 'local',
            options: {
                // VitePress has no isSearchable callback; empty rendered HTML excludes the root redirect page.
                _render: (src, renderEnv, md) => renderEnv.relativePath === 'index.md' ? '' : md.render(src, renderEnv),
                locales: {
                    'zh-cn': {
                        translations: {
                            button: {
                                buttonText: '搜索',
                                buttonAriaLabel: '搜索'
                            },
                            modal: {
                                noResultsText: '无法找到相关结果',
                                resetButtonTitle: '清除查询条件',
                                footer: {
                                    selectText: '选择',
                                    navigateText: '切换',
                                    closeText: '关闭'
                                }
                            }
                        }
                    }
                }
            }
        },
        footer: {
            message: 'Released under the Apache-2.0 License',
            copyright: 'Copyright © 2019 HighCapable'
        }
    }
});

export default createConfig;