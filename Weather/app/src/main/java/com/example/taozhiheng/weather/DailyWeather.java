package com.example.taozhiheng.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taozhiheng on 15-1-7.
 * 记录一天天气信息的类
 */
public class DailyWeather implements Parcelable{
    private int dayIconIndex;          //白天天气图片索引
    private int nightIconIndex;        //夜晚天气图片索引
    private int highTemperature;       //白天温度
    private int lowTemperature;        //夜晚温度
    private String weekDescribe;       //周描述
    private String dateDescribe;       //日期描述
    private String weatherDescribe;    //天气描述
    private String windDescribe;       //风力描述
    private String glass;              //太阳镜指数
    private String clothes;            //穿衣指数
    private String trip;               //旅游指数
    private String sport;              //运动指数
    private String car;                //洗车指数
    private String makeup;             //化妆指数
    private String cold;               //感冒指数
    private String light;              //紫外线指数
    private String soft;               //舒适指数

    public DailyWeather()
    {
        this.dayIconIndex = Constant.ICON_INDEX_NULL;
        this.highTemperature = Constant.HIGH_TEMPERATURE_NULL;
    }
    public void setDayIcon(String icon)
    {
        if(icon.length() == 9)
        {
            this.dayIconIndex = Constant.stringToInt(icon.substring(7, 9));
        }

    }
    public void setNightIcon(String icon)
    {
        this.nightIconIndex = Constant.stringToInt(icon.substring(7, 9))+35;
    }
    public void setHighTemperature(int temperature)
    {
        this.highTemperature = temperature;
    }
    public void setLowTemperature(int temperature)
    {
        this.lowTemperature = temperature;
    }
    public void setWeekDescribe(String describe)
    {
        this.weekDescribe = describe;
    }
    public void setDateDescribe(String describe)
    {
        this.dateDescribe = describe;
    }
    public void setWeatherDescribe(String describe)
    {
        this.weatherDescribe = describe;
    }
    public void setWindDescribe(String describe)
    {
        this.windDescribe = describe;
    }
    public void setGlass(String glass)
    {
        this.glass = glass.replace("</b>", ",").replace("<b>", "");
    }
    public void setClothes(String clothes)
    {
        this.clothes = clothes.replace("</b>", ",").replace("<b>", "");
    }
    public void setTrip(String trip)
    {
        this.trip = trip.replace("</b>", ",").replace("<b>", "");
    }
    public void setSport(String sport)
    {
        this.sport = sport.replace("</b>", ",").replace("<b>", "");
    }
    public void setCar(String car)
    {
        this.car = car.replace("</b>", ",").replace("<b>", "");
    }
    public void setMakeup(String makeup)
    {
        this.makeup = makeup.replace("</b>", ",").replace("<b>", "");
    }
    public void setCold(String cold)
    {
        this.cold = cold.replace("</b>", ",").replace("<b>", "");
    }
    public void setLight(String light)
    {
        this.light = light.replace("</b>", ",").replace("<b>", "");
    }
    public void setSoft(String soft)
    {
        this.soft = soft.replace("</b>", ",").replace("<b>", "");
    }
    public int getDayIconIndex()
    {
        return dayIconIndex;
    }
    public int getNightIconIndex()
    {
        return nightIconIndex;
    }
    public int getHighTemperature()
    {
        return highTemperature;
    }
    public int getLowTemperature()
    {
        return lowTemperature;
    }
    public String getWeekDescribe()
    {
        return weekDescribe;
    }
    public String getDateDescribe()
    {
        return dateDescribe;
    }
    public String getWeatherDescribe()
    {
        return weatherDescribe;
    }
    public String getWindDescribe()
    {
        return windDescribe;
    }
    public String getGlass()
    {
        return glass;
    }
    public String getClothes()
    {
        return clothes;
    }
    public String getTrip()
    {
        return trip;
    }
    public String getSport()
    {
        return sport;
    }
    public String getCar()
    {
        return car;
    }
    public String getMakeup()
    {
        return makeup;
    }
    public String getCold()
    {
        return cold;
    }
    public String getLight()
    {
        return light;
    }
    public String getSoft()
    {
        return soft;
    }

    public void setValueWithIndex(int index, String value)
    {
        switch (index)
        {
            case 0:
                setGlass(value);
                break;
            case 1:
                setClothes(value);
                break;
            case 2:
                setTrip(value);
                break;
            case 3:
                setSport(value);
                break;
            case 4:
                setCar(value);
                break;
            case 5:
                setMakeup(value);
                break;
            case 6:
                setCold(value);
                break;
            case 7:
                setLight(value);
                break;
            case 8:
                setSoft(value);
                break;
        }
    }
    public String getValueWithIndex(int index)
    {
        switch (index)
        {
            case 0:
                return getGlass();
            case 1:
                return getClothes();
            case 2:
                return getTrip();
            case 3:
                return getSport();
            case 4:
                return getCar();
            case 5:
                return getMakeup();
            case 6:
                return getCold();
            case 7:
                return getLight();
            case 8:
                return getSoft();
        }
        return null;
    }

    public static final Parcelable.Creator<DailyWeather> CREATOR = new Creator<DailyWeather>() {
        @Override
        public DailyWeather createFromParcel(Parcel source) {
            DailyWeather weather = new DailyWeather();
            weather.dayIconIndex = source.readInt();
            weather.nightIconIndex = source.readInt();
            weather.highTemperature = source.readInt();
            weather.lowTemperature = source.readInt();
            weather.weekDescribe = source.readString();
            weather.dateDescribe = source.readString();
            weather.weatherDescribe = source.readString();
            weather.windDescribe = source.readString();
            weather.glass = source.readString();
            weather.clothes = source.readString();
            weather.trip = source.readString();
            weather.sport = source.readString();
            weather.car = source.readString();
            weather.makeup = source.readString();
            weather.cold = source.readString();
            weather.light = source.readString();
            weather.soft = source.readString();
            return weather;
        }
        @Override
        public DailyWeather[] newArray(int size) {
            return new DailyWeather[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dayIconIndex);
        dest.writeInt(nightIconIndex);
        dest.writeInt(highTemperature);
        dest.writeInt(lowTemperature);
        dest.writeString(weekDescribe);
        dest.writeString(dateDescribe);
        dest.writeString(weatherDescribe);
        dest.writeString(windDescribe);
        dest.writeString(glass);
        dest.writeString(clothes);
        dest.writeString(trip);
        dest.writeString(sport);
        dest.writeString(car);
        dest.writeString(makeup);
        dest.writeString(cold);
        dest.writeString(light);
        dest.writeString(soft);
    }
}
