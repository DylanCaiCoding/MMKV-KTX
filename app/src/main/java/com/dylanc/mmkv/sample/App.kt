package com.dylanc.mmkv.sample

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * @author Dylan Cai
 */
class App : Application() {

  override fun onCreate() {
    super.onCreate()
    // When you remove the InitializationProvider in the AndroidManifest,
    // you can customize MMKV's root directory.
    val dir = "${filesDir?.absolutePath}/mmkv_2"
    MMKV.initialize(this, dir)
  }
}