package com.dylanc.mmkv.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.mmkv.MMKVOwner
import com.tencent.mmkv.MMKV


class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val dir = filesDir.absolutePath + "/mmkv_2"
    MMKV.initialize(this, dir)
  }
}