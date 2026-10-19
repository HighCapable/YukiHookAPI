-dontwarn io.github.libxposed.annotation.**
-adaptresourcefilecontents META-INF/xposed/java_init.list
-keep,allowoptimization,allowobfuscation public class * extends io.github.libxposed.api.XposedModule {
    public <init>();
}
# Keep the AIDL interface classes for libxposed service.
-keepnames,allowoptimization interface io.github.libxposed.service.** extends android.os.IInterface