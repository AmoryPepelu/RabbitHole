package edu.xzit.inote.app;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import edu.xzit.inote.R;

public class MyApplication extends Application {

	private static final String TAG = MyApplication.class.getSimpleName();

	// 默认存放图片的路径
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "INote"
			+ File.separator + "Images" + File.separator;

	private static Context mContext;
	private ImageLoader mImageLoader;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		initImageLoader();
		initGallerFinal();
	}

	public static Context getContext() {
		return mContext;
	}

	/** 初始化imageLoader */
	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.color.bg_no_photo)
				.showImageOnFail(R.color.bg_no_photo)
				.showImageOnLoading(R.color.bg_no_photo).cacheInMemory(true)
				.cacheOnDisk(true).build();

		File cacheDir = new File(DEFAULT_SAVE_IMAGE_PATH);
		ImageLoaderConfiguration imageconfig = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(200)
				.diskCache(new UnlimitedDiskCache(cacheDir))
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.defaultDisplayImageOptions(options).build();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(imageconfig);
	}

	private void initGallerFinal() {
		// ThemeConfig themeConfig = null;
	}
}
