package edu.xzit.inote.ui.activity.adapter;

import java.util.LinkedList;

import edu.xzit.inote.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestPersonalPageAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<String> mDatas;
	private LayoutInflater mInflater;

	private TextView mTextView;

	public TestPersonalPageAdapter(Context context, LinkedList<String> datas) {
		this.mContext = context;
		this.mDatas = datas;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.test_list_view_item, null);
			mTextView = (TextView) convertView.findViewById(R.id.id_tv);
			convertView.setTag(mTextView);
		} else {
			mTextView = (TextView) convertView.getTag();
		}
		mTextView.setText(mDatas.get(position));
		return convertView;
	}

}
