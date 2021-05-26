# 更新日志

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
