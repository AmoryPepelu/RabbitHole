package edu.xzit.inote.app;

public class SysConfig {

	// public static String IP = "192.168.42.187";

	public static String IP = "10.20.13.120";

	public static String HTTP = "http://";
	public static String PORT = ":8080/inote/";
	public static String URL = HTTP + IP + PORT;
	private static String PIC_PATH = "images/";
	public static String PIC_URL = HTTP + IP + PORT + PIC_PATH;
	// 最大图片选择数量
	public static int MAX_IMAGE_SELECT = 2;
	// 用户名sp key
	public static String USER_NAME = "userName";
	// 用户头像
	public static String USER_HEADER_IMAGE = "userHeaderImage";
}
