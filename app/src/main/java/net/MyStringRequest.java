package net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.hustunique.myapplication.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by taozhiheng on 15-7-17.
 * Add accept, content-type, authorization to Header, and reset timeout to 10s
 */
public class MyStringRequest extends StringRequest{

    public MyStringRequest(int method, String url, Response.Listener<String> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setRetryPolicy(
                new DefaultRetryPolicy(10*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Log.d("web", "stringRequest statusCode:" + response.statusCode + " CONTENT:" + response.data);

        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Authorization", MyApplication.getAuthorization());
        return headers;
    }
}
