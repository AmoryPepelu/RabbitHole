package edu.xzit.inote.app;

import java.util.LinkedList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import edu.xzit.inote.R;

public class TestPullToRefreshActivity extends Activity {
	private LinkedList<String> mListItems;
	/**
	 * 上拉刷新的控件
	 */
	private PullToRefreshListView mPullRefreshListView;

	private ArrayAdapter<String> mAdapter;

	private int mItemCount = 15;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_pull_to_refresh);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);
		// 初始化数据
		initDatas();
		// 设置适配器
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mListItems);
		mPullRefreshListView.setAdapter(mAdapter);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						Log.e("TAG", "onPullDownToRefresh");
						// 这里写下拉刷新的任务
						new GetDataTask().execute();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						Log.e("TAG", "onPullUpToRefresh");
						// 这里写上拉加载更多的任务
						new GetDataTask().execute();
					}
				});
	}

	private void initDatas() {
		// 初始化数据和数据源
		mListItems = new LinkedList<String>();

		for (int i = 0; i < mItemCount; i++) {
			mListItems.add("" + i);
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return "" + (mItemCount++);
		}

		@Override
		protected void onPostExecute(String result) {
			mListItems.add(result);
			mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
		}
	}

}
