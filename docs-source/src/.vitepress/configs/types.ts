import type { MarkdownOptions } from 'vitepress';

/**
 * VitePress 1.x bundles its own MarkdownIt declaration, which TypeScript 6 treats as
 * incompatible with the structurally equivalent declaration from @types/markdown-it.
 * Deriving the callback parameter keeps project helpers on VitePress's exact type boundary.
 */
export type VitePressMarkdownIt = Parameters<NonNullable<MarkdownOptions['config']>>[0];