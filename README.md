

##背景

&#160;&#160;相信大家在开发的过程中肯定会遇到创建悬浮框需要权限的问题，如果不申请权限会出现以下错误：

```
 token null is not valid; is your activity running?
```

&#160;&#160;或者类似的错误，这类的文章在百度上面经常可以查看到，但是基本上都是停留在Android6.0以下的，文章比较老旧。随着6.0之后的巨大变化，主要是高版本之后Google开启了严苛模式，使用户的隐私更加的难获取，对某些敏感手机信息，我们不仅需要在AndroidMainfest.xml配置文件中申请权限,还要在代码中进行动态权限的申请，我们今天讨论的悬浮框权限就是其实的一个，今天我分享的这篇文章最高适配到最新的Android8.0，测试的手机包含了国内外的各大厂商的大部分主流手机。Demo最低适配到api 10，最高适配api26。

##思路

&#160;&#160;我们都清楚Android碎片的化的严重，但其实也有个大致版本的分割线，为了迎合此文，我们大致把手机大致分为3个区间，其实19，22，16的版本都具有很明显的变换。

*  api<19 ,android 4.4以前的
* api>22,api<26, Android4.4与8.0之间的
* api>=26 最新版本的Android手机

##功能


1.唤醒一个悬浮框
2.点击底部虚拟按钮，悬浮框消失不见。
3.监听悬浮框的按钮


##技术实现

###前提：

1. 尽量不在配置文件中申请权限。
2. 尽量不在当前APP申请动态权限。
3. 如果绕不过去前两者，则引导用户开启权限。

    
###核心代码：

