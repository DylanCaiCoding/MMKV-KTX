# MMKV-KTX

English | [中文](README_ZH.md)

[![](https://www.jitpack.io/v/DylanCaiCoding/MMKV-KTX.svg)](https://www.jitpack.io/#DylanCaiCoding/MMKV-KTX)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/MMKV-KTX/blob/master/LICENSE)

Combined with the features of Kotlin property delegation makes [MMKV](https://github.com/Tencent/MMKV) more flexible and easy to use.

## Features

- Automatically initializes MMKV;
- Uses property names as keys, eliminating the need to declare numerous key constants;
- Ensures type safety, avoiding exceptions caused by type or key mismatches;
- Supports conversion to `LiveData` and `StateFlow` for usage;
- Supports conversion to Map, allowing data to be saved based on different ids;
- Supports `getAllKV()`, providing the possibility for data migration.

## Usage

:pencil: **[>> Usage Document <<](https://dylancaicoding.github.io/MMKV-KTX)**

## Get started

Add the following to the end of the repositories section in `settings.gradle`:

```groovy
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://www.jitpack.io' }
  }
}
```

Or add the following to the end of the repositories section in `settings.gradle.ktx`:

```kotlin
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.DylanCaiCoding:MMKV-KTX:2.0.1")
}
```

By having a class inherit from the `MMKVOwner` class, you can use the `by mmkvXXXX()` function to delegate properties to `MMKV`. For example:

```kotlin
object SettingsRepository : MMKVOwner(mmapID = "settings") {
  var isNightMode by mmkvBool()
  var language by mmkvString(default = "zh")
}
```

If there is already a parent class that cannot be inherited, implement `IMMKVOwner by MMKVOwner(mmapID)`, like this:

```kotlin
object SettingsRepository : BaseRepository(), IMMKVOwner by MMKVOwner(mmapID = "settings") {
  // ...
}
```

**Make sure that each mmapID is unique to 100% ensure type safety!!!**

Setting or getting the property values will call the corresponding `encode()` or `decode()` functions, with the property name used as the key.

Support the following types:

| Type         | Function           | Default value |
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

| Type                          | Function                        | Default value |
| ----------------------------- | ------------------------------- | ------------- |
| `MutableLiveData<Int>`        | `mmkvInt().asLiveData()`        | 0             |
| `MutableLiveData<Long>`       | `mmkvLong().asLiveData()`       | 0L            |
| `MutableLiveData<Boolean>`    | `mmkvBool().asLiveData()`       | false         |
| `MutableLiveData<Float>`      | `mmkvFloat().asLiveData()`      | 0f            |
| `MutableLiveData<Double>`     | `mmkvDouble.asLiveData()`       | 0.0           |
| `MutableLiveData<String>`     | `mmkvString().asLiveData()`     | /             |
| `MutableLiveData<Set<String>>`| `mmkvStringSet().asLiveData()`  | /             |
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
| `MutableStateFlow<Set<String>>`| `mmkvStringSet().asStateFlow()`  | /             |
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
| `MutableMap<String, Set<String>>`| `mmkvStringSet().asMap()`  | /             |
| `MutableMap<String, ByteArray>`  | `mmkvBytes().asMap()`      | /             |
| `MutableMap<String, Parcelable>` | `mmkvParcelable().asMap()` | /             |

For more advanced usage, please refer to the [Usage Document](https://dylancaicoding.github.io/MMKV-KTX).

## Update log

[Releases](https://github.com/DylanCaiCoding/MMKV-KTX/releases)

## Other libraries created by the author

| Library | Brief Introduction |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [Longan](https://github.com/DylanCaiCoding/Longan) | Perhaps the most user-friendly Kotlin tool library |
| [LoadingStateView](https://github.com/DylanCaiCoding/LoadingStateView) | Deep decoupling of the default page of the title bar or loading, loading failure, no data, etc. |
| [ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX) | Most comprehensive ViewBinding tool |
| [MultiBaseUrls](https://github.com/DylanCaiCoding/MultiBaseUrls) | Use annotation to allow Retrofit to support multiple baseUrl and dynamically change baseUrl |
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
