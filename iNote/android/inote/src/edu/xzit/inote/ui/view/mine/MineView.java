package edu.xzit.inote.ui.view.mine;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import android.app.Activity;
import android.content.Context;
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
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.callback.Callback;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.entity.SelectedImages;
import edu.xzit.inote.model.entity.User;
import edu.xzit.inote.support.imagepicker.imageloader.ImagePickerActivity;
import edu.xzit.inote.ui.account.impl.LoginActivity;
import edu.xzit.inote.ui.activity.PersonalPageActivity;
import edu.xzit.inote.ui.listener.IBaseListener;
import edu.xzit.inote.ui.page.BasePage;
import edu.xzit.inote.ui.page.PageManager;
import edu.xzit.inote.ui.widgets.CircularImage;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.CommonUtils;
import edu.xzit.inote.utils.OkHttpUtil;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class MineView extends BasePage implements IBaseListener {

	private final String TAG = MineView.class.getSimpleName();

	private Context mContext;
	// 修改的类型
	// 修改昵称
	private final int CHANGE_NICK_NAME = 0;
	// 修改签名
	private final int CHANGE_SIGN = 1;

	// 头像
	private CircularImage mCircularImage;
	// 昵称
	private TextView mNickNameTv;
	// 用户名
	private TextView mUserNameTv;
	// 签名
	private TextView mSignTv;
	// 我的动态
	private View mMessageView;
	// 修改头像
	private View mChangeHeadView;
	// 修改昵称
	private View mChangeNickNameView;
	// 修改签名
	private View mChangeSignView;
	// 退出登录
	private View mExitLoginView;
	// 输入框总框架
	private View mEditLayout;
	// 输入框
	private EditText mEditText;
	// 发送
	private TextView mSendTextView;

	// 用户名
	private String mUserName = "";
	// 用户头像
	private String mUserHeadPic = "";
	// 图片加载器
	private ImageLoader mImageLoader;

	// 启动个人主页需要的参数
	private String pNickName = "";
	private String pHeadPicture = "";

	public MineView(Context context, PageManager pageManager) {
		super(context, R.layout.mine_view, pageManager);
		this.mContext = context;
		this.mUserName = SpUtil.getStringValue(mContext, SysConfig.USER_NAME,
				"");
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	protected void onCreateView(View view) {
		super.onCreateView(view);
		// 设置只能选择一张图片
		SysConfig.MAX_IMAGE_SELECT = 1;
		// 第一次创建时清理图片选择器，防止其他没有的情况
		SelectedImages.getInstance().clearImages();
		initView();
		setClickEvent();
		updateView();
	}

	/**
	 * 初始化控件
	 * 
	 */
	private void initView() {
		mCircularImage = (CircularImage) mView.findViewById(R.id.headIv);
		mNickNameTv = (TextView) mView.findViewById(R.id.id_user_nick_name_tv);
		mUserNameTv = (TextView) mView.findViewById(R.id.id_user_user_name_tv);
		mSignTv = (TextView) mView.findViewById(R.id.id_person_sign_tv);
		mMessageView = mView.findViewById(R.id.id_my_message);
		mChangeHeadView = mView.findViewById(R.id.id_change_head_image);
		mChangeNickNameView = mView.findViewById(R.id.id_change_nick_name);
		mChangeSignView = mView.findViewById(R.id.id_change_sign);
		mExitLoginView = mView.findViewById(R.id.id_exit_login);
		mEditLayout = mView.findViewById(R.id.editTextBodyLl);
		mEditText = (EditText) mView.findViewById(R.id.id_edit_text);
		mSendTextView = (TextView) mView.findViewById(R.id.sendTv);
	}

	/**
	 * 更新视图
	 */
	private void updateView() {
		AppUtil.showProgressDialog(mContext);
		String url = SysConfig.URL + "MineServlet?op=updateView&userName="
				+ mUserName;
		VolleyUtil.getStringRequest(mContext, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				AppUtil.closeProgressDialog();
				if ("1".equals(string)) {
					showError("获取信息失败！");
				} else {
					showSuccess("获取信息成功！");
					onUpdateViewSuccess(string);
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				AppUtil.closeProgressDialog();
				showError("" + error.getMessage());
			}
		});
	}

	private void onUpdateViewSuccess(String string) {
		User user = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<User>() {
			}.getType();
			user = gson.fromJson(string, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user == null) {
			showError("获取信息失败！");
			Log.d(TAG, "user is null!!");
			return;
		}
		pNickName = user.getNikename();
		pHeadPicture = user.getPicture();

		mNickNameTv.setText(user.getNikename());
		mUserNameTv.setText(user.getName());
		mSignTv.setText(user.getIntroduction());
		if (user.getPicture() != null) {
			mImageLoader.displayImage(SysConfig.PIC_URL + user.getPicture(),
					mCircularImage);
		}
	}

	/**
	 * 绑定事件监听
	 */
	private void setClickEvent() {
		mView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideInputEditText();
			}
		});
		mChangeHeadView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ImagePickerActivity.class);
				mContext.startActivity(intent);
			}
		});
		mChangeNickNameView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showEditText(CHANGE_NICK_NAME);
			}
		});
		mChangeSignView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showEditText(CHANGE_SIGN);
			}
		});

		mExitLoginView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SpUtil.setStringValue(mContext, "userName", "");
				Intent intent = new Intent(mContext, LoginActivity.class);
				mContext.startActivity(intent);
				((Activity) mContext).finish();
			}
		});

		mMessageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startPersonalPageActivity();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// 做头像上传
		List<String> imageList = SelectedImages.getInstance().getImages();
		if (imageList == null || imageList.size() == 0) {
			Log.d(TAG, "未选择任何图片，不做头像修改");
			return;
		}
		ToastUtil.showMessage(mContext, "正在上传图片...");
		mUserHeadPic = AppUtil.compressBitmap(mContext, imageList.get(0),
				"headImage.jpg");
		upLoadHeaderImage(mUserHeadPic);
	}

	/**
	 * 上传用户头像
	 * 
	 * @param 用户头像path
	 */
	private void upLoadHeaderImage(String filePath) {
		AppUtil.showProgressDialog(mContext);
		String url = SysConfig.URL + "MineServlet?op=headerImage&userName="
				+ mUserName;
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> headers = new HashMap<String, String>();
		LinkedList<File> files = new LinkedList<File>();
		File file = new File(filePath);
		files.add(file);
		OkHttpUtil.postMultFiles(files, url, params, headers,
				new Callback<String>() {

					@Override
					public void onError(Call call, Exception e) {
						AppUtil.closeProgressDialog();
						Log.d(TAG, "postMessage=" + e.getMessage());
						// 清理图片
						SelectedImages.getInstance().clearImages();
						showError("上传头像失败！");
					}

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "upload pic response=" + response);
						AppUtil.closeProgressDialog();
						// 清理图片
						SelectedImages.getInstance().clearImages();
						if (!"1".equals(response)) {
							showSuccess("上传头像成功！");
							updateHeaderImage(response);
						} else {
							showError("上传头像失败！");
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
	 * 头像上传成功后更新头像
	 */
	private void updateHeaderImage(String filePath) {
		Log.d(TAG, "file path:" + SysConfig.PIC_URL + filePath);
		pHeadPicture = filePath;
		mImageLoader.displayImage(SysConfig.PIC_URL + filePath, mCircularImage);
	}

	/**
	 * 关闭输入法，隐藏输入框
	 */
	private void hideInputEditText() {
		if (mEditLayout != null && mEditLayout.getVisibility() == View.VISIBLE) {
			mEditText.setText("");
			mEditLayout.setVisibility(View.GONE);
			CommonUtils.hideSoftInput(mContext, mEditLayout);
		}
	}

	/**
	 * 资源清理
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		SelectedImages.getInstance().clearImages();
	}

	/**
	 * 显示编辑框
	 * 
	 * @param code
	 *            改变控件时的请求类型
	 */
	private void showEditText(int code) {
		mEditLayout.setVisibility(View.VISIBLE);
		mEditText.setVisibility(View.VISIBLE);
		mSendTextView.setOnClickListener(new MyOnClickListener(code));
	}

	/**
	 * 当发送键按下的事件监听
	 * 
	 * @author John
	 *
	 */
	class MyOnClickListener implements View.OnClickListener {
		private int mCode;

		public MyOnClickListener(int code) {
			this.mCode = code;
		}

		@Override
		public void onClick(View v) {
			String content = mEditText.getText().toString();
			if (AppUtil.isEmpty(content)) {
				ToastUtil.showMessage(mContext, "内容不可为空");
				return;
			}
			switch (mCode) {
			case CHANGE_NICK_NAME:
				changeNickName(content);
				break;
			case CHANGE_SIGN:
				changeSign(content);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 修改昵称
	 * 
	 * @param string
	 */
	private void changeNickName(final String string) {
		AppUtil.showProgressDialog(mContext);
		String url = SysConfig.URL + "MineServlet?op=changeNickName"
				+ "&userName=" + mUserName;
		Map<String, String> params = new HashMap<String, String>();
		params.put("nickName", string);
		VolleyUtil.postStringRequest(mContext, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if ("0".equals(response)) {
					showSuccess("修改昵称成功！");
					mNickNameTv.setText(string);
				} else {
					showError("修改昵称失败！");
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showError("修改昵称失败！" + error.getMessage());
			}
		}, params);
	}

	/**
	 * 修改签名
	 * 
	 * @param string
	 */
	private void changeSign(final String string) {
		AppUtil.showProgressDialog(mContext);
		String url = SysConfig.URL + "MineServlet?op=changeSign" + "&userName="
				+ mUserName;
		Map<String, String> params = new HashMap<String, String>();
		params.put("sign", string);
		VolleyUtil.postStringRequest(mContext, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if ("0".equals(response)) {
					showSuccess("修改签名成功！");
					mSignTv.setText(string);
				} else {
					showError("修改签名失败！");
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showError("修改签名失败!" + error.getMessage());
			}
		}, params);
	}

	/**
	 * 访问网路成功后回调
	 */
	@Override
	public void showSuccess(String string) {
		AppUtil.closeProgressDialog();
		Log.d(TAG, "request net response success !" + string);
		ToastUtil.showMessage(mContext, string);
		hideInputEditText();
	}

	/**
	 * 访问网络失败后回调
	 */
	@Override
	public void showError(String string) {
		AppUtil.closeProgressDialog();
		Log.e(TAG, "request net response faill !" + string);
		ToastUtil.showMessage(mContext, string);
	}

	/**
	 * 展示个人主页
	 * 
	 * @param homeData
	 */
	private void startPersonalPageActivity() {
		Intent intent = new Intent(mContext, PersonalPageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userName",
				SpUtil.getStringValue(mContext, "userName", ""));
		bundle.putString("nikeName", mNickNameTv.getText().toString());
		bundle.putString("userPic", pHeadPicture);

		bundle.putString("follow", "1");
		intent.putExtra("data", bundle);
		mContext.startActivity(intent);
	}
}
