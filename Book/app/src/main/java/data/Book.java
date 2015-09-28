package data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taozhiheng on 15-7-5.
 * 书籍类型
 */
public class Book implements Parcelable {

    private long mId;
    private String mUUID;
    private String mIsbn;
    private String mName;
    private String mAuthor;
    private String mPress;
    private String mUrl;
    private int mColor;
    private int mFinishNum;
    private int mChapterNum;
    private long mWordNum;
    private int mType;
    private String mStartTime;
    private String mEndTime;
    private String mCreateTime;
    private int mStatus;
    private int mTypeStatus;

    public Book()
    {
        this.mId = -1;
    }

    public Book(long id, String uuid, String isbn, String name, String author, String  press,
                String url, int color, int finishNum, int chapterNum,
                long wordNum, int type, String create,int status, int typeStatus)
    {
        this.mId = id;
        this.mUUID = uuid;
        this.mIsbn = isbn;
        this.mName = name;
        this.mAuthor = author;
        this.mPress = press;
        this.mUrl = url;
        this.mColor = color;
        this.mFinishNum = finishNum;
        this.mChapterNum = chapterNum;
        this.mWordNum = wordNum;
        this.mType = type;
        this.mCreateTime = create;
        this.mStatus = status;
        this.mTypeStatus = typeStatus;
    }

    public Book(long id, String uuid, String isbn, String name, String author, String  press,
                String url, int color, int finishNum, int chapterNum,
                long wordNum, int type, String create, int status, int typeStatus,
                String start, String end)
    {
        this.mId = id;
        this.mUUID = uuid;
        this.mIsbn = isbn;
        this.mName = name;
        this.mAuthor = author;
        this.mPress = press;
        this.mUrl = url;
        this.mColor = color;
        this.mFinishNum = finishNum;
        this.mChapterNum = chapterNum;
        this.mWordNum = wordNum;
        this.mType = type;
        this.mStartTime = start;
        this.mEndTime = end;
        this.mCreateTime = create;
        this.mStatus = status;
        this.mTypeStatus = typeStatus;
    }

    public void setId(long id)
    {
        this.mId = id;
    }

    public void setTYpeStatus(int tYpeStatus)
    {
        this.mTypeStatus = tYpeStatus;
    }

    public void setUUID(String uuid)
    {
        this.mUUID = uuid;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public void setIsbn(String isbn){
        this.mIsbn = isbn;
    }

    public void setAuthor(String author)
    {
        this.mAuthor = author;
    }

    public void setPress(String press)
    {
        this.mPress = press;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public void setChapterNum(int chapterNum)
    {
        this.mChapterNum = chapterNum;
    }

    public void setFinishNum(int finishNum)
    {
        this.mFinishNum = finishNum;
    }

    public void setWordNum(long wordNum) {
        this.mWordNum = wordNum;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
    }

    public void setCreateTime(String createTime)
    {
        this.mCreateTime = createTime;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }


    //getter
    public long getId()
    {
        return mId;
    }

    public int getTypeStatus()
    {
        return mTypeStatus;
    }

    public String getUUID()
    {
        return mUUID;
    }

    public String getName() {
        return mName;
    }

    public String getIsbn()
    {
        return mIsbn;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getPress() {
        return mPress;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getColor() {
        return mColor;
    }

    public int getChapterNum() {
        return mChapterNum;
    }

    public int getFinishNum()
    {
        return mFinishNum;
    }

    public long getWordNum() {
        return mWordNum;
    }

    public int getType() {
        return mType;
    }

    public String getStartTime() {
        return mStartTime;
    }



    public String getEndTime() {
        return mEndTime;
    }



    public String getCreateTime() {
        return mCreateTime;
    }


    public int getStatus() {
        return mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mUUID);
        dest.writeString(this.mName);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mPress);
        dest.writeString(this.mUrl);
        dest.writeInt(this.mColor);
        dest.writeInt(this.mFinishNum);
        dest.writeInt(this.mChapterNum);
        dest.writeLong(this.mWordNum);
        dest.writeInt(this.mType);
        dest.writeString(this.mStartTime);
        dest.writeString(this.mEndTime);
        dest.writeString(this.mCreateTime);
        dest.writeInt(this.mStatus);
        dest.writeInt(this.mTypeStatus);
    }

    protected Book(Parcel in) {
        this.mId = in.readLong();
        this.mUUID = in.readString();
        this.mName = in.readString();
        this.mAuthor = in.readString();
        this.mPress = in.readString();
        this.mUrl = in.readString();
        this.mColor = in.readInt();
        this.mFinishNum = in.readInt();
        this.mChapterNum = in.readInt();
        this.mWordNum = in.readLong();
        this.mType = in.readInt();
        this.mStartTime = in.readString();
        this.mEndTime = in.readString();
        this.mCreateTime = in.readString();
        this.mStatus = in.readInt();
        this.mTypeStatus = in.readInt();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
