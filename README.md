# ldmap-download

#### 介绍
一个简易的下载谷歌、百度、高德等地图分片的项目（离线地图分片）

#### 软件架构
基于SpringBoot构建，一个后端窗口操作的小工具


#### 安装教程

1.  Maven构建或者直接在项目中使用
2. 打Jar包后使用 java -jar 项目 （需要安装有Java环境）

#### 使用说明

1.  通过修改配置文件来选择下载地图的类型，（第一次运行需要修改download-map.properties中map.Lv6.flag为true来下载大地图框架）；
2.  项目运行后需要在窗口中输入要下载区域的经纬度（矩形区域，左下角的经纬度与右上角的经纬度）；
3.  download-map.properties中有一些关于本地存储、地图类型的设置；
4.  具体的使用流程参见 https://www.iqiyi.com/v_19rrmucyoc.html?vfm=2008_aldbd


#### 参与贡献

1.  Deason.Z构建了此项目

#### 声明
本项目属于个人研究使用，请勿商用。
