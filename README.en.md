[![CI](https://github.com/jinganix/guess/actions/workflows/ci.yml/badge.svg)](https://github.com/jinganix/guess/actions/workflows/ci.yml)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

[中文版本](README.md)

# Guess

The source code of the 'Guess Who I Am' Wechat miniprogram, you can [scan to experience](#qr-code) the miniprogram

- Frontend development using `TypeScript` with `webpack` for building and bundling
- Backend development using `Java` and `Spring` framework with `Gradle` for building and packaging

## Running the Project

To run the project, you need to first clone the source code and navigate to the project's root directory.

```shell
git clone git@github.com:jinganix/guess.git
cd guess
```

### Backend

#### Running with docker-compose

- Modify the [application-local.yml](service/guess/src/main/resources/application-local.yml) file to configure the backend service of the project

- core.weapp.app-id: The `app-id` of the WeChat mini-program
- core.weapp.app-secret: The `app-secret` of the WeChat mini-program

If you have `docker` and `docker-compose` installed, you can start the backend service using the following command:

```shell
docker-compose up --build
```

#### Running with Gradle

You need to install JDK with the corresponding version specified in [.tool-versions](.tool-versions) and start a MySQL database.

Modify the application-local.yml file to configure the backend services of the project.

- core.weapp.app-id: The `app-id` of the WeChat mini-program
- core.weapp.app-secret: The `app-secret` of the WeChat mini-program
- core.url.db-mysql: The connection URL of the MySQL database, e.g., `jdbc:mysql://127.0.0.1:3306/guess`
- spring.datasource.username: The database username
- spring.datasource.password: The database password

The following command can be used to start the backend services on a Linux or macOS system:

```shell
./gradlew service:guess:bootRun
```

The following command can be used to start the backend services on a Windows system:

```shell
./gradlew.bat service:guess:bootRun
```

### Frontend

You need to install [node.js](https://nodejs.org/en) with the version specified in [.tool-versions](.tool-versions).

#### Running the Commands

```shell
git clone git@github.com:jinganix/guess.git
cd guess/frontend/weapp
npm install
npm start
```

#### Wechat devtools

1. Import`guess/frontend/weapp/dist` into Wechat devtools
2. Disable domain verification

   <img src="docs/devtools.setting.png" alt="Image" width="320" height="484">

## <a id="qr-code"></a>Scan to experience

<img src="docs/qrcode.jpg" alt="Image" width="240" height="240">

## Contributing

If you are interested in reporting/fixing issues and contributing directly to the code base, please see [CONTRIBUTING.md](CONTRIBUTING.md) for more information on what we're looking for and how to get started.
