# MMKV-KTX

English | [中文](README_ZH.md)

[![](https://www.jitpack.io/v/DylanCaiCoding/MMKV-KTX.svg)](https://www.jitpack.io/#DylanCaiCoding/MMKV-KTX) 
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/LoadingStateView/blob/master/LICENSE)

It's easier to use the [MMKV](https://github.com/Tencent/MMKV) without initializing the MMKV and defining the Key value.

## Usage

:pencil: **[>> Usage documentation <<](https://dylancaicoding.github.io/MMKV-KTX)**

## Quick start

Add it in your root `build.gradle` at the end of repositories:

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://www.jitpack.io' }
    }
}
```

Add dependencies in your module `build.gradle` :

```groovy
dependencies {
    implementation 'com.github.DylanCaiCoding:MMKV-KTX:1.2.13'
}
```

Create a class to implement the `MMKVOwner` interface and delegate properties to `MMKV` with the `by mmkvXXXX()` method, for example:

```kotlin
object DataRepository : MMKVOwner {
  var isFirstLaunch by mmkvBool(default = true)
  var user by mmkvParcelable<User>()
}
```

Setting or getting the value of the property calls the corresponding encode or decode method, and the key value is the property name.

The following types are supported：

| Method             | Default value |
| ------------------ | ------------- |
| `mmkvInt()`        | 0             |
| `mmkvLong()`       | 0L            |
| `mmkvBool()`       | false         |
| `mmkvFloat()`      | 0f            |
| `mmkvDouble()`     | 0.0           |
| `mmkvString()`     | /             |
| `mmkvStringSet()`  | /             |
| `mmkvBytes()`      | /             |
| `mmkvParcelable()` | /             |

You can get the `kv` object in the implementation class of `MMKVOwner` to delete values or clear all, for example:

```kotlin
kv.removeValueForKey(::user.name)
kv.clearAll()
```

See the [usage documentation](https://dylancaicoding.github.io/MMKV-KTX) for more advanced usage.

## Change log

[Releases](https://github.com/DylanCaiCoding/MMKV-KTX/releases)

## Author's other libraries

| Library                                                      | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [Longan](https://github.com/DylanCaiCoding/Longan)           | Probably the best Kotlin utils library for Android.         |
| [LoadingStateView](https://github.com/DylanCaiCoding/LoadingStateView) | Decoupling the code of toolbar or loading status view.       |
| [ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX) | The most comprehensive utils of ViewBinding.                 |
| [Tracker](https://github.com/DylanCaiCoding/Tracker)       | A lightweight tracking framework based on the tracking idea of Buzzvideo.|

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
