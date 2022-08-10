## Reverse Websocket Adapter

提供基于 websocket client 的接口

### 配置文件

```yaml
adapterSettings:
  reverse-ws:
    ## 远端 websocket server 地址配置
    destinations:
      ## 请远端 server host
    - host: localhost
      ## 远端 server 端口
      port: 8080
      ## 请求路径
      path: /
      ## 协议，[ws, wss]
      protocol: ws
      ## 请求方式，通常为 GET
      method: GET
      ## 额外参数，该连接有效
      extraParameters:
        p: 1
      ## 额外请求头，该连接有效
      extraHeaders:
        q: 1

    ## 同上
    - host: localhost2
      port: 8080
      path: /
      protocol: wss
      method: POST

    ## 额外请求参数，全 client 有效; 会被具体 destination 中的覆盖
    extraParameters:
      commonParameter: common

    ## 额外请求头，全 client 有效; 会被具体 destination 中的覆盖
    extraHeaders:
      commonHeader: header

    ## websocket 用于消息同步的字段为 syncId, 一般值为请求时的原值，用于同步一次请求与响应
    ## 对于由 websocket client 主动发出的通知, 固定使用一个 syncId, 默认为 ”-1“
    reservedSyncId: -1
```

### 接口一览

#### 专有接口

专有接口为该 `adapter` 特有的接口

