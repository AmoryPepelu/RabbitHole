package edu.xzit.inote.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import edu.xzit.inote.R;
import edu.xzit.inote.ui.page.PageManager;
import edu.xzit.inote.ui.view.find.FindView;
import edu.xzit.inote.ui.view.home.HomeView;
import edu.xzit.inote.ui.view.mine.MineView;

public class RootActivity extends Activity {

	private final String TAG = RootActivity.class.getSimpleName();
	private ViewGroup mRootViewGroup;
	private View mChooseMenuView;
	// 菜单栏
	private View homeView;
	private View findView;
	private View postView;
	private View mineView;

	// 菜单栏代码
	private final int HOME_TAB = 0;
	private final int FIND_TAB = 1;
	private final int MINE_TAB = 2;
	private int mCurrentTab = -1;

	private PageManager mRootPageManager;

	// 展示页
	private HomeView mHomeViewPage;
	private FindView mFindViewPage;
	private MineView mMineViewPage;

	// 对应的ImageView
	private ImageView mHomeImageView;
	private ImageView mFindImageView;
	private ImageView mMineImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_root);
		initView();
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		mRootViewGroup = (ViewGroup) findViewById(R.id.id_root_linear_layout);
		mChooseMenuView = findViewById(R.id.id_choose_menu_linear_layout);
		homeView = findViewById(R.id.id_home_view);
		findView = findViewById(R.id.id_find_view);
		postView = findViewById(R.id.id_post_iv);
		mineView = findViewById(R.id.id_mine_view);
		// 初始化ImageView
		mHomeImageView = (ImageView) findViewById(R.id.id_home_iv);
		mFindImageView = (ImageView) findViewById(R.id.id_find_iv);
		mMineImageView = (ImageView) findViewById(R.id.id_mine_iv);

		// 启动发布页
		postView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RootActivity.this,
						PostActivity.class);
				startActivity(intent);
			}
		});

		homeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onTabChanged(HOME_TAB);
			}
		});
		findView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onTabChanged(FIND_TAB);
			}
		});

		mineView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onTabChanged(MINE_TAB);
			}
		});

		mRootPageManager = new PageManager(mRootViewGroup);
		// 默认显示首页
		onTabChanged(HOME_TAB);
	}

	/**
	 * 菜单栏点击事件处理
	 */
	private void onTabChanged(int id) {
		if (id == mCurrentTab) {
			return;
		}
		mCurrentTab = id;
		mHomeImageView.setImageResource(R.drawable.home);
		mFindImageView.setImageResource(R.drawable.find);
		mMineImageView.setImageResource(R.drawable.mine);

		switch (id) {
		case HOME_TAB:
			mHomeImageView.setImageResource(R.drawable.home_press);
			onHomeTabClicked();
			break;
		case FIND_TAB:
			mFindImageView.setImageResource(R.drawable.find_press);
			onFindTabClicked();
			break;
		case MINE_TAB:
			mMineImageView.setImageResource(R.drawable.mine_press);
			onMineTabClicked();
			break;
		default:
			break;
		}
	}

	/**
	 * home 页点击事件
	 */
	private void onHomeTabClicked() {
		if (mHomeViewPage == null) {
			mHomeViewPage = new HomeView(this, mRootPageManager);
		}
		mRootPageManager.clean();
		mRootPageManager.addPage(mHomeViewPage);
	}

	/**
	 * fine 页点击事件
	 */
	private void onFindTabClicked() {
		if (mFindViewPage == null) {
			mFindViewPage = new FindView(this, mRootPageManager);
		}
		mRootPageManager.clean();
		mRootPageManager.addPage(mFindViewPage);
	}

	/**
	 * mine 页点击事件
	 */
	private void onMineTabClicked() {
		if (mMineViewPage == null) {
			mMineViewPage = new MineView(this, mRootPageManager);
		}
		mRootPageManager.clean();
		mRootPageManager.addPage(mMineViewPage);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mRootPageManager.getCurrentPage() != null) {
			mRootPageManager.getCurrentPage().onResume();
		}
	}

}
