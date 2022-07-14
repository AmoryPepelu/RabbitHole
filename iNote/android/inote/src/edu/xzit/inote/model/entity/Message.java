package edu.xzit.inote.model.entity;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id, state;
	private String content, date;
	private List<String> list;

	/**
	 * 构造函数
	 * 
	 * @param id
	 *            动态i的d
	 * @param state
	 *            状态
	 * @param content
	 *            内容
	 * @param date
	 *            时间
	 * @param list
	 *            图片列表
	 */
	public Message(int id, int state, String content, String date,
			List<String> list) {
		super();
		this.id = id;
		this.state = state;
		this.content = content;
		this.date = date;
		this.list = list;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

}
