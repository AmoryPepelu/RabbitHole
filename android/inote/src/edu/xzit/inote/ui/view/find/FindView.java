package edu.xzit.inote.ui.view.find;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.find.FindData;
import edu.xzit.inote.ui.listener.IBaseListener;
import edu.xzit.inote.ui.page.BasePage;
import edu.xzit.inote.ui.page.PageManager;
import edu.xzit.inote.ui.widgets.RefreshLayout.OnLoadListener;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class FindView extends BasePage implements OnRefreshListener,
		OnLoadListener, IBaseListener {

	private final String TAG = FindView.class.getSimpleName();

	private Context mContext;
	private PageManager mPageManager;
	private SwipeRefreshLayout mRefreshLvLayout;
	private ListView mListView;
	private FindViewAdapter mAdapter;
	private ArrayList<FindData> mDatas;
	private int mCurrentPage = 1;
	private boolean isInit = false;

	public FindView(Context context, PageManager pageManager) {
		super(context, R.layout.find_view, pageManager);
		mContext = context;
		mPageManager = pageManager;
	}

	@Override
	protected void onCreateView(View view) {
		super.onCreateView(view);
		initView();
		// 刷新数据
		updateData();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mRefreshLvLayout = (SwipeRefreshLayout) mView
				.findViewById(R.id.id_find_refresh_layout);
		mListView = (ListView) mView.findViewById(R.id.id_find_list_view);

		// for (int i = 0; i < 10; i++) {
		// mDatas.add(new FindData(0,
		// "http://img.bizhi.sogou.com/images/2014/06/17/666225.jpg",
		// "nick name " + i, "user name " + i,
		// "若要获取int类型的整数，只需要将上面的结果转行成int类型即可。比如，获取[0, 100)之间的int整数"));
		//
		// }
		mRefreshLvLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mRefreshLvLayout.setOnRefreshListener(this);
	}

	/**
	 * 刷新数据
	 */
	private void updateData() {
		String url = SysConfig.URL + "FindServlet" + "?currentPage="
				+ mCurrentPage + "&userName="
				+ SpUtil.getStringValue(mContext, "userName", "pepelu");
		Log.d(TAG, "url=" + url);
		VolleyUtil.getStringRequest(mContext, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				updateView(string);
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showError(error.getMessage());
			}

		});

	}

	/**
	 * 更新页面
	 * 
	 * @param string
	 */
	private void updateView(String string) {

		mDatas = new ArrayList<FindData>();
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<FindData>>() {
			}.getType();
			mDatas = gson.fromJson(string, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mDatas == null) {
			// 有异常发生
			showError("网络异常，请重试");
		} else if (mDatas.size() == 0) {
			showError("已无更多内容");
		} else {
			// 刷新页面
			mAdapter = new FindViewAdapter(mContext, mDatas);
			mListView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			showSuccess(string);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * SwipeRefreshLayout刷新监听，上拉刷新
	 */
	@Override
	public void onLoad() {

	}

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		// do nothing
		mRefreshLvLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 更新数据
				updateData();
				// 更新完后调用该方法结束刷新
				mRefreshLvLayout.setRefreshing(false);
			}
		}, 2000);
	}

	@Override
	public void showSuccess(String string) {
		mCurrentPage += 1;
		ToastUtil.showMessage(mContext, "加载成功");
	}

	@Override
	public void showError(String string) {
		// 分页设置为初始页
		mCurrentPage = 1;
		ToastUtil.showMessage(mContext, string);

	}
}
