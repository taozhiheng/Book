package net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hustunique.myapplication.LoginActivity;
import com.hustunique.myapplication.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by taozhiheng on 15-7-17.
 * Add accept, content-type, authorization to Header, and reset timeout to 10s
 */
public class MyJsonObjectRequest extends JsonObjectRequest {

    public MyJsonObjectRequest(int method, String url, JSONObject jsonRequest,
                             Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        setRetryPolicy(
                new DefaultRetryPolicy(10*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Authorization", MyApplication.getAuthorization());
        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        Log.d("web","jsonRequest statusCode:"+response.statusCode);

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            if(response.statusCode == 200) {
                String json = new String(response.data);
                try {
                    return Response.success(new JSONObject(json), HttpHeaderParser.parseCacheHeaders(response));
                }catch (JSONException E)
                {
                    return Response.success(new JSONObject(), HttpHeaderParser.parseCacheHeaders(response));

                }
            }
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.success(new JSONObject(), HttpHeaderParser.parseCacheHeaders(response));
        }
    }

}
