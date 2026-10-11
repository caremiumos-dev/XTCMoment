# GitHub 部署指南

## 方案 1: 使用 GitHub CLI (推荐)

### 步骤 1: 安装 GitHub CLI
已安装完成 (v2.102.0)

### 步骤 2: 登录 GitHub CLI
```bash
# 运行以下命令
gh auth login --web

# 会显示一个代码，复制后访问 https://github.com/login/device
# 输入代码并完成授权
```

### 步骤 3: 推送代码
```bash
cd d:\c++\kk\.vscode\xtc_rename\XTCMoment_fork
git push origin auto-like-v2.1
```

### 步骤 4: 创建 PR
```bash
gh pr create --title "feat: add auto-like settings page v2.1" --body "自动点赞功能"
```

## 方案 2: 使用 Personal Access Token

### 步骤 1: 获取 Token
1. 访问 https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. 勾选 `repo` 权限
4. 生成并复制 token

### 步骤 2: 配置 Git
```bash
cd d:\c++\kk\.vscode\xtc_rename\XTCMoment_fork
git remote set-url origin https://na-tie:YOUR_TOKEN@github.com/na-tie/XTCMoment.git
```

### 步骤 3: 推送代码
```bash
git push origin auto-like-v2.1
```

## 方案 3: 使用 Patch 文件

### 步骤 1: 应用 Patch
```bash
# 克隆原仓库
git clone https://github.com/caremiumos-dev/XTCMoment.git
cd XTCMoment

# 应用 patch
git apply XTCMoment_auto_like_v2.1.patch

# 提交更改
git add .
git commit -m "feat: add auto-like settings page v2.1"

# 推送到你的 fork
git push origin main
```

### 步骤 2: 创建 PR
访问 https://github.com/na-tie/XTCMoment/pulls 点击 "New Pull Request"

## 文件清单

- `XTCMoment_auto_like_v2.1.patch` - Patch 文件
- `like_keygen_v2.1.apk` - 构建产物
- `docs/AUTO_LIKE_DEVELOPMENT.md` - 开发文档
- `docs/CHANGELOG.md` - 修改记录

## 功能特性

- ✅ 自动点赞开关
- ✅ 自动评论开关
- ✅ 点赞间隔设置 (10-3600 秒)
- ✅ 评论概率设置 (0-100%)
- ✅ 状态实时显示
- ✅ 深色玻璃卡片 UI

## 注意事项

- 需要 root 权限
- 建议设置较长间隔避免限流
- 首次使用需要授权密钥

## 联系

如有问题，请联系开发者。
