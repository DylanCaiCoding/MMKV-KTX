# MMKV-KTX

[English](README.md) | 中文

[![](https://www.jitpack.io/v/DylanCaiCoding/MMKV-KTX.svg)](https://www.jitpack.io/#DylanCaiCoding/MMKV-KTX) 
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/LoadingStateView/blob/master/LICENSE)

结合了 Kotlin 属性委托的特性，使得 [MMKV](https://github.com/Tencent/MMKV) 更加易用，无需初始化 MMKV，无需传 key 值。

## 用法

:pencil: **[>> 使用文档 <<](https://dylancaicoding.github.io/MMKV-KTX)**

## 快速入门

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

让一个类实现 `MMKVOwner` 接口，即可在该类使用 `by mmkvXXXX()` 函数将属性委托给 `MMKV`，例如：

```kotlin
object DataRepository : MMKVOwner {
  var isFirstLaunch by mmkvBool(default = true)
  var user by mmkvParcelable<User>()
}
```

设置或获取属性的值会调用对应的 encode() 或 decode() 函数，**用属性名作为 key 值**。

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

更多进阶用法请查看[使用文档](https://dylancaicoding.github.io/MMKV-KTX)。

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
