package data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * old version chapter data class
 */
public class ChapterInfo implements Parcelable {

    private int mPosition;
    private String mName;

    public ChapterInfo(int mPosition, String mName) {
        this.mPosition = mPosition;
        this.mName = mName;
    }



    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPosition);
        dest.writeString(this.mName);
    }

    protected ChapterInfo(Parcel in) {
        this.mPosition = in.readInt();
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<ChapterInfo> CREATOR = new Parcelable.Creator<ChapterInfo>() {
        public ChapterInfo createFromParcel(Parcel source) {
            return new ChapterInfo(source);
        }

        public ChapterInfo[] newArray(int size) {
            return new ChapterInfo[size];
        }
    };
}
