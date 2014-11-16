package com.example.taozhiheng.application19;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Stack;
import java.util.zip.Inflater;


public class MainActivity extends Activity implements View.OnClickListener{

    private boolean flag=true;  //是否可以用作表达式
    private StringBuffer show=new StringBuffer("0");
    private TextView display;

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

    private Button btn_sin;
    private Button btn_cos;
    private Button btn_tan;

    private Button btn_ln;
    private Button btn_E;
    private Button btn_j;

    private Button btn_pi;
    private Button btn_exp;
    private Button btn_m;

    private Button btn_left;
    private Button btn_right;
    private Button btn_sqr;

    private TableLayout firstView;
    private TableLayout secondView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstView=(TableLayout)findViewById(R.id.board1);
        secondView=(TableLayout)findViewById(R.id.board2);
        display=(TextView)findViewById(R.id.display);

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

            case R.id.btn_more:
                changeView();
                break;
            //清除键
            case R.id.btn_clear:
                show.setLength(0);
                show.append("0");
                break;
            //删除键
            case R.id.btn_delete:
                if(flag)
                {
                    if(show.length()>1)
                    {
                        show.deleteCharAt(show.length()-1);
                    }
                    else
                    {
                        show.replace(0,1,"0");
                    }
                }
                break;
           //小数点键
            case R.id.btn_dot:
                if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'
                        ||show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/'||show.charAt(show.length()-1)=='(')
                {
                    show.append("0.");
                }
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
                    show.replace(0,show.length(),calculate(show.toString()));
                }catch(Exception e)
                {
                    show.setLength(0);
                    show.append("出错");
                }
                break;
            //部分函数键sin cos tan ln log
            case R.id.btn_sin:
            case R.id.btn_cos:
            case R.id.btn_tan:
            case R.id.btn_ln:
            case R.id.btn_exp:
            case R.id.btn_sqr:
                if(!flag)
                {
                    show.setLength(0);
                    show.append("0");
                }
                if(show.toString().equals("0"))
                {
                    show.replace(0,1,btn.getText().toString()+"(");
                }
                else if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'||
                        show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/'||
                        show.charAt(show.length()-1)=='(')
                {
                    show.append(btn.getText().toString()+"(");
                }
                break;
            //阶乘键
            case R.id.btn_j:
                int index=show.length()-1;
                while(show.charAt(index)>='0'&&show.charAt(index)<='9'&&index>0)
                {
                    index--;
                }
                if(show.charAt(index)!='.')
                {
                    show.append('!');
                }
                break;
            //圆周率
            case R.id.btn_pi:
                if(!flag)
                {
                    show.setLength(0);
                    show.append("0");
                }
                if(show.toString().equals("0"))
                {
                    show.replace(0,1,btn.getText().toString());
                }
                else if(!(show.charAt(show.length()-1)==')'||show.charAt(show.length()-1)=='.'
                        ||(show.charAt(show.length()-1)>='0'&&show.charAt(show.length()-1)<='9')))
                {
                    show.append(btn.getText().toString());
                }
                break;
            //左括号
            case R.id.btn_left:
                if(!flag)
                {
                    show.setLength(0);
                    show.append("0");
                }
                if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'
                        ||show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/'||show.charAt(show.length()-1)=='(')
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
        }
        if(v.getId()!=R.id.btn_equ&&v.getId()!=R.id.btn_delete&&v.getId()!=R.id.btn_more)
            flag=true;
        display.setText(show);
    }

    //由输入字符串求值
    protected String calculate(String str)
    {
        str=str.replace("(-","(0-");
        str=str.replace("(+","(0+");
        if(str.charAt(0)=='-')
        {
            str="0"+str;
        }
        //处理PI
        str=str.replace("π",String.valueOf(Math.PI));

        //过滤单操作数运算
        int index;
        //处理Ｅ
        if((index=str.indexOf("E-"))!=-1)
        {
            str=str.replace("E-","E(0-");
            while((str.charAt(index)>='0'&&str.charAt(index)<='9')||str.charAt(index)=='.')
                index++;
            str=str.substring(0,index)+")"+str.substring(index,str.length());
        }
        int left=0;
        int right=0;
        StringBuffer strb=new StringBuffer();
        //处理sin
        if((index=str.indexOf("sin"))!=-1)
        {
            index+=3;
            do
            {
                if(str.charAt(index)=='(')
                    left++;
                if(str.charAt(index)==')')
                    right++;
                strb.append(str.charAt(index));
                index++;
            }while(left>right&&index<str.length());
            String firstRes=calculate(strb.toString());
            firstRes=String.valueOf(Math.sin(Double.parseDouble(firstRes)));
            Log.v("sin的值",firstRes);
            Log.v("calculate的参数",str.substring(0,str.indexOf("sin"))+firstRes+str.substring(index,str.length()));
            return calculate(str.substring(0,str.indexOf("sin"))+firstRes+str.substring(index,str.length()));
        }
        //处理cos
        strb.setLength(0);
        if((index=str.indexOf("cos"))!=-1)
        {
            index+=3;
            do
            {
                if(str.charAt(index)=='(')
                    left++;
                if(str.charAt(index)==')')
                    right++;
                strb.append(str.charAt(index));
                index++;
            }while(left>right);
            String firstRes=calculate(strb.toString());
            firstRes=String.valueOf(Math.cos(Double.parseDouble(firstRes)));
            return calculate(str.substring(0,str.indexOf("cos"))+firstRes+str.substring(index,str.length()));
        }
        //处理tan
        strb.setLength(0);
        if((index=str.indexOf("tan"))!=-1)
        {
            index+=3;
            do
            {
                if(str.charAt(index)=='(')
                    left++;
                if(str.charAt(index)==')')
                    right++;
                strb.append(str.charAt(index));
                index++;
            }while(left>right);
            String firstRes=calculate(strb.toString());
            firstRes=String.valueOf(Math.tan(Double.parseDouble(firstRes)));
            return calculate(str.substring(0,str.indexOf("tan"))+firstRes+str.substring(index,str.length()));
        }
        //处理ln
        strb.setLength(0);
        if((index=str.indexOf("ln"))!=-1)
        {
            index+=2;
            do
            {
                if(str.charAt(index)=='(')
                    left++;
                if(str.charAt(index)==')')
                    right++;
                strb.append(str.charAt(index));
                index++;
            }while(left>right);
            String firstRes=calculate(strb.toString());
            firstRes=String.valueOf(Math.log(Double.parseDouble(firstRes)));
            return calculate(str.substring(0,str.indexOf("ln"))+firstRes+str.substring(index,str.length()));
        }
        //处理exp
        strb.setLength(0);
        if((index=str.indexOf("exp"))!=-1)
        {
            index+=3;
            do
            {
                if(str.charAt(index)=='(')
                    left++;
                if(str.charAt(index)==')')
                    right++;
                strb.append(str.charAt(index));
                index++;
            }while(left>right);
            String firstRes=calculate(strb.toString());
            firstRes=String.valueOf(Math.exp(Double.parseDouble(firstRes)));
            return calculate(str.substring(0,str.indexOf("exp"))+firstRes+str.substring(index,str.length()));
        }
        //处理根号
        strb.setLength(0);
        if((index=str.indexOf("√"))!=-1)
        {
            index+=1;
            do
            {
                if(str.charAt(index)=='(')
                    left++;
                if(str.charAt(index)==')')
                    right++;
                strb.append(str.charAt(index));
                index++;
            }while(left>right);
            String firstRes=calculate(strb.toString());
            firstRes=String.valueOf(Math.sqrt(Double.parseDouble(firstRes)));
            return calculate(str.substring(0,str.indexOf("√"))+firstRes+str.substring(index,str.length()));
        }
        //处理阶乘
        strb.setLength(0);
        if((index=str.indexOf("!"))!=-1)
        {
            index-=1;
            while(index>=0&&str.charAt(index)>='0'&&str.charAt(index)<='9')
            {
                strb.append(str.charAt(index));
                index--;
            }
            int max=Integer.parseInt(strb.reverse().toString());
            int deal=1;
            while(max>0)
            {
                deal*=max;
                max--;
            }
            Log.v("deal",String.valueOf(deal));
            return calculate(str.substring(0,index+1)+String.valueOf(deal)+str.substring(str.indexOf("!")+1,str.length()));
        }
        char[] input=str.toCharArray();
        //转为逆波兰表达式
        Stack<Character> stack=new Stack<Character>();
        Stack<Character> out=new Stack<Character>();
        int i=0;
        while(i<input.length)
        {
            switch(input[i])
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '.':
                    out.push(input[i]);
                    break;
                case '+':
                case '-':
                    while(!stack.isEmpty()&&stack.peek()!='(')
                    {
                        out.push(' ');
                        out.push(stack.pop());
                    }
                    out.push(' ');
                    stack.push(input[i]);
                    break;
                case '*':
                case '/':
                    if(!stack.isEmpty()&&stack.peek()!='+'&&stack.peek()!='-')
                    {
                        while(!stack.isEmpty()&&stack.peek()!='(')
                        {
                            out.push(' ');
                            out.push(stack.pop());
                        }
                    }
                    out.push(' ');
                    stack.push(input[i]);
                    break;
                case '^':
                case 'E':
                    if(!stack.isEmpty()&&(stack.peek()=='^'||stack.peek()=='E'))
                    {
                        while(!stack.isEmpty()&&stack.peek()!='(')
                        {
                            out.push(' ');
                            out.push(stack.pop());
                        }
                    }
                    out.push(' ');
                    stack.push(input[i]);
                    break;
                case '(':
                    out.push(' ');
                    stack.push(input[i]);
                    break;
                case ')':
                    while(!stack.isEmpty()&&stack.peek()!='(')
                    {
                        out.push(' ');
                        out.push(stack.pop());
                    }
                    if(!stack.isEmpty()&&stack.peek()=='(')
                    {
                        stack.pop();
                    }
                    break;
                default:
                    System.out.println("Illegal input!");
            }
            i++;
        }
        while(!stack.isEmpty())
        {
            out.push(' ');
            out.push(stack.pop());
        }

        //对逆波兰表达式求值
        while(!out.isEmpty())
        {
            stack.push(out.pop());
        }
        String res=getResult(stack);
        flag=false;
        return res;
    }
    //由逆波兰表达式求值
    private String getResult(Stack<Character> input)
    {
        Stack<Double> stack=new Stack<Double>();
        StringBuffer str=new StringBuffer(); //未自行转换
        Double right=0.0;
        char ch;
        while(!input.isEmpty())
        {
            ch=input.pop();
            switch (ch)
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '.':
                    str.append(ch);
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
                case '^':
                    right=stack.pop();
                    stack.push(Math.pow(stack.pop(),right));
                    break;
                case 'E':
                    right=stack.pop();
                    stack.push(stack.pop()*Math.pow(10,right));
                case ' ':
                    if(str.length()!=0)
                    {
                        stack.push(Double.parseDouble(str.toString()));
                        str.setLength(0);
                    }
                    break;
            }
        }
        if(str.length()!=0)
        {
            stack.push(Double.parseDouble(str.toString()));
            str.setLength(0);
        }
        return stack.pop().toString();
    }
    //０～９键处理
    private void appendNumber(String str)
    {
       if(!flag)
       {
           show.setLength(0);
           show.append("0");
       }
       if(show.toString().equals("0"))
       {
           show.replace(0,1,str);
       }
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
        if(show.charAt(show.length()-1)=='+'||show.charAt(show.length()-1)=='-'
                ||show.charAt(show.length()-1)=='*'||show.charAt(show.length()-1)=='/')
        {
            show.deleteCharAt(show.length()-1);
            show.append(str);
        }
        else if(show.charAt(show.length()-1)=='.')
        {
            show.append("0"+str);
        }
        else
        {
            show.append(str);
        }
    }

    private void changeView()
    {
        if(firstView.getVisibility()==View.VISIBLE)
        {
            firstView.setVisibility(View.INVISIBLE);
            secondView.setVisibility(View.VISIBLE);
        }
        else
        {
            firstView.setVisibility(View.VISIBLE);
            secondView.setVisibility(View.INVISIBLE);
        }

    }
}
