package com.example.taozhiheng.weather;

import android.content.Context;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by taozhiheng on 15-1-5.
 * 共用常量，共用方法
 */
public class Constant {
    public final static int ICON_INDEX_NULL = -1;
    public final static int HIGH_TEMPERATURE_NULL = 222;
    public final static int ERROR = 0;
    public final static int OK = 1;
    public final static String REQUEST_CITY = "cityName";
    public final static String RESPONSE_STATUS = "responseStatus";
    public final static String IS_QUERY = "isQuery";
    public final static String WEATHER_KEY = "weatherInfo";
    public final static String PREF_KEY = "defaultCity";
    public final static String CREATE = "Weather.intent.action.create";
    public final static String RESPONSE = "Weather.intent.action.response";
    public final static String HTTP_HEAD = "http://www.weather.com.cn/weather/";
    public final static String NO_FOUND = "Sorry,the city you search for is not existed or your network is closed!";
    public final static int[] ids = {
            R.drawable.d0,  R.drawable.d1,  R.drawable.d2,  R.drawable.d3,  R.drawable.d4,  R.drawable.d5,
            R.drawable.d6,  R.drawable.d7,  R.drawable.d8,  R.drawable.d9,  R.drawable.d10, R.drawable.d11,
            R.drawable.d12, R.drawable.d13, R.drawable.d14, R.drawable.d15, R.drawable.d16, R.drawable.d17,
            R.drawable.d18, R.drawable.d19, R.drawable.d20, R.drawable.d21, R.drawable.d22, R.drawable.d23,
            R.drawable.d24, R.drawable.d25, R.drawable.d26, R.drawable.d27, R.drawable.d28, R.drawable.d29,
            R.drawable.d30, R.drawable.d31, R.drawable.d32, R.drawable.d33, R.drawable.d34,
            R.drawable.n0,  R.drawable.n1,  R.drawable.n2,  R.drawable.n3,  R.drawable.n4,  R.drawable.n5,
            R.drawable.n6,  R.drawable.n7,  R.drawable.n8,  R.drawable.n9,  R.drawable.n10, R.drawable.n11,
            R.drawable.n12, R.drawable.n13, R.drawable.n14, R.drawable.n15, R.drawable.n16, R.drawable.n17,
            R.drawable.n18, R.drawable.n19, R.drawable.n20, R.drawable.n21, R.drawable.n22, R.drawable.n23,
            R.drawable.n24, R.drawable.n25, R.drawable.n26, R.drawable.n27, R.drawable.n28, R.drawable.n29,
            R.drawable.n30, R.drawable.n31, R.drawable.n32, R.drawable.n33, R.drawable.n34};
    //通过图片名字符串取得索引
    public static int stringToInt(String str)
    {
        int i = (str.charAt(0)-'0')*10+str.charAt(1)-'0';
        if(i>34)
            i = 34;
        return i;
    }
    //通过城市名称取得城市代码
    public static String getCityCode(Context context,String city)
    {
        String code = NO_FOUND;
        try
        {
            InputStream is = context.getResources().getAssets().open("cityCode.xml");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
               if(eventType == XmlPullParser.START_TAG)
               {
                   if (parser.getName().equals("county")&&parser.getAttributeValue(null, "name").equals(city))
                   {
                       Log.i("cityName", parser.getAttributeValue(null, "name")+parser.getAttributeValue(null, "weatherCode"));
                       code = parser.getAttributeValue(null, "weatherCode");
                       break;
                   }
               }
                eventType = parser.next();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
            code = NO_FOUND;
        }catch (XmlPullParserException E)
        {
            E.printStackTrace();
            code = NO_FOUND;
        }
        return code;
    }

    //通过url取得天气信息
    public static ArrayList<DailyWeather> getWeatherInfo(String urlString) throws Exception
    {
        Log.i("Myurl", urlString);
        ArrayList<DailyWeather> weatherList = new ArrayList<DailyWeather>();
        DailyWeather currentWeather;
        Document document = Jsoup.connect(urlString).get();
        org.jsoup.nodes.Element mainWholeElement = document.getElementById("7d");
        org.jsoup.nodes.Element appendWholeElement = document.getElementById("zs");
        Elements mainElements = mainWholeElement.getElementsByTag("li");
        Elements sets;
        org.jsoup.nodes.Element eachElement;
        //获取７天基本信息
        for (int i = 0; i<7; i++)
        {
            eachElement = mainElements.get(i);
            currentWeather = new DailyWeather();
            //获取星期描述
            sets = eachElement.getElementsByTag("h1");
            currentWeather.setWeekDescribe(sets.get(0).text());
            //获取日期描述
            sets = eachElement.getElementsByTag("h2");
            currentWeather.setDateDescribe(sets.get(0).text());
            //获取天气描述
            sets = eachElement.getElementsByClass("wea");
            currentWeather.setWeatherDescribe(sets.get(0).text());
            //获取天气图片
            sets = eachElement.getElementsByTag("big");
            currentWeather.setDayIcon(sets.get(0).className());
            currentWeather.setNightIcon(sets.get(1).className());
            //获取温度
            sets = eachElement.getElementsByTag("span");
            if(!sets.get(0).text().equals(""))
            {
                currentWeather.setHighTemperature(Integer.valueOf(sets.get(0).text()));
            }
            currentWeather.setLowTemperature(Integer.valueOf(sets.get(1).text()));
            //获取风力
            sets = eachElement.getElementsByTag("i");
            currentWeather.setWindDescribe(sets.get(2).text());
            //获取九种指数
//            sets = appendWholeElement.getElementsByAttributeValue("data-dn", "7d"+(i+1));
//            for(int index = 0; index< sets.size(); index++)
//                currentWeather.setValueWithIndex(index, sets.get(index).html());
            weatherList.add(currentWeather);
        }
        //获取9种指数信息
        mainElements = appendWholeElement.getElementsByTag("li");
        for(int i = 0; i<9;i++ )
        {
            eachElement = mainElements.get(i);
            sets = eachElement.getElementsByTag("aside");
            for(int index = 0; index < sets.size(); index++)
            {
                weatherList.get(index).setValueWithIndex(i, sets.get(index).html());
            }

        }
        return weatherList;
    }
}
