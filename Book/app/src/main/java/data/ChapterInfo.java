package data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * old version chapter data class
 */
public class ChapterInfo implements Parcelable {

    private int mPosition;
    private String mName;
    private int mType;

    public ChapterInfo(int mPosition, String mName) {
        this(mPosition, mName, -1);
    }

    public ChapterInfo(int position, String name, int type)
    {
        this.mPosition = position;
        this.mName = name;
        this.mType = type;
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

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPosition);
        dest.writeString(this.mName);
        dest.writeInt(this.mType);
    }

    protected ChapterInfo(Parcel in) {
        this.mPosition = in.readInt();
        this.mName = in.readString();
        this.mType = in.readInt();
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
