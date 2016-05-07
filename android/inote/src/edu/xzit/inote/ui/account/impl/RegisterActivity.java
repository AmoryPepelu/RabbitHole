package edu.xzit.inote.ui.account.impl;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.entity.NoteBack;
import edu.xzit.inote.ui.account.IRegisterView;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class RegisterActivity extends Activity implements IRegisterView {

	private final String TAG = RegisterActivity.class.getSimpleName();

	private EditText mUserNameEditText;
	private EditText mUserPasswordEditText;
	private EditText mUserPasswordSureEditText;
	private View mRegisterView;
	private TextView mHintTextView;

	private String mUserName;
	private String mUserPassword;
	private String mUserPasswordSure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		mUserNameEditText = (EditText) findViewById(R.id.id_user_name_et);
		mUserPasswordEditText = (EditText) findViewById(R.id.id_user_password_et);
		mUserPasswordSureEditText = (EditText) findViewById(R.id.id_user_password_sure_et);
		mRegisterView = findViewById(R.id.id_regist_ll);
		mHintTextView = (TextView) findViewById(R.id.id_hint_tv);
		mRegisterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUserName = mUserNameEditText.getText().toString();
				mUserPassword = mUserPasswordEditText.getText().toString();
				mUserPasswordSure = mUserPasswordSureEditText.getText()
						.toString();
				Log.d(TAG, "user name=" + mUserName + ",user password="
						+ mUserPassword + ",user password sure="
						+ mUserPasswordSure);
				// 检查字符串是否可用
				if (AppUtil.isEmpty(mUserName)) {
					showHintTextView(getString(R.string.user_name_is_null));
					return;
				}
				if (AppUtil.isEmpty(mUserPassword)) {
					showHintTextView(getString(R.string.user_password_is_null));
					return;
				}
				// 密码长度判断
				if (!mUserPassword.equals(mUserPasswordSure)) {
					showHintTextView(getString(R.string.user_password_diff));
					return;
				}
				mHintTextView.setVisibility(View.GONE);
				// MD5加密
				mUserPassword = AppUtil.getMD5String(mUserPassword);

				postUserDate(mUserName, mUserPassword);
			}
		});
	}

	/**
	 * 显示错误信息
	 * 
	 * @param hint
	 */
	private void showHintTextView(String hint) {
		mHintTextView.setVisibility(View.VISIBLE);
		mHintTextView.setText(hint);
	}

	/**
	 * 发送注册信息
	 * 
	 * @param userName
	 * @param password
	 */
	private void postUserDate(String userName, String password) {
		String url = SysConfig.URL + "RegistServlet";
		Log.d(TAG, "url=" + url);
		Map<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("password", password);
		VolleyUtil.postStringRequest(RegisterActivity.this, url,
				new Listener<String>() {

					@Override
					public void onResponse(String res) {
						showResult(res);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				}, params);
	}

	/**
	 * 显示注册结果
	 * 
	 * @param response
	 */
	private void showResult(String response) {
		try {
			Gson gson = new Gson();
			NoteBack noteBack = gson.fromJson(response, NoteBack.class);
			if ("0".equals(noteBack.getCode())) {
				showSuccess(noteBack.getMessage());
			} else {
				showError(noteBack.getMessage());
				Log.d(TAG, "error code =" + noteBack.getCode() + ",message="
						+ noteBack.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void showSuccess(String string) {
		SpUtil.setStringValue(this, "userName", mUserName);
		ToastUtil.showMessage(this, string);
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public void showError(String string) {
		mHintTextView.setVisibility(View.VISIBLE);
		mHintTextView.setText(string);
		ToastUtil.showMessage(this, string);

	}
}
