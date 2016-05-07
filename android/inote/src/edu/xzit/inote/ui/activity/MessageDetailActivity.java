package edu.xzit.inote.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import edu.xzit.inote.R;
import edu.xzit.inote.app.SysConfig;
import edu.xzit.inote.model.entity.ActionItem;
import edu.xzit.inote.model.entity.Comment;
import edu.xzit.inote.ui.activity.adapter.CommentAdapter;
import edu.xzit.inote.ui.listener.IBaseListener;
import edu.xzit.inote.ui.listener.IDeleteComment;
import edu.xzit.inote.ui.listener.IReplyComment;
import edu.xzit.inote.ui.widgets.AppNoScrollerListView;
import edu.xzit.inote.ui.widgets.CircularImage;
import edu.xzit.inote.ui.widgets.CommentDialog;
import edu.xzit.inote.ui.widgets.MultiImageView;
import edu.xzit.inote.ui.widgets.SnsPopupWindow;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.CommonUtils;
import edu.xzit.inote.utils.SpUtil;
import edu.xzit.inote.utils.ToastUtil;
import edu.xzit.inote.utils.VolleyUtil;

public class MessageDetailActivity extends Activity implements IBaseListener,
		OnTouchListener, IDeleteComment, IReplyComment {
	private final String TAG = MessageDetailActivity.class.getSimpleName();

	private ImageLoader mImageLoader;
	private CircularImage mCircularImage;
	private TextView mNameTv;
	private TextView mTipTv;
	private TextView mContentTv, mTimeTv, mDeleteTv, mFavortListTv;
	private View mMainView;
	// 评论
	private ImageView mSnsBtn;
	private AppNoScrollerListView mCommentList;
	private View mDigCommentView;
	// private LinkedList<String> mDatas;

	// 多图
	private View mMultiImageViewContent;
	private MultiImageView mMultiImageView;

	// 评论layout
	private View mEditTextBodyLl;
	private EditText mCommentEt;
	private TextView mSendTv;
	private CommentAdapter mAdapter;
	private SnsPopupWindow mPopupWindow;

	// 已知
	private Bundle mBundle;
	// 此条动态发布者的用户名,昵称
	// private String userName, nikename;
	// “我的”用户名:当前APP使用者的用户名
	private String myName = "";
	// 此条动态的id
	private String messageId;
	// 此条动态的图片列表
	private ArrayList<String> pictures;
	// 有URL地址的图片
	private LinkedList<String> mPictures;
	// 此条动态发布者的id
	private int id;
	// 头像url
	private String mMessageUserHeadeUrl = "";
	// 此动态发布者的用户名
	private String mMessageUserName = "";
	// 此动态发布者的昵称
	private String mMessageUserNickName = "";
	// 此动态发布者的发布时间
	private String mMessageDate = "";
	// 发布内容
	private String mMessageContent = "";

	// 需要加载的数据
	// 点赞列表：用户名
	private LinkedList<String> likes;
	private StringBuilder likeSB;

	private LinkedList<Comment> comments;

	// 回复评论
	// 判断是否是评论，默认为否
	private boolean isReply = false;
	// 回复的人的位置
	private int mCommentPosition = 0;

	// 判断当前使用者是否已经赞了
	private boolean isMeLike = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);

		likeSB = new StringBuilder();

		// 获取传来的数据
		mBundle = getIntent().getBundleExtra("data");
		messageId = mBundle.getString("messageId");
		pictures = mBundle.getStringArrayList("pictures");
		mMessageUserName = mBundle.getString("messageUserName");
		mMessageUserHeadeUrl = mBundle.getString("messageUserHeadeUrl");
		mMessageUserNickName = mBundle.getString("messageUserNickName");
		mMessageDate = mBundle.getString("messageDate");
		mMessageContent = mBundle.getString("messageContent");
		// 只有自己的才能删除
		myName = SpUtil.getStringValue(this, "userName", "");
		Log.d(TAG, "messageId=" + messageId + ",mMessageUserName="
				+ mMessageUserName + ",mMessageUserHeadeUrl="
				+ mMessageUserHeadeUrl + ",mMessageUserNickName="
				+ mMessageUserNickName + ",mMessageDate=" + mMessageDate);

		// 主要用来关闭输入法
		mMainView = findViewById(R.id.id_main_ll);
		mMainView.setOnTouchListener(this);

		mImageLoader = ImageLoader.getInstance();
		// 多图显示
		mMultiImageView = (MultiImageView) findViewById(R.id.id_mult_images);
		mMultiImageViewContent = findViewById(R.id.id_images_ll);

		if (pictures != null && pictures.size() > 0) {
			mPictures = new LinkedList<String>();
			for (String s : pictures) {
				if (s == null) {
					continue;
				}
				mPictures.add(SysConfig.PIC_URL + s);
			}
			if (mPictures.size() > 0) {
				mMultiImageViewContent.setVisibility(View.VISIBLE);
				mMultiImageView.setVisibility(View.VISIBLE);
				mMultiImageView.setList(mPictures);
				mMultiImageView
						.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
							@Override
							public void onItemClick(View view, int position) {
								ImagePagerActivity.imageSize = new ImageSize(
										view.getWidth(), view.getHeight());
								ImagePagerActivity.startImagePagerActivity(
										MessageDetailActivity.this, mPictures,
										position);
							}
						});
			} else {
				mMultiImageViewContent.setVisibility(View.GONE);
				mMultiImageView.setVisibility(View.GONE);
			}
		}

		// 头像
		mCircularImage = (CircularImage) findViewById(R.id.headIv);
		mNameTv = (TextView) findViewById(R.id.nameTv);
		mTipTv = (TextView) findViewById(R.id.urlTipTv);
		mContentTv = (TextView) findViewById(R.id.contentTv);
		mTimeTv = (TextView) findViewById(R.id.timeTv);
		// 删除
		mDeleteTv = (TextView) findViewById(R.id.deleteBtn);
		mSnsBtn = (ImageView) findViewById(R.id.snsBtn);
		mDigCommentView = findViewById(R.id.digCommentBody);
		// 喜欢,赞的人
		mFavortListTv = (TextView) findViewById(R.id.favortListTv);
		// 评论列表
		mCommentList = (AppNoScrollerListView) findViewById(R.id.commentList);
		// 评论Layout
		mEditTextBodyLl = findViewById(R.id.editTextBodyLl);
		// 输入评论
		mCommentEt = (EditText) findViewById(R.id.commentEt);
		// 发送评论
		mSendTv = (TextView) findViewById(R.id.sendTv);
		mSendTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 输入内容判断
				if (AppUtil.isEmpty(mCommentEt.getText().toString())) {
					ToastUtil.showMessage(MessageDetailActivity.this,
							"请输入内容后发送");
					return;
				}
				// 用参数控制一下
				// 回复
				if (isReply) {
					isReply = false;
					doReplyComment(mCommentEt.getText().toString());
					return;
				}
				// 仅仅是评论
				postComment(mCommentEt.getText().toString(), 0, 0);
			}
		});
		mPopupWindow = new SnsPopupWindow(this);

		if (myName.equals(mMessageUserName)) {
			mDeleteTv.setVisibility(View.VISIBLE);
		} else {
			mDeleteTv.setVisibility(View.GONE);
		}
		// mDeleteTv.setVisibility(View.VISIBLE);
		// 删除此条动态
		mDeleteTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteMessage(messageId);
			}
		});

		// 添加数据
		mImageLoader.displayImage(SysConfig.PIC_URL + mMessageUserHeadeUrl,
				mCircularImage);
		mNameTv.setText(mMessageUserNickName + "@" + mMessageUserName);
		mContentTv.setText(mMessageContent);
		mTimeTv.setText(mMessageDate);

		mPopupWindow.getmActionItems().get(0).mTitle = "赞";
		mPopupWindow.update();
		mPopupWindow.setmItemClickListener(new PopupItemClickListener());
		
		mSnsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// ToastUtil.showMessage(MessageDetailActivity.this,
				// "u click this btn");
				// 弹出popupwindow
				Log.d(TAG, "is me like:" + isMeLike);
				if (isMeLike) {
					mPopupWindow.getmActionItems().get(0).mTitle = "取消赞";
				} else {
					mPopupWindow.getmActionItems().get(0).mTitle = "赞";
				}
				mPopupWindow.showPopupWindow(view);
			}
		});

		// ListView
		mCommentList.setOnItemClickListener(null);
		mCommentList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				int state = 0;
				if (myName != null
						&& myName.equals(comments.get(position).getUserName())) {
					// 是自己的评论，可以删除
					Log.d(TAG, "on item long click ,myName=" + mMessageUserName
							+ ",comment user name="
							+ comments.get(position).getUserName() + ",state="
							+ state);
					state = 0;
				} else {
					state = 1;
					Log.d(TAG, "on item long click ,myName=" + myName
							+ ",comment user name="
							+ comments.get(position).getUserName() + ",state="
							+ state);
				}
				CommentDialog dialog = new CommentDialog(
						MessageDetailActivity.this, position, state,
						MessageDetailActivity.this, MessageDetailActivity.this);
				dialog.show();
				return false;
			}

		});
		mCircularImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		// mEditTextBodyLl.setVisibility(View.VISIBLE);

		// 隐藏输入法盘
		hideInputEditText();

		// 加载数据，comment评论在赞加载完成后加载
		upLoadLikes(true);
	}

	/**
	 * 测试用
	 * 
	 * @param num
	 * @return
	 */
	// private LinkedList<String> getAdapterData(int num) {
	// mDatas = new LinkedList<String>();
	// for (int i = 0; i < num; i++) {
	// mDatas.add("comment balalbal=" + i);
	// }
	// return mDatas;
	// }

	/**
	 * pop window 事件监听
	 * 
	 * @author John
	 *
	 */
	private class PopupItemClickListener implements
			SnsPopupWindow.OnItemClickListener {
		private long mLasttime = 0;
		private int position;

		public PopupItemClickListener() {
		}

		@Override
		public void onItemClick(ActionItem item, int position) {
			// 如果我已经”赞“过该动态则改变显示
			switch (position) {
			case 0:// 点赞、取消点赞
				if (System.currentTimeMillis() - mLasttime < 700)// 防止快速点击操作
					return;
				mLasttime = System.currentTimeMillis();
				if ("赞".equals(item.mTitle.toString())) {
					// ToastUtil.showMessage(MessageDetailActivity.this, "赞");
					commitLike(item);
				} else {// 取消点赞
					// ToastUtil.showMessage(MessageDetailActivity.this, "取消赞");
					// item.mTitle="赞";
					cancleLike(item);
				}
				break;
			case 1:// 发布评论
					// ToastUtil.showMessage(MessageDetailActivity.this,
					// "发布评论");
				// 显示评论框
				showCommentEditText();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 显示评论框
	 */
	private void showCommentEditText() {
		if (mEditTextBodyLl != null && mCommentEt != null) {
			mCommentEt.setVisibility(View.VISIBLE);
			mCommentEt.setText("");
			mEditTextBodyLl.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 收起输入法盘
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mEditTextBodyLl != null
				&& mEditTextBodyLl.getVisibility() == View.VISIBLE) {
			mEditTextBodyLl.setVisibility(View.GONE);
			CommonUtils.hideSoftInput(MessageDetailActivity.this, mCommentEt);
			return true;
		}
		return false;
	}

	/**
	 * 加载赞列表
	 * 
	 * @param isLoadComment
	 *            是否加载评论：true：加载评论，false：不加载评论
	 */
	private void upLoadLikes(final boolean isLoadComment) {
		// http://127.0.0.1:8080/inote/LikeServlet?messageId=1
		AppUtil.showProgressDialog(this);
		String url = SysConfig.URL + "LoadLikesServlet?messageId=" + messageId;
		Log.d(TAG, "url=" + url);
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				// 赞加载完成后接着加载评论
				showLikesSuccess(string, isLoadComment);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showLikesError(error.getMessage());
				error.printStackTrace();
			}
		});
	}

	/**
	 * 赞加载成功
	 * 
	 * @param string
	 * @param isLoadComment
	 *            是否加载评论：true：加载评论，false：不加载评论
	 */
	private void showLikesSuccess(String string, boolean isLoadComment) {
		if (string == null || "".equals(string)) {
			return;
		}
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<String>>() {
			}.getType();
			likes = gson.fromJson(string, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		likeSB = new StringBuilder();
		if (likes != null && likes.size() != 0) {
			// 判断：到赞列表的结尾，最后不加"、"
			String last = likes.getLast();
			likeSB.append("赞：");
			String meTemp = "";
			for (String str : likes) {
				if (str == null) {
					continue;
				}
				// 如果当前使用者也点赞了，则把用户名换成“我”
				if (str.equals(myName)) {
					meTemp = "我";
					isMeLike = true;
				} else {
					meTemp = str;
				}
				// 如果到赞列表的最后，不加“、”
				if (str.equals(last)) {
					likeSB.append(meTemp);
				} else {
					likeSB.append(meTemp + "、");
				}
			}
		}

		mFavortListTv.setText(likeSB.toString());

		if (isLoadComment) {
			// 加载评论
			upLoadComments(messageId);
		} else {
			// 关闭加载框
			AppUtil.closeProgressDialog();
			if (likeSB.toString().length() == 0
					&& (comments == null || comments.size() == 0)) {

				mDigCommentView.setVisibility(View.GONE);
			} else {
				mDigCommentView.setVisibility(View.VISIBLE);
			}
		}

	}

	/**
	 * 赞加载失败
	 * 
	 * @param string
	 */
	private void showLikesError(String string) {
		Log.d(TAG, "showLikesError=" + string);
		ToastUtil.showMessage(this, string);
		// 加载评论
		upLoadComments(messageId);
	}

	/**
	 * 加载评论
	 * 
	 * @param messageId
	 *            动态的id
	 */
	private void upLoadComments(String messageId) {
		// http://127.0.0.1:8080/inote/LoadCommentServlet?messageId=3
		String url = SysConfig.URL + "LoadCommentServlet?messageId="
				+ messageId;
		Log.d(TAG, "load comments url=" + url);
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				if ("0".equals(string)) {
					// 没有任何评论
					if (likes == null || likes.size() == 0) {
						// 也没有赞，不显示赞和评论框
						mDigCommentView.setVisibility(View.GONE);
					} else {
						// 有赞，但没有评论
						mCommentList.setVisibility(View.GONE);
					}
					showCommentsError("还没有任何评论");
				} else {
					showCommentsSuccess(string);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				// 没有任何评论
				if (likes == null || likes.size() == 0) {
					// 也没有赞，不显示赞和评论框
					mDigCommentView.setVisibility(View.GONE);
				}
				showCommentsError("加载失败！");
			}
		});
	}

	/**
	 * 评论加载成功
	 * 
	 * @param string
	 */
	private void showCommentsSuccess(String string) {
		AppUtil.closeProgressDialog();
		mDigCommentView.setVisibility(View.VISIBLE);
		mCommentList.setVisibility(View.VISIBLE);
		try {
			Gson gson = new Gson();
			Type type = new TypeToken<LinkedList<Comment>>() {
			}.getType();
			comments = gson.fromJson(string, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter = new CommentAdapter(comments, this);
		mCommentList.setAdapter(mAdapter);
	}

	/**
	 * 赞加载失败
	 * 
	 * @param string
	 */
	private void showCommentsError(String string) {
		AppUtil.closeProgressDialog();
		Log.d(TAG, "showCommentsError=" + string);
		ToastUtil.showMessage(this, string);
	}

	/**
	 * 删除动态
	 * 
	 * @param messageId
	 */
	private void deleteMessage(String messageId) {
		// http://127.0.0.1:8080/inote/MessageServlet?messageId=5&op=d
		String url = SysConfig.URL + "MessageServlet?op=d&messageId="
				+ messageId;
		Log.d(TAG, "delete message:url=" + url);
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				// 删除成功
				if ("0".equals(string)) {
					finish();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastUtil.showMessage(MessageDetailActivity.this, "删除失败！");
			}
		});
	}

	/**
	 * 提交评论
	 * 
	 * @param content
	 *            评论内容
	 * @param state
	 *            0：仅仅是评论，1：回复他人评论
	 * @param otherId
	 *            回复他人的评论时对方的id
	 */
	private void postComment(String content, int otherId, int state) {
		// messageId,SpUtil.getStringValue(this, "userName", "");
		String userName = SpUtil.getStringValue(this, "userName", "");
		String url = SysConfig.URL + "CommentServlet";
		Map<String, String> params = new HashMap<String, String>();

		Log.d(TAG, "postComment url=" + url + ",userName=" + userName
				+ ",messageId=" + messageId + ",state=" + state + ",content="
				+ content + ",otherId=" + otherId);
		// 仅仅评论
		params.put("op", "com");
		params.put("userName", userName);
		params.put("messageId", messageId);
		// 这里只是提交评论
		params.put("state", "0");
		params.put("content", content);
		VolleyUtil.postStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				hideInputEditText();
				if ("0".equals(string)) {
					onCommentSuccess();
				} else {
					showError("评论失败！");
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				hideInputEditText();
				showError("评论失败！" + error.getMessage());
			}
		}, params);
	}

	/**
	 * 评论成功回调
	 */
	private void onCommentSuccess() {
		showSuccess("评论成功！");
		// 加载评论
		AppUtil.showProgressDialog(MessageDetailActivity.this);
		upLoadComments(messageId);

	}

	@Override
	public void showSuccess(String string) {
		Log.d(TAG, "showSuccess=" + string);
		ToastUtil.showMessage(this, string);
	}

	@Override
	public void showError(String string) {
		Log.d(TAG, "showError=" + string);
		ToastUtil.showMessage(this, string);
	}

	/**
	 * 回复评论回调
	 */
	@Override
	public void onReplyComment(int position) {
		Log.d(TAG, "onReplyComment,position=" + position);
		mCommentPosition = position;
		// 开启回复
		isReply = true;
		showCommentEditText();

	}

	/**
	 * 删除评论回调
	 */
	@Override
	public void onDeleteComment(int position) {
		Log.d(TAG, "onDeleteComment,position=" + position);
		int commentId = comments.get(position).getId();
		String url = SysConfig.URL + "CommentServlet?op=" + "d&" + "commentId="
				+ commentId;
		Log.d(TAG, "on delete comment,url=" + url);
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				if ("0".equals(string)) {
					showSuccess("删除成功！");
					// 加载评论
					AppUtil.showProgressDialog(MessageDetailActivity.this);
					upLoadComments(messageId);
				} else {
					showError("删除失败！error code:" + string);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showError("删除失败！" + error.getMessage());
			}
		});
	}

	/**
	 * 关闭输入法，隐藏输入框
	 */
	private void hideInputEditText() {
		if (mEditTextBodyLl != null
				&& mEditTextBodyLl.getVisibility() == View.VISIBLE) {
			mCommentEt.setText("");
			mEditTextBodyLl.setVisibility(View.GONE);
			CommonUtils.hideSoftInput(MessageDetailActivity.this, mCommentEt);
		}
	}

	/**
	 * 回复评论
	 * 
	 * @param string
	 *            评论内容
	 */
	private void doReplyComment(String string) {
		Comment comment = comments.get(mCommentPosition);
		int messageId = comment.getMessageId();
		// 被回复的人的user id
		int otherId = comment.getUserId();
		Map<String, String> params = new HashMap<String, String>();
		params.put("op", "reply");
		params.put("messageId", "" + messageId);
		params.put("otherId", "" + otherId);
		params.put("content", string);
		// 此处为我的用户名信息
		params.put("userName", myName);
		Log.d(TAG, "doReplyComment:messageId=" + messageId + ",otherId="
				+ otherId + ",content=" + string);
		String url = SysConfig.URL + "CommentServlet";
		VolleyUtil.postStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				hideInputEditText();
				if ("0".equals(response)) {
					onCommentSuccess();
				} else {
					showError("评论失败！error code:" + response);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				hideInputEditText();
				error.printStackTrace();
				showError("评论失败！" + error.getMessage());
			}
		}, params);
	}

	/**
	 * 提交赞
	 * 
	 * @param item
	 */
	private void commitLike(final ActionItem item) {
		// http://127.0.0.1:8080/inote/LikeServlet?op=add&messageId=1&userName=pepelu
		String url = SysConfig.URL + "LikeServlet?op=add&messageId="
				+ messageId + "&userName=" + myName;
		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				if ("3".equals(string)) {
					// 添加赞成功
					showSuccess("添加赞成功！");
					isMeLike = true;
					upLoadLikes(false);
				} else {
					showSuccess("添加赞失败！error code:" + string);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showError("添加赞失败！" + error.getMessage());
			}
		});
	}

	/**
	 * 取消赞
	 * 
	 * @param item
	 */
	private void cancleLike(final ActionItem item) {
		String url = SysConfig.URL + "LikeServlet?op=del&messageId="
				+ messageId + "&userName=" + myName;
		// http://127.0.0.1:8080/inote/LikeServlet?op=del&messageId=1&userName=pepelu

		VolleyUtil.getStringRequest(this, url, new Listener<String>() {

			@Override
			public void onResponse(String string) {
				if ("0".equals(string)) {
					showSuccess("删除赞成功!");
					// 仅仅加载赞，完成后不必加载评论
					isMeLike = false;
					upLoadLikes(false);
				} else {
					showError("删除赞失败！error code=" + string);
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showError("取消赞失败！" + error.getMessage());
			}
		});
	}
}
