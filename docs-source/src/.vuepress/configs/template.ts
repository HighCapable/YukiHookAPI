import { i18n } from './utils';

const baseApiPath = '/api/public/com/highcapable/yukihookapi/';

const navigationLinks = {
    start: [
        '/guide/home',
        '/guide/supportive',
        '/guide/knowledge',
        '/guide/quick-start',
        '/guide/example',
        '/guide/move-to-new-api'
    ],
    config: [
        '/config/api-example',
        '/config/api-exception',
        '/config/xposed-using',
        '/config/api-using',
        '/config/r8-proguard'
    ],
    tools: '/tools/yukihookapi-projectbuilder',
    apiDocs: [
        '/api/home',
        '/api/public/',
        '/api/special-features/'
    ],
    publicApi: [
        baseApiPath + 'YukiHookAPI',
        baseApiPath + 'hook/param/PackageParam',
        baseApiPath + 'hook/param/HookParam',
        baseApiPath + 'annotation/xposed/InjectYukiHookWithXposed',
        baseApiPath + 'hook/xposed/proxy/IYukiHookXposedInit',
        baseApiPath + 'hook/xposed/prefs/YukiHookPrefsBridge',
        baseApiPath + 'hook/xposed/prefs/ui/ModulePreferenceFragment',
        baseApiPath + 'hook/xposed/prefs/data/PrefsData',
        baseApiPath + 'hook/xposed/channel/YukiHookDataChannel',
        baseApiPath + 'hook/xposed/channel/data/ChannelData',
        baseApiPath + 'hook/xposed/channel/priority/ChannelPriority',
        baseApiPath + 'hook/xposed/application/ModuleApplication',
        baseApiPath + 'hook/xposed/parasitic/activity/base/ModuleAppActivity',
        baseApiPath + 'hook/xposed/parasitic/activity/base/ModuleAppCompatActivity',
        baseApiPath + 'hook/xposed/parasitic/context/wrapper/ModuleContextThemeWrapper',
        baseApiPath + 'hook/xposed/parasitic/reference/ModuleClassLoader',
        baseApiPath + 'hook/xposed/bridge/resources/YukiModuleResources',
        baseApiPath + 'hook/xposed/bridge/resources/YukiResources',
        baseApiPath + 'hook/xposed/bridge/resources/YukiResForwarder',
        baseApiPath + 'hook/xposed/bridge/event/YukiXposedEvent',
        baseApiPath + 'hook/type/android/ComponentTypeFactory',
        baseApiPath + 'hook/type/android/GraphicsTypeFactory',
        baseApiPath + 'hook/type/android/ViewTypeFactory',
        baseApiPath + 'hook/type/java/VariableTypeFactory',
        baseApiPath + 'hook/type/defined/DefinedTypeFactory',
        baseApiPath + 'hook/log/LoggerFactory',
        baseApiPath + 'hook/factory/ReflectionFactory',
        baseApiPath + 'hook/factory/YukiHookFactory',
        baseApiPath + 'hook/entity/YukiBaseHooker',
        baseApiPath + 'hook/core/api/compat/type/ExecutorType',
        baseApiPath + 'hook/core/YukiMemberHookCreator',
        baseApiPath + 'hook/core/YukiResourcesHookCreator',
        baseApiPath + 'hook/core/finder/members/MethodFinder',
        baseApiPath + 'hook/core/finder/members/ConstructorFinder',
        baseApiPath + 'hook/core/finder/members/FieldFinder',
        baseApiPath + 'hook/core/finder/classes/DexClassFinder',
        baseApiPath + 'hook/core/finder/classes/rules/result/MemberRulesResult',
        baseApiPath + 'hook/core/finder/classes/rules/MemberRules',
        baseApiPath + 'hook/core/finder/classes/rules/FieldRules',
        baseApiPath + 'hook/core/finder/classes/rules/MethodRules',
        baseApiPath + 'hook/core/finder/classes/rules/ConstructorRules',
        baseApiPath + 'hook/core/finder/base/BaseFinder',
        baseApiPath + 'hook/core/finder/base/rules/CountRules',
        baseApiPath + 'hook/core/finder/base/rules/ModifierRules',
        baseApiPath + 'hook/core/finder/base/rules/NameRules',
        baseApiPath + 'hook/core/finder/base/rules/ObjectRules',
        baseApiPath + 'hook/bean/HookClass',
        baseApiPath + 'hook/bean/VariousClass',
        baseApiPath + 'hook/bean/CurrentClass',
        baseApiPath + 'hook/bean/GenericClass',
        baseApiPath + 'hook/bean/HookResources'
    ],
    specialFeature: [
        '/api/special-features/reflection',
        '/api/special-features/logger',
        '/api/special-features/xposed-storage',
        '/api/special-features/xposed-channel',
        '/api/special-features/host-lifecycle',
        '/api/special-features/host-inject'
    ],
    about: [
        '/about/changelog',
        '/about/future',
        '/about/contacts',
        '/about/about'
    ]
};

