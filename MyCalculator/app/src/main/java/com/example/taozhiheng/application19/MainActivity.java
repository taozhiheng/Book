package com.example.taozhiheng.application19;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import java.math.BigDecimal;
import java.util.Stack;
import java.util.zip.Inflater;

public class MainActivity extends Activity implements View.OnClickListener{

    private boolean flag=true;  //是否可以用作表达式
    private StringBuffer show=new StringBuffer("0");  //记录显示框内的字符
    private TextView display;    //显示框
    //第一行按钮
    private Button btn_more;     //更多功能键
    private Button btn_clear;    //清空
    private Button btn_delete;   //删除
    //第二行按钮
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_div;      //除号
    //第三行按钮
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_mul;     //乘号
    //第四行按钮
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_red;     //减号
    //第五行按钮
    private Button btn_dot;     //小数点
    private Button btn_0;
    private Button btn_equ;     //等号
    private Button btn_add;     //加号

    private Button btn_sin;     //正弦
    private Button btn_cos;     //余弦
    private Button btn_tan;     //正切

    private Button btn_ln;      //对数
    private Button btn_E;       //10的次方
    private Button btn_j;       //阶乘

    private Button btn_pi;      //圆周率
    private Button btn_exp;     //e的次方
    private Button btn_m;       //乘幂

    private Button btn_left;    //左括号
    private Button btn_right;   //右括号
    private Button btn_sqr;     //根号

    private Button btn_MC;      //清空M
    private Button btn_MA;      //M累加
    private Button btn_M;       //M
    private Button btn_ANS;     //上次结果

    private TableLayout firstView;  //第一个按键视图
    private TableLayout secondView; //第二个按键视图

    private SharedPreferences pref; //记录键值对
    private double ANS=0;           //记录计算结果
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref=getSharedPreferences("pref",MODE_PRIVATE);
        firstView=(TableLayout)findViewById(R.id.board1);
        secondView=(TableLayout)findViewById(R.id.board2);
        display=(TextView)findViewById(R.id.display);
        //为所有按钮绑定监视器
        btn_more=(Button)findViewById(R.id.btn_more);
        btn_more.setOnClickListener(this);
        btn_clear=(Button)findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
        btn_delete=(Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);
        btn_7=(Button)findViewById(R.id.btn_7);
        btn_7.setOnClickListener(this);
        btn_8=(Button)findViewById(R.id.btn_8);
        btn_8.setOnClickListener(this);
        btn_9=(Button)findViewById(R.id.btn_9);
        btn_9.setOnClickListener(this);
        btn_div=(Button)findViewById(R.id.btn_div);
        btn_div.setOnClickListener(this);
        btn_4=(Button)findViewById(R.id.btn_4);
        btn_4.setOnClickListener(this);
        btn_5=(Button)findViewById(R.id.btn_5);
        btn_5.setOnClickListener(this);
        btn_6=(Button)findViewById(R.id.btn_6);
        btn_6.setOnClickListener(this);
        btn_mul=(Button)findViewById(R.id.btn_mul);
        btn_mul.setOnClickListener(this);
        btn_1=(Button)findViewById(R.id.btn_1);
        btn_1.setOnClickListener(this);
        btn_2=(Button)findViewById(R.id.btn_2);
        btn_2.setOnClickListener(this);
        btn_3=(Button)findViewById(R.id.btn_3);
        btn_3.setOnClickListener(this);
        btn_red=(Button)findViewById(R.id.btn_red);
        btn_red.setOnClickListener(this);
        btn_dot=(Button)findViewById(R.id.btn_dot);
        btn_dot.setOnClickListener(this);
        btn_0=(Button)findViewById(R.id.btn_0);
        btn_0.setOnClickListener(this);
        btn_equ=(Button)findViewById(R.id.btn_equ);
        btn_equ.setOnClickListener(this);
        btn_add=(Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);

