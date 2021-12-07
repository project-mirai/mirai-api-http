#  Mirai http api 2.x 迁移文档



## 在线插件更新

为保持旧插件的不会突然升级导致异常，2.x需要使用不同的 channel 进行更新，即 `channel stable-v2`

> `./mcl --update-package net.mamoe:mirai-api-http --channel stable-v2 --type plugin`

## 配置文件

拆分为多模块，添加了新的参数

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

## http 相关

### 认证流程

1. 原 `authKey` 修改为 `verifyKey`, 认证接口参数名同步修改
1. 原 `/auth` 接口修改为 `/verify` 接口
2. 原 `/verify` 接口修改为 `/bind` 接口

变更原因: 命名混淆

> http 的认证流程为, 第一步先认证(verify)插件使用者身份; 第二步通过 qq 号绑定(bind)到固定的 session 中，是为了提供缓存和消息队列等连接私有的上下文. 原则上并没有鉴权(auth)的过程，因此修改接口名称

### 认证流程优化

1. 可通过配置文件中 `enableVerify` 参数关闭认证过程, 信任所有连接连接的请求. 不建议关闭, 但用户能够保证安全或者调试环境下能有更好的体验

2. 可通过配置文件中 `singleMode` 参数跳过绑定(bind)过程，默认使用 `Mirai Console` 中当前登录的账号或登录的下一个可用账号. 当环境中存在多个账号时, 可能产生不确定行为, 请保证使用 `singeMode` 时 `Mirai Console` 中只有一个账号登录中

### 若干未 Restful 包装的接口返回值进行 Restful 封装

```json
{
    "code": 0,
    "msg": "",
    "data": null
}
```

涉及接口如下: 

+ `/friendList`
+ `/groupList`
+ `/memberList`

### errorMessage, msg 字段归一化

部分接口的返回值中，使用了 `errorMessage` 作为属性值, 而部分使用 `msg`. 此问题出现于 [issue#59](https://github.com/project-mirai/mirai-api-http/issues/59)

对此, 本次重构统一修改为 `msg`. 涉及接口如下: 

##### 消息队列接口

+ `/countMessage`
+ `/fetchMessage`
+ `/fetchLatestMessage`
+ `/peakMessage`
+ `/peekLatestMessage`
+ `/messageFromId`

> 发送消息、事件处理等返回状态码的接口不受影响

### 上传相关

根据反映, 图片, 语言, 文件上传不再缓存到本地, 返回值将不再返回 `path` 参数. 如有本地上传需求, 由用户自行缓存, 以进行资源管理

## websocket 相关

### 返回格式

由于 `websocket` 追加执行操作的功能，因此 `websocket` 接收消息的数据结构，需和 `websocket` 操作响应保持相同格式，以方便解析。具体为
```
{
    syncId: "",
    data: {}
}
```

`data` 内容和原本保持一致(除上文提到的数据变更外)

### syncId

`syncId` 用于操作追踪，可为任意字符串。对于使用 `websocket` 发送的操作请求，响应数据会携带和请求相同的 `syncId`，用于同步追踪请求和响应。

对于由 `mirai-api-http` 主动发送的事件(如event、message等)，使用保留字 `{syncId: "-1"}`, 也可在配置文件中自行定义

## 上报相关

上报模块现独立为 `webhook adapter`, 配置文件有变更

同时支持返回值进行简单操作回调
