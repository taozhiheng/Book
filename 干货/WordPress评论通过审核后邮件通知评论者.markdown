
    
  
    ![picture](http://upload.jianshu.io/users/upload_avatars/948/79fc8a5ce413.png?imageMogr/thumbnail/90x90/quality/100)
    

    常阳时光
  
      常阳时光，cyhour.com

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/home-262e288438e1edb07c4cf2e2d0804dfb.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/douban-093d391615fe9b2bcb5a9aea3752e615.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/weibo-e6860361a21f50530184f82f276acab3.png)![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/google_plus-6297e2bef72e74a2b554863b0969da47.png)
  
    
      ![picture](http://upload.jianshu.io/users/qrcodes/948/1834071887.jpg?imageMogr/thumbnail/318x318/quality/100)
    


    
      
        #WordPress评论通过审核后邮件通知评论者
        
          
            
              ![picture](http://upload.jianshu.io/users/upload_avatars/948/79fc8a5ce413.png?imageMogr/thumbnail/90x90/quality/100)
            
            +常阳时光
        
        
    
    发表于 

    
      日记本

    2014-10-30 11:48

    

    阅读量: 8
  


        
            评论通过审核后，给评论者发送一封通知邮件。

  在当前主题的functions.php中添加以下PHP代码：
>
  //评论通过后邮件通知  

  add_action('comment_unapproved_to_approved', 'cy_comment_approved');  

  function cy_comment_approved($comment) {  

    if(is_email($comment-&gt;comment_author_email)) {  

      $wp_email = 'no-reply@' . preg_replace('#^www.#', '', strtolower($_SERVER['SERVER_NAME'])); // e-mail 發出點, no-reply 可改為可用的 e-mail.  

      $subject = '您在 [' . get_option("blogname") . '] 的评论已通过审核';  

      $message = '  

          &lt;table style="width: 99.8%;height:99.8% "&gt;  

              &lt;tbody&gt;  

                  &lt;tr&gt;  

                      &lt;td style="background:#FFF"&gt;  

                          &lt;div style="background-

  color:white;border-top:3px solid #000;box-shadow:0 2px 2px #000;line-

  padding:0 15px 14px;width:600px;margin:20px auto;color:#000;font-

  family:Century Gothic,Trebuchet MS,Hiragino Sans GB,微软雅

  黑,Microsoft Yahei,Tahoma,Helvetica,Arial,SimSun,sans-serif;font-size:14px;"&gt;  

                              &lt;h2 style="border-bottom:1px solid #888;font-size:16px;font-weight:blod;padding:5px 0 20px 10px;"&gt;  

                                  您在 『&lt;a style="text-decoration:none;color: #12ADDB;" href="' . get_option('home') . '"&gt;' . get_option('blogname') . ' &lt;/a&gt;』博客上的评论已通过审核啦！  

                              &lt;/h2&gt;  

                              &lt;div style="padding:0 12px 0 12px;margin-top:18px"&gt;  

                                  &lt;p&gt;  

                                      &lt;b&gt;' . trim($comment-&gt;comment_author) . ' &lt;/b&gt;君，您曾在文章『' . get_the_title($comment-&gt;comment_post_ID) . '』上发表评论:  

                                  &lt;/p&gt;  

                                  &lt;p style="background-color: #ddd;border: 1px solid #888;padding: 10px 10px;margin:10px 0"&gt;  

                                      ' . nl2br($comment-&gt;comment_content) . '  

                                  &lt;/p&gt;  

                                  &lt;p&gt;  

                                      已通过管理员审核并显示。  

                                  &lt;/p&gt;  

                                  &lt;p style="padding: 10px 10px;margin:20px 0"&gt;  

                                      您可以：&lt;a style="text-decoration:none; color:#12addb" href="' . htmlspecialchars(get_comment_link($comment-&gt;comment_ID)) . '"&gt;前往查看您的完整评论內容&lt;/a&gt;！  

                                      欢迎再次光临&lt;a style="text-decoration:none; color:#12addb" href="' . get_option('home') . '"&gt;' . get_option('blogname') . '&lt;/a&gt;！  

                                  &lt;/p&gt;  

                              &lt;/div&gt;  

                          &lt;/div&gt;  

                      &lt;/td&gt;  

                  &lt;/tr&gt;  

              &lt;/tbody&gt;  

          &lt;/table&gt;';  

          // 这行代码会转换评论中使用的表情  

      $message = convert_smilies($message);   

      $from = "From: "" . get_option('blogname') . "" &lt;$wp_email&gt;";  

      $headers = "$fromnContent-Type: text/html; charset=" . get_option('blog_charset') . "n";  

      wp_mail( $comment-&gt;comment_author_email, $subject, $message, $headers );       

    }  

  }  

  最后用UTF-8、无BOM编码另存，替换原来的functions.php就可以了。
  效果图：

![picture](http://upload-images.jianshu.io/upload_images/948-f73e24d9a823ac54.jpg?imageView2/2/w/1240/q/100)
常阳时光评论通知效果图
  常阳时光  ——修改自 露兜  


        
           评论通过审核后，给评论者发送一封通知邮件。   在当前主题的functions.php中添加...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


