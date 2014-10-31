
    
  
    ![picture](http://upload.jianshu.io/users/upload_avatars/19275/0dda24c49a09.jpg?imageMogr/thumbnail/90x90/quality/100)
    

    溪离欣洛
  
      FE | 绘画 | 爱生活 | 爱科技 | 旅游 | 文学

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/weibo-e6860361a21f50530184f82f276acab3.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/douban-093d391615fe9b2bcb5a9aea3752e615.png)
  


    
      
        #移动web页面头部书写 
        
          
            
              ![picture](http://upload.jianshu.io/users/upload_avatars/19275/0dda24c49a09.jpg?imageMogr/thumbnail/90x90/quality/100)
            
            +溪离欣洛
        
        
    
    发表于 

    
      移动Web开发

    2014-10-21 11:06

    

    阅读量: 171
  


        
            HTTP 标题信息(http-equiv) 和页面描述信息(name)

  <strong>http-equiv:</strong>
该枚举的属性定义，可以改变服务器和用户代理行为的编译。编译的值取content 里的内容。简单来说即可以模拟 HTTP 协议响应头。
最常见的大概属于Content-Type了，设置编码类型。如

  <code>&lt;meta http-equiv="Content-Type" content="text/html; charset=utf-8" /&gt;</code></pre>
<p>H5中可以简化为

  <code>&lt;meta charset="utf-8"&gt;</code></pre>
<p>http-equiv常见还有其它如下等（合理使用可增加 SEO 收录）。

  <code>Content-Language : 设置网页语言
Refresh : 指定时间刷新页面
set-cookie : 设定页面 cookie 过期时间
last-modified : 页面最后生成时间
expires : 设置 cache 过期时间
cache-control : 设置文档的缓存机制
...</code></pre>
<p><strong>name:</strong>
    该属性定义了文档级元数据的名称。用于对应网页内容，便于搜索引擎查找分类，如 keywords, description; 也可以使用浏览器厂商自定义的 meta， 如 viewport；

  <strong>viewport</strong>可视区域的定义，如屏幕缩放等。

  告诉浏览器如何规范的渲染网页。

  <code>&lt;meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0,  user-scalable=0;" name="viewport" /&gt;</code></pre>
<p>这个meta标签表示：强制让文档的宽度与设备的宽度保持1:1，并且文档最大的宽度比例是1.0，且不允许用户点击屏幕放大浏览；如果想设置用户可以进行缩放 user-scalable=yes;但是手机浏览器（UC）上依然无法缩放。

  <code>width – // [pixel_value | device-width] viewport 的宽度，范围从 200 到 10,000，默认为 980 像素
height – // [pixel_value | device-height ] viewport 的高度，范围从 223 到 10,000 
initial-scale – // float_value，初始的缩放比例 （范围从 &gt; 0 到 10）
minimum-scale – // float_value，允许用户缩放到的最小比例
maximum-scale – // float_value，允许用户缩放到的最大比例
user-scalable – // [yes | no] 用户是否可以手动缩放
target-densitydpi = [dpi_value | device-dpi | high-dpi | medium-dpi | low-dpi] 目标屏幕像素密度</code></pre>
<p><strong>format-detection</strong>对电话号码的识别&amp;&amp;<strong>email</strong>对EMAIL识别

  <code>&lt;meta content="telephone=no" name="format-detection" /&gt;</code></pre>
<p>meta标签表示：告诉设备忽略将页面中的数字识别为电话号码

  <code>&lt;meta name="format-detection" content="email=no" /&gt;</code></pre>
<p>允许合写：

  <code>&lt;meta name="format-detection" content="telphone=no, email=no" /&gt;</code></pre>
<p><strong>no-cache</strong>缓存控制

  <code>&lt;meta http-equiv="Cache-Control" content="no-cache"/&gt;</code></pre>
<p>没有这句话的话，WAP 浏览器将仅仅显示缓存中的文档的复本, 而不需要每次刷新都连接服务器。某些 WAP 浏览器不支持用 &lt;meta/&gt; 标签来控制缓存, 但它们确实知道 HTTP 头 "Cache-Control: no-cache" 的意思. 这种情况下, 解决方案是在服务器端的 HTTP 响应中设置 HTTP 头.

###IOS私有属性
  <strong>apple-mobile-web-app-capable</strong> 启用 webapp 模式, 会隐藏工具栏和菜单栏，和其它配合使用。

  <code>&lt;meta content="yes” name=" apple-mobile-web-app-capable" /&gt;</code></pre>
<p>meta标签是iphone设备中的safari私有meta标签，它表示：允许全屏模式浏览；

  <strong>apple-mobile-web-app-status-bar-style</strong>在webapp模式下，改变顶部状态条的颜色。

  <code>&lt;meta content="black" name=" apple-mobile-web-app-status-bar-style"   /&gt;</code></pre>
<p>meta标签也是iphone的私有标签，它指定的iphone中safari顶端的状态条的样式，default(白色，默认) | black(黑色) | black-translucent(半透明)

  <strong>apple-touch-startup-image</strong>在 webapp 下，设置启动时候的界面;

  <code>&lt;link rel="apple-touch-startup-image" href="/startup.png" /&gt;</code></pre>
<p>不支持 size 属性，可以使用 media query 来控制。iphone 和 touch 上，图片大小必须是 230*480 px,只支持竖屏;

  <strong>apple-touch-icon</strong>在webapp下，指定放置主屏幕上 icon 文件路径;

  <code>&lt;link rel="apple-touch-icon" href="touch-icon-iphone.png"&gt;
&lt;link rel="apple-touch-icon" sizes="76x76" href="touch-icon-ipad.png"&gt;
&lt;link rel="apple-touch-icon" sizes="120x120" href="touch-icon-iphone-retina.png"&gt;
&lt;link rel="apple-touch-icon" sizes="152x152" href="touch-icon-ipad-retina.png"&gt;</code></pre>
<p>默认 iphone 大小为 60px, ipad 为 76px, retina 屏乘2；
如没有一致尺寸的图标，会优先选择比推荐尺寸大，但是最接近推荐尺寸的图标。
ios7以前系统默认会对图标添加特效（圆角及高光），如果不希望系统添加特效，则可以用apple-touch-icon-precomposed.png代替apple-touch-icon.png

###其他meta
  <code>&lt;meta name="renderer" content="webkit"&gt;
&lt;!-- 避免IE使用兼容模式 --&gt;
&lt;meta http-equiv="X-UA-Compatible" content="IE=edge"&gt;
&lt;!-- 针对手持设备优化，主要是针对一些老的不识别viewport的浏览器，比如黑莓 --&gt;
&lt;meta name="HandheldFriendly" content="true"&gt;
&lt;!-- 微软的老式浏览器 --&gt;
&lt;meta name="MobileOptimized" content="320"&gt;
&lt;!-- uc强制竖屏 --&gt;
&lt;meta name="screen-orientation" content="portrait"&gt;
&lt;!-- QQ强制竖屏 --&gt;
&lt;meta name="x5-orientation" content="portrait"&gt;
&lt;!-- UC强制全屏 --&gt;
&lt;meta name="full-screen" content="yes"&gt;
&lt;!-- QQ强制全屏 --&gt;
&lt;meta name="x5-fullscreen" content="true"&gt;
&lt;!-- UC应用模式 --&gt;
&lt;meta name="browsermode" content="application"&gt;
&lt;!-- QQ应用模式 --&gt;
&lt;meta name="x5-page-mode" content="app"&gt;
&lt;!-- windows phone 点击无高光 --&gt;
&lt;meta name="msapplication-tap-highlight" content="no"&gt;</code></pre>
###窗口大小
<p>Apple为了解决移动版Safari的屏幕分辨率大小问题，专门定义了viewport虚拟窗口，它的主要作用是允许开发者创建一个虚拟的窗口，并自定义其窗口的大小缩放功能。

  如果开发者没有定义这个模拟窗口，移动版Safari的虚拟窗口默认大小980像素。现在，除了Safari的浏览器外，其他浏览器也支持viewPort虚拟窗口。但是，不同的浏览器对viewport窗口的默认大小支持都不一致。

  <strong>虚拟窗口（layout viewport）</strong>
    移动浏览器默认情况下把 viewport 设置为一个比较宽的值（防止太窄而在可视区域中显示错乱）。该默认的 viewport 称为 layout viewport。
    宽度可通过 Js 获取(基本所有设备都支持)

  <code>document.documentElement.clientWidth
document.documentElement.clientHeight</code></pre>
<p><strong>视觉窗口（visual viewport）</strong>
    浏览器可视区域大小。可理解为手机物理屏幕。
    宽度可通过 Js 获取(不支持Android2, Opera Mini, UC8)

  <code>window.innerWidth
window.innerHeight</code></pre>
<p><strong>ideal viewport</strong>
    由Peter-Paul Koch提出的一种概念，一个完美适配移动设备的 viewport。理想状态是不需要用户缩放和横向滚动条就能正常查看，显示的文字大小合适，不区分分辨率，屏幕密度等。

  <strong>meta viewport</strong>
    移动端默认使用的是 layout viewport ，而我们想要达到类似 ideal viewport 的效果的话，可以通过 meta 标签来对 viewport 进行控制。

###web开发须知
  开发触摸屏你需要触摸事件，并且hover事件失效，使用tap事件替换click事件。
关于适配不同分辨率的屏幕，通常使用两倍像素的图片。
移动端设备对于Web Storage的支持情况比较理想。

  <strong>Touch</strong>触控事件

  touchstart 手指放在一个DOM元素上不放时出发事件
touchmove手指拖拽一个DOM元素时触发事件
touchend手指从一个DOM元素中移开时触发事件
除了上述的标准触摸事件外，SenchaTouch还自定义了非常多的事件，分别如下：
touchdowm 手机触摸屏幕时触发事件
dragstart 拖拽DOM元素前触发事件
drag拖拽DOM元素时触发事件
dragend 拖拽DOM元素后触发事件
singletap 和tap事件类型
tap 手指触摸屏幕并迅速的离开屏幕
doubletap手指连续两次放在DOM元素上后触发事件
taphold触摸并保持一段时间后触发事件
tapcancle触摸中断事件
swipe滑动时触发事件
pinch 手指按捏一个DOM元素时触发事件
pinchstart 手指按捏一个DOM元素之前触发事件
pinchend手指按捏一个DOM元素之后触发事件

###屏幕大小适配Css文件
  <code>&lt;link rel='stylesheet' media='screen and(max-width:600px)' href='small.css'/&gt;</code></pre>
<p>在small.css样式文件内，需要定义media类型的样式，例如：

  <code>@media screen  and (max-width:600px){
    .demo{
            background-color:#ccc;
    }
}</code></pre>
<p>当屏幕可视区域的宽度长度600px和900px之间时，应用该样式文件。导入Css文件写法如下：

  <code>&lt;link rel='stylesheet' media='screen and(min-width:600px)and(max-width:900px)'href='small.css'&gt;&lt;/link&gt;</code></pre>
<p>small.css样式文件内对应写法如下：

  <code>@media screen and（min-width:600px）and(max-width:900px){
.demo{
    ……
    }
}</code></pre>
<p>当文件最大屏幕可是去为480像素时，应用该样式文件。导入CSS文件写法如下：

  <code>&lt;link rel='stylesheet' media='screen and(max-device-width:480px)' href='small.css'&gt;&lt;/link&gt;</code></pre>
<p>small.css样式文件内对应写法如下：

  <code>@media screen and(max-device-width:480px){
    .demo{
        background-color:#ccc;
        }
}</code></pre>
###根据方向适配文件
<pre><code>&lt;link rel='stylesheet' media ='all and(orientation:portrait)' href='portrait.css'/&gt;

&lt;link rel='stylesheet' media='all and(orientation:landscape)' href='landscape.css'/&gt;</code></pre>
###媒体查询语法
<pre><code>@media [media_query] media_type and media_feature</code></pre>
<p>使用Media Queries样式模块时都必须‘@media’方式开头。media_query表示查询关键字，在这里可以使用not关键字和only关键字。not 关键字和only关键字。not关键字表示对后面的样式表达式执行取反操作。

  <code>@media not screen  and (max-device-width:480px)</code></pre>
<p>only关键字的作用是，让不支持MediaQueries 的设备但能读取 Media  Type  类型的浏览器忽略这个样式。例如如下代码：

  <code>@media only screen and (max-device-width:480px)</code></pre>
<p>导入Media Queries 样式文件,在首页的HTML文件的head元素内新增以下Media Queries 样式文件模块；

<pre><code>&lt;link rel='stylesheet' type='text/css' media='only screen and(max-width:480px),only screen and (max-device-width)' href='/resources/style/device.css'/&gt;</code></pre>

        
           HTTP 标题信息(http-equiv) 和页面描述信息(name) 
  http-eq...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


