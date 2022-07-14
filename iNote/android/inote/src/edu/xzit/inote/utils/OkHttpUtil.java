package edu.xzit.inote.utils;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;

public class OkHttpUtil {

	/**
	 * 普通get请求
	 * 
	 * @param url
	 * @param params
	 * @param stringCallback
	 */
	public static <T> void get(String url, Map<String, String> params,
			Callback<T> stringCallback) {
		OkHttpUtils.get().url(url).params(params).build()
				.execute(stringCallback);
	}

	/**
	 * 普通post请求
	 * 
	 * @param url
	 * @param params
	 * @param callback
	 */
	public static <T> void post(String url, Map<String, String> params,
			Callback<T> callback) {
		OkHttpUtils.post().url(url).params(params).build().execute(callback);
	}

	/**
	 * 
	 * @param url
	 * @param content
	 *            json 字符串
	 * @param callback
	 */
	public static <T> void postJsonString(String url, String content,
			Callback<T> callback) {
		OkHttpUtils.postString().url(url).content(content).build()
				.execute(callback);

	}

	/**
	 * 上传一个文件，支持单个多个文件，addFile的第一个参数为文件的key，
	 * <p>
	 * 即类别表单中<input type="file" name="mFile"/>的name属性。
	 * </P>
	 * 
	 * @param key
	 * @param fileName
	 * @param file
	 * @param url
	 * @param params
	 * @param headers
	 * @param callback
	 */
	public static <T> void postFile(String key, String fileName, File file,
			String url, Map<String, String> params,
			Map<String, String> headers, Callback<T> callback) {
		OkHttpUtils.post().addFile(key, fileName, file).url(url).params(params)
				.headers(headers).build().execute(callback);
	}

	/**
	 * 多文件上传，可以用
	 * 
	 * @param files
	 * @param url
	 * @param params
	 * @param headers
	 * @param callback
	 */
	public static <T> void postMultFiles(List<File> files, String url,
			Map<String, String> params, Map<String, String> headers,
			Callback<T> callback) {
		PostFormBuilder builder = OkHttpUtils.post();
		for (File file : files) {
			builder.addFile(file.getName(), file.getName(), file);
		}
		headers.put("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		builder.url(url).params(params).headers(headers).build()
				.execute(callback);
	}

	/**
	 * 文件下载
	 * 
	 * @param url
	 * @param fileCallBack
	 */
	public static void downloadFile(String url, FileCallBack fileCallBack) {
		OkHttpUtils.get().url(url).build().execute(fileCallBack);
	}

	/*
	 * new
	 * FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),
	 * "gson-2.2.1.jar")// {
	 * 
	 * @Override public void inProgress(float progress) {
	 * mProgressBar.setProgress((int) (100 * progress)); }
	 * 
	 * @Override public void onError(Request request, Exception e) { Log.e(TAG,
	 * "onError :" + e.getMessage()); }
	 * 
	 * @Override public void onResponse(File file) { Log.e(TAG, "onResponse :" +
	 * file.getAbsolutePath()); } }
	 */
}
