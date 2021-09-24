package com.dylanc.mmkv

import android.os.Parcelable
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.parcelize.Parcelize
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Dylan Cai
 */

@RunWith(AndroidJUnit4::class)
class MMKVTest {

  @Before
  fun clear() {
    DataRepository.clearAll()
  }

  @Test
  fun putInt1() {
    Assert.assertEquals(0, DataRepository.i1)
    DataRepository.i1 = 6
    Assert.assertEquals(6, DataRepository.i1)
  }

  @Test
  fun putInt2() {
    Assert.assertEquals(-1, DataRepository.i2)
    DataRepository.i2 = 6
    Assert.assertEquals(6, DataRepository.i2)
  }

  @Test
  fun putLong1() {
    Assert.assertEquals(0L, DataRepository.l1)
    DataRepository.l1 = 3L
    Assert.assertEquals(3L, DataRepository.l1)
  }

  @Test
  fun putLong2() {
    Assert.assertEquals(-1L, DataRepository.l2)
    DataRepository.l2 = 3L
    Assert.assertEquals(3L, DataRepository.l2)
  }

  @Test
  fun putBoolean1() {
    Assert.assertEquals(false, DataRepository.b1)
    DataRepository.b1 = true
    Assert.assertEquals(true, DataRepository.b1)
  }

  @Test
  fun putBoolean2() {
    Assert.assertEquals(true, DataRepository.b2)
    DataRepository.b2 = true
    Assert.assertEquals(true, DataRepository.b2)
  }

  @Test
  fun putFloat1() {
    Assert.assertEquals(0f, DataRepository.f1)
    DataRepository.f1 = 0.5f
    Assert.assertEquals(0.5f, DataRepository.f1)
  }

  @Test
  fun putFloat2() {
    Assert.assertEquals(-1f, DataRepository.f2)
    DataRepository.f2 = 0.5f
    Assert.assertEquals(0.5f, DataRepository.f2)
  }

  @Test
  fun putDouble1() {
    Assert.assertEquals(0.0, DataRepository.d1, 0.0)
    DataRepository.d1 = 0.5
    Assert.assertEquals(0.5, DataRepository.d1, 0.0)
  }

  @Test
  fun putDouble2() {
    Assert.assertEquals(-1.0, DataRepository.d2, 0.0)
    DataRepository.d2 = 0.5
    Assert.assertEquals(0.5, DataRepository.d2, 0.0)
  }

  @Test
  fun putString1() {
    Assert.assertEquals(null, DataRepository.s1)
    DataRepository.s1 = "test"
    Assert.assertEquals("test", DataRepository.s1)
  }

  @Test
  fun putString2() {
    Assert.assertEquals("", DataRepository.s2)
    DataRepository.s2 = "test"
    Assert.assertEquals("test", DataRepository.s2)
  }

  @Test
  fun putSet1() {
    Assert.assertEquals(null, DataRepository.set1)
    val set = setOf("22", "33")
    DataRepository.set1 = set
    Assert.assertEquals(set, DataRepository.set1)
  }

  @Test
  fun putSet2() {
    Assert.assertEquals(emptySet<String>(), DataRepository.set2)
    val set = setOf("22", "33")
    DataRepository.set2 = set
    Assert.assertEquals(set, DataRepository.set2)
  }

  @Test
  fun putBytes1() {
    Assert.assertEquals(null, DataRepository.bytes1)
    val bytes = byteArrayOf(0x1A, 0x2B)
    DataRepository.bytes1 = bytes
    Assert.assertTrue(bytes.contentEquals(DataRepository.bytes1))
  }

  @Test
  fun putBytes2() {
    Assert.assertTrue(byteArrayOf(0x1A).contentEquals(DataRepository.bytes2))
    val bytes = byteArrayOf(0x1A, 0x2B)
    DataRepository.bytes2 = bytes
    Assert.assertTrue(bytes.contentEquals(DataRepository.bytes2))
  }

  @Test
  fun putParcelable1() {
    Assert.assertEquals(null, DataRepository.user1)
    DataRepository.user1 = User(1, "DylanCai")
    Assert.assertEquals(User(1, "DylanCai"), DataRepository.user1)
  }

  @Test
  fun putParcelable2() {
    Assert.assertEquals(User(0, "Admin"), DataRepository.user2)
    DataRepository.user2 = User(1, "DylanCai")
    Assert.assertEquals(User(1, "DylanCai"), DataRepository.user2)
  }
}

object DataRepository : MMKVOwner {
  var i1 by mmkvInt()
  var i2 by mmkvInt(default = -1)
  var l1 by mmkvLong()
  var l2 by mmkvLong(default = -1L)
  var b1 by mmkvBool()
  var b2 by mmkvBool(default = true)
  var f1 by mmkvFloat()
  var f2 by mmkvFloat(default = -1f)
  var d1 by mmkvDouble()
  var d2 by mmkvDouble(default = -1.0)
  var s1 by mmkvString()
  var s2 by mmkvString(default = "")
  var set1 by mmkvStringSet()
  var set2 by mmkvStringSet(default = emptySet())
  var bytes1 by mmkvBytes()
  var bytes2 by mmkvBytes(default = byteArrayOf(0x1A))
  var user1 by mmkvParcelable<User>()
  var user2 by mmkvParcelable(default = User(0, "Admin"))

  fun clearAll() {
    kv.clearAll()
  }
}

@Parcelize
data class User(val id: Long, val name: String) : Parcelable
