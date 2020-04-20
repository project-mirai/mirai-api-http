## Mirai-api-http事件类型一览

#### Bot登录成功

```json5
{
    "type": "BotOnlineEvent",
    "qq": 123456
}
```

| 名字 | 类型 | 说明                |
| ---- | ---- | ------------------- |
| qq   | Long | 登录成功的Bot的QQ号 |



#### Bot主动离线

```json5
{
    "type": "BotOfflineEventActive",
    "qq": 123456
}
```

| 名字 | 类型 | 说明                |
| ---- | ---- | ------------------- |
| qq   | Long | 主动离线的Bot的QQ号 |



#### Bot被挤下线

```json5
{
    "type": "BotOfflineEventForce",
    "qq": 123456
}
```

| 名字 | 类型 | 说明                |
| ---- | ---- | ------------------- |
| qq   | Long | 被挤下线的Bot的QQ号 |



#### Bot被服务器断开或因网络问题而掉线

```json5
{
    "type": "BotOfflineEventDropped",
    "qq": 123456
}
```

| 名字 | 类型 | 说明                                      |
| ---- | ---- | ----------------------------------------- |
| qq   | Long | 被服务器断开或因网络问题而掉线的Bot的QQ号 |



#### Bot主动重新登录.

```json5
{
    "type": "BotReloginEvent",
    "qq": 123456
}
```

| 名字 | 类型 | 说明                    |
| ---- | ---- | ----------------------- |
| qq   | Long | 主动重新登录的Bot的QQ号 |



#### 群消息撤回

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



#### Bot在群里的权限被改变. 操作人一定是群主

