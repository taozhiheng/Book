package data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taozhiheng on 15-9-20.
 */
public class TimeInfo implements Parcelable {

    private String startTime;
    private String endTime;

    public TimeInfo()
    {

    }

    public TimeInfo(String startTime, String endTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
    }

    protected TimeInfo(Parcel in) {
        this.startTime = in.readString();
        this.endTime = in.readString();
    }

    public static final Parcelable.Creator<TimeInfo> CREATOR = new Parcelable.Creator<TimeInfo>() {
        public TimeInfo createFromParcel(Parcel source) {
            return new TimeInfo(source);
        }

        public TimeInfo[] newArray(int size) {
            return new TimeInfo[size];
        }
    };
}
