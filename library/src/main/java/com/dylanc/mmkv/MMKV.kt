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
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author Dylan Cai
 */

private val defaultMMKV = MMKV.defaultMMKV()

interface MMKVOwner {
  val kv: MMKV get() = defaultMMKV
}

fun MMKVOwner.mmkvInt(default: Int = 0) =
  kv.property(MMKV::decodeInt, MMKV::encode, default)

fun MMKVOwner.mmkvLong(default: Long = 0L) =
  kv.property(MMKV::decodeLong, MMKV::encode, default)

fun MMKVOwner.mmkvBool(default: Boolean = false) =
  kv.property(MMKV::decodeBool, MMKV::encode, default)

fun MMKVOwner.mmkvFloat(default: Float = 0f) =
  kv.property(MMKV::decodeFloat, MMKV::encode, default)

fun MMKVOwner.mmkvDouble(default: Double = 0.0) =
  kv.property(MMKV::decodeDouble, MMKV::encode, default)

fun MMKVOwner.mmkvString() =
  kv.nullableProperty(MMKV::decodeString, MMKV::encode)

fun MMKVOwner.mmkvString(default: String) =
  kv.nullablePropertyWithDefault(MMKV::decodeString, MMKV::encode, default)

fun MMKVOwner.mmkvStringSet(): ReadWriteProperty<MMKVOwner, Set<String>?> =
  kv.nullableProperty(MMKV::decodeStringSet, MMKV::encode)

fun MMKVOwner.mmkvStringSet(default: Set<String>) =
  kv.nullablePropertyWithDefault(MMKV::decodeStringSet, MMKV::encode, default)

fun MMKVOwner.mmkvBytes() =
  kv.nullableProperty(MMKV::decodeBytes, MMKV::encode)

fun MMKVOwner.mmkvBytes(default: ByteArray) =
  kv.nullablePropertyWithDefault(MMKV::decodeBytes, MMKV::encode, default)

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable() =
  mmkvParcelable(T::class.java)

fun <T : Parcelable> MMKVOwner.mmkvParcelable(clazz: Class<T>) =
  object : ReadWriteProperty<MMKVOwner, T?> {
    override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): T? =
      kv.decodeParcelable(getKey(property), clazz)

    override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: T?) {
      kv.encode(getKey(property), value)
    }
  }

inline fun <reified T : Parcelable> MMKVOwner.mmkvParcelable(default: T) =
  mmkvParcelable(T::class.java, default)

fun <T : Parcelable> MMKVOwner.mmkvParcelable(clazz: Class<T>, default: T) =
  object : ReadWriteProperty<MMKVOwner, T> {
    override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): T =
      kv.decodeParcelable(getKey(property), clazz) ?: default

    override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: T) {
      kv.encode(getKey(property), value)
    }
  }

private inline fun <V> MMKV.property(
  crossinline getValue: MMKV.(String, V) -> V,
  crossinline putValue: MMKV.(String, V) -> Boolean,
  defaultValue: V
) = object : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    getValue(thisRef.getKey(property), defaultValue)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    putValue(thisRef.getKey(property), value)
  }
}

private inline fun <V> MMKV.nullableProperty(
  crossinline getValue: MMKV.(String, V?) -> V?,
  crossinline putValue: MMKV.(String, V?) -> Boolean
) = object : ReadWriteProperty<MMKVOwner, V?> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V? =
    getValue(thisRef.getKey(property), null)

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V?) {
    putValue(thisRef.getKey(property), value)
  }
}

private inline fun <V> MMKV.nullablePropertyWithDefault(
  crossinline getValue: MMKV.(String, V?) -> V?,
  crossinline putValue: MMKV.(String, V?) -> Boolean,
  defaultValue: V
) = object : ReadWriteProperty<MMKVOwner, V> {
  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
    getValue(thisRef.getKey(property), null) ?: defaultValue

  override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
    putValue(thisRef.getKey(property), value)
  }
}

private fun MMKVOwner.getKey(property: KProperty<*>) =
  "${javaClass.canonicalName}_${property.name}"
