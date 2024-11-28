# MMKV-KTX

English | [中文](README_ZH.md)

[![](https://www.jitpack.io/v/DylanCaiCoding/MMKV-KTX.svg)](https://www.jitpack.io/#DylanCaiCoding/MMKV-KTX)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/MMKV-KTX/blob/master/LICENSE)

Combined with the features of Kotlin property delegation, it makes [MMKV](https://github.com/Tencent/MMKV) more flexible and easy to use.

## Features

- Automatic initialization of MMKV;
- Use the property name as the key name, eliminating the need to declare a large number of key name constants;
- Can ensure type safety and avoid exceptions caused by inconsistent types or key values;

## Usage

:pencil: **[>> Usage Document <<](https://dylancaicoding.github.io/MMKV-KTX)**

## Get started

Add the following to the `build.gradle` file in the root directory:

```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://www.jitpack.io' }
    }
}
```

Add the dependency in the module's `build.gradle` file:

```groovy
dependencies {
    implementation 'com.github.DylanCaiCoding:MMKV-KTX:2.0.1'
}
```

By having a class inherit from the `MMKVOwner` class, you can use the `by mmkvXXXX()` function to delegate properties to `MMKV`. For example:

```kotlin
object SettingsRepository : MMKVOwner(mmapID = "settings") {
  var isNightMode by mmkvBool()
  var language by mmkvString(default = "zh")
}
```

If you already have a parent class that cannot be inherited from, implement `IMMKVOwner by MMKVOwner(mmapID)`, such as:

```kotlin
object SettingsRepository : BaseRepository(), IMMKVOwner by MMKVOwner(mmapID = "settings") {
  // ...
}
```

Make sure that each `mmapID` is unique to ensure type safety 100%!!!

Setting or getting the value of a property will call the corresponding `encode()` or `decode()` function with the property name as the key name. For example:

```kotlin
if (SettingsRepository.isNightMode) {
  // do some thing
}

SettingsRepository.isNightMode = true
```

Support the following types:

| Type         | Function           | Default value |
| ------------ | ------------------ | ------------- |
| `Int`        | `mmkvInt()`        | 0             |
| `Long`       | `mmkvLong()`       | 0L            |
| `Boolean`    | `mmkvBool()`       | false         |
| `Float`      | `mmkvFloat()`      | 0f            |
| `Double`     | `mmkvDouble()`     | 0.0           |
| `String`     | `mmkvString()`     | /             |
| `StringSet`  | `mmkvStringSet()`  | /             |
| `ByteArray`  | `mmkvBytes()`      | /             |
| `Parcelable` | `mmkvParcelable()` | /             |

| Type                          | Function                        | Default value |
| ----------------------------- | ------------------------------- | ------------- |
| `MutableLiveData<Int>`        | `mmkvInt().asLiveData()`        | 0             |
| `MutableLiveData<Long>`       | `mmkvLong().asLiveData()`       | 0L            |
| `MutableLiveData<Boolean>`    | `mmkvBool().asLiveData()`       | false         |
| `MutableLiveData<Float>`      | `mmkvFloat().asLiveData()`      | 0f            |
| `MutableLiveData<Double>`     | `mmkvDouble.asLiveData()        | 0.0           |
| `MutableLiveData<String>`     | `mmkvString().asLiveData()`     | /             |
| `MutableLiveData<StringSet>`  | `mmkvStringSet().asLiveData()`  | /             |
| `MutableLiveData<ByteArray>`  | `mmkvBytes().asLiveData()`      | /             |
| `MutableLiveData<Parcelable>` | `mmkvParcelable().asLiveData()` | /             |

| Type                           | Function                         | Default value |
| ------------------------------ | -------------------------------- | ------------- |
| `MutableStateFlow<Int>`        | `mmkvInt().asStateFlow()`        | 0             |
| `MutableStateFlow<Long>`       | `mmkvLong().asStateFlow()`       | 0L            |
| `MutableStateFlow<Boolean>`    | `mmkvBool().asStateFlow()`       | false         |
| `MutableStateFlow<Float>`      | `mmkvFloat().asStateFlow()`      | 0f            |
| `MutableStateFlow<Double>`     | `mmkvDouble().asStateFlow()`     | 0.0           |
| `MutableStateFlow<String>`     | `mmkvString().asStateFlow()`     | /             |
| `MutableStateFlow<StringSet>`  | `mmkvStringSet().asStateFlow()`  | /             |
| `MutableStateFlow<ByteArray>`  | `mmkvBytes().asStateFlow()`      | /             |
| `MutableStateFlow<Parcelable>` | `mmkvParcelable().asStateFlow()` | /             |

| Type                             | Function                   | Default value |
| -------------------------------- | -------------------------- | ------------- |
| `MutableMap<String, Int>`        | `mmkvInt().asMap()`        | 0             |
| `MutableMap<String, Long>`       | `mmkvLong().asMap()`       | 0L            |
| `MutableMap<String, Boolean>`    | `mmkvBool().asMap()`       | false         |
| `MutableMap<String, Float>`      | `mmkvFloat().asMap()`      | 0f            |
| `MutableMap<String, Double>`     | `mmkvDouble().asMap()`     | 0.0           |
| `MutableMap<String, String>`     | `mmkvString().asMap()`     | /             |
| `MutableMap<String, StringSet>`  | `mmkvStringSet().asMap()`  | /             |
| `MutableMap<String, ByteArray>`  | `mmkvBytes().asMap()`      | /             |
| `MutableMap<String, Parcelable>` | `mmkvParcelable().asMap()` | /             |



Support using the `mmkvXXXX().asLiveData()` function to delegate the property to `LiveData`, such as:

```kotlin
object SettingRepository : MMKVOwner(mmapID = "settings") {
  val isNightMode by mmkvBool().asLiveData()
}

SettingRepository.isNightMode.observe(this) {
  checkBox.isChecked = it
}

SettingRepository.isNightMode.value = true
```

The `kv` object can be used to delete values or clear the cache, for example:

```kotlin
kv.removeValueForKey(::language.name) // Recommend removing the key after the default value is modified to shorten the assignment operation
kv.clearAll()
```

For more advanced usage, please refer to the [Usage Document](https://dylancaicoding.github.io/MMKV-KTX).

## Update log

[Releases](https://github.com/DylanCaiCoding/MMKV-KTX/releases)

## Other libraries created by the author

| Library | Brief Introduction |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [Longan](https://github.com/DylanCaiCoding/Longan) | Perhaps the most user-friendly Kotlin tool library |
| [LoadingStateView](https://github.com/DylanCaiCoding/LoadingStateView) | Deep decoupling of the default page of the title bar or loading, loading failure, no data, etc. |
| [ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX) | Most comprehensive ViewBinding tool |
| [Tracker](https://github.com/DylanCaiCoding/Tracker) | Lightweight burrowing framework based on the chain of responsibility burrowing idea of Buzzvideo |

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
