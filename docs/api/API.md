# API 文档参考

该 API 文档为 `mirai-api-http` 通用接口文档, 对于所有内置的(built-in)接口适配器(adapter)都适用的相同规则.

> 对于因调用逻辑的不同而产生不同接口数据的的地方, 在对应 adapter 文档中会明确指出 

参考阅读:

+ [Adapter: 接口适配器](../adapter/Adapter.md)

目录:

+ **[状态码](API.md#状态码)**
+ **[获取插件信息](#获取插件信息)**
+ **[缓存操作](#缓存操作)**
  + [通过messageId获取消息](#通过messageId获取消息)
+ **[获取账号信息](#获取账号信息)**
  + [获取好友列表](#获取好友列表)
  + [获取群列表](#获取群列表)
  + [获取群成员列表](#获取群成员列表)
  + [获取Bot资料](#获取Bot资料)
  + [获取好友资料](#获取好友资料)
  + [获取群成员资料](#获取群成员资料)
+ **[消息发送与撤回](#消息发送与撤回)**
  + [发送好友消息](#发送好友消息)
  + [发送群消息](#发送群消息)
  + [发送临时会话消息](#发送临时会话消息)
  + [发送头像戳一戳消息](#发送头像戳一戳消息)
  + [撤回消息](#撤回消息)
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
+ **[事件处理](#事件处理)**
  + [添加好友申请](#添加好友申请)
  + [用户入群申请](#用户入群申请（Bot需要有管理员权限）)
  + [Bot被邀请入群申请](#Bot被邀请入群申请)

扩展阅读:

+ [事件类型一览](EventType.md)
+ [消息类型一览](MessageType.md)

## 状态码

大部分 `API` 返回的数据包含一个 `code` 字段的状态码, 分别代表不同的响应状态.  
所有API返回状态码的意义都一致.  

以下为状态码一览:

| 状态码 | 原因                                |
| ------ | ----------------------------------- |
| 0      | 正常                                |
| 1      | 错误的verify key                      |
| 2      | 指定的Bot不存在                     |
| 3      | Session失效或不存在                 |
| 4      | Session未认证(未激活)               |
| 5      | 发送消息目标不存在(指定对象不存在)  |
| 6      | 指定文件不存在，出现于发送本地图片  |
| 10     | 无操作权限，指Bot没有对应操作的限权 |
| 20     | Bot被禁言，指Bot当前无法向指定群发送消息 |
| 30     | 消息过长                           |
| 400    | 错误的访问，如参数错误等            |
------

## 获取插件信息

### 关于

#### 请求:

无

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data":{
    "version":"v1.0.0"
  }
}
```

## 缓存操作

### 通过messageId获取消息

#### 请求:

| 名字         | 类型   |  可选  | 举例            | 说明                 |
| ----------- | ------ | ----- | -------------- | -------------------- |
| sessionKey  | String | true  | YourSessionKey | 你的session key      |
| target      | Int    | false | 1234567890     | 获取消息的messageId  |

#### 响应: 

**当该messageId没有被缓存或缓存失效时，返回code 5(指定对象不存在)**

```json5
{
  "code":0,
  "msg":"",
  "data":{
    "type":"FriendMessage",         // 消息类型：GroupMessage或FriendMessage或TempMessage或各类Event
    "messageChain":[                // 消息链，是一个消息对象构成的数组
    {
      "type":"Source",
      "id":123456,
      "time":123456789
    },
    {
      "type":"Plain",
      "text":"Miral牛逼"
    }
    ],
    "sender":{                    // 发送者信息
      "id":1234567890,            // 发送者的QQ号码
      "nickname":"",              // 发送者的昵称
      "remark":""                 // 发送者的备注
    }
  }
}
```

## 获取账号信息

### 获取好友列表

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | true  | YourSessionKey | 你的session key |

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data":[
    {
      "id":123456789,
      "nickname":"",
      "remark":""
    },
    {
      "id":987654321,
      "nickname":"",
      "remark":""
    }
  ]
}
```

### 获取群列表

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | true  | YourSessionKey | 你的session key |

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data":[
    {
      "id":123456789,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    },
    {
      "id":987654321,
      "name":"群名2",
      "permission":"MEMBER"
    }
  ]
}
```

### 获取群成员列表

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | true  | YourSessionKey | 你的session key |
| target     | false | 123456789      | 指定群的群号    |

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data":[
    {
      "id":1234567890,
      "memberName":"",
      "permission":"MEMBER",  // 群成员在群中的权限
      "specialTitle":"群头衔",
      "joinTimestamp":12345678,
      "lastSpeakTimestamp":8765432,
      "muteTimeRemaining":0,
      "group":{
        "id":12345,
        "name":"群名1",
        "permission":"MEMBER" // bot 在群中的权限
      }
    },
    {
      "id":9876543210,
      "memberName":"",
      "specialTitle":"群头衔",
      "permission":"OWNER",
      "joinTimestamp":12345678,
      "lastSpeakTimestamp":8765432,
      "muteTimeRemaining":0,
      "group":{
        "id":54321,
        "name":"群名2",
        "permission":"MEMBER"
      }
    }
  ]
}
```
------

### 获取Bot资料

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | true  | YourSessionKey | 你的session key |

#### 响应:

```json5
{
  "nickname":"nickname",
  "email":"email",
  "age":18,
  "level":1,
  "sign":"mirai",
  "sex":"UNKNOWN" // UNKNOWN, MALE, FEMALE
}
```

### 获取好友资料

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | true  | YourSessionKey | 你的session key |
| target     | false | 123456789      | 指定群的群号    |

#### 响应:

```json5
{
  "nickname":"nickname",
  "email":"email",
  "age":18,
  "level":1,
  "sign":"mirai",
  "sex":"UNKNOWN" // UNKNOWN, MALE, FEMALE
}
```

### 获取群成员资料

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | true  | YourSessionKey | 你的session key |
| group      | false | 123456789      | 指定群的群号    |
| member     | false | 987654321      | 群成员QQ号码    |

#### 响应:

```json5
{
  "nickname":"nickname",
  "email":"email",
  "age":18,
  "level":1,
  "sign":"mirai",
  "sex":"UNKNOWN" // UNKNOWN, MALE, FEMALE
}
```

## 消息发送与撤回

### 发送好友消息

#### 请求:

```json5
{
  "sessionKey":"YourSession",
  "target":987654321,
  "messageChain":[
    { "type":"Plain", "text":"hello\n" },
    { "type":"Plain", "text":"world" },
	{ "type":"Image", "url":"https://i0.hdslb.com/bfs/album/67fc4e6b417d9c68ef98ba71d5e79505bbad97a1.png" }
  ]
}
```

| 名字          | 类型   | 可选   | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| target       | Long   | true  | 987654321   | 可选，发送消息目标好友的QQ号           |
| qq           | Long   | true  | 987654321   | 可选，target与qq中需要有一个参数不为空，当target不为空时qq将被忽略，同target  |
| quote        | Int    | true  | 135798642   | 引用一条消息的messageId进行回复  |
| messageChain | Array  | false | []          | 消息链，是一个消息对象构成的数组 |

#### 响应:

```json5
{
  "code":0,
  "msg":"success",
  "messageId":1234567890 // 一个Int类型属性，标识本条消息，用于撤回和引用回复
}
```

### 发送群消息

#### 请求:

```json5
{
  "sessionKey":"YourSession",
  "target":987654321,
  "messageChain":[
    { "type":"Plain", "text":"hello\n" },
    { "type":"Plain", "text":"world" },
    { "type":"Image", "url":"https://i0.hdslb.com/bfs/album/67fc4e6b417d9c68ef98ba71d5e79505bbad97a1.png" }
  ]
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| target       | Long   | true  | 987654321   | 可选，发送消息目标群的群号        |
| group        | Long   | true  | 987654321   | 可选，target与group中需要有一个参数不为空，当target不为空时group将被忽略，同target |
| quote        | Int    | true  | 135798642   | 引用一条消息的messageId进行回复  |
| messageChain | Array  | false | []          | 消息链，是一个消息对象构成的数组 |

#### 响应:

```json5
{
  "code":0,
  "msg":"success",
  "messageId":1234567890 // 一个Int类型属性，标识本条消息，用于撤回和引用回复
}
```

### 发送临时会话消息

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "qq":1413525235,
  "group":987654321,
  "messageChain":[
    { "type":"Plain", "text":"hello\n" },
    { "type":"Plain", "text":"world" }
  ]
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| qq           | Long   | false | 987654321   | 临时会话对象QQ号 |
| group        | Long   | false | 987654321   | 临时会话群号 |
| quote        | Int    | true  | 135798642   | 引用一条消息的messageId进行回复  |
| messageChain | Array  | false | []          | 消息链，是一个消息对象构成的数组 |

#### 响应: 

```json5
{
  "code":0,
  "msg":"success",
  "messageId":1234567890 // 一个Int类型属性，标识本条消息，用于撤回和引用回复
}
```

### 发送头像戳一戳消息

#### 请求:
```json5
{
  "sessionKey":"YourSessionKey",
  "target":123456,
  "subject":654321,
  "kind":"Group"
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | true  | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 戳一戳的目标, QQ号, 可以为 bot QQ号      |
| subject           | false | Long    | 987654321        | 戳一戳接受主体(上下文), 戳一戳信息会发送至该主体, 为群号/好友QQ号 |
| kind              | false | String  | "Group"          | 上下文类型, 可选值 `Friend`, `Group`, `Stranger` |

#### 响应:

```json5
{
    "code": 0,
    "msg": "success"
}
```

### 撤回消息

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "target":987654321
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| target       | Int    | false | 987654321   | 需要撤回的消息的messageId        |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

## 账号管理

### 删除好友

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":1234567890
}
```

| 名字        | 类型    | 可选   | 举例            | 说明                 |
| ---------- | ------ | ----- | -------------- | -------------------- |
| sessionKey | String | true  | YourSessionKey | 你的session key      |
| target     | Long   | false | 1234567890     | 删除好友的QQ号码  |

#### 响应: 

```json5
{
  "code":0,
  "msg":""
}
```

## 群管理

### 禁言群成员

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":123456789,
  "memberId":987654321,
  "time":1800
}
```

| 名字       | 可选  | 类型   | 举例             | 说明                                  |
| ---------- | ----- | ------ | ---------------- | ------------------------------------- |
| sessionKey | true  | String | "YourSessionKey" | 你的session key                       |
| target     | false | Long   | 123456789        | 指定群的群号                          |
| memberId   | false | Long   | 987654321        | 指定群员QQ号                          |
| time       | true  | Int    | 1800             | 禁言时长，单位为秒，最多30天，默认为0 |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 解除群成员禁言

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":123456789,
  "memberId":987654321
}
```

#### 响应

```json5
{
  "code":0,
  "msg":"success"
}
```

### 移除群成员

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":123456789,
  "memberId":987654321,
  "msg":"您已被移出群聊"
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | true  | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 指定群的群号    |
| memberId   | false | Long   | 987654321        | 指定群员QQ号    |
| msg        | true  | String | ""               | 信息            |

#### 响应

```json5
{
  "code":0,
  "msg":"success"
}
```

### 退出群聊

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":123456789
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | true  | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 退出的群号    |

#### 响应

```json5
{
  "code":0,
  "msg":"success"
}
```

> bot为该群群主时退出失败并返回code 10(无操作权限)

### 全体禁言

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":123456789
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | true  | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 指定群的群号    |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 解除全体禁言

#### 请求:

同全体禁言

#### 响应

同全体禁言

### 设置群精华消息

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":1234567
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | true  | String | "YourSessionKey" | 你的session key |
| target     | false | Int   | 1234567           | 精华消息的messageId |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 获取群设置

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | true  | String  | YourSessionKey   | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |

#### 响应

```json5
{
  "name":"群名称",
  "announcement":"群公告",
  "confessTalk":true,
  "allowMemberInvite":true,
  "autoApprove":true,
  "anonymousChat":true
}
```

### 修改群设置

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "target":123456789,
  "config":{
    "name":"群名称",
    "announcement":"群公告",
    "confessTalk":true,
    "allowMemberInvite":true,
    "autoApprove":true,
    "anonymousChat":true
  }
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | true  | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| config            | false | Object  | {}               | 群设置               |
| name              | true  | String  | "Name"           | 群名                 |
| announcement      | true  | String  | "Announcement"   | 群公告               |
| confessTalk       | true  | Boolean | true             | 是否开启坦白说       |
| allowMemberInvite | true  | Boolean | true             | 是否允许群员邀请     |
| autoApprove       | true  | Boolean | true             | 是否开启自动审批入群 |
| anonymousChat     | true  | Boolean | true             | 是否允许匿名聊天     |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 获取群员设置

使用此方法获取群员资料

```
[Get] /memberInfo?sessionKey=YourSessionKey&target=123456789
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | true | String  | YourSessionKey   | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| memberId          | false | Long    | 987654321        | 群员QQ号             |


#### 响应:

```json5
{
  "id":987654321,
  "memberName":"群名片",
  "specialTitle":"群头衔",
  "permission":"OWNER",
  "joinTimestamp":12345678,
  "lastSpeakTimestamp":8765432,
  "muteTimeRemaining":0,
  "group":{
    "id":12345,
    "name":"群名1",
    "permission":"MEMBER" // bot 在群中的权限
  }
}
```

### 修改群员设置

使用此方法修改群员资料（需要有相关限权）

```
[POST] /memberInfo
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
    "memberId": 987654321,
    "info": {
        "name": "群名片",
        "specialTitle": "群头衔"
    }
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | true  | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| memberId          | false | Long    | 987654321        | 群员QQ号             |
| info              | false | Object  | {}               | 群员资料             |
| name              | true  | String  | "Name"           | 群名片，即群昵称     |
| specialTitle      | true  | String  | "Title"          | 群头衔               |

#### 响应: 返回统一状态码

```json5
{
  "code":0,
  "msg":"success"
}
```

## 事件处理

### 添加好友申请

处理 [添加好友申请事件(NewFriendRequestEvent)](EventType.md#添加好友申请)

```json5
{
  "sessionKey":"YourSessionKey",
  "eventId":12345678,
  "fromId":123456,
  "groupId":654321,
  "operate":0,
  "message":""
}
```

| 名字       | 类型   | 说明                          |
| ---------- | ------ | ----------------------------- |
| sessionKey | String | session key                   |
| eventId    | Long   | 响应申请事件的标识            |
| fromId     | Long   | 事件对应申请人QQ号            |
| groupId    | Long   | 事件对应申请人的群号，可能为0 |
| operate    | Int    | 响应的操作类型                |
| message    | String | 回复的信息                    |

| operate | 说明                                               |
| ------- | -------------------------------------------------- |
| 0       | 同意添加好友                                       |
| 1       | 拒绝添加好友                                       |
| 2       | 拒绝添加好友并添加黑名单，不再接收该用户的好友申请 |

### 用户入群申请（Bot需要有管理员权限）

处理 [用户入群申请事件(MemberJoinRequestEvent)](EventType.md#用户入群申请（Bot需要有管理员权限）)

```json
{
  "sessionKey":"YourSessionKey",
  "eventId":12345678,
  "fromId":123456,
  "groupId":654321,
  "operate":0,
  "message":""
}
```

| 名字       | 类型   | 说明                          |
| ---------- | ------ | ----------------------------- |
| sessionKey | String | session key                   |
| eventId    | Long   | 响应申请事件的标识            |
| fromId     | Long   | 事件对应申请人QQ号            |
| groupId    | Long   | 事件对应申请人的群号           |
| operate    | Int    | 响应的操作类型                |
| message    | String | 回复的信息                    |

| operate | 说明                                           |
| ------- | ---------------------------------------------- |
| 0       | 同意入群                                       |
| 1       | 拒绝入群                                       |
| 2       | 忽略请求                                       |
| 3       | 拒绝入群并添加黑名单，不再接收该用户的入群申请 |
| 4       | 忽略入群并添加黑名单，不再接收该用户的入群申请 |

### Bot被邀请入群申请

处理 [Bot被邀请入群申请事件(BotInvitedJoinGroupRequestEvent)](EventType.md#Bot被邀请入群申请)

```json
{
  "sessionKey":"YourSessionKey",
  "eventId":12345678,
  "fromId":123456,
  "groupId":654321,
  "operate":0,
  "message":""
}
```

| 名字       | 类型   | 说明                          |
| ---------- | ------ | ----------------------------- |
| sessionKey | String | session key                   |
| eventId    | Long   | 事件标识            |
| fromId     | Long   | 邀请人（好友）的QQ号            |
| groupId    | Long   | 被邀请进入群的群号 |
| operate    | Int    | 响应的操作类型                |
| message    | String | 回复的信息                    |

| operate | 说明                                           |
| ------- | ---------------------------------------------- |
| 0       | 同意邀请                                       |
| 1       | 拒绝邀请                                       |
