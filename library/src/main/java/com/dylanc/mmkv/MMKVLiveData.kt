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

import androidx.lifecycle.MutableLiveData
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <V> MMKVProperty<V>.asLiveData() = object : ReadOnlyProperty<MMKVOwner, MMKVLiveData<V>> {
  private var cache: MMKVLiveData<V>? = null

  override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): MMKVLiveData<V> =
    cache ?: MMKVLiveDataImpl(this@asLiveData, thisRef, property).also { cache = it }
}

abstract class MMKVLiveData<V> : MutableLiveData<V>() {
  abstract var key: String?
}

class MMKVLiveDataImpl<V>(
  private val delegate: MMKVProperty<V>,
  private val owner: MMKVOwner,
  private val property: KProperty<*>
) : MMKVLiveData<V>() {
  override var key: String? by delegate::key

  override fun getValue() = delegate.getValue(owner, property)

  override fun setValue(value: V) {
    if (super.getValue() == value) return
    delegate.setValue(owner, property, value)
    super.setValue(value)
  }

  override fun onActive() = super.setValue(value)
}