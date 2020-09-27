## 上报服务

当MiraiApiHttp收到新的消息（事件，群消息，好友消息），都可以给指定地址发送包含该消息的POST请求。

### 启用

因为MiraiApiHttp自带上报服务，你只需要在setting.yml配置文件里面配置一下内容，即可使用。

```yaml
# 可选，上报服务
report:
  # 可选，是否启用上报，默认不启用
  enable: true
  # 可选，上报群消息的配置
  groupMessage:
    # 可选，是否上报，默认不上报
    report: true
  # 可选，上报好友消息的配置
  friendMessage:
    # 可选，是否上报，默认不上报
    report: true
  # 可选，上报临时消息的配置
  tempMessage:
    # 可选，是否上报，默认不上报
    report: true
  # 可选，上报事件消息的配置
  eventMessage:
    report: true
  # 必选，上报的地址列表
  destinations:
    - https://postman-echo.com/post
  # 可选，上报时需要带上的请求头
  extraHeaders:
    # 填上你需要的请求头（如授权信息等）
    Authorization: basic xxx
```
