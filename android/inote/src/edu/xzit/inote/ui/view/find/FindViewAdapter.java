package edu.xzit.inote.ui.view.find;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.find.FindData;
import edu.xzit.inote.ui.activity.PersonalPageActivity;
import edu.xzit.inote.ui.widgets.CircularImage;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

/**
 * 发现页ViewAdapter
 * 
 * @author John
 *
 */
public class FindViewAdapter extends BaseAdapter {

	private final String TAG = FindViewAdapter.class.getSimpleName();

	private Context mContext;
	private ArrayList<FindData> mDatas;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;

	private int otherId = 0;
	

	private int[] isFollowedList;

	public FindViewAdapter(Context context, ArrayList<FindData> datas) {
		mContext = context;
		mDatas = datas;
		mInflater = LayoutInflater.from(mContext);
		mImageLoader = ImageLoader.getInstance();
		initFollowList();
	}

	private void initFollowList() {
		isFollowedList = new int[mDatas.size()];
		for (int i = 0; i < isFollowedList.length; i++) {
			// 未关注：0,关注：1
			isFollowedList[i] = 0;
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		 final FindData data = mDatas.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.find_view_listview_item,
					null);
			viewHolder.headImageView = (CircularImage) convertView
					.findViewById(R.id.id_user_pic);
			viewHolder.userNameTv = (TextView) convertView
					.findViewById(R.id.id_user_name);
			viewHolder.userNickNameTv = (TextView) convertView
					.findViewById(R.id.id_user_nick_name);
			viewHolder.fouceIv = (ImageView) convertView
					.findViewById(R.id.id_foucse);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.id_content);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.content.setText(data.getIntroduction());
		viewHolder.userNameTv.setText(data.getName());
		viewHolder.userNickNameTv.setText(data.getNikename()+"@");
		if (mDatas.get(position).getPicture() != null) {
			mImageLoader.displayImage(SysConfig.PIC_URL + data.getPicture(),
					viewHolder.headImageView);
		}
		// 头像点击事件监听
		viewHolder.headImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startPersonalPageActivity(data, position);
			}
		});

		// 关注 按钮点击事件监听
		viewHolder.fouceIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				followSB(position);
			}
		});

		// 是否已经关注
		if (isFollowedList[position] == 1) {
			// 关注
			viewHolder.fouceIv
					.setImageResource(R.drawable.btn_inline_following_pressed);
		} else {
			// 未关注
			viewHolder.fouceIv
					.setImageResource(R.drawable.btn_inline_follow_default);
		}

		return convertView;
	}

	/**
	 * 展示个人主页
	 * 
	 * @param homeData
	 */
	private void startPersonalPageActivity(FindData data, int position) {
		Intent intent = new Intent(mContext, PersonalPageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userName", data.getName());
		bundle.putString("nikeName", data.getNikename());
		bundle.putString("userPic", data.getPicture());
		// 0:已经关注了此人;1:未关注
		if (isFollowedList[position] == 1) {
			bundle.putString("follow", "0");
		} else {
			bundle.putString("follow", "1");
		}

		intent.putExtra("data", bundle);
		mContext.startActivity(intent);
	}

	/**
	 * ViewHolder
	 * 
	 * @author John
	 *
	 */
	private class ViewHolder {
		CircularImage headImageView;
		TextView userNickNameTv;
		TextView userNameTv;
		TextView content;
		ImageView fouceIv;
	}

	/**
	 * 关注事件处理
	 * 
	 * @param position
	 *            按钮位置
	 */
	private void followSB(final int position) {
		otherId = mDatas.get(position).getId();
		String userName = SpUtil.getStringValue(mContext, "userName", "");
		String url = "";

		if (isFollowedList[position] == 1) {
			// 已经关注，在点击事件发生后取消关注
			url = SysConfig.URL + "CancelFollowSB?userName=" + userName
					+ "&otherId=" + otherId;
		} else {
			// 没有关注，在点击事件发生后增加关注
			url = SysConfig.URL + "FollowServlet?userName=" + userName
					+ "&otherId=" + otherId;
		}
		Log.d(TAG, "follow url=" + url);
		VolleyUtil.getStringRequest(mContext, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				followSBSuccess(string, position);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				followSBError(error.getMessage());
			}
		});
	}

	/**
	 * 成功关注某人
	 * 
	 * @param string
	 */
	private void followSBSuccess(String string, int positon) {
		if ("0".equals(string)) {
			// 关注成功，刷新数据：手动修改用户名
			String userName = mDatas.get(positon).getName();
			mDatas.get(positon).setName(" ");
			isFollowedList[positon] = 1;
			mDatas.get(positon).setName(userName);
			notifyDataSetChanged();
		} else if ("1".equals(string)) {
			// 关注失败
			followSBError("关注失败");
		} else if ("2".equals(string)) {
			// 取消关注成功
			String userName = mDatas.get(positon).getName();
			mDatas.get(positon).setName(" ");
			isFollowedList[positon] = 0;
			mDatas.get(positon).setName(userName);
			notifyDataSetChanged();
		} else if ("3".equals(string)) {
			// 取消关注失败
			followSBError("取消关注失败");
		}
	}

	/**
	 * 关注某人失败
	 * 
	 * @param string
	 */
	private void followSBError(String string) {
		ToastUtil.showMessage(mContext, string);
	}

}
