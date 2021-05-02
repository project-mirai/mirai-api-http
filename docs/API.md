# API 文档参考
## 状态码
绝大部分API返回的数据包含一个`code`状态码,分别代表不同的状况.  
所有API返回状态码的意义都一致.  
以下是状态码表:  
| 状态码 | 原因                                |
| ------ | ----------------------------------- |
| 0      | 正常                                |
| 1      | 错误的auth key                      |
| 2      | 指定的Bot不存在                     |
| 3      | Session失效或不存在                 |
| 4      | Session未认证(未激活)               |
| 5      | 发送消息目标不存在(指定对象不存在)  |
| 6      | 指定文件不存在，出现于发送本地图片  |
| 10     | 无操作权限，指Bot没有对应操作的权限 |
| 20     | Bot被禁言，指Bot当前无法向指定群发送消息 |
| 30     | 消息过长                           |
| 400    | 错误的访问，如参数错误等            |
------
## 获取插件信息

```
[GET] /about
```

使用此方法获取插件的信息，如版本号

#### 响应

```json
{
    "code": 0,
    "errorMessage": "",
    "data": {
        "version": "v1.0.0"
    }
}
```
------
## 认证与会话

### 开始认证

```
[POST] /auth
```
使用此方法验证你的身份，并返回一个会话

#### 请求:

```json5
{
    "authKey": "U9HSaDXl39ksd918273hU"
}
```

| 名字    | 类型   | 可选  | 举例                    | 说明                                                       |
| ------- | ------ | ----- | ----------------------- | ---------------------------------------------------------- |
| authKey | String | false | "U9HSaDXl39ksd918273hU" | 创建Mirai-Http-Server时生成的key，可在启动时指定或随机生成 |

#### 响应: 

```json5
{
    "code": 0,
    "session": "UnVerifiedSession"
}
```

| 名字    | 类型   | 举例                | 说明            |
| ------- | ------ | ------------------- | --------------- |
| code    | Int    | 0                   | 返回状态码      |
| session | String | "UnVerifiedSession" | 你的session key |

#### 状态码:

| 代码 | 原因                          |
| ---- | ----------------------------- |
| 0    | 正常                          |
| 1    | 错误的MIRAI API HTTP auth key |

 session key 是使用以下方法必须携带的
 session key 使用前必须进行校验和绑定指定的Bot，**每个Session只能绑定一个Bot，但一个Bot可有多个Session**
 session Key 在未进行校验的情况下，一定时间后将会被自动释放


### 校验Session

```
[POST] /verify
```

使用此方法校验并激活你的Session，同时将Session与一个**已登录**的Bot绑定

#### 请求:

```json5
{
    "sessionKey": "UnVerifiedSession",
    "qq": 123456789
}
```

| 名字       | 类型   | 可选  | 举例                | 说明                       |
| ---------- | ------ | ----- | ------------------- | -------------------------- |
| sessionKey | String | false | "UnVerifiedSession" | 你的session key            |
| qq         | Long   | false | 123456789           | Session将要绑定的Bot的qq号 |

#### 响应: 

```json5
{
    "code": 0,
    "msg": "success"
}
```


### 释放Session

```
[POST] /release
```

