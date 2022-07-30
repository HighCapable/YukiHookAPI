/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
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
 * This file is Created by weishu on 2018/6/7.
 * This file is Forked from https://github.com/tiann/FreeReflection
 */
package com.highcapable.yukihookapi.thirdparty.me.weishu.reflection;

import static android.os.Build.VERSION.SDK_INT;
import static com.highcapable.yukihookapi.thirdparty.me.weishu.reflection.BootstrapClass.exemptAll;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.Keep;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import dalvik.system.DexFile;

/**
 * NO ANNOTATION
 */
@SuppressWarnings("unused")
@Keep
public class Reflection {
    private static final String TAG = "Reflection";

    private static final String DEX = "ZGV4CjAzNQCXDT0vQ44GJqsrjm32y0qlQmxUevbk56r0CwAAcAAAAHhWNBIAAAAAAAAAADwLAABDAAAAcAAAABMAAAB8AQAACwAAAMgBAAAMAAAATAIAAA8AAACsAgAAAwAAACQDAABwCAAAhAMAAIQDAACGAwAAiwMAAJUDAACdAwAArQMAALkDAADJAwAA3gMAAPADAAD3AwAA/wMAAAIEAAAGBAAACgQAABAEAAATBAAAGAQAADMEAABZBAAAdQQAAIkEAADYBAAAJgUAAHAFAACDBQAAmQUAAK0FAADBBQAA1QUAAOwFAAAIBgAAFAYAACUGAAAuBgAAMwYAADYGAABEBgAAUgYAAFYGAABZBgAAXQYAAHEGAACGBgAAmwYAAKQGAAC9BgAAwAYAAMgGAADTBgAA3AYAAO0GAAABBwAAFAcAACAHAAAoBwAANQcAAE8HAABXBwAAYAcAAHsHAACEBwAAkAcAAKgHAAC6BwAAwgcAANAHAAALAAAAEQAAABIAAAATAAAAFAAAABUAAAAWAAAAFwAAABgAAAAaAAAAGwAAABwAAAAdAAAAHgAAACMAAAAnAAAAKQAAACoAAAArAAAADAAAAAAAAAD4BwAADQAAAAAAAAAMCAAADgAAAAAAAAAACAAADwAAAAIAAAAAAAAAEAAAAAkAAAAUCAAAEAAAAA0AAADoBwAAIwAAAA4AAAAAAAAAJgAAAA4AAADgBwAAJwAAAA8AAAAAAAAAKAAAAA8AAADgBwAAKAAAAA8AAADwBwAAAgAAAD8AAAADAAAAIQAAAAUACgAEAAAABQAKAAUAAAAFAA8ACQAAAAUACgAKAAAABQAAACQAAAAFAAoAJQAAAAYACgAiAAAABgAJAD0AAAAGAA0APgAAAAcACgAiAAAAAQADADMAAAAEAAIALgAAAAUABgADAAAABgAGAAIAAAAGAAYAAwAAAAYACQAvAAAABgAKAC8AAAAGAAgAMAAAAAcABgADAAAABwABAEAAAAAHAAAAQQAAAAgABQA0AAAACQAGAAMAAAALAAcANwAAAA0ABAA2AAAABQAAABEAAAAJAAAAAAAAAAgAAAAAAAAA7AoAAB8IAAAGAAAAEQAAAAkAAAAAAAAABwAAAAAAAAACCwAAHAgAAAcAAAABAAAACQAAAAAAAAAgAAAAAAAAACULAAArCAAAAAADMS4wAAg8Y2xpbml0PgAGPGluaXQ+AA5BUFBMSUNBVElPTl9JRAAKQlVJTERfVFlQRQAOQm9vdHN0cmFwQ2xhc3MAE0Jvb3RzdHJhcENsYXNzLmphdmEAEEJ1aWxkQ29uZmlnLmphdmEABURFQlVHAAZGTEFWT1IAAUkAAklJAAJJTAAESUxMTAABTAADTExMABlMYW5kcm9pZC9jb250ZW50L0NvbnRleHQ7ACRMYW5kcm9pZC9jb250ZW50L3BtL0FwcGxpY2F0aW9uSW5mbzsAGkxhbmRyb2lkL29zL0J1aWxkJFZFUlNJT047ABJMYW5kcm9pZC91dGlsL0xvZzsATUxjb20vaGlnaGNhcGFibGUveXVraWhvb2thcGkvdGhpcmRwYXJ0eS9tZS93ZWlzaHUvZnJlZXJlZmxlY3Rpb24vQnVpbGRDb25maWc7AExMY29tL2hpZ2hjYXBhYmxlL3l1a2lob29rYXBpL3RoaXJkcGFydHkvbWUvd2Vpc2h1L3JlZmxlY3Rpb24vQm9vdHN0cmFwQ2xhc3M7AEhMY29tL2hpZ2hjYXBhYmxlL3l1a2lob29rYXBpL3RoaXJkcGFydHkvbWUvd2Vpc2h1L3JlZmxlY3Rpb24vUmVmbGVjdGlvbjsAEUxqYXZhL2xhbmcvQ2xhc3M7ABRMamF2YS9sYW5nL0NsYXNzPCo+OwASTGphdmEvbGFuZy9PYmplY3Q7ABJMamF2YS9sYW5nL1N0cmluZzsAEkxqYXZhL2xhbmcvU3lzdGVtOwAVTGphdmEvbGFuZy9UaHJvd2FibGU7ABpMamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kOwAKUmVmbGVjdGlvbgAPUmVmbGVjdGlvbi5qYXZhAAdTREtfSU5UAANUQUcAAVYADFZFUlNJT05fQ09ERQAMVkVSU0lPTl9OQU1FAAJWTAABWgACWkwAEltMamF2YS9sYW5nL0NsYXNzOwATW0xqYXZhL2xhbmcvT2JqZWN0OwATW0xqYXZhL2xhbmcvU3RyaW5nOwAHY29udGV4dAAXZGFsdmlrLnN5c3RlbS5WTVJ1bnRpbWUAAWUABmV4ZW1wdAAJZXhlbXB0QWxsAAdmb3JOYW1lAA9mcmVlLXJlZmxlY3Rpb24AEmdldEFwcGxpY2F0aW9uSW5mbwARZ2V0RGVjbGFyZWRNZXRob2QACmdldFJ1bnRpbWUABmludm9rZQALbG9hZExpYnJhcnkAGG1lLndlaXNodS5mcmVlcmVmbGVjdGlvbgAGbWV0aG9kAAdtZXRob2RzABlyZWZsZWN0IGJvb3RzdHJhcCBmYWlsZWQ6AAdyZWxlYXNlAApzVm1SdW50aW1lABZzZXRIaWRkZW5BcGlFeGVtcHRpb25zABB0YXJnZXRTZGtWZXJzaW9uAAZ1bnNlYWwADHVuc2VhbE5hdGl2ZQAOdm1SdW50aW1lQ2xhc3MAAQAAAAoAAAACAAAACgAQAAEAAAASAAAAAQAAAAAAAAADAAAACgAKAAwAAAABAAAAAQAAAAIAAAAJABEAARcGBhc4FzwfFwAEARcBARcfAAAAAAAABgAHDgAWAAcOav8DATIOARUQAwI1DvAEBEMJGgESDwMDNg4BGw+pBQIFAwUEGR4DAC8NAA4ABw4ALAE6Bw4ANgE7ByydGuIBAQMALw0eAEgABw4ADQAHDgATAS0HHXIZa1oAAAEAAQABAAAANAgAAAQAAABwEAwAAAAOAAoAAAADAAEAOQgAAHsAAABgBQEAEwYcADRlbQAcBQgAGgYxABIXI3cQABIIHAkKAE0JBwhuMAsAZQcMARwFCAAaBjQAEicjdxAAEggcCQoATQkHCBIYHAkQAE0JBwhuMAsAZQcMAhIFEhYjZhEAEgcaCC0ATQgGB24wDgBRBgwEHwQIABIlI1URABIGGgc1AE0HBQYSFhIHTQcFBm4wDgBCBQwDHwMNABIlI1URABIGGgc+AE0HBQYSFhIXI3cQABIIHAkSAE0JBwhNBwUGbjAOAEIFDAUfBQ0AaQUKABIFEgYjZhEAbjAOAFMGDAVpBQkADgANABoFBgAaBjsAcTABAGUAKPcAAAYAAABrAAEAAQEMcgEAAQABAAAAaAgAAAQAAABwEAwAAAAOAAMAAQABAAAAbQgAAAsAAAASECMAEgASAU0CAAFxEAYAAAAKAA8AAAAIAAEAAwABAHMIAAAdAAAAEhESAmIDCQA4AwYAYgMKADkDBAABIQ8BYgMKAGIECQASFSNVEQASBk0HBQZuMA4AQwUo8g0AASEo7wAADAAAAA0AAQABAQwaAwAAAAEAAACDCAAADQAAABIQIwASABIBGgIPAE0CAAFxEAYAAAAKAA8AAAABAAEAAQAAAIgIAAAEAAAAcBAMAAAADgAEAAEAAQAAAI0IAAAeAAAAEgBgAQEAEwIcADUhAwAPAHEABwAAAAoBOQH7/xoAMgBxEA0AAABuEAAAAwAMAFIAAABxEAoAAAAKACjqBgABAAIZARkBGQEZARkBGQKBgASYEQMABQAIGgEKAQoDiIAEsBEBgYAExBMBCdwTAYkBhBQBCdwUAQADAAsaCIGABIgVAQmgFQGKAgAAAAAPAAAAAAAAAAEAAAAAAAAAAQAAAEMAAABwAAAAAgAAABMAAAB8AQAAAwAAAAsAAADIAQAABAAAAAwAAABMAgAABQAAAA8AAACsAgAABgAAAAMAAAAkAwAAAiAAAEMAAACEAwAAARAAAAcAAADgBwAABSAAAAMAAAAcCAAAAxAAAAEAAAAwCAAAAyAAAAgAAAA0CAAAASAAAAgAAACYCAAAACAAAAMAAADsCgAAABAAAAEAAAA8CwAA";

