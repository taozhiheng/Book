
    
  
    ![picture](http://tp4.sinaimg.cn/1671682487/180/5675820596/1)
    

    继续海阔天空
  
      我们在自己的世界里独自狂欢

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/home-262e288438e1edb07c4cf2e2d0804dfb.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/weibo-e6860361a21f50530184f82f276acab3.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/douban-093d391615fe9b2bcb5a9aea3752e615.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/google_plus-6297e2bef72e74a2b554863b0969da47.png)
  


    
      
        #解决chrome无法登陆google账号的问题
        
          
            
              ![picture](http://tp4.sinaimg.cn/1671682487/180/5675820596/1)
            
            +继续海阔天空
        
        
    
    发表于 

    
      海阔天空的云

    2014-10-31 09:13

    

    阅读量: 170
  


        
          #问题
  由于你懂的原因，现在使用chrome浏览器，首先要面临的两个问题是：

  1用谷歌官网上的在线安装方式进行安装（我改过hosts之后还是不可以），所以只能通过下载完整的安装包进行安装。这里我提供一个最新稳定版本的chrome38的安装包：http://pan.baidu.com/s/1dDzTZup  请自行下载。

  2即使你的chrome安装成功，登陆google账号又是一个问题。要知道chrome浏览器的好处就是能够有很多扩展来帮你实现很多功能，提高它的性能，通过登陆google账号来将你之前的扩展同步过来，但是现在无法登陆google账号就直接阻止了你个性化你的chrome。chrome不能登陆google账号显示的状态就是不能连接服务器。

  这里说一下我的问题：我是在清除了系统的cookies之后，再次登陆chrome的时候发现chrome被初始化了，原因大概是因为我将cookies清除掉了，而登陆状态也被清除，原来同步的扩展也就自动消失了。所以请大家<strong>谨慎清除coockies</strong>

#解决办法
  首先，我们知道有一个神器叫做goagent,在local文件夹下就有一个后缀名为crx的文件
如图：

![picture](http://hktkdy.qiniudn.com/crx.png)

  而这个文件：SwitchySharp.crx就是 chrome上的神奇代理扩展啦。我们不能够登陆chrome应用商店，也就没有办法下载这个扩展进行安装，也就没有办法来登陆google账号这是我们一直纠结的问题，但是很多人可能并不知道：
<strong>chrome是可以本地安装的</strong>

  具体操作时  鼠标点击chrome的三条杠，点击设置，在弹出的界面里选择扩展程序，这个时候将你在local文件夹里的SwitchySharp.crx 文件拖入到这个页面里面，你将会看到这个扩展将被安装，点击确定即可。

  接下来我们只需要对这个扩展进行简单的配置：具体方法请自行google。（什么？你说你还没有配置好不能上google，请选择http://wen.lu）,在全局模式下再次登陆chrome的google账号，于是：

  界面是不是很美！
<strong>success</strong>

#写在后面
  写这篇文是因为本人的确遇到了这个问题，我的折中方法是下载了一个非安装版（便携版），这个便携版当时下载的时候是37V。而当我想要再装另外一个扩展的时候，提示我版本低不能安装，作为一个强迫症患者，觉得还是有必要再装一个安装版的，至少以后不会有其他这样类似的麻烦。
如果你看完这篇还有疑问，请评论。如没有及时回复，请到新浪微博@继续海阔天空  找我。也欢迎更多的朋友关注我。


        
           问题 
 由于你懂的原因，现在使用chrome浏览器，首先要面临的两个问题是： 
 1用谷歌...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


