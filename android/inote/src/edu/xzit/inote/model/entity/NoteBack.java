package edu.xzit.inote.model.entity;

import java.io.Serializable;

/**
 * 返回结果
 * 
 * @author John
 *
 */
public class NoteBack implements Serializable {

	private static final long serialVersionUID = 1L;
	// 返回识别码：0：成功，1：失败
	String code;
	String message;
	//错误识别码
	int errorCode;

	public NoteBack(String code, String message,int errorCode) {
		this.code = code;
		this.message = message;
		this.errorCode=errorCode;
	}

	public String getCode() {
		return code;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
