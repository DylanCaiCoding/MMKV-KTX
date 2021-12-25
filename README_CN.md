# MMKV-KTX

[English](README.md) | 中文

[![](https://www.jitpack.io/v/DylanCaiCoding/MMKV-KTX.svg)](https://www.jitpack.io/#DylanCaiCoding/MMKV-KTX) [![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/LoadingStateView/blob/master/LICENSE)

使 [MMKV](https://github.com/Tencent/MMKV) 更加易用，无需初始化 MMKV，无需传 Key 值。


## 用法

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
    implementation 'com.github.DylanCaiCoding:MMKV-KTX:1.2.11'
}
```

让一个类实现 `MMKVOwner` 接口，即可通过 `by mmkvXXXX()` 方法将属性委托给 `MMKV`，例如：

```kotlin
object DataRepository : MMKVOwner {
  var isFirstLaunch by mmkvBool(default = true)
  var user by mmkvParcelable<User>()
}
```

设置或获取属性的值则调用对应的 encode 或 decode 方法，key 值为属性名。

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

如果不同业务需要**区别存储**，可以重写 `kv` 属性来创建不同的 `MMKV` 实例：

```kotlin
object DataRepository : MMKVOwner {
  override val kv: MMKV = MMKV.mmkvWithID("MyID")
}
```

完整的用法可查看[单元测试](https://github.com/DylanCaiCoding/MMKV-KTX/blob/master/library/src/androidTest/java/com/dylanc/mmkv/MMKVTest.kt)代码。

## 更新日志

[Releases](https://github.com/DylanCaiCoding/MMKV-KTX/releases)

## 作者其它的库

| 库                                                           | 简介                                           |
| ------------------------------------------------------------ | ---------------------------------------------- |
| [Longan](https://github.com/DylanCaiCoding/Longan)           | 简化 Android 开发的 Kotlin 工具类集合      |
| [LoadingStateView](https://github.com/DylanCaiCoding/LoadingStateView) | 深度解耦标题栏或加载中、加载失败、无数据等缺省页 |
| [ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX) | 最全面的 ViewBinding 工具                      |

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
