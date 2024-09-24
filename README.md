<img src="https://user-images.githubusercontent.com/13775137/226222990-558b58ca-20c0-4a45-8285-bf037f79647f.png" align="right" width="150" height="150" />

# <img src=".idea/icon.svg" height="23"/>  NYTimes-KMP

[![Build](https://github.com/xxfast/NYTimes-KMP/actions/workflows/build.yml/badge.svg)](https://github.com/xxfast/NYTimes-KMP/actions/workflows/build.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-wearos](http://img.shields.io/badge/platform-wearos-8ECDA0.svg?style=flat)
![badge-desktop](http://img.shields.io/badge/platform-desktop-4D76CD.svg?style=flat)
![badge-desktop](http://img.shields.io/badge/platform-ios-EAEAEA.svg?style=flat)
![badge-browser-js](https://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat)
![badge-browser-wasm](https://img.shields.io/badge/platform-wasm-F8DB5D.svg?style=flat)

A KMP template of the New York Times App using Compose multiplatform. To build and run this application you will need [an API key from the New York Times](https://developer.nytimes.com/).

<img src="https://user-images.githubusercontent.com/13775137/235060514-3b7f8779-7f2b-4f48-8e09-ef89d0a06344.png" width="720">

## Libraries used
- 🧩 [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform); for shared UI
- 🌐 [Ktor](https://github.com/ktorio/ktor); for networking
- 📦 [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization); for content negotiation
- 🕰️ [Kotlinx Datetime](https://github.com/Kotlin/kotlinx-datetime); for datetime
- 🗃️ [KStore](https://github.com/xxfast/KStore); for storage
- 🚏 [Decompose](https://github.com/arkivanov/Decompose) + [Router](https://github.com/xxfast/Decompose-Router); for navigation
- 🧪 [Molecule](https://github.com/cashapp/molecule); for modeling state
- 🏞️ [Compose-imageloader](https://github.com/qdsfdhvh/compose-imageloader); for loading images

## Run instructions

Run configurations available on `.idea/runConfigurations` for each platform.

<img alt="run-config.png" src="artwork%2Frun-config.png" align="right" " width="250"/>

| platform | gradle command                                                                                                                      |
|----------|-------------------------------------------------------------------------------------------------------------------------------------|
| android  | `./gradlew :app:android:assembleDebug`                                                                                              |
| wear     | `./gradlew :app:wear:assembleDebug`                                                                                                 |
| ios      | `/Applications/Xcode.app/Contents/Developer/usr/bin/xcodebuild -project app/ios/ios.xcodeproj -scheme NYTimes -configuration Debug` |
| desktop  | `./gradlew :app:desktop:run`                                                                                                        |
| js       | `./gradlew :app:web:jsBrowserDevelopmentRun`                                                                                        |
| wasm     | `./gradlew :app:web:wasmJsBrowserDevelopmentRun`                                                                                    |

## Showcase

### Android

https://github.com/xxfast/NYTimes-KMP/assets/13775137/25adabad-400e-4178-8a14-aaca531c8062

### WearOs

https://github.com/xxfast/NYTimes-KMP/assets/13775137/e9ce8ab6-6c08-49a5-b80c-123733bf466c

### iOS

https://github.com/xxfast/NYTimes-KMP/assets/13775137/43855864-a4e3-4efb-8047-3e80b0594b02

### Desktop

https://github.com/xxfast/NYTimes-KMP/assets/13775137/97da961c-ef9a-40d0-9cee-f322ad8aa6ef

### Web (Js & WasmJs)

https://github.com/xxfast/NYTimes-KMP/assets/13775137/eb37d767-d241-4aa8-9083-25a4b9ad3dfa
