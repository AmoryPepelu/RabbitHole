package edu.xzit.inote.servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.xzit.inote.model.Comment;
import edu.xzit.inote.utils.DBUtils;

/**
 * 加载评论 Servlet implementation class LoadCommentServlet
 * 如果没有评论，则返回0
 */
@WebServlet("/LoadCommentServlet")
public class LoadCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoadCommentServlet() {
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

		System.out.println("LoadCommentServlet");

		int messageIdInt = -1;
		String messageId = request.getParameter("messageId");
		if (messageId != null && !"".equals(messageId)) {
			try {
				messageIdInt = Integer.parseInt(messageId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LinkedList<Comment> comments = getComments(messageIdInt);
		if (comments == null || comments.size() == 0) {
			// 无评论
			response.getWriter().print("0");
			System.out.println("LoadCommentServlet END");
			return;
		}

		for (Comment c : comments) {
			//有回复
			if (c.getState() == 1) {
				String otherName = getOtherName(c.getOtherId());
				c.setOtherName(otherName);
			}
		}
		Gson gson = new Gson();
		Type type = new TypeToken<LinkedList<Comment>>() {
		}.getType();
		String resJsonString = gson.toJson(comments, type);
		response.getWriter().print(resJsonString);
		System.out.println("LoadCommentServlet END");
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
	 * 获取用户名
	 * 
	 * @param otherId
	 * @return
	 */
	private String getOtherName(int otherId) {
		// System.out.println("getOtherName:other id=" + otherId);
		String otherName = "";
		String sql = "SELECT `user`.`name` FROM `user` WHERE `user`.id=?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, otherId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				otherName = resultSet.getString("name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		return otherName;
	}

	/**
	 * 获取评论列表
	 * 
	 * @param messageId
	 * @return
	 */
	private LinkedList<Comment> getComments(int messageId) {
		LinkedList<Comment> comments = new LinkedList<Comment>();

		String sql = "SELECT `comment`.id,`comment`.userid,`comment`.otherid,`comment`.content,`comment`.state,`user`.`name`"
				+ " from `comment`,user where `comment`.userid=`user`.id AND messageid=?"
				+ " ORDER BY id";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Comment comment = null;
		String userName = "", otherName = "", content = "", date = "";
		int id = 0, userId = 0, otherId = 0, state = 0;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				content = resultSet.getString("content");
				userName = resultSet.getString("name");
				id = resultSet.getInt("id");
				otherId = resultSet.getInt("otherid");
				state = resultSet.getInt("state");
				userId = resultSet.getInt("userid");
				comment = new Comment(userName, otherName, content, date, id,
						messageId, userId, otherId, state);
				comments.add(comment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		return comments;
	}
}
