import type { HeadConfig } from 'vitepress';
import { configs } from './template';

/** Locale identifiers supported by the documentation site. */
export type DocsLocale = keyof typeof configs.website.locales;

const supportedLocales = Object.keys(configs.website.locales) as DocsLocale[];
const homepagePaths = new Set(['index.md', 'en/index.md', 'zh-cn/index.md']);

/** Locale used when the visitor has not selected a documentation language. */
export const defaultLocale: DocsLocale = 'en';

/** Storage key used to remember the last documentation locale visited by the user. */
export const localeStorageKey = 'yukihook-docs-locale';

/** Returns a supported locale when the route belongs to a localized documentation tree. */
export const resolveRouteLocale = (path: string) => path
    .split('/')
    .find((segment): segment is DocsLocale => supportedLocales.includes(segment as DocsLocale));

/** Returns a stored locale when valid, otherwise falling back to English. */
export const resolveStoredLocale = (locale: string | null) => supportedLocales
    .find((supportedLocale) => supportedLocale === locale) ?? defaultLocale;

/** Creates reciprocal hreflang links for every localized homepage and the x-default root. */
export const createHomepageAlternates = (page: string): HeadConfig[] => {
    if (!homepagePaths.has(page)) return [];
    const siteRoot = `${configs.github.page}/`;
    const localeLinks = Object.entries(configs.website.locales).map(([locale, options]) => [
        'link',
        {
            rel: 'alternate',
            hreflang: options.lang,
            href: `${siteRoot}${locale}/`
        }
    ] satisfies HeadConfig);
    return [
        ...localeLinks,
        ['link', { rel: 'alternate', hreflang: 'x-default', href: siteRoot }]
    ];
};

/** Creates the root-page fallback redirect used when static hosting cannot return HTTP 302. */
export const createRootLocaleRedirect = (page: string): HeadConfig[] => {
    if (page !== 'index.md') return [];
    const base = configs.website.base;
    const script = `(() => { let locale = '${defaultLocale}'; try { const saved = localStorage.getItem('${localeStorageKey}'); if (saved === 'en' || saved === 'zh-cn') locale = saved; } catch {} location.replace('${base}' + locale + '/'); })();`;
    return [['script', {}, script]];
};