package edu.xzit.inote.model;

import java.io.Serializable;

public class Like implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id, messgaeId, userId;
	private String date, userName;

	/**
	 * 喜欢、赞列表
	 * 
	 * @param id
	 * @param messgaeId
	 * @param userId
	 * @param userName
	 * @param date
	 */
	public Like(int id, int messgaeId, int userId, String userName, String date) {
		super();
		this.id = id;
		this.messgaeId = messgaeId;
		this.userId = userId;
		this.userName = userName;
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMessgaeId() {
		return messgaeId;
	}

	public void setMessgaeId(int messgaeId) {
		this.messgaeId = messgaeId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
