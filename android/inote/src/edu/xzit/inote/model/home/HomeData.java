package edu.xzit.inote.model.home;

import java.io.Serializable;

import edu.xzit.inote.model.entity.Message;
import edu.xzit.inote.model.entity.User;

public class HomeData implements Serializable {

	private static final long serialVersionUID = 1L;

	private User user;
	// 动态
	private Message message;

	/**
	 * 
	 * @param user
	 *            用户信息
	 * @param message
	 *            动态内容
	 */
	public HomeData(User user, Message message) {
		super();
		this.user = user;
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

}
