# 接口适配器

接口适配器是对接 `mirai-core` 为其提供网络接口的具体实现.

从 `mirai-api-http 2` 版本开始，接口适配器提供更为模块化的网络接口实现. 通过启用不同的适配器可以实现不同的接口调用形式甚至数据格式。

## 内置接口适配器 (built-in adapter)

`mirai-api-http 2` 内置若干 `adapter`. 所有 `built-in adapter` 采用相同的接口逻辑和数据格式标准, 只在调用形式上有所不同

| 适配器名称     | 描述                           | 文档                                      |
| ------------ | ------------------------------ | ---------------------------------------- |
| http         | 提供基于轮询的 http 接口          | [http](HttpAdapter.md)                   |
| ws           | 提供 websocket server 形式的接口 | [ws](WebsocketAdapter.md)                |
| reverse-ws   | 提供 websocket client 形式的接口 | [reverse-ws](ReverseWebsocketAdapter.md) |
| webhook      | 通过 http 反向调用形式的接口      | [webhook](WebhookAdapter.md)              | 

## 自定义扩展接口适配器

接口适配器支持扩展, 从外部加载, 接口适配器开发详情查看 [CustomizedAdapter](CustomizedAdapter.md)

## 关于 verifyKey, session 和 cache

`verifyKey`, `session`, `cache` 是 `built-in adapter` 特有的校验逻辑. 目前只有内置的 `adapter` 实现了身份校验(`webhook adapter` 除外).
用于校验接口调用者的身份, 以及缓存接口实现过程中的消息缓存和上下文.

对于 `Customized Adapter`, 没有强制实现这三个标准, 是否提供校验逻辑, 需要阅读具体 `adapter` 的使用说明.

## 关于 verifyKey, session 和 cache 的作用域

对于 `built-in adapter` 而言, `verifyKey`, `session`, `cache` 是全局的. 即, 若开启了多个 `adapter` 的情况下,
多个客户端通过多个 `adaptor` 使用同一个 `session`, 客户端之间的操作会相互影响.

举例:

1. 客户端A, 通过 `http adapter` 申请到 `session1` 并监听 
2. 客户端B, 通过 `websocket adatper` 监听 `session1`
3. 当 `session1` 绑定的 `bot` 接收到事件时, 客户端A 和 客户端B 都会收到消息推送
4. 客户端A发送消息并取得 `messageId1`
5. 客户端A 和 客户端B 均可以通过 `messageId1` 获取到消息缓存

总而言之, `session` 不在乎是否有多个客户端通过哪种 `adapter` 在监听同一个 `session`, 只要 `session` 收取到消息事件，都会无差别地通知客户端.

虽然不建议多个客户端监听同一个 `session`, 但是在分布式操作上有一定作用
