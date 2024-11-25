package com.dylanc.mmkv

import android.os.Parcelable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.tencent.mmkv.MMKV
import kotlinx.parcelize.Parcelize
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Dylan Cai
 */
@RunWith(AndroidJUnit4::class)
class MMKVTest : IMMKVOwner by MMKVOwner(mmapID = "test") {

  private var i1 by mmkvInt()
  private var i2 by mmkvInt(default = -1)
  private var l1 by mmkvLong()
  private var l2 by mmkvLong(default = -1L)
  private var b1 by mmkvBool()
  private var b2 by mmkvBool(default = true)
  private var f1 by mmkvFloat()
  private var f2 by mmkvFloat(default = -1f)
  private var d1 by mmkvDouble()
  private var d2 by mmkvDouble(default = -1.0)
  private var s1 by mmkvString()
  private var s2 by mmkvString(default = "")
  private var set1 by mmkvStringSet()
  private var set2 by mmkvStringSet(default = emptySet())
  private var bytes1 by mmkvBytes()
  private var bytes2 by mmkvBytes(default = byteArrayOf(0x1A))
  private var user1 by mmkvParcelable<User>()
  private var user2 by mmkvParcelable(default = User(0, "Admin"))
  private val liveData1 by mmkvInt().asLiveData()
  private val liveData2 by mmkvInt(default = -1).asLiveData()
  private val flow1 by mmkvInt().asStateFlow()
  private val flow2 by mmkvInt(default = -1).asStateFlow()
  private val map1 by mmkvInt().asMap()
  private val map2 by mmkvInt(-1).asMap()

