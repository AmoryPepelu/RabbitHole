package edu.xzit.inote.app;

import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import edu.xzit.inote.R;
import edu.xzit.inote.ui.activity.ImagePagerActivity;
import edu.xzit.inote.ui.widgets.MultiImageView;
import edu.xzit.inote.ui.widgets.MultiImageView.OnItemClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TestMultImagesActivity extends Activity {

	private MultiImageView mMultImageView;
	
	private List<String> lists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_multimages);
		mMultImageView = (MultiImageView) findViewById(R.id.multiImagView);
		lists=getImageList(1);
		mMultImageView.setList(lists);
		mMultImageView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				ImagePagerActivity.imageSize = new ImageSize(view.getWidth(),
						view.getHeight());
				ImagePagerActivity.startImagePagerActivity(
						TestMultImagesActivity.this,lists, position);
			}
		});
	}

	private List<String> getImageList(int num) {
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < num; i++) {
//			list.add("https://www.baidu.com/img/bd_logo1.png");
			list.add(SysConfig.PIC_URL+"06395808868e4921bb90c660ed48a492.jpg");
			list.add(SysConfig.PIC_URL+"5867ec17e6864e75b511d969f4d9ea72.jpg");
			list.add(SysConfig.PIC_URL+"8f48f71c990546238f41c242fd2ecd4d.jpg");
		}
		return list;
	}
}
