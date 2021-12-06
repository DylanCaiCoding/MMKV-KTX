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

@file:Suppress("unused")

package com.dylanc.mmkv

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A class that has a MMKV object. If you want to customize the MMKV,
 * you can override the kv object. For example:
 *
 * ```kotlin
 * object DataRepository : MMKVOwner {
 *
 *   override val kv: MMKV = MMKV.mmkvWithID("MyID")
 * }
 * ```
 *
 * @author Dylan Cai
 */
interface MMKVOwner {
  val kv: MMKV get() = defaultMMKV

  companion object {
    private val defaultMMKV = MMKV.defaultMMKV()
  }
}

fun MMKVOwner.mmkvInt(default: Int = 0) =
  MMKVProperty(this, MMKV::decodeInt, MMKV::encode, default)

fun MMKVOwner.mmkvLong(default: Long = 0L) =
  MMKVProperty(this, MMKV::decodeLong, MMKV::encode, default)

fun MMKVOwner.mmkvBool(default: Boolean = false) =
  MMKVProperty(this, MMKV::decodeBool, MMKV::encode, default)

fun MMKVOwner.mmkvFloat(default: Float = 0f) =
  MMKVProperty(this, MMKV::decodeFloat, MMKV::encode, default)

fun MMKVOwner.mmkvDouble(default: Double = 0.0) =
  MMKVProperty(this, MMKV::decodeDouble, MMKV::encode, default)

fun MMKVOwner.mmkvString() =
  MMKVNullableProperty(this, MMKV::decodeString, MMKV::encode)

fun MMKVOwner.mmkvString(default: String) =
  MMKVNullablePropertyWithDefault(this, MMKV::decodeString, MMKV::encode, default)

fun MMKVOwner.mmkvStringSet(): ReadWriteProperty<MMKVOwner, Set<String>?> =
  MMKVNullableProperty(this, MMKV::decodeStringSet, MMKV::encode)

fun MMKVOwner.mmkvStringSet(default: Set<String>) =
  MMKVNullablePropertyWithDefault(this, MMKV::decodeStringSet, MMKV::encode, default)

fun MMKVOwner.mmkvBytes() =
  MMKVNullableProperty(this, MMKV::decodeBytes, MMKV::encode)

fun MMKVOwner.mmkvBytes(default: ByteArray) =
  MMKVNullablePropertyWithDefault(this, MMKV::decodeBytes, MMKV::encode, default)

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable() =
  MMKVParcelableProperty(this, T::class.java)

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable(default: T) =
  MMKVParcelablePropertyWithDefault(this, T::class.java, default)

class MMKVProperty<V>(
  private val owner: MMKVOwner,
  private val decode: MMKV.(String, V) -> V,
  private val encode: MMKV.(String, V) -> Boolean,
  private val defaultValue: V
) : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    owner.kv.decode(property.name, defaultValue)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    owner.kv.encode(property.name, value)
  }
}

class MMKVNullableProperty<V>(
  private val owner: MMKVOwner,
  private val decode: MMKV.(String, V?) -> V?,
  private val encode: MMKV.(String, V?) -> Boolean
) : ReadWriteProperty<MMKVOwner, V?> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V? =
    owner.kv.decode(property.name, null)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V?) {
    owner.kv.encode(property.name, value)
  }
}

class MMKVNullablePropertyWithDefault<V>(
  private val owner: MMKVOwner,
  private val decode: MMKV.(String, V?) -> V?,
  private val encode: MMKV.(String, V?) -> Boolean,
  private val defaultValue: V
) : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    owner.kv.decode(property.name, null) ?: defaultValue

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    owner.kv.encode(property.name, value)
  }
}

class MMKVParcelableProperty<V : Parcelable>(
  private val owner: MMKVOwner,
  private val clazz: Class<V>
) : ReadWriteProperty<MMKVOwner, V?> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V? =
    owner.kv.decodeParcelable(property.name, clazz)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V?) {
    owner.kv.encode(property.name, value)
  }
}

class MMKVParcelablePropertyWithDefault<V : Parcelable>(
  private val owner: MMKVOwner,
  private val clazz: Class<V>,
  private val defaultValue: V
) : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    owner.kv.decodeParcelable(property.name, clazz) ?: defaultValue

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    owner.kv.encode(property.name, value)
  }
}
