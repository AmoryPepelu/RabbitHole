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

import edu.xzit.inote.model.Comment;
import edu.xzit.inote.model.HomeData;
import edu.xzit.inote.model.Message;
import edu.xzit.inote.model.User;
import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class HomeServlet
 */
@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final int pageSize = 5;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		System.out.println("HomeServlet");

		int currentPageInt = 1;
		String currentPage = request.getParameter("currentPage");
		if (currentPage != null && !"".equals(currentPage)) {
			try {
				currentPageInt = Integer.parseInt(currentPage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int startpage = (currentPageInt - 1) * pageSize;
		String userName = request.getParameter("userName");

		LinkedList<HomeData> homeList = new LinkedList<HomeData>();
		User user = null;
		// 主体
		HomeData homeData = null;
		LinkedList<Message> messages = null;
		// 获取动态列表
		messages = getMessages(userName, startpage, pageSize);

		if (messages.size() == 0) {
			// 0：用户不存在
			// 1：未关注任何人
			// 3：关注的人并没有发布任何动态
			response.getWriter().print("0");
			System.out.println("HomeServlet END");
			return;
		}

		for (Message message : messages) {
			user = getUser(message.getId());
			homeData = new HomeData(user, message);
			homeList.add(homeData);
		}
		
		Gson gson = new Gson();
		Type type = new TypeToken<LinkedList<HomeData>>() {
		}.getType();
		String resJsonString = gson.toJson(homeList, type);
		response.getWriter().print(resJsonString);
		System.out.println("HomeServlet END");

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
	 * 查询用户信息
	 * 
	 * @param messageId
	 */
	private User getUser(int messageId) {

		String sql = "SELECT id,name,nikename,picture from user where user.id="
				+ " (SELECT userid from um WHERE um.messageid=?)";

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		User user = null;
		int id = 0;
		String name = "";
		String nikename = "";
		String picture = "";

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				id = resultSet.getInt("id");
				// System.out.println("id=" + id);
				name = resultSet.getString("name");
				nikename = resultSet.getString("nikename");
				picture = resultSet.getString("picture");
				// System.out.println("user :name=" + name);
				user = new User(id, name, nikename, "", "", "", picture, "", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		return user;
	}

	/**
	 * 获取动态列表
	 * 
	 * @param userName
	 * @param startPage
	 * @param pageSize
	 * @return
	 */
	private LinkedList<Message> getMessages(String userName, int startPage,
			int pageSize) {
		LinkedList<Message> messages = new LinkedList<Message>();
		String sql = "SELECT * from message WHERE message.id in"
				+ " (SELECT um.messageid from um WHERE um.userid in"
				+ " (SELECT otherid from follow where userid="
				+ " (SELECT id from user where name=?)))" + " ORDER BY id DESC"
				+ " LIMIT ?,?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Message message = null;
		int id = 0, state = 0;
		String content = "", date = "";
		List<String> list = null;
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
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		return messages;
	}

	private void getComments(int messageId) {
		String sql = "SELECT user.id as userid,user.name,comment.content,comment.state,comment.otherid"
				+ " from user,commen  WHERE user.id=comment.userid AND comment.messageid =?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Comment comment = null;

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
	}

}
