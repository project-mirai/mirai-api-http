# 更新日志



## \[1.5.0\] - 2020-04-05

### 变更

* 更新 `core` 依赖到 0.33.0
* 所有 `messageId`, `time` 由 `Long` 变更为 `Int`
* `Quote` 消息类型增加 `targetId` 作为获取原消息发送目标的依据
* Breaking Change: `/fetchMessage` 接口返回带状态码对象， [详情](https://github.com/mamoe/mirai-api-http/blob/master/README.md#%E5%93%8D%E5%BA%94-%E8%BF%94%E5%9B%9Ejson%E5%AF%B9%E8%B1%A1)

### 新增
* 支持 `FlashImage` 闪照类型，处理同 `Image`, `imageId` 可通用

### 修复
* 处理 `消息过长` 异常 (code: 30)
* 处理意料之外的 `Bot被禁言` 异常 (code: 20), #34



## \[1.5.0-pre\] - 2020-04-05

### 变更

* 更新 `core` 依赖到 0.33.0
* 所有 `messageId`, `time` 由 `Long` 变更为 `Int`
* `Quote` 消息类型

> 已知BUG： Quote的id错误。该问题将在1.5.0正式版本中修复



## \[1.4.1\] - 2020-04-03

### 变更

* 更新 `core` 依赖到 0.32.0



## \[1.4.0\] - 2020-03-31

### 变更

* 更新`core`依赖到 0.31.4
* 更新`console`依赖到 0.3.9

### 新增

* `Command`可获取发送者的信息，详情见`REAMDME.md`
* 支持戳一戳，`Poke`消息

### 修复

* 插件重启后`HTTP Server`没有停止导致端口被占用, closed #25
* 开启websocket导致Session长时间未被使用而回收
* Bot被禁言时，发送消息抛出异常。添加新状态码(state code 20)
* `Quote`消息类型丢失



## \[1.3.2\] - 2020-03-26

### 修复
* command接口的若干异常

### 变更

* 更新`core`依赖到 0.30.1
* 更新`console`依赖到 0.3.7



## \[1.3.1\] - 2020-03-23

### 修复
* 修复CORS无法处理复杂请求(预检请求)

### 变更

* 更新`core`依赖到 0.29.1



## \[1.3.0\] - 2020-03-22

### 变更

* 更新`core`依赖到 0.29.0
* 更新`conosle`依赖到 0.3.5

### 新增
* 支持配置CORS，初始默认允许所有域名
* 支持注册指令到`Mirai-console`
* 支持发送指令到`Mirai-console`
* 支持通过websocket获取`Mirai-console`指令触发事件
* 支持获取`Mirai-console`中`bot`的`managers`

> 支持通过指令和`manager`两个框架，使用http-api开发符合`Mirai`规范的插件

### 修复
* 修复撤回事件序列化错误的异常



## \[1.2.3\] - 2020-03-18

### 变更

* 更新`conosle`依赖到 0.3.4
* 错误信息从日志输出，而从非错误输出流

### 新增

* 支持通过`path`参数直接发送本地图片
* `UploadImage`将图片缓存到插件文件夹
* `UploadImage`返回图片缓存路径



## \[1.2.2\] - 2020-03-14

### 变更

* 弃用Bot事件中的`new`属性，使用`current`属性代替。`new`将在若干个版本后移除。
* 更新Web服务依赖版本

### 新增

* 支持消息撤回事件

### 修复

* 修复出现IgnoreEvent parse error的错误



## \[1.2.1\] - 2020-03-09

### 修复

* 配置文件中的`enableWebSocket`->`enableWebsocket`




## \[1.2.0\] - 2020-03-09

### 变更

* `mirai-core` 更新至 `0.27.0`
* `mirai-console` 更新至 `0.3.3`
* `kotlin`到`1.3.70`
* 好友对象属性`nickName`->`nickname`

### 新增

* 好友对象`nickname`属性可用（不再是空字符串）
* 缓存消息表的缓存大小可配置
* 增加通过`config`接口获取和修改指定session有效的配置
* 支持通过websocket获取消息与事件
* 支持XML、JSON、小程序富文本消息

### 修复

* Quote消息类型属性与文档不一致：`imageId`->`id`
* `uploadImage`接口无法处理异常的问题
* 击毙一些不为人知的BUG(s)



## \[1.1.1\] - 2020-03-07

### 修复

* `uploadImage`接口返回500的异常



## \[1.1.0\] - 2020-03-06

### 变更

* `mirai-core` 更新至 `0.25.0`
* `mirai-console` 更新至 `0.3.2`
* `uploadImage`接口由返回imageId字符串 变更为 返回包含`imageId`和`url`的json对象

### 新增

* `Source`消息类型增加属性`time`表示时间戳
* `Quote`消息类型增加`groupId`、`senderId`、`origin`3个属性
* `Face`消息类型增加`name`属性
* 支持配置文件修改服务端口号和初始`authKey`



## \[1.0.0\] - 2020-03-01

### 变更

* `mirai-core` 更新至 `0.23.0`
* json解析采用非严格模式，将忽略无用参数

### 新增

* 支持引用消息（Quote）的消息类型
* 支持通过messageId获取一条被缓存的消息
