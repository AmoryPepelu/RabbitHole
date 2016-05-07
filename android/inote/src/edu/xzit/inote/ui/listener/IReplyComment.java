package edu.xzit.inote.ui.listener;

/**
 * 回复评论的回调
 * 
 * @author John
 *
 */
public interface IReplyComment {
	/**
	 * 回复评论
	 * 
	 * @param position
	 *            listview 的位置
	 */
	void onReplyComment(int position);
}
