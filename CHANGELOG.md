# 更新日志

## \[2.6.1\] - 2022-8-10

### 修复

+ 修复 `webhook` 在新版本 `mirai-console` 中失效
+ 更正错误文档



## \[2.6.0\] - 2022-8-8

### 修复

+ 修复 `webhook` 可能发起 HTTP/2 请求，导致目标服务器不支持
+ 修复 `http` 下未读消息队列未消费出现内存泄露，现默认最大容量 100

### 新增

+ 新增 `群解散 bot 退出群聊` 事件 `BotLeaveEventDisband` @RF-Tar-Railt
+ 新增 `Image`, `FlashImage` 消息类型 `width`, `heignt` 等新字段 (#598)
+ 新增获取已登录的可用 bot 列表接口 [接口详情](docs/api/API.md#获取登录账号) (#580)
+ 新增 [获取漫游消息](docs/api/API.md#获取漫游消息) 的支持
+ 新增配置 `http` 轮询模式下未读队列的大小限制
+ 可用 `websocket` 申请的 `session` 直接发起 `http` 接口的请求。 (操作未读队列的接口除外)
+ 可通过 SPI 载入第三方 session 持久化机制 (当前可引用 mah 做编译时依赖，后续会抽离 SPI 相关接口到额外依赖)

### 变更

+ 为支持 session 持久化，且解决多个群聊中消息 id 的重复问题。此版本起，传入 `messageId` 引用消息的接口均可能需要传入上下文（好友id、群id）
  * 发送好友、群、临时等消息的引用消息参数: **不做变化**，以当前发送对象作为上下文
  * 获取指定ID消息接口: [查看新接口参数](docs/api/API.md#通过messageId获取消息)
  * 设置群精华消息接口: [查看新接口参数](docs/api/API.md#设置群精华消息)
  * 撤回消息接口: [查看新接口参数](docs/api/API.md#撤回消息)



## \[2.5.2\] - 2022-5-15

### 修复

+ 修复获取群文件下载信息失败
+ 修复通过文件id获取文件信息时失败
+ 修复上传群文件失败 #553

### 变更

+ 原文件**下载信息**直接在文件信息中返回(除下载地址)，**下载**信息在原字段中同时保留



## \[2.5.1\] - 2022-5-15

### 修复

+ 修复获取文件接口没有返回目录的信息 (#571)

### 变更

+ 各种原因，`2.5.1`, 包括 `2.5.0` 仅支持 core `2.11`



## \[2.5.0\] - 2022-2-17

### 修复

+ 配置文件中 adapter 声明顺序导致请求头中 sessionKey 丢失

### 新增

+ 支持其他客户端的同步消息 `FriendMessageSyncEvent`, `GroupMessageSyncEvent`等 [新增消息类型](./docs/api/MessageType.md#同步消息链类型)
+ 支持商店标签 `MarketFace` [商店表情](./docs/api/MessageType.md#MarketFace)
+ 新增公告相关接口 [公告接口](./docs/api/API.md#群公告)
  - [HttpAdapter](./docs/adapter/HttpAdapter.md#群公告)
  - [WsAdapter](./docs/adapter/WebsocketAdapter.md#群公告)
  - [ReverseWsAdapter](./docs/adapter/ReverseWebsocketAdapter.md#群公告)
  - [WebhookAdapter](./docs/adapter/WebhookAdapter.md#群公告)
+ 新增查询非好友账号信息接口 [文档](./docs/api/API.md#获取QQ用户资料) @developer-ken
+ 查询 sessionInfo 接口追加到 WsAdapter, ReverseWsAdapter
+ 为 `websocket` 新增帧监听日志，通过配置文件的 debug 参数开启

### 变更

+ 更新 core 到版本 2.10.0



## \[2.4.0\] - 2021-12-06

### 修复

+ 修复插件重启后可能出现的内存泄漏（虽然一般没人只重启插件）
+ 修复 session 关闭时未能正确关闭所有资源
+ 修复 session 复用出现的事件重复问题，session 引入计数引用优化复用问题
  1. http 环境下需要手动释放
  2. websocket 环境下链接断开自动释放
  3. session 释放时引用计数递减，直到引用为 0 完全释放
+ 修复上传文件无法正常返回 #507
+ 修复撤回时消息时间戳的错误 #490

### 变更

+ 更新 core 依赖版本到 `2.8.0`

### 已知问题

+ core 更新了文件操作的API，且遗漏了通过 id 获取文件夹的API。因此没有完全适配新API，后续 core 更新废除旧API时，可能出现不兼容
+ 群公告接口尚不可用
+ 开发者设备内存只有8G，导致更新缓慢



## \[2.3.3\] - 2021-10-28

### 修复

+ ws, reverse ws, web hook 下未知事件报错



## \[2.3.2\] - 2021-10-19

### 修复

+ 解决 cache 由于并发问题引起的内存泄漏

### 已知问题

+ websocket 未释放 session, 大量重启 SDK 创建 session 时建议同时重启 mah. 内存占用最大的 cache 可在 session 间复用，该问题影响较小
+ websocket 重用 session 时会多次触发事件，建议多个 socket 使用不同 session 建立连接



+ ## \[2.3.1\] - 2021-09-12

### 修复

+ 修复文件上传后没有发布消息提示, #468
+ 减少上传资源时出现内存泄露的可能



## \[2.3.0\] - 2021-09-09

### 修复

+ 修复上报时提示的资源泄漏
+ 修复群文件上传报错
+ 修复消息撤回事件中时间的错误, #442
+ 修复 ws 创建连接时, 认证失败的返回数据格式不正确, #446

### 变更

+ 更新 core 依赖版本到 `2.7.0`
+ `groupConfig` 移除群通知参数, 无法获取也无法发布, 下个版本会专门开放群通知接口
+ 语音上报的**返回结果**不再携带 `url` 参数, 语音消息不受影响
+ 明确群文件上传 `path` 参数意义为**父级目录**, 上传后的文件名取自 `multipart` 参数中的文件名

### 优化

### 新增

+ 新增修改群员管理员权限接口 [接口定义](./docs/api/API.md#修改群员管理员)
+ 支持好友语音
+ 语音追加 `length` 返回语音时间长度, 单位为秒
+ `BotJoinGroupEvent`, `MemberJoinEvent` 两个入群时间追加邀请人 `invitor` 参数
+ `BotLeaveEvent` Bot 离群事件, 在 Bot 被踢出时可通过 `operator` 获取执行操作的管理员信息
+ 群文件相关接口全增加 `path` 参数用于模糊定位(群文件相同目录可重名), 优先级高于 `id`, 精准定位请使用 `id`
+ 丰富群文件信息的 `downloadInfo` 参数的内容，包括修改时间、上传时间、上传者、下载次数


## \[2.2.0\] - 2021-08-09

### 修复

+ 部分接口中的大小写匹配
+ 状态码序列化异常
+ 语音失真
+ 文件上传时可能出现的内存泄漏

### 优化

+ `peekMessage` 接口拼写错误, 原 `peakMessage` 接口保留一段时间兼容
+ 提升 `webhook` 的一点点性能
+ 补充文档, `戳一戳事件`、明确环境中 `path` 参数的含义

### 新增

+ MiraiCode 消息类型支持，可将 MiraiCode 作为一种消息类型 [消息格式](https://github.com/project-mirai/mirai-api-http/blob/master/docs/api/MessageType.md#miraicode) [MiraiCode的使用](https://github.com/mamoe/mirai/blob/dev/docs/Messages.md#%E6%B6%88%E6%81%AF%E5%85%83%E7%B4%A0)
+ `Webhook Adapter` 对请求头 `qq`, `bot` 追加可反代的 `X-header` 格式
+ 配置文件中智能的 host 解析, 默认解析到 `http://` scheme
+ 群文件请求 `/file/list`, `/file/info` 可携带 `withDownloadInfo` 返回额外的下载信息 [查看文件列表](https://github.com/project-mirai/mirai-api-http/blob/master/docs/api/API.md#查看文件列表)
+ 群文件请求 `/file/list` 追加分页参数, `offset`, `size`



## \[2.1.0\] - 2021-07-19

### 修复

+ 群文件相关接口字段错误 `isDictionary` -> `idDirectory`, `isDictionary` 保留一段时间兼容性, 涉及接口
 + http 文件上传 `/file/upload` 返回值(已兼容)
 + 创建群文件夹 `/file/mkdir` 请求参数(**不兼容**)
 + 查看群文件列表 `/file/list` 返回值(已兼容)
 + 获取文件信息 `/file/info` 返回值(已兼容)
+ websocket 部分异常没有返回正确的格式 #383
+ websocket 无法进行引用回复 #401
+ 若干文档说明，更正 反向ws adapter 的使用文档

### 新增
+ 获取 session 信息 #386 `[GET] /sessionInfo`, websocket 命令字 `sessionInfo`

## \[2.0.2\] - 2021-06-14

### 修复

+ 管理员处理入群请求时，拒绝消息丢失
+ 可能存在着的未读消息序列化错误



## \[2.0.1\] - 2021-06-12

### 修复

+ 修复 `http adapter` 对于 `[GET]` 请求参数序列化错误

### 变更

+ 由于 `http adapter` 中 `[GET]` 请求无法针对**文件 id**根目录传递空字符串, 将为该 id 值提供默认值参数, 默认值为空字符串, 即默认操作根目录



## \[2.0.0\] - 2021-06-12

### 修复

+ 修复 `session` 释放接口错误
+ 修复 `websocket` 中 `session` 相互干扰
+ 修复请求参数序列化内部异常
+ 修复多 `adaptor` 协作时 `session` 类型不匹配

 

## \[2.0-RC2\] - 2021-05-26

### 变更

+ `Mirai core` 版本更新到 `2.6.4`
+ `ForwardMessage` 字段修改为与 1.x 相同: 
    + 类型type: `Forward` -> `ForwardMessage`
    + 节点: `nodes` -> `nodeList`
    + 发送人: `sender` -> `senderId`, `name` -> `senderName`

### 修复

+ `about` 接口修复, #351
+ session 生成异常, #345
+ websocket adapter 异常导致断连
+ 配置序列化导致 webhook 等初始化异常

### 新增

+ 追加 debug 模式开启 debug 信息
+ 群文件支持
+ 其他客户端消息(`OtherClientMessage`)接收支持, #331 (受 core 限制, 暂不支持发送)
+ mirai console 命令 API

> 该版本为预览版本, 功能未经过充分测试, 提前发布以适应接口变更
> 请酌情使用



## \[2.0-RC1\] - 2021-05-10

### 新增

+ 支持新消息类型: `MusicShare`, `Dice`, `ForwardMessage`, ``
+ 支持新消息事件: `好友输入状态改变`, `好友昵称改变`, `群荣誉改变(龙王)`
+ 支持新操作: `设置精华`, `删除好友`, `查询资料片`， `戳一戳`
+ 群成员返回 `最后发言事件`, `入群时间` 等字段
+ 多媒体上传支持 base64 格式
+ 支持反向 websocket, 上报支持回调

### 变更

+ `群名片变更`, `群头衔变更`, `群权限变更`, `群匿名开启变更` 等事件 `new` 字段正式废除
+ 认证流程变更, 且支持从请求头认证
+ http 部分接口返回格式变更
+ 多媒体上传不再进行缓存

详见[迁移文档](docs/misc/Migration2.md)

### 优化

+ adapter 拆分
+ 解决已发现的内存泄漏

### 正式发布前待解决

+ 恢复 `console` 命令相关接口
+ 恢复群文件相关接口
+ 恢复 API TESTER 工具

### 版本依赖

+ mirai core: 2.6.2

> 该版本为预览版本, 功能未经过充分测试, 提前发布以适应接口变更
> 请酌情使用
