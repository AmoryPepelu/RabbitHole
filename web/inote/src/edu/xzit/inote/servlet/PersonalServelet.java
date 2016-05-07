package edu.xzit.inote.servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.xzit.inote.model.Message;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class PersonalServelet
 */
@WebServlet("/PersonalServelet")
public class PersonalServelet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final int pageSize = 5;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PersonalServelet() {
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

		System.out.println("PersonalServelet");

		String op = request.getParameter("op");
		if ("getMessages".equals(op)) {
			// 获取用户发布的动态
			getMessages(request, response);
		} else if ("followsb".equals(op)) {
			// 关注某人
			followSB(request, response);
		} else if ("canclefollow".equals(op)) {
			// 取消关注某人
			cancleFollowSB(request, response);
		}

		System.out.println("PersonalServelet END");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * 获取用户动态信息:1:查询错误;2:内容为空
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getMessages(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int currentPageInt = 1;
		String currentPage = request.getParameter("currentPage");
		if (currentPage != null && !"".equals(currentPage)) {
			try {
				currentPageInt = Integer.parseInt(currentPage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int startPage = (currentPageInt - 1) * pageSize;
		String userName = request.getParameter("userName");
		String sql = "SELECT * from message WHERE message.id in "
				+ " (SELECT um.messageid FROM um WHERE um.userid ="
				+ " (SELECT `user`.id from user WHERE `user`.`name`=?)) ORDER BY id DESC LIMIT ?,?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Message message = null;
		int id = 0, state = 0;
		String content = "", date = "";
		List<String> list = null;
		LinkedList<Message> messages = new LinkedList<Message>();
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			preparedStatement.setInt(2, startPage);
			preparedStatement.setInt(3, pageSize);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				list = new LinkedList<String>();
				content = resultSet.getString("content");
				date = resultSet.getString("date");
				id = resultSet.getInt("id");
				state = resultSet.getInt("state");
				list.add(resultSet.getString("pic1"));
				list.add(resultSet.getString("pic2"));
				list.add(resultSet.getString("pic3"));
				message = new Message(id, state, content, date, list);
				messages.add(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("1");
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		if (messages.size() == 0) {
			response.getWriter().print("2");
			return;
		}
		Gson gson = new Gson();
		Type type = new TypeToken<LinkedList<Message>>() {
		}.getType();
		String resJsonString = gson.toJson(messages, type);
		response.getWriter().print(resJsonString);
	}

	/**
	 * 关注某人
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void followSB(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// APP使用者的用户名
		String userName = request.getParameter("userName");
		// 被关注的用户
		String otherUserName = request.getParameter("otherUserName");
		String sql = "insert into follow(userid,otherid,date) VALUES((SELECT id FROM user where name=?),(SELECT id FROM user where name=?),?)";

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, otherUserName);
			preparedStatement.setString(3, AppUtil.getDateStringWithSecond());
			preparedStatement.execute();

		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("1");
			System.out.println("FollowServlet Exception END");
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		// 回调，0：成功，1失败
		response.getWriter().print("0");
	}

	/**
	 * 取消关注某人
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cancleFollowSB(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// APP使用者的用户名
		String userName = request.getParameter("userName");
		// 被关注的用户
		String otherUserName = request.getParameter("otherUserName");
		String sql = "delete from follow where userid =(SELECT id from user where name=?) and otherid =(SELECT id from user where name=?)";

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, otherUserName);
			preparedStatement.execute();

		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("3");
			System.out.println("FollowServlet Exception END");
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		// 回调，2：成功，3失败
		response.getWriter().print("2");
	}
}
