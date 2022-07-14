package edu.xzit.inote.ui.activity.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MessageDetailAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<String> mDatas;
	private LayoutInflater mInflater;

	public MessageDetailAdapter(Context context, LinkedList<String> datas) {
		this.mContext = context;
		this.mDatas = datas;
		mInflater = LayoutInflater.from(context);
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
		
		return null;
	}

}