&#160;&#160;其中最关键的是圈中代码，对type进行的适配。有兴趣的可以下载Demo在研究研究，该篇文章以研究为主。
![这里写图片描述](http://img.blog.csdn.net/20180223091013518?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzY1MTQwNQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
 
    
##测试机型

| 厂商 | 型号 | Android版本号 | 系统 | 配置静态权限 | 动态悬浮框开关 | targetSdkVersion | 交互成功 |
| :-------- |---:| :--: | :--: | :--: | :--: | :--: | :--: |
|SAMSUNG|三星Nexus|4.3(18)|4.3|有|无|无|Y
|SAMSUNG|三星Note3|5.0(21)|5.0|无|无|无|Y
|SAMSUNG|三星 G9300|7.0(24)|7.0|无|无|无|Y
|MIUI|小米4|6.0.1(23)|MIUI 8.5|无|无|无|Y
|MIUI|小米3|4.4.4(19)|MIUI 9.2|有|有|无|Y
|MIUI|小米 note |7.0(24)|MIUI 8.7|有|有|无|Y
|MIUI|小米 6 |7.1.1(25)|MIUI 9.2|无|有|无|Y
|魅族|MEIZU MX5|5.0.0(21)|Flyme 6.2|无|无|无|Y
|HTC|HTC A9W|7.0(24)|7.0|无|无|无|Y
|Google|Nexus 5|5.1.1(22)|5.1.1|无|22|Y
|华为|Nexus 6P|8.0.0(26)|8.0.0|无|无|无|Y
|Google|Pixel XL|8.0.0(26)|8.0.0|无|无|<=22|3秒之后消失
|Google|Pixel XL|8.0.0(26)|8.0.0|有|有| api>22|Y
|努比亚|Nubia 4.0|5.1.1(22)|nubia v4.0|无|无|Y
|华为|华为 P9|7.0.0|7.0.0(25)|有|有|api>22|Y
|华为|华为 Nova 2s|8.0.0(26)|EMUI 8.0|无|无|<=22|3秒之后消失
|华为|华为 Nova 2s|8.0.0(26)|EMUI 8.0|有|有|api>22|Y
|联想|Lenovo K920|4.4.2(19)|4.4.2|无|无|无|Y
|OPPO|colorOs v1.2.0|4.3(19)|4.3|有|无|无|Y
|OPPO|OPPO R11|7.1(25)|7.1|无|无|无|Y
|vivo|vivo Y55A|6.0.1(23)|6.0.1|无|有|无|Y
|vivo|vivo X9|7.1.1(23)|7.1.1|无|有|无|Y

静态权限申请：
```
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
```
##结论:

 
|API Level| 厂商|targetSdkVersion |静态权限|动态权限|WindowManager|
|:---|:---:|:---:|:---:|:---:|:---:|
|api<19(4.3)|All|-|需要|-|`TYPE_PHONE`|
|19<=api<26|All(小米,vivo除外)|-|<font color=red>不需要</font>|<font color=red>不需要</font>|`TYPE_TOAST`|
||小米|-|需要|需要|`TYPE_PHONE`|
||vivo|-|<font color=red>不需要</font>|需要|`TYPE_PHONE`|
|api>=26(8.0)|All|version>=23|需要|需要|`TYPE_APPLICATION_OVERLAY`|
|api>=26(8.0)|All|version<23|<font color=red>不需要</font>|<font color=red>不需要</font>|`TYPE_TOAST`|

注：
 * 优先使用WindowManager.LayoutParams.TYPE_TOAST，但4.4版本之前无法接收事件也即无法交互, 7.1 版本后源app失去焦点时有个定时器自动隐藏掉这个 Toast Window
 
 * 适配api>=26的，且 `targeSdkVersion>=23` 不仅需要静态配置，还需要动态配置悬浮框权限, `type=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`。如果不在清单中申请静态配置，无法打开动态启动的悬浮框权限; `targeSdkVersion<23`,如果依据不增加任何权限，悬浮框出现3秒之后自动消失,`type=WindowManager.LayoutParams.TYPE_TOAST`，**这个不一定，少部分机型是不会消失的，比如华为代工的nexus 6p**
 * MIUI V5的以上的手机根据版本号与机型进行调整。少数是不需要配置权限的，但是一定需要动态开启悬浮框权限，建议都进行静态配置，并且调用配置悬浮框权限。
 * 如果打开了静态控制权限，19<=api<26 的情况下，则动态申请一定要打开，`type=WindowManager.LayoutParams.TYPE_PHONE`；
 * vivo手机需要动态打开悬浮框权限。

答疑：
1.为什么4.4之下使用 `TYPE_TOAST`没有效果，而大于等于4.4就有效果了？
&#160;&#160;&#160;&#160;低版本中使用这个参数只能够进行展示效果，不能被点击，而高版本中在这个参数上面加了一段逻辑，没错。。。就可以点击了，可以获取焦点的逻辑，系统已经帮我们做了。因为系统帮我们加了两个设置：
2.为什么在8.0上面会有个3秒自动消失的结果？
&#160;&#160;&#160;&#160;因为Google预留这个WindowManager.LayoutParams.TYPE_TOAST本质不是给开发者用的，而是给手机厂商用的，结果没想到国内悬浮框的需求量巨大，想到了这么一个后门来绕过敏感权限的申请，所以在7.1之后就慢慢的废除了，在7.1版本增加了定时消失的功能，并且该定时没公开。
![这里写图片描述](http://img.blog.csdn.net/20180223090002779?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzY1MTQwNQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
3.为什么小米和vivo都需要动态配置打开悬浮框权限？
&#160;&#160;&#160;&#160;没有为什么，大厂就是会玩，自己强行加上了这个功能。MIUI 5之前是可以无权限打开的，高版本后就不行了，但有个别型号是可以不需要开通权限的，比如小米4W;vivo就更奇葩了，无论有没有增加权限，但是一定要手动打开悬浮框的权限 。
4.为什么使用`type=WindowManager.LayoutParams.TYPE_PHONE`需要在配置文件中增加权限？
&#160;&#160;&#160;&#160;这个不一定，除了vivo这个奇葩定制除外，绝大部分都要遵循这个规则。
![这里写图片描述](http://img.blog.csdn.net/20180223090635302?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzY1MTQwNQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

###最后

根据版本迭代趋势，我还是建议大家遵循Google大法的开发规则，老老实实的申请权限。(部分图片来源于网络）
Demo地址是：https://github.com/LiuLei0571/FloatDemo.git


 


