
    
  
    ![picture](http://upload.jianshu.io/users/upload_avatars/50646/6e8c091f9376.jpg?imageMogr/thumbnail/90x90/quality/100)
    

    叶猜
  
      

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/weibo-e6860361a21f50530184f82f276acab3.png)
  


    
      
        #HTML的各个标签的默认样式
        
          
            
              ![picture](http://upload.jianshu.io/users/upload_avatars/50646/6e8c091f9376.jpg?imageMogr/thumbnail/90x90/quality/100)
            
            +叶猜
        
        
    
    发表于 

    
      CODE

    2014-08-16 16:12

    

    阅读量: 174
  


        
            在HTML中，了解各个标签的默认样式，有助于理解代码的表现，也有利于精简代码。(๑•́ ₃ •̀๑) 

  <code>head{ display: none }
body{ margin: 8px;line-height: 1.12 }      
button, textarea,input, object,select  { display:inline-block;}
ol, ul, dir,menu, dd{ margin-left: 40px }
i, cite, em,var, address{ font-style: italic }

//块级元素
html, body,  div,ol, p, ul,  h1, h2,h3,h4,h5, h6, 
address,blockquote, form,
dd,dl, dt, fieldset, frame, frameset,noframes,center, dir, hr, menu, pre   
{ display: block }

//列表元素
li{ display:list-item }
ol{list-style-type: decimal }
ol ul, ul ol,ul ul, ol ol  { margin-top: 0; margin-bottom: 0 }

//标题 
h1{ font-size:2em; margin: .67em 0 }
h2{ font-size:1.5em; margin: .75em 0 }
h3{ font-size:1.17em; margin: .83em 0 }
h4, p,blockquote, ul,fieldset, form,ol, dl, dir,menu { margin: 1.12em 0}
h5 { font-size:.83em; margin: 1.5em 0 }
h6{ font-size:.75em; margin: 1.67em 0 }
h1, h2, h3, h4,h5, h6, b,strong  { font-weight: bolder }


//伪类
br:before{ content: ”\A” }
:before, :after{ white-space: pre-line }
:link, :visited { text-decoration: underline }
:focus{ outline: thin dotted invert }


//表格
table{ display: table }
tr{ display:table-row }
thead{ display:table-header-group }
tbody{ display:table-row-group }
tfoot{ display:table-footer-group }
col{ display:table-column }
colgroup{ display:table-column-group }
td, th{ display: table-cell;}
caption{ display: table-caption }
th{font-weight: bolder; text-align: center }
caption{ text-align: center }
table{ border-spacing: 2px;}
thead, tbody,tfoot { vertical-align:middle }
td, th { vertical-align:inherit }

//其它元素
blockquote{ margin-left: 40px;margin-right: 40px }
pre, tt, code,kbd, samp  { font-family: monospace }
pre{ white-space: pre}
big{ font-size:1.17em }
small, sub, sup{ font-size: .83em }
sub{ vertical-align:sub }
sup{ vertical-align:super }
s, strike, del{ text-decoration: line-through }
hr{ border: 1px inset }
u, ins{ text-decoration:underline }
center{ text-align: center }
abbr, acronym{ font-variant: small-caps; letter-spacing:0.1em }

 BDO[DIR="ltr"]  { direction: ltr; unicode-bidi:bidi-override }
 BDO[DIR="rtl"]  { direction: rtl; unicode-bidi:bidi-override }
 /*定义BDO元素当其属性为DIR="ltr/rtl"时的默认文本读写显示顺序*/
 *[DIR="ltr"]{ direction: ltr;unicode-bidi: embed }
 *[DIR="rtl"] { direction: rtl;unicode-bidi: embed }
 /*定义任何元素当其属性为DIR="rtl/rtl"时的默认文本读写显示顺序*/
 @media print { 
       h1{page-break-before: always }
       h1, h2, h3,h4, h5, h6    { page-break-after: avoid }
       ul, ol, dl{ page-break-before: avoid }
  } /*定义标题和列表默认的打印样式*/</code></pre>
##浏览器默认样式

 1.
<p>页边距
IE默认为10px，通过body的<code>margin</code>属性设置
FF默认为8px，通过body的<code>padding</code>属性设置
要清除页边距一定要清除这两个属性值

  <code> body {
margin:0;
padding:0;
}</code></pre>

+
<p>段间距
IE默认为19px，通过p的<code>margin-top</code>属性设置
FF默认为1.12em，通过p的<code>margin-bottom</code>属性设
p默认为块状显示，要清除段间距，一般可以设置

  <code> p {
margin-top:0;
margin-bottom:0;
 }</code></pre>

+
<p>标题样式
h1~h6默认加粗显示：<code>font-weight:bold;</code>。
默认大小请参上表
还有是这样的写的

  <code> h1 {font-size:xx-large;}
 h2 {font-size:x-large;}
 h3 {font-size:large;}
 h4 {font-size:medium;}
 h5 {font-size:small;}
 h6 {font-size:x-small;}</code></pre>
<p>个大浏览器默认字体大小为16px，即等于medium，h1~h6元素默认以块状显示字体显示为粗体，
要清除标题样式，一般可以设置

  <code>hx {
font-weight:normal;
font-size:value;
 }</code></pre>

+
<p>列表样式
IE默认为40px，通过ul、ol的margin属性设置
FF默认为40px，通过ul、ol的padding属性设置
dl无缩进，但起内部的说明元素dd默认缩进40px，而名称元素dt没有缩进。
要清除列表样式，一般可以设置

  <code>ul, ol, dd {
list-style-type:none;
margin-left:0;
padding-left:0;
}</code></pre>

+
<p>元素居中
IE默认为<code>text-align:center;</code>
FF默认为<code>margin-left:auto;margin-right:auto;</code>


+
  超链接样式
a 样式默认带有下划线，显示颜色为蓝色，被访问过的超链接变紫色，要清除链接样式，一般可以设置

  <code>a {
text-decoration:none;
color:#colorname;
}</code></pre>

+
<p>鼠标样式
IE默认为<code>cursor:hand;</code>
FF默认为<code>cursor:pointer;</code>该声明在IE中也有效


+
  图片链接样式
IE默认为紫色2px的边框线
FF默认为蓝色2px的边框线
要清除图片链接样式，一般可以设置

<pre><code>img {
border:0;
}</code></pre>



        
           在HTML中，了解各个标签的默认样式，有助于理解代码的表现，也有利于精简代码。(๑•́ ₃ ...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


