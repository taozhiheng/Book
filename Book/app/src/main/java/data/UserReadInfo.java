package data;

/**
 * Created by taozhiheng on 15-7-23.
 * the information about user reading
 */
public class UserReadInfo {

    private int bookNum;
    private int chapterNum;
    private long wordNum;
    private int dayNum;

    public UserReadInfo()
    {
        this(0, 0, 0, 0);
    }

    public UserReadInfo(int bookNum, int chapterNum, long wordNum, int dayNum) {
        this.bookNum = bookNum;
        this.chapterNum = chapterNum;
        this.wordNum = wordNum;
        this.dayNum = dayNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public void setWordNum(long wordNum) {
        this.wordNum = wordNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public int getBookNum() {
        return bookNum;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public long getWordNum() {
        return wordNum;
    }

    public int getDayNum() {
        return dayNum;
    }
}
