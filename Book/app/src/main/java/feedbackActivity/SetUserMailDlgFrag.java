package feedbackActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

import data.UserPref;

/**
 * 封装了设置用户邮箱功能的DialogFragment。
 * 因为重写了{@link #onSaveInstanceState(Bundle)}
 * ，所以无需担心屏幕旋转对其的影响。
 * 
 * @author 寂静的写者
 * @since 2015年2月25日19:00:29
 * @blog http://blog.csdn.net/LonelyWriter
 */
public class SetUserMailDlgFrag extends DialogFragment implements OnClickListener {
	
	/**
	 * 标志输入框数据的key
	 */
	private static final String KEY_INPUT_USER_MAIL = "KEY_INPUT_USER_MAIL";
	
	/* 视图 */
	private EditText mailEdit;
	
	/* 继承：DiaglogFragment */
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		// 初始化输入框
		mailEdit = new EditText(getActivity());
		mailEdit.setSingleLine();
		if (getUserMail() != null) {
			mailEdit.setText(getUserMail());
		}
		mailEdit.setHint("邮箱或QQ");
		UserPref.init(getActivity());
		mailEdit.setText(UserPref.getUserMail());
		mailEdit.setSelectAllOnFocus(true);
		
		// 恢复数据，如果有的话
		if (savedInstanceState != null && savedInstanceState.containsKey(KEY_INPUT_USER_MAIL)) {
			mailEdit.setText(savedInstanceState.getString(KEY_INPUT_USER_MAIL));
		}
		
		// 创建对话框
		AlertDialog.Builder builder;
		if (android.os.Build.VERSION.SDK_INT >= 11) {// API在11以上
			// 使用holo_dark主题
			builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
		} else {// 在11以下，不使用主题
			builder = new AlertDialog.Builder(getActivity());
		}
		builder.setTitle("请告诉我们您的联系方式").setView(mailEdit).setPositiveButton("确定", this).setNegativeButton("取消", null);
		return builder.create();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 保存数据
		outState.putString(KEY_INPUT_USER_MAIL, mailEdit.getText().toString());
	}
	
	/* 接口：OnClickListener */
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// 确定
				String userMail = mailEdit.getText().toString();
				if (userMail != null && !userMail.equals("") && !userMail.equals(getUserMail())) {
					// 输入不为空，且和原有配置不同时才更新邮箱信息
					updateUserMailAddress(userMail);
				}
				break;
		}
	}
	
	/* 拓展：用户邮箱 */
	
	/**
	 * 配置文件中对应的key
	 */
	private static final String KEY_PREF_USER_MAIL = "KEY_PREF_USER_MAIL";
	
	/**
	 * 将用户邮箱写入配置文件中
	 * 
	 * @param userMail
	 *            用户邮箱
	 */
	private void setUserMail(String userMail) {
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(KEY_PREF_USER_MAIL, userMail)
				.commit();
	}
	
	/**
	 * 获取用户邮箱。
	 * 
	 * @return
	 *         用户邮箱。
	 *         默认值为null
	 */
	private String getUserMail() {
		return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(KEY_PREF_USER_MAIL, null);
	}
	
	/**
	 * 设置本地用户邮箱地址配置，同时异步更新友盟用户配置。
	 * 
	 * @param userMail
	 */
	private void updateUserMailAddress(String userMail) {
		// 更新本地配置文件
		setUserMail(userMail);
		
		// 创建反馈
		final FeedbackAgent feedbackAgent = new FeedbackAgent(getActivity());
		
		// 创建map保存信息
		Map<String, String> contact = new HashMap<>();
		contact.put("email/qq", userMail);
		UserInfo userInfo = feedbackAgent.getUserInfo();
		if (userInfo == null) {
			userInfo = new UserInfo();
		}
		userInfo.setContact(contact);
		feedbackAgent.setUserInfo(userInfo);
		
		// 异步更新友盟上的用户信息
		new Thread(new Runnable() {
			@Override
			public void run() {
				feedbackAgent.updateUserInfo();
			}
		}).start();
		
	}
	
}
