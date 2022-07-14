package edu.xzit.inote.ui.view.home;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.home.HomeData;
import edu.xzit.inote.ui.listener.IBaseListener;
import edu.xzit.inote.ui.page.BasePage;
import edu.xzit.inote.ui.page.PageManager;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class HomeView extends BasePage implements IBaseListener {

	private final String TAG = HomeView.class.getSimpleName();

	private Context mContext;
	private PageManager mPageManager;
	private PullToRefreshListView mPullRefreshListView;
	private HomeAdapter mAdapter;
	private LinkedList<HomeData> mDatas;

	private int mCurrentPage = 1;

	public HomeView(Context context, PageManager pageManager) {
		super(context, R.layout.home_view, pageManager);
		this.mContext = context;
		this.mPageManager = pageManager;
		mDatas = new LinkedList<HomeData>();
	}

	@Override
	protected void onCreateView(View view) {
		super.onCreateView(view);
		initView();
		updateView();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		mPullRefreshListView = (PullToRefreshListView) mView
				.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);
		// 为ListView添加数据
		// mDatas = new ArrayList<HomeData>();
		// mDatas = getDatas(1);
		mAdapter = new HomeAdapter(mContext, mDatas);
		mPullRefreshListView.setAdapter(mAdapter);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						Log.e("TAG", "onPullDownToRefresh");
						// 这里写下拉刷新的任务
						// new GetDataTask().execute();
						pullDownRefresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						Log.e("TAG", "onPullUpToRefresh");
						// 这里写上拉加载更多的任务
						// new GetDataTask().execute();
						pullUpRefresh();
					}
				});
	}

	/**
	 * 更新数据
	 */
	private void updateView() {
		pullUpRefresh();
	}

	/**
	 * 上拉刷新
	 */
	private void pullUpRefresh() {
		String userName = SpUtil.getStringValue(mContext, "userName", "pepelu");
		String url = SysConfig.URL + "HomeServlet?currentPage=" + mCurrentPage
				+ "&userName=" + userName;
		VolleyUtil.getStringRequest(mContext, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				Log.d(TAG, "pullUpRefresh=" + string);
				mPullRefreshListView.onRefreshComplete();
				if (string == null || "0".equals(string)) {
					showError("没有更多内容");
				} else {
					pullUpSuccess(string);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				mPullRefreshListView.onRefreshComplete();
				Log.e(TAG, "pullUpRefresh error=" + error);
				showError(error.getMessage());
			}
		});
	}

	/**
	 * 上拉刷新成功
	 * 
	 * @param string
	 */
	private void pullUpSuccess(String string) {
		mCurrentPage += 1;
		ToastUtil.showMessage(mContext, "加载成功");
		LinkedList<HomeData> datas = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<HomeData>>() {
			}.getType();
			datas = gson.fromJson(string, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (datas == null || datas.size() == 0) {
			showError("已无更多内容");
			return;

		}

		if (mDatas.size() == 0) {
			mDatas.addAll(datas);
			mAdapter.notifyDataSetChanged();
			return;
		}

		int mesID = mDatas.getLast().getMessage().getId();
		for (HomeData data : datas) {
			if (mesID > data.getMessage().getId()) {
				mDatas.addLast(data);
			}
		}
		mAdapter.notifyDataSetChanged();
		showSuccess("加载成功");
	}

	/**
	 * 下拉刷新
	 */
	private void pullDownRefresh() {
		String userName = SpUtil.getStringValue(mContext, "userName", "pepelu");
		String url = SysConfig.URL + "HomeServlet?currentPage=" + 1
				+ "&userName=" + userName;
		VolleyUtil.getStringRequest(mContext, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				mPullRefreshListView.onRefreshComplete();
				if (string == null || "0".equals(string)) {
					showError("当前已是最新");
				} else {
					pullDownSuccess(string);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				mPullRefreshListView.onRefreshComplete();
				showError("加载失败!" + error.getMessage());
			}
		});
	}

	/**
	 * 下拉刷新成功
	 * 
	 * @param string
	 */
	private void pullDownSuccess(String string) {
		ToastUtil.showMessage(mContext, "加载成功");
		LinkedList<HomeData> datas = null;
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<HomeData>>() {
			}.getType();
			datas = gson.fromJson(string, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (datas == null || datas.size() == 0) {
			showError("已无更多内容");
			return;
		}

		if (datas.getLast() == null || datas.getLast().getUser() == null) {
			showError("已无更多内容");
			return;
		}

		if (mDatas.size() == 0) {
			mDatas.addAll(datas);
			mAdapter.notifyDataSetChanged();
			return;
		}

		int mesID = mDatas.getFirst().getMessage().getId();
		for (int i = datas.size(); i > 0; i--) {
			if (datas.get(i - 1).getMessage().getId() > mesID) {
				mDatas.addFirst(datas.get(i - 1));
			}
		}
		mAdapter.notifyDataSetChanged();
		showSuccess("success");
	}

	@Override
	public void showSuccess(String string) {
		ToastUtil.showMessage(mContext, "加载成功");
	}

	@Override
	public void showError(String string) {
		ToastUtil.showMessage(mContext, string);
	}

}
