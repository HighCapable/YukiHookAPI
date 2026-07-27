import { resolveI18nLink } from './anchors';
import type { VitePressMarkdownIt } from './types';

const containerTypes = ['tip', 'warning', 'danger'] as const;
const containerTitles = {
    en: {
        tip: 'Tips',
        warning: 'Notice',
        danger: 'Pay Attention'
    },
    'zh-cn': {
        tip: '小提示',
        warning: '注意',
        danger: '特别注意'
    }
};

/** Provides Markdown renderer hooks shared by development and production builds. */
export const markdown = {
    /** Localizes default custom-container titles while preserving titles declared in Markdown. */
    localizeContainerTitles: (md: VitePressMarkdownIt) => {
        for (const type of containerTypes) {
            const ruleName = `container_${type}_open`;
            const defaultRender = md.renderer.rules[ruleName];
            if (!defaultRender)
                continue;
            md.renderer.rules[ruleName] = function (tokens, idx, options, renderEnv, self) {
                const token = tokens[idx];
                const originalInfo = token.info;
                if (originalInfo.trim() !== type)
                    return defaultRender(tokens, idx, options, renderEnv, self);
                const locale = renderEnv.relativePath?.startsWith('zh-cn/') ? 'zh-cn' : 'en';
                token.info = `${type} ${containerTitles[locale][type]}`;
                try {
                    return defaultRender(tokens, idx, options, renderEnv, self);
                } finally {
                    token.info = originalInfo;
                }
            };
        }
    },
    injectLinks: (md: VitePressMarkdownIt, maps: Record<string, string>[], base: string) => {
        const defaultRender = md.renderer.rules.link_open || function (tokens, idx, options, _env, self) {
            return self.renderToken(tokens, idx, options);
        };
        md.renderer.rules.link_open = function (tokens, idx, options, renderEnv, self) {
            const hrefIndex = tokens[idx].attrIndex('href');
            if (hrefIndex < 0 || !tokens[idx].attrs)
                return defaultRender(tokens, idx, options, renderEnv, self);
            let current = tokens[idx].attrs[hrefIndex][1];
            current = resolveI18nLink({
                base,
                filePathRelative: renderEnv.relativePath
            }, current);
            for (const map of maps) {
                for (const [search, replace] of Object.entries(map)) {
                    if (current.startsWith(search)) {
                        current = current.replace(search, replace);
                        break;
                    }
                }
            }
            tokens[idx].attrs[hrefIndex][1] = current;
            return defaultRender(tokens, idx, options, renderEnv, self);
        };
    }
};