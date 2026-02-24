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
import androidx.lifecycle.LiveData
import com.dylanc.mmkv.property.BaseMMKVProperty
import com.dylanc.mmkv.property.MMKVListProperty
import com.dylanc.mmkv.property.MMKVLiveDataProperty
import com.dylanc.mmkv.property.MMKVMapProperty
import com.dylanc.mmkv.property.MMKVProperty
import com.dylanc.mmkv.property.MMKVWithKeyProperty
import com.dylanc.mmkv.property.MMKVStateFlowProperty
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

open class MMKVOwner(override val mmapID: String) : IMMKVOwner {
  override val kv: MMKV by lazy { MMKV.mmkvWithID(mmapID) }
}

interface IMMKVOwner {
  val mmapID: String

  val kv: MMKV

  fun mmkvInt(default: Int = 0) =
    MMKVProperty({ kv.decodeInt(it, default) }, { kv.encode(first, second) })

  fun mmkvLong(default: Long = 0L) =
    MMKVProperty({ kv.decodeLong(it, default) }, { kv.encode(first, second) })

  fun mmkvBool(default: Boolean = false) =
    MMKVProperty({ kv.decodeBool(it, default) }, { kv.encode(first, second) })

  fun mmkvFloat(default: Float = 0f) =
    MMKVProperty({ kv.decodeFloat(it, default) }, { kv.encode(first, second) })

  fun mmkvDouble(default: Double = 0.0) =
    MMKVProperty({ kv.decodeDouble(it, default) }, { kv.encode(first, second) })

  fun mmkvString() =
    MMKVProperty({ kv.decodeString(it) }, { kv.encode(first, second) })

  fun mmkvString(default: String) =
    MMKVProperty({ kv.decodeString(it) ?: default }, { kv.encode(first, second) })

  fun mmkvStringSet() =
    MMKVProperty({ kv.decodeStringSet(it) }, { kv.encode(first, second) })

  fun mmkvStringSet(default: Set<String>) =
    MMKVProperty({ kv.decodeStringSet(it) ?: default }, { kv.encode(first, second) })

  fun mmkvBytes() =
    MMKVProperty({ kv.decodeBytes(it) }, { kv.encode(first, second) })

  fun mmkvBytes(default: ByteArray) =
    MMKVProperty({ kv.decodeBytes(it) ?: default }, { kv.encode(first, second) })

  fun <V> BaseMMKVProperty<V>.withKey(key: String) = MMKVWithKeyProperty(this, key)

  fun <V> MMKVProperty<V>.asLiveData() = MMKVLiveDataProperty(this)

  fun <V> MMKVProperty<V>.asStateFlow() = MMKVStateFlowProperty(this)

  fun <V> MMKVProperty<V>.asMap() = MMKVMapProperty(this)

  fun <V> MMKVProperty<V>.asList() = MMKVListProperty(this)

  fun clearAllKV() = kv.clearAll()
}

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable() =
  MMKVProperty({ kv.decodeParcelable(it, T::class.java) }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable(default: T) =
  MMKVProperty({ kv.decodeParcelable(it, T::class.java) ?: default }, { kv.encode(first, second) })

fun IMMKVOwner.getAllKV(): Map<String, Any?> = buildMap {
  val types = arrayOf(
    BaseMMKVProperty::class,
    MMKVProperty::class,
    MMKVLiveDataProperty::class,
    MMKVStateFlowProperty::class,
    MMKVMapProperty::class,
    MMKVListProperty::class
  )

  fun resolveNestedValue(rootName: String, decode: (String) -> Any?): Any? {
    val keys = kv.decodeStringSet("$rootName\$key")
    if (!keys.isNullOrEmpty()) {
      return keys.associateWith { key ->
        resolveNestedValue("$rootName$$$key", decode)
      }
    }
    return decode(rootName)
  }

  this@getAllKV::class.memberProperties
    .filterIsInstance<KProperty1<IMMKVOwner, *>>()
    .forEach { property ->
      property.isAccessible = true
      val delegate = property.getDelegate(this@getAllKV)
      if (types.any { it.isInstance(delegate) }) {
        val value = property.get(this@getAllKV)
        this[property.name] = when (value) {
          is LiveData<*> -> value.value
          is StateFlow<*> -> value.value
          else -> {
            val base = delegate as? BaseMMKVProperty<*>
            if (base != null) {
              resolveNestedValue(property.name) { key -> base.decode(key) }
            } else {
              value
            }
          }
        }
      }
      property.isAccessible = false
    }
}