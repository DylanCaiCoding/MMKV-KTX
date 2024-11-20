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
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel

/**
 * Initialize MMKV by App startup. You can cancel the automatic initialization of MMKV.
 * If you want to change root directory or log level, you can add the following code to
 * the AndroidManifest.xml:
 *
 * ```xml
 * <meta-data
 *   android:name="mmkv_root_dir"
 *   android:value="/mmkv_2" />
 *
 * <meta-data
 *   android:name="mmkv_log_level"
 *   android:value="debug" />
 * ```
 *
 * @author Dylan Cai
 */
class MMKVInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    val rootDir = context.filesDir.absolutePath + (getMetaData(context, "mmkv_root_dir") ?: "/mmkv")
    val logLevel = when (getMetaData(context, "mmkv_log_level")?.lowercase()) {
      "debug" -> MMKVLogLevel.LevelDebug
      "info" -> MMKVLogLevel.LevelInfo
      "warning" -> MMKVLogLevel.LevelWarning
      "error" -> MMKVLogLevel.LevelError
      "none" -> MMKVLogLevel.LevelNone
      else -> MMKVLogLevel.LevelInfo
    }
    MMKV.initialize(context, rootDir, logLevel)
  }

  private fun getMetaData(context: Context, key: String): String? =
    try {
      context.packageManager.getApplicationInfo(context.packageName, GET_META_DATA)
        .metaData?.getString(key)
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
      null
    }

  override fun dependencies() = emptyList<Class<Initializer<*>>>()
}