import {
    InjectionKey as InjectionKeyEnhancedReadabilities,
    NolebaseEnhancedReadabilitiesMenu,
    NolebaseEnhancedReadabilitiesScreenMenu,
    type Options as EnhancedReadabilitiesOptions
} from '@nolebase/vitepress-plugin-enhanced-readabilities/client';
import '@nolebase/vitepress-plugin-enhanced-readabilities/client/style.css';
import {
    InjectionKey as InjectionKeyGitChangelogPlugin,
    NolebaseGitChangelogPlugin,
    type Options as GitChangelogOptions
} from '@nolebase/vitepress-plugin-git-changelog/client';
import '@nolebase/vitepress-plugin-git-changelog/client/style.css';
import { inBrowser, useData, useRoute, withBase, type Theme } from 'vitepress';
import DefaultTheme from 'vitepress/theme';
import giscusTalk from 'vitepress-plugin-comment-with-giscus';
import { computed, h, nextTick, onMounted, onUnmounted, watch } from 'vue';
import { giscusExcludedPages, giscusOptions } from '../configs/giscus';
import {
    defaultLocale,
    localeStorageKey,
    resolveRouteLocale,
    resolveStoredLocale
} from '../configs/i18n';
import './styles/index.scss';

const enhancedReadabilitiesOptions: EnhancedReadabilitiesOptions = {
    locales: {
        'zh-CN': {
            title: {
                title: '界面设置',
                titleAriaLabel: '界面设置菜单'
            },
            layoutSwitch: {
                title: '布局切换',
                titleAriaLabel: '布局切换菜单',
                titleHelpMessage: '切换不同的页面布局以获得最佳阅读体验',
                titleScreenNavWarningMessage: '在小屏幕上部分布局可能无法正确显示',
                optionFullWidth: '全宽',
                optionFullWidthAriaLabel: '切换为全宽布局',
                optionFullWidthHelpMessage: '内容区域与侧边栏最大宽度',
                optionSidebarWidthAdjustableOnly: '仅调整侧边栏',
                optionSidebarWidthAdjustableOnlyAriaLabel: '切换为仅可调整侧边栏宽度的布局',
                optionSidebarWidthAdjustableOnlyHelpMessage: '内容宽度固定，仅调整侧边栏',
                optionBothWidthAdjustable: '可调内容与侧边栏',
                optionBothWidthAdjustableAriaLabel: '切换为可调整内容和侧边栏宽度的布局',
                optionBothWidthAdjustableHelpMessage: '自定义自由度最大',
                optionOriginalWidth: '原始布局',
                optionOriginalWidthAriaLabel: '恢复为原始布局',
                optionOriginalWidthHelpMessage: '使用默认页面布局设置',
                contentLayoutMaxWidth: {
                    title: '内容最大宽度',
                    titleAriaLabel: '设置内容最大宽度',
                    titleHelpMessage: '调整主要内容区的最大宽度',
                    titleScreenNavWarningMessage: '在小屏幕设备上可能无效',
                    slider: '内容宽度滑块',
                    sliderAriaLabel: '滑动调整内容最大宽度',
                    sliderHelpMessage: '单位为 px'
                },
                pageLayoutMaxWidth: {
                    title: '页面最大宽度',
                    titleAriaLabel: '设置页面最大宽度',
                    titleHelpMessage: '控制整个页面容器的最大宽度',
                    titleScreenNavWarningMessage: '在小屏幕设备上可能无效',
                    slider: '页面宽度滑块',
                    sliderAriaLabel: '滑动调整页面宽度',
                    sliderHelpMessage: '单位为 px'
                }
            },
            spotlight: {
                title: '聚焦模式',
                titleAriaLabel: '聚焦模式切换',
                titleHelpMessage: '开启后将高亮当前阅读区域',
                titleScreenNavWarningMessage: '小屏幕设备上可能无法正常显示',
                optionOn: '开启',
                optionOnAriaLabel: '开启聚焦模式',
                optionOnHelpMessage: '增强段落可读性',
                optionOff: '关闭',
                optionOffAriaLabel: '关闭聚焦模式',
                optionOffHelpMessage: '恢复正常显示',
                styles: {
                    title: '聚焦样式',
                    titleAriaLabel: '选择聚焦样式',
                    titleHelpMessage: '设置聚焦时的显示方式',
                    titleScreenNavWarningMessage: '某些样式在移动设备上可能无效',
                    optionUnder: '底线高亮',
                    optionUnderAriaLabel: '底部高亮样式',
                    optionUnderHelpMessage: '在当前段落下方显示一条线',
                    optionAside: '边栏高亮',
                    optionAsideAriaLabel: '边栏高亮样式',
                    optionAsideHelpMessage: '在页面边缘显示聚焦指示器'
                }
            }
        }
    }
};