export const configs = {
    dev: {
        dest: '../docs/',
        port: 9000
    },
    website: {
        base: '/YukiHookAPI/',
        icon: '/YukiHookAPI/images/logo.png',
        logo: '/images/logo.png',
        title: 'Yuki Hook API',
        locales: {
            '/en/': {
                lang: 'en-US',
                description: 'An efficient Hook API and Xposed Module solution built in Kotlin'
            },
            '/zh-cn/': {
                lang: 'zh-CN',
                description: '一个使用 Kotlin 构建的高效 Hook API 与 Xposed 模块解决方案'
            }
        }
    },
    github: {
        repo: 'https://github.com/fankes/YukiHookAPI',
        branch: 'master',
        dir: 'docs-source/src'
    }
};

export const navBarItems = {
    '/en/': [{
        text: 'Navigation',
        children: [{
            text: 'Get Started',
            children: [
                { text: 'Introduce', link: i18n.string(navigationLinks.start[0], 'en') },
                { text: 'Supportive', link: i18n.string(navigationLinks.start[1], 'en') },
                { text: 'Basic Knowledge', link: i18n.string(navigationLinks.start[2], 'en') },
                { text: 'Quick Start', link: i18n.string(navigationLinks.start[3], 'en') },
                { text: 'Usage Example', link: i18n.string(navigationLinks.start[4], 'en') },
                { text: 'Migrate from Other Hook APIs', link: i18n.string(navigationLinks.start[5], 'en') }
            ]
        }, {
            text: 'Configs',
            children: [
                { text: 'API Basic Configs', link: i18n.string(navigationLinks.config[0], 'en') },
                { text: 'API Exception Handling', link: i18n.string(navigationLinks.config[1], 'en') },
                { text: 'Use as Xposed Module Configs', link: i18n.string(navigationLinks.config[2], 'en') },
                { text: 'Use as Hook API Configs', link: i18n.string(navigationLinks.config[3], 'en') },
                { text: 'R8 & Proguard Obfuscate', link: i18n.string(navigationLinks.config[4], 'en') }
            ]
        }, {
            text: 'Tools',
            children: [
                { text: 'YukiHookAPI Project Builder', link: i18n.string(navigationLinks.tools, 'en') }
            ]
        }, {
            text: 'API Document',
            children: [{ text: 'Document Introduction', link: i18n.string(navigationLinks.apiDocs[0], 'en') }, {
                text: 'Public API',
                link: i18n.string(navigationLinks.publicApi[0], 'en'),
                activeMatch: i18n.string(navigationLinks.apiDocs[1], 'en')
            }, {
                text: 'Special Features',
                link: i18n.string(navigationLinks.specialFeature[0], 'en'),
                activeMatch: i18n.string(navigationLinks.apiDocs[2], 'en')
            }]
        }, {
            text: 'About',
            children: [
                { text: 'Changelog', link: i18n.string(navigationLinks.about[0], 'en') },
                { text: 'Looking for Future', link: i18n.string(navigationLinks.about[1], 'en') },
                { text: 'Contact Us', link: i18n.string(navigationLinks.about[2], 'en') },
                { text: 'About this Document', link: i18n.string(navigationLinks.about[3], 'en') }
            ]
        }]
    }, {
        text: 'Contact Us',
        link: i18n.string(navigationLinks.about[2], 'en')
    }],
    '/zh-cn/': [{
        text: '导航',
        children: [{
            text: '入门',
            children: [
                { text: '介绍', link: i18n.string(navigationLinks.start[0], 'zh-cn') },
                { text: '支持性', link: i18n.string(navigationLinks.start[1], 'zh-cn') },
                { text: '基础知识', link: i18n.string(navigationLinks.start[2], 'zh-cn') },
                { text: '快速开始', link: i18n.string(navigationLinks.start[3], 'zh-cn') },
                { text: '用法示例', link: i18n.string(navigationLinks.start[4], 'zh-cn') },
                { text: '从其它 Hook API 迁移', link: i18n.string(navigationLinks.start[5], 'zh-cn') }
            ]
        }, {
            text: '配置',
            children: [
                { text: 'API 基本配置', link: i18n.string(navigationLinks.config[0], 'zh-cn') },
                { text: 'API 异常处理', link: i18n.string(navigationLinks.config[1], 'zh-cn') },
                { text: '作为 Xposed 模块使用的相关配置', link: i18n.string(navigationLinks.config[2], 'zh-cn') },
                { text: '作为 Hook API 使用的相关配置', link: i18n.string(navigationLinks.config[3], 'zh-cn') },
                { text: 'R8 与 Proguard 混淆', link: i18n.string(navigationLinks.config[4], 'zh-cn') }
            ]
        }, {
            text: '工具',
            children: [
                { text: 'YukiHookAPI 构建工具', link: i18n.string(navigationLinks.tools, 'zh-cn') }
            ]
        }, {
            text: 'API 文档',
            children: [{ text: '文档介绍', link: i18n.string(navigationLinks.apiDocs[0], 'zh-cn') }, {
                text: 'Public API',
                link: i18n.string(navigationLinks.publicApi[0], 'zh-cn'),
                activeMatch: i18n.string(navigationLinks.apiDocs[1], 'zh-cn')
            }, {
                text: '特色功能',
                link: i18n.string(navigationLinks.specialFeature[0], 'zh-cn'),
                activeMatch: i18n.string(navigationLinks.apiDocs[2], 'zh-cn')
            }]
        }, {
            text: '关于',
            children: [
                { text: '更新日志', link: i18n.string(navigationLinks.about[0], 'zh-cn') },
                { text: '展望未来', link: i18n.string(navigationLinks.about[1], 'zh-cn') },
                { text: '联系我们', link: i18n.string(navigationLinks.about[2], 'zh-cn') },
                { text: '关于此文档', link: i18n.string(navigationLinks.about[3], 'zh-cn') }
            ]
        }]
    }, {
        text: '联系我们',
        link: i18n.string(navigationLinks.about[2], 'zh-cn')
    }]
};

