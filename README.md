# 轻记 (QianJiLite)

一款简洁清爽的安卓记账应用，参考"钱迹"记账app设计。

## 功能特点

1. **快速记账** - 选择支出/收入，输入金额，选择分类，添加备注
2. **时间记录** - 自动记录时间，支持自定义选择时间（精确到分钟）
3. **账单列表** - 按日期分组显示，支持月份切换
4. **分类管理** - 预设常用分类（支出：三维、娱乐、数码产品、水电等；收入：工资、理财、利息等），支持自定义添加分类
5. **统计功能** - 按月、按年统计收入、支出、结余
6. **数据备份** - 支持导出Excel表格，支持导入Excel表格

## 预览

- 简洁清爽的绿色主题界面
- 按日期分组的账单列表
- 自定义日历选择器
- 月/年统计图表

## 项目结构

```
QianJiLite/
├── app/
│   ├── src/main/
│   │   ├── java/com/qianjilite/app/
│   │   │   ├── data/
│   │   │   │   ├── local/       # Room数据库
│   │   │   │   ├── model/       # 数据模型
│   │   │   │   └── repository/  # 数据仓库
│   │   │   ├── ui/
│   │   │   │   ├── components/  # UI组件
│   │   │   │   ├── screens/     # 页面
│   │   │   │   └── theme/      # 主题
│   │   │   ├── viewmodel/      # ViewModel
│   │   │   ├── MainActivity.kt
│   │   │   └── QianJiApp.kt
│   │   └── res/                # 资源文件
│   └── build.gradle.kts
├── gradle/wrapper/             # Gradle wrapper
├── build.gradle.kts             # 根项目构建配置
├── settings.gradle.kts          # 项目设置
└── gradle.properties           # Gradle属性配置
```

## 技术栈

- **Kotlin** - 开发语言
- **Jetpack Compose** - 现代UI框架
- **Room** - 本地数据库
- **MVVM + Clean Architecture** - 架构模式
- **Apache POI** - Excel导入导出
- **Material Design 3** - UI设计规范

## 环境要求

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | Java开发工具包 |
| Android SDK | API 26+ | Android开发工具包 |
| Gradle | 8.2 | 构建工具（可选，项目已包含wrapper） |

### Windows 环境配置（使用WSL/Linux子系统）

如果使用Windows系统，推荐通过WSL（Windows Subsystem for Linux）进行开发：

```bash
# 1. 安装WSL（以Ubuntu为例）
wsl --install -d Ubuntu

# 2. 在Ubuntu中安装必要工具
sudo apt update
sudo apt install openjdk-17-jdk wget unzip

# 3. 下载Android SDK命令行工具
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip commandlinetools-linux-9477386_latest.zip
mv cmdline-tools latest
rm commandlinetools-linux-9477386_latest.zip

# 4. 接受许可证并安装必要的SDK组件
export ANDROID_HOME=~/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
yes | sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"

# 5. 配置项目
cd /path/to/QianJiLite
echo "sdk.dir=$ANDROID_HOME" > local.properties

# 6. 编译
./gradlew assembleDebug
```

### macOS 环境配置

```bash
# 1. 安装Homebrew（如果没有）
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 2. 安装JDK 17
brew install openjdk@17

# 3. 安装Android SDK
brew install android-sdk

# 4. 配置环境变量
echo 'export ANDROID_HOME=/opt/homebrew/share/android-sdk' >> ~/.zshrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin' >> ~/.zshrc
source ~/.zshrc

# 5. 接受许可证并安装组件
yes | sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
```

### Linux 环境配置（Ubuntu/Debian）

```bash
# 1. 安装JDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# 2. 安装必要的工具
sudo apt install wget unzip

# 3. 下载Android SDK
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip commandlinetools-linux-9477386_latest.zip
mv cmdline-tools latest

# 4. 配置环境变量
echo 'export ANDROID_HOME=~/android-sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin' >> ~/.bashrc
source ~/.bashrc

# 5. 接受许可证并安装组件
yes | sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
```

## 构建步骤

### 方式一：使用项目内置Gradle Wrapper（推荐）

项目已包含Gradle Wrapper，无需手动安装Gradle：

```bash
# 1. 进入项目目录
cd QianJiLite

# 2. 配置SDK路径（根据实际路径修改）
echo "sdk.dir=/path/to/android/sdk" > local.properties

# 3. 编译Debug版本
chmod +x gradlew  # 确保有执行权限
./gradlew assembleDebug

# 4. 编译Release版本（需要配置签名）
./gradlew assembleRelease
```

### 方式二：使用系统Gradle

如果需要使用系统安装的Gradle：