const gitChangelogOptions: GitChangelogOptions = {
    hideChangelogHeader: true,
    hideChangelogNoChangesText: true,
    displayAuthorsInsideCommitLine: true,
    locales: {
        'zh-CN': {
            changelog: {
                title: '变更日志',
                noData: '暂无最近变更日志',
                viewFullHistory: '查看完整变更日志',
                committedOn: ' 提交于 {{date}}',
                lastEdited: '最后编辑于 {{daysAgo}}',
                lastEditedDateFnsLocaleName: 'zhCN'
            }
        }
    }
};

const sidebarScrollingClass = 'is-scrolling';

/** Extends the default theme with readability controls, Git history, locale memory, sidebar behavior, and Giscus. */
export default {
    extends: DefaultTheme,
    Layout: () => h(DefaultTheme.Layout, null, {
        'nav-bar-content-after': () => h(NolebaseEnhancedReadabilitiesMenu),
        'nav-screen-content-after': () => h(NolebaseEnhancedReadabilitiesScreenMenu)
    }),
    enhanceApp({ app }) {
        app.use(NolebaseGitChangelogPlugin);
        app.provide(InjectionKeyGitChangelogPlugin, gitChangelogOptions);
        app.provide(InjectionKeyEnhancedReadabilities, enhancedReadabilitiesOptions);
    },
    setup() {
        const { frontmatter, page } = useData();
        const route = useRoute();
        let activeSidebar: HTMLElement | undefined;
        const onSidebarScroll = () => activeSidebar?.classList.add(sidebarScrollingClass);
        const attachSidebarScrollListener = () => {
            const sidebar = document.querySelector<HTMLElement>('.VPSidebar') ?? undefined;
            if (activeSidebar === sidebar) return;
            activeSidebar?.removeEventListener('scroll', onSidebarScroll);
            activeSidebar?.classList.remove(sidebarScrollingClass);
            activeSidebar = sidebar;
            activeSidebar?.addEventListener('scroll', onSidebarScroll, { passive: true });
        };
        onMounted(attachSidebarScrollListener);
        onUnmounted(() => {
            activeSidebar?.removeEventListener('scroll', onSidebarScroll);
            activeSidebar?.classList.remove(sidebarScrollingClass);
        });
        if (inBrowser)
            watch(() => route.path, (path) => {
                void nextTick(attachSidebarScrollListener);
                const locale = resolveRouteLocale(path);
                if (locale) {
                    // VitePress locale links are ordinary navigation links, so remember the resolved locale after routing.
                    try {
                        localStorage.setItem(localeStorageKey, locale);
                    } catch {
                        // Storage can be disabled by browser privacy settings without affecting the current locale page.
                    }
                    return;
                }
                const rootPaths = ['/', '/index.html', withBase('/'), withBase('/index.html')];
                if (!rootPaths.includes(path)) return;
                let selectedLocale = defaultLocale;
                try {
                    selectedLocale = resolveStoredLocale(localStorage.getItem(localeStorageKey));
                } catch {
                    // VitePress transformHead only runs for generated HTML, so dev mode keeps the English fallback here.
                }
                window.location.replace(withBase(`/${selectedLocale}/`));
            }, { immediate: true });
        // The plugin only supports exclusions through frontmatter, so project-owned exclusions are projected here.
        const commentFrontmatter = computed(() => {
            const commentsDisabled = frontmatter.value.layout === 'home' ||
                giscusExcludedPages.includes(page.value.relativePath);
            return commentsDisabled ? { ...frontmatter.value, comment: false } : frontmatter.value;
        });
        giscusTalk(giscusOptions, {
            frontmatter: commentFrontmatter,
            route
        });
    }
} satisfies Theme;