export const sideBarItems = {
    '/en/': [{
        text: 'Get Started',
        collapsible: true,
        children: i18n.array(navigationLinks.start, 'en')
    }, {
        text: 'Configs',
        collapsible: true,
        children: i18n.array(navigationLinks.config, 'en')
    }, {
        text: 'Tools',
        collapsible: true,
        children: [i18n.string(navigationLinks.tools, 'en')]
    }, {
        text: 'API Document',
        collapsible: true,
        children: [i18n.string(navigationLinks.apiDocs[0], 'en'), {
            text: 'Public API' + i18n.space,
            collapsible: true,
            children: i18n.array(navigationLinks.publicApi, 'en')
        }, {
            text: 'Special Features' + i18n.space,
            collapsible: true,
            children: i18n.array(navigationLinks.specialFeature, 'en')
        }]
    }, {
        text: 'About',
        collapsible: true,
        children: i18n.array(navigationLinks.about, 'en')
    }],
    '/zh-cn/': [{
        text: '入门',
        collapsible: true,
        children: i18n.array(navigationLinks.start, 'zh-cn')
    }, {
        text: '配置',
        collapsible: true,
        children: i18n.array(navigationLinks.config, 'zh-cn')
    }, {
        text: '工具',
        collapsible: true,
        children: [i18n.string(navigationLinks.tools, 'zh-cn')]
    }, {
        text: 'API 文档',
        collapsible: true,
        children: [i18n.string(navigationLinks.apiDocs[0], 'zh-cn'), {
            text: 'Public API' + i18n.space,
            collapsible: true,
            children: i18n.array(navigationLinks.publicApi, 'zh-cn')
        }, {
            text: '特色功能' + i18n.space,
            collapsible: true,
            children: i18n.array(navigationLinks.specialFeature, 'zh-cn')
        }]
    }, {
        text: '关于',
        collapsible: true,
        children: i18n.array(navigationLinks.about, 'zh-cn')
    }]
};