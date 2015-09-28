package data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taozhiheng on 15-7-5.
 * Chapter data
 */
public class Chapter implements Parcelable{



    private long mId;
    private long mBookId;
    private int mWebId;
    private String mWebBookId;
    private String mName;

    private int mType;
    private String mCreateTime;
    private int mStatus;
    private int mTypeStatus;

    private String mBookName;
    private String mUrl;

    public Chapter()
    {

    }


    public Chapter(long id, long bookId, int webId, String webBookId, String name,
                   int type, String createTime, int status, int typeStatus)
    {
        this(id, bookId, webId, webBookId, name, type, createTime, status, typeStatus, null, null);
    }

    public Chapter(long id, long bookId, int webId, String webBookId, String name,
                   int type, String createTime, int status, int typeStatus,
                   String bookName, String url)
    {
        this.mId = id;
        this.mBookId = bookId;
        this.mWebId = webId;
        this.mWebBookId = webBookId;
        this.mName = name;

        this.mType = type;
        this.mCreateTime = createTime;
        this.mStatus = status;
        this.mTypeStatus = typeStatus;

        this.mBookName = bookName;
        this.mUrl = url;
    }

    public void setId(long id)
    {
        this.mId = id;
    }

    public void setBookId(long bookId)
    {
        this.mBookId = bookId;
    }

    public void setTypeStatus(int status)
    {
        this.mTypeStatus = status;
    }

    public long getId()
    {
        return mId;
    }

    public long getBookId()
    {
        return mBookId;
    }

    public int getTypeStatus()
    {
        return mTypeStatus;
    }


    public void setWebId(int id)
    {
        this.mWebId = id;
    }

    public void setWebBookId(String bookId)
    {
        this.mWebBookId = bookId;
    }

    public int getWebId() {
        return mWebId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getWebBookId() {
        return mWebBookId;
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
        dest.writeLong(this.mId);
        dest.writeLong(this.mBookId);
        dest.writeInt(this.mWebId);
        dest.writeString(this.mWebBookId);
        dest.writeString(this.mName);
        dest.writeInt(this.mType);
        dest.writeString(this.mCreateTime);
        dest.writeInt(this.mStatus);
        dest.writeInt(this.mTypeStatus);
        dest.writeString(this.mBookName);
        dest.writeString(this.mUrl);

    }

    protected Chapter(Parcel in) {
        this.mId = in.readLong();
        this.mBookId = in.readLong();
        this.mWebId = in.readInt();
        this.mWebBookId = in.readString();
        this.mName = in.readString();
        this.mType = in.readInt();
        this.mCreateTime = in.readString();
        this.mStatus = in.readInt();
        this.mTypeStatus = in.readInt();
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
