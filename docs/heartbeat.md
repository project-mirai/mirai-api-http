# 心跳服务

心跳服务实现原理是，通过每一定时间向一个其他服务器发起一个POST请求，达到实现通知目标服务器MiraiApiHttp依然在线的目的。

## 作用

- 注册服务到发现中心。
- 通过一个中央服务器，来检测API HTTP服务是否离线，从而可以提示管理员及时修复离线的服务。

## 启用

因为MiraiApiHttp自带心跳服务，你只需要在setting.yml配置文件里面配置一下内容，即可使用。

```yaml
# 可选，心跳服务
heartbeat:
  # 可选，是否启用心跳，默认不启用
  enable: true
  # 可选，心跳启动延迟，默认1000
  delay: 1000
  # 可选，心跳周期，默认15000
  period: 15000
  # 必选，心跳PING的地址列表
  destinations:
    - https://postman-echo.com/post
  # 可选，心跳PING时需要带上的请求体
  extraBody:
    # 填上你需要的请求体（如QQ等信息）
    qq: 775150
  # 可选，心跳PING时需要带上的请求头
  extraHeaders:
    # 填上你需要的请求头（如授权信息等）
    Authorization: basic xxx
```
