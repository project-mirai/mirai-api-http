# API 文档参考

该 API 文档为 `mirai-api-http` 通用接口文档, 对于所有内置的(built-in)接口适配器(adapter)都适用的相同规则.

> 对于因调用逻辑的不同而产生不同接口数据的的地方, 在对应 adapter 文档中会明确指出 

参考阅读:

+ [Adapter: 接口适配器](../adapter/Adapter.md)

<!-- BEGIN DROP directory -->

目录:

+ **[状态码](API.md#状态码)**
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
+ **[文件操作](#文件操作)**
  + [查看文件列表](#查看文件列表)
  + [获取文件信息](#获取文件信息)
  + [创建文件夹](#创建文件夹)
  + [删除文件](#删除文件)
  + [移动文件](#移动文件)
  + [重命名文件](#重命名文件)
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
  + [修改群员管理员](#修改群员管理员)
+ **[群公告](#群公告)**
  + [获取群公告](#获取群公告)
  + [发布群公告](#发布群公告)
  + [删除群公告](#删除群公告)
+ **[事件处理](#事件处理)**
  + [添加好友申请](#添加好友申请)
  + [用户入群申请](#用户入群申请（Bot需要有管理员权限）)
  + [Bot被邀请入群申请](#Bot被邀请入群申请)
+ **[命令(Console Command)](# 命令(Console Command))**
  + [执行命令](#执行命令)
  + [注册命令](#注册命令)
  + [命令接收](#命令接收)

<!-- END DROP directory -->


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

### 获取登录账号

#### 请求:

无

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data":[
    123456789,
    987654321,
    1145141919
  ]
}
```

## 缓存操作

### 通过messageId获取消息

#### 请求:

| 名字         | 类型     |  可选  | 举例            | 说明             |
|------------|--------| ----- | -------------- |----------------|
| sessionKey | String | true  | YourSessionKey | 你的session key  |
| id         | Int    | false | 1234567890     | 获取消息的messageId |
| target     | Long   | false | 1234567890     | 好友id或群id       |

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
| target     | false | 123456789      | 指定好友账号        |

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
| target     | false | 123456789      | 指定群的群号    |
| memberId   | false | 987654321      | 群成员QQ号码    |

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
### 获取QQ用户资料

#### 请求:

| 名字           | 可选      | 举例              | 说明              |
|--------------|---------|-----------------|-----------------|
| sessionKey   | true    | YourSessionKey  | 你的session key   |
| target       | false   | 987654321       | 要查询的QQ号码        |

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
  "target":987654321,
  "messageId":12345
}
```

| 名字         | 类型     | 可选  | 举例          | 说明                |
|------------|--------| ----- |-------------|-------------------|
| sessionKey | String | true  | YourSession | 已经激活的Session      |
| messageId  | Int    | false | 12345       | 需要撤回的消息的messageId |
| target     | Long   | false | 987654321   | 好友id或群id          |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 获取漫游消息

#### 请求
```json5
{
  "timeStart": 0,
  "timeEnd": 0,
  "target": 123456789,
}
```

| 名字           | 类型   | 可选  | 举例  | 说明                                                                                               |
|--------------|------| ----- |-----|--------------------------------------------------------------------------------------------------|
| timeStart    | Long | false  | 0   | 起始时间, UTC+8 时间戳, 单位为秒. 可以为 0, 即表示从可以获取的最早的消息起. 负数将会被看是 0.                                        |
| timeEnd      | Long | false | 0   | 结束时间, UTC+8 时间戳, 单位为秒. 可以为 Long.MAX_VALUE, 即表示到可以获取的最晚的消息为止. 低于 timeStart 的值将会被看作是 timeStart 的值. |
| target       | Long | false | 0   | 漫游消息对象，好友id，目前仅支持好友漫游消息                                                                          |

#### 响应:

```json5
{
  "code":0,
  "msg":"success",
  "data":[]
}
```

> data 为 #[消息链](MessageType.md#消息链类型) 数组

## 文件操作

目前仅支持群文件的操作, 所有好友文件的字段为保留字段

### 查看文件列表

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "id": "",
  "path": null,
  "target":987654321,
  "group":null,
  "qq":null,
  "withDownloadInfo":true,
  "offset": 0,
  "size": 1
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| id           | String | false | ""          | 文件夹id, 空串为根目录            |
| path         | String | true  | null        | 文件夹路径, 文件夹允许重名, 不保证准确, 准确定位使用 id |
| target       | Long   | true  | 987654321   | 群号或好友QQ号                   |
| group        | Long   | true  | 987654321   | 群号                            |
| qq           | Long   | true  | 987654321   | 好友QQ号                        |
| withDownloadInfo | Boolean | true  | true   | 是否携带下载信息，额外请求，无必要不要携带 |
| offset | Long | true  | 1   | 分页偏移 |
| size | Long | true  | 10   | 分页大小 |

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data": [
    {
      "name":"setu.png",
      "id":"/12314d-1wf13-a98ffa",
      "path":"/setu.png",
      "parent":null,
      "contact":{
        "id":123123,
        "name":"setu qun",
        "permission":"OWNER"
      },
      "isFile":true,
      "isDictionary":false,
      "isDirectory":false,
      "sha1":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
      "md5":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
      "downloadTimes":10,
      "uploaderId":123456789,
      "uploadTime":1631153749,
      "lastModifyTime":1631153749,
      "downloadInfo":{
        "sha1":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "md5":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "downloadTimes":10,
        "uploaderId":123456789,
        "uploadTime":1631153749,
        "lastModifyTime":1631153749,
        "url":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
      }
    }
  ]
}
```

| 名字         | 类型   | 说明                             |
| ------------ | ------ | -------------------------------- |
| data         | Array  | 文件对象数组                     |
| data.name    | String | 文件名                          |
| data.id      | String | 文件ID                          |
| data.parent  | Object | 文件对象, 递归类型. null 为存在根目录 |
| data.contact | Object | 群信息或好友信息                  |
| data.contact | Object | 群信息或好友信息                  |
| data.isFile  | Boolean | 是否文件                         |
| data.isDictionary | Boolean | ~~是否文件夹~~(弃用)                  |
| data.isDirectory | Boolean | 是否文件夹                  |
| downloadInfo | Object | 文件下载信息                  |
| downloadInfo.sha1 | String | 文件sha1校验                  |
| downloadInfo.md5 | String | 文件md5校验               |
| downloadInfo.url | String | 文件下载url                  |

### 获取文件信息

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "id": "",
  "path": null,
  "target":987654321,
  "group":null,
  "qq":null,
  "withDownloadInfo":true
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| id           | String | false | ""          | 文件id,空串为根目录               |
| path         | String | true  | null        | 文件夹路径, 文件夹允许重名, 不保证准确, 准确定位使用 id |
| target       | Long   | true  | 987654321   | 群号或好友QQ号                   |
| group        | Long   | true  | 987654321   | 群号                            |
| qq           | Long   | true  | 987654321   | 好友QQ号                        |
| withDownloadInfo | Boolean | true  | true   | 是否携带下载信息，额外请求，无必要不要携带 |

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data": {
    "name":"setu.png",
    "id":"/12314d-1wf13-a98ffa",
    "path":"/setu.png",
    "parent":null,
    "contact":{
      "id":123123,
      "name":"setu qun",
      "permission":"OWNER"
    },
    "isFile":true,
    "isDictionary":false,
    "isDirectory":false,
    "sha1":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    "md5":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    "downloadTimes":10,
    "uploaderId":123456789,
    "uploadTime":1631153749,
    "lastModifyTime":1631153749,
    "downloadInfo":{
      "sha1":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
      "md5":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
      "downloadTimes":10,
      "uploaderId":123456789,
      "uploadTime":1631153749,
      "lastModifyTime":1631153749,
      "url":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    }
  }
}
```

| 名字         | 类型   | 说明                             |
| ------------ | ------ | -------------------------------- |
| data         | Object | 文件信息                     |
| data.name    | String | 文件名                          |
| data.id      | String | 文件ID                          |
| data.parent  | Object | 文件对象, 递归类型. null 为存在根目录 |
| data.contact | Object | 群信息或好友信息                  |
| data.contact | Object | 群信息或好友信息                  |
| data.isFile  | Boolean | 是否文件                         |
| data.isDictionary | Boolean | ~~是否文件夹~~(弃用)                  |
| data.isDirectory | Boolean | 是否文件夹                  |
| downloadInfo | Object | 文件下载信息                  |
| downloadInfo.sha1 | String | 文件sha1校验                  |
| downloadInfo.md5 | String | 文件md5校验               |
| downloadInfo.url | String | 文件下载url                  |

### 创建文件夹

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "id": "",
  "path": null,
  "target":987654321,
  "group":null,
  "qq":null,
  "directoryName": "newDirectoryName"
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| id           | String | false | ""          | 父目录id,空串为根目录             |
| path         | String | true  | null        | 文件夹路径, 文件夹允许重名, 不保证准确, 准确定位使用 id |
| target       | Long   | true  | 987654321   | 群号或好友QQ号                   |
| group        | Long   | true  | 987654321   | 群号                            |
| qq           | Long   | true  | 987654321   | 好友QQ号                        |
| directoryName | String | false  | ""       | 新建文件夹名                     |

#### 响应:

```json5
{
  "code":0,
  "msg":"",
  "data": {
    "name":"setu",
    "id":"/12314d-1wf13-a98ffa",
    "path":"/setu",
    "parent":null,
    "contact":{
      "id":123123,
      "name":"setu qun",
      "permission":"OWNER"
    },
    "isFile":false,
    "isDictionary":true,
    "isDirectory":true
  }
}
```

> 返回新建文件夹的信息

### 上传文件

> 未通用，仅 http 支持

### 删除文件

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "id": "",
  "path": null,
  "target":987654321,
  "group":null,
  "qq":null
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| id           | String | false | ""          | 删除文件id                       |
| path         | String | true  | null        | 文件夹路径, 文件夹允许重名, 不保证准确, 准确定位使用 id |
| target       | Long   | true  | 987654321   | 群号或好友QQ号                   |
| group        | Long   | true  | 987654321   | 群号                            |
| qq           | Long   | true  | 987654321   | 好友QQ号                        |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 移动文件

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "id": "",
  "path": null,
  "target":987654321,
  "group":null,
  "qq":null,
  "moveTo": "/23fff2-3fwe-ga12eds",
  "moveToPath": null
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| id           | String | false | ""          | 移动文件id                       |
| path         | String | false | null        | 文件夹路径, 文件夹允许重名, 不保证准确, 准确定位使用 id |
| target       | Long   | true  | 987654321   | 群号或好友QQ号                   |
| group        | Long   | true  | 987654321   | 群号                            |
| qq           | Long   | true  | 987654321   | 好友QQ号                        |
| moveTo       | String | true  | "/23fff2-3fwe-ga12eds" | 移动目标文件夹id |
| moveToPath   | String | true  | null | 移动目标文件路径, 文件夹允许重名, 不保证准确, 准确定位使用 moveTo |

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 重命名文件

#### 请求

```json5
{
  "sessionKey":"YourSession",
  "id": "",
  "path": null,
  "target":987654321,
  "group":null,
  "qq":null,
  "renameTo": "setu"
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | true  | YourSession | 已经激活的Session                |
| id           | String | false | ""          | 重命名文件id                     |
| path         | String | false | null        | 文件夹路径, 文件夹允许重名, 不保证准确, 准确定位使用 id |
| target       | Long   | true  | 987654321   | 群号或好友QQ号                   |
| group        | Long   | true  | 987654321   | 群号                            |
| qq           | Long   | true  | 987654321   | 好友QQ号                        |
| renameTo     | Long   | true  | 987654321   | 新文件名                        |

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

| 名字         | 可选  | 类型     | 举例               | 说明             |
|------------| ----- |--------|------------------|----------------|
| sessionKey | true  | String | "YourSessionKey" | 你的session key  |
| messageId  | false | Int    | 1234567          | 精华消息的messageId |
| target     | false | Long   | 1234567890       | 群id            |

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

### 修改群员管理员

使用此方法修改群员的管理员权限（需要有群主限权）

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
    "memberId": 987654321,
    "assign": true
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | true  | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| memberId          | false | Long    | 987654321        | 群员QQ号             |
| assign            | false | Boolean | true             | 是否设置为管理员       |

#### 响应: 返回统一状态码

```json5
{
  "code":0,
  "msg":"success"
}
```

## 群公告

### 获取群公告

此方法获取指定群公告列表

#### 请求

| 名字     | 可选    | 类型   | 举例        | 说明        |
|--------|-------|------|-----------|-----------|
| id     | false | Long | 123456789 | 群号        |
| offset | true  | Long | 0         | 分页参数      |
| size   | true  | Long | 10        | 分页参数，默认10 |

#### 响应

```json5
{
  "code": 0,
  "msg": "",
  "data":[
    {
      "group":{"id": 123456789, "name": "group name", "permission": "ADMINISTRATOR"},
      "content": "群公告内容",
      "senderId": 987654321,        // 发布者账号
      "fid": "公告唯一id",
      "allConfirmed": false,        // 是否所有群成员已确认
      "confirmedMembersCount": 0,   // 确认群成员人数
      "publicationTime": 1645085843 // 发布时间
    }
  ]
}
```

### 发布群公告

此方法向指定群发布群公告

#### 请求

```json5
{
  "target": 123456789,
  "content": "测试公告内容",
  "pinned": true
}
```

| 名字                  | 可选    | 类型      | 举例        | 说明              |
|---------------------|-------|---------|-----------|-----------------|
| target              | false | Long    | 123456789 | 群号              |
| content             | false | String  | ""        | 公告内容            |
| sendToNewMember     | true  | Boolean | false     | 是否发送给新成员        |
| pinned              | true  | Boolean | false     | 是否置顶            |
| showEditCard        | true  | Boolean | false     | 是否显示群成员修改群名片的引导 |
| showPopup           | true  | Boolean | false     | 是否自动弹出          |
| requireConfirmation | true  | Boolean | false     | 是否需要群成员确认       |
| imageUrl            | false | String  | null      | 公告图片url         |
| imagePath           | false | String  | null      | 公告图片本地路径        |
| imageBase64         | false | String  | null      | 公告图片base64编码    |

#### 响应

```json5
{
  "code": 0,
  "msg": "",
  "data":[
    {
      "group":{"id": 123456789, "name": "group name", "permission": "ADMINISTRATOR"},
      "content": "群公告内容",
      "senderId": 987654321,        // 发布者账号
      "fid": "公告唯一id",
      "allConfirmed": false,        // 是否所有群成员已确认
      "confirmedMembersCount": 0,   // 确认群成员人数
      "publicationTime": 1645085843 // 发布时间
    }
  ]
}
```

### 删除群公告

此方法删除指定群中一条公告

#### 请求

```json5
{
  "id": 123456789,
  "fid": "群公告唯一id",
}
```

| 名字  | 可选    | 类型      | 举例        | 说明              |
|-----|-------|---------|-----------|-----------------|
| id  | false | Long    | 123456789 | 群号              |
| fid | false | String  | ""        | 群公告唯一id         |

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


## 命令(Console Command)

### 执行命令

#### 请求:

```json5
{
  "sessionKey":"YourSessionKey",
  "command":[]
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | true  | String | "YourSessionKey" | 你的session key |
| command    | false | Array  | []               | 命令与参数    |

> console 支持以不同消息类型作为指令的参数, 执行命令需要以消息类型作为参数, 若执行纯文本的命令, 构建多个 `Plain` 格式的消息
> console 会将第一个消息作为指令名, 后续消息作为参数
> 具体参考 console 文档

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 注册命令

#### 请求:

```json5
{
  "name":"shutdown",
  "alias":["close", "exit"],
  "usage":"/shutdown <nil>",
  "description":"Shutdown console."
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | true  | String | "YourSessionKey" | 你的session key |
| name      | false | String  | "shutdown"       | 指令名    |
| alias     | true  | Array  | []                | 指令别名    |
| usage     | false  | String  | ""              | 使用说明    |
| description | false  | String  | ""            | 命令描述    |

> 注册的指令会直接覆盖已有的指令(包括 console 内置的指令)

#### 响应:

```json5
{
  "code":0,
  "msg":"success"
}
```

### 命令接收

命令被调用时, 会触发 [CommandExecutedEvent](EventType.md#命令被执行)
