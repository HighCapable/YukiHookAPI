import { existsSync, readFileSync, statSync } from 'node:fs';
import path from 'node:path';
import MarkdownIt from 'markdown-it';
import { slugify } from '@mdit-vue/shared';
import type { VitePressMarkdownIt } from './types';

interface HeadingAnchor {
    alignedId: string;
    localizedSlug: string;
    pathKey: string;
}

interface HeadingLookup {
    byPathKey: Map<string, HeadingAnchor>;
    bySlug: Map<string, string>;
}

interface CachedHeadingLookup {
    lookup: HeadingLookup;
    mtimeMs: number;
}

interface MarkdownToken {
    children: MarkdownToken[] | null;
    content: string;
    tag: string;
    type: string;
    attrGet: (name: string) => string | null;
    attrSet: (name: string, value: string) => void;
}

interface HeadingToken {
    inlineToken: MarkdownToken;
    pathKey: string;
    token: MarkdownToken;
}

interface LinkResolveContext {
    base?: string;
    filePathRelative?: string | null;
}

const sourceRoot = path.resolve(process.cwd(), 'src');
const englishRoot = path.join(sourceRoot, 'en');
const headingParser = new MarkdownIt({ html: true });
const headingLookupCache = new Map<string, CachedHeadingLookup>();

const createHeadingPathTracker = (): ((level: number) => string) => {
    const indexes: number[] = [];
    return (level: number): string => {
        const slot = Math.max(level - 1, 0);
        indexes.length = slot + 1;
        for (let index = 0; index < slot; index += 1)
            if (typeof indexes[index] !== 'number')
                indexes[index] = 0;
        indexes[slot] = (indexes[slot] ?? -1) + 1;
        return indexes.join('.');
    };
};

const getHeadingLevel = (token: MarkdownToken) => Number.parseInt(token.tag.slice(1), 10);

const getHeadingText = (inlineToken: MarkdownToken) =>
    (inlineToken.children ?? [])
        .filter((child) => child.type === 'text' || child.type === 'code_inline')
        .map((child) => child.content)
        .join('')
        .trim();

const createUniqueSlug = (candidate: string, usedSlugs: Set<string>) => {
    let resolvedSlug = candidate;
    let suffix = 1;
    while (usedSlugs.has(resolvedSlug)) {
        resolvedSlug = `${candidate}-${suffix}`;
        suffix += 1;
    }
    usedSlugs.add(resolvedSlug);
    return resolvedSlug;
};

const collectHeadings = (tokens: MarkdownToken[]) => {
    const nextPathKey = createHeadingPathTracker();
    const usedSlugs = new Set<string>();
    const headings: HeadingAnchor[] = [];
    for (let index = 0; index < tokens.length; index += 1) {
        const token = tokens[index];
        if (token?.type !== 'heading_open')
            continue;
        const inlineToken = tokens[index + 1];
        if (!inlineToken)
            continue;
        const level = getHeadingLevel(token);
        const title = getHeadingText(inlineToken);
        const localizedSlug = createUniqueSlug(slugify(title), usedSlugs);
        headings.push({
            alignedId: localizedSlug,
            localizedSlug,
            pathKey: nextPathKey(level)
        });
    }
    return headings;
};

const resolveFileHeadings = (filePath: string) => {
    const tokens = headingParser.parse(readFileSync(filePath, 'utf-8'), {});
    return collectHeadings(tokens);
};

const buildHeadingLookup = (localizedFilePath: string, englishFilePath: string) => {
    const localizedHeadings = resolveFileHeadings(localizedFilePath);
    const englishHeadings = resolveFileHeadings(englishFilePath);
    const englishByPathKey = new Map(englishHeadings.map((heading) => [heading.pathKey, heading.alignedId]));
    const byPathKey = new Map<string, HeadingAnchor>();
    const bySlug = new Map<string, string>();
    for (const localizedHeading of localizedHeadings) {
        const alignedId = englishByPathKey.get(localizedHeading.pathKey) ?? localizedHeading.localizedSlug;
        const heading = {
            ...localizedHeading,
            alignedId
        };
        byPathKey.set(localizedHeading.pathKey, heading);
        bySlug.set(localizedHeading.localizedSlug, alignedId);
    }
    return { byPathKey, bySlug };
};

const resolveLocalizedFilePath = (filePathRelative: string) =>
    path.join(sourceRoot, ...filePathRelative.replace(/\\/g, '/').split('/'));

const resolveEnglishFilePath = (filePathRelative: string) =>
    path.join(englishRoot, ...filePathRelative.replace(/\\/g, '/').split('/').slice(1));

const ensureMarkdownFilePath = (filePath: string) => {
    if (existsSync(filePath) && statSync(filePath).isFile())
        return filePath;
    const indexFilePath = path.join(filePath, 'index.md');
    if (existsSync(indexFilePath) && statSync(indexFilePath).isFile())
        return indexFilePath;
    return null;
};

const isEnglishLocalePath = (filePathRelative: string) =>
    filePathRelative.replace(/\\/g, '/').split('/')[0] === 'en';

