package edu.xzit.inote.model;

import java.io.Serializable;

/**
 * 评论表
 * 
 * @author John
 *
 */
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName, otherName, content, date;
	private int id, messageId, userId, otherId, state;

	/**
	 * 
	 * @param userName
	 *            用户名
	 * @param otherName
	 *            其他用户名
	 * @param content
	 *            评论内容
	 * @param date
	 *            时间
	 * @param id
	 *            评论的id
	 * @param messageId
	 *            动态内容的id
	 * @param userId
	 *            评价的用户id
	 * @param otherId
	 *            回复的用户的id
	 * @param state
	 *            回复状态
	 */
	public Comment(String userName, String otherName, String content,
			String date, int id, int messageId, int userId, int otherId,
			int state) {
		super();
		this.userName = userName;
		this.otherName = otherName;
		this.content = content;
		this.date = date;
		this.id = id;
		this.messageId = messageId;
		this.userId = userId;
		this.otherId = otherId;
		this.state = state;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getOtherId() {
		return otherId;
	}

	public void setOtherId(int otherId) {
		this.otherId = otherId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
