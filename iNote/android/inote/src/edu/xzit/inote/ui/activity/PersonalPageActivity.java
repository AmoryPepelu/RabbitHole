package edu.xzit.inote.ui.activity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.entity.Message;
import edu.xzit.inote.model.entity.User;
import edu.xzit.inote.model.home.HomeData;
import edu.xzit.inote.ui.activity.adapter.TestPersonalPageAdapter;
import edu.xzit.inote.ui.listener.IBaseListener;
import edu.xzit.inote.ui.view.home.HomeAdapter;
import edu.xzit.inote.ui.widgets.ScrollViewWithListView;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class PersonalPageActivity extends Activity implements IBaseListener {

	private final String TAG = PersonalPageActivity.class.getSimpleName();

	private PullToRefreshScrollView mScrollView;
	private ScrollViewWithListView mListView;
	private ImageView mHeadImageView;
	private View mFouceContent;
	private ImageView mFouceImageView;
	// 昵称
	private TextView mNikeNameTextView;
	private TextView mUserNameTextView;
	private TextView mSignTextView;

	// 是否关注了某人
	private boolean isFouced = false;

	// 测试
	// private LinkedList<String> mDatas;
	TestPersonalPageAdapter adapter;

	// 已知的数据
	private Bundle mBundle;
	// 要展示的用户的用户名
	private String mUserName = "";
	// 用户头像
	private String mUserHeadPic = "";
	// 图片加载器
	private ImageLoader mImageLoader;
	// 当前APP使用者的user name
	private String mAppUserName = "";
	// 昵称
	private String mNikeName = "";
	// 是否已关注:0:关注；1:未关注
	private String mFollow = "1";
	// 签名
	private String mSign = "";
	// 当前分页:初始值为1
	private int mCurrentPage = 1;
	private LinkedList<HomeData> mDatas = new LinkedList<HomeData>();
	private HomeAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_page);
		mImageLoader = ImageLoader.getInstance();
		// 获取传来的数据
		mBundle = getIntent().getBundleExtra("data");
		mUserName = mBundle.getString("userName");
		mNikeName = mBundle.getString("nikeName");
		mUserHeadPic = mBundle.getString("userPic");
		mFollow = mBundle.getString("follow");
		if ("0".equals(mFollow)) {
			isFouced = true;
		} else if ("1".equals(mFollow)) {
			isFouced = false;
		}
		mAppUserName = SpUtil.getStringValue(this, "userName", "");
		initView();
		setData();
		// 获取网络数据
		getData(false);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mScrollView = (PullToRefreshScrollView) findViewById(R.id.id_personal_page_scroll_view);
		mListView = (ScrollViewWithListView) findViewById(R.id.id_personal_page_list_view);
		mHeadImageView = (ImageView) findViewById(R.id.id_personal_page_head_iv);
		mFouceContent = findViewById(R.id.id_personal_page_fouce_ll);
		mFouceImageView = (ImageView) findViewById(R.id.id_personal_page_fouce_iv);
		mNikeNameTextView = (TextView) findViewById(R.id.id_user_nick_name_tv);
		mUserNameTextView = (TextView) findViewById(R.id.id_user_user_name_tv);
		mSignTextView = (TextView) findViewById(R.id.id_person_sign_tv);

		mScrollView.setMode(Mode.PULL_FROM_END);
		mScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				getData(true);
			}

		});

		// 如果是自己的主页就设置不可关注
		if (mUserName != null && mUserName.equals(mAppUserName)) {
			mFouceContent.setVisibility(View.GONE);
		}

		mFouceImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				followSB();

			}
		});

		// adapter = new TestPersonalPageAdapter(this,
		// getTestListViewData(100));
		mAdapter = new HomeAdapter(this, mDatas);
		mListView.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(mListView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ToastUtil.showMessage(PersonalPageActivity.this,
				// "u have clicked " + position);
			}
		});
	}

	/**
	 * 关注某人
	 */
	private void followSB() {
		AppUtil.showProgressDialog(this);
		String url = "";
		if (isFouced) {
			// 已经关注，取消关注
			// http://192.168.1.103:8080/inote/PersonalServelet?op=canclefollow&userName=pepelu&otherUserName=aa
			url = SysConfig.URL + "PersonalServelet?op=canclefollow&userName="
					+ mAppUserName + "&otherUserName=" + mUserName;
		} else {
			// 未关注，设置关注
			// http://192.168.1.103:8080/inote/PersonalServelet?op=followsb&userName=pepelu&otherUserName=aa
			url = SysConfig.URL + "PersonalServelet?op=followsb&userName="
					+ mAppUserName + "&otherUserName=" + mUserName;
		}
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				AppUtil.closeProgressDialog();
				if ("0".equals(response)) {
					// 关注成功
					isFouced = true;
					mFouceImageView
							.setImageResource(R.drawable.btn_inline_following_pressed);
					showSuccess("关注成功");

				} else if ("1".equals(response)) {
					// 关注失败
					showError("关注失败!");
				} else if ("2".equals(response)) {
					// 取消关注成功
					isFouced = false;
					mFouceImageView
							.setImageResource(R.drawable.btn_inline_follow_default);
					showSuccess("取消关注成功");

				} else if ("3".equals(response)) {
					// 取消关注失败
					showError("取消关注失败!");
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				AppUtil.closeProgressDialog();
				showError("操作失败！" + error.getMessage());
				error.printStackTrace();
			}
		});
	}

	/**
	 * 初始化之后根据已有数据未控件设置初始值
	 */
	private void setData() {
		// 是否关注
		if (isFouced) {
			mFouceImageView
					.setImageResource(R.drawable.btn_inline_following_pressed);
		} else {
			mFouceImageView
					.setImageResource(R.drawable.btn_inline_follow_default);
		}
		// 头像
		mImageLoader.displayImage(SysConfig.PIC_URL + mUserHeadPic,
				mHeadImageView);
		mNikeNameTextView.setText(mNikeName);
		mUserNameTextView.setText(mUserName);
		mSignTextView.setText(mSign);
	}

	/**
	 * 初始化时加载数据
	 * 
	 * @param isPull
	 *            是否是上拉操作
	 */
	private void getData(final boolean isPull) {
		String url = SysConfig.URL
				+ "PersonalServelet?op=getMessages&userName=" + mUserName
				+ "&currentPage=" + mCurrentPage;
		Log.d(TAG, "get data url:" + url);
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if (isPull) {
					mScrollView.onRefreshComplete();
				}
				if ("1".equals(response)) {
					showError("获取数据失败！");
				} else if ("2".equals(response)) {
					showError("已无更多内容！");
				} else {
					onGetDataSuccess(response);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (isPull) {
					mScrollView.onRefreshComplete();
				}
				showError(error.getMessage());
			}
		});

	}

	/**
	 * 获取数据成功
	 * 
	 * @param response
	 */
	private void onGetDataSuccess(String response) {

		LinkedList<Message> messages = null;
		User user = new User(0, mUserName, mNikeName, "", "", "", mUserHeadPic,
				mSign, 1);
		HomeData homeData = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<Message>>() {
			}.getType();
			messages = gson.fromJson(response, type);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		if (messages == null) {
			showError("获取数据失败！");
		} else if (messages.size() == 0) {
			showError("已无更多内容！");
		} else {
			for (Message m : messages) {
				homeData = new HomeData(user, m);
				mDatas.add(homeData);
			}
			mAdapter.notifyDataSetChanged();
			setListViewHeightBasedOnChildren(mListView);
			mCurrentPage += 1;
		}
	}

	// 测试用
	// private LinkedList<String> getTestListViewData(int num) {
	// mDatas = new LinkedList<String>();
	// for (int i = 0; i < num; i++) {
	// mDatas.add(TAG + ",i=" + i);
	// }
	// return mDatas;
	// }

	/**
	 * 解决ScrollView中嵌套ListView ,listview的item是显示不全问题
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		HomeAdapter listAdapter = (HomeAdapter) listView.getAdapter();

		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);

	}

	@Override
	public void showSuccess(String string) {
		Log.d(TAG, "show success :" + string);
		ToastUtil.showMessage(this, string);
	}

	@Override
	public void showError(String string) {
		Log.e(TAG, "show error :" + string);
		ToastUtil.showMessage(this, string);
	}
}
