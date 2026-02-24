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
  private val list by mmkvInt().asList()
  private var nested1 by mmkvString().withKey("aaa")
  private var nested2 by mmkvString().withKey("aaa").withKey("bbb")

  override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.MULTI_PROCESS_MODE)

  @Before
  fun clear() {
    kv.clearAll()
  }

  @Test
  fun intDefaultZero_putAndGet() {
    Assert.assertEquals(0, i1)
    i1 = 6
    Assert.assertEquals(6, i1)
  }

  @Test
  fun intDefaultMinusOne_putAndGet() {
    Assert.assertEquals(-1, i2)
    i2 = 6
    Assert.assertEquals(6, i2)
  }

  @Test
  fun longDefaultZero_putAndGet() {
    Assert.assertEquals(0L, l1)
    l1 = 3L
    Assert.assertEquals(3L, l1)
  }

  @Test
  fun longDefaultMinusOne_putAndGet() {
    Assert.assertEquals(-1L, l2)
    l2 = 3L
    Assert.assertEquals(3L, l2)
  }

  @Test
  fun boolDefaultFalse_putAndGet() {
    Assert.assertEquals(false, b1)
    b1 = true
    Assert.assertEquals(true, b1)
  }

  @Test
  fun boolDefaultTrue_putAndGet() {
    Assert.assertEquals(true, b2)
    b2 = true
    Assert.assertEquals(true, b2)
  }

  @Test
  fun floatDefaultZero_putAndGet() {
    Assert.assertEquals(0f, f1)
    f1 = 0.5f
    Assert.assertEquals(0.5f, f1)
  }

  @Test
  fun floatDefaultMinusOne_putAndGet() {
    Assert.assertEquals(-1f, f2)
    f2 = 0.5f
    Assert.assertEquals(0.5f, f2)
  }

  @Test
  fun doubleDefaultZero_putAndGet() {
    Assert.assertEquals(0.0, d1, 0.0)
    d1 = 0.5
    Assert.assertEquals(0.5, d1, 0.0)
  }

  @Test
  fun doubleDefaultMinusOne_putAndGet() {
    Assert.assertEquals(-1.0, d2, 0.0)
    d2 = 0.5
    Assert.assertEquals(0.5, d2, 0.0)
  }

  @Test
  fun stringDefaultNull_putAndGet() {
    Assert.assertEquals(null, s1)
    s1 = "test"
    Assert.assertEquals("test", s1)
  }

  @Test
  fun stringDefaultEmpty_putAndGet() {
    Assert.assertEquals("", s2)
    s2 = "test"
    Assert.assertEquals("test", s2)
  }

  @Test
  fun stringSetDefaultNull_putAndGet() {
    Assert.assertEquals(null, set1)
    val set = setOf("22", "33")
    set1 = set
    Assert.assertEquals(set, set1)
  }

  @Test
  fun stringSetDefaultEmpty_putAndGet() {
    Assert.assertEquals(emptySet<String>(), set2)
    val set = setOf("22", "33")
    set2 = set
    Assert.assertEquals(set, set2)
  }

  @Test
  fun bytesDefaultNull_putAndGet() {
    Assert.assertEquals(null, bytes1)
    val bytes = byteArrayOf(0x1A, 0x2B)
    bytes1 = bytes
    Assert.assertTrue(bytes.contentEquals(bytes1))
  }

  @Test
  fun bytesDefaultValue_putAndGet() {
    Assert.assertTrue(byteArrayOf(0x1A).contentEquals(bytes2))
    val bytes = byteArrayOf(0x1A, 0x2B)
    bytes2 = bytes
    Assert.assertTrue(bytes.contentEquals(bytes2))
  }

  @Test
  fun parcelableDefaultNull_putAndGet() {
    Assert.assertEquals(null, user1)
    user1 = User(1, "DylanCai")
    Assert.assertEquals(User(1, "DylanCai"), user1)
  }

  @Test
  fun parcelableDefaultValue_putAndGet() {
    Assert.assertEquals(User(0, "Admin"), user2)
    user2 = User(1, "DylanCai")
    Assert.assertEquals(User(1, "DylanCai"), user2)
  }

  @Test
  fun liveDataDefaultZero_putAndGet() {
    UiThreadStatement.runOnUiThread {
      Assert.assertEquals(0, liveData1.value)
      liveData1.value = 6
      Assert.assertEquals(6, liveData1.value)
      clearAllKV()
      Assert.assertEquals(0, liveData1.value)
    }
  }

  @Test
  fun liveDataDefaultMinusOne_putAndGet() {
    UiThreadStatement.runOnUiThread {
      Assert.assertEquals(-1, liveData2.value)
      liveData2.value = 6
      Assert.assertEquals(6, liveData2.value)
      clearAllKV()
      Assert.assertEquals(-1, liveData2.value)
    }
  }

  @Test
  fun stateFlowDefaultZero_putAndGet() {
    Assert.assertEquals(0, flow1.value)
    flow1.value = 6
    Assert.assertEquals(6, flow1.value)
    clearAllKV()
    Assert.assertEquals(0, flow1.value)
  }

  @Test
  fun stateFlowDefaultMinusOne_putAndGet() {
    Assert.assertEquals(-1, flow2.value)
    flow2.value = 6
    Assert.assertEquals(6, flow2.value)
    clearAllKV()
    Assert.assertEquals(-1, flow2.value)
  }

  @Test
  fun mapDefaultZero_putAndGet() {
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
  fun mapDefaultMinusOne_putAndGet() {
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
  fun list_putAndGet() {
    Assert.assertTrue(list.isEmpty())
    list.add(1)
    list.add(2)
    Assert.assertEquals(listOf(1, 2), list.toList())
    list.removeAt(0)
    Assert.assertEquals(listOf(2), list.toList())
    clearAllKV()
    Assert.assertTrue(list.isEmpty())
  }

  @Test
  fun list_indexOperations_keepOrder() {
    list.addAll(listOf(1, 2, 3))
    list.add(1, 9)
    Assert.assertEquals(listOf(1, 9, 2, 3), list.toList())
    list[2] = 7
    Assert.assertEquals(listOf(1, 9, 7, 3), list.toList())
    list.removeAt(0)
    Assert.assertEquals(listOf(9, 7, 3), list.toList())
  }

  @Test
  fun list_duplicatesAndSearch_work() {
    list.addAll(listOf(1, 2, 2, 3))
    Assert.assertTrue(list.contains(2))
    Assert.assertEquals(1, list.indexOf(2))
    Assert.assertEquals(2, list.lastIndexOf(2))
    list.remove(2)
    Assert.assertEquals(listOf(1, 2, 3), list.toList())
  }

  @Test
  fun list_snapshot_isIndependent() {
    list.addAll(listOf(1, 2, 3))
    val snapshot = list.toList()
    list.add(4)
    Assert.assertEquals(listOf(1, 2, 3), snapshot)
    Assert.assertEquals(listOf(1, 2, 3, 4), list.toList())
  }

  @Test
  fun list_removeAtEmpty_throws() {
    Assert.assertTrue(list.isEmpty())
    Assert.assertThrows(IndexOutOfBoundsException::class.java) {
      list.removeAt(0)
    }
  }

  @Test
  fun mapEntryIteratorRemovals_work() {
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
  fun removeSingleKey() {
    i1 = 6
    Assert.assertTrue(kv.containsKey(::i1.name))
    kv.removeValueForKey(::i1.name)
    Assert.assertFalse(kv.containsKey(::i1.name))
  }

  @Test
  fun removeMultipleKeys() {
    s1 = "1"
    s2 = "2"
    Assert.assertTrue(kv.containsKey(::s1.name))
    Assert.assertTrue(kv.containsKey(::s2.name))
    kv.removeValuesForKeys(arrayOf(::s1.name, ::s2.name))
    Assert.assertFalse(kv.containsKey(::s1.name))
    Assert.assertFalse(kv.containsKey(::s2.name))
  }

  @Test
  fun getAllKV_includesDefaultsAndCollections() {
    clearAllKV()
    map1["id1"] = 1
    map1["id2"] = 2
    list.add(3)
    nested1 = "111"
    nested2 = "222"

    val allKV = getAllKV()
    Assert.assertEquals(27, allKV.size)
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
    Assert.assertEquals(listOf(3),allKV["list"])

    val nested1 = allKV["nested1"] as Map<*, *>
    Assert.assertEquals("111", nested1["aaa"])

    val nested2 = allKV["nested2"] as Map<*, *>
    val aaa = nested2["aaa"] as Map<*, *>
    Assert.assertEquals("222", aaa["bbb"])
  }

  @Test
  fun nameWithId_getOrPut() {
    nested1 = "111"
    Assert.assertEquals("111", kv.decodeString("nested1$\$aaa"))
    Assert.assertEquals(setOf("aaa"), kv.decodeStringSet("nested1\$key"))

    nested2 = "222"
    Assert.assertEquals("222", kv.decodeString("nested2$\$aaa$\$bbb"))
    Assert.assertEquals(setOf("aaa"), kv.decodeStringSet("nested2\$key"))
    Assert.assertEquals(setOf("bbb"), kv.decodeStringSet("nested2$\$aaa\$key"))
  }

  @Parcelize
  data class User(val id: Long, val name: String) : Parcelable
}
