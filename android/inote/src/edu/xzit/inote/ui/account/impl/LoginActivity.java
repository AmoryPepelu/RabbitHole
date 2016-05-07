package edu.xzit.inote.ui.account.impl;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import edu.xzit.inote.ui.activity.RootActivity;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class LoginActivity extends Activity {

	private final String TAG = LoginActivity.class.getSimpleName();

	private EditText mUserNameEditText;
	private EditText mUserPasswordEditText;
	private View mLoginView;
	private View mRegisterView;
	private TextView mHintTextView;

	private String mUserName;
	private String mPassword;
	private String mHint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		justLogin();
	}

	private void initView() {
		mUserNameEditText = (EditText) findViewById(R.id.id_user_name_et);
		mUserPasswordEditText = (EditText) findViewById(R.id.id_user_password_et);
		mLoginView = findViewById(R.id.id_login_ll);
		mRegisterView = findViewById(R.id.id_register_ll);
		mHintTextView = (TextView) findViewById(R.id.id_hint_tv);

		mLoginView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUserName = mUserNameEditText.getText().toString();
				mPassword = mUserPasswordEditText.getText().toString();
				if (AppUtil.isEmpty(mUserName)) {
					mHint = "用户名不可为空";
					ToastUtil.showMessage(LoginActivity.this, mHint);
					showHint(mHint);
					return;
				}
				hideHint();
				if (AppUtil.isEmpty(mPassword)) {
					mHint = "密码不可为空";
					ToastUtil.showMessage(LoginActivity.this, mHint);
					showHint(mHint);
					return;
				}
				hideHint();
				login(mUserName, mPassword);
			}
		});
		mRegisterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 启动注册
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * 显示提示信息
	 * 
	 * @param hint
	 */
	private void showHint(String hint) {
		if (mHintTextView == null) {
			return;
		}
		mHintTextView.setVisibility(View.VISIBLE);
		mHintTextView.setText(hint);
	}

	/**
	 * 隐藏提示信息
	 */
	private void hideHint() {
		if (mHintTextView == null) {
			return;
		}
		mHintTextView.setVisibility(View.GONE);
	}

	/**
	 * 登录验证
	 * 
	 * @param userName
	 * @param password
	 */
	private void login(String userName, String password) {
		String url = SysConfig.URL + "LoginServlet";
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", userName);
		params.put("password", AppUtil.getMD5String(password));
		VolleyUtil.postStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					Gson gson = new Gson();
					NoteBack noteBack = gson.fromJson(response, NoteBack.class);
					if ("0".equals(noteBack.getCode())) {
						showSuccess(noteBack.getMessage());
					} else {
						showError(noteBack.getMessage());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError e) {

			}
		}, params);
	}

	/**
	 * 显示登录成功
	 * 
	 * @param string
	 */
	public void showSuccess(String string) {
		ToastUtil.showMessage(this, "登录成功");
		SpUtil.setStringValue(this, "userName", mUserName);
		Intent intent = new Intent(this, RootActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 显示失败信息
	 * 
	 * @param string
	 */
	public void showError(String string) {
		showHint(string);
	}

	/**
	 * 如果sharedperference中保存有用户的用户名，直接登录
	 */
	private void justLogin() {
		String userName = SpUtil.getStringValue(this, "userName", "");
		if (!"".equals(userName)) {
			Intent intent = new Intent(this, RootActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
