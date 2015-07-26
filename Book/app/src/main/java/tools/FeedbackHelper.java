package tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.hustunique.myapplication.FeedbackActivity;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.List;

/**
 * 本类是封装了检查开发者回复的工具类。
 * 部分逻辑来源自官方论坛提供的DEMO，详见：
 * http://bbs.umeng.com/thread-7309-1-1.html
 * 
 * @author 寂静的写者
 * @since 2015年2月25日19:00:29
 * @blog http://blog.csdn.net/LonelyWriter
 */
public class FeedbackHelper implements SyncListener {
	
	/* 数据 */
	private Context context;
	/* 控制 */
	private FeedbackAgent agent;
	private Conversation conversation;
	
	public FeedbackHelper(Context context) {
		super();
		this.context = context;
		
		// 初始化反馈组件
		agent = new FeedbackAgent(context);
		agent.closeAudioFeedback();
		agent.openFeedbackPush();
		conversation = agent.getDefaultConversation();
	}
	
	/* 接口：SyncListener */
	
	@Override
	public void onReceiveDevReply(List<Reply> replies) {
		String content = "";
		if (replies.size() == 1) {// 一条开发者回复
			content = "开发者：" + replies.get(0).content;
		} else if (replies.size() > 1) {// 多条回复
			content = "有 " + replies.size() + " 条新回复";
		} else {// 没有回复
			return;
		}
		
		System.out.println("接收到了开发者的新回复");
		
		// 提示用户
		showDevReplyNotification("有开发者的回复", content);
	}
	
	@Override
	public void onSendUserReply(List<Reply> replies) {
	}
	
	/* 拓展： 检查更新 */
	
	/**
	 * 检查开发者的新回复，如果有则以Notification的方式通知用户。<br>
	 * 当用户点击之后，默认会跳转到{@link FeedbackActivity}界面去。
	 */
	public void checkDevReply() {
		conversation.sync(this);
	}
	
	/**
	 * 使用Notification通知用户有开发者的新回复。
	 * 当用户点击之后会跳转到{@link FeedbackActivity}去。
	 * 
	 * @param title
	 *            通知的标题
	 * @param content
	 *            通知的内容
	 */
	private void showDevReplyNotification(String title, String content) {
		// 获取NotificationManager服务
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent intentToLaunch = new Intent(context, FeedbackActivity.class);// 跳转到我们自定义反馈UI的FeedbackActivity
		intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		int requestCode = (int) SystemClock.uptimeMillis();
		PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, intentToLaunch,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		try {
			// 获取本应用的icon
			int appIcon = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.icon;
			
			// 显示通知
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(appIcon)
					.setContentTitle(title).setTicker(title).setContentText(content).setAutoCancel(true)
					.setContentIntent(contentIntent);
			notificationManager.notify(0, mBuilder.build());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
