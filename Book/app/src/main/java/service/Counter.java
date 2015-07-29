package service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taozhiheng on 15-7-27.
 *
 */
public class Counter implements Parcelable {

    int mCount;
    int mBookFinishNum;
    int mBookNum;
    int mChapterNum;
    public Counter(int count)
    {
        if(count < 0)
            count = 0;
        this.mCount = count;
    }

    public int getCount() {
        return mCount;
    }

    public int getBookFinishNum()
    {
        return mBookFinishNum;
    }

    public int getBookNum()
    {
        return mBookNum;
    }

    public int getChapterNum()
    {
        return mChapterNum;
    }

    public void setCount(int mCount) {
        if(mCount >= 0)
            this.mCount = mCount;
    }

    public boolean hasCount()
    {
        return mCount != 0;
    }

    public void increase()
    {
        mCount++;
    }

    public void decrease()
    {
        if(mCount != 0)
            mCount--;
    }

    public void addBookNum(int num)
    {
        this.mBookNum += num;
    }

    public void addBookFinishNum(int num)
    {
        this.mBookFinishNum += num;
    }

    public void addChapterNum(int num)
    {
        this.mChapterNum += num;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCount);
        dest.writeInt(this.mBookFinishNum);
        dest.writeInt(this.mBookNum);
        dest.writeInt(this.mChapterNum);
    }

    protected Counter(Parcel in) {
        this.mCount = in.readInt();
        this.mBookFinishNum = in.readInt();
        this.mBookNum = in.readInt();
        this.mChapterNum = in.readInt();
    }

    public static final Parcelable.Creator<Counter> CREATOR = new Parcelable.Creator<Counter>() {
        public Counter createFromParcel(Parcel source) {
            return new Counter(source);
        }

        public Counter[] newArray(int size) {
            return new Counter[size];
        }
    };
}
