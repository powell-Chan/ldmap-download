# ldmap-download

#### 介绍
一个简易的下载谷歌、百度、高德等地图分片的项目（离线地图分片）

#### 软件架构
基于SpringBoot构建，一个后端窗口操作的小工具


#### 安装教程

1.  Maven构建或者直接在项目中使用
2. 打Jar包后使用 java -jar 项目 （需要安装有Java环境）

#### 使用说明

1.  选择下载区域以及保存路径
2.  在config/download-map.properties配置文件中配置file.mapImgPath以发布地图
3.  地图默认自动发布，如果修改了发布路径需要重启项目；
4.  地图发布后可以在前端JS API中使用‘http://localhost:9000/img/{z}/{x}/{y}.png’来访问加载
5.  此功能适用于大部分地图，默认是高德，如需修改下载地图可以选择config/download-map.properties中修改map.baseurl来选择；
6.  page下有一个示例，项目启动后直接将该页面用浏览器打开即可

#### 参与贡献

1.  Deason.Z构建了此项目
2.  Deason.Z目前在维护项目的运行

#### 声明
本项目属于个人研究使用，请勿商用。
