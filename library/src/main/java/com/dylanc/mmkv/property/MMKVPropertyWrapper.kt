/*
 * Copyright (c) 2026. Dylan Cai
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
import kotlin.collections.orEmpty
import kotlin.collections.toMutableSet
import kotlin.reflect.KProperty


class MMKVPropertyWrapper<V>(
  private val decorated: BaseMMKVProperty<V>,
  private val suffix: String
) : BaseMMKVProperty<V>() {

  override fun setValue(thisRef: IMMKVOwner, property: KProperty<*>, value: V) {
    super.setValue(thisRef, property, value)

    val keysName = "${property.name}\$key"
    val keys = thisRef.kv.decodeStringSet(keysName).orEmpty().toMutableSet()
    if (value != null) {
      thisRef.kv.encode(keysName, keys + property.name)
    } else {
      thisRef.kv.encode(keysName, keys - property.name)
    }
  }

  override fun toName(propertyName: String) =
    decorated.toName("$propertyName$$$suffix")

  override fun decode(key: String): V =
    decorated.decode(key)

  override fun encode(key: String, value: V) =
    decorated.encode(key, value)
}