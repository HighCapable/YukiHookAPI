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
 * This file is created by fankes on 2023/1/6.
 */
package com.highcapable.yukihookapi.hook.xposed.channel.data.wrapper

import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.xposed.channel.data.ChannelData
import com.highcapable.yukihookapi.hook.xposed.parasitic.reference.ModuleClassLoader
import java.io.Serializable

/**
 * Wraps key-value data sent through the data channel.
 * @param wrapperId the wrapper instance ID.
 * @param isSegmented whether the payload is segmented.
 * @param segmentCount the total number of payload segments.
 * @param segmentIndex the index of the currently received segment.
 * @param instance the original data instance.
 */
internal data class ChannelDataWrapper<T>(
    val wrapperId: String,
    val isSegmented: Boolean,
    val segmentCount: Int,
    val segmentIndex: Int,
    val instance: ChannelData<T>
) : Parcelable {

    companion object {

        private const val NULL = 0
        private const val SERIALIZABLE = 1
        private const val CHAR_SEQUENCE = 2
        private const val PARCELABLE = 3

        @JvmField
        val CREATOR = object : Parcelable.Creator<ChannelDataWrapper<*>> {

            override fun createFromParcel(source: Parcel) = ChannelDataWrapper(
                wrapperId = checkNotNull(source.readString()),
                isSegmented = source.readInt() != 0,
                segmentCount = source.readInt(),
                segmentIndex = source.readInt(),
                instance = ChannelData(checkNotNull(source.readString()), source.readPayload())
            )

            override fun newArray(size: Int) = arrayOfNulls<ChannelDataWrapper<*>>(size)
        }

        private fun Parcel.readPayload() = when (readInt()) {
            NULL -> null
            SERIALIZABLE -> ParcelCompat.readSerializable(this, ModuleClassLoader.get(), classOf<Serializable>())
            CHAR_SEQUENCE -> readValue(ModuleClassLoader.get()) as? CharSequence
            PARCELABLE -> ParcelCompat.readParcelable(this, ModuleClassLoader.get(), classOf<Parcelable>())
            else -> error("Unsupported channel payload type.")
        }
    }

    override fun describeContents() = when (val value = instance.value) {
        is Serializable -> 0
        is Parcelable -> value.describeContents()
        else -> 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(wrapperId)
        dest.writeInt(if (isSegmented) 1 else 0)
        dest.writeInt(segmentCount)
        dest.writeInt(segmentIndex)
        dest.writeString(instance.key)

        // Existing Serializable values keep their original object type across the channel.
        when (val value = instance.value) {
            null -> dest.writeInt(NULL)
            is Serializable -> {
                dest.writeInt(SERIALIZABLE)
                dest.writeSerializable(value)
            }
            is CharSequence -> {
                dest.writeInt(CHAR_SEQUENCE)
                dest.writeValue(value)
            }
            is Parcelable -> {
                dest.writeInt(PARCELABLE)
                dest.writeParcelable(value, flags)
            }
            else -> error("Key-Value type ${value.javaClass.name} is not allowed.")
        }
    }
}