import { defineConfig } from 'vitepress';
import { configs, createThemeNavigation } from '../configs/template';

const navigation = createThemeNavigation('en');

/** Defines English theme labels, navigation, and footer content. */
export default defineConfig({
    description: configs.website.locales.en.description,
    lang: configs.website.locales.en.lang,
    themeConfig: {
        nav: navigation.nav,
        sidebar: navigation.sidebar,
        outline: {
            level: [2, 3],
            label: 'On this page'
        },
        footer: {
            message: 'Released under the Apache-2.0 License',
            copyright: 'Copyright © 2019 HighCapable'
        },
        langMenuLabel: 'English'
    }
});