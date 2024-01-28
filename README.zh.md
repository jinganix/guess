[![CI](https://github.com/jinganix/guess/actions/workflows/ci.yml/badge.svg)](https://github.com/jinganix/guess/actions/workflows/ci.yml)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# 猜我是谁

`猜我是谁`微信小程序源码

## 运行项目

### 后端

#### Linux和MacOS

```shell
git clone git@github.com:jinganix/guess.git
cd guess/service/guess
./gradlew service:guess:bootRun --args='--core.weapp.app-id=? --core.weapp.app-secret=? --core.url.db-mysql=? --spring.datasource.username=? --spring.datasource.password=?'
```

#### Windows

使用`./gradlew.bat`代替`./gradlew`

#### 参数说明

- core.weapp.app-id: 微信小程序的`app-id`
- core.weapp.app-secret: 微信小程序的`app-secret`
- core.url.db-mysql: mysql数据库连接地址，如`jdbc:mysql://127.0.0.1:3306/guess`
- spring.datasource.username: 数据库用户名
- spring.datasource.password: 数据库密码

### 前端

#### 运行命令

```shell
git@github.com:jinganix/guess.git
cd guess/frontend/weapp
npm install
npm start
```

#### 开发者工具设置

1. 开发者工具导入项目的dist目录：`guess/frontend/weapp/dist`
2. 关闭域名校验

   <img src="docs/devtools.setting.png" alt="Image" width="381" height="576">

## 扫码体验

<img src="docs/qrcode.jpg" alt="Image" width="300" height="300">

## 贡献

如果您有兴趣报告/修复问题并直接为代码库做出贡献，请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 获取更多信息，了解我们期望的贡献内容以及如何开始。
