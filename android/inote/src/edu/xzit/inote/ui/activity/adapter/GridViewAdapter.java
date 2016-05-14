package edu.xzit.inote.ui.activity.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import edu.xzit.inote.R;

public class GridViewAdapter extends BaseAdapter {

	private final String TAG = GridViewAdapter.class.getSimpleName();
	private Context mContext;
	private LayoutInflater mInflater;
	private LinkedList<String> mDatas;
	private ImageLoader mImageLoader;

	public GridViewAdapter(Context context, LinkedList<String> datas) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDatas = datas;
		this.mImageLoader = ImageLoader.getInstance();
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
		ImageView imageView = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.post_girdview_item, null);
			imageView = (ImageView) convertView
					.findViewById(R.id.id_item_image);
			convertView.setTag(imageView);
		} else {
			imageView = (ImageView) convertView.getTag();
		}
		imageView.setImageBitmap(getLoacalBitmap(mDatas.get(position)));
		return convertView;
	}

	/**
	 * 从本地获取bitmap图片
	 * @param url
	 * @return
	 */
	private Bitmap getLoacalBitmap(String url) {
		InputStream is = null;
		Bitmap btp = null;
		try {
			is = new FileInputStream(url);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inTempStorage = new byte[100 * 1024];
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inSampleSize = 4;
			opts.inInputShareable = true;
			btp = BitmapFactory.decodeStream(is, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return btp;
	}

}
