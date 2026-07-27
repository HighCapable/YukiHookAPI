import { defineConfig } from 'vitepress';
import en from './en';
import zhCn from './zh-cn';

/** Defines the localized site metadata and theme configuration. */
export default defineConfig({
    locales: {
        en: {
            label: 'English',
            link: '/en/',
            lang: en.lang,
            description: en.description,
            themeConfig: en.themeConfig
        },
        'zh-cn': {
            label: '简体中文',
            link: '/zh-cn/',
            lang: zhCn.lang,
            description: zhCn.description,
            themeConfig: zhCn.themeConfig
        }
    }
});