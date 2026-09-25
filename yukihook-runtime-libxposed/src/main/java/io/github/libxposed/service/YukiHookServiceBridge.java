/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2026/9/26.
 */
package io.github.libxposed.service;

import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * Bridges the package-private libxposed service interface required by <code>YukiHook</code>.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class YukiHookServiceBridge {

    private YukiHookServiceBridge() {
    }

    /**
     * Gets the Binder identity behind the specified service.
     *
     * <p>libxposed 102 does not expose this identity from {@link XposedService}. Keeping this
     * bridge in the service package avoids reflection and lets R8 rewrite the direct reference.</p>
     *
     * @param service the service wrapper.
     * @return the Binder identity backing the service.
     */
    @NonNull
    public static IBinder asBinder(@NonNull XposedService service) {
        return service.asInterface().asBinder();
    }
}