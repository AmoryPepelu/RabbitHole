package edu.xzit.inote.model;

import java.io.Serializable;

public class FollowData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 是否关注，0：关注，1，未关注
	private int userid, otherid, isFollowed;

	public FollowData(int userid, int otherid, int isFollowed) {
		super();
		this.userid = userid;
		this.otherid = otherid;
		this.isFollowed = isFollowed;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getOtherid() {
		return otherid;
	}

	public void setOtherid(int otherid) {
		this.otherid = otherid;
	}

	public int getIsFollowed() {
		return isFollowed;
	}

	public void setIsFollowed(int isFollowed) {
		this.isFollowed = isFollowed;
	}

}
