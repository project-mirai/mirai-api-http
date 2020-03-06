# 更新日志



## [1.1.0] - 2020-03-06

### 变更

* `mirai-core` 更新至 `0.25.0`
* `mirai-console` 更新至 `0.3.2`
* `uploadImage`接口由返回imageId字符串 变更为 返回包含`imageId`和`url`的json对象

### 新增

* `Source`消息类型增加属性`time`表示时间戳
* `Quote`消息类型增加`groupId`、`senderId`、`origin`3个属性
* `Face`消息类型增加`name`属性
* 支持配置文件修改服务端口号和初始`authKey`



## [1.0.0] - 2020-03-01

### 变更

* `mirai-core` 更新至 `0.23.0`
* json解析采用非严格模式，将忽略无用参数

### 新增

* 支持引用消息（Quote）的消息类型
* 支持通过messageId获取一条被缓存的消息
