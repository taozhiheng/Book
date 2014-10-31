
    
  
    ![picture](http://upload.jianshu.io/users/upload_avatars/48184/db8832d65219.jpg?imageMogr/thumbnail/90x90/quality/100)
    

    超低空
  
      低空飞行，仰望蓝天
Blog：http://blog.csdn.net/mc_hust
Github：http://github.com/hust-mc

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/home-262e288438e1edb07c4cf2e2d0804dfb.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/google_plus-6297e2bef72e74a2b554863b0969da47.png)
  
    
      ![picture](http://upload.jianshu.io/users/qrcodes/48184/weixin4.jpg?imageMogr/thumbnail/318x318/quality/100)
    


    
      
        #java正则表达式笔记
        
          
            
              ![picture](http://upload.jianshu.io/users/upload_avatars/48184/db8832d65219.jpg?imageMogr/thumbnail/90x90/quality/100)
            
            +超低空
        
        
    
    发表于 

    
      WIKI

    2014-10-20 21:57

    

    阅读量: 5099
  


        
            正则表达式是一种强大的字符串处理工具,平时经常会用到。这里完整的做一次总结，以便查阅记忆。

<h5>1. 字符串中的正则表达式</h5>
  使用正则表达式可以对字符串进行查找、提取、分割、替换等操作。String类当中提供了如下几个特殊方法：


+boolean   matches(String  regex)：判断该字符串是否匹配指定的正则表达式。
+String   replaceAll(String  regex, String  replacement)：将该字符串中所有匹配regex的子串替换成replacement。
+String[]  split(String  regex)：以regex作为分隔符，把该字符串分割成多个子串。

  <strong>以上这些特殊的方法都依赖于Java提供的正则表达式。</strong>

<h5>2. 创建正则表达式</h5>

 1.x:      字符x(x可代表任何合法的字符)；
+\0mnn:      八进制数Omnn所表示的字符；
+\xhh:       十六进制0xhh所表示的字符；
+\uhhhh:          十六进制0xhhhh所表示的UNICODE字符；
+\t          :制表符('\u0009')；
+\n:     新行(换行)符('\u000A')；
+\r:       回车符('\u000D')；
+\f:        换页符('\u000C')；
+\a:        报警(bell)符('\u0007')；
+\e:     Escape符('\u001B')；
+\cx:       x对应的控制符。例如,\cM匹配Ctrl-M。x值必须为A~Z或a~z之一；

<h5>3. 正则表达式中的特殊字符</h5>

 1.$:  匹配一行的结尾。要匹配$字符本身，请使用\$；
+^: 匹配一行的开头。要匹配^字符本身，请使用\^；
+(): 标记子表达式的开始和结束位置。要匹配这些字符，请使用\(和\)；
+[]: 用于确定中括号表达式的开始和结束位置。要匹配这些字符，请使用\[和\]；
+{}: 用于标记前面子表达式的出现的频度。要匹配这些字符，请使用\{和\}；
+*: 指定前面子表达式可以出现零次或多次。要匹配*字符本身，请使用\*；
++: 指定前面子表达式可以出现一次或多次。要匹配+字符本身，请使用\+；
+?: 指定前面子表达式可以出现零次或一次。要匹配?字符本身，请使用\?；
+.: 匹配除换行符\n之外的任何单位字符。要匹配，字符本身，请使用\.；
+\:用于转义下一个字符，或指定八进制、十六进制符。要匹配\字符，请使用\\；
+|:指定两项之间任选一项。要匹配|字符本身，请使用\|；

<h5>4. 预定义字符</h5>

 1..: 可以匹配任何字符；
+\d:匹配0~9的所有数字；
+\D:匹配非数字；
+\s:匹配所有的空白字符,包括空格、制表符、回车符、换页符、换行符等；
+\S:匹配所有的非空白字符；
+\w:匹配所有的单词字符，包括0~9所有的数字、26个英文字母和下划线(_)；
+\W:匹配所有的非单词字符；

####5. 边界匹配符

 1.^: 行的开头
+$: 行的结尾
+\b: 单词的边界
+\B: 非单词的边界
+\A: 输入的开头
+\G: 前一个匹配的结尾
+\Z: 输入的结尾，仅用于最后的结束符
8.\z: 输入的结尾

<h5>6. 表示匹配次数的符号</h5>
  下图显示了表示匹配次数的符号，这些符号用来确定紧靠该符号左边的符号出现的次数：

![picture](http://upload-images.jianshu.io/upload_images/48184-c06dff036083d9ac.jpg?imageView2/2/w/1240/q/100)
匹配次数


 1.
  假设我们要在文本文件中搜索美国的社会安全号码。这个号码的格式是999-99-9999。用来匹配它的正则表达式如图一所示。在正则表达式中，连字符（“-”）有着特殊的意义，它表示一个范围，比如从0到9。因此，匹配社会安全号码中的连字符号时，它的前面要加上一个转义字符“\”。


![picture](http://upload-images.jianshu.io/upload_images/48184-4221f6acfe0b7536.gif?imageView2/2/w/1240/q/100)
连字符的转义方式


+
  假设进行搜索的时候，你希望连字符号可以出现，也可以不出现——即，999-99-9999和999999999都属于正确的格式。这时，你可以在连字符号后面加上“？”数量限定符号，如图所示：


![picture](http://upload-images.jianshu.io/upload_images/48184-a6505c059f595b23.gif?imageView2/2/w/1240/q/100)
"?"表示可选


+
  下面我们再来看另外一个例子。美国汽车牌照的一种格式是四个数字加上二个字母。它的正则表达式前面是数字部分“[0-9]{4}”，再加上字母部分“[A-Z]{2}”。下图显示了完整的正则表达式。


![picture](http://upload-images.jianshu.io/upload_images/48184-9b769909898191b3.gif?imageView2/2/w/1240/q/100)
匹配字数



<h6>补充：正则表达式支持的数量标识符的贪婪、勉强、占有模式</h6>

+
  贪婪模式(Greedy):数量表示符默认采用贪婪模式，除非另有表示。贪婪模式的表达式会一直匹配下去，直到无法匹配为止。如果你发现表达式匹配的结果与预期的不符，很有可能是因为——你以为表达式只会匹配前面几个字符，而实际上它是贪婪模式，所以会一直匹配下去。


+
  勉强模式(Reluctant):用问号后缀(?)表示,它只会匹配最少的字符。也称为最小匹配模式。


+
  占有模式(Possessive):用加号后缀(+)表示，目前只有Java支持占有模式。 




        
           正则表达式是一种强大的字符串处理工具,平时经常会用到。这里完整的做一次总结，以便查阅记忆。 ...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


