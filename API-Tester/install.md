## 1.安装chrome调试插件[Talend API Tester - Free Edition](https://chrome.google.com/webstore/detail/talend-api-tester-free-ed/aejoelaoggembcahagimdiliamlcdmfm)

![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710224754.png)

## 2.下载[mirai-http-api.json](https://raw.githubusercontent.com/daofeng2015/mirai-api-http/master/json/mirai-http-api.json)配置好的api文件，环境变量配置文件[PATH.json](https://raw.githubusercontent.com/daofeng2015/mirai-api-http/master/json/PATH.json)

## 3.导入配置文件.
### 3.1 点击 ![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710225505.png) 新建环境变量名称,名称随意


点击此处导入PATH.json文件![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710225616.png)

选中此处 ![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710225754.png)
点击导入

点击开始编辑环境配置 ![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710230023.png)

按照截图中配置好环境(session需要自己获取,等会说明)![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710230331.png)

### 3.2 点击左下角开始导入api配置文件![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710230614.png)



如图选择一样 ![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710230658.png)

## 认证会话
第一步开始会话认证 ![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710230844.png)

![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710231028.png)

把上一步获取到的session复制到校验会话中激活 
![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710231229.png)

成功完成后需要把环境变量中的session的值更新为新的session值
![](https://cdn.jsdelivr.net/gh/daofeng2015/image/img/20200710231443.png)


更新完成后就可以测试其他API调用了,如有不懂请参考http-api文字版详细说明
