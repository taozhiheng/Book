package ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class Pointwithcolor extends View {
	
	private float width=30;
	private float height=30;
	private Paint paint;
	private int color= Color.GRAY;
	public Pointwithcolor(Context context, AttributeSet attrs) {
		super(context, attrs);
        //TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.Pointwithcolor);
        //this.height=ta.getDimension(R.styleable.Pointwithcolor_pheight,30);
        //this.width=ta.getDimension(R.styleable.Pointwithcolor_pwidth,30);
		paint=new Paint();
		paint.setStyle(Style.FILL);
		// TODO Auto-generated constructor stub
	}
	
	
	public Pointwithcolor(Context context){
		super(context);
		paint=new Paint();
		paint.setStyle(Style.FILL);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		this.height=this.getHeight()==0?30:this.getHeight();
		this.width=this.getWidth()==0?30:this.getWidth();
		paint.setColor(this.color);
		paint.setAntiAlias(true);
		canvas.drawCircle(width/2 ,height/2, height/2, paint);
	}

    public int getColor(){
        return this.color;
    }

	public void setColor(int color){
		this.color=color;
		postInvalidate();
	}
	
	
}
