package data;

/**
 * Created by taozhiheng on 15-7-29.
 */
public class WebBook {

    private long id;
    private String uuid;
    private int type;
    private String start;
    private String end;

    public WebBook(long id, String uuid, int type) {
        this.id = id;
        this.uuid = uuid;
        this.type = type;
    }

    public WebBook(long id, String uuid, int type, String start, String end) {
        this.id = id;
        this.uuid = uuid;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
