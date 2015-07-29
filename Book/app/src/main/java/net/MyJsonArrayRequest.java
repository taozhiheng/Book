package net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hustunique.myapplication.MyApplication;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by taozhiheng on 15-7-17.
 * Add accept, content-type, authorization to Header, and reset timeout to 10s,retry time to 3
 */
public class MyJsonArrayRequest extends JsonArrayRequest {

    public MyJsonArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        setRetryPolicy(
                new DefaultRetryPolicy(10*1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public MyJsonArrayRequest(int method, String url, JSONArray jsonRequest,
                           Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        setRetryPolicy(
                new DefaultRetryPolicy(10*1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        Log.d("web", "jsonRequest statusCode:" + response.statusCode + " CONTENT:" + response.data);

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
