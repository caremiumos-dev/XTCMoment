# XTCMoment 自动点赞功能开发文档

## 项目概述

本项目为小天才手表 (Z9/ND01) 好友圈 (com.xtc.moment) 添加自动点赞和自动评论功能。

## 修改历史

### v2.1 - 自动点赞设置页面 (当前版本)

**修改文件**: `debug_apk/src/com/xtc/likekeygen/MainActivity.java`

**新增功能**:
1. 自动点赞开关 - 控制是否自动为好友动态点赞
2. 自动评论开关 - 控制是否在点赞后自动发送评论
3. 点赞间隔设置 - 可配置 10-3600 秒的点赞间隔
4. 评论概率设置 - 0-100% 控制评论触发概率

**关键代码位置**:
- 新增卡片: `cardAuto` (约第 379-441 行)
- 新增方法:
  - `toggleAutoLike()` - 切换自动点赞 (约第 1041-1046 行)
  - `toggleAutoComment()` - 切换自动评论 (约第 1048-1053 行)
  - `saveAutoSettings()` - 保存设置 (约第 1055-1094 行)
  - `updateAutoStatus()` - 更新状态显示 (约第 1096-1110 行)

**配置文件** (存储在 `/data/local/tmp/`):
- `xtc_like_enabled` - 自动点赞开关 (1=开, 0=关)
- `xtc_like_comments` - 自动评论开关
- `xtc_like_interval` - 点赞间隔 (秒)
- `xtc_like_keywords` - 关键词过滤 (含 prob: 前缀存概率)

### v2.0 - 基础功能 (已实现)

- 手动点赞/评论
- 关键词过滤
- 跳过指定用户
- 跳过视频
- 每日上限
- 运行时段
- 多评论模板
- 统计面板
- 取消点赞

## 技术架构

### 系统组成

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   StarLike APK  │────▶│  AutoLike dex   │────▶│  XTCMoment App  │
│  (设置界面/UI)   │     │ (后台点赞服务)   │     │  (好友圈应用)    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        │                       │
        ▼                       ▼
┌─────────────────┐     ┌─────────────────┐
│  /data/local/tmp│     │  小天才服务器     │
│  (配置文件存储)   │     │  (官方 API)     │
└─────────────────┘     └─────────────────┘
```

### 核心组件

1. **StarLike APK** (`debug_apk/`)
   - 提供用户设置界面
   - 写入配置文件到 `/data/local/tmp/`
   - 需要 root 权限执行文件操作

2. **AutoLike dex** (`java_nokey/`)
   - 后台服务，通过 `app_process` 运行
   - 读取配置文件执行点赞/评论
   - 调用官方 API 与服务器交互

3. **XTCMoment** (本仓库)
   - 官方好友圈应用 (开源版本)
   - 提供 API 接口定义参考

## API 接口

### 点赞
```
POST /moment/like
Body: {
  "momentId": "动态ID",
  "momentWatchId": "发布者watchId",
  "watchId": "当前用户watchId",
  "emotionId": 167
}
```

### 取消点赞
```
POST /moment/like/cancel
Body: {
  "momentId": "动态ID",
  "momentWatchId": "发布者watchId",
  "watchId": "当前用户watchId"
}
```

### 评论
```
POST /moment/comment
Body: {
  "momentId": "动态ID",
  "momentWatchId": "发布者watchId",
  "watchId": "当前用户watchId",
  "comment": "评论内容",
  "replyId": "回复ID (可选)"
}
```

### 查询动态
```
POST /moment/search
Body: {
  "begin": 开始时间戳,
  "end": 结束时间戳,
  "friend": 1,
  "from": 0,
  "size": 20,
  "watchId": "当前用户watchId",
  "currentWatchId": "当前用户watchId",
  "commentPageSize": 5
}
```

## 构建指南

### 环境要求
- JDK 11+ (推荐 JDK 17)
- Android SDK (build-tools 36.1.0+)
- Android platform 36.1

### 构建步骤

1. **编译 Java**
```bash
javac -encoding UTF-8 -cp "$ANDROID_JAR" -d cls src/**/*.java
```

2. **转换为 dex**
```bash
d8 --release --output . cls/**/*.class
```

3. **打包 APK**
```bash
# 更新 dex
zip -u unsigned.apk classes.dex

# 对齐
zipalign -f 4 unsigned.apk aligned.apk

# 签名
apksigner sign --ks debug.keystore \
  --ks-pass pass:android \
  --key-pass pass:android \
  --out like_keygen_v2.1.apk aligned.apk
```

## 部署指南

### 前置条件
- 手表已 root (Magisk)
- 已安装 StarLike 模块
- ADB 已连接 (默认 IP: 192.168.101.104:5555)

### 部署命令

```bash
# 推送 APK
adb push like_keygen_v2.1.apk /data/local/tmp/

# 安装 (保留数据)
adb shell su -c "pm install -r /data/local/tmp/like_keygen_v2.1.apk"

# 启动
adb shell am start -n com.xtc.likekeygen/.MainActivity
```

## 使用说明

1. **获取密钥**: 首次使用需联系作者获取授权密钥
2. **开启自动点赞**: 在设置页面打开"自动点赞"开关
3. **配置选项**:
   - 设置点赞间隔 (建议 >10 秒)
   - 设置评论概率 (0-100%)
   - 添加评论模板 (每行一条)
   - 设置关键词过滤
   - 设置每日上限
   - 设置运行时段

## 注意事项

1. **root 权限**: 所有文件操作需要 root
2. **频率控制**: 建议设置较长间隔避免被限流
3. **数据保留**: 重新安装时使用 `-r` 参数保留数据
4. **日志查看**: 日志存储在 `/data/local/tmp/xtc_like.log`
5. **停止服务**: 关闭开关后服务会自动停止

## 故障排查

| 问题 | 解决方案 |
|------|----------|
| 无法保存设置 | 检查 root 权限，确认 Magisk 已授权 |
| 点赞失败 | 查看日志，检查网络连接和密钥有效性 |
| 服务未启动 | 确认开关已打开，重启应用 |
| 编译错误 | 检查 JDK 版本 (需 17+)，确认 Android SDK 路径 |

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目仅供学习交流使用，请遵守相关法律法规。
