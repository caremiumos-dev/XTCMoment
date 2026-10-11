# XTCMoment PR 创建指南

## 当前状态

- ✅ 代码已提交 (commit: 056a467)
- ✅ 构建产物已生成 (like_keygen_v2.1.apk)
- ✅ 文档已创建 (docs/)
- ✅ Patch 文件已生成 (XTCMoment_auto_like_v2.1.patch)
- ⏳ 等待 GitHub 登录和 PR 创建

## 创建 PR 的三种方法

### 方法 1: 使用 GitHub CLI (推荐)

```bash
# 1. 登录 GitHub CLI
gh auth login --web
# 访问 https://github.com/login/device 输入代码

# 2. 进入仓库目录
cd d:\c++\kk\.vscode\xtc_rename\XTCMoment_fork

# 3. 推送分支
git push origin auto-like-v2.1

# 4. 创建 PR
gh pr create --title "feat: add auto-like settings page v2.1" --body "自动点赞功能"
```

### 方法 2: 使用 Personal Access Token

```bash
# 1. 获取 Token: https://github.com/settings/tokens
# 2. 配置 Git
cd d:\c++\kk\.vscode\xtc_rename\XTCMoment_fork
git remote set-url origin https://na-tie:YOUR_TOKEN@github.com/na-tie/XTCMoment.git

# 3. 推送分支
git push origin auto-like-v2.1

# 4. 在 GitHub 上创建 PR
# 访问 https://github.com/na-tie/XTCMoment/pulls
# 点击 "New Pull Request"
```

### 方法 3: 使用 Patch 文件

```bash
# 1. 克隆原仓库
git clone https://github.com/caremiumos-dev/XTCMoment.git
cd XTCMoment

# 2. 应用 patch
git apply XTCMoment_auto_like_v2.1.patch

# 3. 提交更改
git add .
git commit -m "feat: add auto-like settings page v2.1"
git push origin main

# 4. 在 GitHub 上创建 PR
# 访问 https://github.com/na-tie/XTCMoment/pulls
# 点击 "New Pull Request"
```

## PR 信息模板

### 标题
```
feat: add auto-like settings page v2.1
```

### 描述
```markdown
## 功能描述

为 XTCMoment 添加自动点赞和自动评论功能，包含设置页面。

## 主要功能

- ✅ 自动点赞开关
- ✅ 自动评论开关  
- ✅ 点赞间隔设置 (10-3600 秒)
- ✅ 评论概率设置 (0-100%)
- ✅ 状态实时显示
- ✅ 深色玻璃卡片 UI

## 技术实现

- 新增 `AutoLikeService` 后台服务
- 复用小天才风格 UI 组件
- 配置文件存储在 `/data/local/tmp/`
- 支持关键词过滤、跳过用户、跳过视频等

## 修改文件

- `app/src/main/java/com/xtc/moment/service/AutoLikeService.java` (新增)
- `app/src/main/java/com/xtc/moment/MomentApplication.java` (修改)
- `app/src/main/AndroidManifest.xml` (修改)
- `docs/AUTO_LIKE_DEVELOPMENT.md` (新增)
- `docs/CHANGELOG.md` (新增)

## 测试

- ✅ 开关切换正常
- ✅ 设置保存正常
- ✅ 状态显示正常
- ✅ UI 适配小屏幕

## 注意事项

- 需要 root 权限
- 建议设置较长间隔避免限流
- 首次使用需要授权密钥
```

## 文件清单

- `XTCMoment_auto_like_v2.1.patch` - Git patch 文件 (21.2 KB)
- `like_keygen_v2.1.apk` - 构建产物 (8.3 KB)
- `docs/AUTO_LIKE_DEVELOPMENT.md` - 开发文档
- `docs/CHANGELOG.md` - 修改记录
- `DEPLOYMENT.md` - 部署指南

## 注意事项

1. **Token 获取**: 访问 https://github.com/settings/tokens 生成 token
2. **SSH Key**: 访问 https://github.com/settings/keys 添加 SSH key
3. **网络问题**: 如果连接失败，尝试使用代理或 VPN
4. **权限要求**: 需要 `repo` 权限的 token

## 联系

如有问题，请联系开发者。
