package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * 弹性listView。
 * 注意，当且仅当在列表项目够多可占满一个屏幕以上时才能显现出弹性效果。
 * 
 * @author 参阅自：
 *         http://blog.csdn.net/eastman520/article/details/
 *         19043973
 * @since 2015年1月30日19:34:08
 */
public class ElasticListView extends ListView {
	
	/**
	 * 初始可拉动Y轴方向距离
	 */
	private static final int MAX_Y_OVERSCROLL_DISTANCE = 120;
	// 实际可上下拉动Y轴上的距离
	private int distance;
	
	public ElasticListView(Context context) {
		super(context);
		initWidget();
	}
	
	public ElasticListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWidget();
	}
	
	public ElasticListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWidget();
	}
	
	private void initWidget() {
		// 初始化参数
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		float density = metrics.density;
		distance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
	}
	
	/* 拓展：弹性 */
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// 实现的本质就是在这里动态改变了maxOverScrollY的值
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				distance, isTouchEvent);
	}
	
}