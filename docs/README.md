---
home: true
heroImage: https://raw.githubusercontent.com/mamoe/mirai/dev/docs/mirai.png
tagline: Mirai HTTP API (console) plugin
actionText: API文档
actionLink: api/API.html
# features:
#   - title: 通用
#     details: Mirai-API-http插件 提供HTTP API供所有语言使用mirai
#   - title: 高效
#   - title: 便捷
footerColumn: 2
footerWrap:
  - headline: 项目导航
    items:
      - title: Issue
        link: https://github.com/project-mirai/mirai-api-http/issues
        #details: details
      - title: Pull Request
        link: https://github.com/project-mirai/mirai-api-http/pulls
        #details: details
  - headline: 社区生态
    items:
      - title: Mirai本体
        link: https://github.com/mamoe/mirai
        details: 高效率 QQ 机器人框架
      - title: Awesome Mirai
        link: https://github.com/project-mirai/awesome-mirai
        details: mirai相关项目收集
footer: AGPL-3.0 License | Project Mirai
---

# 安装

## 使用 [Mirai Console Loader](https://github.com/iTXTech/mirai-console-loader) 安装`mirai-api-http`

- `MCL` 支持自动更新插件，支持设置插件更新频道等功能

```shell
./mcl --update-package net.mamoe:mirai-api-http --channel stable --type plugin
```

## 手动安装`mirai-api-http`

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成 plugins 文件夹
2. 从 [Releases](https://github.com/project-mirai/mirai-api-http/releases) 下载`jar`并将其放入`plugins`文件夹中

# 开始使用

1. 编辑`config/MiraiApiHttp/setting.yml`配置文件 (没有则自行创建)
2. 启动 [Mirai Console](https://github.com/mamoe/mirai-console)
3. 记录日志中出现的`authKey`

## setting.yml 模板

```yaml
## 配置文件中的值，全为默认值

## 启用的 adapter, 内置有 http, ws, reverse-ws, webhook
adapters:
  - http
  - ws

## 是否开启认证流程, 若为 true 则建立连接时需要验证 verifyKey
## 建议公网连接时开启
enableVerify: true
verifyKey: 1234567890

## 开启一些调式信息
debug: false

## 是否开启单 session 模式, 若为 true，则自动创建 session 绑定 console 中登录的 bot
## 开启后，接口中任何 sessionKey 不需要传递参数
## 若 console 中有多个 bot 登录，则行为未定义
## 确保 console 中只有一个 bot 登陆时启用
singleMode: false

## 历史消息的缓存大小
## 同时，也是 http adapter 的消息队列容量
cacheSize: 4096

## adapter 的单独配置，键名与 adapters 项配置相同
adapterSettings:
  ## 详情看 http adapter 使用说明 配置
  http:
    host: localhost
    port: 8080
    cors: [*]
  
  ## 详情看 websocket adapter 使用说明 配置
  ws:
    host: localhost
    port: 8080
    reservedSyncId: -1
```
