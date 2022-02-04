/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2022/2/5.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi_ksp_xposed

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import java.io.File

/**
 * 这是 YukiHook API 的自动生成处理类 - 核心基于 KSP
 *
 * 可以帮你快速生成 Xposed 入口类和包名
 *
 * 你只需要添加 [InjectYukiHookWithXposed] 注释即可完美解决一切问题
 */
@AutoService(SymbolProcessorProvider::class)
class YukiHookXposedProcessor : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment) = object : SymbolProcessor {

        /** 插入 Xposed 尾部的名称 */
        private val xposedClassShortName = "_YukiHookXposedInit"

        /**
         * 获取父类名称 - 只取最后一个
         * @return [String]
         */
        private val KSClassDeclaration.superName
            get() = try {
                superTypes.last().element.toString()
            } catch (_: Exception) {
                ""
            }

        /**
         * 创建一个环境方法体方便调用
         * @param env 方法体
         */
        private fun environment(env: SymbolProcessorEnvironment.() -> Unit) = runCatching { environment.apply(env) }

        override fun process(resolver: Resolver) = emptyList<KSAnnotated>().let {
            injectProcess(resolver)
            it
        }

        /**
         * 开始作业入口
         * @param resolver [Resolver]
         */
        private fun injectProcess(resolver: Resolver) = environment {
            var injectOnce = true
            resolver.getSymbolsWithAnnotation(InjectYukiHookWithXposed::class.java.name)
                .asSequence()
                .filterIsInstance<KSClassDeclaration>().forEach {
                    if (injectOnce)
                        if (it.superName == "YukiHookXposedInitProxy") {
                            injectAssets(
                                codePath = (it.location as? FileLocation?)?.filePath ?: "",
                                packageName = it.packageName.asString(),
                                className = it.simpleName.asString()
                            )
                            injectClass(it.packageName.asString(), it.simpleName.asString())
                        } else logger.error(message = "HookEntryClass \"${it.simpleName.asString()}\" must be implements YukiHookXposedInitProxy")
                    else logger.error(message = "@InjectYukiHookWithXposed only can be use in once times")
                    /** 仅处理第一个标记的类 - 再次处理将拦截并报错 */
                    injectOnce = false
                }
        }

        /**
         * 自动生成 Xposed assets 入口文件
         * @param codePath 注释类的完整代码文件路径
         * @param packageName 包名
         * @param className 类名
         */
        private fun injectAssets(codePath: String, packageName: String, className: String) = environment {
            runCatching {
                if (codePath.isBlank()) error("Project CodePath is empty")
                val projectPath = when (File.separator) {
                    "\\" -> {
                        if (codePath.contains("\\src\\main\\"))
                            codePath.split("\\src\\main\\")[0] + "\\src\\main\\"
                        else error("Project source path must be ..\\src\\main\\..")
                    }
                    "/" -> {
                        if (codePath.contains("/src/main/"))
                            codePath.split("/src/main/")[0] + "/src/main/"
                        else error("Project source path must be ../src/main/..")
                    }
                    else -> error("Unix File Separator unknown")
                }
                File("$projectPath${File.separator}assets").also { assFile ->
                    if (!assFile.exists() || !assFile.isDirectory) {
                        assFile.delete()
                        assFile.mkdirs()
                    }
                    File("${assFile.absolutePath}${File.separator}xposed_init")
                        .writeText(text = "$packageName.${className}$xposedClassShortName")
                }
            }.onFailure {
                logger.error(message = "Inject XposedAssets Failed! $it")
            }
        }

        /**
         * 注入并生成指定类
         * @param packageName 包名
         * @param className 类名
         */
        private fun injectClass(packageName: String, className: String) = environment {
            var realPackageName = "unknown"
            if (packageName.contains(".hook."))
                realPackageName = packageName.split(".hook.")[0]
            else logger.warn(message = "YukiHook cannot identify your App's package name,please refer to the wiki https://github.com/fankes/YukiHookAPI/wiki to fix the package name or manually configure the package name")
            codeGenerator.createNewFile(Dependencies.ALL_FILES, packageName, fileName = "$className$xposedClassShortName")
                .apply {
                    /** 🤡 由于插入的代码量不大就不想用工具生成了 */
                    write(
                        ("@file:Suppress(\"EXPERIMENTAL_API_USAGE\")\n" +
                                "\n" +
                                "package $packageName\n" +
                                "\n" +
                                "import androidx.annotation.Keep\n" +
                                "import com.highcapable.yukihookapi.YukiHookAPI\n" +
                                "import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus\n" +
                                "import com.highcapable.yukihookapi.hook.log.loggerE\n" +
                                "import de.robv.android.xposed.IXposedHookLoadPackage\n" +
                                "import de.robv.android.xposed.XC_MethodReplacement\n" +
                                "import de.robv.android.xposed.XposedHelpers\n" +
                                "import de.robv.android.xposed.callbacks.XC_LoadPackage\n" +
                                "import $packageName.$className\n" +
                                "\n" +
                                "@Keep\n" +
                                "class $className$xposedClassShortName : IXposedHookLoadPackage {\n" +
                                "\n" +
                                "    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {\n" +
                                "        if (lpparam == null) return\n" +
                                "        runCatching {\n" +
                                "            $className().onHook()\n" +
                                "        }.onFailure {\n" +
                                "            loggerE(msg = \"Try to load $packageName.$className Failed\", e = it)\n" +
                                "        }\n" +
                                "        YukiHookAPI.modulePackageName.ifEmpty {\n" +
                                "            YukiHookAPI.modulePackageName = \"$realPackageName\"\n" +
                                "            \"$realPackageName\"\n" +
                                "        }.also {\n" +
                                "            if (lpparam.packageName == it)\n" +
                                "                XposedHelpers.findAndHookMethod(\n" +
                                "                    YukiHookModuleStatus::class.java.name,\n" +
                                "                    lpparam.classLoader,\n" +
                                "                    \"isActive\",\n" +
                                "                    object : XC_MethodReplacement() {\n" +
                                "                        override fun replaceHookedMethod(param: MethodHookParam?) = true\n" +
                                "                    })\n" +
                                "        }\n" +
                                "        YukiHookAPI.onXposedLoaded(lpparam)\n" +
                                "    }\n" +
                                "}").toByteArray()
                    )
                    flush()
                    close()
                }
        }
    }
}