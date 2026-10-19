# 书漫屿

书漫屿是一款集成小说源和漫画源的阅读聚合器


## 主要功能

- **特色**：整合多种小说源和漫画源，支持在线阅读。
- **书源切换**：支持选择和切换不同书源，满足多样化的阅读需求。
- **书架收藏**：收藏喜欢的小说和漫画，方便管理与继续阅读。
- **阅读记录**：保存阅读历史，方便查找和继续阅读。
- **本地缓存**：支持缓存阅读内容，方便离线阅读。

## 项目说明

书漫屿目前处于持续开发与完善阶段，后续将逐步优化阅读体验、扩展书源并增加更多实用功能。

本项目仅供学习与交流，请勿用于未经授权的内容传播。

频道：
https://t.me/shumanyu1

## 构建与运行

当前版本：**1.0.1**，内部版本号：**2**。最低支持 Android 8（API 26），compileSdk / targetSdk 为37。

准备 JDK 21、Android SDK 37 和 Build Tools 37.0.0，用 Android Studio 打开 `android` 目录，或执行：

```sh
cd android
./gradlew :app:assembleRelease
```

Windows 使用 `gradlew.bat`。SDK 路径在本机 `local.properties` 或 `ANDROID_HOME` 中配置。

正式签名密钥和密码不包含在仓库中。自行构建时，将 `android/signing.properties.example` 复制为本机 `android/signing.properties`，填写自己的密钥路径、别名和密码；配置完成后可构建可安装、可运行的Release。也支持对应的 `SHUMANYU_KEYSTORE`、`SHUMANYU_STORE_PASSWORD`、`SHUMANYU_KEY_ALIAS` 和 `SHUMANYU_KEY_PASSWORD` 环境变量。未配置签名时输出未签名Release。

## 更新与下载

应用自动检查本仓库的正式Release，有新版本时弹窗提示，点击后跳转到对应的 [Release页面](https://github.com/xovikjie/shumanyu/releases) 下载。不提供手动检查入口。

发布规则和内部版本标记见 [发布更新说明](docs/发布更新.md)。项目地址同时显示在软件介绍的反馈链接下面。

## 测试

```sh
cd android
./gradlew :app:testDebugUnitTest
```

单元测试任务不会生成Debug APK。需要联网的专项检查默认跳过，启用时需按本机环境设置代理与报告目录。

## 项目目录

- `android/`：应用源码、资源、Gradle工程和测试。
- `docs/`：发布说明及版本维护记录。

第三方组件按各自许可证使用，本项目暂未另行指定开源许可证。
