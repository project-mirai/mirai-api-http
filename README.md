<div align="center">
   <img width="160" src="http://img.mamoe.net/2020/02/16/a759783b42f72.png" alt="logo"></br>

   <img width="95" src="http://img.mamoe.net/2020/02/16/c4aece361224d.png" alt="title">

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



## 开始使用
0. 请首先运行[Mirai-console](https://github.com/mamoe/mirai-console)相关客户端生成plugins文件夹
1. 将`mirai-api-http`生成的`jar包文件`放入`plugins`文件夹中
2. 编辑`plugins/MiraiAPIHTTP/setting.yml`配置文件
3. 再次启动[Mirai-console](https://github.com/mamoe/mirai-console)相关客户端
4. 记录日志中出现的`authKey`

```yaml
## 该配置为全局配置，对所有Session有效

# 可选，默认值为8080
port: 8080          

# 可选，默认由插件随机生成，建议手动指定
authKey: 1234567890  

# 可选，缓存大小，默认4096.缓存过小会导致引用回复与撤回消息失败
cacheSize: 4096

# 可选，是否开启websocket，默认关闭，建议通过Session范围的配置设置
enableWebsocket: false

# 可选，配置CORS跨域，默认为*，即允许所有域名
cors: 
  - '*'
```



## API-HTTP插件相关

### 获取插件信息

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




## 认证相关

### 开始会话-认证(Authorize)

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

#### 响应: 返回(成功):

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

#### 响应: 返回统一状态码（后续不再赘述）

```json5
{
    "code": 0,
    "msg": "success"
}
```

| 状态码 | 原因                                |
| ------ | ----------------------------------- |
| 0      | 正常                                |
| 1      | 错误的auth key                      |
| 2      | 指定的Bot不存在                     |
| 3      | Session失效或不存在                 |
| 4      | Session未认证(未激活)               |
| 5      | 发送消息目标不存在(指定对象不存在)  |
| 6      | 指定文件不存在，出现于发送本地图片  |
| 10     | 无操作权限，指Bot没有对应操作的限权 |
| 20     | Bot被禁言，指Bot当前无法向指定群发送消息 |
| 30     | 消息过长                           |
| 400    | 错误的访问，如参数错误等            |



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

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```
> SessionKey与Bot 对应错误时将会返回状态码2：指定的Bot不存在




## 消息相关


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
        { "type": "Plain", "text":"hello\n" },
        { "type": "Plain", "text":"world" }
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

#### 响应: 返回统一状态码（并携带messageId）

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

使用此方法向指定好友发送消息

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

#### 响应: 返回统一状态码（并携带messageId）

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
        { "type": "Plain", "text":"hello\n" },
        { "type": "Plain", "text":"world" }
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

#### 响应: 返回统一状态码（并携带messageId）

```json5
{
    "code": 0,
    "msg": "success",
    "messageId": 1234567890 // 一个Int类型属性，标识本条消息，用于撤回和引用回复
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

#### 响应: 图片的imageId数组

```json5
[
    "{XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX}.mirai",
    "{YYYYYYYY-YYYY-YYYY-YYYY-YYYYYYYYYYYY}.mirai"
]
```



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


#### 响应: 图片的imageId（好友图片与群聊图片Id不同）

```json5
{
    "imageId": "{XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX}.mirai",
    "url": "xxxxxxxxxxxxxxxxxxxx",
    "path": "xxxxxxxxxx"
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

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```



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

#### 响应: 返回JSON对象

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

#### 响应: 返回JSON对象

**当该messageId没有被缓存或缓存失效时，返回code 5(指定对象不存在)**

```json5
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

#### 响应: 返回JSON对象

```json5
{
    "code": 0,
    "errorMessage": "",
    "data": 520,   
}
```



### 事件类型一览
[事件类型一览](EventType.md)

> 事件为Bot被动接收的信息，无法主动构建


### 消息类型一览

#### 消息是构成消息链的基本对象，目前支持的消息类型有

+ [x] At，@消息
+ [x] AtAll，@全体成员
+ [x] Face，表情消息
+ [x] Plain，文字消息
+ [x] Image，图片消息
+ [x] Xml，Xml卡片消息
+ [x] Json，Json卡片消息
+ [x] App，小程序消息
+ [ ] 敬请期待

[消息类型一览](MessageType.md)



## 管理相关

### 获取好友列表

使用此方法获取bot的好友列表

```
[GET] /friendList?sessionKey=YourSessionKey
```

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | false | YourSessionKey | 你的session key |

#### 响应: 返回JSON对象

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

#### 响应: 返回JSON对象

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
[GET] /memberList?sessionKey=YourSessionKey
```

#### 请求:

| 名字       | 可选  | 举例           | 说明            |
| ---------- | ----- | -------------- | --------------- |
| sessionKey | false | YourSessionKey | 你的session key |
| target     | false | 123456789      | 指定群的群号    |

#### 响应: 返回JSON对象

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



### 群全体禁言

使用此方法令指定群进行全体禁言（需要有相关限权）

```
[POST] /muteAll
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | false | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 指定群的群号    |


#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```



### 群解除全体禁言

使用此方法令指定群解除全体禁言（需要有相关限权）

```
[POST] /unmuteAll
```

#### 请求:

同全体禁言

#### 响应

同全体禁言



### 群禁言群成员

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



### 群解除群成员禁言

使用此方法令指定群解除全体禁言（需要有相关限权）

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

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```



### 退出群聊

使用此方法使Bot退出群聊

```
[POST] /kick
```

#### 请求:

```json5
{
    "sessionKey": "YourSessionKey",
    "target": 123456789,
}
```

| 名字       | 可选  | 类型   | 举例             | 说明            |
| ---------- | ----- | ------ | ---------------- | --------------- |
| sessionKey | false | String | "YourSessionKey" | 你的session key |
| target     | false | Long   | 123456789        | 群出的群号    |

#### 响应

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```

> bot为该群群主时退出失败并返回code 10(无操作权限)


### 群设置

使用此方法修改群设置（需要有相关限权）

```
[POST] /groupConfig
```

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
| announcement      | true  | Boolean | true             | 群公告               |
| confessTalk       | true  | Boolean | true             | 是否开启坦白说       |
| allowMemberInvite | true  | Boolean | true             | 是否运行群员邀请     |
| autoApprove       | true  | Boolean | true             | 是否开启自动审批入群 |
| anonymousChat     | true  | Boolean | true             | 是否允许匿名聊天     |

#### 响应: 返回统一状态码

```json5
{
    "code": 0,
    "msg": "success"
}
```



### 获取群设置

使用此方法获取群设置

```
[Get] /groupConfig?sessionKey=YourSessionKey&target=123456789
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
    "specialTitle": "群头衔"
}
```



## Websocket

### 获取消息

监听该接口，插件将推送Bot收到的消息

```
[ws] /message?sessionKey=YourSessionKey
```

#### 请求:

| 名字              | 可选  | 类型    | 举例             | 说明                 |
| ----------------- | ----- | ------- | ---------------- | -------------------- |
| sessionKey        | false | String  | YourSessionKey   | 你的session key      |

#### 响应

```josn5
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


## 获取事件

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


## 获取事件和消息

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



## 配置相关

### 获取指定Session的配置

使用此方法获取指定Session的配置信息，注意该配置是Session范围有效

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

### 设置指定Session的配置

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

## 插件相关、Console相关

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
    "description": "用于登录",
    "usage": "/login qq password",
}
```

| 名字        | 可选  | 类型   | 举例          | 说明                             |
| ----------- | ----- | ------ | ------------- | -------------------------------- |
| authKey     | false | String | "YourAuthKey" | 你的authKey                      |
| name        | false | String | "login"       | 指令名                           |
| alias       | false | String[] | -           | 群员QQ号                         |
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
    "name": "login",
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



### 获取Mangers

```
[GET] /managers?qq=123456
```

#### 响应

```json5
[123456789, 987654321]
```

> 响应Manager的qq号数组，当QQ号不存在时返回状态码(StateCode 2)

## 其他服务

- [心跳服务](docs/heartbeat.md)

- [上报服务](docs/report.md)