    private static native int unsealNative(int targetSdkVersion);

    /**
     * Begin
     *
     * @param context The Base Context
     * @return int
     */
    public static int unseal(Context context) {
        if (SDK_INT < 28) {
            // Below Android P, ignore
            return 0;
        }

        // try exempt API first.
        if (exemptAll()) {
            return 0;
        }
        if (unsealByDexFile(context)) {
            return 0;
        }

        return -1;
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    private static boolean unsealByDexFile(Context context) {
        byte[] bytes = Base64.decode(DEX, Base64.NO_WRAP);
        File codeCacheDir = getCodeCacheDir(context);
        if (codeCacheDir == null) {
            return false;
        }
        File code = new File(codeCacheDir, "__temp_" + System.currentTimeMillis() + ".dex");
        try {

            try (FileOutputStream fos = new FileOutputStream(code)) {
                fos.write(bytes);
            }

            DexFile dexFile = new DexFile(code);
            // This class is hardcoded in the dex, Don't use BootstrapClass.class to reference it
            // it maybe obfuscated!!
            Class<?> bootstrapClass = dexFile.loadClass("com.highcapable.yukihookapi.thirdparty.me.weishu.reflection.BootstrapClass", null);
            Method exemptAll = bootstrapClass.getDeclaredMethod("exemptAll");
            return (boolean) exemptAll.invoke(null);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            if (code.exists()) {
                code.delete();
            }
        }
    }

    private static File getCodeCacheDir(Context context) {
        if (context != null) {
            return context.getCodeCacheDir();
        }
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (TextUtils.isEmpty(tmpDir)) {
            return null;
        }
        File tmp = new File(tmpDir);
        if (!tmp.exists()) {
            return null;
        }
        return tmp;
    }
}