package edu.xzit.inote.app;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;
import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.zhy.http.okhttp.callback.Callback;

import edu.xzit.inote.R;
import edu.xzit.inote.support.imagepicker.imageloader.ImagePickerActivity;
import edu.xzit.inote.ui.account.impl.LoginActivity;
import edu.xzit.inote.ui.account.impl.RegisterActivity;
import edu.xzit.inote.ui.activity.MessageDetailActivity;
import edu.xzit.inote.ui.activity.PersonalPageActivity;
import edu.xzit.inote.ui.activity.PostActivity;
import edu.xzit.inote.ui.activity.RootActivity;
import edu.xzit.inote.utils.OkHttpUtil;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class TestActivity extends Activity {

	private final String TAG = "TestActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SpUtil.setStringValue(this, "userName", "pepelu");
		setContentView(R.layout.activity_test);
	}

	public void onClickHandler(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.id_btn_root_activity:
			startRootActivity();
			break;
		case R.id.id_btn_login_activity:
			startLoginActivity();
			break;
		case R.id.id_btn_post_activity:
			startPostActivity();
			break;
		case R.id.id_btn_image_picker_activity:
			startImagePickerActivity();
			break;
		case R.id.id_btn_test_okhttp:
			testOkHttp();
			break;
		case R.id.id_btn_regist_activity:
			testRegist();
			break;
		case R.id.id_btn_test_pull_to_refresh:
			testPullToRefresh();
			break;
		case R.id.id_btn_md_activity:
			startActivityMessageDetail();
			break;
		case R.id.id_btn_psersonal_page_activity:
			startPersonalPageActivity();
			break;
		case R.id.id_btn_test_multimage_activity:
			startTestMultImages();
			break;
		case R.id.id_btn_test_net:
			testNetConnect();
			break;
		default:
			break;
		}
	}

	/**
	 * 测试网络连接
	 */
	private void testNetConnect() {
		// http://127.0.0.1:8080/inote/GG
		String url = SysConfig.URL + "GG";
		Log.d(TAG, "testNetConnect url=" + url);
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				Log.d(TAG, "testNetConnect=" + string);
				ToastUtil.showMessage(TestActivity.this, string);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError e) {
				Log.e(TAG, "testNetConnect=" + e.getMessage());
				ToastUtil.showMessage(TestActivity.this, e.getMessage());
			}
		});
	}

	private void startTestMultImages() {
		Intent intent = new Intent(this, TestMultImagesActivity.class);
		startActivity(intent);
	}

	private void startActivityMessageDetail() {
		SpUtil.setStringValue(this, "userName", "pepelu");

		Intent intent = new Intent(this, MessageDetailActivity.class);
		Bundle bundle = new Bundle();
		ArrayList<String> list = new ArrayList<String>();
		list.add("https://www.baidu.com/img/bd_logo1.png");
		list.add("https://img3.doubanio.com/view/photo/albumcover/public/p2321519111.jpg");
		list.add("https://img3.doubanio.com/view/photo/albumcover/public/p2293692141.jpg");

		bundle.putStringArrayList("pictures", list);
		bundle.putString("messageId", "1");
		bundle.putString("messageUserName", "pepelu");
		bundle.putString("messageUserHeadeUrl",
				"https://img3.doubanio.com/view/photo/albumcover/public/p2293692141.jpg");
		bundle.putString("messageUserNickName", "tony");
		bundle.putString("messageDate", "2016-3-1 21:00:00");
		bundle.putString("messageContent",
				"低档的鳕鱼，价格只有30——40元一公斤，这个叫法就复杂了：有水鳕鱼、鳕鱼片、阿拉斯加鳕鱼、基地鳕鱼等等。");

		intent.putExtra("data", bundle);

		startActivity(intent);
	}

	private void testPullToRefresh() {
		Intent intent = new Intent(this, TestPullToRefreshActivity.class);
		startActivity(intent);
	}

	private void testRegist() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);

	}

	private void testOkHttp() {

		String url = "http://" + SysConfig.IP + ":8080/inote/FF";
		// VolleyUtil.getStringRequest(this, url, new Listener<String>() {
		//
		// @Override
		// public void onResponse(String string) {
		// Log.d("pepelu", "res=" + string);
		// }
		// }, new ErrorListener() {
		//
		// @Override
		// public void onErrorResponse(VolleyError arg0) {
		//
		// }
		//
		// }
		//
		// );

		// Map<String, String> params=new HashMap<String, String>();
		// params.put("username", "王二");
		//
		// VolleyUtil.postStringRequest(this, url, new Listener<String>() {
		//
		// @Override
		// public void onResponse(String string) {
		//
		// }
		// }, new ErrorListener(){
		//
		// @Override
		// public void onErrorResponse(VolleyError error) {
		//
		// }
		//
		// }, params);

	}

	private void testFileUpload(String url) {

		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + "aaa.png";
		File file = new File(filePath);
		Log.d("pepelu", "file exit?" + file.exists());
		OkHttpUtil.postFile("file", "yui.jpg", file, url, null, null,
				new Callback<String>() {

					@Override
					public void onError(Call arg0, Exception arg1) {
						Log.d("pepelu", "on error :" + arg1.getMessage());
					}

					@Override
					public void onResponse(String arg0) {

					}

					@Override
					public String parseNetworkResponse(Response arg0)
							throws Exception {
						return null;
					}
				});
	}

	private void startImagePickerActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ImagePickerActivity.class);
		startActivity(intent);
	}

	private void startPostActivity() {
		Intent intent = new Intent();
		intent.setClass(this, PostActivity.class);
		startActivity(intent);
	}

	private void startRootActivity() {
		Intent intent = new Intent();
		intent.setClass(this, RootActivity.class);
		startActivity(intent);
	}

	private void startLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
	}

	private void startPersonalPageActivity() {
		Intent intent = new Intent(this, PersonalPageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userName", "aa");
		bundle.putString("nikeName", "aa");
		bundle.putString("userPic", "5867ec17e6864e75b511d969f4d9ea72.jpg");
		bundle.putString("follow", "1");
		intent.putExtra("data", bundle);
		startActivity(intent);
	}
}
