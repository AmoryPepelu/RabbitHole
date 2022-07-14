package edu.xzit.inote.ui.page;

import java.util.Stack;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class PageManager {

	private final String TAG = PageManager.class.getSimpleName();

	private Stack<BasePage> mPageStack;
	private ViewGroup mRootViewGroup;
	private BasePage mCurrentPage;

	public PageManager(ViewGroup root) {
		mRootViewGroup = root;
		mPageStack = new Stack<BasePage>();
	}

	/**
	 * 添加page
	 * 
	 * @param basePage
	 */
	public void addPage(BasePage basePage) {
		if (mRootViewGroup == null) {
			return;
		}
		mCurrentPage = basePage;
		if (mCurrentPage != null) {
			mPageStack.push(mCurrentPage);
		}
		mRootViewGroup.removeAllViews();
		// 初始化各自的布局文件
		basePage.onAttach();
		mRootViewGroup.addView(basePage.getPageView(),
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/**
	 * 清空页面的堆栈
	 */
	public void clear() {
		mPageStack.clear();
	}

	public void clean() {
		onDetachPage();
		clear();
		mCurrentPage = null;
	}

	private void onDetachPage() {
		if (mCurrentPage != null) {
			mCurrentPage.onDetach();
		}
	}

	/**
	 * 显示前一页
	 */
	public boolean previousPage() {
		if (mPageStack.empty()) {
			return false;
		}
		// mPageStack.pop();
		// if (mPageStack.empty()) {
		// return false;
		// }
		BasePage page = mPageStack.pop();
		if (page == null) {
			return false;
		}
		addPage(page);
		return true;
	}

	/**
	 * 获取根布局
	 * 
	 * @return
	 */
	public ViewGroup getRootView() {
		return mRootViewGroup;
	}

	/**
	 * 获取当前页
	 * 
	 * @return
	 */
	public BasePage getCurrentPage() {
		return mCurrentPage;
	}
}
