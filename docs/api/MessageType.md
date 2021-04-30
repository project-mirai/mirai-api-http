## 消息类型

#### Source

```json5
{
    "type": "Source",
    "id": 123456,
    "time": 123456
}
```

| 名字 | 类型 | 说明                                                         |
| ---- | ---- | ------------------------------------------------------------ |
| id   | Int  | 消息的识别号，用于引用回复（Source类型永远为chain的第一个元素） |
| time | Int  | 时间戳                                                       |

#### Quote

```json5
{
    "type": "Quote",
    "id": 123456,
    "groupId": 123456789,
    "senderId": 987654321,
    "targetId": 9876543210,
    "origin": [
        { "type": "Plain", text: "text" }
    ] 
}
```

| 名字     | 类型   | 说明                                              |
| -------- | ------ | ------------------------------------------------- |
| id       | Int    | 被引用回复的原消息的messageId                     |
| groupId  | Long   | 被引用回复的原消息所接收的群号，当为好友消息时为0 |
| senderId | Long   | 被引用回复的原消息的发送者的QQ号                  |
| targetId | Long   | 被引用回复的原消息的接收者者的QQ号（或群号）       |
| origin   | Object | 被引用回复的原消息的消息链对象                    |


#### At

```json5
{
    "type": "At",
    "target": 123456,
    "display": "@Mirai"
}
```

| 名字    | 类型   | 说明                                           |
| ------- | ------ | ---------------------------------------------- |
| target  | Long   | 群员QQ号                                       |
| dispaly | String | At时显示的文字，发送消息时无效，自动使用群名片 |

#### AtAll

```json5
{
    "type": "AtAll"
}
```

| 名字    | 类型   | 说明                      |
| ------- | ------ | ------------------------- |
| -       | -      | -                         |

#### Face

```json5
{
    "type": "Face",
    "faceId": 123,
    "name": "bu"
}
```

| 名字   | 类型    | 说明                           |
| ------ | ------- | ------------------------------ |
| faceId | Int     | QQ表情编号，可选，优先高于name |
| name   | String  | QQ表情拼音，可选               |

#### Plain

```json5
{
    "type": "Plain",
    "text": "Mirai牛逼"
}
```

| 名字 | 类型   | 说明     |
| ---- | ------ | -------- |
| text | String | 文字消息 |

#### Image

```json5
{
    "type": "Image",
    "imageId": "{01E9451B-70ED-EAE3-B37C-101F1EEBF5B5}.mirai",  //群图片格式
    //"imageId": "/f8f1ab55-bf8e-4236-b55e-955848d7069f"      //好友图片格式
    "url": "http://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    "path": null
}
```

| 名字    | 类型   | 说明                                                         |
| ------- | ------ | ------------------------------------------------------------ |
| imageId | String | 图片的imageId，群图片与好友图片格式不同。不为空时将忽略url属性 |
| url     | String | 图片的URL，发送时可作网络图片的链接；接收时为腾讯图片服务器的链接，可用于图片下载 |
| path    | String | 图片的路径，发送本地图片，相对路径于`plugins/MiraiAPIHTTP/images` |

#### FlashImage

```json5
{
    "type": "FlashImage",
    "imageId": "{01E9451B-70ED-EAE3-B37C-101F1EEBF5B5}.mirai",  //群图片格式
    //"imageId": "/f8f1ab55-bf8e-4236-b55e-955848d7069f"      //好友图片格式
    "url": "http://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    "path": null
}
```

同 `Image`

> 三个参数任选其一，出现多个参数时，按照imageId > url > path的优先级

#### Voice

```json5
{
    "type": "Voice",
    "voiceId": "23C477720A37FEB6A9EE4BCCF654014F.amr",
    "url": "http://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    "path": null
}
```

| 名字    | 类型   | 说明                                                         |
| ------- | ------ | ------------------------------------------------------------ |
| voiceId | String | 语音的voiceId，不为空时将忽略url属性 |
| url     | String | 语音的URL，发送时可作网络语音的链接；接收时为腾讯语音服务器的链接，可用于语音下载 |
| path    | String | 语音的路径，发送本地语音，相对路径于`plugins/MiraiAPIHTTP/voices` |

> 三个参数任选其一，出现多个参数时，按照voiceId > url > path的优先级

#### Xml

```json5
{
    "type": "Xml",
    "xml": "XML"
}
```

| 名字 | 类型   | 说明    |
| ---- | ------ | ------- |
| xml  | String | XML文本 |

#### Json

```json5
{
    "type": "Json",
    "json": "{}"
}
```

| 名字 | 类型   | 说明     |
| ---- | ------ | -------- |
| json | String | Json文本 |

#### App

```json5
{
    "type": "App",
    "content": "<>"
}
```

| 名字     | 类型   | 说明    |
| -------- | ------ | ------- |
| content  | String | 内容    |

#### Poke

```json5
{
    "type": "Poke",
    "name": "SixSixSix"
}
```

| 名字 | 类型   | 说明         |
| ---- | ------ | ------------ |
| name | String | 戳一戳的类型 |

1. "Poke": 戳一戳
2. "ShowLove": 比心
3. "Like": 点赞
4. "Heartbroken": 心碎
5. "SixSixSix": 666
6. "FangDaZhao": 放大招

#### Dice

```json5
{
  "type": "Dice",
  "value": 1
}
```

| 名字 | 类型   | 说明         |
| ---- | ------ | ------------ |
| value | Int | 点数 |

#### MusicShare

```json5
{
  "type": "MusicShare",
  "kind": "String",
  "title": "String",
  "summary": "String",
  "jumpUrl": "String",
  "pictureUrl": "String",
  "musicUrl": "String",
  "brief": "String"
}
```

| 名字 | 类型   | 说明         |
| ---- | ------ | ------------ |
| kind | String | 类型 |
| title | String | 标题 |
| summary | String | 概括 |
| jumpUrl | String | 跳转路径 |
| pictureUrl | String | 封面路径 |
| musicUrl | String | 音源路径 |
| brief | String | 简介 |

#### ForwardMessage

```json5
{
  "type": "ForwardMessage",
  "nodes": [
    {
      "sender": 123,
      "time": 0,
      "name": "sender name",
      "messageChain": [],
      "sourceId": 123
    }
  ] 
}
```

| 名字 | 类型   | 说明         |
| ---- | ------ | ------------ |
| nodes | object | 消息节点 |
| sender | Long | 发送人QQ号 |
| time | Int | 发送时间 |
| messageChain | Array | 消息数组 |
| messageId | Int | 可以只使用消息messageId，从缓存中读取一条消息作为节点 |
