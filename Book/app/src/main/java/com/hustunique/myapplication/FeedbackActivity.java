package com.hustunique.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import feedbackActivity.FeedbackFrag;
import feedbackActivity.SetUserMailDlgFrag;

/**
 * @author 寂静的写者
 * @since 2015年2月25日19:00:29
 * @blog http://blog.csdn.net/LonelyWriter
 */
public class FeedbackActivity extends AppCompatActivity {

	private Toolbar mToolbar;

	/* 继承：FeedbackActivity */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		mToolbar = (Toolbar) findViewById(R.id.feedback_toolbar);
		mToolbar.setTitle("口袋书目反馈");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 初始化动作栏
		
		// 添加反馈Fragment
		if (savedInstanceState == null) {
			FeedbackFrag feedbackFrag = new FeedbackFrag();
			getFragmentManager().beginTransaction().add(R.id.frameLayout_feedback_content, feedbackFrag).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_feedback, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_feedback_email)
		{
			new SetUserMailDlgFrag().show(getFragmentManager(), "setUserMailDlgFrag");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
