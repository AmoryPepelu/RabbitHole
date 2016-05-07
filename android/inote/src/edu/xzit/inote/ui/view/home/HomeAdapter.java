package edu.xzit.inote.ui.view.home;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.home.HomeData;
import edu.xzit.inote.ui.activity.ImagePagerActivity;
import edu.xzit.inote.ui.activity.MessageDetailActivity;
import edu.xzit.inote.ui.activity.PersonalPageActivity;
import edu.xzit.inote.ui.widgets.CircularImage;
import edu.xzit.inote.ui.widgets.MultiImageView;
import edu.xzit.inote.utils.ToastUtil;

/**
 * 首页主视图adapter
 * 
 * @author John
 *
 */
public class HomeAdapter extends BaseAdapter {

	private final String TAG = HomeAdapter.class.getSimpleName();

	private LinkedList<HomeData> mDatas;
	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	List<String> pics;

	public HomeAdapter(Context context, LinkedList<HomeData> datas) {
		this.mDatas = datas;
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
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
		final HomeData data = mDatas.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.home_list_view_item, null);
			viewHolder.circularImage = (CircularImage) convertView
					.findViewById(R.id.headIv);
			viewHolder.nameTv = (TextView) convertView
					.findViewById(R.id.nameTv);
			viewHolder.contentTv = (TextView) convertView
					.findViewById(R.id.contentTv);
			viewHolder.timeTv = (TextView) convertView
					.findViewById(R.id.timeTv);

			// 多图
			viewHolder.multImageContent = convertView
					.findViewById(R.id.id_images_ll);
			viewHolder.multiImageView = (MultiImageView) convertView
					.findViewById(R.id.id_mult_images);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (data.getUser().getPicture() != null
				&& !"".equals(data.getUser().getPicture())) {
			mImageLoader.displayImage(SysConfig.PIC_URL
					+ data.getUser().getPicture(), viewHolder.circularImage);
		}

		viewHolder.nameTv.setText(data.getUser().getName());
		viewHolder.contentTv.setText(data.getMessage().getContent());

		// 时间
		viewHolder.timeTv.setText(data.getMessage().getDate());
		if (data.getMessage().getList() != null
				&& data.getMessage().getList().size() > 0) {
			pics = new LinkedList<String>();
			// pics.addAll(getImageListLocal(1));
			for (String pic : data.getMessage().getList()) {
				if (pic == null) {
					continue;
				}
				pic = SysConfig.PIC_URL + pic;
				pics.add(pic);
				Log.d(TAG, pic);
			}
			if (pics != null && pics.size() != 0) {
				Log.d(TAG, "add mult image listener" + position);
				// 多图片处理
				viewHolder.multImageContent.setVisibility(View.VISIBLE);
				viewHolder.multiImageView.setVisibility(View.VISIBLE);
				viewHolder.multiImageView.setList(pics);
				// viewHolder.multiImageView
				// .setOnItemClickListener(new
				// MultiImageView.OnItemClickListener() {
				// @Override
				// public void onItemClick(View view, int position) {
				// ImagePagerActivity.imageSize = new ImageSize(
				// view.getWidth(), view.getHeight());
				// ImagePagerActivity.startImagePagerActivity(
				// mContext, pics, position);
				// }
				// });
			} else {
				viewHolder.multImageContent.setVisibility(View.GONE);
				viewHolder.multiImageView.setVisibility(View.GONE);
			}

		} else {
			viewHolder.multImageContent.setVisibility(View.GONE);
			viewHolder.multiImageView.setVisibility(View.GONE);
		}
		
		// viewHolder.multiImageView
		// .setOnItemClickListener(new MultiImageView.OnItemClickListener() {
		// @Override
		// public void onItemClick(View view, int position) {
		// ImagePagerActivity.imageSize = new ImageSize(view
		// .getWidth(), view.getHeight());
		// ImagePagerActivity.startImagePagerActivity(mContext,
		// pics, position);
		// }
		// });

