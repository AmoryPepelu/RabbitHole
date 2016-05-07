package edu.xzit.inote.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyUtil {

	@Deprecated
	public void demo(Activity activity) {
		RequestQueue newRequestQueue = Volley.newRequestQueue(activity);
		StringRequest stringRequest = new StringRequest("http://www.baidu.com",
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("TAG", response);

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				});

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				"http://m.weather.com.cn/data/101010100.html", null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("TAG", response.toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				});

		newRequestQueue.add(stringRequest);
		newRequestQueue.add(jsonObjectRequest);
	}

	/**
	 * string request
	 * 
	 * @param activity
	 * @param url
	 * @param listener
	 * @param errorListener
	 */
	public static void getStringRequest(Context context, String url,
			Listener<String> listener, ErrorListener errorListener) {
		RequestQueue newRequestQueue = Volley.newRequestQueue(context);
		StringRequest stringRequest = new StringRequest(url, listener,
				errorListener);
		stringRequest
				.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		newRequestQueue.add(stringRequest);
	}

	/**
	 * jsonObject Request
	 * 
	 * @param activity
	 * @param url
	 * @param request
	 * @param listener
	 * @param errorListener
	 */
	public static void jsonObjectRequest(Context context, String url,
			JSONObject request, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		RequestQueue newRequestQueue = Volley.newRequestQueue(context);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
				request, listener, errorListener);
		jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
				1.0f));
		newRequestQueue.add(jsonObjectRequest);
	}

	/**
	 * post String Request
	 * 
	 * @param activity
	 * @param url
	 * @param listener
	 * @param errorListener
	 * @param params
	 */
	public static void postStringRequest(Context context, String url,
			Listener<String> listener, ErrorListener errorListener,
			final Map<String, String> params) {
		RequestQueue newRequestQueue = Volley.newRequestQueue(context);
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				// params.put("Content-Type",
				// "application/x-www-form-urlencoded");
				headers.put("Content-Type",
						"application/x-www-form-urlencoded; charset=UTF-8");
				return headers;
			}
		};
		stringRequest
				.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		newRequestQueue.add(stringRequest);
	}
}
