/*
 * Copyright (c) 2024. Dylan Cai
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

package com.dylanc.mmkv.property

import android.os.Build
import androidx.annotation.RequiresApi
import com.dylanc.mmkv.IMMKVOwner
import com.tencent.mmkv.MMKV
import java.util.function.BiFunction
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MMKVMapProperty<V>(
  private val mmkvProperty: BaseMMKVProperty<V>
) : ReadOnlyProperty<IMMKVOwner, MutableMap<String, V>> {
  private var cache: MMKVMap<V>? = null

  override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): MutableMap<String, V> =
    cache?.updateValues() ?: MMKVMap(
      thisRef.kv, mmkvProperty.toName(property.name),
      mmkvProperty::decode, mmkvProperty::encode
    ).also { cache = it }
}

class MMKVMap<V>(
  private val kv: MMKV,
  private val propertyName: String,
  private val decode: (String) -> V,
  private val encode: (String, V) -> Boolean,
  private val map: MutableMap<String, V> = mutableMapOf()
) : MutableMap<String, V> by map {
  private val keysName = "$propertyName\$key"

  init {
    updateValues()
  }

  fun updateValues() = apply {
    map.clear()
    keys.forEach { key ->
      map[key] = decode(key.addSuffix())
    }
  }

  private fun String.addSuffix() = "$propertyName$$$this"

  override fun get(key: String): V? = decode(key.addSuffix())

  override fun put(key: String, value: V): V? =
    map.put(key, value).also {
      encode(key.addSuffix(), value)
      kv.encode(keysName, keys + key)
    }

  override fun putAll(from: Map<out String, V>) =
    map.putAll(from).also {
      from.forEach { (key, value) -> encode(key.addSuffix(), value) }
      kv.encode(keysName, keys + from.keys)
    }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun putIfAbsent(key: String, value: V): V? {
    val isAbsent = map.containsKey(key).not()
    return map.putIfAbsent(key, value).also {
      if (isAbsent) {
        encode(key.addSuffix(), value)
        kv.encode(keysName, keys + key)
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun merge(key: String, value: V & Any, remappingFunction: BiFunction<in V & Any, in V & Any, out V?>): V? =
    map.merge(key, value, remappingFunction).also {
      if (it != null) {
        encode(key.addSuffix(), it)
        kv.encode(keysName, keys + key)
      }
    }

  override fun remove(key: String): V? =
    map.remove(key).also {
      kv.remove(key.addSuffix())
      kv.encode(keysName, keys - key)
    }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun remove(key: String, value: V): Boolean =
    map.remove(key, value).also { removed ->
      if (removed) {
        kv.remove(key.addSuffix())
        kv.encode(keysName, keys - key)
      }
    }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun replace(key: String, value: V): V? =
    map.replace(key, value).also {
      if (containsKey(key)) encode(key.addSuffix(), value)
    }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun replace(key: String, oldValue: V, newValue: V): Boolean =
    map.replace(key, oldValue, newValue).also { replaced ->
      if (replaced) encode(key.addSuffix(), newValue)
    }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun replaceAll(function: BiFunction<in String, in V, out V>) =
    map.replaceAll { key, value ->
      function.apply(key, value)
        .also { encode(key.addSuffix(), it) }
    }

  override fun clear() {
    keys.forEach { key -> kv.remove(key.addSuffix()) }
    kv.remove(keysName)
    map.clear()
  }

  override val keys: MutableSet<String>
    get() = kv.decodeStringSet(keysName).orEmpty().toMutableSet()

  override val size: Int
    get() = keys.size

  override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
    get() {
      val entries = map.entries
      return object : MutableSet<MutableMap.MutableEntry<String, V>> by entries {

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, V>> {
          val iterator = entries.iterator()
          return object : MutableIterator<MutableMap.MutableEntry<String, V>> by iterator {
            private var lastKey: String? = null

            override fun remove() =
              iterator.remove().also {
                kv.remove(lastKey?.addSuffix())
                kv.encode(keysName, keys - lastKey)
              }

            override fun next(): MutableMap.MutableEntry<String, V> {
              val entry = iterator.next()
              lastKey = entry.key
              return object : MutableMap.MutableEntry<String, V> by entry {
                override fun setValue(newValue: V): V =
                  entry.setValue(newValue).also { encode(key.addSuffix(), newValue) }
              }
            }
          }
        }
      }
    }

  override fun toString() = map.toString()

  override fun hashCode() = map.hashCode()

  override fun equals(other: Any?) = map == other
}