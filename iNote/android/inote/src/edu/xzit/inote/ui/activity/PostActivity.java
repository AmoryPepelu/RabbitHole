package edu.xzit.inote.ui.activity;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.zhy.http.okhttp.callback.Callback;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.entity.SelectedImages;
import edu.xzit.inote.support.imagepicker.imageloader.ImagePickerActivity;
import edu.xzit.inote.ui.activity.adapter.GridViewAdapter;
import edu.xzit.inote.ui.widgets.NoScrollGridView;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.CommonUtils;
import edu.xzit.inote.utils.OkHttpUtil;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class PostActivity extends Activity implements TextWatcher {

	private final String TAG = PostActivity.class.getSimpleName();

	private EditText mContentEditText;
	private TextView mTextNumberTextView;
	private NoScrollGridView mGridView;
	private GridViewAdapter mAdapter;
	// 发布按钮
	private View mPostView;
	// 选择图片
	private ImageView mSelectPicIv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mContentEditText = (EditText) findViewById(R.id.id_content_et);
		mTextNumberTextView = (TextView) findViewById(R.id.id_nmber);
		mPostView = findViewById(R.id.id_post_ll);
		mSelectPicIv = (ImageView) findViewById(R.id.id_add_iv);
		mContentEditText.addTextChangedListener(this);
		mGridView = (NoScrollGridView) findViewById(R.id.id_gridView);
		mPostView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断输入不能为空
				if (mContentEditText.getText().toString().length() == 0) {
					ToastUtil.showMessage(PostActivity.this, "内容不可为空");
					return;
				}
				if (SelectedImages.getInstance().getImages().size() > 0) {
					postMessageWithPictures();
				} else {
					postMessageWithoutPictures();
				}
			}
		});
		// 图片选择
		mSelectPicIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPics();
			}
		});
		mTextNumberTextView.setText("0");
		// 查看初始化时content view
		Log.d(TAG, "content textview :" + mContentEditText.getText().toString());
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
		mAdapter = new GridViewAdapter(this, SelectedImages.getInstance()
				.getImages());
		mGridView.setAdapter(mAdapter);
	}

	private void updateView() {
		hideInputEditText();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * 发布,使用先判断是否有图片文件，如果没有就用另一个网络请求工具上传图片
	 */
	private void postMessageWithPictures() {
		String url = SysConfig.URL + "PostServlet?op=file&fileNum="
				+ SelectedImages.getInstance().getImages().size()
				+ "&userName=" + SpUtil.getStringValue(this, "userName", "");
		Map<String, String> params = new HashMap<String, String>();

		params.put("content", mContentEditText.getText().toString());
		Map<String, String> headers = new HashMap<String, String>();
		LinkedList<File> files = new LinkedList<File>();
		int i = 0;
		for (String path : SelectedImages.getInstance().getImages()) {
			String outPath = AppUtil.compressBitmap(this, path, "pic" + i
					+ ".jpg");
			File file = new File(outPath);
			files.add(file);
			i++;
			Log.d(TAG, "file out path:" + outPath + ",post file path=" + path);
		}
		OkHttpUtil.postMultFiles(files, url, params, headers,
				new Callback<String>() {

					@Override
					public void onError(Call call, Exception e) {
						Log.d(TAG, "postMessage=" + e.getMessage());
						onPostMessageError(e.getMessage());
					}

					@Override
					public void onResponse(String response) {
						if ("0".equals(response)) {
							onPostMessageSuccess(response);
						}
					}

					@Override
					public String parseNetworkResponse(Response response)
							throws Exception {
						if (response == null) {
							return null;
						}
						return response.body().string();
					}
				});
	}

	/**
	 * 发布回调
	 * 
	 * @param string
	 */
	private void onPostMessageSuccess(String string) {
		// 清除图片容器占用空间，防止内存泄露
		SelectedImages.getInstance().clearImages();
		Log.d(TAG, "post success images size:"
				+ SelectedImages.getInstance().getImages().size());
		ToastUtil.showMessage(this, "发布成功！！");
		finish();
	}

	/**
	 * 发布失败回调
	 * 
	 * @param string
	 */
	private void onPostMessageError(String string) {
		ToastUtil.showMessage(this, "发布失败！！" + string);
		Log.d(TAG, "on post message error:" + string);
	}

	/**
	 * 不带图片的动态消息上传
	 */
	private void postMessageWithoutPictures() {
		String url = SysConfig.URL + "PostServlet";
		Log.d(TAG, "postMessageWithoutPictures=" + url);
		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", SpUtil.getStringValue(this, "userName", ""));
		params.put("content", mContentEditText.getText().toString());
		VolleyUtil.postStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if ("0".equals(response)) {
					onPostMessageSuccess(response);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				onPostMessageError(error.getMessage());
			}
		}, params);
	}

	/**
	 * 选择图片
	 */
	private void selectPics() {
		// 设置可选择的图片数量
		SysConfig.MAX_IMAGE_SELECT = 3;
		Intent intent = new Intent(this, ImagePickerActivity.class);
		startActivity(intent);
	}

	/**
	 * 获取图片地址列表，测试用
	 * 
	 * @param size
	 * @return
	 */
	@Deprecated
	private LinkedList<String> getListDatas(int size) {
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < size; i++) {
			list.add(Environment.getExternalStorageDirectory() + File.separator
					+ "aaa.png");
		}
		return list;

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 这里无需判断最大长度，因为在xml文件中已经为edit text设置过最大长度了
		mTextNumberTextView.setText(""
				+ mContentEditText.getText().toString().length());
		if (mContentEditText.getText().toString().length() >= 140) {
			ToastUtil.showMessage(this, "输入内容最大长度140字");
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	/**
	 * 关闭输入法，隐藏输入框
	 */
	private void hideInputEditText() {
		CommonUtils.hideSoftInput(this, mContentEditText);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SelectedImages.getInstance().clearImages();
	}
}
