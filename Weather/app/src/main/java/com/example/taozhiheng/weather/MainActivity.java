package com.example.taozhiheng.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, NLPullRefreshView.RefreshListener{

    private DrawerLayout drawerLayout;                        //滑动抽屉
    private ListView leftListView;                            //左边listView
    private ListView rightListView;                           //右边listView
    private TextView rightHeaderView;                         //右边listView的headerView
    private MyLeftAdapter leftAdapter;                        //左边listView适配器
    private MyRightAdapter rightAdapter;                      //右边listView适配器
    private SearchView search;                                //搜索框
    private TextView city;                                    //显示当前城市信息
    private TextView highTemperature;                         //白天温度
    private TextView lowTemperature;                          //夜间温度
    private TextView weather;                                 //显示天气描述信息
    private Button update;                                    //查询更新按钮
    private Button last;                                      //产看上一天按钮
    private Button next;                                      //查看下一天按钮
    private ImageView dayIcon;                                //白天天气图标
    private ImageView nightIcon;                              //夜间天气图标
    private MyWeatherView future;                             //天气视图
    private Receiver receiver;                                //广播接收者
    private ArrayList<DailyWeather> list;                     //左边listView数据源
    private List<String> stringList= new ArrayList<String>(); //右边listView数据源
    private String currentCity;                               //当前城市名称
    private int currentPage = 0;                              //当前日期索引
    private SharedPreferences pref;                           //保存默认城市
    private NLPullRefreshView pullRefreshView;                //下拉刷新视图
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("defaultCity", MODE_PRIVATE);
        currentCity = pref.getString(Constant.PREF_KEY, "武汉");
        setContentView(R.layout.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(0x88ffffff);
        leftListView = (ListView) findViewById(R.id.left_list);
        rightListView = (ListView) findViewById(R.id.right_list);
        rightHeaderView = new TextView(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightHeaderView.setLayoutParams(params);
        rightHeaderView.setTextSize(25f);
        rightHeaderView.setTextColor(Color.WHITE);
        rightHeaderView.setGravity(Gravity.CENTER);
        rightListView.addHeaderView(rightHeaderView);
        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rightAdapter.showDetail(position-1);
            }
        });
        search = (SearchView) findViewById(R.id.search);
        city = (TextView) findViewById(R.id.city);
        highTemperature = (TextView) findViewById(R.id.highTemperature);
        lowTemperature = (TextView) findViewById(R.id.lowTemperature);
        weather = (TextView) findViewById(R.id.weather);
        update = (Button) findViewById(R.id.update);
        last = (Button) findViewById(R.id.last);
        next = (Button) findViewById(R.id.next);
        update.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);
        city.setOnClickListener(this);
        weather.setOnClickListener(this);
        dayIcon = (ImageView) findViewById(R.id.dayIcon);
        nightIcon = (ImageView) findViewById(R.id.nightIcon);
        future = (MyWeatherView) findViewById(R.id.future);

        pullRefreshView = (NLPullRefreshView) findViewById(R.id.refresh_root);
        pullRefreshView.setRefreshListener(this);

        receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.RESPONSE);
        registerReceiver(receiver, filter);
        Intent intent = new Intent().setAction(Constant.CREATE);
        intent.putExtra(Constant.IS_QUERY, Constant.OK);
        intent.putExtra(Constant.REQUEST_CITY,currentCity);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_settings:
                View view = getLayoutInflater().inflate(R.layout.dialog, null);
                final EditText editText = (EditText) view.findViewById(R.id.defaultCity);
                editText.setText(pref.getString(Constant.PREF_KEY, "武汉"));
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("设置默认城市")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pref.edit().putString(Constant.PREF_KEY, editText.getText().toString()).apply();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                return true;
            case R.id.action_exit:
                stopService(new Intent(this, MyService.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.update:
                InputMethodManager manager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                manager.hideSoftInputFromWindow(search.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                if(search.getQuery().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(), "Sorry, the city is not existed!", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent().setAction(Constant.CREATE);
                intent.putExtra(Constant.IS_QUERY, Constant.OK);
                intent.putExtra(Constant.REQUEST_CITY, search.getQuery().toString());
                startService(intent);
                break;
            case R.id.last:
                currentPage = (7+currentPage-1)%7;
                showWeather();
                break;
            case R.id.next:
                currentPage = (currentPage+1)%7;
                showWeather();
                break;
            case R.id.city:
                Toast.makeText(getBaseContext(), "我不会告诉你左边还藏有东西哦", Toast.LENGTH_SHORT).show();
                break;
            case R.id.weather:
                Toast.makeText(getBaseContext(), "我不会告诉你右边还藏有东西哦", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onRefresh(NLPullRefreshView view) {
        Intent intent = new Intent().setAction(Constant.CREATE);
        intent.putExtra(Constant.IS_QUERY, Constant.OK);
        intent.putExtra(Constant.REQUEST_CITY, currentCity);
        startService(intent);
    }

    class Receiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getIntExtra(Constant.RESPONSE_STATUS, Constant.ERROR) == Constant.OK)
            {
                list = intent.getParcelableArrayListExtra(Constant.WEATHER_KEY);
                currentCity = intent.getStringExtra(Constant.REQUEST_CITY);
                showWeather();
                future.setVisibility(View.VISIBLE);
                future.setViewData(list);
                leftAdapter = new MyLeftAdapter(MainActivity.this, list);
                leftListView.setAdapter(leftAdapter);
            }
            else
                Toast.makeText(getBaseContext(), Constant.NO_FOUND, Toast.LENGTH_SHORT).show();
            pullRefreshView.finishRefresh();
        }
    }

    private void showWeather()
    {
        if(null == list)
            return;
        DailyWeather dailyWeather = list.get(currentPage);
        city.setText(currentCity+"("+dailyWeather.getWeekDescribe()+")");
        if(dailyWeather.getDayIconIndex() == Constant.ICON_INDEX_NULL)
        {
            highTemperature.setText(null);
        }
        else
        {
            highTemperature.setText(dailyWeather.getHighTemperature()+"°C");
        }
        lowTemperature.setText(dailyWeather.getLowTemperature()+"°C");
        weather.setText(dailyWeather.getWeatherDescribe()+"  "+dailyWeather.getWindDescribe());
        Task task1 = new Task(dayIcon);
        task1.execute(dailyWeather.getDayIconIndex());
        Task task2 = new Task(nightIcon);
        task2.execute(dailyWeather.getNightIconIndex());
        stringList.removeAll(stringList);
        String str;
        for(int i = 0 ; i < 9; i++)
        {
            str = dailyWeather.getValueWithIndex(i);
            if(str == null)
                str = Constant.NO_FOUND;
            stringList.add(str);
        }
        if(rightAdapter == null) {
            rightAdapter = new MyRightAdapter(MainActivity.this, stringList);
            rightListView.setAdapter(rightAdapter);
        }
        else
        {
           rightAdapter.notifyDataSetChanged();
        }
        rightHeaderView.setText(dailyWeather.getDateDescribe()+"生活指数");
    }

    class Task extends AsyncTask
    {

        private ImageView view;
        public Task(ImageView view)
        {
            this.view = view;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            view.setImageBitmap((Bitmap)o);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            int iconIndex = (Integer)params[0];
            if(iconIndex == Constant.ICON_INDEX_NULL)
                return null;
            Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), Constant.ids[iconIndex]);
            return bitmap;
        }
    }
}
