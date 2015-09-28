package ui;

import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by taozhiheng on 15-4-15.
 *
 */
public class MyAnimation extends Animation {

    private float centerX;
    private float centerY;
    private float fromX;
    private float fromY;
    private float toX;
    private float toY;
    private float scale;
    private View view;

    public MyAnimation(View v, float toX, float toY, float scale)
    {
        Log.d("drag", "animation shadow position.x:" + v.getX() + " y:" + v.getY()+" scale:"+scale);
        this.view = v;
        this.toX = toX;
        this.toY = toY;
        this.scale = scale;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        Log.d("drag", "before initialize super shadow position.x:" + view.getX() + " y:" + view.getY());
        super.initialize(width, height, parentWidth, parentHeight);
        Log.d("drag", "initialize shadow position.x:" + view.getX() + " y:" + view.getY());
        centerX = width/2.0f;
        centerY = height/2.0f;
//        toX = parentWidth/2.0f;
//        toY = parentHeight/2.0f;
        fromX = view.getX();
        fromY = view.getY();
//        scale = parentHeight/height;
        //setFillAfter(true);
        setDuration(1000);
        setInterpolator(new AccelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final Matrix matrix = t.getMatrix();
        if(interpolatedTime<0.5f)
            matrix.setTranslate(2*(toX- fromX -centerX)*interpolatedTime, 2*(toY- fromY -centerY)*interpolatedTime);
        else
        {
            matrix.setScale(2*(scale-1)*interpolatedTime-scale+2, 2*(scale-1)*interpolatedTime-scale+2, toX - fromX, toY - fromY);
            matrix.preTranslate(toX - fromX - centerX, toY - fromY - centerY);
        }
    }
}
