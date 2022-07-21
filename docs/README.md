
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
    implementation 'com.github.DylanCaiCoding:MMKV-KTX:1.2.13'
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

在 `MMKVOwner` 的实现类可以获取 `kv` 对象进行删除值或清理缓存等操作：

```kotlin
kv.removeValueForKey(::isFirstLaunch.name)
kv.clearAll()
```

## 进阶用法

### 区别存储

比如我们在组件化项目进行开发，各自负责的模块是不知道别人用了什么 key 值，重名了可能被覆盖。这就可以重写 `kv` 属性创建不同的 `MMKV` 实例来规避这个问题。

```kotlin
object UserRepository : MMKVOwner {
  // ...
  
  override val kv = MMKV.mmkvWithID("user")
}
```

### 加密

MMKV 默认明文存储所有 key-value，依赖 Android 系统的沙盒机制保证文件加密。如果你担心信息泄露，你可以选择加密 MMKV。

```kotlin
object DataRepository : MMKVOwner {
  // ...
  
  private const val cryptKey = "My-Encrypt-Key"
  
  override val kv = MMKV.mmkvWithID("MyID", MMKV.SINGLE_PROCESS_MODE, cryptKey)
}
```

### 取消自动初始化 MMKV

比如需要自定义 MMKV 文件的更目录，或者有个小伙伴反馈在老项目使用本库时存在数据保存不成功的情况，后来发现是多次初始化 MMKV 导致的，此时可以取消本库自动初始化 MMKV。

需要添加 App Startup 的依赖：

```groovy
implementation "androidx.startup:startup-runtime:1.1.0"
```

然后在 `AndroidManifest.xml` 添加以下代码就能取消自动初始化：

```xml
<provider
  android:name="androidx.startup.InitializationProvider"
  android:authorities="${applicationId}.androidx-startup"
  android:exported="false"
  tools:node="merge">
  <meta-data
    android:name="com.dylanc.mmkv.MMKVInitializer"
    tools:node="remove" />
</provider>
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
