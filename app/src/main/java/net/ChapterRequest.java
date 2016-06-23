package net;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import java.util.regex.Pattern;

/**
 * Created by taozhiheng on 15-7-18.
 * useless
 */
public class ChapterRequest extends StringRequest{

    public ChapterRequest(int method, String url, Response.Listener<String> listener,
                          Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }
    public ChapterRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(Request.Method.GET, url, listener, errorListener);
    }
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        int i = 0;
        int length = 1024;
        int section = response.data.length/length;
        String str;
        String lastStr = "";
        StringBuilder stringBuilder = new StringBuilder();
        int start = -1;
        int end = -1;
        Pattern patternStart = Pattern.compile("<div class=\"indent\" id=\"dir_[0-9]*_full\" style=\"display:none\">");
        Pattern patternEnd = Pattern.compile("#dir_[0-9]*_full.*dir_[0-9]*_short.*</a>");
        while (i < section )
        {
            str = new String(response.data, length * i, length);
            Log.d("net", "eachLine:"+str);
            i++;
            boolean containStart = patternStart.matcher(lastStr+str).find();
            boolean containEnd = patternEnd.matcher(lastStr+str).find();
            if(containStart && containEnd) {
                start = (lastStr+str).indexOf("_full\" style=\"display:none\">")+28;
                end = (lastStr+str).indexOf("<a href=\"javascript:$");
                if(stringBuilder.length()>0)
                    stringBuilder.setLength(0);
                stringBuilder.append((lastStr+str).substring(start, end));
                Log.d("net","str change:"+stringBuilder.toString());
                Log.d("net", "start:" + start + "end:" + end);
                break;
            }
            else if(containStart) {
                start = (lastStr+str).indexOf("_full\" style=\"display:none\">")+28;
                if(stringBuilder.length()<=0)
                    stringBuilder.append((lastStr+str).substring(start));
                Log.d("net","str change:"+stringBuilder.toString());
                Log.d("net", "start:"+start);
            }
            else if(containEnd && start != -1)
            {
                end= (lastStr+str).indexOf("<a href=\"javascript:$");
                Log.d("net", "end:"+end);
                if(end >= lastStr.length()) {
                    stringBuilder.append(str.substring(0, end + 1 - lastStr.length()));
                    Log.d("net","str change:"+stringBuilder.toString());
                }
                break;
            }
            else if(start != -1)
            {
                stringBuilder.append(str);
                Log.d("net","str change:"+stringBuilder.toString());
            }
            lastStr = str;
        }
        String parsed = stringBuilder.toString();
        Log.d("net", "result:"+parsed);
        Log.d("net", "s:"+start+" e:"+end);

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
