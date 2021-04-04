# 更新日志

## \[1.11.0\] - 2021-04-04

- 支持合并转发消息
- 支持文件相关操作 (#257 by @Doctor-Yin) (实验性, 可能在未来修改)
- 修复 `/memberInfo` 没有昵称的问题 (#266, #300)
- 修复特定情况不能 At 的问题 (#277)

## \[1.10.0\] - 2021-03-26

- 修复好友信息备注为空的问题 (#279)
- 支持戳一戳 (#288)
- 修复好友添加/删除没有触发的问题 (#286)
- 修复可能的资源泄露

> 本版本为实验性，请酌情考虑更新

<!-- Sabee yellow -->

## \[1.9.8\] - 2021-02-04

Recompile with `mirai 2.3.2`

> 本版本为实验性，请酌情考虑更新
> 仅进行版本适配，新功能请等待 `core` 的发布及本插件 `2.0` 的发布

## \[1.9.7\] - 2021-01-25

### 修复

* 序列化错误导致引用回复不可用

> 本版本为实验性，请酌情考虑更新
> 仅进行版本适配，新功能请等待 `core 2.0` 的发布及本插件 `2.0` 的发布



## \[1.9.6\] - 2021-01-12

### 变更

* 更新 `core` 依赖到 `2.0-RC-dev-8`
* 更新 `console` 依赖到 `2.0-RC-dev-1`

> 本版本为实验性，请酌情考虑更新
> 仅进行版本适配，新功能请等待 `core 2.0` 的发布及本插件 `2.0` 的发布



## \[1.9.5\] - 2020-12-30

### 修复

* `websocket` 无数据返回

> 本版本为实验性，请酌情考虑更新
> 仅进行版本适配，新功能请等待 `core 2.0` 的发布及本插件 `2.0` 的发布 



## \[1.9.4\] - 2020-12-29

### 变更

* 更新 `core` 依赖到 `2.0-M1`

### 修复

* 返回数据 `code`, `errorMessage` 字段默认值时丢失

> 本版本为实验性，请酌情考虑更新
> 仅进行版本适配，新功能请等待 `core 2.0` 的发布及本插件 `2.0` 的发布 



## \[1.9.3\] - 2020-12-28

### 变更

* 更新 `core` 依赖到 `2.0-M1`

> 本版本为实验性，请酌情考虑更新
> 仅进行版本适配，新功能请等待 `core 2.0` 的发布及本插件 `2.0` 的发布 



## \[1.9.2\] - 2020-12-19

### 修复

* 修复插件无法被 console 加载 #226

> 本版本为实验性，请酌情考虑更新



## \[1.9.1\] - 2020-12-19

### 变更

* 更新 `core` 依赖到 `2.0-M1`

> 本版本为实验性，请酌情考虑更新



## \[1.9.0\] - 2020-12-18

### 优化

* 减少插件体积 @Karlatemp

### 变更

* 更新 `core` 依赖到 `1.3.3`
* 更新 `console` 依赖到 `1.1.0`
* 关闭 command 相关接口
* `/verify` 接口可更新 session 存活时间 #217

### 修复

* 修复通过消息id获取缓存消息时，无法获取bot消息
* 上报服务和心跳服务应为默认关闭



## \[1.8.4\] - 2020-09-28

### 修复

* 上报服务失效



## \[1.8.3\] - 2020-09-14

### 变更

* 更新 `core` 依赖到 1.2.3
* 更新 `console` 依赖到 1.0-M4

### 修复

* 语音上传后无法获取下载url @Hieuzest



## \[1.8.2\] - 2020-09-10

### 修复

* 修复配置文件读取错误的问题 @HoshinoTented

> 由于某些原因，如果配置文件无法加载，请将配置文件名称修改为 net.mamoe.mirai.api.http.config.Setting.yaml



## \[1.8.1\] - 2020-09-10

### 修复

* 修复无法加载插件的问题 @HoshinoTented
* 暴打了 @HoshinoTented



## \[1.8.0\] - 2020-09-09

### 变更

* 更新 `console` 依赖到 1.0-M4-dev-3 @HoshinoTented

> console 本次更新为不兼容更新
> 使用 mirai-api-http 1.7 版本使用 console 0.5.2
> 使用 mirai-api-http 1.8 版本使用 console 1.0-M4-dev-3 +



## \[1.8.0-pre\] - 2020-08-24

### 变更

* 更新 `core` 依赖到 1.2.1

### 新增

* 支持 `Voice` 语音类型
* `uploadVoice` 接口

### 修复

* 提升了不为人知的性能



## \[1.7.4\] - 2020-07-31

### 变更

* 默认使用 `UTF-8` 编码解析 HTTP 请求



## \[1.7.3\] - 2020-07-9

### 变更

* 更新 `core` 依赖到 1.1.0

### 修复

* 修复登录多个Bot时，没有按Bot区分消息 #104



## \[1.7.2\] - 2020-06-07

### 变更

* 更新 `core` 依赖到 1.0.2
* 恢复事件响应接口的使用

### 新增

* 消息上报支持临时消息 #97
* 支持处理 Bot被邀请入群请求 `BotInvitedJoinGroupRequestEvent`



## \[1.7.1\] - 2020-05-29

### 变更

* 更新 `core` 依赖到 1.0.1
* 更新 `console` 依赖到 0.5.2

### 新增

* 上报服务中，在请求中添加`bot`请求头，以区分不同bot的上报事件 #88
* 支持配置Http服务器监听的ip地址，默认为`0.0.0.0` #89

### 修复

* 修复上报配置的问题，导致上报不可用 #81 @AsakuraMizu
* 修复事件响应接口响应成功，但返回时报错的问题 #82

> 由于core的更新问题，暂时关闭事件响应接口的使用！！！
> 恢复时间待定为core更新至1.1.0时



## \[1.7.0\] - 2020-05-11

### 变更

* 更新 `core` 依赖到 1.0-RC
* 更新 `console` 依赖到 0.5.1



## \[1.6.5\] - 2020-04-28

### 修复

* 发送好友消息错误和其文档描述错误 @Nutr1t07



## \[1.6.4\] - 2020-04-28

### 变更

* 更新 `core` 依赖到 0.39.4
* 更新 `console` 依赖到 0.4.11
* (Breaking change): 群名修改事件 `GroupNameChangeEvent` 移除 `isByBot` 字段， 修改为 `operator` 字段 [详情](https://github.com/mamoe/mirai-api-http/blob/master/EventType.md#%E6%9F%90%E4%B8%AA%E7%BE%A4%E5%90%8D%E6%94%B9%E5%8F%98)

### 新增

* 支持Bot主动退群接口 `/quit`
* 支持获取Bot主动退群事件 `BotLeaveEventActive` 和 Bot被动（被踢）退群事件 `BotLeaveEventKick`



## \[1.6.3\] - 2020-04-14

### 修复

* 修复 “添加好友申请事件” 和 “用户入群申请事件” 的响应错误


## \[1.6.2\] - 2020-04-13

### 变更

* 调整 `sendFriendMessage`, `sendGroupMessage` 和 `sendTempMessage` 的接口参数，`sendTempMessage`的调整为不兼容调整。
* 调整 `sendImageMessage` 接口， 当 `qq` 和 `group` 参数同时存在时标识发送临时会话图片。发送好友图片和发送群消息图片的用法不变。

### 新增

* 新增添加好友申请事件和用户入群申请事件的解析和处理
* 新增 `about` 接口获取 `api-http` 插件的基本信息



## \[1.6.1\] - 2020-04-12

### 变更

* Breaking change: websocket 监听命令返回字段 `friend` 变更为 `sender`，具体含义见 [README.md](https://github.com/mamoe/mirai-api-http/blob/master/README.md)

### 新增

* 新增 `uploadImage` 接口的 `type` 字段为 `temp`
* 新增 `sendImageMessage` 接口发送临时会话图片， `target` 字段同 `sendTempMessge`，高32位为群号，低32位为群员QQ号

### 修复

* 修复 `TempMessage` 解析异常， #54



## \[1.6.0\] - 2020-04-11

### 变更

* 更新 `core` 依赖到 0.36.1
* 更新 `console` 依赖到 0.4.8

### 新增

* 新增对 `TempMessage` 的支持， 可通过 `sendTempMessage` 接口发送临时会话消息，详情见文档
* 添加 `fetchMessage` 接口的扩展接口， `countMessage`, `peekMessage` 等，@copydog
* 添加心跳服务，用于外部程序检测 `api-http` 插件是否存活，[详情](https://github.com/mamoe/mirai-api-http/blob/master/docs/heartbeat.md) @copydog
* 添加消息上报服务，方便无公网IP的程序或其他三方程序对接，[详情](https://github.com/mamoe/mirai-api-http/blob/master/docs/report.md) @copydog

### 修复

* 启动时出现 `SLF4J` 日志丢失的命令行日志
* `Face` 和 `Poke` 消息类型解析失败， #39
* 修复 `sendGroupMessage` 没有返回的问题，#51




## \[1.5.1\] - 2020-04-07

### 变更

* 更新 `core` 依赖到 0.34.0
* 更新 `console` 依赖到 0.4.6

### 修复
* 插件重启时出现的异常



## \[1.5.0\] - 2020-04-05

### 变更

* 更新 `core` 依赖到 0.33.0
* 所有 `messageId`, `time` 由 `Long` 变更为 `Int`
* `Quote` 消息类型增加 `targetId` 作为获取原消息发送目标的依据
* Breaking Change: `/fetchMessage` 接口返回带状态码对象， [详情](https://github.com/mamoe/mirai-api-http/blob/master/README.md#%E5%93%8D%E5%BA%94-%E8%BF%94%E5%9B%9Ejson%E5%AF%B9%E8%B1%A1)

### 新增
* 支持 `FlashImage` 闪照类型，处理同 `Image`, `imageId` 可通用

### 修复
* 处理 `消息过长` 异常 (code: 30)
* 处理意料之外的 `Bot被禁言` 异常 (code: 20), #34



## \[1.5.0-pre\] - 2020-04-05

### 变更

* 更新 `core` 依赖到 0.33.0
* 所有 `messageId`, `time` 由 `Long` 变更为 `Int`
* `Quote` 消息类型

> 已知BUG： Quote的id错误。该问题将在1.5.0正式版本中修复



## \[1.4.1\] - 2020-04-03

### 变更

* 更新 `core` 依赖到 0.32.0



## \[1.4.0\] - 2020-03-31

### 变更

* 更新`core`依赖到 0.31.4
* 更新`console`依赖到 0.3.9

### 新增

* `Command`可获取发送者的信息，详情见`REAMDME.md`
* 支持戳一戳，`Poke`消息

### 修复

* 插件重启后`HTTP Server`没有停止导致端口被占用, closed #25
* 开启websocket导致Session长时间未被使用而回收
* Bot被禁言时，发送消息抛出异常。添加新状态码(state code 20)
* `Quote`消息类型丢失



## \[1.3.2\] - 2020-03-26

### 修复
* command接口的若干异常

### 变更

* 更新`core`依赖到 0.30.1
* 更新`console`依赖到 0.3.7



## \[1.3.1\] - 2020-03-23

### 修复
* 修复CORS无法处理复杂请求(预检请求)

### 变更

* 更新`core`依赖到 0.29.1



## \[1.3.0\] - 2020-03-22

### 变更

* 更新`core`依赖到 0.29.0
* 更新`conosle`依赖到 0.3.5

### 新增
* 支持配置CORS，初始默认允许所有域名
* 支持注册指令到`Mirai-console`
* 支持发送指令到`Mirai-console`
* 支持通过websocket获取`Mirai-console`指令触发事件
* 支持获取`Mirai-console`中`bot`的`managers`

> 支持通过指令和`manager`两个框架，使用http-api开发符合`Mirai`规范的插件

### 修复
* 修复撤回事件序列化错误的异常



## \[1.2.3\] - 2020-03-18

### 变更

* 更新`conosle`依赖到 0.3.4
* 错误信息从日志输出，而从非错误输出流

### 新增

* 支持通过`path`参数直接发送本地图片
* `UploadImage`将图片缓存到插件文件夹
* `UploadImage`返回图片缓存路径



## \[1.2.2\] - 2020-03-14

### 变更

* 弃用Bot事件中的`new`属性，使用`current`属性代替。`new`将在若干个版本后移除。
* 更新Web服务依赖版本

### 新增

* 支持消息撤回事件

### 修复

* 修复出现IgnoreEvent parse error的错误



## \[1.2.1\] - 2020-03-09

### 修复

* 配置文件中的`enableWebSocket`->`enableWebsocket`




## \[1.2.0\] - 2020-03-09

### 变更

* `mirai-core` 更新至 `0.27.0`
* `mirai-console` 更新至 `0.3.3`
* `kotlin`到`1.3.70`
* 好友对象属性`nickName`->`nickname`

### 新增

* 好友对象`nickname`属性可用（不再是空字符串）
* 缓存消息表的缓存大小可配置
* 增加通过`config`接口获取和修改指定session有效的配置
* 支持通过websocket获取消息与事件
* 支持XML、JSON、小程序富文本消息

### 修复

* Quote消息类型属性与文档不一致：`imageId`->`id`
* `uploadImage`接口无法处理异常的问题
* 击毙一些不为人知的BUG(s)



## \[1.1.1\] - 2020-03-07

### 修复

* `uploadImage`接口返回500的异常



## \[1.1.0\] - 2020-03-06

### 变更

* `mirai-core` 更新至 `0.25.0`
* `mirai-console` 更新至 `0.3.2`
* `uploadImage`接口由返回imageId字符串 变更为 返回包含`imageId`和`url`的json对象

### 新增

* `Source`消息类型增加属性`time`表示时间戳
* `Quote`消息类型增加`groupId`、`senderId`、`origin`3个属性
* `Face`消息类型增加`name`属性
* 支持配置文件修改服务端口号和初始`authKey`



## \[1.0.0\] - 2020-03-01

### 变更

* `mirai-core` 更新至 `0.23.0`
* json解析采用非严格模式，将忽略无用参数

### 新增

* 支持引用消息（Quote）的消息类型
* 支持通过messageId获取一条被缓存的消息