		viewHolder.multiImageView
				.setOnItemClickListener(new MultiImageListener(pics));

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ToastUtil.showMessage(mContext, "convert view");
				// 显示动态详情
				startActivityMessageDetail(data);
			}
		});

		viewHolder.circularImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ToastUtil.showMessage(mContext, "head view");
				// 显示个人主页
				startPersonalPageActivity(data);
			}
		});

		// 多图片处理 测试
		// viewHolder.multImageContent.setVisibility(View.VISIBLE);
		// viewHolder.multiImageView.setVisibility(View.VISIBLE);
		// viewHolder.multiImageView.setList(getImageList(3));
		// viewHolder.multiImageView
		// .setOnItemClickListener(new MultiImageView.OnItemClickListener() {
		// @Override
		// public void onItemClick(View view, int position) {
		// ImagePagerActivity.imageSize = new ImageSize(view
		// .getWidth(), view.getHeight());
		// ImagePagerActivity.startImagePagerActivity(mContext,
		// getImageList(3), position);
		// }
		// });

		return convertView;
	}

	private List<String> getImageList(int num) {
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < num; i++) {
			list.add("https://www.baidu.com/img/bd_logo1.png");
		}
		return list;
	}

	private List<String> getImageListLocal(int num) {
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < num; i++) {
			// list.add("https://www.baidu.com/img/bd_logo1.png");
			list.add(SysConfig.PIC_URL + "06395808868e4921bb90c660ed48a492.jpg");
			list.add(SysConfig.PIC_URL + "5867ec17e6864e75b511d969f4d9ea72.jpg");
			list.add(SysConfig.PIC_URL + "8f48f71c990546238f41c242fd2ecd4d.jpg");
		}
		return list;
	}

	/**
	 * 显示动态详情
	 * 
	 * @param homeData
	 */
	private void startActivityMessageDetail(HomeData homeData) {

		Intent intent = new Intent(mContext, MessageDetailActivity.class);
		Bundle bundle = new Bundle();
		ArrayList<String> list = new ArrayList<String>();
		List<String> picList = homeData.getMessage().getList();
		if (picList != null && picList.size() != 0) {
			for (String s : picList) {
				list.add(s);
			}
		}
		bundle.putStringArrayList("pictures", list);
		bundle.putString("messageId", "" + homeData.getMessage().getId());
		bundle.putString("messageUserName", homeData.getUser().getName());
		bundle.putString("messageUserHeadeUrl", homeData.getUser().getPicture());
		bundle.putString("messageUserNickName", homeData.getUser()
				.getNikename());
		bundle.putString("messageDate", homeData.getMessage().getDate());
		bundle.putString("messageContent", homeData.getMessage().getContent());
		intent.putExtra("data", bundle);
		mContext.startActivity(intent);
	}

	/**
	 * 展示个人主页
	 * 
	 * @param homeData
	 */
	private void startPersonalPageActivity(HomeData homeData) {
		Intent intent = new Intent(mContext, PersonalPageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userName", homeData.getUser().getName());
		bundle.putString("nikeName", homeData.getUser().getNikename());
		bundle.putString("userPic", homeData.getUser().getPicture());
		// 已经关注了此人
		bundle.putString("follow", "0");
		intent.putExtra("data", bundle);
		mContext.startActivity(intent);
	}

	private class ViewHolder {
		// 圆角图片
		CircularImage circularImage;
		// 姓名
		TextView nameTv;
		// 发布内容
		TextView contentTv;
		// 时间
		TextView timeTv;
		// 多个图片容器
		View multImageContent;
		// 多个图片
		MultiImageView multiImageView;
	}

	/**
	 * 多图片点击监听器
	 * 
	 * @author John
	 *
	 */
	class MultiImageListener implements MultiImageView.OnItemClickListener {
		private List<String> mList = new LinkedList<String>();

		public MultiImageListener(List<String> list) {
			for (String string : list) {
				mList.add(string);
			}
		}

		@Override
		public void onItemClick(View view, int position) {
			ImagePagerActivity.imageSize = new ImageSize(view.getWidth(),
					view.getHeight());
			ImagePagerActivity.startImagePagerActivity(mContext, mList,
					position);
		}

	}
}
