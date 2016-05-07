package edu.xzit.inote.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import edu.xzit.inote.R;
import edu.xzit.inote.ui.listener.IDeleteComment;
import edu.xzit.inote.ui.listener.IReplyComment;

/**
 * 
 * @ClassName: CommentDialog
 * @Description: 评论长按对话框，保护回复和删除
 *
 */
public class CommentDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context mContext;
	private IDeleteComment deleteComment;
	private IReplyComment replyComment;
	private int mPosition;
	private int mState;

	/**
	 * 
	 * @param context
	 * @param position
	 *            评论被点击的位置
	 * @param state
	 *            是否可以删除，0：是自己的评论，可以删除,1:不是自己的评论，不可以删除
	 * @param replyComment
	 *            回复回调
	 * @param deleteComment
	 *            删除回调
	 */
	public CommentDialog(Context context, int position, int state,
			IReplyComment replyComment, IDeleteComment deleteComment) {
		super(context, R.style.comment_dialog);
		this.deleteComment = deleteComment;
		this.replyComment = replyComment;
		mContext = context;
		this.mPosition = position;
		this.mState = state;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_comment);
		initWindowParams();
		initView();
	}

	private void initWindowParams() {
		Window dialogWindow = getWindow();
		// 获取屏幕宽、高用
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (display.getWidth() * 0.65); // 宽度设置为屏幕的0.65

		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);
	}

	private void initView() {
		TextView replyTextView = (TextView) findViewById(R.id.replyTv);
		TextView deleteTv = (TextView) findViewById(R.id.deleteTv);
		replyTextView.setOnClickListener(this);
		deleteTv.setOnClickListener(this);
		if (mState == 1) {
			deleteTv.setVisibility(View.GONE);
		} else {
			deleteTv.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.replyTv:
			dismiss();
			replyComment.onReplyComment(mPosition);
			break;
		case R.id.deleteTv:
			dismiss();
			deleteComment.onDeleteComment(mPosition);
			break;
		default:
			break;
		}
	}

}