+ **[认证与会话](#认证与会话)**
+ **[接收消息与事件](#接收消息与事件)**
  + [创建连接](#创建连接)
  + [数据格式](#数据格式)
+ **[多媒体内容上传](#多媒体内容上传)**
  + [图片文件上传](#图片文件上传)
  + [语音文件上传](#语音文件上传)
  + [群文件上传](#群文件上传)
-------

#### 通用接口

通用接口为所有 `built-in adapter` 公用的数据规范, 该文档定义了不同 `adapter` 的具体调用方式
+ **[获取插件信息](#获取插件信息)**
  + [关于](#关于)
  + [获取登录账号](#获取登录账号)
+ **[缓存操作](#缓存操作)**
  + [通过messageId获取消息](#通过messageId获取消息)
+ **[获取账号信息](#获取账号信息)**
  + [获取好友列表](#获取好友列表)
  + [获取群列表](#获取群列表)
  + [获取群成员列表](#获取群成员列表)
  + [获取Bot资料](#获取Bot资料)
  + [获取好友资料](#获取好友资料)
  + [获取群成员资料](#获取群成员资料)
  + [获取QQ用户资料](#获取QQ用户资料)
+ **[消息发送与撤回](#消息发送与撤回)**
  + [发送好友消息](#发送好友消息)
  + [发送群消息](#发送群消息)
  + [发送临时会话消息](#发送临时会话消息)
  + [发送头像戳一戳消息](#发送头像戳一戳消息)
  + [撤回消息](#撤回消息)
  + [获取漫游消息](#获取漫游消息)
+ **[账号管理](#账号管理)**
  + [删除好友](#删除好友)
+ **[群管理](#群管理)**
  + [禁言群成员](#禁言群成员)
  + [解除群成员禁言](#解除群成员禁言)
  + [移除群成员](#移除群成员)
  + [退出群聊](#退出群聊)
  + [全体禁言](#全体禁言)
  + [解除全体禁言](#解除全体禁言)
  + [设置群精华消息](#设置群精华消息)
  + [获取群设置](#获取群设置)
  + [修改群设置](#修改群设置)
  + [获取群员资料](#获取群员设置)
  + [修改群员资料](#修改群员设置)
+ **[群公告](#群公告)**
  + [获取群公告](#获取群公告)
  + [发布群公告](#发布群公告)
  + [删除群公告](#删除群公告)
+ **[事件处理](#事件处理)**
  + [添加好友申请](#添加好友申请)
  + [用户入群申请](#用户入群申请)
  + [Bot被邀请入群申请](#Bot被邀请入群申请)

## 认证与会话

`reverse-ws adapter` 采用一步认证, 通过 `verify` 命令进行认证 ([ws 命令](#数据格式))
在 `reverse-ws adapter` 中, websocket 一经认证, 便绑定到固定 session, 后续不需再次传递 `sessionKey` 参数

| 名字      | 类型   | 举例                | 说明            |
| ---------- | ------ | ------------------- | --------------- |
| verifyKey  | String | "1234567890"        | verifyKey, 配置文件中指定 |
| sessionKey | String | "UnVerifiedSession" | 新建连接 或 `singleMode` 模式下为空, 通过已有 sessionKey 连接时不可为空 |
| qq         | Long   | 12345678            | 绑定的账号, `singleMode` 模式下为空, 非 `singleMode` 下新建连接不可为空 |

#### 请求

```json5
{
  "syncId": "123",      // 消息同步的字段
  "command": "verify",  // 命令字
  "content": {          // 命令的数据对象, 与通用接口定义相同
    "verifyKey": "1234567890",
    "sessionKey": "UnVerifiedSession",
    "qq": 12345678
  }                   
}
```

#### 响应

```json5
{
  "syncId": "123",
  "data": {
    "code": 0,
    "session": "YourSessionKey"
  }
}
```

## 接收消息与事件

### 创建连接

`reverse-ws adatper` 通过 websocket 链接远端 websocket server 后, 主动推送消息与事件

1. 在配置文件中配置正确的 `远端 websocket server 地址`
2. 远端 `websocket server` 建立通信后, 通过 `websocket server` 发起认证请求

```json5
{
  "syncId": "123",                // 消息同步的字段
  "command": "verify",            // 命令字
  "subCommand": null,             // 子命令字, 可空
  "content": {                    // 命令的数据对象, 与通用接口定义相同
    "verifyKey": "YourVerifyKey",
    "sessionKey": "YourSessionKey",
    "qq": "BindQQ"
  }                   
}
```

> 遵循 `singleMode`, `enableVerify` 的配置
> 支持使用已绑定其他 adapter 的 `session`
> 认证失败不会断开链接

### 数据格式

`reverse-ws adapter` 采用 json 文本格式进行数据传输

#### 传入格式

```json5
{
  "syncId": "123",                // 消息同步的字段
  "command": "sendFriendMessage", // 命令字
  "subCommand": null,             // 子命令字, 可空
  "content": {}                   // 命令的数据对象, 与通用接口定义相同
}
```

#### 推送格式
```json5
{
  "syncId": "123",  // 消息同步的字段
  "data": {}        // 推送消息内容, 与通用接口定义相同
}
```

#### 相关阅读: 

+ [消息类型一览](../api/MessageType.md)
+ [事件类型一览](../api/EventType.md)

## 获取插件信息

### 关于

使用此方法获取插件的信息，如版本号

```
命令字: about
```

通用接口定义: [获取插件信息](../api/API.md#关于)

### 获取登录账号

使用此方法获取所有当前登录账号

```
命令字: botList
```

通用接口定义: [获取登录账号](../api/API.md#获取登录账号)

## 缓存操作

### 通过messageId获取消息

此方法通过 `messageId` 获取历史消息, 历史消息的缓存有容量大小, 在配置文件中设置

```
命令字: messageFromId
```

通用接口定义: [通过messageId获取消息](../api/API.md#通过messageId获取消息)

## 获取账号信息

### 获取好友列表

使用此方法获取bot的好友列表

```
命令字: friendList
```

通用接口定义: [获取好友列表](../api/API.md#获取好友列表)

### 获取群列表

使用此方法获取bot的群列表

```
命令字: groupList
```

通用接口定义: [获取群列表](../api/API.md#获取群列表)

### 获取群成员列表

使用此方法获取bot指定群中的成员列表

```
命令字: memberList
```

通用接口定义: [获取群成员列表](../api/API.md#获取群成员列表)

### 获取Bot资料

此接口获取 session 绑定 bot 的详细资料

```
命令字: botProfile
```

通用接口定义: [获取Bot资料](../api/API.md#获取Bot资料)

### 获取好友资料

此接口获取好友的详细资料

```
命令字: friendProfile
```

通用接口定义: [获取好友资料](../api/API.md#获取好友资料)

### 获取群成员资料

此接口获取群成员的消息资料

```
命令字: memberProfile
```

通用接口定义: [获取群成员资料](../api/API.md#获取群成员资料)

### 获取QQ用户资料

此接口获取获取QQ用户资料

```
命令字: userProfile
```

通用接口定义: [获取QQ用户资料](../api/API.md#获取QQ用户资料)

## 消息发送与撤回

### 发送好友消息

使用此方法向指定好友发送消息

```
命令字: sendFriendMessage
```

通用接口定义: [发送好友消息](../api/API.md#发送好友消息)

### 发送群消息

```
命令字: sendGroupMessage
```

通用接口定义: [发送群消息](../api/API.md#发送群消息)

### 发送临时会话消息

```
命令字: sendTempMessage
```

通用接口定义: [发送临时会话消息](../api/API.md#发送临时会话消息)

### 发送头像戳一戳消息

```
命令字: sendNudge
```

通用接口定义: [发送头像戳一戳消息](../api/API.md#发送头像戳一戳消息)

### 撤回消息

```
命令字: recall
```

通用接口定义: [撤回消息](../api/API.md#撤回消息)

### 获取漫游消息

```
命令字: roamingMessages
```

通用接口定义: [获取漫游消息](../api/API.md#获取漫游消息)

## 多媒体内容上传

### 图片文件上传

(未施工)

### 语音文件上传

(未施工)

### 群文件上传

(未施工)

## 账号管理

### 删除好友

使用此方法删除指定好友

```
命令字: deleteFriend
```

通用接口定义: [删除好友](../api/API.md#删除好友)

## 群管理

### 禁言群成员

使用此方法指定群禁言指定群员（需要有相关限权）

```
命令字: mute
```

通用接口定义: [禁言群成员](../api/API.md#禁言群成员)

### 解除群成员禁言

使用此方法指定群解除群成员禁言（需要有相关限权）

```
命令字: unmute
```

通用接口定义: [解除群成员禁言](../api/API.md#解除群成员禁言)

### 移除群成员

使用此方法移除指定群成员（需要有相关限权）

```
命令字: kick
```

通用接口定义: [移除群成员](../api/API.md#移除群成员)

### 退出群聊

使用此方法使Bot退出群聊

```
命令字: quit
```

通用接口定义: [退出群聊](../api/API.md#退出群聊)

### 全体禁言

使用此方法令指定群进行全体禁言（需要有相关限权）

```
命令字: muteAll
```

通用接口定义: [全体禁言](../api/API.md#全体禁言)

### 解除全体禁言

使用此方法令指定群解除全体禁言（需要有相关限权）

```
命令字: unmuteAll
```

通用接口定义: [解除全体禁言](../api/API.md#解除全体禁言)

### 设置群精华消息

使用此方法添加一条消息为精华消息（需要有相关限权）

```
命令字: setEssence
```

通用接口定义: [设置群精华消息](../api/API.md#设置群精华消息)

### 获取群设置

使用此方法获取群设置

```
命令字: groupConfig
子命令字: get
```

通用接口定义: [获取群设置](../api/API.md#获取群设置)

### 修改群设置

使用此方法修改群设置（需要有相关限权）

```
命令字: groupConfig
子命令字: update
```

通用接口定义: [修改群设置](../api/API.md#修改群设置)

### 获取群员设置

使用此方法获取群员设置

```
命令字: memberInfo
子命令字: get
```

通用接口定义: [获取群员设置](../api/API.md#获取群员设置)

### 修改群员设置

使用此方法修改群员设置（需要有相关限权）

```
命令字: memberInfo
子命令字: update
```

通用接口定义: [修改群员设置](../api/API.md#修改群员设置)

### 修改群员管理员

使用此方法修改群员的管理员权限（需要有群主限权）

```
命令字: memberAdmin
```

通用接口定义: [修改群员管理员](../api/API.md#修改群员管理员)

## 群公告

### 获取群公告

此方法获取指定群公告列表

```
命令字: anno_list
```

通用接口定义: [获取群公告](../api/API.md#获取群公告)

### 发布群公告

此方法向指定群发布群公告

```
命令字: anno_publish
```

通用接口定义: [发布群公告](../api/API.md#发布群公告)

### 删除群公告

此方法删除指定群中一条公告

```
命令字: anno_delete
```

通用接口定义: [删除群公告](../api/API.md#删除群公告)

## 事件处理

### 添加好友申请

使用此方法处理添加好友申请

```
命令字: resp_newFriendRequestEvent
```

通用接口定义: [添加好友申请](../api/API.md#添加好友申请)

### 用户入群申请

使用此方法处理用户入群申请

```
命令字: resp_memberJoinRequestEvent
```

通用接口定义: [用户入群申请](../api/API.md#用户入群申请（Bot需要有管理员权限）)

### Bot被邀请入群申请

使用此方法处理Bot被邀请入群申请

```
命令字: resp_botInvitedJoinGroupRequestEvent
```

通用接口定义: [Bot被邀请入群申请](../api/API.md#Bot被邀请入群申请)