        btn_sin=(Button)findViewById(R.id.btn_sin);
        btn_sin.setOnClickListener(this);
        btn_cos=(Button)findViewById(R.id.btn_cos);
        btn_cos.setOnClickListener(this);
        btn_tan=(Button)findViewById(R.id.btn_tan);
        btn_tan.setOnClickListener(this);
        btn_ln=(Button)findViewById(R.id.btn_ln);
        btn_ln.setOnClickListener(this);
        btn_exp=(Button)findViewById(R.id.btn_exp);
        btn_exp.setOnClickListener(this);
        btn_j=(Button)findViewById(R.id.btn_j);
        btn_j.setOnClickListener(this);
        btn_pi=(Button)findViewById(R.id.btn_pi);
        btn_pi.setOnClickListener(this);
        btn_E=(Button)findViewById(R.id.btn_E);
        btn_E.setOnClickListener(this);
        btn_m=(Button)findViewById(R.id.btn_m);
        btn_m.setOnClickListener(this);
        btn_left=(Button)findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        btn_right=(Button)findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        btn_sqr=(Button)findViewById(R.id.btn_sqr);
        btn_sqr.setOnClickListener(this);
        btn_MC=(Button)findViewById(R.id.btn_MC);
        btn_MC.setOnClickListener(this);
        btn_MA=(Button)findViewById(R.id.btn_MA);
        btn_MA.setOnClickListener(this);
        btn_M=(Button)findViewById(R.id.btn_M);
        btn_M.setOnClickListener(this);
        btn_ANS=(Button)findViewById(R.id.btn_ANS);
        btn_ANS.setOnClickListener(this);
        //显示框初始化
        display.setText("0");
    }

    @Override
    public void onClick(View v)
    {
        Button btn=(Button)v;
        switch(v.getId())
        {
            //0~9键
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                appendNumber(btn.getText().toString());
                break;
            //+ - * /键
            case R.id.btn_add:
            case R.id.btn_red:
            case R.id.btn_mul:
            case R.id.btn_div:
            //科学计数法
            case R.id.btn_E:
            //乘幂
            case R.id.btn_m:
                appendChar(btn.getText().toString());
                break;
            //切换按钮视图
            case R.id.btn_more:
                changeView();
                break;
            //清空键
            case R.id.btn_clear:
                show.setLength(0);
                show.append("0");
                break;
            //退格键
            case R.id.btn_delete:
                //不是计算结果才能删除
                if(flag)
                {
                    if(show.length()>1)
                    {
                        //如果是sin,cos,tan,,exp,ln,ANS删除多个符号
                        if((show.charAt(show.length()-1)=='p'||show.charAt(show.length()-1)=='S'||show.charAt(show.length()-1)=='s'||show.charAt(show.length()-1)=='n')
                                &&show.charAt(show.length()-2)!='l')
                        {
                            show.delete(show.length()-3,show.length());
                            if(show.length()==0)
                                show.append("0");
                        }
                        else if(show.charAt(show.length()-2)=='l')
                        {
                            show.delete(show.length()-2,show.length());
                            if(show.length()==0)
                                show.append("0");
                        }
                        //一般删除最后一个
                        else
                        {
                            show.deleteCharAt(show.length()-1);
                        }
                    }
                    //只有一个字符，置为0
                    else
                    {
                        show.replace(0,1,"0");
                    }
                }
                break;
           //小数点键
            case R.id.btn_dot:
                //如果是计算结果，先置为0
                if(!flag)
                {
                    show.setLength(0);
                    show.append("0");
                }
                //如果前一个是+-*/(^E ,多加一个0
                if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'
                        ||show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/'
                        ||show.charAt(show.length()-1)=='('||show.charAt(show.length()-1)=='^'
                        ||show.charAt(show.length()-1)=='E')
                {
                    show.append("0.");
                }
                //一般情况，检测之前数字串中没有小数点才能添加
                else
                {
                    int i=show.length()-1;
                    while(show.charAt(i)>='0'&&show.charAt(i)<='9'&&i>0)
                    {
                        i--;
                    }
                    if(show.charAt(i)!='.')
                    {
                        show.append('.');
                    }
                }
                break;
            //等号键
            case R.id.btn_equ:
                try
                {
                    show.replace(0,show.length(),cal(show.toString()));
                }catch(Exception e)
                {
                    show.setLength(0);
                    show.append("出错");
                }
                flag=false;
                break;
            //部分函数键sin cos tan ln log
            case R.id.btn_sin:
            case R.id.btn_cos:
            case R.id.btn_tan:
            case R.id.btn_ln:
            case R.id.btn_exp:
            case R.id.btn_sqr:
                //如果是结果，先置为0
                if(!flag)
                {
                    show.setLength(0);
                    show.append("0");
                }
                //如果表达式只有0,替换掉0
                if(show.toString().equals("0"))
                {
                    show.replace(0,1,btn.getText().toString()+"(");
                }
                //前一个是＋－×／（，才能添加
                else if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'||
                        show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/'||
                        show.charAt(show.length()-1)=='(')
                {
                    show.append(btn.getText().toString()+"(");
                }
                break;
            //阶乘键
            case R.id.btn_j:
                //前面不是小数时添加
                int index=show.length()-1;
                while(show.charAt(index)>='0'&&show.charAt(index)<='9'&&index>0)
                {
                    index--;
                }
                if(index==0&&show.charAt(index)>='0'&&show.charAt(index)<='9'||(show.length()>1&&show.charAt(index)!='.'&&index<show.length()-1))
                {
                    show.append('!');
                }
                break;
            //圆周率
            case R.id.btn_pi:
                //如果是结果，先置为0
                if(!flag)
                {
                    show.setLength(0);
                    show.append("0");
                }
                //如果只有0,替换掉0
                if(show.toString().equals("0"))
                {
                    show.replace(0,1,btn.getText().toString());
                }
                //前一个不是数字，小数点，右括号才能添加
                else if(!(show.charAt(show.length()-1)==')'||show.charAt(show.length()-1)=='.'
                        ||(show.charAt(show.length()-1)>='0'&&show.charAt(show.length()-1)<='9')))
                {
                    show.append(btn.getText().toString());
                }
                break;
            //左括号
            case R.id.btn_left:
                //如果是结果，先置为0
                if(!flag)
                {
                    show.setLength(0);
                    show.append("0");
                }
                //前面是＋ － × ／ （ ^ E s n p ，才能添加
                if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'
                        ||show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/'
                        ||show.charAt(show.length()-1)=='('||show.charAt(show.length()-1)=='^'
                        ||show.charAt(show.length()-1)=='E'||show.charAt(show.length()-1)=='s'
                        ||show.charAt(show.length()-1)=='n'||show.charAt(show.length()-1)=='p')
                {
                    show.append("(");
                }
                if(show.toString().equals("0"))
                {
                    show.replace(0,1,"(");
                }
                break;
            //右括号
            case R.id.btn_right:
                //表达式中左括号多余有括号才能添加
                int left=0;
                int right=0;
                int i=0;
                while(i<show.length())
                {
                    if(show.charAt(i)=='(')
                        left++;
                    if(show.charAt(i)==')')
                        right++;
                    i++;
                }
                if(left>right)
                    show.append(")");
                break;
            //清空M
            case R.id.btn_MC:
                //将0写入M
                SharedPreferences.Editor editor=pref.edit();
                editor.putString("M","0");
                editor.commit();
                break;
            //M累加
            case R.id.btn_MA:
                //让等号键模拟按下一次，再将原值与计算结果相加重新写给M
                btn_equ.performClick();
                SharedPreferences.Editor editor2=pref.edit();
                editor2.putString("M",String.valueOf(Double.parseDouble(pref.getString("M","0"))+ANS));
                editor2.commit();
                break;
            //M
            case R.id.btn_M:
                appendNumber("M");
                break;
            //上次结果
            case R.id.btn_ANS:
                appendNumber("ANS");
                break;
        }
        //如果按下的不是等号，退格，视图切换，小数点　，　认为在输入表达式
        if(v.getId()!=R.id.btn_equ&&v.getId()!=R.id.btn_delete&&v.getId()!=R.id.btn_more&&v.getId()!=R.id.btn_dot)
            flag=true;
        //改变显示框内容
        display.setText(show);
    }
    //０～９键处理
    private void appendNumber(String str)
    {
        //若为结果，先重置为初始状态
       if(!flag)
       {
           show.setLength(0);
           show.append("0");
       }
        //若为初始状态，替换掉０
       if(show.toString().equals("0"))
       {
           show.replace(0,1,str);
       }
       //若前一个为０，再前一个为+ - * /,替换掉０
       else if(show.length()>1&&show.charAt(show.length()-1)=='0'&&(show.charAt(show.length()-2)=='+'||
          show.charAt(show.length()-2)=='-'||show.charAt(show.length()-2)=='*'||show.charAt(show.length()-2)=='/'))
       {
           show.deleteCharAt(show.length()-1);
           show.append(str);
       }
       else
       {
        show.append(str);
       }
    }
    //+ - * /处理
    private void appendChar(String str)
    {
        //若为结果，用ANS替换掉上一次结果
        if(!flag)
        {
            show.replace(0,show.length(),"ANS");
        }
        //若前一个符号为+ - * /,替换掉前一个符号
        if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'
                ||show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/')
        {
            show.deleteCharAt(show.length()-1);
            show.append(str);
        }
        //若前一个为. ,在最后追加一个０
        else if(show.charAt(show.length()-1)=='.')
        {
            show.append("0"+str);
        }
        else
        {
            show.append(str);
        }
    }
    //改变按钮显示
    private void changeView()
    {
        //切换显示键盘
        if(firstView.getVisibility()==View.VISIBLE)
        {
            firstView.setVisibility(View.GONE);
            secondView.setVisibility(View.VISIBLE);
        }
        else
        {
            firstView.setVisibility(View.VISIBLE);
            secondView.setVisibility(View.GONE);
        }
    }
    //将StringBuffer转化为double
    double StrToDouble(StringBuffer str)
    {
        long first=0;    //整数部分
        long second=0;   //小数部分
        int index=0;
        char ch;
        while(index<str.length()&&(ch=str.charAt(index))!='.')
        {
            first=first*10+ch-'0';
            index++;
        }
        //如果只有整数部分，返回
        if(index==str.length())
            return (double)first;
        int len=str.length()-1-index;
        index++;
        while(index<str.length())
        {
            second=second*10+str.charAt(index)-'0';
            index++;
        }
        return (double)first+second*Math.pow(0.1,len);
    }
    //逆波兰解析计算
    private String cal(String str)
    {
        //处理M,ANS
        str=str.replace("M",pref.getString("M","0"));
        str=str.replace("ANS",String.valueOf(ANS));
        //处理正负号
        if(str.charAt(0)=='+'||str.charAt(0)=='-')
            str='0'+str;
        str=str.replace("(-","(0-");
        str=str.replace("(+","(0+");
        //处理π
        str=str.replace("π",String.valueOf(Math.PI));
        //操作数栈
        Stack<Character> number=new Stack<Character>();
        //符号栈
        Stack<Character> symbol=new Stack<Character>();
        //将表达式转为数组，以逐一遍历
        char[] input=str.toCharArray();
        int i=0;
        while(i<input.length)
        {
            switch(input[i])
            {
                case '0':case'1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':case '.':
                    number.push(input[i]);
                    break;
                case '+':case '-':
                  　//symbol出栈
                    while(!symbol.isEmpty()&&symbol.peek()!='(')
                    {
                        number.push(' ');
                        number.push(symbol.pop());
                    }
                    number.push(' ');
                    symbol.push(input[i]);
                    break;
                case '*':case '/':
                    if(!symbol.isEmpty()&&symbol.peek()!='+'&&symbol.peek()!='-')
                    {
                        while(!symbol.isEmpty()&&symbol.peek()!='('&&symbol.peek()!='+'&&symbol.peek()!='-')
                        {
                            number.push(' ');
                            number.push(symbol.pop());
                        }
                    }
                    number.push(' ');
                    symbol.push(input[i]);
                    break;
                case 's':case 'c':case 't':case 'e':
                    if(!symbol.isEmpty()&&symbol.peek()!='+'&&symbol.peek()!='-'&&symbol.peek()!='*'&&symbol.peek()!='/')
                    {
                        while(!symbol.isEmpty()&&symbol.peek()!='('
                                &&symbol.peek()!='+'&&symbol.peek()!='-'&&symbol.peek()!='*'&&symbol.peek()!='/')
                        {
                            number.push(' ');
                            number.push(symbol.pop());
                        }
                    }
                    number.push(' ');
                    symbol.push(input[i]);
                    i+=2;
                    break;
                case 'l':
                    if(!symbol.isEmpty()&&symbol.peek()!='+'&&symbol.peek()!='-'&&symbol.peek()!='*'&&symbol.peek()!='/')
                    {
                        while(!symbol.isEmpty()&&symbol.peek()!='('
                                &&symbol.peek()!='+'&&symbol.peek()!='-'&&symbol.peek()!='*'&&symbol.peek()!='/')
                        {
                            number.push(' ');
                            number.push(symbol.pop());
                        }
                    }
                    number.push(' ');
                    symbol.push(input[i]);
                    i+=1;
                    break;
                case '!':case 'E':case '^':case '√':
                    if(!symbol.isEmpty()&&symbol.peek()!='+'&&symbol.peek()!='-'&&symbol.peek()!='*'&&symbol.peek()!='/')
                    {
                        while(!symbol.isEmpty()&&symbol.peek()!='('
                                &&symbol.peek()!='+'&&symbol.peek()!='-'&&symbol.peek()!='*'&&symbol.peek()!='/')
                        {
                            number.push(' ');
                            number.push(symbol.pop());
                        }
                    }
                    number.push(' ');
                    symbol.push(input[i]);
                    break;
                case '(':
                    number.push(' ');
                    symbol.push(input[i]);
                    break;
                case ')':
                    while(!symbol.isEmpty()&&symbol.peek()!='(')
                    {
                        number.push(' ');
                        number.push(symbol.pop());
                    }
                    if(!symbol.isEmpty()&&symbol.peek()=='(')
                    {
                        symbol.pop();
                    }
                    break;
                default:
                    System.out.println("Illegal input!");
            }
            i++;
        }
        //运算符全部出栈，整合到一个栈中
        while(!symbol.isEmpty())
        {
            number.push(' ');
            number.push(symbol.pop());
        }
        Log.v("逆波兰表达式：",number.toString());
        //颠倒栈的顺序
        while(!number.isEmpty())
        {
            symbol.push(number.pop());
        }
        Log.v("逆波兰反序后：",symbol.toString());

        //对逆波兰表达式求值
        Stack<Double> stack=new Stack<Double>();
        StringBuffer std=new StringBuffer(); //未自行转换
        double right=0.0;
        char ch;
        while(!symbol.isEmpty())
        {
            ch=symbol.pop();
            switch (ch)
            {
                case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':case '.':
                    std.append(ch);
                    break;
                case '+':
                    right=stack.pop();
                    stack.push(stack.pop()+right);
                    break;
                case '-':
                    right=stack.pop();
                    stack.push(stack.pop()-right);
                    break;
                case '*':
                    right=stack.pop();
                    stack.push(stack.pop()*right);
                    break;
                case '/':
                    right=stack.pop();
                    stack.push(stack.pop()/right);
                    break;
                case 's':
                    stack.push(Math.sin(stack.pop()));
                    break;
                case 'c':
                    stack.push(Math.cos(stack.pop()));
                    break;
                case 't':
                    double tanValue=Math.tan(stack.pop());
                    if(Math.abs(tanValue)>5E15)
                        tanValue=0;
                    stack.push(tanValue);
                    break;
                case 'l':
                    stack.push(Math.log(stack.pop()));
                    break;
                case 'e':
                    stack.push(Math.exp(stack.pop()));
                    break;
                case '!':
                    right=stack.pop();
                    int max=(int)right;
                    int end=1;
                    while(max>0)
                    {
                        end*=max;
                        max--;
                    }
                    stack.push((double)end);
                    break;
                case '^':
                    right=stack.pop();
                    stack.push(Math.pow(stack.pop(),right));
                    break;
                case 'E':
                    right=stack.pop();
                    stack.push(stack.pop()*Math.pow(10,right));
                    break;
                case '√':
                    stack.push(Math.sqrt(stack.pop()));
                    break;
                case ' ':
                    if(std.length()!=0)
                    {
                        stack.push(StrToDouble(std));
                        std.setLength(0);
                    }
                    break;
            }
        }
        //表达式只有一个操作数，没有任何运算符
        if(std.length()!=0)
        {
            stack.push(StrToDouble(std));
            std.setLength(0);
        }
        //记下计算结果
        ANS=stack.peek();
        //保留10位小数，四舍五入
        BigDecimal bd=new BigDecimal(stack.pop().toString());
        double d=Double.parseDouble(bd.setScale(10,BigDecimal.ROUND_HALF_UP).toString());
        Log.v("bd",""+bd.toString());
        if(finalStr.endsWith(".0"))
            finalStr=finalStr.substring(0,finalStr.length()-2);
        return finalStr;
    }
}
