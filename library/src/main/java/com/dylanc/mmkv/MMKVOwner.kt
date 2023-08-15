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
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.isAccessible

interface IMMKVOwner {
  val mmapID: String
  val kv: MMKV
  fun clearAllKV() = kv.clearAll()
}

open class MMKVOwner(override val mmapID: String) : IMMKVOwner {
  override val kv: MMKV by lazy { MMKV.mmkvWithID(mmapID) }
}

fun IMMKVOwner.mmkvInt(default: Int = 0) =
  MMKVProperty({ kv.decodeInt(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvLong(default: Long = 0L) =
  MMKVProperty({ kv.decodeLong(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBool(default: Boolean = false) =
  MMKVProperty({ kv.decodeBool(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvFloat(default: Float = 0f) =
  MMKVProperty({ kv.decodeFloat(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvDouble(default: Double = 0.0) =
  MMKVProperty({ kv.decodeDouble(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvString() =
  MMKVProperty({ kv.decodeString(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvString(default: String) =
  MMKVProperty({ kv.decodeString(it) ?: default }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvStringSet() =
  MMKVProperty({ kv.decodeStringSet(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvStringSet(default: Set<String>) =
  MMKVProperty({ kv.decodeStringSet(it) ?: default }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBytes() =
  MMKVProperty({ kv.decodeBytes(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBytes(default: ByteArray) =
  MMKVProperty({ kv.decodeBytes(it) ?: default }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable() =
  MMKVProperty({ kv.decodeParcelable(it, T::class.java) }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable(default: T) =
  MMKVProperty({ kv.decodeParcelable(it, T::class.java) ?: default }, { kv.encode(first, second) })

fun <V> MMKVProperty<V>.asLiveData() = object : ReadOnlyProperty<IMMKVOwner, MutableLiveData<V>> {
  private var cache: MutableLiveData<V>? = null

  override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): MutableLiveData<V> =
    cache ?: object : MutableLiveData<V>() {
      override fun getValue() = this@asLiveData.getValue(thisRef, property)

      override fun setValue(value: V) {
        if (super.getValue() == value) return
        this@asLiveData.setValue(thisRef, property, value)
        super.setValue(value)
      }

      override fun onActive() = super.setValue(value)
    }.also { cache = it }
}

val IMMKVOwner.allKV: Map<String, Any?>
  get() = HashMap<String, Any?>().also { map ->
    this::class.declaredMembers.filerProperties<KProperty1<IMMKVOwner, *>>("kv", "mmapID")
      .forEach { property ->
        property.isAccessible = true
        val value = property.get(this)
        if (value is MutableLiveData<*>) {
          map[property.name] = value.value
        } else {
          map[property.name] = value
        }
        property.isAccessible = false
      }
  }

inline fun <reified R : KProperty1<*, *>> Collection<*>.filerProperties(vararg exceptNames: String): List<R> =
  ArrayList<R>().also { destination ->
    for (element in this) if (element is R && !exceptNames.contains(element.name)) destination.add(element)
  }

class MMKVProperty<V>(
  private val decode: (String) -> V,
  private val encode: Pair<String, V>.() -> Boolean
) : ReadWriteProperty<IMMKVOwner, V> {
  override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): V =
    decode(property.name)

  override fun setValue(thisRef: IMMKVOwner, property: KProperty<*>, value: V) {
    encode((property.name) to value)
  }
}