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

@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.dylanc.mmkv

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A class that has a MMKV object. If you want to customize the MMKV,
 * you can override the customMMKV object. For example:
 *
 * ```kotlin
 * object DataRepository : MMKVOwner {
 *
 *   override val customMMKV: MMKV?
 *     get() = MMKV.mmkvWithID("MyID")
 * }
 * ```
 *
 * @author Dylan Cai
 */
interface MMKVOwner {

  val kv: MMKV
    get() {
      val clazzName = javaClass.name
      if (!mmkvMap.containsKey(clazzName)) {
        customMMKV?.let { mmkvMap[clazzName] = it }
      }
      return mmkvMap.getValue(clazzName)
    }

  val customMMKV: MMKV? get() = null

  companion object {
    private val mmkvMap = mutableMapOf<String, MMKV>()
      .withDefault { MMKV.defaultMMKV() }
  }
}

inline fun MMKVOwner.mmkvInt(default: Int = 0) =
  MMKVProperty(kv, MMKV::decodeInt, MMKV::encode, default)

inline fun MMKVOwner.mmkvLong(default: Long = 0L) =
  MMKVProperty(kv, MMKV::decodeLong, MMKV::encode, default)

inline fun MMKVOwner.mmkvBool(default: Boolean = false) =
  MMKVProperty(kv, MMKV::decodeBool, MMKV::encode, default)

inline fun MMKVOwner.mmkvFloat(default: Float = 0f) =
  MMKVProperty(kv, MMKV::decodeFloat, MMKV::encode, default)

inline fun MMKVOwner.mmkvDouble(default: Double = 0.0) =
  MMKVProperty(kv, MMKV::decodeDouble, MMKV::encode, default)

inline fun MMKVOwner.mmkvString() =
  MMKVNullableProperty(kv, MMKV::decodeString, MMKV::encode)

inline fun MMKVOwner.mmkvString(default: String) =
  MMKVNullablePropertyWithDefault(kv, MMKV::decodeString, MMKV::encode, default)

inline fun MMKVOwner.mmkvStringSet(): ReadWriteProperty<MMKVOwner, Set<String>?> =
  MMKVNullableProperty(kv, MMKV::decodeStringSet, MMKV::encode)

inline fun MMKVOwner.mmkvStringSet(default: Set<String>) =
  MMKVNullablePropertyWithDefault(kv, MMKV::decodeStringSet, MMKV::encode, default)

inline fun MMKVOwner.mmkvBytes() =
  MMKVNullableProperty(kv, MMKV::decodeBytes, MMKV::encode)

inline fun MMKVOwner.mmkvBytes(default: ByteArray) =
  MMKVNullablePropertyWithDefault(kv, MMKV::decodeBytes, MMKV::encode, default)

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable() =
  MMKVParcelableProperty(kv, T::class.java)

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable(default: T) =
  MMKVParcelablePropertyWithDefault(kv, T::class.java, default)

class MMKVProperty<V>(
  private val kv: MMKV,
  private val decode: MMKV.(String, V) -> V,
  private val encode: MMKV.(String, V) -> Boolean,
  private val defaultValue: V
) : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    kv.decode(property.name, defaultValue)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    kv.encode(property.name, value)
  }
}

class MMKVNullableProperty<V>(
  private val kv: MMKV,
  private val decode: MMKV.(String, V?) -> V?,
  private val encode: MMKV.(String, V?) -> Boolean
) : ReadWriteProperty<MMKVOwner, V?> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V? =
    kv.decode(property.name, null)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V?) {
    kv.encode(property.name, value)
  }
}

class MMKVNullablePropertyWithDefault<V>(
  private val kv: MMKV,
  private val decode: MMKV.(String, V?) -> V?,
  private val encode: MMKV.(String, V?) -> Boolean,
  private val defaultValue: V
) : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    kv.decode(property.name, null) ?: defaultValue

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    kv.encode(property.name, value)
  }
}

class MMKVParcelableProperty<V : Parcelable>(
  private val kv: MMKV,
  private val clazz: Class<V>
) : ReadWriteProperty<MMKVOwner, V?> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V? =
    kv.decodeParcelable(property.name, clazz)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V?) {
    kv.encode(property.name, value)
  }
}

class MMKVParcelablePropertyWithDefault<V : Parcelable>(
  private val kv: MMKV,
  private val clazz: Class<V>,
  private val defaultValue: V
) : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    kv.decodeParcelable(property.name, clazz) ?: defaultValue

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    kv.encode(property.name, value)
  }
}
