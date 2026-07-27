import type giscusTalk from 'vitepress-plugin-comment-with-giscus';

type GiscusOptions = Parameters<typeof giscusTalk>[0];

/** Lists source pages that must not mount the Giscus comment section. */
export const giscusExcludedPages = [
    'en/about/about.md',
    'en/special-features/reflection.md',
    'zh-cn/about/about.md',
    'zh-cn/special-features/reflection.md'
];

/** Defines the GitHub Discussions repository, category, and localized Giscus behavior. */
export const giscusOptions = {
    repo: 'HighCapable/YukiHookAPI',
    repoId: 'R_kgDOGwbWNw',
    category: 'General',
    categoryId: 'DIC_kwDOGwbWN84CZ9XA',
    inputPosition: 'bottom',
    locales: {
        'en-US': 'en',
        'zh-CN': 'zh-CN'
    },
    homePageShowComment: false
} satisfies GiscusOptions;