```json5
{
    "type": "BotGroupPermissionChangeEvent",
    "origin": "MEMBER",
    "new": "ADMINISTRATOR",
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
| new(Deprecated)  | String | Bot的新权限，OWNER、ADMINISTRATOR或MEMBER     |
| current          | String | Bot的新权限，OWNER、ADMINISTRATOR或MEMBER     |
| group            | Object | 权限改变所在的群信息                          |
| group.id         | Long   | 群号                                          |
| group.name       | String | 群名                                          |
| group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |



#### Bot被禁言

```json5
{
    "type": "BotMuteEvent",
    "durationSeconds": 600,
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
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



#### Bot被取消禁言

```json5
{
    "type": "BotUnmuteEvent",
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
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



#### Bot加入了一个新群

```json5
{
    "type": "BotJoinGroupEvent",
    "group": {
        "id": 123456789,
        "name": "Miral Technology",
        "permission": "MEMBER"
    }
}
```

| 名字             | 类型   | 说明                                                         |
| ---------------- | ------ | ------------------------------------------------------------ |
| group            | Object | Bot新加入群的信息                                            |
| group.id         | Long   | 群号                                                         |
| group.name       | String | 群名                                                         |
| group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER（新加入群通常是Member） |



#### Bot主动退出一个群

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



#### Bot被踢出一个群

```json5
{
    "type": "BotLeaveEventKick",
    "group": {
        "id": 123456789,
        "name": "Miral Technology",
        "permission": "MEMBER"
    }
}
```

| 名字             | 类型   | 说明                                                         |
| ---------------- | ------ | ------------------------------------------------------------ |
| group            | Object | Bot被踢出的群的信息                                            |
| group.id         | Long   | 群号                                                         |
| group.name       | String | 群名                                                         |
| group.permission | String | Bot在群中的权限，ADMINISTRATOR或MEMBER |



#### 某个群名改变

```json5
{
    "type": "GroupNameChangeEvent",
    "origin": "miral technology",
    "new": "MIRAI TECHNOLOGY",
    "current": "MIRAI TECHNOLOGY",
    "group": {
        "id": 123456789,
        "name": "MIRAI TECHNOLOGY",
        "permission": "MEMBER"
    },
    "isByBot": false
}
```

| 名字             | 类型    | 说明                                          |
| ---------------- | ------- | --------------------------------------------- |
| origin           | String  | 原群名                                        |
| new(Deprecated)  | String  | 新群名                                        |
| current          | String  | 新群名                                        |
| group            | Object  | 群名改名的群信息                              |
| group.id         | Long    | 群号                                          |
| group.name       | String  | 群名                                          |
| group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| isByBot          | Boolean | 是否Bot进行该操作                             |



#### 某群入群公告改变

```json5
{
    "type": "GroupEntranceAnnouncementChangeEvent",
    "origin": "abc",
    "new": "cba",
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
| new(Deprecated)     | String  | 新公告                                        |
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



#### 全员禁言

```json5
{
    "type": "GroupMuteAllEvent",
    "origin": false,
    "new": true,
    "current": true,
    "group": {
        "id": 123456789,
        "name": "Miral Technology",
        "permission": "MEMBER"
    },
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
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
| origin              | Boolean | 原本是否处于全员禁言                          |
| new(Deprecated)     | Boolean | 现在是否处于全员禁言                          |
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



#### 匿名聊天

```json5
{
    "type": "GroupAllowAnonymousChatEvent",
    "origin": false,
    "new": true,
    "current": true,
    "group": {
        "id": 123456789,
        "name": "Miral Technology",
        "permission": "MEMBER"
    },
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
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
| new(Deprecated)     | Boolean | 现在匿名聊天是否开启                          |
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



#### 坦白说

```json5
{
    "type": "GroupAllowConfessTalkEvent",
    "origin": false,
    "new": true,
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
| new(Deprecated)  | Boolean | 现在坦白说是否开启                            |
| current          | Boolean | 现在坦白说是否开启                            |
| group            | Object  | 坦白说状态改变的群信息                        |
| group.id         | Long    | 群号                                          |
| group.name       | String  | 群名                                          |
| group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| isByBot          | Boolean | 是否Bot进行该操作                             |



#### 允许群员邀请好友加群

```json5
{
    "type": "GroupAllowMemberInviteEvent",
    "origin": false,
    "new": true,
    "current": true,
    "group": {
        "id": 123456789,
        "name": "Miral Technology",
        "permission": "MEMBER"
    },
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
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
| new(Deprecated)     | Boolean | 现在是否允许群员邀请好友加群                  |
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



#### 新人入群的事件

```json5
{
    "type": "MemberJoinEvent",
    "member": {
        "id": 123456789,
        "memberName": "我是新人",
        "permission": "MEMBER",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
        }
    }
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



#### 成员被踢出群（该成员不是Bot）

```json5
{
    "type": "MemberLeaveEventKick",
    "member": {
        "id": 123456789,
        "memberName": "我是被踢的",
        "permission": "MEMBER",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
        }
    },
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
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



#### 成员主动离群（该成员不是Bot）

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



#### 群名片改动

```json5
{
    "type": "MemberCardChangeEvent",
    "origin": "origin name",
    "new": "我是被改名的",
    "current": "我是被改名的",
    "member": {
        "id": 123456789,
        "memberName": "我是被改名的",
        "permission": "MEMBER",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
        }
    },
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员，也可能是我自己",
        "permission": "ADMINISTRATOR",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
        }
    }
}
```

| 名字                    | 类型    | 说明                                                     |
| ----------------------- | ------- | -------------------------------------------------------- |
| origin                  | String  | 原本名片                                                 |
| new(Deprecated)         | String  | 现在名片                                                 |
| current                 | String  | 现在名片                                                 |
| member                  | Object  | 名片改动的群员的信息                                     |
| member.id               | Long    | 名片改动的群员的QQ号                                     |
| member.memberName       | String  | 名片改动的群员的群名片                                   |
| member.permission       | String  | 名片改动的群员在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| member.group            | Object  | 名片改动的群员所在群的信息                               |
| member.group.id         | Long    | 群号                                                     |
| member.group.name       | String  | 群名                                                     |
| member.group.permission | String  | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER            |
| operator                | Object? | 操作者的信息，可能为该群员自己，当null时为Bot操作        |
| operator.id             | Long    | 操作者的QQ号                                             |
| operator.memberName     | String  | 操作者的群名片                                           |
| operator.permission     | String  | 操作者在群中的权限，OWNER、ADMINISTRATOR或MEMBER         |
| operator.group          | Object  | 同member.group                                           |



#### 群头衔改动（只有群主有操作限权）

```json5
{
    "type": "MemberSpecialTitleChangeEvent",
    "origin": "origin title",
    "new": "new title",
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
| origin                  | String | 原头衔                                                   |
| new（Deprecated)        | String | 现头衔                                                   |
| current                 | String | 现头衔                                                   |
| member                  | Object | 头衔改动的群员的信息                                     |
| member.id               | Long   | 头衔改动的群员的QQ号                                     |
| member.memberName       | String | 头衔改动的群员的群名片                                   |
| member.permission       | String | 头衔改动的群员在群中的权限，OWNER、ADMINISTRATOR或MEMBER |
| member.group            | Object | 头衔改动的群员所在群的信息                               |
| member.group.id         | Long   | 群号                                                     |
| member.group.name       | String | 群名                                                     |
| member.group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER            |



#### 成员权限改变的事件（该成员不可能是Bot，见BotGroupPermissionChangeEvent）

```json5
{
    "type": "MemberPermissionChangeEvent",
    "origin": "MEMBER",
    "new": "ADMINISTRATOR",
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
| origin                  | String | 原权限                                            |
| new                     | String | 现权限(Deprecated)                                            |
| current                 | String | 现权限                                            |
| member                  | Object | 权限改动的群员的信息                              |
| member.id               | Long   | 权限改动的群员的QQ号                              |
| member.memberName       | String | 权限改动的群员的群名片                            |
| member.permission       | String | 权限改动的群员在群中的权限，ADMINISTRATOR或MEMBER |
| member.group            | Object | 权限改动的群员所在群的信息                        |
| member.group.id         | Long   | 群号                                              |
| member.group.name       | String | 群名                                              |
| member.group.permission | String | Bot在群中的权限，OWNER、ADMINISTRATOR或MEMBER     |



#### 群成员被禁言事件（该成员不可能是Bot，见BotMuteEvent）

```json5
{
    "type": "MemberMuteEvent",
    "durationSeconds": 600,
    "member": {
        "id": 123456789,
        "memberName": "我是被禁言的",
        "permission": "MEMBER",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
        }
    },
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
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



#### 群成员被取消禁言事件（该成员不可能是Bot，见BotUnmuteEvent）

```json5
{
    "type": "MemberUnmuteEvent",
    "member": {
        "id": 123456789,
        "memberName": "我是被取消禁言的",
        "permission": "MEMBER",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
        }
    },
    "operator": {
        "id": 123456789,
        "memberName": "我是管理员",
        "permission": "ADMINISTRATOR",
        "group": {
            "id": 123456789,
            "name": "Miral Technology",
            "permission": "MEMBER"
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



#### 添加好友申请

```json
{
    "type": "NewFriendRequestEvent",
    "eventId": 12345678,
    "fromId": 123456,
    "groupId": 654321,
    "nick": "Nick Name"
}
```

| 名字    | 类型   | 说明                                                  |
| ------- | ------ | ----------------------------------------------------- |
| eventId | Long   | 事件标识，响应该事件时的标识                          |
| fromId  | Long   | 申请人QQ号                                            |
| groupId | Long   | 申请人如果通过某个群添加好友，该项为该群群号；否则为0 |
| nick    | String | 申请人的昵称或群名片                                  |

##### 响应

```
[POST] /resp/newFriendRequestEvent
```

```json
{
    "sessionKey": "YourSessionKey",
    "eventId": 12345678,
    "fromId": 123456,
    "groupId": 654321,
    "operate": 0,
    "message": ""
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



#### 用户入群申请（Bot需要有管理员权限）

```json
{
    "type": "MemberJoinRequestEvent",
    "eventId": 12345678,
    "fromId": 123456,
    "groupId": 654321,
    "groupName": "Group",
    "nick": "Nick Name"
}
```

| 名字      | 类型   | 说明                         |
| --------- | ------ | ---------------------------- |
| eventId   | Long   | 事件标识，响应该事件时的标识 |
| fromId    | Long   | 申请人QQ号                   |
| groupId   | Long   | 申请人申请入群的群号         |
| groupName | String | 申请人申请入群的群名称       |
| nick      | String | 申请人的昵称或群名片         |

##### 响应

```
[POST] /resp/memberJoinRequestEvent
```

```json
{
    "sessionKey": "YourSessionKey",
    "eventId": 12345678,
    "fromId": 123456,
    "groupId": 654321,
    "operate": 0,
    "message": ""
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

| operate | 说明                                           |
| ------- | ---------------------------------------------- |
| 0       | 同意入群                                       |
| 1       | 拒绝入群                                       |
| 2       | 忽略请求                                       |
| 3       | 拒绝入群并添加黑名单，不再接收该用户的入群申请 |
| 4       | 忽略入群并添加黑名单，不再接收该用户的入群申请 |

