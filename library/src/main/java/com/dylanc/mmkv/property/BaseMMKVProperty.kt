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
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class BaseMMKVProperty<V> : ReadWriteProperty<IMMKVOwner, V> {

  override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): V =
    decode(property.name)

  override fun setValue(thisRef: IMMKVOwner, property: KProperty<*>, value: V) {
    encode(property.name, value)
  }

  open fun toName(propertyName: String) = propertyName

  abstract fun decode(key: String): V
  abstract fun encode(key: String, value: V): Boolean
}