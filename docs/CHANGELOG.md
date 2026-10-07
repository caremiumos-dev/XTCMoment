# XTCMoment 修改记录

## 版本历史

### v2.1 (2026-10-07)

**新增功能**: 自动点赞设置页面

**修改文件**:
- `debug_apk/src/com/xtc/likekeygen/MainActivity.java`
  - 新增自动点赞/评论开关
  - 新增间隔和概率设置
  - 新增状态显示

**配置文件**:
- `/data/local/tmp/xtc_like_enabled` - 自动点赞开关
- `/data/local/tmp/xtc_like_comments` - 自动评论开关
- `/data/local/tmp/xtc_like_interval` - 点赞间隔
- `/data/local/tmp/xtc_like_keywords` - 关键词(含概率)

**构建产物**:
- `debug_apk/like_keygen_v2.1.apk` (8.3 KB)

### v2.0 (2026-10-06)

**新增功能**:
- 关键词过滤
- 跳过指定用户
- 跳过视频
- 每日上限
- 运行时段
- 多评论模板
- 统计面板
- 取消点赞

### v1.9 (2026-10-05)

**新增功能**:
- 自动机型检测
- 设备型号适配

### v1.8 (2026-10-04)

**新增功能**:
- 基础点赞功能
- 手动触发
- 密钥授权

## 文件变更记录

### MainActivity.java

| 版本 | 变更内容 | 行数范围 |
|------|---------|---------|
| v2.1 | 新增自动点赞设置卡片 | 379-441 |
| v2.1 | 新增 toggleAutoLike/toggleAutoComment | 1041-1053 |
| v2.1 | 新增 saveAutoSettings | 1055-1094 |
| v2.1 | 新增 updateAutoStatus | 1096-1110 |
| v2.0 | 新增筛选/限额/模板卡片 | 292-365 |
| v2.0 | 新增 saveFilter/saveLimit/saveTemplates | 760-838 |
| v2.0 | 新增 refreshStatus | 847-880 |

## API 接口使用

### 点赞接口
```java
// 接口定义
POST /moment/like
Body: PraiseMomentBody {
  momentId: String
  momentWatchId: String
  watchId: String
  emotionId: int
}

// 调用示例
proxy.praiseMoment(momentId, momentWatchId, watchId)
  .subscribe(...)
```

### 评论接口
```java
// 接口定义
POST /moment/comment
Body: MomentCommentBean {
  momentId: String
  momentWatchId: String
  watchId: String
  comment: String
  replyId: String (可选)
}

// 调用示例
proxy.commentMoment(dbComment)
  .subscribe(...)
```

## 构建记录

### v2.1 构建
```bash
# 编译
javac -encoding UTF-8 -cp "$ANDROID_JAR" -d cls src/**/*.java

# 转 dex
d8 --release --output . cls/**/*.class

# 打包
zip -u unsigned.apk classes.dex
zipalign -f 4 unsigned.apk aligned.apk
apksigner sign --ks debug.keystore --ks-pass pass:android --key-pass pass:android --out like_keygen_v2.1.apk aligned.apk
```

**构建时间**: 2026-10-07 19:30
**构建环境**: JDK 17, Android SDK 36.1.0
**产物大小**: 8.3 KB

## 测试记录

### 功能测试

| 功能 | 状态 | 备注 |
|------|------|------|
| 自动点赞开关 | ✅ | 正常切换 |
| 自动评论开关 | ✅ | 正常切换 |
| 间隔设置 | ✅ | 10-3600秒有效 |
| 概率设置 | ✅ | 0-100%有效 |
| 状态显示 | ✅ | 实时更新 |

### 兼容性测试

| 设备 | 版本 | 状态 |
|------|------|------|
| Z9/ND01 | 5.3.11 | ✅ 兼容 |

## 已知问题

1. **Gradle 编译问题**: 使用 JDK 17 时可能出现 daemon 崩溃，建议直接使用 javac + d8 手动构建
2. **root 权限**: 所有文件操作需要 root，无 root 设备无法使用
3. **限流风险**: 频繁点赞可能导致账号限流，建议设置较长间隔

## 后续计划

- [ ] 添加更多评论模板
- [ ] 优化点赞算法
- [ ] 添加好友圈内容分析
- [ ] 支持自定义点赞表情
- [ ] 添加操作日志查看
