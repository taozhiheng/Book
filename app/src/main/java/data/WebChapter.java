package data;

/**
 * Created by taozhiheng on 15-7-29.
 */
public class WebChapter {

    private long id;
    private String webBookId;
    private int webId;
    private int type;

    public WebChapter(long id, String webBookId, int webId, int type) {
        this.id = id;
        this.webBookId = webBookId;
        this.webId = webId;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWebBookId() {
        return webBookId;
    }

    public void setWebBookId(String webBookId) {
        this.webBookId = webBookId;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
