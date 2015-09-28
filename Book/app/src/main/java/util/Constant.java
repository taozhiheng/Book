package util;

import android.graphics.Color;

/**
 * Created by taozhiheng on 15-5-15.
 *
 */
public class Constant {


    /**
     * service
     * */
    public static final String KEY_CMD = "command";
    public static final int CMD_SYNC = 0;
    public static final int CMD_UPDATE = 1;
    public static final String KEY_CHOICE = "choice";
    public static final int CHOICE_LOCAL = 2;
    public static final int CHOICE_WEB = 3;

    //http://115.28.165.230:2333
    public final static String ip = "http://pokebook.whitepanda.org:2333";


    /**
     * 微博登录属性
     * */
    public static final String WB_APP_KEY = "2428164989";
    public static final String WB_REDIRECT_URL = "http://pokebook.whitepanda.org/oauth/2/weibo";
    public static final String WB_AUTH_URL = "https://api.weibo.com/oauth2/authorize" +
            "?client_id=" + WB_APP_KEY +
            "&redirect_uri=" + WB_REDIRECT_URL +
            "&response_type=code";
    public static final String WB_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    /**
     * 豆瓣登录属性
     * */
    public static final String DB_APP_KEY = "090ab21eb1bdc1df1b20165c1539459d";
    //
    public static final String DB_REDIRECT_URL = "http://pokebook.whitepanda.org/oauth/2/douban";
    public static final String DB_AUTH_URL = "https://www.douban.com/service/auth2/auth" +
            "?client_id=" + DB_APP_KEY +
            "&redirect_uri=" + DB_REDIRECT_URL +
            "&response_type=code";

    public static final String AUTH_CODE = "authorizationCode";


    //time format
    public final static String TIME_FORMAT = "yyyy-MM-dd.hh:mm:ss";

    //"pokebook.whitepanda.org"
    public final static String HOST_NAME = "";
    public final static int PORT = 2333;
    public final static String DB_ORIGIN_URL = "http://api.douban.com/book/subject/isbn/";
    public final static String DB_QUERY_URL = "https://api.douban.com/v2/book/search";
    public final static String DB_URL = "https://api.douban.com/v2/book/isbn/";

    public final static int COLOR = Color.rgb(0xe9, 0x1e, 0x63);

    public final static String PREF_NAME = "poke_book_config";
    public final static String PREF_USERNAME = "username";
    public final static String PREF_PASSWORD = "password";
    public final static String PREF_AUTH = "authorization";

    public final static String PREF_TIME = "lastTime";
    public final static String PREF_WORDS = "todayWords";
    public final static String PREF_WORDS2 = "tomorrowWords";
    public final static String PREF_WORDS3 = "theDayAfterTomorrowWords";


    public final static String KEY_OLD_MAIL = "oldMail";
    public final static String KEY_MAIL = "mail";

    public final static String PREF_FIRST_USE = "firstUse";

    public final static String USER_OR_PASSWORD_ILLEGAL = "用户名或密码太长或太短";
    public final static String REGISTER_SUCCEED = "注册成功";
    public final static String REGISTER_FAIL = "注册失败";
    public final static String EDIT_SUCCEED = "编辑成功";
    public final static String EDIT_FAIL = "编辑失败";

    //intent keys
    public final static String KEY_ACTION = "action";
    public final static String KEY_BOOK = "book";
    public final static String KEY_BOOKS = "books";
    public final static String KEY_CHAPTERS = "chapters";
    public final static String KEY_ICON_PATH = "iconPath";
    public final static String KEY_TITLE = "title";

    public final static String KEY_USER_ICON = "userIcon";
    public final static String KEY_USER_NAME = "userName";
    public final static String KEY_USER_MAIL = "userMail";
    public final static String KEY_USER_SEX = "userSex";
    public final static String KEY_USER_ICON_CHANGE = "userIconChange";

    //book operate
    public final static int STATE_ATON = 0;
    public final static int STATE_ATOB = 1;
    public final static int STATE_NTOA = 2;
    public final static int STATE_NTOB = 3;
    public final static int STATE_BTON = 4;

    //book classes
    public final static int INDEX_READ = 0;
    public final static int INDEX_AFTER = 1;
    public final static int INDEX_NOW = 2;
    public final static int INDEX_BEFORE = 3;

    //intent action
    public final static int IMAGE = 0;
    public static final int LOGIN = 1;
    public static final int PERSON = 2;
    public final static int ADD_BOOK = 3;
    public final static int ADD_CHAPTER = 4;
    public final static int VIEW_BOOK = 5;
    public final static int ACTION_CREATE_BOOK = 6;
    public final static int ACTION_SCAN_BOOK = 7;
    public final static int ACTION_EDIT_BOOK = 8;
    public final static int ACTION_EDIT = 9;
    public final static int WB_LOGIN = 10;
    public final static int DB_LOGIN = 11;


    public static int colors[]={
            Color.rgb(0xe9, 0x1e, 0x63),Color.rgb(0xf4,0x43,0x36),
            Color.rgb(0x7f,0xd6,0x8b),Color.rgb(0x21,0x96,0xf3),
            Color.rgb(0x86,0xa8,0xf8),Color.rgb(0xcb,0x8e,0xf6)};
    public static int REQUEST_CODE=1;
    public final static String ADDPLAN_ACTION="ADD_PLAN";
    public final static String POPULIST_ACTION="POP_LIST";
    public static final String ADDBOOK_ACTION="ADD_BOOK";
    public static final String DEL_BOOK="DEL_BOOK";
    public static final String EDIT_BOOK="EDIT_BOOK";
    private static final String[] months={"Jan.","Feb.","Mar.","Apr.","May.","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};
    public static String getMonth(int month){
        return months[month];
    }
    public static int colordiv=0x17;
    public static int colordive2=0x171717;

    //table name
    public final static String TABLE_BOOK = "book";
    public final static String TABLE_CHAPTER = "chapter";


    //data type
    public final static int TYPE_AFTER= 0;
    public final static int TYPE_NOW = 1;
    public final static int TYPE_BEFORE = 2;

    //data status
    public final static int STATUS_OK = 0;
    public final static int STATUS_ADD = 1;
    public final static int STATUS_MOD = 2;
    public final static int STATUS_DEL = 3;

    //type status
    public final static int T_STATUS_OK = -1;
    public final static int T_STATUS_AFTER = 0;
    public final static int T_STATUS_NOW = 1;
    public final static int T_STATUS_BEFORE = 2;


    //url
    public final static String URL_LOGIN = "/api/v1/user/login";
    public final static String URL_THIRD_PART_LOGIN = "/api/v1/user/login/oauth/2";
    public final static String URL_REGISTER = "/api/v1/user/register";
    public final static String URL_USER_INFO = "/api/v1/user";
    public final static String URL_USER_AVATAR = "/api/v1/user/avatar";
    public final static String URL_SAYING = "/api/v1/sayings/everyday";

    public final static String URL_UBOOK = ip+"/api/v1/user/books";
    public final static String URL_BOOK = ip+"/api/v1/books";
    public final static String URL_BOOKS_COUNT = ip+"/api/v1/user/books/count";

}
