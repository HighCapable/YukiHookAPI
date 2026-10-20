# R8 & ProGuard Obfuscation

> In most scenarios, Android application installation packages can reduce size through obfuscation. Here is a configuration method for obfuscation rules.

The generated entry class is kept with the `androidx.annotation.Keep` annotation and the built-in rules of the framework, so you do not need any special configuration for YukiHook under R8.