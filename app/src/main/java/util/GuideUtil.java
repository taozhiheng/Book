package util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hustunique.myapplication.R;



public class GuideUtil {
    private Context context;
    private ImageView imgView;
    private WindowManager windowManager;
    private static GuideUtil instance = null;
    /** 是否第一次进入该程序 **/
    private boolean isFirst = true;
    private int index = 0;

    private ClearGuideListener mClearListener;

    public interface ClearGuideListener
    {
        public void clearGuide();
    }

    public void setClearGuideListener(ClearGuideListener listener)
    {
        this.mClearListener = listener;
    }

    /**采用私有的方式，只保证这种通过单例来引用，同时保证这个对象不会存在多个**/
    private GuideUtil() {
    }

    /**采用单例的设计模式，同时用了同步锁**/
    public static GuideUtil getInstance() {
        synchronized (GuideUtil.class) {
            if (null == instance) {
                instance = new GuideUtil();
            }
        }
        return instance;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 1:
                // 设置LayoutParams参数
                final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                // 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
                // 设置显示格式
                params.format = PixelFormat.RGBA_8888;
                // 设置对齐方式
                params.gravity = Gravity.LEFT | Gravity.TOP;
                // 设置宽高
                params.width = ScreenUtils.getScreenWidth(context);
                params.height = ScreenUtils.getScreenHeight(context);
                // 设置动画
//                params.windowAnimations = R.style.view_anim;

                // 添加到当前的窗口上
                windowManager.addView(imgView, params);
                Log.d("life cycle", "add view");
                break;
            }
        };
    };

/**
     * @方法说明:初始化
     * @方法名称:initGuide
     * @param context
     * @param resIds：引导图片的资源Id
     * @返回值:void
     */
    public void initGuide(Activity context, final int[] resIds) {
        /**如果不是第一次进入该界面**/
        if (!isFirst ) {
            return;
        }
        this.context = context;
        windowManager = context.getWindowManager();

        /** 动态初始化图层**/
        imgView = new ImageView(context);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        imgView.setImageResource(resIds[index]);
        /**这里我特意用了一个handler延迟显示界面，主要是为了进入界面后，你能看到它淡入得动画效果，不然的话，引导界面就直接显示出来**/
        handler.sendEmptyMessageDelayed(1, 1000);
        Log.d("life cycle", "send message");

        imgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /** 点击图层之后，将图层移除**/
                Log.d("life cycle", "onClick");
                index++;
                if(index < resIds.length)
                    imgView.setImageResource(resIds[index]);
                else {
                    if (mClearListener != null)
                    {
                        mClearListener.clearGuide();
                        mClearListener = null;
                    }
                    windowManager.removeView(imgView);
                }
            }
        });
    }

    public boolean isFirst() {
        return isFirst;
    }

/**
     * @方法说明:设置是否第一次进入该程序
     * @方法名称:setFirst
     * @param isFirst
     * @返回值:void
     */
    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
        if(isFirst)
            index = 0;
    }
}