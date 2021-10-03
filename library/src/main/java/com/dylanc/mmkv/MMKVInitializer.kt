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

import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV

/**
 * Set up MMKV on App startup. You can add the following code to customize MMKV's root directory.
 *
 * **Step 1. Add startup's dependency in `build.gradle`:**
 *
 * ```
 * implementation "androidx.startup:startup-runtime:1.1.0"
 * ```
 *
 * **Step 2. Remove MMKVInitializer in `AndroidManifest.xml`:**
 *
 * ```xml
 * <provider
 *   android:name="androidx.startup.InitializationProvider"
 *   android:authorities="${applicationId}.androidx-startup"
 *   android:exported="false"
 *   tools:node="merge">
 *   <meta-data
 *     android:name="com.dylanc.mmkv.MMKVInitializer"
 *     tools:node="remove" />
 * </provider>
 * ```
 *
 * **Then you can customize MMKV's root directory on App startup, for example:**
 *
 * ```kotlin
 * val dir = "${filesDir?.absolutePath}/mmkv_2"
 * MMKV.initialize(this, dir)
 * ```
 *
 * @author Dylan Cai
 */
class MMKVInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    MMKV.initialize(context)
  }

  override fun dependencies() = emptyList<Class<Initializer<*>>>()
}
