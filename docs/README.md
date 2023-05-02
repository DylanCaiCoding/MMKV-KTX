
[![](https://www.jitpack.io/v/DylanCaiCoding/MMKV-KTX.svg)](https://www.jitpack.io/#DylanCaiCoding/MMKV-KTX)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/LoadingStateView/blob/master/LICENSE)
[![GitHub Repo stars](https://img.shields.io/github/stars/DylanCaiCoding/MMKV-KTX?style=social)](https://github.com/DylanCaiCoding/MMKV-KTX)

结合了 Kotlin 属性委托的特性，使得 [MMKV](https://github.com/Tencent/MMKV) 更加易用，无需初始化 MMKV，无需传 key 值。

## Gradle

在根目录的 `build.gradle` 添加:

```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://www.jitpack.io' }
    }
}
```

在模块的 `build.gradle` 添加依赖：

```groovy
dependencies {
    implementation 'com.github.DylanCaiCoding:MMKV-KTX:1.2.16'
}
```

## 基础用法

让一个类继承 `MMKVOwner` 类，即可在该类使用 `by mmkvXXXX()` 函数将属性委托给 `MMKV`，例如：

```kotlin
object SettingsRepository : MMKVOwner(mmapID = "settings") {
  var isNightMode by mmkvBool()
  var language by mmkvString(default = "zh")
}
```

如果已经有了父类继承不了，那就实现 `IMMKVOwner by MMKVOwner(mmapID)`，比如：

```kotlin
object SettingsRepository : BaseRepository(), IMMKVOwner by MMKVOwner(mmapID = "settings") {
  // ...
}
```

**不管哪种都要确保每个 `mmapID` 不重复，只有这样才能 100% 确保类型安全！！！**

设置或获取属性的值会调用对应的 encode() 或 decode() 函数，用属性名作为 key 值。比如：

```kotlin
if (SettingsRepository.isNightMode) {
  // do some thing
}

SettingsRepository.isNightMode = true
```

支持以下类型：

| 函数               | 默认值 |
| ------------------ | ------ |
| `mmkvInt()`        | 0      |
| `mmkvLong()`       | 0L     |
| `mmkvBool()`       | false  |
| `mmkvFloat()`      | 0f     |
| `mmkvDouble()`     | 0.0    |
| `mmkvString()`     | /      |
| `mmkvStringSet()`  | /      |
| `mmkvBytes()`      | /      |
| `mmkvParcelable()` | /      |

支持用 `mmkvXXXX().asLiveData()` 函数将属性委托给 `LiveData`，例如：

```kotlin
object SettingRepository : MMKVOwner(mmapID = "settings") {
  val isNightMode by mmkvBool().asLiveData()
}

SettingRepository.isNightMode.observe(this) {
  checkBox.isChecked = it
}

SettingRepository.isNightMode.value = true
```

可以用 `kv` 对象进行删除值或清理缓存等操作，例如：

```kotlin
kv.removeValueForKey(::language.name) // 建议修改了默认值才移除 key，否则赋值操作更简洁
kv.clearAll()
```

## 进阶用法

### 取消自动初始化

本库会自动调用 `MMKV.initialize(context)` 进行初始化，如果在用了 MMKV 的项目中使用本库，建议把自动初始化给取消了，多次初始化可能会导致数据异常。

需要添加 App Startup 的依赖：

```groovy
implementation "androidx.startup:startup-runtime:1.1.0"
```

然后在 `AndroidManifest.xml` 添加以下代码就能取消自动初始化操作：

```xml
<application>
  <provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
      android:name="com.dylanc.mmkv.MMKVInitializer"
      tools:node="remove" />
  </provider>
</application>
```

### 重写 kv 对象

有一些场景需要使用新的 MMKV 对象，此时可以重写 `kv` 属性。

#### 多进程

MMKV 默认是单进程模式，如果你需要多进程支持：

```kotlin
object SettingsRepository : MMKVOwner(mmapID = "settings") {
  // ...

  override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.MULTI_PROCESS_MODE)
}
```

或者：

```kotlin
object SettingsRepository : BaseRepository(), IMMKVOwner by MMKVOwner(mmapID = "settings") {
  // ...

  override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.MULTI_PROCESS_MODE)
}
```

#### 加密

MMKV 默认明文存储所有 key-value，依赖 Android 系统的沙盒机制保证文件加密。如果你担心信息泄露，你可以选择加密 MMKV。

```kotlin
object SettingsRepository : MMKVOwner(mmapID = "settings") {
  // ...

  private const val CRYPT_KEY = "My-Encrypt-Key"

  override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.SINGLE_PROCESS_MODE, CRYPT_KEY)
}
```

或者：

```kotlin
object SettingsRepository : BaseRepository(), IMMKVOwner by MMKVOwner(mmapID = "settings") {
  // ...

  private const val CRYPT_KEY = "My-Encrypt-Key"

  override val kv: MMKV = MMKV.mmkvWithID(mmapID, MMKV.SINGLE_PROCESS_MODE, CRYPT_KEY)
}
```

## 更新日志

[Releases](https://github.com/DylanCaiCoding/MMKV-KTX/releases)

## 作者其它的库

| 库                                                           | 简介                                       |
| ------------------------------------------------------------ |------------------------------------------|
| [Longan](https://github.com/DylanCaiCoding/Longan)           | 可能是最好用的 Kotlin 工具库                       |
| [LoadingStateView](https://github.com/DylanCaiCoding/LoadingStateView) | 深度解耦标题栏或加载中、加载失败、无数据等视图，支持两行代码集成到基类      |
| [ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX) | 最全面的 ViewBinding 工具，可用各种姿势使用 ViewBinding |
| [Tracker](https://github.com/DylanCaiCoding/Tracker)         | 基于西瓜视频的责任链埋点思路实现的轻量级埋点框架                 |

## License

```
Copyright (C) 2021. Dylan Cai

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
