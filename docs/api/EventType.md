# 事件类型一览

<!-- BEGIN DROP directory -->


## 目录

+ **[事件类型一览](EventType.md)**
  + [Bot自身事件](#bot自身事件)
  + [好友事件](#好友事件)
  + [群事件](#群事件)
  + [申请事件](#申请事件)
  + [其他客户端事件](#其他客户端事件)
  + [命令事件](#命令事件)

<!-- END DROP directory -->


## Bot自身事件

### Bot登录成功

```json5
{
  "type":"BotOnlineEvent",
  "qq":123456
}
```

| 名字 | 类型 | 说明                |
| ---- | ---- | ------------------- |
| qq   | Long | 登录成功的Bot的QQ号 |



### Bot主动离线

```json5
{
  "type":"BotOfflineEventActive",
  "qq":123456
}
```

| 名字 | 类型 | 说明                |
| ---- | ---- | ------------------- |
| qq   | Long | 主动离线的Bot的QQ号 |



### Bot被挤下线

```json5
{
  "type":"BotOfflineEventForce",
  "qq":123456
}
```

| 名字 | 类型 | 说明                |
| ---- | ---- | ------------------- |
| qq   | Long | 被挤下线的Bot的QQ号 |



### Bot被服务器断开或因网络问题而掉线

```json5
{
  "type":"BotOfflineEventDropped",
  "qq":123456
}
```

| 名字 | 类型 | 说明                                      |
| ---- | ---- | ----------------------------------------- |
| qq   | Long | 被服务器断开或因网络问题而掉线的Bot的QQ号 |



### Bot主动重新登录

```json5
{
  "type":"BotReloginEvent",
  "qq":123456
}
```

| 名字 | 类型 | 说明                    |
| ---- | ---- | ----------------------- |
| qq   | Long | 主动重新登录的Bot的QQ号 |



## 好友事件



### 好友输入状态改变

```json5
{
  "type": "FriendInputStatusChangedEvent",
  "friend": {
    "id": 123123,
    "nickname": "nick",
    "remark": "remark"
  }, 
  "inputting": true
}
```

| 名字             | 类型   | 说明                                          |
| ---------------- | ------ | --------------------------------------------- |
| id               | Long   | 好友 QQ 号码     |
| nickname         | String | 好友昵称     |
| remark           | String | 好友备注                          |
| inputting        | Boolean | 当前输出状态是否正在输入                    |



### 好友昵称改变

```json5
{
  "type": "FriendNickChangedEvent",
  "friend": {
    "id": 123123,
    "nickname": "nick",
    "remark": "remark"
  }, 
  "from": "origin nickname",
  "to": "new nickname"
}
```

| 名字             | 类型   | 说明                                          |
| ---------------- | ------ | --------------------------------------------- |
| id               | Long   | 好友 QQ 号码     |
| nickname         | String | 好友昵称(值不确定)     |
| remark           | String | 好友备注                          |
| from             | String | 原昵称                    |
| to               | String | 新昵称                    |




## 群事件



### Bot在群里的权限被改变. 操作人一定是群主

```json5
{
  "type": "BotGroupPermissionChangeEvent",
  "origin": "MEMBER",
  "current": "ADMINISTRATOR",
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "ADMINISTRATOR"
  }
}
```

| 名字             | 类型   | 说明                                          |
| ---------------- | ------ | --------------------------------------------- |
| origin           | String | Bot的原权限，OWNER、ADMINISTRATOR或MEMBER     |
| current          | String | Bot的新权限，OWNER、ADMINISTRATOR或MEMBER     |
| group            | Object | 权限改变所在的群信息                          |
| group.id         | Long   | 群号                                          |
| group.name       | String | 群名                                          |
| group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |



### Bot被禁言

```json5
{
  "type": "BotMuteEvent",
  "durationSeconds": 600,
  "operator": {
    "id": 123456789,
    "memberName": "我是管理员",
    "permission": "ADMINISTRATOR",
    "specialTitle":"群头衔",
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                      | 类型   | 说明                                             |
| ------------------------- | ------ | ------------------------------------------------ |
| durationSeconds           | Int    | 禁言时长，单位为秒                               |
| operator                  | Object | 操作的管理员或群主信息                           |
| operator.id               | Long   | 操作者的QQ号                                     |
| operator.memberName       | String | 操作者的群名片                                   |
| operator.permission       | String | 操作者在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator.group            | Object | Bot被禁言所在群的信息                            |
| operator.group.id         | Long   | 群号                                             |
| operator.group.name       | String | 群名                                             |
| operator.group.permission | String | Bot在群中的权限，OWNER或ADMINISTRATOR            |



### Bot被取消禁言

```json5
{
  "type": "BotUnmuteEvent",
  "operator": {
    "id": 123456789,
    "memberName": "我是管理员",
    "permission": "ADMINISTRATOR",
    "specialTitle":"群头衔",
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                      | 类型   | 说明                                             |
| ------------------------- | ------ | ------------------------------------------------ |
| operator                  | Object | 操作的管理员或群主信息                           |
| operator.id               | Long   | 操作者的QQ号                                     |
| operator.memberName       | String | 操作者的群名片                                   |
| operator.permission       | String | 操作者在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator.group            | Object | Bot被取消禁言所在群的信息                        |
| operator.group.id         | Long   | 群号                                             |
| operator.group.name       | String | 群名                                             |
| operator.group.permission | String | Bot在群中的权限，OWNER或ADMINISTRATOR            |



### Bot加入了一个新群

```json5
{
  "type": "BotJoinGroupEvent",
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  invitor: null
}
```

| 名字             | 类型   | 说明                                                         |
| ---------------- | ------ | ------------------------------------------------------------ |
| group            | Object | Bot新加入群的信息                                            |
| group.id         | Long   | 群号                                                         |
| group.name       | String | 群名                                                         |
| group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER（新加入群通常是Member） |
| invitor          | Object | 如果被要求入群的话，则为邀请人的 Member 对象 |



### Bot主动退出一个群

```json5
{
  "type": "BotLeaveEventActive",
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  }
}
```

| 名字             | 类型   | 说明                                                         |
| ---------------- | ------ | ------------------------------------------------------------ |
| group            | Object | Bot退出的群的信息                                            |
| group.id         | Long   | 群号                                                         |
| group.name       | String | 群名                                                         |
| group.permission | String | Bot在群中的权限，ADMINISTRATOR或MEMBER |



### Bot被踢出一个群

```json5
{
  "type": "BotLeaveEventKick",
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  operator: null
}
```

| 名字             | 类型   | 说明                                                         |
| ---------------- | ------ | ------------------------------------------------------------ |
| group            | Object | Bot被踢出的群的信息                                            |
| group.id         | Long   | 群号                                                         |
| group.name       | String | 群名                                                         |
| group.permission | String | Bot在群中的权限，ADMINISTRATOR或MEMBER |
| operator         | Object | Bot被踢后获取操作人的 Member 对象 |


### Bot因群主解散群而退出群, 操作人一定是群主

```json5
{
  "type": "BotLeaveEventDisband",
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  operator: null
}
```

| 名字             | 类型   | 说明                             |
| ---------------- | ------ |--------------------------------|
| group            | Object | Bot所在被解散的群的信息                  |
| group.id         | Long   | 群号                             |
| group.name       | String | 群名                             |
| group.permission | String | Bot在群中的权限，ADMINISTRATOR或MEMBER |
| operator         | Object | Bot离开群后获取操作人的 Member 对象        |


### 群消息撤回

```json5
{
   "type": "GroupRecallEvent",
   "authorId": 123456,
   "messageId": 123456789,
   "time": 1234679,
   "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "ADMINISTRATOR"
   },
   "operator": {
      "id": 123456789,
      "memberName": "我是管理员",
      "permission": "ADMINISTRATOR",
      "specialTitle":"群头衔",
      "joinTimestamp":12345678,
      "lastSpeakTimestamp":8765432,
      "muteTimeRemaining":0,
      "group": {
        "id": 123456789,
        "name": "Miral Technology",
        "permission": "MEMBER"
      }
   }
}
```

| 名字                      | 类型    | 说明                                             |
| ------------------------- | ------- | ------------------------------------------------ |
| authorId                  | Long    | 原消息发送者的QQ号                               |
| messageId                 | Int     | 原消息messageId                                  |
| time                      | Int     | 原消息发送时间                                   |
| group                     | Object  | 消息撤回所在的群                                 |
| group.id                  | Long    | 群号                                             |
| group.name                | String  | 群名                                             |
| group.permission          | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER    |
| operator                  | Object? | 撤回消息的操作人，当null时为bot操作              |
| operator.id               | Long    | 操作者的QQ号                                     |
| operator.memberName       | String  | 操作者的群名片                                   |
| operator.permission       | String  | 操作者在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator.group            | Object  | 同group                                          |



#### 好友消息撤回

```json5
{
    "type": "FriendRecallEvent",
    "authorId": 123456,
    "messageId": 123456789,
    "time": 1234679,
    "operator": 123456
}
```

| 名字      | 类型 | 说明               |
| --------- | ---- | ------------------ |
| authorId  | Long | 原消息发送者的QQ号 |
| messageId | Int  | 原消息messageId    |
| time      | Int  | 原消息发送时间     |
| operator  | Long | 好友QQ号或BotQQ号  |


### 戳一戳事件

```json5
{
    "type": "NudgeEvent",
    "fromId": 123456,
    "subject": {
        "id": 123456,
        "kind": "Group"
    },
    "action": "戳了戳",
    "suffix": "的脸",
    "target": 123456
}
```

| 名字         | 类型    | 说明               |
| ------------ | ------ | ------------------- |
| fromId       | Long   | 动作发出者的QQ号 |
| subject      | Object | 来源       |
| subject.id   | Long   | 来源的QQ号（好友）或群号 |
| subject.kind | String | 来源的类型，"Friend"或"Group" |
| action       | String | 动作类型     |
| suffix       | String | 自定义动作内容  |
| target       | Long   | 动作目标的QQ号 |


### 某个群名改变

```json5
{
  "type": "GroupNameChangeEvent",
  "origin": "miral technology",
  "current": "MIRAI TECHNOLOGY",
  "group": {
    "id": 123456789,
    "name": "MIRAI TECHNOLOGY",
    "permission": "MEMBER"
  },
  "operator": {
    "id": 123456,
    "memberName": "我是群主",
    "permission": "ADMINISTRATOR",
    "specialTitle":"群头衔",
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "OWNER"
    }
  }
}
```

| 名字             | 类型    | 说明                                          |
| ---------------- | ------- | --------------------------------------------- |
| origin           | String  | 原群名                                        |
| current          | String  | 新群名                                        |
| group            | Object  | 群名改名的群信息                              |
| group.id         | Long    | 群号                                          |
| group.name       | String  | 群名                                          |
| group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator            | Object? | 操作的管理员或群主信息，当null时为Bot操作                           |
| operator.id         | Long   | 操作者的QQ号                                     |
| operator.memberName | String | 操作者的群名片                                   |
| operator.permission | String | 操作者在群中的权限，OWNER或ADMINISTRATOR |
| operator.group      | Object | 同group                 |



### 某群入群公告改变

```json5
{
  "type": "GroupEntranceAnnouncementChangeEvent",
  "origin": "abc",
  "current": "cba",
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  "operator": {
    "id": 123456789,
    "memberName": "我是管理员",
    "permission": "ADMINISTRATOR",
    "specialTitle":"群头衔",
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                | 类型    | 说明                                          |
| ------------------- | ------- | --------------------------------------------- |
| origin              | String  | 原公告                                        |
| current             | String  | 新公告                                        |
| group               | Object  | 公告改变的群信息                              |
| group.id            | Long    | 群号                                          |
| group.name          | String  | 群名                                          |
| group.permission    | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator            | Object? | 操作的管理员或群主信息，当null时为Bot操作     |
| operator.id         | Long    | 操作者的QQ号                                  |
| operator.memberName | String  | 操作者的群名片                                |
| operator.permission | String  | 操作者在群中的权限，OWNER或ADMINISTRATOR      |
| operator.group      | Object  | 同group                                       |



### 全员禁言

```json5
{
  "type": "GroupMuteAllEvent",
  "origin": false,
  "current": true,
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  "operator": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"OWNER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    },  
  }
}
```

| 名字                | 类型    | 说明                                          |
| ------------------- | ------- | --------------------------------------------- |
| origin              | Boolean | 原本是否处于全员禁言                          |
| current             | Boolean | 现在是否处于全员禁言                          |
| group               | Object  | 全员禁言的群信息                              |
| group.id            | Long    | 群号                                          |
| group.name          | String  | 群名                                          |
| group.permission    | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator            | Object? | 操作的管理员或群主信息，当null时为Bot操作     |
| operator.id         | Long    | 操作者的QQ号                                  |
| operator.memberName | String  | 操作者的群名片                                |
| operator.permission | String  | 操作者在群中的权限，OWNER或ADMINISTRATOR      |
| operator.group      | Object  | 同group                                       |



### 匿名聊天

```json5
{
  "type": "GroupAllowAnonymousChatEvent",
  "origin": false,
  "current": true,
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  "operator": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"OWNER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                | 类型    | 说明                                          |
| ------------------- | ------- | --------------------------------------------- |
| origin              | Boolean | 原本匿名聊天是否开启                          |
| current             | Boolean | 现在匿名聊天是否开启                          |
| group               | Object  | 匿名聊天状态改变的群信息                      |
| group.id            | Long    | 群号                                          |
| group.name          | String  | 群名                                          |
| group.permission    | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator            | Object? | 操作的管理员或群主信息，当null时为Bot操作     |
| operator.id         | Long    | 操作者的QQ号                                  |
| operator.memberName | String  | 操作者的群名片                                |
| operator.permission | String  | 操作者在群中的权限，OWNER或ADMINISTRATOR      |
| operator.group      | Object  | 同group                                       |



### 坦白说

```json5
{
  "type": "GroupAllowConfessTalkEvent",
  "origin": false,
  "current": true,
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  "isByBot": false
}
```

| 名字             | 类型    | 说明                                          |
| ---------------- | ------- | --------------------------------------------- |
| origin           | Boolean | 原本坦白说是否开启                            |
| current          | Boolean | 现在坦白说是否开启                            |
| group            | Object  | 坦白说状态改变的群信息                        |
| group.id         | Long    | 群号                                          |
| group.name       | String  | 群名                                          |
| group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| isByBot          | Boolean | 是否Bot进行该操作                             |



### 允许群员邀请好友加群

```json5
{
  "type": "GroupAllowMemberInviteEvent",
  "origin": false,
  "current": true,
  "group": {
    "id": 123456789,
    "name": "Miral Technology",
    "permission": "MEMBER"
  },
  "operator": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"OWNER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                | 类型    | 说明                                          |
| ------------------- | ------- | --------------------------------------------- |
| origin              | Boolean | 原本是否允许群员邀请好友加群                  |
| current             | Boolean | 现在是否允许群员邀请好友加群                  |
| group               | Object  | 允许群员邀请好友加群状态改变的群信息          |
| group.id            | Long    | 群号                                          |
| group.name          | String  | 群名                                          |
| group.permission    | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator            | Object? | 操作的管理员或群主信息，当null时为Bot操作     |
| operator.id         | Long    | 操作者的QQ号                                  |
| operator.memberName | String  | 操作者的群名片                                |
| operator.permission | String  | 操作者在群中的权限，OWNER或ADMINISTRATOR      |
| operator.group      | Object  | 同group                                       |



### 新人入群的事件

```json5
{
  "type": "MemberJoinEvent",
  "member": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"MEMBER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  },
  invitor: null
}
```

| 名字                    | 类型   | 说明                                                         |
| ----------------------- | ------ | ------------------------------------------------------------ |
| member                  | Object | 新人信息                                                     |
| member.id               | Long   | 新人的QQ号                                                   |
| member.memberName       | String | 新人的群名片                                                 |
| member.permission       | String | 新人在群中的权限，OWNER、ADMINISTRATOR或MEMBER（新入群通常是MEMBER） |
| member.group            | Object | 新人入群的群信息                                             |
| member.group.id         | Long   | 群号                                                         |
| member.group.name       | String | 群名                                                         |
| member.group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER                |
| invitor                 | Object | 如果被要求入群的话，则为邀请人的 Member 对象 |



### 成员被踢出群（该成员不是Bot）

```json5
{
  "type": "MemberLeaveEventKick",
  "member": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"MEMBER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  },
  "operator": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"OWNER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  }
}
```

| 名字                    | 类型    | 说明                                          |
| ----------------------- | ------- | --------------------------------------------- |
| member                  | Object  | 被踢者的信息                                  |
| member.id               | Long    | 被踢者的QQ号                                  |
| member.memberName       | String  | 被踢者的群名片                                |
| member.permission       | String  | 被踢者在群中的权限，ADMINISTRATOR或MEMBER     |
| member.group            | Object  | 被踢者所在的群                                |
| member.group.id         | Long    | 群号                                          |
| member.group.name       | String  | 群名                                          |
| member.group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| operator                | Object? | 操作的管理员或群主信息，当null时为Bot操作     |
| operator.id             | Long    | 操作者的QQ号                                  |
| operator.memberName     | String  | 操作者的群名片                                |
| operator.permission     | String  | 操作者在群中的权限，OWNER或ADMINISTRATOR      |
| operator.group          | Object  | 同member.group                                |



### 成员主动离群（该成员不是Bot）

```json5
{
  "type": "MemberLeaveEventQuit",
  "member": {
    "id": 123456789,
    "memberName": "我是被踢的",
    "permission": "MEMBER",
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                    | 类型   | 说明                                          |
| ----------------------- | ------ | --------------------------------------------- |
| member                  | Object | 退群群员的信息                                |
| member.id               | Long   | 退群群员的QQ号                                |
| member.memberName       | String | 退群群员的群名片                              |
| member.permission       | String | 退群群员在群中的权限，ADMINISTRATOR或MEMBER   |
| member.group            | Object | 退群群员所在的群信息                          |
| member.group.id         | Long   | 群号                                          |
| member.group.name       | String | 群名                                          |
| member.group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |



### 群名片改动

```json5
{
  "type": "MemberCardChangeEvent",
  "origin": "origin name",
  "current": "我是被改名的",
  "member": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"MEMBER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }  
  }
}
```

| 名字                    | 类型    | 说明                                                     |
| ----------------------- | ------- | -------------------------------------------------------- |
| origin                  | String  | 原本名片                                                 |
| current                 | String  | 现在名片                                                 |
| member                  | Object  | 名片改动的群员的信息                                     |
| member.id               | Long    | 名片改动的群员的QQ号                                     |
| member.memberName       | String  | 名片改动的群员的群名片                                   |
| member.permission       | String  | 名片改动的群员在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| member.group            | Object  | 名片改动的群员所在群的信息                               |
| member.group.id         | Long    | 群号                                                     |
| member.group.name       | String  | 群名                                                     |
| member.group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER            |


### 群头衔改动（只有群主有操作限权）

```json5
{
  "type": "MemberSpecialTitleChangeEvent",
  "origin": "origin title",
  "current": "new title",
  "member": {
    "id": 123456789,
    "memberName": "我是被改头衔的",
    "permission": "MEMBER",
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                    | 类型   | 说明                                                     |
| ----------------------- | ------ | -------------------------------------------------------- |
| origin                  | String | 原头衔                                                   |                                                 |
| current                 | String | 现头衔                                                   |
| member                  | Object | 头衔改动的群员的信息                                     |
| member.id               | Long   | 头衔改动的群员的QQ号                                     |
| member.memberName       | String | 头衔改动的群员的群名片                                   |
| member.permission       | String | 头衔改动的群员在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| member.group            | Object | 头衔改动的群员所在群的信息                               |
| member.group.id         | Long   | 群号                                                     |
| member.group.name       | String | 群名                                                     |
| member.group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER            |



### 成员权限改变的事件（该成员不是Bot）

```json5
{
  "type": "MemberPermissionChangeEvent",
  "origin": "MEMBER",
  "current": "ADMINISTRATOR",
  "member": {
    "id": 123456789,
    "memberName": "我是被改权限的",
    "permission": "ADMINISTRATOR",
    "group": {
      "id": 123456789,
      "name": "Miral Technology",
      "permission": "MEMBER"
    }
  }
}
```

| 名字                    | 类型   | 说明                                              |
| ----------------------- | ------ | ------------------------------------------------- |
| origin                  | String | 原权限                                            |                                        |
| current                 | String | 现权限                                            |
| member                  | Object | 权限改动的群员的信息                              |
| member.id               | Long   | 权限改动的群员的QQ号                              |
| member.memberName       | String | 权限改动的群员的群名片                            |
| member.permission       | String | 权限改动的群员在群中的权限，ADMINISTRATOR或MEMBER |
| member.group            | Object | 权限改动的群员所在群的信息                        |
| member.group.id         | Long   | 群号                                              |
| member.group.name       | String | 群名                                              |
| member.group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER     |



### 群成员被禁言事件（该成员不是Bot）

```json5
{
  "type": "MemberMuteEvent",
  "durationSeconds": 600,
  "member": {
    "id":1234567890,
    "memberName":"我是被取消禁言的",
    "specialTitle":"群头衔",
    "permission":"MEMBER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  },
  "operator": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"OWNER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  }
}
```

| 名字                    | 类型    | 说明                                            |
| ----------------------- | ------- | ----------------------------------------------- |
| durationSeconds         | Long    | 禁言时长，单位为秒                              |
| member                  | Object  | 被禁言的群员的信息                              |
| member.id               | Long    | 被禁言的群员的QQ号                              |
| member.memberName       | String  | 被禁言的群员的群名片                            |
| member.permission       | String  | 被禁言的群员在群中的权限，ADMINISTRATOR或MEMBER |
| member.group            | Object  | 被禁言的群员所在群的信息                        |
| member.group.id         | Long    | 群号                                            |
| member.group.name       | String  | 群名                                            |
| member.group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER   |
| operator                | Object? | 操作者的信息，当null时为Bot操作                 |
| operator.id             | Long    | 操作者的QQ号                                    |
| operator.memberName     | String  | 操作者的群名片                                  |
| operator.permission     | String  | 操作者在群中的权限，OWNER、ADMINISTRATOR        |
| operator.group          | Object  | 同member.group                                  |



### 群成员被取消禁言事件（该成员不是Bot）

```json5
{
  "type": "MemberUnmuteEvent",
  "member": {
    "id":1234567890,
    "memberName":"我是被取消禁言的",
    "specialTitle":"群头衔",
    "permission":"MEMBER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  },
  "operator": {
    "id":1234567890,
    "memberName":"",
    "specialTitle":"群头衔",
    "permission":"OWNER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  }
}
```

| 名字                    | 类型    | 说明                                                |
| ----------------------- | ------- | --------------------------------------------------- |
| member                  | Object  | 被取消禁言的群员的信息                              |
| member.id               | Long    | 被取消禁言的群员的QQ号                              |
| member.memberName       | String  | 被取消禁言的群员的群名片                            |
| member.permission       | String  | 被取消禁言的群员在群中的权限，ADMINISTRATOR或MEMBER |
| member.group            | Object  | 被取消禁言的群员所在群的信息                        |
| member.group.id         | Long    | 群号                                                |
| member.group.name       | String  | 群名                                                |
| member.group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER       |
| operator                | Object? | 操作者的信息，当null时为Bot操作                     |
| operator.id             | Long    | 操作者的QQ号                                        |
| operator.memberName     | String  | 操作者的群名片                                      |
| operator.permission     | String  | 操作者在群中的权限，OWNER、ADMINISTRATOR            |
| operator.group          | Object  | 同member.group                                      |



### 群员称号改变

```json5
{
  "type": "MemberHonorChangeEvent",
  "member": {
    "id":1234567890,
    "memberName":"我是被取消禁言的",
    "specialTitle":"群头衔",
    "permission":"MEMBER",  // 群成员在群中的权限
    "joinTimestamp":12345678,
    "lastSpeakTimestamp":8765432,
    "muteTimeRemaining":0,
    "group":{
      "id":12345,
      "name":"群名1",
      "permission":"MEMBER" // bot 在群中的权限
    }
  },
  "action": "achieve",
  "honor": "龙王"
}
```

| 名字                    | 类型    | 说明                                                |
| ----------------------- | ------- | --------------------------------------------------- |
| member                  | Object  | 被取消禁言的群员的信息                               |
| member.id               | Long    | 被取消禁言的群员的QQ号                               |
| member.memberName       | String  | 被取消禁言的群员的群名片                               |
| member.permission       | String  | 被取消禁言的群员在群中的权限，ADMINISTRATOR或MEMBER    |
| member.group            | Object  | 被取消禁言的群员所在群的信息                           |
| member.group.id         | Long    | 群号                                                |
| member.group.name       | String  | 群名                                                |
| member.group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER         |
| action                  | String  | 称号变化行为：achieve获得称号，lose失去称号            |
| honor                   | String  | 称号名称                                             |



## 申请事件

### 添加好友申请

```json
{
  "type": "NewFriendRequestEvent",
  "eventId": 12345678,
  "fromId": 123456,
  "groupId": 654321,
  "nick": "Nick Name",
  "message": ""
}
```

| 名字    | 类型   | 说明                                                  |
| ------- | ------ | ----------------------------------------------------- |
| eventId | Long   | 事件标识，响应该事件时的标识                          |
| fromId  | Long   | 申请人QQ号                                            |
| groupId | Long   | 申请人如果通过某个群添加好友，该项为该群群号；否则为0 |
| nick    | String | 申请人的昵称或群名片                                  |
| message | String | 申请消息                                           |


### 用户入群申请（Bot需要有管理员权限）

```json
{
  "type": "MemberJoinRequestEvent",
  "eventId": 12345678,
  "fromId": 123456,
  "groupId": 654321,
  "groupName": "Group",
  "nick": "Nick Name",
  "message": ""
}
```

| 名字      | 类型   | 说明                         |
| --------- | ------ | ---------------------------- |
| eventId   | Long   | 事件标识，响应该事件时的标识 |
| fromId    | Long   | 申请人QQ号                   |
| groupId   | Long   | 申请人申请入群的群号         |
| groupName | String | 申请人申请入群的群名称       |
| nick      | String | 申请人的昵称或群名片         |
| message   | String | 申请消息                  |



### Bot被邀请入群申请

```json
{
  "type": "BotInvitedJoinGroupRequestEvent",
  "eventId": 12345678,
  "fromId": 123456,
  "groupId": 654321,
  "groupName": "Group",
  "nick": "Nick Name",
  "message": ""
}
```

| 名字      | 类型   | 说明                         |
| --------- | ------ | ---------------------------- |
| eventId   | Long   | 事件标识，响应该事件时的标识 |
| fromId    | Long   | 邀请人（好友）的QQ号              |
| groupId   | Long   | 被邀请进入群的群号         |
| groupName | String | 被邀请进入群的群名称       |
| nick      | String | 邀请人（好友）的昵称         |
| message   | String | 邀请消息                  |

## 其他客户端事件

### 其他客户端上线

```json
{
  "type": "OtherClientOnlineEvent",
  "client": {
    "id": 1,
    "platform": "WINDOWS"
  },
  "kind": 69899
}
```

| 名字              | 类型   | 说明           |
| ----------------- | ------ | -------------- |
| client            | Object | 其他客户端     |
| client.id         | Long   | 客户端标识号   |
| client.platform   | String | 客户端类型     |
| kind              | Long?  | 详细设备类型   |

### 其他客户端下线

```json
{
  "type": "OtherClientOfflineEvent",
  "client": {
    "id": 1,
    "platform": "WINDOWS"
  }
}
```

| 名字              | 类型   | 说明           |
| ----------------- | ------ | -------------- |
| client            | Object | 其他客户端     |
| client.id         | Long   | 客户端标识号   |
| client.platform   | String | 客户端类型     |


## 命令事件

### 命令被执行

```json
{
  "type": "CommandExecutedEvent",
  "name": "shutdown",
  "friend": null,
  "member": null,
  "args": [
    {
      "type": "Plain",
      "text": "myself"
    }
  ]
}
```

> 即执行了 `/shutdown myself`

| 名字      | 类型   | 说明                         |
| --------- | ------ | ---------------------------- |
| eventId   | Long   | 事件标识，响应该事件时的标识 |
| name      | String | 命令名称                 |
| friend    | Object | 发送命令的好友, 从控制台发送为 null |
| member    | Object | 发送命令的群成员, 从控制台发送为 null |
| args      | Array  | 指令的参数, 以消息类型传递  |