  override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.MULTI_PROCESS_MODE)

  @Before
  fun clear() {
    kv.clearAll()
  }

  @Test
  fun putInt1() {
    Assert.assertEquals(0, i1)
    i1 = 6
    Assert.assertEquals(6, i1)
  }

  @Test
  fun putInt2() {
    Assert.assertEquals(-1, i2)
    i2 = 6
    Assert.assertEquals(6, i2)
  }

  @Test
  fun putLong1() {
    Assert.assertEquals(0L, l1)
    l1 = 3L
    Assert.assertEquals(3L, l1)
  }

  @Test
  fun putLong2() {
    Assert.assertEquals(-1L, l2)
    l2 = 3L
    Assert.assertEquals(3L, l2)
  }

  @Test
  fun putBoolean1() {
    Assert.assertEquals(false, b1)
    b1 = true
    Assert.assertEquals(true, b1)
  }

  @Test
  fun putBoolean2() {
    Assert.assertEquals(true, b2)
    b2 = true
    Assert.assertEquals(true, b2)
  }

  @Test
  fun putFloat1() {
    Assert.assertEquals(0f, f1)
    f1 = 0.5f
    Assert.assertEquals(0.5f, f1)
  }

  @Test
  fun putFloat2() {
    Assert.assertEquals(-1f, f2)
    f2 = 0.5f
    Assert.assertEquals(0.5f, f2)
  }

  @Test
  fun putDouble1() {
    Assert.assertEquals(0.0, d1, 0.0)
    d1 = 0.5
    Assert.assertEquals(0.5, d1, 0.0)
  }

  @Test
  fun putDouble2() {
    Assert.assertEquals(-1.0, d2, 0.0)
    d2 = 0.5
    Assert.assertEquals(0.5, d2, 0.0)
  }

  @Test
  fun putString1() {
    Assert.assertEquals(null, s1)
    s1 = "test"
    Assert.assertEquals("test", s1)
  }

  @Test
  fun putString2() {
    Assert.assertEquals("", s2)
    s2 = "test"
    Assert.assertEquals("test", s2)
  }

  @Test
  fun putSet1() {
    Assert.assertEquals(null, set1)
    val set = setOf("22", "33")
    set1 = set
    Assert.assertEquals(set, set1)
  }

  @Test
  fun putSet2() {
    Assert.assertEquals(emptySet<String>(), set2)
    val set = setOf("22", "33")
    set2 = set
    Assert.assertEquals(set, set2)
  }

  @Test
  fun putBytes1() {
    Assert.assertEquals(null, bytes1)
    val bytes = byteArrayOf(0x1A, 0x2B)
    bytes1 = bytes
    Assert.assertTrue(bytes.contentEquals(bytes1))
  }

  @Test
  fun putBytes2() {
    Assert.assertTrue(byteArrayOf(0x1A).contentEquals(bytes2))
    val bytes = byteArrayOf(0x1A, 0x2B)
    bytes2 = bytes
    Assert.assertTrue(bytes.contentEquals(bytes2))
  }

  @Test
  fun putParcelable1() {
    Assert.assertEquals(null, user1)
    user1 = User(1, "DylanCai")
    Assert.assertEquals(User(1, "DylanCai"), user1)
  }

  @Test
  fun putParcelable2() {
    Assert.assertEquals(User(0, "Admin"), user2)
    user2 = User(1, "DylanCai")
    Assert.assertEquals(User(1, "DylanCai"), user2)
  }

  @Test
  fun putLiveData1() {
    UiThreadStatement.runOnUiThread {
      Assert.assertEquals(0, liveData1.value)
      liveData1.value = 6
      Assert.assertEquals(6, liveData1.value)
      clearAllKV()
      Assert.assertEquals(0, liveData1.value)
    }
  }

  @Test
  fun putLiveData2() {
    UiThreadStatement.runOnUiThread {
      Assert.assertEquals(-1, liveData2.value)
      liveData2.value = 6
      Assert.assertEquals(6, liveData2.value)
      clearAllKV()
      Assert.assertEquals(-1, liveData2.value)
    }
  }

  @Test
  fun putFlow1() {
    Assert.assertEquals(0, flow1.value)
    flow1.value = 6
    Assert.assertEquals(6, flow1.value)
    clearAllKV()
    Assert.assertEquals(0, flow1.value)
  }

  @Test
  fun putFlow2() {
    Assert.assertEquals(-1, flow2.value)
    flow2.value = 6
    Assert.assertEquals(6, flow2.value)
    clearAllKV()
    Assert.assertEquals(-1, flow2.value)
  }

  @Test
  fun putMapData1() {
    Assert.assertEquals(0, map1["id1"])
    Assert.assertEquals(0, map1["id2"])
    map1["id1"] = 1
    map1["id2"] = 2
    Assert.assertEquals(1, map1["id1"])
    Assert.assertEquals(2, map1["id2"])
    map1.clear()
    Assert.assertEquals(0, map1["id1"])
    Assert.assertEquals(0, map1["id2"])
    map1["id1"] = 3
    map1["id2"] = 4
    clearAllKV()
    Assert.assertEquals(0, map1["id1"])
    Assert.assertEquals(0, map1["id2"])
  }

  @Test
  fun putMapData2() {
    Assert.assertEquals(-1, map2["id1"])
    Assert.assertEquals(-1, map2["id2"])
    map2["id1"] = 1
    map2["id2"] = 2
    Assert.assertEquals(1, map2["id1"])
    Assert.assertEquals(2, map2["id2"])
    map2.clear()
    Assert.assertEquals(-1, map2["id1"])
    Assert.assertEquals(-1, map2["id2"])
    map2["id1"] = 3
    map2["id2"] = 4
    Assert.assertEquals(3, map2["id1"])
    Assert.assertEquals(4, map2["id2"])
    clearAllKV()
    Assert.assertEquals(-1, map2["id1"])
    Assert.assertEquals(-1, map2["id2"])
    Assert.assertEquals(0, map2.size)
  }

  @Test
  fun removeValueForKey() {
    map1["id1"] = 1
    map1["id2"] = 2
    Assert.assertEquals(1, map1["id1"])
    Assert.assertEquals(2, map1["id2"])
    val iterator = map1.entries.iterator()
    while (iterator.hasNext()) {
      val entry = iterator.next()
      if (entry.key == "id1") {
        iterator.remove()
      }
    }
    Assert.assertEquals(0, map1["id1"])
    Assert.assertEquals(2, map1["id2"])
    Assert.assertEquals(1, map1.size)

    map1.entries.removeIf { it.key == "id2" }
    Assert.assertEquals(0, map1["id2"])
    Assert.assertEquals(0, map1.size)

    map1["merge"] = 1
    map1.merge("merge", 2) { oldValue, newValue -> oldValue + newValue }
    Assert.assertEquals(3, map1["merge"])
  }

  @Test
  fun removeValue() {
    i1 = 6
    Assert.assertTrue(kv.containsKey(::i1.name))
    kv.removeValueForKey(::i1.name)
    Assert.assertFalse(kv.containsKey(::i1.name))
  }

  @Test
  fun removeValues() {
    s1 = "1"
    s2 = "2"
    Assert.assertTrue(kv.containsKey(::s1.name))
    Assert.assertTrue(kv.containsKey(::s2.name))
    kv.removeValuesForKeys(arrayOf(::s1.name, ::s2.name))
    Assert.assertFalse(kv.containsKey(::s1.name))
    Assert.assertFalse(kv.containsKey(::s2.name))
  }

  @Test
  fun getAll() {
    clearAllKV()
    map1["id1"] = 1
    map1["id2"] = 2
    Assert.assertEquals(setOf("id1", "id2"), map1.keys)
    val allKV = allKV
    Assert.assertTrue(allKV.size == 24)
    Assert.assertEquals(0, allKV["i1"])
    Assert.assertEquals(-1, allKV["i2"])
    Assert.assertEquals(0L, allKV["l1"])
    Assert.assertEquals(-1L, allKV["l2"])
    Assert.assertEquals(false, allKV["b1"])
    Assert.assertEquals(true, allKV["b2"])
    Assert.assertEquals(0f, allKV["f1"])
    Assert.assertEquals(-1f, allKV["f2"])
    Assert.assertEquals(0.0, allKV["d1"])
    Assert.assertEquals(-1.0, allKV["d2"])
    Assert.assertEquals(null, allKV["s1"])
    Assert.assertEquals("", allKV["s2"])
    Assert.assertEquals(null, allKV["set1"])
    Assert.assertEquals(emptySet<String>(), allKV["set2"])
    Assert.assertEquals(null, allKV["bytes1"])
    Assert.assertTrue(byteArrayOf(0x1A).contentEquals(allKV["bytes2"] as? ByteArray))
    Assert.assertEquals(null, allKV["user1"])
    Assert.assertEquals(User(0, "Admin"), allKV["user2"])
    Assert.assertEquals(0, allKV["liveData1"])
    Assert.assertEquals(-1, allKV["liveData2"])
    Assert.assertEquals(0, allKV["flow1"])
    Assert.assertEquals(-1, allKV["flow2"])
    Assert.assertEquals(mapOf("id1" to 1, "id2" to 2), allKV["map1"])
    Assert.assertEquals(emptyMap<String, Int>(), allKV["map2"])
  }

  @Parcelize
  data class User(val id: Long, val name: String) : Parcelable
}
