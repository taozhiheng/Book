package data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taozhiheng on 15-7-5.
 * Chapter data
 */
public class Chapter implements Parcelable{



    private int mId;
    private String mBookId;
    private String mName;

    private int mType;
    private String mCreateTime;
    private int mStatus;

    private String mBookName;
    private String mUrl;

    public Chapter()
    {

    }


    public Chapter(int id, String bookId, String name,
                   int type, String createTime, int status)
    {
        this(id, bookId, name, type, createTime, status, null, null);
    }

    public Chapter(int id, String bookId, String name,
                   int type, String createTime, int status,
                   String bookName, String url)
    {
        this.mId = id;
        this.mBookId = bookId;
        this.mName = name;

        this.mType = type;
        this.mCreateTime = createTime;
        this.mStatus = status;

        this.mBookName = bookName;
        this.mUrl = url;
    }

    public void setId(int id)
    {
        this.mId = id;
    }

    public void setBookId(String bookId)
    {
        this.mBookId = bookId;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getBookId() {
        return mBookId;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String getCreateTime() {
        return mCreateTime;
    }


    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        this.mBookName = bookName;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mBookId);
        dest.writeString(this.mName);
        dest.writeInt(this.mType);
        dest.writeString(this.mCreateTime);
        dest.writeInt(this.mStatus);
        dest.writeString(this.mBookName);
        dest.writeString(this.mUrl);

    }

    protected Chapter(Parcel in) {
        this.mId = in.readInt();
        this.mBookId = in.readString();
        this.mName = in.readString();
        this.mType = in.readInt();
        this.mCreateTime = in.readString();
        this.mStatus = in.readInt();
        this.mBookName = in.readString();
        this.mUrl = in.readString();
    }

    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        public Chapter createFromParcel(Parcel source) {
            return new Chapter(source);
        }

        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
}
