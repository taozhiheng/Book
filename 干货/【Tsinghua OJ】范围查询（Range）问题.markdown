
    
  
    ![picture](http://upload.jianshu.io/users/upload_avatars/76056/1f149bf6ed3e.jpg?imageMogr/thumbnail/90x90/quality/100)
    

    Maples7
  
      Forever young, forever on the road.

  
  
    ![picture](http://jianshu-prd.b0.upaiyun.com/assets/social_icons/48/home-262e288438e1edb07c4cf2e2d0804dfb.png)
  


    
      
        #【Tsinghua OJ】范围查询（Range）问题
        
          
            
              ![picture](http://upload.jianshu.io/users/upload_avatars/76056/1f149bf6ed3e.jpg?imageMogr/thumbnail/90x90/quality/100)
            
            +Maples7
        
        
    
    发表于 

    
      一只代码狗的自我修养

    2014-10-19 20:28

    

    阅读量: 46
  


        
            【问题描述】
数轴上有n个点，对于任一闭区间 [a, b]，试计算落在其内的点数。

  【输入】
第一行包括两个整数：点的总数n，查询的次数m。
第二行包含n个数，为各个点的坐标。
以下m行，各包含两个整数：查询区间的左、右边界a和b。
【输出】
对每次查询，输出落在闭区间[a, b]内点的个数。
【输入样例】
5 2
1 3 7 9 11
4 6
7 12
【输出样例】
0
3
【限制】
0 ≤ n, m ≤ 5×105
对于次查询的区间[a, b]，都有a ≤ b
各点的坐标互异
各点的坐标、查询区间的边界a、b，均为不超过10^7的非负整数
时间：2s，内存：256MB


  【solution】先不废话，先贴源代码：<code><pre>#include &lt;stdio.h&gt;
#include &lt;stdlib.h&gt; 

#define L 500005 

int a[L]; 

int compare(const void *a, const void *b)
{
    int *pa = (int*)a;
    int *pb = (int*)b;
    return (*pa) - (*pb);
} 

void swap(int &amp;a, int &amp;b)
{
    int temp;
    temp = a;
    a = b;
    b = temp;
} 

int find(int begin, int end, int ac)
{
    int mid, left = begin, right = end;
    while (left &lt;= right)
    {
        mid = left + ((right - left) &gt;&gt; 1);
        if (a[mid] &gt;= ac) right = mid - 1;
        else left = mid + 1;
    }
    return left;
} 

int main()
{
    int n, m, i;
    scanf("%d %d\n", &amp;n, &amp;m); 

    for (i = 0; i &lt; n; i++)
    {
        scanf("%d", &amp;a[i]);
    }  

    qsort(a, n, sizeof(int), compare); 

    for (i = 0; i &lt; m; i++)
    {
        int l, r, ans, lf, rt;
        scanf("%d %d", &amp;l, &amp;r); 

        //make sure l &lt;= r
        if (l &gt; r)
        {
            swap(l, r);
        } 

        rt = find(0, n - 1, r);
        lf = find(0, n - 1, l);
        ans = rt - lf;
        if (a[rt] == r) ans++;
        if (ans &lt; 0) ans = 0; 

        printf("%d\n", ans);
    }
}</pre></code>
第一感觉都是这道题以前学的时候肯定做过，很简单，看到这个数据规模基本也就确定得用二分查找了。（反正看网上想先维护好线性数组再O(1)的查找是没混过去的）

  实际上，二分查找并没有看起来那么简单，尤其是具体写起来的时候，有很多细节与临界点的处理都得根据实际情况仔细斟酌。

  结合上述源代码，有几点值得注意的地方：
1）qsort的用法，参考了：http://www.cnblogs.com/CCBB/archive/2010/01/15/1648827.html。 Tsinghua OJ 不支持 algorithm 库。
2）倒数第三行代码（<code>if (a[rt] == r) ans++;</code>）实际上就是二分查找结合具体情况对答案的调整。不妨分上界和下界等于或者不等于a数组中的值分情况讨论，即可明白这一行的涵义。这也跟二分查找几个细节的处理相统一。
3）二分查找函数中这一行：<code>mid = left + ((right - left) &gt;&gt; 1);</code>。一方面，位运算提高运算效率；另一方面，不直接用 <code>(left + right) &gt;&gt; 1</code> 防止计算过程中数字越界，进而导致数组下标越界。
4）二分查找不要用递归形式。一是提高效率；二是防止堆栈溢出。


        
           【问题描述】 数轴上有n个点，对于任一闭区间 [a, b]，试计算落在其内的点数。 
 【输...
      
    
    
      
      
      
          
             推荐拓展阅读
        
      
    
    
      
          
     喜欢

      
      
        +
                  
        +
          ![picture](http://jianshu-prd.b0.upaiyun.com/assets/weixin_share_out-092e0f24fed532b7b2c00423fdd080f8.png)
        
      
    
  


