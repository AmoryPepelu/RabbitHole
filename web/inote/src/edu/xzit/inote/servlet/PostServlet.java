package edu.xzit.inote.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class PostServlet
 * 
 */
@WebServlet("/PostServlet")
@MultipartConfig
public class PostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PostServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		System.out.println("PostServlet");

		String userNameString = request.getParameter("userName");
		System.out.println("user name="+userNameString);
		
		if (userNameString == null || "".equals(userNameString)) {
			// 用户名不合法
			response.getWriter().print("1");
			System.out.println("PostServlet END 用户名不合法");
			return;
		}

		String op = request.getParameter("op");

		String path = request.getServletContext().getRealPath("/");

		System.out.println("getRealPath : " + path);

		System.out.println(request.getContextPath());
		if ("file".equals(op)) {
			postWithPics(request, response);
		} else {
			postWhitoutPics(request, response);
		}
		System.out.println("PostServlet END");
	}

	/**
	 * 有图片的动态发布
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void postWithPics(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 用户名
		String userName = request.getParameter("userName");
		// 发布内容
		String content = request.getParameter("content");
		// 图片数量
		String fileNumString = request.getParameter("fileNum");
		int fileNumInt = 0;
		if (fileNumString != null && !"".equals(fileNumString)) {
			try {
				fileNumInt = Integer.parseInt(fileNumString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String path = "";
		String[] picNames = new String[3];
		for (int i = 0; i < picNames.length; i++) {
			picNames[i] = AppUtil.getUUID() + ".jpg";
		}
		int i = 0;
		for (Part part : request.getParts()) {
			// 只处理上传文件区段
			if ("content".equals(part.getName())) {
				continue;
			}
			// 图片存入服务器用getRealPath，图片从数据库取出文件名返回给客户端时用request.getContextPath() +
			// "images" + File.separator+filename
			path = request.getServletContext().getRealPath("/") + "images"
					+ File.separator + picNames[i];
			part.write(path);
			i++;
			System.out.println("part write path:" + path);
		}

		switch (fileNumInt) {
		case 0:
			// 没有图片的
			insertIntoMessage(content);
			break;
		case 1:
			insertIntoMessage(content, picNames[0]);
			break;
		case 2:
			insertIntoMessage(content, picNames[0], picNames[1]);
			break;
		case 3:
			insertIntoMessage(content, picNames[0], picNames[1], picNames[2]);
			break;
		default:
			break;
		}

		// 先把数据插入message表中，再从message表中检索出最新的id，把message.id与user.id配合插入um表中
		int messageId = getLatestMessageId();
		boolean isSuccess = insertIntoUM(messageId, userName);
		if (isSuccess) {
			// 操作成功，返回：0
			response.getWriter().print("0");

		} else {
			// 操作失败，返回：1
			response.getWriter().print("1");
		}

		System.out.println("content : " + content);
	}

	/**
	 * 有三张图片的
	 * 
	 * @param content
	 * @param pic1
	 * @param pic2
	 * @param pic3
	 */
	private void insertIntoMessage(String content, String pic1, String pic2,
			String pic3) {
		String sql = "INSERT INTO message(message.content,message.date,message.pic1,message.pic2,message.pic3)"
				+ " VALUES(?,?,?,?,?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, content);
			preparedStatement.setString(2, AppUtil.getDateStringWithSecond());
			preparedStatement.setString(3, pic1);
			preparedStatement.setString(4, pic2);
			preparedStatement.setString(5, pic3);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
	}

	/**
	 * 有两张图片的
	 * 
	 * @param content
	 * @param pic1
	 * @param pic2
	 */
	private void insertIntoMessage(String content, String pic1, String pic2) {
		String sql = "INSERT INTO message(message.content,message.date,message.pic1,message.pic2)"
				+ " VALUES(?,?,?,?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, content);
			preparedStatement.setString(2, AppUtil.getDateStringWithSecond());
			preparedStatement.setString(3, pic1);
			preparedStatement.setString(4, pic2);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
	}

	/**
	 * 有一张图片的
	 * 
	 * @param content
	 * @param pic1
	 */
	private void insertIntoMessage(String content, String pic1) {
		String sql = "INSERT INTO message(message.content,message.date,message.pic1)"
				+ " VALUES(?,?,?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, content);
			preparedStatement.setString(2, AppUtil.getDateStringWithSecond());
			preparedStatement.setString(3, pic1);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
	}

	/**
	 * 没有图片的
	 * 
	 * @param content
	 */
	private void insertIntoMessage(String content) {
		String sql = "INSERT INTO message(message.content,message.date)"
				+ " VALUES(?,?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, content);
			preparedStatement.setString(2, AppUtil.getDateStringWithSecond());
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
	}

	/**
	 * 获取最新的message的id
	 * 
	 * @return
	 */
	private int getLatestMessageId() {
		int messageId = -1;
		String sql = "SELECT message.id from message ORDER BY message.id DESC LIMIT 0,1";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				messageId = resultSet.getInt("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return messageId;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		return messageId;
	}

	/**
	 * 把message.id与user.id关联插入表um
	 * 
	 * @param messageId
	 *            动态的id
	 * @param userName
	 *            用户名
	 * @return 插入成功，返回：true；失败，返回：false
	 */
	private boolean insertIntoUM(int messageId, String userName) {
		String sql = "INSERT INTO um(um.messageid,um.userid) VALUES(?,(SELECT `user`.id FROM `user` WHERE `user`.`name`=?))";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		return true;
	}

	/**
	 * 无图片的动态发布
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void postWhitoutPics(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 用户名
		String userName = request.getParameter("userName");
		// 发布内容
		String content = request.getParameter("content");
		// 没有图片的
		insertIntoMessage(content);
		// 先把数据插入message表中，再从message表中检索出最新的id，把message.id与user.id配合插入um表中
		int messageId = getLatestMessageId();
		boolean isSuccess = insertIntoUM(messageId, userName);
		if (isSuccess) {
			// 操作成功，返回：0
			response.getWriter().print("0");
		} else {
			// 操作失败，返回：1
			response.getWriter().print("1");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
