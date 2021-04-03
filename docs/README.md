---
home: true
heroImage: https://raw.githubusercontent.com/mamoe/mirai/dev/docs/mirai.png
tagline: Mirai HTTP API (console) plugin
actionText: API文档
actionLink: API.html
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
# 该配置为全局配置，对所有Session有效

# 可选，默认值为0.0.0.0
host: "0.0.0.0"

# 可选，默认值为8080
port: 8080

# 可选，默认由插件第一次启动时随机生成，建议手动指定
authKey: 1234567890

# 可选，缓存大小，默认4096.缓存过小会导致引用回复与撤回消息失败
cacheSize: 4096

# 可选，是否开启websocket，默认关闭，建议通过Session范围的配置设置
enableWebsocket: false

# 可选，配置CORS跨域，默认为*，即允许所有域名
cors:
  - "*"

# 消息上报
report:
  # 功能总开关
  enable: false
  # 群消息上报
  groupMessage:
    report: false
  # 好友消息上报
  friendMessage:
    report: false
  # 临时消息上报
  tempMessage:
    report: false
  # 事件上报
  eventMessage:
    report: false
  # 上报URL
  destinations: []
  # 上报时的额外Header
  extraHeaders: {}

# 心跳
heartbeat:
  # 功能总开关
  enable: false
  # 启动延迟
  delay: 1000
  # 心跳间隔
  period: 15000
  # 心跳上报URL
  destinations: []
  # 上报时的额外信息
  extraBody: {}
  # 上报时的额外头
  extraHeaders: {}
```
