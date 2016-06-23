package feedbackActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hustunique.myapplication.R;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.ArrayList;
import java.util.List;

import tools.TimeUtil;

/**
 * 反馈列表的适配器。
 * 部分程序逻辑来源自官方论坛提供的demo，详见：
 * http://bbs.umeng.com/thread-7309-1-1.html
 * 
 * @author 寂静的写者
 * @since 2015年2月25日19:00:29
 * @blog http://blog.csdn.net/LonelyWriter
 */
public class ReplyAdapter extends BaseAdapter {
	
	private final int VIEW_TYPE_USER = 0;
	private final int VIEW_TYPE_DEV = 1;
	private final int VIEW_TYPE_COUNT = 2;
	
	private static class ViewHolder {
		public TextView replyContent;
		public ProgressBar replyProgressBar;
		public ImageView replyStateFailed;
		public TextView replyData;
	}
	
	/* 数据 */
	private Context context;
	private final List<Reply> repliesList = new ArrayList<>();
	
	public ReplyAdapter(Context context) {
		super();
		this.context = context;
	}
	
	/* 继承：BaseAdapter */
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// 获取单条回复
		Reply reply = getItem(position);
		if (convertView == null) {
			// 根据Type的类型来加载不同的Item布局
			if (Reply.TYPE_DEV_REPLY.equals(reply.type)) {
				// 开发者的回复
				convertView = LayoutInflater.from(context).inflate(R.layout.listitem_feedback_reply_dev, null);
			} else {
				// 用户的反馈、回复
				convertView = LayoutInflater.from(context).inflate(R.layout.listitem_feedback_reply_user, null);
			}
			
			// 创建ViewHolder并获取各种View
			holder = new ViewHolder();
			holder.replyContent = (TextView) convertView.findViewById(R.id.textView_listItem_feedBack_reply_content);
			holder.replyProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_listItem_feedBack_reply);
			holder.replyStateFailed = (ImageView) convertView
					.findViewById(R.id.imageView_listItem_feedBack_reply_failed);
			holder.replyData = (TextView) convertView.findViewById(R.id.textView_listItem_feedBack_reply_date);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// 设置Reply的内容
		holder.replyContent.setText(reply.content);
		// 在App应用界面，对于开发者的Reply来讲status没有意义
		if (!Reply.TYPE_DEV_REPLY.equals(reply.type)) {
			// 根据Reply的状态来设置replyStateFailed的状态
			if (Reply.STATUS_NOT_SENT.equals(reply.status)) {
				holder.replyStateFailed.setVisibility(View.VISIBLE);
			} else {
				holder.replyStateFailed.setVisibility(View.GONE);
			}
			
			// 根据Reply的状态来设置replyProgressBar的状态
			if (Reply.STATUS_SENDING.equals(reply.status)) {
				holder.replyProgressBar.setVisibility(View.VISIBLE);
			} else {
				holder.replyProgressBar.setVisibility(View.GONE);
			}
		}
		
		// 判断是否需要显示时间
		if (position == 0) {// 第一条数据，显示时间
			holder.replyData.setText(TimeUtil.getDescription(reply.created_at, null));
			holder.replyData.setVisibility(View.VISIBLE);
		} else {
			// 获取上一条数据，此时上一条回复必定存在
			Reply prevReply = getItem(position - 1);
			if (reply.created_at - prevReply.created_at >= 1800000) {// 两条Reply之间相差三十分钟时，显示时间
				holder.replyData.setText(TimeUtil.getDescription(reply.created_at, null));
				holder.replyData.setVisibility(View.VISIBLE);
			} else {// 其余情况不显示
				holder.replyData.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	@Override
	public int getCount() {
		return repliesList.size();
	}
	
	@Override
	public Reply getItem(int position) {
		return repliesList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		// 两种不同的Item布局
		return VIEW_TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		// 获取单条回复
		Reply reply = getItem(position);
		if (Reply.TYPE_DEV_REPLY.equals(reply.type)) {
			// 开发者回复Item布局
			return VIEW_TYPE_DEV;
		} else {
			// 用户反馈、回复Item布局
			return VIEW_TYPE_USER;
		}
	}
	
	/* 拓展：显示反馈信息 */
	
	/**
	 * 显示反馈会话信息。
	 * 
	 * @param conversation
	 */
	public void displayConversation(Conversation conversation) {
		repliesList.clear();
		repliesList.addAll(conversation.getReplyList());
		notifyDataSetChanged();
	}
	
}
