package edu.xzit.inote.model.find;

import java.io.Serializable;

/**
 * 发现页面数据
 * 
 * @author John
 *
 */
public class FindData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name, nikename, introduction, picture;
	private int id;

	public FindData(int id, String name, String nikename, String introduction,
			String picture) {
		super();
		this.id = id;
		this.name = name;
		this.nikename = nikename;
		this.introduction = introduction;
		this.picture = picture;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNikename() {
		return nikename;
	}

	public void setNikename(String nikename) {
		this.nikename = nikename;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

}
