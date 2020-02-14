Vip视频无广告解析
vip 视频解析，支持主流视频网站，仅学习使用  

前言
国内现在视频类知名网站主要有：爱奇艺、优酷、腾讯视频、搜狐视频、乐视视频、芒果TV、Bilibili等。这些网站都有各自的付费VIP视频，虽然本人不怎么看视频。但是，生活中很多同事、朋友都在追剧，在办公室在学校经常会听到某某有没有XX视频的VIP账号？生活中其实很多人都开通了这些视频网站的VIP。然而，依旧问题严重，因为很多电影&电视剧是版权独家的，只有某个视频站才能观看，那么买的其他站的VIP那不就是无效了吗？(前面这段描述是抄的...)

智能电视和盒子上找资源同样是件麻烦事，广电总局一纸命令，很多视频网站都停止了TV版的app开发和维护，怎么看视频是个问题。投屏好像也不是那么方便，于是就萌发了这个想法，把手机上解析出来的视频地址同步推送到电视上，省去按遥控的麻烦(这段是自己写的)

[Demo](https://github.com/zqq5054/VipVideo/blob/master/demo.apk?raw=true)

截图:
![截图](https://raw.githubusercontent.com/zqq5054/VipVideo/master/screenshoot/screenshoot01.jpg)

github地址：https://github.com/zqq5054/VipVideo  

利用第三方接口在线播放各大视频网站VIP视频
项目根目录有demo

各大视频网站视频解析已经不是什么秘密，某度一搜一大把
手机上利用webview解析，广告一大把
视频上跑马灯有境外菠菜网的广告就算了，界面还有很多少儿不宜的广告
受疫情影响，失业了，闲着也是闲着，顺手写了个无广告解析
基本思路：
利用隐藏的webview解析视频,达到隐藏广告的目的 

在shouldInterceptRequest里拦截资源

取到视频的播放地址，然后利用腾讯X5内核或者ijk等播放器播放

支持智能电视和电视盒子上使用

电视上找资源麻烦且遥控器操作不方便

电视上起一个微型http服务器(扫描二维码绑定ip地址)，手机上解析到视频地址后，把视频地址通过http传到电视端
电视端取到地址，调用播放器播放视频
电视上或者电视盒子配置低，腾讯TBS视频播放器好像不行，系统webview播放也不行，但是ijk是可以的
可以考虑手机和电视分两端，电视只需要播放和http服务器功能就可以

用到的第三方开源项目如下
android端http Server:https://github.com/koush/AndroidAsync
二维码扫描和生成:https://github.com/yipianfengye/android-zxingLibrary
腾讯TBS  x5内核
DKVideoPlayer 播放器(集成ijk等):https://github.com/xusigh/dueeeke-DKVideoPlayer
其他:Gson 等等

视频解析讨论群:1053200636
