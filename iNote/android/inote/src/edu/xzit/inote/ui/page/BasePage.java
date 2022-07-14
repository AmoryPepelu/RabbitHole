package edu.xzit.inote.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class BasePage {

	private final String TAG = BasePage.class.getSimpleName();

	private Context mContext;
	private int mLayoutId;
	private PageManager mPageManager;
	// 布局文件view
	protected View mView;
	private LayoutInflater mInflater;

	public BasePage(Context context, int layoutId, PageManager pageManager) {
		this.mContext = context;
		this.mLayoutId = layoutId;
		this.mPageManager = pageManager;
	}

	/**
	 * 布局文件Layout初始化
	 */
	public void onAttach() {
		if (mView == null) {
			mInflater = LayoutInflater.from(mContext);
			mView = mInflater.inflate(mLayoutId, null);
			onCreateView(mView);
		}
		onResume();
	}

	/**
	 * 布局文件中的控件初始化
	 */
	protected void onCreateView(View view) {

	}

	/**
	 * 做数据加载
	 */
	public void onResume() {

	}

	/**
	 * 获取当前布局
	 * 
	 * @return
	 */
	public View getPageView() {
		return mView;
	}

	/**
	 * 做清理工作
	 */
	public void onDetach() {

	}
}
