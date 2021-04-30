## Websocket Adapter

提供基于 websocket server

### 配置文件

```yaml
adapterSettings:
  ws:
    ## websocket server 监听的本地地址
    ## 一般为 localhost 即可, 如果多网卡等情况，自定设置
    host: localhost
    ## websocket server 监听的端口
    ## 与 http server 可以重复, 由于协议与路径不同, 不会产生冲突
    port: 8080
    ## websocket 用于消息同步的字段为 syncId, 一般值为请求时的原值，用于同步一次请求与响应
    ## 对于由 websocket server 主动发出的通知, 固定使用一个 syncId, 默认为 ”-1“
    reservedSyncId: -1
```

### 接口一览

(未施工)
