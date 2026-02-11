/*
 * Copyright (c) 2025. Dylan Cai
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
import java.util.function.Predicate
import java.util.function.UnaryOperator
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MMKVListProperty<V>(
  private val mmkvProperty: MMKVProperty<V>
) : ReadOnlyProperty<IMMKVOwner, MutableList<V>> {
  private var cache: MMKVList<V>? = null

  override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): MutableList<V> =
    cache?.updateValues()
      ?: MMKVList(thisRef.kv, property.name, mmkvProperty.decode, mmkvProperty.encode).also { cache = it }
}

class MMKVList<V>(
  private val kv: MMKV,
  private val propertyName: String,
  private val decode: (String) -> V,
  private val encode: Pair<String, V>.() -> Boolean,
  private val list: MutableList<V> = mutableListOf()
) : MutableList<V> by list {
  private val sizeName = "$propertyName\$size"

  init {
    updateValues()
  }

  fun updateValues() = apply {
    list.clear()
    val size = kv.decodeInt(sizeName, 0)
    for (index in 0 until size) {
      list.add(decode(index.addPrefix()))
    }
  }

  private fun Int.addPrefix() = "$propertyName$$$this"

  private fun persistAll() {
    val oldSize = kv.decodeInt(sizeName, 0)
    val newSize = list.size
    kv.encode(sizeName, newSize)
    for (index in list.indices) {
      encode(index.addPrefix() to list[index])
    }
    if (newSize < oldSize) {
      for (index in newSize until oldSize) {
        kv.remove(index.addPrefix())
      }
    }
  }

  override fun set(index: Int, element: V): V =
    list.set(index, element).also { persistAll() }

  override fun add(element: V): Boolean =
    list.add(element).also { if (it) persistAll() }

  override fun add(index: Int, element: V) {
    list.add(index, element)
    persistAll()
  }

  override fun addAll(elements: Collection<V>): Boolean =
    list.addAll(elements).also { if (it) persistAll() }

  override fun addAll(index: Int, elements: Collection<V>): Boolean =
    list.addAll(index, elements).also { if (it) persistAll() }

  override fun remove(element: V): Boolean =
    list.remove(element).also { if (it) persistAll() }

  override fun removeAll(elements: Collection<V>): Boolean =
    list.removeAll(elements.toSet()).also { if (it) persistAll() }

  override fun retainAll(elements: Collection<V>): Boolean =
    list.retainAll(elements.toSet()).also { if (it) persistAll() }

  override fun removeAt(index: Int): V =
    list.removeAt(index).also { persistAll() }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun removeIf(filter: Predicate<in V>): Boolean =
    list.removeIf(filter).also { if (it) persistAll() }

  override fun clear() {
    val oldSize = kv.decodeInt(sizeName, 0)
    for (index in 0 until oldSize) {
      kv.remove(index.addPrefix())
    }
    kv.remove(sizeName)
    list.clear()
  }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun replaceAll(operator: UnaryOperator<V>) {
    list.replaceAll(operator)
    persistAll()
  }

  override fun iterator(): MutableIterator<V> {
    val iterator = list.iterator()
    return object : MutableIterator<V> by iterator {
      override fun remove() = iterator.remove().also { persistAll() }
    }
  }

  override fun listIterator(): MutableListIterator<V> =
    listIterator(0)

  override fun listIterator(index: Int): MutableListIterator<V> {
    val iterator = list.listIterator(index)
    return object : MutableListIterator<V> by iterator {
      override fun add(element: V) = iterator.add(element).also { persistAll() }

      override fun remove() = iterator.remove().also { persistAll() }

      override fun set(element: V) = iterator.set(element).also { persistAll() }
    }
  }

  override fun subList(fromIndex: Int, toIndex: Int): MutableList<V> {
    val subList = list.subList(fromIndex, toIndex)
    return object : MutableList<V> by subList {
      override fun add(element: V): Boolean =
        subList.add(element).also { if (it) persistAll() }

      override fun add(index: Int, element: V) {
        subList.add(index, element)
        persistAll()
      }

      override fun addAll(elements: Collection<V>): Boolean =
        subList.addAll(elements).also { if (it) persistAll() }

      override fun addAll(index: Int, elements: Collection<V>): Boolean =
        subList.addAll(index, elements).also { if (it) persistAll() }

      override fun remove(element: V): Boolean =
        subList.remove(element).also { if (it) persistAll() }

      override fun removeAll(elements: Collection<V>): Boolean =
        subList.removeAll(elements.toSet()).also { if (it) persistAll() }

      override fun retainAll(elements: Collection<V>): Boolean =
        subList.retainAll(elements.toSet()).also { if (it) persistAll() }

      override fun removeAt(index: Int): V =
        subList.removeAt(index).also { persistAll() }

      override fun clear() {
        subList.clear()
        persistAll()
      }

      override fun set(index: Int, element: V): V =
        subList.set(index, element).also { persistAll() }
    }
  }
}