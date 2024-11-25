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

import com.dylanc.mmkv.IMMKVOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MMKVStateFlowProperty<V>(private val mmkvProperty: MMKVProperty<V>) : ReadOnlyProperty<IMMKVOwner, MutableStateFlow<V>> {
  private var cache: MutableStateFlow<V>? = null

  override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): MutableStateFlow<V> =
    cache ?: MMKVFlow(
      { mmkvProperty.getValue(thisRef, property) },
      { mmkvProperty.setValue(thisRef, property, it) }
    ).also { cache = it }
}

class MMKVFlow<V>(
  private val getMMKVValue: () -> V,
  private val setMMKVValue: (V) -> Unit,
  private val flow: MutableStateFlow<V> = MutableStateFlow(getMMKVValue())
) : MutableStateFlow<V> by flow {
  override var value: V
    get() = getMMKVValue()
    set(value) {
      val origin = flow.value
      flow.value = value
      if (origin != value) {
        setMMKVValue(value)
      }
    }

  override fun compareAndSet(expect: V, update: V): Boolean =
    flow.compareAndSet(expect, update).also { setSuccess ->
      if (setSuccess) setMMKVValue(value)
    }
}