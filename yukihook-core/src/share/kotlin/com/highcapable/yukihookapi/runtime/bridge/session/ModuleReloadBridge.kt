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
 * This file is created by fankes on 2026/9/27.
 */
package com.highcapable.yukihookapi.runtime.bridge.session

import android.os.Bundle
import androidx.annotation.RestrictTo

/**
 * Module-process service operations supplied by a runtime.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ModuleReloadBridge {

    /**
     * An immutable service target snapshot with runtime-owned identity.
     */
    interface Target {

        /** Process UID for display. */
        val uid: Int

        /** Process PID for display. */
        val pid: Int

        /** Process name for display and filtering. */
        val processName: String

        /** State observed when this snapshot was queried. */
        val state: State

        /** Loaded module version code for display, not code identity. */
        val loadedVersionCode: Long

        /**
         * Framework-reported state of a running target.
         */
        enum class State {
            /** The installed code is active. */
            UP_TO_DATE,

            /** Older module code is active. */
            STALE,

            /** A reload is running. */
            RELOADING,

            /** The last reload failed. */
            FAILED
        }
    }

    /**
     * Outcome of a reload request.
     */
    enum class Status {
        /** The new module instance completed initialization. */
        SUCCEEDED,

        /** Submission failed, the old module instance refused, or reload failed. */
        FAILED,

        /** The target cannot be reloaded. */
        UNSUPPORTED,

        /** Another reload is already running. */
        IN_PROGRESS,

        /** The target process died. */
        PROCESS_DIED
    }

    /** Whether one service with reload support is connected. */
    val isAvailable: Boolean

    /** Queries fresh target handles. */
    val targets: List<Target>

    /**
     * Submits a reload request.
     * Submission failures are reported as [Status.FAILED] with the original diagnostic message.
     * @param target an opaque target obtained from this bridge.
     * @param extras classloader-neutral request data.
     * @param callback the completion listener.
     */
    fun request(target: Target, extras: Bundle?, callback: (Status, String?) -> Unit)
}