使用此方式释放session及其相关资源（Bot不会被释放）
**不使用的Session应当被释放，长时间（30分钟）未使用的Session将自动释放，否则Session持续保存Bot收到的消息，将会导致内存泄露(开启websocket后将不会自动释放)**

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "qq": 123456789
}
```

| 名字       | 类型   | 可选  | 举例             | 说明                       |
| ---------- | ------ | ----- | -----------------| -------------------------- |
| sessionKey | String | false | "YourSessionKey" | 你的session key            |
| qq         | Long   | false | 123456789        | 与该Session绑定Bot的QQ号码 |

#### 响应: 

```json5
{
    "code": 0,
    "msg": "success"
}
```
> SessionKey与Bot 对应错误时将会返回状态码2：指定的Bot不存在
------
## 消息发送与撤回

### 发送好友消息

```
[POST] /sendFriendMessage
```

使用此方法向指定好友发送消息

#### 请求

```json5
{
    "sessionKey": "YourSession",
    "target": 987654321,
    "messageChain": [
        { "type": "Plain", "text": "hello\n" },
        { "type": "Plain", "text": "world" },
	{ "type": "Image", "url": "https://i0.hdslb.com/bfs/album/67fc4e6b417d9c68ef98ba71d5e79505bbad97a1.png" }
    ]
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session                |
| target       | Long   | true(false) | 987654321   | 可选，发送消息目标好友的QQ号           |
| qq           | Long   | true  | 987654321   | 可选，target与qq中需要有一个参数不为空，当target不为空时qq将被忽略，同target  |
| quote        | Int    | true  | 135798642   | 引用一条消息的messageId进行回复  |
| messageChain | Array  | false | []          | 消息链，是一个消息对象构成的数组 |

#### 响应: 

```json5
{
    "code": 0,
    "msg": "success",
    "messageId": 1234567890 // 一个Int类型属性，标识本条消息，用于撤回和引用回复
}
```


### 发送临时会话消息

```
[POST] /sendTempMessage
```

使用此方法向临时会话对象发送消息

#### 请求

```json5
{
    "sessionKey": "YourSession",
    "qq": 1413525235,
    "group": 987654321,
    "messageChain": [
        { "type": "Plain", "text":"hello\n" },
        { "type": "Plain", "text":"world" }
    ]
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session                |
| qq           | Long   | false | 987654321   | 临时会话对象QQ号 |
| group        | Long   | false | 987654321   | 临时会话群号 |
| quote        | Int    | true  | 135798642   | 引用一条消息的messageId进行回复  |
| messageChain | Array  | false | []          | 消息链，是一个消息对象构成的数组 |

#### 响应: 

```json5
{
    "code": 0,
    "msg": "success",
    "messageId": 1234567890 // 一个Int类型属性，标识本条消息，用于撤回和引用回复
}
```

### 发送群消息

```
[POST] /sendGroupMessage
```

使用此方法向指定群发送消息

#### 请求

```json5
{
    "sessionKey": "YourSession",
    "target": 987654321,
    "messageChain": [
        { "type": "Plain", "text": "hello\n" },
        { "type": "Plain", "text": "world" },
	{ "type": "Image", "url": "https://i0.hdslb.com/bfs/album/67fc4e6b417d9c68ef98ba71d5e79505bbad97a1.png" }
    ]
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session                |
| target       | Long   | false(true)  | 987654321   | 可选，发送消息目标群的群号        |
| group        | Long   | true  | 987654321   | 可选，target与group中需要有一个参数不为空，当target不为空时group将被忽略，同target |
| quote        | Int    | true  | 135798642   | 引用一条消息的messageId进行回复  |
| messageChain | Array  | false | []          | 消息链，是一个消息对象构成的数组 |

#### 响应:

```json5
{
    "code": 0,
    "msg": "success",
    "messageId": 1234567890 // 一个Int类型属性，标识本条消息，用于撤回和引用回复
}
```

### 撤回消息

```
[POST] /recall
```

使用此方法撤回指定消息。对于bot发送的消息，有2分钟时间限制。对于撤回群聊中群员的消息，需要有相应权限

#### 请求

```json5
{
    "sessionKey": "YourSession",
    "target": 987654321
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                             |
| ------------ | ------ | ----- | ----------- | -------------------------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session                |
| target       | Int    | false | 987654321   | 需要撤回的消息的messageId        |

#### 响应:

```json5
{
    "code": 0,
    "msg": "success"
}
```


### 发送图片消息（通过URL）

```
[POST] /sendImageMessage
```

使用此方法向指定对象（群或好友）发送图片消息
**除非需要通过此手段获取imageId，否则不推荐使用该接口**

#### 请求

```json5
{
    "sessionKey": "YourSession",
    "target": 987654321,
    "qq": 1234567890,
    "group": 987654321,
    "urls": [
        "https://xxx.yyy.zzz/",
        "https://aaa.bbb.ccc/"
    ]
}
```

| 名字         | 类型   | 可选  | 举例        | 说明                               |
| ------------ | ------ | ----- | ----------- | ---------------------------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session                  |
| target       | Long   | true  | 987654321   | 发送对象的QQ号或群号，可能存在歧义 |
| qq           | Long   | true  | 123456789   | 发送对象的QQ号                     |
| group        | Long   | true  | 987654321   | 发送对象的群号                     |
| urls         | Array  | false | []          | 是一个url字符串构成的数组          |

> 当qq和group同时存在时，表示发送临时会话图片，qq为临时会话对象QQ号，group为临时会话发起的群号

#### 响应: 
一个包含图片imageId的数组:
```json5
[
    "{XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX}.mirai",
    "{YYYYYYYY-YYYY-YYYY-YYYY-YYYYYYYYYYYY}.mirai"
]
```
------
## 多媒体内容上传

**如果发送错误的请求,API将不会返回任何数据,也不会断开连接.** 请确保发送了正确的`multipart`请求.

### 图片文件上传

```
[POST] /uploadImage
```

使用此方法上传图片文件至服务器并返回ImageId

#### 请求

Content-Type：multipart/form-data

| 名字         | 类型   | 可选  | 举例        | 说明                               |
| ------------ | ------ | ----- | ----------- | ---------------------------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session                  |
| type         | String | false | "friend "   | "friend" 或 "group" 或 "temp"        |
| img          | File   | false | -           | 图片文件                           |


#### 响应: 
图片的imageId,好友图片与群聊图片Id不同.

```json5
{
    "imageId": "{XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX}.mirai",
    "url": "xxxxxxxxxxxxxxxxxxxx",
    "path": "xxxxxxxxxx"
}
```



### 语音文件上传
```
[POST] /uploadVoice
```

使用此方法上传语音文件至服务器并返回VoiceId

#### 请求

Content-Type：multipart/form-data

| 名字         | 类型   | 可选  | 举例        | 说明                               |
| ------------ | ------ | ----- | ----------- | ---------------------------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session                  |
| type         | String | false | "group"     | 当前仅支持 "group"                   |
| voice        | File   | false | -           | 语音文件                           |


#### 响应: 

```json5
{
    "voiceId": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.amr", //语音的VoiceId
    "url": "xxxxxxxxxxxxxxxxxxxx",
    "path": "xxxxxxxxxx"
}
```



### 文件上传
```
[POST] /uploadFileAndSend
```

使用此方法上传文件至群/好友并返回FileId

#### 请求

Content-Type：multipart/form-data

| 名字         | 类型   | 可选  | 举例        | 说明                 |
| ------------ | ------ | ----- | ----------- | ----------------- |
| sessionKey   | String | false | YourSession | 已经激活的Session   |
| type         | String | false | "Group"     | 当前仅支持 "Group" |
| target       | Long   | false | 123456      | 指定群的群号 |
| path         | String | false | 文件夹/文件名 | 文件上传目录与名字   |
| file         | File   | false | -           | 文件内容          |


#### 响应:

```json5
{
    "code": 0,
    "msg": "success",
    "id": "/xxx-xxx-xxx-xxx" //文件唯一id
}
```
------
## 接收消息与事件

### 获取Bot收到的消息和事件

```
[GET] /fetchMessage?sessionKey=YourSessionKey&count=10
```

使用此方法获取bot接收到的最老消息和最老各类事件(会从MiraiApiHttp消息记录中删除)

```
[GET] /fetchLatestMessage?sessionKey=YourSessionKey&count=10
```

使用此方法获取bot接收到的最新消息和最新各类事件(会从MiraiApiHttp消息记录中删除)

```
[GET] /peekMessage?sessionKey=YourSessionKey&count=10
```

使用此方法获取bot接收到的最老消息和最老各类事件(不会从MiraiApiHttp消息记录中删除)

```
[GET] /peekLatestMessage?sessionKey=YourSessionKey&count=10
```

使用此方法获取bot接收到的最新消息和最新各类事件(不会从MiraiApiHttp消息记录中删除)

#### 请求:

| 名字       | 可选  | 举例           | 说明                 |
| ---------- | ----- | -------------- | -------------------- |
| sessionKey | false | YourSessionKey | 你的session key      |
| count      | false | 10             | 获取消息和事件的数量 |

#### 响应:

```json5
{
  "code": 0,
  "data": [
    {
      "type": "GroupMessage",        // 消息类型：GroupMessage或FriendMessage或TempMessage或各类Event
      "messageChain": [              // 消息链，是一个消息对象构成的数组
        {
          "type": "Source",
          "id": 123456,
          "time": 123456789
        },
        {
          "type": "Plain",
          "text": "Miral牛逼"
        }
      ],
      "sender": {                      // 发送者信息
          "id": 123456789,             // 发送者的QQ号码
          "memberName": "化腾",        // 发送者的群名片
          "permission": "MEMBER",      // 发送者的群限权：OWNER、ADMINISTRATOR或MEMBER
          "group": {                   // 消息发送群的信息
              "id": 1234567890,        // 发送群的群号
              "name": "Miral Technology", // 发送群的群名称
              "permission": "MEMBER"      // 发送群中，Bot的群限权
          }
      }
    },
    {
      "type": "FriendMessage",         // 消息类型：GroupMessage或FriendMessage或TempMessage或各类Event
      "messageChain": [                // 消息链，是一个消息对象构成的数组
        {
          "type": "Source",
          "id": 123456,
          "time": 123456789
        },
        {
          "type": "Plain",
          "text": "Miral牛逼"
        }
      ],
      "sender": {                      // 发送者信息
          "id": 1234567890,            // 发送者的QQ号码
          "nickname": "",              // 发送者的昵称
          "remark": ""                 // 发送者的备注
      }
    },
    {
      "type": "MemberMuteEvent",       // 消息类型：GroupMessage或FriendMessage或TempMessage或各类Event
      "durationSeconds": 600,
      "member":{
          "id": 123456789,
          "memberName": "禁言对象",
          "permission": "MEMBER",
          "group": {
              "id": 123456789,
              "name": "Miral Technology",
              "permission": "MEMBER"
          }
      },
      "operator":{
          "id": 987654321, 
          "memberName": "群主大人", 
          "permission": "OWNER",
          "group": {
              "id": 123456789,
              "name": "Miral Technology",
              "permission": "MEMBER"
          }
      }
    }
  ]
}
```
### 通过messageId获取一条被缓存的消息

```
[GET] /messageFromId?sessionKey=YourSessionKey&id=1234567890
```

使用此方法获取bot接收到的消息和各类事件

#### 请求:

| 名字       | 可选  | 举例           | 说明                 |
| ---------- | ----- | -------------- | -------------------- |
| sessionKey | false | YourSessionKey | 你的session key      |
| id         | false | 1234567890     | 获取消息的messageId  |

#### 响应: 

**当该messageId没有被缓存或缓存失效时，返回code 5(指定对象不存在)**

```json5
{
    "code": 0,
    "errorMessage": "",
    "data":{
        "type": "FriendMessage",         // 消息类型：GroupMessage或FriendMessage或TempMessage或各类Event
        "messageChain": [                // 消息链，是一个消息对象构成的数组
        {
            "type": "Source",
            "id": 123456,
            "time": 123456789
        },
        {
            "type": "Plain",
            "text": "Miral牛逼"
        }
        ],
        "sender": {                      // 发送者信息
            "id": 1234567890,            // 发送者的QQ号码
            "nickname": "",              // 发送者的昵称
            "remark": ""                 // 发送者的备注
        }
    }
}
```

### 查看缓存的消息总数

```
[GET] /countMessage?sessionKey=YourSessionKey
```

使用此方法获取bot接收并缓存的消息总数，注意不包含被删除的。

#### 请求:

| 名字       | 可选  | 举例           | 说明                 |
| ---------- | ----- | -------------- | -------------------- |
| sessionKey | false | YourSessionKey | 你的session key      |

#### 响应: 

```json5
{
    "code": 0,
    "errorMessage": "",
    "data": 520,   
}
```

### 通过WebSocket

#### 接收消息

监听该接口，插件将推送Bot收到的消息

```
[ws] /message?sessionKey=YourSessionKey
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | YourSessionKey   | 你的session key      |

#### 响应

```json5
{
    "type": "GroupMessage",        // 消息类型：GroupMessage或FriendMessage或TempMessage或各类Event
	"messageChain": [              // 消息链，是一个消息对象构成的数组
      {
	    "type": "Source",
	    "id": 123456,
        "time": 123456789
	  },
      {
        "type": "Plain",
        "text": "Miral牛逼"
      }
    ],
    "sender": {                      // 发送者信息
        "id": 123456789,             // 发送者的QQ号码
        "memberName": "化腾",        // 发送者的群名片
        "permission": "MEMBER",      // 发送者的群限权：OWNER、ADMINISTRATOR或MEMBER
        "group": {                   // 消息发送群的信息
            "id": 1234567890,        // 发送群的群号
            "name": "Miral Technology", // 发送群的群名称
            "permission": "MEMBER"      // 发送群中，Bot的群限权
        }
    }
}
```


#### 接收事件

监听该接口，插件将推送Bot收到的事件

```
[ws] /event?sessionKey=YourSessionKey
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | YourSessionKey   | 你的session key      |

#### 响应

```json
{
    "type": "BotOfflineEventActive",
    "qq": 123456
}
```


#### 同时接收事件与消息

监听该接口，插件将推送Bot收到的事件和消息

```
[ws] /all?sessionKey=YourSessionKey
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | YourSessionKey   | 你的session key      |

#### 响应
参考上文

------
## 好友与群(成员)列表

### 获取好友列表

使用此方法获取bot的好友列表

```
[GET] /friendList?sessionKey=YourSessionKey
```

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | false | YourSessionKey | 你的session key |

#### 响应:

```json5
[
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
```



### 获取群列表

使用此方法获取bot的群列表

```
[GET] /groupList?sessionKey=YourSessionKey
```

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | false | YourSessionKey | 你的session key |

#### 响应:

```json5
[
  {
    "id":123456789,
    "name":"群名1",
    "permission": "MEMBER"
  },
  {
    "id":987654321,
    "name":"群名2",
    "permission": "MEMBER"
  }
]
```



### 获取群成员列表

使用此方法获取bot指定群种的成员列表

```
[GET] /memberList?sessionKey=YourSessionKey&target=123456789
```

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | false | YourSessionKey | 你的session key |
| target     | false | 123456789      | 指定群的群号    |

#### 响应:

```json5
[
  {
    "id":1234567890,
    "memberName":"",
    "permission":"MEMBER",
    "group":{
        "id":12345,
        "name":"群名1",
        "permission": "MEMBER"
    }
  },
  {
    "id":9876543210,
    "memberName":"",
    "permission":"OWNER",
    "group":{
        "id":54321,
        "name":"群名2",
        "permission": "MEMBER"
    }
  }
]
```
------
## 群管理

### 禁言群成员

使用此方法指定群禁言指定群员（需要有相关限权）

```
[POST] /mute
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
    "memberId": 987654321,
    "time": 1800
}
```

| 名字       | 可选  | 类型   | 举例             | 说明                                  |
| ---------- | ----- | ------ | ---------------- | ------------------------------------- |
| sessionKey | false | String | "YourSessionKey" | 你的session key                       |
| target     | false | Long   | 123456789        | 指定群的群号                          |
| memberId   | false | Long   | 987654321        | 指定群员QQ号                          |
| time       | true  | Int    | 1800             | 禁言时长，单位为秒，最多30天，默认为0 |

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```



### 解除群成员禁言

使用此方法指定群解除群成员禁言（需要有相关限权）

```
[POST] /unmute
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
    "memberId": 987654321
}
```

#### 响应

同群禁言群成员

### 移除群成员

使用此方法移除指定群成员（需要有相关限权）

```
[POST] /kick
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
    "memberId": 987654321,
    "msg": "您已被移出群聊"
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | false | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 指定群的群号    |
| memberId   | false | Long   | 987654321        | 指定群员QQ号    |
| msg        | true  | String | ""               | 信息            |

#### 响应

```json5
{
    "code": 0,
    "msg": "success"
}
```



### 退出群聊

使用此方法使Bot退出群聊

```
[POST] /quit
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | false | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 退出的群号    |

#### 响应

```json5
{
    "code": 0,
    "msg": "success"
}
```

> bot为该群群主时退出失败并返回code 10(无操作权限)
> 
### 全体禁言

使用此方法令指定群进行全体禁言（需要有相关限权）

```
[POST] /muteAll
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | false | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 指定群的群号    |


#### 响应

```json5
{
    "code": 0,
    "msg": "success"
}
```



### 解除全体禁言

使用此方法令指定群解除全体禁言（需要有相关限权）

```
[POST] /unmuteAll
```

#### 请求:

同全体禁言

#### 响应

同全体禁言





### 获取群设置

使用此方法获取群设置

```
[GET] /groupConfig?sessionKey=YourSessionKey&target=123456789
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | YourSessionKey   | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |


#### 响应

```json5
{
    "name": "群名称",
    "announcement": "群公告",
    "confessTalk": true,
    "allowMemberInvite": true,
    "autoApprove": true,
    "anonymousChat": true
}
```

### 修改群设置

使用此方法修改群设置（需要有相关限权）

```
[POST] /groupConfig
```
注意:请求头部需要加上`charset=utf-8`,否则可能导致乱码.
#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
    "config": {
        "name": "群名称",
        "announcement": "群公告",
        "confessTalk": true,
        "allowMemberInvite": true,
        "autoApprove": true,
        "anonymousChat": true
    }
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| config            | false | Object  | {}               | 群设置               |
| name              | true  | String  | "Name"           | 群名                 |
| announcement      | true  | String  | "Announcement"   | 群公告               |
| confessTalk       | true  | Boolean | true             | 是否开启坦白说       |
| allowMemberInvite | true  | Boolean | true             | 是否允许群员邀请     |
| autoApprove       | true  | Boolean | true             | 是否开启自动审批入群 |
| anonymousChat     | true  | Boolean | true             | 是否允许匿名聊天     |

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```





### 获取群员资料

使用此方法获取群员资料

```
[Get] /memberInfo?sessionKey=YourSessionKey&target=123456789
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | YourSessionKey   | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| memberId          | false | Long    | 987654321        | 群员QQ号             |


#### 响应

```json5
{
    "name": "群名片",
    "nick": "群员昵称",
    "specialTitle": "群头衔"
}
```

### 修改群员资料

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
| sessionKey        | false | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| memberId          | false | Long    | 987654321        | 群员QQ号             |
| info              | false | Object  | {}               | 群员资料             |
| name              | true  | String  | "Name"           | 群名片，即群昵称     |
| specialTitle      | true  | String  | "Title"          | 群头衔               |

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```
----
## 群文件管理
### 获取群文件列表

```text
[GET] /groupFileList?sessionKey=YourSessionKey&target=123456789&dir=dir
```
#### 请求

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| dir               | true  | String  | dir              | 指定查询目录，不填为根目录          |


#### 响应

```json5
[
  {
    "name" : "File Name",
    "id" : "/xxx-xxx-xxx-xxx",
    "path" : "/path/File Name",
    "isFile" : true //是否为文件
  },
  {
    "name" : "File Name",
    "id" : "/xxx-xxx-xxx-xxx",
    "path" : "/path/File Name",
    "isFile" : true
  }//...
]
```

### 获取群文件详细信息

```text
[GET] /groupFileInfo?sessionKey=YourSessionKey&target=123456789&id=/xxx-xxx-xxx-xxx
```

#### 请求

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 指定群的群号         |
| id                | false | String  | /xxx-xxx-xxx-xxx | 文件唯一ID          |

#### 响应

```json5
{
  "name" : "File Name", //文件名字
  "path" : "/path/File Name", //文件绝对位置
  "id" : "/xxx-xxx-xxx-xxx", //文件唯一ID
  "length" : 0, //文件长度
  "downloadTimes" : 0, //下载次数
  "uploaderId" : 987654321, //上传者QQ
  "uploadTime" : 0, //上传时间
  "lastModifyTime" : 0, //最后修改时间
  "downloadUrl" : "https://www.com", //文件下载链接
  "sha1" : "85ad7a14d51a", //文件sha1值
  "md5" : "d4wa84d6aw1d4ad57" //文件md5值
}
```

### 重命名群文件/目录
```text
[POST] /groupFileRename
```
#### 请求
```json5
{
   "sessionKey": "YourSessionKey",
   "target": 123456,
   "id": "/xxx-xxx-xxx-xxx",
   "rename": "new File Name"
}
```

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```

### 创建群文件目录
```text
[POST] /groupMkdir
```
#### 请求
```json5
{
   "sessionKey": "YourSessionKey",
   "group": 123456,
   "dir": "Dir Name"
}
```

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```

### 移动群文件
```text
[POST] /groupFileMove
```
#### 请求
```json5
{
   "sessionKey": "YourSessionKey",
   "target": 123456,
   "id": "/xxx-xxx-xxx-xxx",
   "movePath": "movePath" //移动到的目录，根目录为/，目录不存在时自动创建
}
```

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```

### 删除群文件/目录
```text
[POST] /groupFileDelete
```
#### 请求
```json5
{
   "sessionKey": "YourSessionKey",
   "target": 123456,
   "id": "/xxx-xxx-xxx-xxx"
}
```

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```

## 设置群精华消息

```text
[POST] /setEssence
```

#### 请求

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | "YourSessionKey" | 你的session key      |
| target            | false | Int     | 123456789        | 将被设置为精华消息ID      |

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```

------

## 戳一戳
```text
[POST] /sendNudge
```
#### 请求:
```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456,
    "subject": 654321,
    "kind": "Group"
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | "YourSessionKey" | 你的session key      |
| target            | false | Long    | 123456789        | 戳一戳的目标, QQ号, 可以为 bot QQ号      |
| subject           | false | Long    | 987654321        | 戳一戳接受主体(上下文), 戳一戳信息会发送至该主体, 为群号/好友QQ号 |
| kind              | false | Enum    | "Group"          | 上下文类型, 可选值 `Friend`, `Group`. |

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```

------

## Session配置

### 获取指定Session的配置

使用此方法获取指定Session的配置信息，注意该配置仅在Session范围有效

```
[GET] /config?sessionKey=YourSessionKey
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | YourSessionKey   | 你的session key      |

#### 响应

```json5
{
    "cacheSize": 4096,
    "enableWebsocket": false
}
```

### 修改指定Session的配置

使用此方法设置指定Session的配置信息，注意该配置是Session范围有效

```
[Post] /config
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "cacheSize": 4096,
    "enableWebsocket": false
}
```

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | "YourSessionKey" | 你的session key      |
| cacheSize         | true  | Int     | 123456789        | 缓存大小             |
| enableWebsocket   | true  | Boolean | false            | 是否开启Websocket    |

------
## 插件与Console

### 简介

`Mirai-console`通过指令执行任务，如`/login qq password`进行登录，`Mirai-api-http`支持通过`POST`请求发送指令和注册指令，
帮助第三方开发语言进行符合`Mirai-console`规范的插件开发

`Mirai-console`通过`manager`列表进行对`bot`任务的鉴权。尽量避免各插件使用自己的鉴权方式而产生重复配置的混乱情况出现。

### 注册指令

```
[POST] /command/register
```

#### 请求:

```json5
{
    "authKey": "U9HSaDXl39ksd918273hU",
    "name": "login",
    "alias": ["lg", "SignIn"],
    "description": "测试",
    "usage": "/login qq password"
}
```

| 名字        | 可选  | 类型   | 举例          | 说明                             |
| ----------- | ----- | ------ | ------------- | -------------------------------- |
| authKey     | false | String | "YourAuthKey" | 你的authKey                      |
| name        | false | String | "login"       | 指令名                           |
| alias       | false | String[] | -           | 指令别名                         |
| description | false | String | ""            | 指令描述                         |
| usage       | true  | String | "Name"        | 指令描述，会在指令执行错误时显示 |

#### 响应

按普通文本处理



### 发送指令

```
[POST] /command/send
```

#### 请求:

```json5
{
    "authKey": "U9HSaDXl39ksd918273hU",
    "name": "ogin",
    "args": ["123", "pwd"]
}
```

| 名字    | 可选  | 类型   | 举例          | 说明        |
| ------- | ----- | ------ | ------------- | ----------- |
| authKey | false | String | "YourAuthKey" | 你的authKey |
| name    | false | String | 123456789     | 指令名      |
| args    | false | String[] | 987654321     | 指令参数    |


#### 响应

按普通文本处理



### 监听指令

```
[ws] /command?authKey=U9HSaDXl39ksd918273hU
```

#### 响应

```json5
{
    "name": "commandName",
    "sender": 12345,
    "group": 54321,
    "args": ["arg1", "arg2"]
}
```

> 当指令通过好友消息发送时，sender为好友QQ号，group为0
>
> 当指令通过群组消息发送时，sender为发送人QQ号，group为群号
>
> 当指令通过其他方式发送时，如控制台、HTTP接口等，sender和group均为0


### 获取Managers

```
[GET] /managers?qq=123456
```

#### 响应

```json5
[123456789, 987654321]
```

> 响应Manager的qq号数组，当QQ号不存在时返回状态码(StateCode 2)
