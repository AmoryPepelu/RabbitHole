package edu.xzit.inote.ui.activity.adapter;

import java.util.LinkedList;

import edu.xzit.inote.R;
import edu.xzit.inote.model.entity.Comment;
import edu.xzit.inote.utils.SpUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	private final String TAG = CommentAdapter.class.getSimpleName();

	private LinkedList<Comment> mDatas;
	private Context mContext;
	private LayoutInflater mInflater;
	// 当前使用者的username
	private String mUserName = "";

	/**
	 * 
	 * @param datas
	 *            评论数据
	 * @param context
	 * @param userName
	 *            当前APP的使用者用户名
	 */
	public CommentAdapter(LinkedList<Comment> datas, Context context) {
		this.mDatas = datas;
		this.mContext = context;
		this.mUserName = SpUtil.getStringValue(context, "userName", "");
		mInflater = LayoutInflater.from(mContext);
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
		ViewHolder viewHolder = null;
		Comment comment = mDatas.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.comment_item, null, false);
			viewHolder.commentTv = (TextView) convertView
					.findViewById(R.id.commentTv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 评论内容
		String content = "";

		if (mUserName.equals(comment.getUserName())) {
			content = "我";
		} else {
			content = comment.getUserName();
		}
		if (comment.getState() == 0) {
			// 仅仅是评论，无回复
			content = content + ":" + comment.getContent();
		} else if (comment.getState() == 1) {
			// 有回复
			if (mUserName.equals(comment.getOtherName())) {
				content = content + " 回复 " + " 我 " + ":" + comment.getContent();
			} else {
				content = content + " 回复 " + comment.getOtherName() + ":"
						+ comment.getContent();
			}

		}
		viewHolder.commentTv.setText(content);
		return convertView;
	}

	class ViewHolder {
		TextView commentTv;
	}
}
