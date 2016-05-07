package edu.xzit.inote.model;

public class ErrorCode {
	// 成功
	public static final int SUCCESS = 42;
	// 注册失败:用户名已存在
	public static final int USER_EXIT = 44;
	// 注册失败:用户名，密码格式不正确
	public static final int NAME_PASS_ERROR = 43;
	// 用户不存在
	public static final int USER_NOT_EXIT = 44;
	// 用户密码错误
	public static final int USER_LOGIN_PASSWORD_WRONG = 45;

}