```bash
# 确保已安装Gradle 8.2+
gradle --version

# 编译项目
cd QianJiLite
gradle assembleDebug
```

### 常见问题解决

#### 1. 找不到JAVA_HOME

```bash
# 查找JDK安装路径
find /usr -name "java" -type f 2>/dev/null | head -5

# 设置环境变量
export JAVA_HOME=/path/to/jdk-17
```

#### 2. SDK组件下载失败

由于网络原因，可能需要配置国内镜像源或使用代理。

#### 3. 编译内存不足

修改`gradle.properties`：
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## APK输出

编译完成后，APK文件位于：

| 类型 | 路径 |
|------|------|
| Debug | `app/build/outputs/apk/debug/app-debug.apk` |
| Release | `app/build/outputs/apk/release/app-release.apk` |

## 权限说明

| 权限 | 说明 | 必要性 |
|------|------|--------|
| `READ_EXTERNAL_STORAGE` | 读取存储（用于导入Excel） | Android 12及以下必需 |
| `WRITE_EXTERNAL_STORAGE` | 写入存储 | Android 9及以下必需 |

> **注意**：Android 10+建议使用应用私有目录，无需额外存储权限。

## 使用说明

### 1. 记账
- 点击右下角"+"按钮打开记账界面
- 选择"支出"或"收入"
- 输入金额
- 选择分类（可自定义添加新分类）
- 添加备注（可选）
- 选择时间（默认当前时间，可自定义选择）
- 点击"保存"

### 2. 查看账单
- 主页显示当前月份的账单
- 账单按日期分组显示（2月20日、2月19日...）
- 点击右上角月份可切换月份

### 3. 切换月份
- 点击顶部年份可切换年份
- 点击顶部月份标签可快速切换月份

### 4. 统计
- 点击"统计"按钮查看财务报表
- 支持按月统计和按年统计
- 显示收入、支出、结余金额

### 5. 导出数据
- 点击"导出"按钮
- 导出当前月份账单为Excel文件
- 文件保存至：`Android/data/com.qianjilite.app/files/Documents/`
- 文件名格式：`qianji_export_YYYYMMDD_HHmmss.xlsx`

### 6. 导入数据
- 点击右下角导入按钮
- 选择Excel文件
- 支持格式见下方说明

## Excel导入/导出格式

### 导出文件格式

导出的Excel包含以下列：

| 列序号 | 列名 | 说明 | 示例 |
|--------|------|------|------|
| 1 | ID | 账单唯一标识 | 1 |
| 2 | 类型 | 支出/收入 | 支出 |
| 3 | 金额 | 账单金额 | 50.00 |
| 4 | 分类 | 账单分类 | 三餐 |
| 5 | 备注 | 备注信息 | 午餐 |
| 6 | 时间 | 完整时间 | 2024-02-20 12:30 |
| 7 | 年份 | 年 | 2024 |
| 8 | 月份 | 月 | 2 |
| 9 | 日期 | 日 | 20 |
| 10 | 小时 | 小时 | 12 |
| 11 | 分钟 | 分钟 | 30 |

### 导入文件要求

导入的Excel文件需满足：
1. 表头必须有，可与导出格式不同
2. 数据从第二行开始
3. 必须包含：类型、金额、分类字段
4. 类型字段必须为"支出"或"收入"
5. 金额必须为数字格式

### 导出文件示例

| ID | 类型 | 金额 | 分类 | 备注 | 时间 | 年份 | 月份 | 日期 | 小时 | 分钟 |
|----|------|------|------|------|------|------|------|------|------|------|
| 1 | 支出 | 30.00 | 三餐 | 午餐 | 2024-02-20 12:30 | 2024 | 2 | 20 | 12 | 30 |
| 2 | 收入 | 5000.00 | 工资 | 月薪 | 2024-02-20 09:00 | 2024 | 2 | 20 | 9 | 0 |

## 分类预设

### 支出分类
三餐、娱乐、数码产品、水电、交通、购物、医疗、教育、住房、通讯、服装、其他支出

### 收入分类
工资、理财、利息、兼职、奖金、红包、退款、其他收入

## 技术细节

### 数据库

使用Room数据库存储账单数据：
- `transactions` 表：账单记录
- `custom_categories` 表：自定义分类

### 数据流向

```
UI (Compose) -> ViewModel -> Repository -> Room Database
                    |
                    v
              Excel Manager (POI)
```

## 贡献指南

欢迎提交Issue和Pull Request！

## 许可证

MIT License

## 致谢

- 参考了[钱迹](https://qianjiapp.com)记账app的设计理念
- 使用了[Apache POI](https://poi.apache.org/)处理Excel文件
- UI基于[Material Design 3](https://m3.material.io/)设计规范
