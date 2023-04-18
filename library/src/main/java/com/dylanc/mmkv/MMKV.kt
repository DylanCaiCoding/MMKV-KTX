/*
 * Copyright (c) 2021. Dylan Cai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dylanc.mmkv

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A class that has a MMKV instance. If you want to customize the MMKV, you can override
 * the kv property. For example:
 *
 * ```kotlin
 * object DataRepository : MMKVOwner {
 *   override val kv = MMKV.mmkvWithID("MyID")
 * }
 * ```
 *
 * @author Dylan Cai
 */
interface MMKVOwner {
  val kv: MMKV
    get() = default ?: throw IllegalStateException("If you use MMKV in Application, you should set MMKVOwner.default first.")

  companion object {
    @JvmStatic
    var default: MMKV? = null
  }
}

fun MMKVOwner.mmkvInt(default: Int = 0) =
  MMKVProperty({ kv.decodeInt(it, default) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvLong(default: Long = 0L) =
  MMKVProperty({ kv.decodeLong(it, default) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvBool(default: Boolean = false) =
  MMKVProperty({ kv.decodeBool(it, default) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvFloat(default: Float = 0f) =
  MMKVProperty({ kv.decodeFloat(it, default) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvDouble(default: Double = 0.0) =
  MMKVProperty({ kv.decodeDouble(it, default) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvString() =
  MMKVProperty({ kv.decodeString(it) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvString(default: String) =
  MMKVProperty({ kv.decodeString(it) ?: default }, { kv.encode(first, second) })

fun MMKVOwner.mmkvStringSet() =
  MMKVProperty({ kv.decodeStringSet(it) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvStringSet(default: Set<String>) =
  MMKVProperty({ kv.decodeStringSet(it) ?: default }, { kv.encode(first, second) })

fun MMKVOwner.mmkvBytes() =
  MMKVProperty({ kv.decodeBytes(it) }, { kv.encode(first, second) })

fun MMKVOwner.mmkvBytes(default: ByteArray) =
  MMKVProperty({ kv.decodeBytes(it) ?: default }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable() =
  MMKVProperty({ kv.decodeParcelable(it, T::class.java) }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable(default: T) =
  MMKVProperty({ kv.decodeParcelable(it, T::class.java) ?: default }, { kv.encode(first, second) })

fun <V> MMKVProperty<V>.asLiveData() = object : ReadOnlyProperty<MMKVOwner, MutableLiveData<V>> {
  private var cache: MutableLiveData<V>? = null

  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): MutableLiveData<V> =
    cache ?: object : MutableLiveData<V>() {
      override fun setValue(value: V) {
        if (super.getValue() == value) return
        this@asLiveData.setValue(thisRef, property, value)
        super.setValue(value)
      }

      override fun getValue(): V? = this@asLiveData.getValue(thisRef, property)
    }.also { cache = it }
}

class MMKVProperty<V>(
  private val decode: (String) -> V,
  private val encode: Pair<String, V>.() -> Boolean
) : ReadWriteProperty<MMKVOwner, V> {

  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    decode(property.name)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    encode(property.name to value)
  }
}