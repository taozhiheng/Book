
    
  
    ![picture](http://tp3.sinaimg.cn/2737334170/180/5690253044/1)
    

    流星狂飙
  
      一个放荡不羁的攻城狮！

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/weibo-e6860361a21f50530184f82f276acab3.png)
  


    
      
        #REST风格的软件架构
        
          
            
              ![picture](http://tp3.sinaimg.cn/2737334170/180/5690253044/1)
            
            +流星狂飙
        
        
    
    发表于 

    
      安静地做个码神

    2014-10-21 23:44

    

    阅读量: 540
  


        
          >  如果一个网站不是 REST 风格架构，肯会被程序员鄙视一番！

  移动互联网的飞速发展，特别是移动互联网，给开发者带来了新的机遇和挑战。手机端除了app，我们还会经常接触到移动web，除了浏览器中，很多app里面也会使用web服务，我们会在手机上面做更多复杂的操作，老一代的系统架构已经不再适应了，需要更加规范和优秀的软件架构来应对今天的挑战，那就是 <strong>REST</strong> 。

##从 HTTP 协议说起
  首先的熟悉一个概念 URI，Web上可用的每种资源 -HTML文档、图像、视频片段、程序等 - 由一个通用资源标识符（Uniform Resource Identifier, 简称"URI"）进行定位。
例如：<code>http://www.bigertech.com/a.jpg</code>  

  Http协议定义了客户端与服务器交互的不同方法，最基本的方法有4种，分别是GET，POST，PUT，DELETE 


+若要在服务器上创建资源，应该使用 POST 方法。
+若要检索某个资源，应该使用 GET 方法。
+若要更改资源状态或对其进行更新，应该使用 PUT 方法。
+若要删除某个资源，应该使用 DELETE 方法。

  <strong> 资源多重表述 </strong>
针对不同的需求提供资源多重表述。这里所说的多重表述包括XML、JSON、HTML等。即服务器端需要向外部提供多种格式的资源表述，供不同的客户端使用。比如移动应用可以使用XML或JSON和服务器端通信，而浏览器则能够理解HTML。

  <strong>注意</strong>：HTTP规范中中，GET用于信息获取，而且应该是安全的和幂等的  

  实际开发中很多人违背了这个协议


+很多人贪方便，嫌 POST 使用表单的麻烦，更新资源时用了GET
+对资源的增，删，改，查操作，其实都可以通过GET/POST完成，不需要用到PUT和DELETE。  
+另外一个是，早期的但是Web MVC框架设计者们并没有有意识地将URL当作抽象的资源来看待和设计 。还有一个较为严重的问题是传统的Web MVC框架基本上都只支持GET和POST两种HTTP方法，而不支持PUT和DELETE方法。

  所以新的一套支持HTTP 软件架构风格出现了。

##什么是REST
>  REST (Representational state transfer）,表征状态转义。是 Roy Fielding 博士在2000年他的博士论文中提出来的一种 <strong>软件架构</strong> 风格。

  越来越多的服务使用这种软件架构来设计和实现，例如：Amazon.com提供接近REST风格的Web服务进行图书查找；雅虎提供的Web服务也是REST风格的。

  ** 值得注意的是，REST是设计风格而不是标准。而是通过表征（Representional ）来描述传输状态的一种原则。其宗旨是从资源的角度来观察整个网络，分布在各处的资源由URI确定，而客户端的应用通过URI来获取资源的表征。获得这些表征致使这些应用程序转变了其状态。随着不断获取资源的表征，客户端应用不断地在转变着其状态。

  REST软件架构使用了CRUD原则，该原则告诉我们对于资源（包括网络资源）只需要四种行为：创建（Create）、获取（Read）、更新（Update）和销毁（DELETE），就可以组合成其他无数的操作。其实世界万物都是遵循这一规律：生、变、见、灭。这个原则是源自于我们对于数据库表的数据操作：insert（生）、select（见）、update（变）和delete（灭），所以有时候CRUD也写作为RUDI（read update delete insert）。这四个操作是最基本的操作，即无法再细分的操作，通过它们可以构造复杂的操作过程，正如数学上四则运算是数字的最基本的运算一样。

##REST的要求

+客户端和服务器结构
+连接协议具有无状态性
+能够利用Cache机制增进性能
+层次化的系统

##关于状态
  应该注意区别应用的状态和连接协议的状态。HTTP连接是无状态的（也就是不记录每个连接的信息），而REST传输会包含应用的所有状态信息，因此可以大幅降低对HTTP连接的重复请求资源消耗。

##含状态传输的 Web 服务
  含状态传输的 Web 服务（也称为 RESTful Web API）是一个使用HTTP并遵循REST原则的Web服务。它从以下三个方面资源进行定义：


 1.直观简短的资源地址：URI，比如：<code>http://example.com/resources/</code>。
+传输的资源：Web服务接受与返回的互联网媒体类型，比如：JSON，XML ，YAML 等。
+对资源的操作：Web服务在该资源上所支持的一系列请求方法（比如：POST，GET，PUT或DELETE）。
PUT 和 DELETE 方法是幂等方法。GET方法是安全方法 （不会对服务器端有修改，因此当然也是幂等的）。幂等的意味着对同一URL的多个请求应该返回同样的结果。比如绝对值运算就是一个例子，在实数集中，有abs(a) = abs(abs(a)) 。  

##REST的实现
  各大客户端和服务器端都 REST 的风格架构都有相应的实现，包括 android、IOS 、web端

###web端
  jquery的ajax 函数

  <code>$.ajax({  
          type: 'PUT',   //options GET、POST、DELETE
          url: this.myurl, 
      });</code></pre>
###nodejs

+express 4.x 版本自带了 rest 的路由
+node-restify  
+restler 客户端使用rest

##REST和AJAX
<p>在Ajax出现以前，浏览器的功能相对比较弱，只能实现一些瘦客户端的功能，因此，Web应用的开发者们只能把功能的实现尽量向服务器端移，这样产生了现在被广泛使用的WebMVC架构模式。这样做，其实有很多地方已经违反了REST的架构约束，但在当时是没有办法的Ajax出现后，浏览器的能力大大增强了，这时依靠Ajax的能力真的有可能完全遵从REST的架构约束了，这就需要把许多功能前移，建造更强大的客户端了。这也许就是为什么REST会随着Ajax的出现而渐渐流行开来的原因，当然，Rails DHH的大力推广也有功不可没。

##REST的优点

+可更高效利用缓存来提高响应速度
+通讯本身的无状态性可以让不同的服务器的处理一系列请求中的不同请求，提高服务器的扩展性
+浏览器即可作为客户端，简化软件需求
+相对于其他叠加在HTTP协议之上的机制，REST的软件依赖性更小
+不需要额外的资源发现机制
+在软件技术演进中的长期的兼容性更好

  众所周知，对于基于网络的分布式应用，网络传输是一个影响应用性能的重要因素。如何使用缓存来节省网络传输带来的开销，这是每一个构建分布式网络应用的开发人员必须考虑的问题。

  HTTP 协议带条件的 HTTP GET 请求 (Conditional GET) 被设计用来节省客户端与服务器之间网络传输带来的开销，这也给客户端实现 Cache 机制 ( 包括在客户端与服务器之间的任何代理 ) 提供了可能。HTTP 协议通过 HTTP HEADER 域：If-Modified-Since/Last- Modified，If-None-Match/ETag 实现带条件的 GET 请求。

  REST 的应用可以充分地挖掘 HTTP 协议对缓存支持的能力。当客户端第一次发送 HTTP GET 请求给服务器获得内容后，该内容可能被缓存服务器 (Cache Server) 缓存。当下一次客户端请求同样的资源时，缓存可以直接给出响应，而不需要请求远程的服务器获得。而这一切对客户端来说都是透明的。


        
            如果一个网站不是 REST 风格架构，肯会被程序员鄙视一番！  
 移动互联网的飞速发展，...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


