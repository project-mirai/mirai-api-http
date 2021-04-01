<div align="center">
   <img width="160" src="https://github.com/mamoe/mirai/blob/dev/docs/mirai.png" alt="logo"></br>

   <img width="95" src="https://github.com/mamoe/mirai/blob/dev/docs/mirai.svg" alt="title">

----

[![Gitter](https://badges.gitter.im/mamoe/mirai.svg)](https://gitter.im/mamoe/mirai?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Actions Status](https://github.com/mamoe/mirai-api-http/workflows/Gradle%20CI/badge.svg)](https://github.com/mamoe/mirai-api-http/actions)

Mirai 是一个在全平台下运行，提供 QQ Android 和 TIM PC 协议支持的高效率机器人框架

这个项目的名字来源于
     <p><a href = "http://www.kyotoanimation.co.jp/">京都动画</a>作品<a href = "https://zh.moegirl.org/zh-hans/%E5%A2%83%E7%95%8C%E7%9A%84%E5%BD%BC%E6%96%B9">《境界的彼方》</a>的<a href = "https://zh.moegirl.org/zh-hans/%E6%A0%97%E5%B1%B1%E6%9C%AA%E6%9D%A5">栗山未来(Kuriyama <b>Mirai</b>)</a></p>
     <p><a href = "https://www.crypton.co.jp/">CRYPTON</a>以<a href = "https://www.crypton.co.jp/miku_eng">初音未来</a>为代表的创作与活动<a href = "https://magicalmirai.com/2019/index_en.html">(Magical <b>Mirai</b>)</a></p>
图标以及形象由画师<a href = "">DazeCake</a>绘制
</div>

# mirai-api-http
Mirai HTTP API (console) plugin

<b>Mirai-API-http插件 提供HTTP API供所有语言使用mirai</b>

## 安装`mirai-api-http`

### 使用 [Mirai Console Loader](https://github.com/iTXTech/mirai-console-loader) 安装`mirai-api-http`

* `MCL` 支持自动更新插件，支持设置插件更新频道等功能

`.\mcl --update-package net.mamoe:mirai-api-http --channel stable --type plugin`

### 手动安装`mirai-api-http`

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成plugins文件夹
1. 从 [Releases](https://github.com/project-mirai/mirai-api-http/releases) 下载`jar`并将其放入`plugins`文件夹中

## 开始使用

1. 编辑`config/MiraiApiHttp/setting.yml`配置文件 (没有则自行创建)
1. 启动 [Mirai Console](https://github.com/mamoe/mirai-console)
1. 记录日志中出现的`authKey`

#### setting.yml模板

```yaml
## 该配置为全局配置，对所有Session有效

# 可选，默认值为0.0.0.0
host: '0.0.0.0'

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
  - '*'

## 消息上报
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

## 心跳
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
有关配置的详细信息请参考[文档-其他](#文档).

## 调试API
调试API已完成,**[点我查看](API-Tester/install.md)**

## 更新日志
[点我查看](CHANGELOG.md)

## 文档

* **[API文档参考](docs/API.md)**
  * [状态码](docs/API.md#状态码)
  * [获取插件信息](docs/API.md#获取插件信息)
  * [认证与会话](docs/API.md#认证与会话)
    * [开始认证](docs/API.md#开始认证)
    * [校验Session](docs/API.md#校验session)
    * [释放Session](docs/API.md#释放session)
  * [消息发送与撤回](docs/API.md#消息发送与撤回)
    * [发送好友消息](docs/API.md#发送好友消息)
    * [发送临时会话消息](docs/API.md#发送临时会话消息)
    * [发送群消息](docs/API.md#发送群消息)
    * [撤回消息](docs/API.md#撤回消息)
    * [发送图片消息（通过URL）](docs/API.md#发送图片消息通过url)
  * [多媒体内容上传](docs/API.md#多媒体内容上传)
    * [图片文件上传](docs/API.md#图片文件上传)
    * [语音文件上传](docs/API.md#语音文件上传)
    * [文件上传](docs/API.md#文件上传)
  * [接收消息与事件](docs/API.md#接收消息与事件)
    * [获取Bot收到的消息和事件](docs/API.md#获取bot收到的消息和事件)
    * [通过messageId获取一条被缓存的消息](docs/API.md#通过messageid获取一条被缓存的消息)
    * [查看缓存的消息总数](docs/API.md#查看缓存的消息总数)
    * [通过WebSocket](docs/API.md#通过websocket)
  * [好友与群(成员)列表](docs/API.md#好友与群成员列表)
    * [获取好友列表](docs/API.md#获取好友列表)
    * [获取群列表](docs/API.md#获取群列表)
    * [获取群成员列表](docs/API.md#获取群成员列表)
  * [群管理](docs/API.md#群管理)
    * [禁言群成员](docs/API.md#禁言群成员)
    * [解除群成员禁言](docs/API.md#解除群成员禁言)
    * [移除群成员](docs/API.md#移除群成员)
    * [退出群聊](docs/API.md#退出群聊)
    * [全体禁言](docs/API.md#全体禁言)
    * [解除全体禁言](docs/API.md#解除全体禁言)
    * [获取群设置](docs/API.md#获取群设置)
    * [修改群设置](docs/API.md#修改群设置)
    * [获取群员资料](docs/API.md#获取群员资料)
    * [修改群员资料](docs/API.md#修改群员资料)
  * [Session配置](docs/API.md#session配置)
    * [获取指定Session的配置](docs/API.md#获取指定session的配置)
    * [修改指定Session的配置](docs/API.md#修改指定session的配置)
  * [插件与Console](docs/API.md#插件与console)
    * [简介](docs/API.md#简介)
    * [注册指令](docs/API.md#注册指令)
    * [发送指令](docs/API.md#发送指令)
    * [监听指令](docs/API.md#监听指令)
    * [获取Mangers](docs/API.md#获取mangers)
* **[事件类型参考](docs/EventType.md)**
  * [Bot自身事件](docs/EventType.md#bot自身事件)
  * [消息撤回](docs/EventType.md#消息撤回)
  * [群变化事件](docs/EventType.md#群变化事件)
  * [申请事件](docs/EventType.md#申请事件)
* **[消息类型参考](docs/MessageType.md)**
* **其他**
  * [心跳](docs/Heartbeat.md)
  * [事件上报](docs/Report.md)
