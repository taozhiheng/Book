package feedbackActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hustunique.myapplication.R;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 封装了用户发送反馈和接受开发者回复功能的fragment。
 * 
 * @author 寂静的写者
 * @since 2015年2月25日19:00:29
 * @blog http://blog.csdn.net/LonelyWriter
 */
public class FeedbackFrag extends Fragment implements SyncListener, OnRefreshListener, OnClickListener {
	
	/**
	 * 用于恢复数据时标志用户已输入文字的key
	 */
	private static final String KEY_FEEDBACK_CONTENT = "KEY_FEEDBACK_CONTENT";
	
	/**
	 * 处理非UI线程发起的UI事件
	 */
	private static final class SyncHandler extends Handler {
		
		public static final int EVENT_SYNC = 1;// 同步事件
		
		/* 控制 */
		private FeedbackFrag feedbackFrag;
		
		public SyncHandler(FeedbackFrag feedbackFrag) {
			super();
			this.feedbackFrag = feedbackFrag;
		}
		
		/* 继承：Handler */
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == EVENT_SYNC) {
				feedbackFrag.sync();
			}
		}
	}
	
	/* 数据 */
	private Conversation conversation;
	/* 视图 */
	private SwipeRefreshLayout swipeLayout;
	private ListView listView;
	private EditText inputText;
	private Button sendBtn;
	/* 控制 */
	private ReplyAdapter adapter;
	private FeedbackAgent feedbackAgent;
	private Timer timer;
	private int lastSize = 0;// 用于标志上一次回复的数量
	private SyncHandler handler = new SyncHandler(this);
	
	/* 继承：Fragment */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化用户反馈
		feedbackAgent = new FeedbackAgent(getActivity());
		feedbackAgent.closeAudioFeedback();
		feedbackAgent.openFeedbackPush();
		conversation = feedbackAgent.getDefaultConversation();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.frag_feedback, container, false);
		
		initView(rootView);
		
		// 恢复保存的数据
		if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FEEDBACK_CONTENT)) {
			inputText.setText(savedInstanceState.getString(KEY_FEEDBACK_CONTENT));
		}
		
		return rootView;
	}
	
	/**
	 * 初始化所有控件
	 * 
	 * @param rootView
	 */
	private void initView(View rootView) {
		// 获取控件
		swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout_frag_feedback);
		listView = (ListView) rootView.findViewById(R.id.listView_frag_feedback_list);
		inputText = (EditText) rootView.findViewById(R.id.editText_frag_feedback_content);
		sendBtn = (Button) rootView.findViewById(R.id.button_frag_feedback_send);
		
		// 初始化控件样式
		swipeLayout.setColorSchemeResources(R.color.holo_blue_dark, R.color.holo_green_light, R.color.holo_blue_light,
				R.color.holo_green_light);
		
		// 初始化控件监听
		sendBtn.setOnClickListener(this);
		swipeLayout.setOnRefreshListener(this);
		
		// 初始化适配器
		adapter = new ReplyAdapter(getActivity());
		listView.setAdapter(adapter);
		adapter.displayConversation(conversation);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		beginAutoSync();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// 关闭自动同步
		stopAutpSync();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 保存用户已经输入的文字
		outState.putString(KEY_FEEDBACK_CONTENT, inputText.getText().toString());
	}
	
	/* 接口： SyncListener、OnRefreshListener、OnClickListener */
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.button_frag_feedback_send:
				String content = inputText.getText().toString();
				inputText.getEditableText().clear();
				if (!TextUtils.isEmpty(content)) {
					// 将内容添加到会话列表，这样用户体验会更好一点
					conversation.addUserReply(content);
					// 刷新ListView
					adapter.displayConversation(conversation);
					scrollToBottom();
					// 数据同步
					sync();
				}
				break;
		}
	}
	
	@Override
	public void onRefresh() {
		sync();
	}
	
	@Override
	public void onReceiveDevReply(List<Reply> arg0) {
		if (conversation.getReplyList().size() > lastSize) {// 仅当有新回复时才刷新界面并滚动到底部
			adapter.displayConversation(conversation);
			scrollToBottom();
			lastSize = conversation.getReplyList().size();
		}
	}
	
	@Override
	public void onSendUserReply(List<Reply> arg0) {
		// SwipeRefreshLayout停止刷新
		swipeLayout.setRefreshing(false);
		
		if (conversation.getReplyList().size() > lastSize) {// 仅当有新回复时才刷新界面并滚动到底部
			adapter.displayConversation(conversation);
			scrollToBottom();
			lastSize = conversation.getReplyList().size();
		}
	}
	
	/* 拓展：同步控制 */
	
	private void sync() {
		conversation.sync(this);
	}
	
	private void scrollToBottom() {
		if (adapter.getCount() > 0) {
			listView.smoothScrollToPosition(adapter.getCount());
		}
	}
	
	/* 拓展：自动同步 */
	
	/**
	 * 自动同步的时间间隔。 <br>
	 * 默认为10000毫秒，也就是10秒。
	 */
	private int syncInterval = 10000;
	
	public int getSyncInterval() {
		return syncInterval;
	}
	
	public void setSyncInterval(int syncInterval) {
		this.syncInterval = syncInterval;
	}
	
	/**
	 * 启动自动同步。
	 * 注意：<li>启动后会立即调用一次同步。 <li>
	 * 如果计时器timer不为null，会执行cancel后启动新的计时任务。
	 */
	private void beginAutoSync() {
		// 关闭上一同步状态
		stopAutpSync();
		
		// 10之后，每间隔10秒刷新一次
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// 调用handler刷新启动刷新
				Message syncMsg = new Message();
				syncMsg.what = SyncHandler.EVENT_SYNC;
				handler.sendMessage(syncMsg);
			}
		}, 0, syncInterval);
	}
	
	/**
	 * 关闭自动同步。
	 */
	private void stopAutpSync() {
		// 取消自动同步
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
}
