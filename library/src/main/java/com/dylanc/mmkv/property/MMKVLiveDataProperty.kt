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

import androidx.lifecycle.MutableLiveData
import com.dylanc.mmkv.IMMKVOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MMKVLiveDataProperty<V>(private val mmkvProperty: MMKVProperty<V>) : ReadOnlyProperty<IMMKVOwner, MutableLiveData<V>> {
  private var cache: MutableLiveData<V>? = null

  override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): MutableLiveData<V> =
    cache ?: MMKVLiveData(
      { mmkvProperty.getValue(thisRef, property) },
      { mmkvProperty.setValue(thisRef, property, it) }
    ).also { cache = it }
}

class MMKVLiveData<V>(
  private val getMMKVValue: () -> V,
  private val setMMKVValue: (V) -> Unit
) : MutableLiveData<V>(getMMKVValue()) {
  override fun getValue() = getMMKVValue()

  override fun setValue(value: V) {
    super.setValue(value)
    setMMKVValue(value)
  }
}