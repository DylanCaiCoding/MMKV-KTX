
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
    implementation 'com.github.DylanCaiCoding:MMKV-KTX:1.2.15'
}
```

## 基础用法

让一个类实现 `MMKVOwner` 接口，即可通过 `by mmkvXXXX()` 方法将属性委托给 `MMKV`，例如：

```kotlin
object DataRepository : MMKVOwner {
  var isFirstLaunch by mmkvBool(default = true)
  var user by mmkvParcelable<User>()
}
```

设置或获取属性的值会调用对应的 encode() 或 decode() 函数，**用属性名作为 key 值**。

支持以下类型：

| 方法               | 默认值 |
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

1.2.15 版本新增 `mmkvXXXX().asLiveData()` 函数将属性委托给 `LiveData`，例如：

```kotlin
object SettingRepository : MMKVOwner {
  val nightMode by mmkvBool().asLiveData()
}

SettingRepository.nightMode.observe(this) {
  checkBox.isChecked = it
}

SettingRepository.nightMode.value = true
```

在 `MMKVOwner` 的实现类可以获取 `kv` 对象进行删除值或清理缓存等操作：

```kotlin
kv.removeValueForKey(::isFirstLaunch.name)
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

#### 区别存储

比如我们在组件化项目进行开发，各自负责的模块是不知道别人用了什么 key 值，重名了可能被覆盖。这就可以重写 `kv` 属性创建不同的 `MMKV` 实例来规避这个问题。

```kotlin
object UserRepository : MMKVOwner {
  // ...
  
  override val kv: MMKV = MMKV.mmkvWithID("user")
}
```

#### 多进程

MMKV 默认是单进程模式，如果你需要多进程支持：

```kotlin
object DataRepository : MMKVOwner {
  // ...

  override val kv: MMKV = MMKV.mmkvWithID("InterProcessKV", MMKV.MULTI_PROCESS_MODE)
}
```

#### 加密

MMKV 默认明文存储所有 key-value，依赖 Android 系统的沙盒机制保证文件加密。如果你担心信息泄露，你可以选择加密 MMKV。

```kotlin
object DataRepository : MMKVOwner {
  // ...
  
  private const val cryptKey = "My-Encrypt-Key"
  
  override val kv: MMKV = MMKV.mmkvWithID("MyID", MMKV.SINGLE_PROCESS_MODE, cryptKey)
}
```

## 更新日志

[Releases](https://github.com/DylanCaiCoding/MMKV-KTX/releases)

## 作者其它的库

| 库                                                           | 简介                                           |
| ------------------------------------------------------------ | ---------------------------------------------- |
| [Longan](https://github.com/DylanCaiCoding/Longan)           | 可能是最好用的 Kotlin 工具库                       |
| [LoadingStateView](https://github.com/DylanCaiCoding/LoadingStateView) | 深度解耦标题栏或加载中、加载失败、无数据等缺省页 |
| [ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX) | 最全面的 ViewBinding 工具                   |
| [Tracker](https://github.com/DylanCaiCoding/Tracker)         | 基于西瓜视频的责任链埋点思路实现的轻量级埋点框架         |

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
