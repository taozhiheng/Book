
    
  
    ![picture](http://upload.jianshu.io/users/upload_avatars/76056/1f149bf6ed3e.jpg?imageMogr/thumbnail/90x90/quality/100)
    

    Maples7
  
      Forever young, forever on the road.

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/home-262e288438e1edb07c4cf2e2d0804dfb.png)
  


    
      
        #【Tsinghua OJ】多米诺骨牌(domino)问题
        
          
            
              ![picture](http://upload.jianshu.io/users/upload_avatars/76056/1f149bf6ed3e.jpg?imageMogr/thumbnail/90x90/quality/100)
            
            +Maples7
        
        
    
    发表于 

    
      一只代码狗的自我修养

    2014-10-18 15:07

    

    阅读量: 105
  


        
            （domino.c/cpp）
【问题描述】
　　小牛牛对多米诺骨牌有很大兴趣，然而她的骨牌比较特别，只有黑色和白色的两种。她觉得如果存在连续三个骨牌是同一种颜色，那么这个骨牌排列便是不美观的。现在她有n个骨牌要来排列，她想知道不美观的排列的个数。由于数字较大，数学不好的她不会统计，所以请你来帮忙。希望你帮她求出不美观的排列的个数。

  【输入数据】
　　只有一个正整数，即要排列的骨牌个数。
【输出数据】
　　一个数，即不美观的排列个数。
【样例输入】
 4
【样例输出】
 6
【样例解释】
　　有四种不美观的排列。
　　黑黑黑黑，白白白白，黑黑黑白，白白白黑，黑白白白，白黑黑黑
【数据范围】
　　20%的数据，n&lt;=60；
　　50%的数据，n&lt;=6000；
　　100%的数据，n&lt;=10000。

  　　时间限制： 1 sec
　　空间限制： 256 MB
【提示】
　　动态规划、高精度加法。

  —————————————————————————————————

  【solution】
虽然只是Tutorial里面的题，虽然听说现在是小学僧的练习题（T_T），不过还真是想了辣么一会儿。算算真是已经有4年多没碰过这些东西了，为了完成这门课作业也真是找回了当初的感觉，真是怀念这种一道一道题“过关斩将”的感觉，已经很久不曾有这种感觉了。

  回到正题。这道题初看很容易去正向考虑如何统计“不美观”的排列个数，甚至会误入使用组合数学的错误算法。根据提示，往动态规划方面想，会发现，实际上，这道题需要反向来思考，即考虑“美观”的排列个数。那么，题目转化为求解连续颜色不超过3（不包括3）的排列个数 a，然后再用所有的排列个数（2^n）减去 a 即得问题解。再细想，这不跟动态规划的经典问题——上楼梯问题 很像吗？

  于是，问题得解：
对于每一个色块（连续的 1 个或者 2 个相同颜色的白色或者黑色色块），就相当于上楼梯问题中的上升一阶或者两阶，所以这里其实我们完全可以忽略到颜色这个因素（最后再把得到的上阶梯的总数乘以2，因为把所有的色块全部反转一次颜色都可以得到原来那种的状态的 twin solution，而上楼梯问题并未考虑颜色问题，只是简单的划分为一次动作，这个问题正是因为颜色来划分的），而是把一个色块等同为上楼梯问题中的一次动作。
状态方程为：f[n] = f[n-1] + f[n-2]。初始条件 f[1] = 1; f[2] = 2。
也就是不严格对应项数的著名的斐波拉契数列。
最后的结果为 2^n - 2*f[n]。

  由于问题数据规模较大，最后还要用高精度加法来实现。

  【source code】<code><pre>#include &lt;stdio.h&gt; 

#define L 6001
#define wei 208 

void echo(int ans)        //make sure printing a 4-wei number
{
    if (ans &gt; 999)
    {
        printf("%d", ans);
    }
    else if (ans &gt; 99)
    {
        printf("0%d", ans);
    }
    else if (ans &gt; 9)
    {
        printf("00%d", ans);
    }
    else
    {
        printf("000%d", ans);
    }
} 

int main(void)
{
    int n, i, j, temp, pro = 0, cn = 0, an[L] = { 0 }, a[L][wei] = { 0 }, c[wei] = { 0 }, ans[wei] = { 0 };
    bool zero = false; 

    scanf("%d\n", &amp;n);  

    //bases for a, c and an, cn
    a[3][0] = 3; a[2][0] = 2; c[0] = 1;  

    //a[n] = a[n-1] + a[n-2]
    for (i = 4; i &lt;= n; i++)
    {
        //Gao Jin Du Jia Fa
        pro = 0;
        for (j = 0; j &lt;= an[i - 1]; j++)
        {
            temp = a[i - 1][j] + a[i - 2][j] + pro;
            a[i][j] = temp % 10000;
            pro = temp  / 10000;
        }
        if (pro &gt; 0)
        {
            a[i][j] = pro;
            an[i] = j;
        }
        else an[i] = an[i - 1];
    } 

    // 2^n
    for (i = 0; i &lt; n; i++)
    {
        //Gao Jin Du Jia Fa
        pro = 0;
        for (j = 0; j &lt;= cn; j++)
        {
            temp = c[j] * 2 + pro;
            c[j] = temp % 10000;
            pro = temp / 10000;
        }
        if (pro &gt; 0)
        {
            c[j] = pro;
            cn++;
        }
    } 

    //ans = 2^n - a[n] *2, Gao Jin Du Jia Fa
    pro = 0;
    for (j = 0; j &lt;= an[n]; j++)
    {
        temp = a[n][j] * 2 + pro;
        a[n][j] = temp % 10000;
        pro = temp / 10000;
    }
    if (pro &gt; 0)
    {
        an[n]++;
        a[n][j] = pro;
     } 

    pro = 0;
    for (j = 0; j &lt;= cn; j++)
    {
        temp = c[j] - a[n][j] + pro;
        if (temp &lt; 0)
        {
            ans[j] = temp + 10000;
            pro = -1;
        }
        else
        {
            ans[j] = temp;
            pro = 0;
        }
    } 

    //print the answer, ignoring the zeros in the front
    for (i = cn; i &gt;= 0; i--)
    {
        if (!zero)
        {
            if (ans[i] != 0)
            {
                printf("%d", ans[i]);
                zero = true;
            }
        }
        else echo(ans[i]);
    }
    printf("\n"); 

    return 0;
}</pre></code>
【代码改进空间】
1、将高精度算法函数化;
2、仍然只能通过 Tsinghua Online Judge 40%的数据，其他数据都是Runtime error (exitcode: 11)，暂无果。

  【优化后AC的代码】
感谢@Plan能抽出时间来AC这道题，同时找到了字符串的高精度加法解决办法，过了100%的数据。以下是参考了她的代码后自己重新几乎是照着写的代码（求2^n的函数从递归形式改成了循环版）：<code><pre>#include &lt;stdio.h&gt;
#include &lt;string.h&gt;
#include &lt;stdlib.h&gt;   

char *add(char a[], char b[])
{
    int len, i, j, k, up, x, y, z;
    char *c, *back; 

    len = (strlen(a) &gt; strlen(b)) ? strlen(a) + 2 : strlen(b) + 2;
    c = (char *)malloc(len*sizeof(char));
    back = (char *)malloc(len*sizeof(char)); 

    i = strlen(a) - 1;
    j = strlen(b) - 1;
    k = 0; up = 0; 

    while (i &gt;= 0 || j &gt;= 0)
    {
        if (i&lt;0) x = '0'; else x = a[i];
        if (j&lt;0) y = '0'; else y = b[j];
        z = x - '0' + y - '0';
        if (up == 1) z += 1;
        if (z&gt;9)
        {
            up = 1; z %= 10;
        }
        else up = 0;
        c[k++] = z + '0';
        i--; j--;
    }
    if (up) c[k++] = '1';
    c[k] = '\0'; 

    //reverse
    i = 0;
    for (k -= 1; k &gt;= 0; k--) back[i++] = c[k];
    back[i] = '\0'; 

    return back;
} 

char *sub(char a[], char b[])
{
    int len, i, j, k, down, x, y, z;
    char *c, *back; 

    len = strlen(a);
    c = (char *)malloc(len*sizeof(char));
    back = (char *)malloc(len*sizeof(char)); 

    i = strlen(a) - 1;
    j = strlen(b) - 1;
    k = 0; down = 0; 

    while (i &gt;= 0 || j &gt;= 0)
    {
        if (i&lt;0) x = '0'; else x = a[i];
        if (j&lt;0) y = '0'; else y = b[j];
        z = x - '0' - (y - '0') - down;
        if ( z &lt; 0 )
        {
            down = 1;
            z = z + 10;
        }
        else down = 0;
        c[k++] = z + '0'; 
        i--; j--;
    }
    while (c[--k] == '0') ; 
    //reverse
    i = 0;
    for (k; k &gt;= 0; k--)
    {
        back[i++] = c[k];
    } 

    return back;
} 

char *power(int n)
{
    int i;
    char *temp="2";

    for (i = 2; i &lt;= n; i++)
    {
        temp = add(temp, temp);
    } 

    return temp;
} 

char *fib(int n)
{
    char *p = "1", *q = "1";
    char *s = "1";
    int i; 

    for (i = 0; i &lt; n - 1; i++)
    {
        s = add(p, q);
        p = q;
        q = s;
    } 

    return s;
} 

int main()
{
    int n;
    char *mi, *f; 

    scanf("%d\n", &amp;n); 

    mi = power(n);
    f = fib(n);
    f = add(f, f); 

    printf("%s\n", sub(mi, f)); 

    return 0;
}</pre></code>
【参考资料】
1：http ://www.cnblogs.com/kuangbin/archive/2011/07/22/2113836.html    高精度加法的C++实现；
2：http://blog.sina.com.cn/s/blog_993d2542010143qw.html Fibonacci数列的第N项 log(N)算法（未用到）。

  有几点：
1）由于数据规模，四位进一次位的int版高精也无法AC掉所有数据，只能用string来解决了。
2）要注意高精度运算string的顺序是不是跟数字顺序一致，所以代码中有reverse操作。


        
           （domino.c/cpp） 【问题描述】 　　小牛牛对多米诺骨牌有很大兴趣，然而她的骨牌比...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