const resolveHeadingLookup = (filePathRelative: string | null | undefined) => {
    if (!filePathRelative || isEnglishLocalePath(filePathRelative))
        return null;
    const localizedFilePath = ensureMarkdownFilePath(resolveLocalizedFilePath(filePathRelative));
    const englishFilePath = ensureMarkdownFilePath(resolveEnglishFilePath(filePathRelative));
    if (!localizedFilePath || !englishFilePath)
        return null;
    const cacheKey = `${localizedFilePath}::${englishFilePath}`;
    const localizedMtimeMs = statSync(localizedFilePath).mtimeMs;
    const englishMtimeMs = statSync(englishFilePath).mtimeMs;
    const currentMtimeMs = Math.max(localizedMtimeMs, englishMtimeMs);
    const cached = headingLookupCache.get(cacheKey);
    if (cached?.mtimeMs === currentMtimeMs)
        return cached.lookup;
    const lookup = buildHeadingLookup(localizedFilePath, englishFilePath);
    headingLookupCache.set(cacheKey, {
        lookup,
        mtimeMs: currentMtimeMs
    });
    return lookup;
};

const collectHeadingTokens = (tokens: MarkdownToken[]) => {
    const nextPathKey = createHeadingPathTracker();
    const headings: HeadingToken[] = [];
    for (let index = 0; index < tokens.length; index += 1) {
        const token = tokens[index];
        if (token?.type !== 'heading_open')
            continue;
        const inlineToken = tokens[index + 1];
        if (!inlineToken)
            continue;
        headings.push({
            token,
            inlineToken,
            pathKey: nextPathKey(getHeadingLevel(token))
        });
    }
    return headings;
};

const syncPermalinkHref = (inlineToken: MarkdownToken, id: string) => {
    for (const child of inlineToken.children ?? []) {
        if (child.type !== 'link_open')
            continue;
        const className = child.attrGet('class') ?? '';
        if (!className.split(/\s+/).includes('header-anchor'))
            continue;
        child.attrSet('href', `#${id}`);
        break;
    }
};

const rewriteLocalHash = (rawHref: string, lookup: HeadingLookup) => {
    const hashIndex = rawHref.indexOf('#');
    if (hashIndex < 0)
        return rawHref;
    const hash = decodeURIComponent(rawHref.slice(hashIndex + 1));
    const resolvedHash = lookup.bySlug.get(hash);
    if (!resolvedHash || resolvedHash === hash)
        return rawHref;
    return `${rawHref.slice(0, hashIndex + 1)}${resolvedHash}`;
};

const hasUriScheme = (value: string) => /^[a-z][a-z\d+.-]*:/i.test(value);

const normalizeFilePathRelative = (filePathRelative: string) => filePathRelative.replace(/\\/g, '/');

const resolveTargetFilePathRelative = (currentFilePathRelative: string, rawPath: string, base = '/') => {
    const normalizedCurrentPath = normalizeFilePathRelative(currentFilePathRelative);
    if (rawPath.length === 0)
        return normalizedCurrentPath;
    if (hasUriScheme(rawPath) || rawPath.startsWith('//'))
        return null;
    if (rawPath.startsWith('/')) {
        const normalizedBase = base === '/' ? '/' : `${base.replace(/\/+$/, '')}/`;
        const trimmedPath = rawPath.startsWith(normalizedBase)
            ? rawPath.slice(normalizedBase.length)
            : rawPath.replace(/^\/+/, '');
        if (trimmedPath.endsWith('.html'))
            return trimmedPath.replace(/\.html$/, '.md');
        if (trimmedPath.endsWith('.md'))
            return trimmedPath;
        return `${trimmedPath}.md`;
    }
    if (rawPath.endsWith('.html'))
        return path.posix.join(path.posix.dirname(normalizedCurrentPath), rawPath).replace(/\.html$/, '.md');
    if (rawPath.endsWith('.md'))
        return path.posix.join(path.posix.dirname(normalizedCurrentPath), rawPath);
    return `${path.posix.join(path.posix.dirname(normalizedCurrentPath), rawPath)}.md`;
};

/** Aligns localized heading IDs with the matching English document structure. */
export const alignI18nAnchors = (md: VitePressMarkdownIt) => {
    md.core.ruler.after('anchor', 'align-i18n-anchors', (state) => {
        const lookup = resolveHeadingLookup(state.env.relativePath);
        if (!lookup)
            return;
        const localizedHeadings = collectHeadingTokens(state.tokens);
        if (localizedHeadings.length === 0)
            return;
        for (const localizedHeading of localizedHeadings) {
            const alignedHeading = lookup.byPathKey.get(localizedHeading.pathKey);
            if (!alignedHeading)
                continue;
            localizedHeading.token.attrSet('id', alignedHeading.alignedId);
            syncPermalinkHref(localizedHeading.inlineToken, alignedHeading.alignedId);
        }
    });
};

/** Rewrites localized heading fragments while preserving VitePress source-relative paths. */
export const resolveI18nLink = (context: LinkResolveContext, rawHref: string) => {
    if (!context.filePathRelative || !rawHref.includes('#'))
        return rawHref;
    const hashIndex = rawHref.indexOf('#');
    const rawPath = rawHref.slice(0, hashIndex);
    const targetFilePathRelative = resolveTargetFilePathRelative(context.filePathRelative, rawPath, context.base);
    if (!targetFilePathRelative)
        return rawHref;
    const lookup = resolveHeadingLookup(targetFilePathRelative);
    if (!lookup)
        return rawHref;
    const rewrittenHref = rewriteLocalHash(rawHref, lookup);
    if (rawPath.length === 0)
        return rewrittenHref;
    const resolvedHash = rewrittenHref.slice(rewrittenHref.indexOf('#') + 1);
    const currentFilePathRelative = normalizeFilePathRelative(context.filePathRelative);
    if (normalizeFilePathRelative(targetFilePathRelative) === currentFilePathRelative)
        return `#${resolvedHash}`;
    return `${rawPath}#${resolvedHash}`;
};