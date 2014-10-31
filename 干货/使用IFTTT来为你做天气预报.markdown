
    
  
    ![picture](http://tp4.sinaimg.cn/2693448851/180/5693923463/1)
    

    爱薇薇
  
      

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/home-262e288438e1edb07c4cf2e2d0804dfb.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/weibo-e6860361a21f50530184f82f276acab3.png)
  


    
      
        #使用IFTTT来为你做天气预报
        
          
            
              ![picture](http://tp4.sinaimg.cn/2693448851/180/5693923463/1)
            
            +爱薇薇
        
        
    
    发表于 

    
      日记本

    2014-10-21 14:01

    

    阅读量: 563
  


        
          ##引子
  前几天IFTTT开放了微博的接口，使得IFTTT在国内的实用性有了进一步的提升。说这个之前，先介绍一下IFFTT。（针对互联网小白而写，大神绕道）

##0，关于IFTTT
  IFTTT是一个网站，是一个app，最主要的是一个服务。它的全称是If This Then That，顾名思义，IFTTT就是全称的缩写了（废话）。这个服务的核心是"This"和"That"，This就是条件，而That就是行为啦。IFTTT就是通过一个条件来激发另一个行为，比如

  <code>if
 “我更新了微博”
then
“在twitter也发一条一样的（同步）”</code></pre>
<p>或者我们的天气预报

  <code>if
天气预报服务在运作
then
在每天早上8点发一条描述哈尔滨天气情况的微博，并@一下薇薇</code></pre>
<p>IFTTT就是提供这样简单的服务将条件和行为联系起来行为，而他们的精神就是使互联网为我所用。

##1，注册IFTTT
  IFTTT有自己的网站，地址是 http://ifttt.com 。我们点击它
看到欢迎页

![picture](http://img02.taobaocdn.com/imgextra/i2/665732193/TB26dXvaVXXXXcTXXXXXXXXXXXX_!!665732193.png)
1
  点击join IFTTT即可。然后输入自己的用户名密码注册邮箱，点击创建账户。

![picture](http://img02.taobaocdn.com/imgextra/i2/665732193/TB2KBRraVXXXXbBXpXXXXXXXXXX_!!665732193.png)
2
  然后就出现一个默认的条件行为组合，点use就行了，不用等会可以关闭。

![picture](http://img02.taobaocdn.com/imgextra/i2/665732193/TB2m3lAaVXXXXauXXXXXXXXXXXX_!!665732193.png)
3
  然后就进入My Recipes的主界面，我们看到，现在只有一个组合，就是我们刚刚选择use那个组合，可以点击第一个开关按钮把它关闭啦。

![picture](http://img02.taobaocdn.com/imgextra/i2/665732193/TB2f18waVXXXXb2XXXXXXXXXXXX_!!665732193.png)
4

##2，组合中的条件“This”
  接下来，我们点击Create a Recipe，出现如下界面。我们点击This

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2NtltaVXXXXaLXpXXXXXXXXXX_!!665732193.png)
5
  出现一个搜索Channel的栏目，我们在里边输入weather，找到weather服务，点击之

![picture](http://img03.taobaocdn.com/imgextra/i3/665732193/TB26v4xaVXXXXbsXXXXXXXXXXXX_!!665732193.png)
6
  这里IFTTT默认设置的是按需开放Channel，所以，点开了Weather我们必须点Activate来激活它。

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2EIRsaVXXXXbaXpXXXXXXXXXX_!!665732193.png)
7
  激活后，会弹出一个窗口让你选择城市，你输入你想预报的城市，这里我们写上“哈尔滨”，然后选择，注意这个天气服务收录了全球大部分的城市，所以必须得看清楚，不要选到其他国家的了，一定是China。

![picture](http://img01.taobaocdn.com/imgextra/i1/665732193/TB2ViVxaVXXXXbQXXXXXXXXXXXX_!!665732193.png)
8
  现在可以点击continue了

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2ZuNBaVXXXXXMXXXXXXXXXXXX_!!665732193.png)
9
  下一个就是需要选择的服务，我们选择第一个今日天气预报

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2wgtAaVXXXXaaXXXXXXXXXXXX_!!665732193.png)
10
  然后设置时间，选择你想接收的时间

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2NSxraVXXXXa_XpXXXXXXXXXX_!!665732193.png)
11
  到这里，条件部分我们就弄好了。接下来是行为部分。

##3，组合中的行为“That”
  点击That

![picture](http://img03.taobaocdn.com/imgextra/i3/665732193/TB2KitAaVXXXXahXXXXXXXXXXXX_!!665732193.png)
12
  和this一样，我们先要选择服务Channel，搜索框中搜索微博

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2XKNvaVXXXXc1XXXXXXXXXXXX_!!665732193.png)
13
  还是一样点激活

![picture](http://img01.taobaocdn.com/imgextra/i1/665732193/TB2RvpxaVXXXXbwXXXXXXXXXXXX_!!665732193.png)
14
  弹窗出来，会让你对IFTTT进行授权，这里和一般的微博授权是一样的，授权成功就出现以下内容。

![picture](http://img03.taobaocdn.com/imgextra/i3/665732193/TB2I4twaVXXXXbPXXXXXXXXXXXX_!!665732193.png)
15
  点击了Done后选择行为，默认的就是发一条微博

![picture](http://img01.taobaocdn.com/imgextra/i1/665732193/TB2AItsaVXXXXaTXpXXXXXXXXXX_!!665732193.png)
16
  然后，我们看到发微博的格式，默认的是今天的状态，使用华氏温度，我们需要自己设置

![picture](http://img03.taobaocdn.com/imgextra/i3/665732193/TB2o3FsaVXXXXaWXpXXXXXXXXXX_!!665732193.png)
17
  点右边那个实验瓶的按钮，会出现一个选框，我们选择摄氏度的表现，最高温用HighTempCelsius，最低温用LowTempCelsius

![picture](http://img01.taobaocdn.com/imgextra/i1/665732193/TB2a5dBaVXXXXXDXXXXXXXXXXXX_!!665732193.jpg)
18
  写出来如下两个花括号是在编辑状态下的形式，可以不用管它

![picture](http://img03.taobaocdn.com/imgextra/i3/665732193/TB2HtpwaVXXXXcgXXXXXXXXXXXX_!!665732193.jpg)
19
  最后是这样，你可以在末尾艾特你想@的人，这里我们艾特一下薇薇，下面的photo url是图片的地址，保持默认就好。

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2EK8waVXXXXb9XXXXXXXXXXXX_!!665732193.png)
20
  接下来，点击Create Recipe，下边那个是会在这个行为被激发的时候给你推送通知，前提是你要去激活iOS或者安卓的Channel。

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2f4RxaVXXXXblXXXXXXXXXXXX_!!665732193.png)
21

##4，善后
  上一步点击创建后就会跳转到如下的页面，这种状态时激活状态，我们同样可以点击右边那个开关按钮来关闭，关闭后就会变成灰色啦。

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB2400taVXXXXXVXpXXXXXXXXXX_!!665732193.png)
22
  到时间，会在你的微博自动发送这样的信息

![picture](http://img02.taobaocdn.com/imgextra/i2/665732193/TB2iThwaVXXXXb_XXXXXXXXXXXX_!!665732193.png)

  再说说更换城市的问题，你可以在顶部状态栏那儿找到Channels这个栏目，点击之，跟以前一样，我们搜索天气weather

![picture](http://img04.taobaocdn.com/imgextra/i4/665732193/TB28tXvaVXXXXcwXXXXXXXXXXXX_!!665732193.png)
23
  我们可以查看此时这个服务的状态，显示城市是哈尔滨。如果需要更换城市，就点击Edit Channel，然后改为你喜欢的城市就可以啦。

![picture](http://img02.taobaocdn.com/imgextra/i2/665732193/TB2KndzaVXXXXaIXXXXXXXXXXXX_!!665732193.png)
24
  相应的，你可以找到安卓或者iOS的通知服务，激活就行啦，前提是你要去装一个IFTTT的app，然后用这个账号登陆。

##尾声
  IFTTT还有很多好玩的用法，有兴趣就自己去探索，互联网也是一样，互联网不光有游戏可以玩，不光有百度可以搜索。当生活真正为互联网所改变而美好的时候，我们才称这个时代为“互联网时代”。
我们都在谈情怀，我觉得IFTTT就是提供的服务就很有情怀，"Put the internet to work for you."，感谢这些为我们免费提供美好产品的企业、团队和个人吧。


<h5>资料及教程：</h5>

+关于IFTTT
+IFTTT客户端下载-iOS
+IFTTT客户端下载-Android
+简书：如何同步微博到twitter
+我的博客：自动保存知乎日报-深夜食堂到你的pocket


        
           引子 
 前几天 IFTTT 开放了微博的接口，使得IFTTT在国内的实用性有了进一步的提升...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


