# MMKV-KTX

[English](README.md) | 中文

[![](https://www.jitpack.io/v/DylanCaiCoding/MMKV-KTX.svg)](https://www.jitpack.io/#DylanCaiCoding/MMKV-KTX)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/MMKV-KTX/blob/master/LICENSE)

结合了 Kotlin 属性委托的特性，使得 [MMKV](https://github.com/Tencent/MMKV) 更加灵活易用。

## Features

-   自动初始化 MMKV ；
-   用属性名作为键名，无需声明大量的键名常量；
-   可以确保类型安全，避免类型或者键名不一致导致的异常；
-   支持转换成 `LiveData` 和 `StateFlow` 来使用；
-   支持转换成 `Map`，可以根据不同的 `id` 来保存数据；
-   支持 `getAllKV()`，为数据迁移提供了可能性；

## 用法

:pencil: **[>> 使用文档 <<](https://dylancaicoding.github.io/MMKV-KTX)**

## 快速入门


在 `settings.gradle` 文件的 `repositories` 结尾处添加：

```groovy
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://www.jitpack.io' }
  }
}
```

或者在 `settings.gradle.ktx` 文件的 `repositories` 结尾处添加：

```kotlin
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}
```

添加依赖：

```kotlin
dependencies {
  implementation("com.github.DylanCaiCoding:MMKV-KTX:2.0.1")
}
```

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

**要确保每个 `mmapID` 不重复，只有这样才能 100% 确保类型安全！！！**

设置或获取属性的值会调用对应的 encode() 或 decode() 函数，用属性名作为键名。

支持以下类型：

| 类型         | 函数           | 默认值 |
| ------------ | ------------------ | ------------- |
| `Int`        | `mmkvInt()`        | 0             |
| `Long`       | `mmkvLong()`       | 0L            |
| `Boolean`    | `mmkvBool()`       | false         |
| `Float`      | `mmkvFloat()`      | 0f            |
| `Double`     | `mmkvDouble()`     | 0.0           |
| `String`     | `mmkvString()`     | /             |
| `Set<String>`| `mmkvStringSet()`  | /             |
| `ByteArray`  | `mmkvBytes()`      | /             |
| `Parcelable` | `mmkvParcelable()` | /             |

| 类型         | 函数           | 默认值 |
| ----------------------------- | ------------------------------- | ------------- |
| `MutableLiveData<Int>`        | `mmkvInt().asLiveData()`        | 0             |
| `MutableLiveData<Long>`       | `mmkvLong().asLiveData()`       | 0L            |
| `MutableLiveData<Boolean>`    | `mmkvBool().asLiveData()`       | false         |
| `MutableLiveData<Float>`      | `mmkvFloat().asLiveData()`      | 0f            |
| `MutableLiveData<Double>`     | `mmkvDouble.asLiveData()`        | 0.0           |
| `MutableLiveData<String>`     | `mmkvString().asLiveData()`     | /             |
| `MutableLiveData<Set<String>>`| `mmkvStringSet().asLiveData()`  | /             |
| `MutableLiveData<ByteArray>`  | `mmkvBytes().asLiveData()`      | /             |
| `MutableLiveData<Parcelable>` | `mmkvParcelable().asLiveData()` | /             |

| 类型         | 函数           | 默认值 |
| ------------------------------ | -------------------------------- | ------------- |
| `MutableStateFlow<Int>`        | `mmkvInt().asStateFlow()`        | 0             |
| `MutableStateFlow<Long>`       | `mmkvLong().asStateFlow()`       | 0L            |
| `MutableStateFlow<Boolean>`    | `mmkvBool().asStateFlow()`       | false         |
| `MutableStateFlow<Float>`      | `mmkvFloat().asStateFlow()`      | 0f            |
| `MutableStateFlow<Double>`     | `mmkvDouble().asStateFlow()`     | 0.0           |
| `MutableStateFlow<String>`     | `mmkvString().asStateFlow()`     | /             |
| `MutableStateFlow<Set<String>>`| `mmkvStringSet().asStateFlow()`  | /             |
| `MutableStateFlow<ByteArray>`  | `mmkvBytes().asStateFlow()`      | /             |
| `MutableStateFlow<Parcelable>` | `mmkvParcelable().asStateFlow()` | /             |

| 类型         | 函数           | 默认值 |
| -------------------------------- | -------------------------- | ------------- |
| `MutableMap<String, Int>`        | `mmkvInt().asMap()`        | 0             |
| `MutableMap<String, Long>`       | `mmkvLong().asMap()`       | 0L            |
| `MutableMap<String, Boolean>`    | `mmkvBool().asMap()`       | false         |
| `MutableMap<String, Float>`      | `mmkvFloat().asMap()`      | 0f            |
| `MutableMap<String, Double>`     | `mmkvDouble().asMap()`     | 0.0           |
| `MutableMap<String, String>`     | `mmkvString().asMap()`     | /             |
| `MutableMap<String, Set<String>>`| `mmkvStringSet().asMap()`  | /             |
| `MutableMap<String, ByteArray>`  | `mmkvBytes().asMap()`      | /             |
| `MutableMap<String, Parcelable>` | `mmkvParcelable().asMap()` | /             |

更多进阶用法请查看[使用文档](https://dylancaicoding.github.io/MMKV-KTX)。

## 更新日志

[Releases](https://github.com/DylanCaiCoding/MMKV-KTX/releases)

## 作者其它的库

| 库                                                           | 简介                                           |
| ------------------------------------------------------------ | ---------------------------------------------- |
| [Longan](https://github.com/DylanCaiCoding/Longan)           | 可能是最好用的 Kotlin 工具库                       |
| [LoadingStateView](https://github.com/DylanCaiCoding/LoadingStateView) | 深度解耦标题栏或加载中、加载失败、无数据等缺省页 |
| [ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX) | 最全面的 ViewBinding 工具                   |
| [MultiBaseUrls](https://github.com/DylanCaiCoding/MultiBaseUrls) | 用注解让 Retrofit 同时支持多个 baseUrl 以及动态改变 baseUrl |
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
