import type { HeadConfig } from 'vitepress';
import type { Plugin } from 'vite';
import { configs } from './template';

/** Locale identifiers supported by the documentation site. */
export type DocsLocale = keyof typeof configs.website.locales;

const supportedLocales = Object.keys(configs.website.locales) as DocsLocale[];
const homepagePaths = new Set(['index.md', 'en/index.md', 'zh-cn/index.md']);
const legacyRouteRedirects = {
    '/en/api/special-features/': '/en/special-features/',
    '/zh-cn/api/special-features/': '/zh-cn/special-features/'
};
const websiteBase = configs.website.base.endsWith('/') ? configs.website.base : `${configs.website.base}/`;

const resolveLegacyRouteRedirect = (requestUrl: string) => {
    const url = new URL(requestUrl, 'http://localhost');
    for (const [from, to] of Object.entries(legacyRouteRedirects)) {
        const source = websiteBase + from.slice(1);
        const sourceWithoutTrailingSlash = source.endsWith('/') ? source.slice(0, -1) : source;
        if (url.pathname !== sourceWithoutTrailingSlash && !url.pathname.startsWith(source)) continue;
        const suffix = url.pathname === sourceWithoutTrailingSlash ? '' : url.pathname.slice(source.length);
        return websiteBase + to.slice(1) + suffix + url.search;
    }
};

/** Locale used when the visitor has not selected a documentation language. */
export const defaultLocale: DocsLocale = 'en';

/** Storage key used to remember the last documentation locale visited by the user. */
export const localeStorageKey = 'yukihookapi-docs-locale';

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

/** Creates a client-side fallback redirect for legacy routes on static hosting. */
export const createLegacyRouteRedirect = (): HeadConfig => {
    const redirects = JSON.stringify(legacyRouteRedirects);
    const script = `(() => { const base = '${websiteBase}'; const path = location.pathname; for (const [from, to] of Object.entries(${redirects})) { const source = base + from.slice(1); const sourceWithoutTrailingSlash = source.endsWith('/') ? source.slice(0, -1) : source; if (path !== sourceWithoutTrailingSlash && !path.startsWith(source)) continue; const suffix = path === sourceWithoutTrailingSlash ? '' : path.slice(source.length); location.replace(base + to.slice(1) + suffix + location.search + location.hash); break; } })();`;
    return ['script', {}, script];
};

/** Creates HTTP redirects for legacy routes served by the Vite development server. */
export const createLegacyRouteRedirectPlugin = (): Plugin => ({
    name: 'yukihookapi:legacy-route-redirects',
    configureServer: (server) => {
        server.middlewares.use((request, response, next) => {
            const location = request.url ? resolveLegacyRouteRedirect(request.url) : undefined;
            if (!location) return next();
            response.statusCode = 302;
            response.setHeader('Location', location);
            response.end();
        });
    }
});