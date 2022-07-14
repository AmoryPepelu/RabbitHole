package edu.xzit.inote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.xzit.inote.utils.DBUtils;

/**
 * 删除动态 Servlet implementation class MessageServlet
 */
@WebServlet("/MessageServlet")
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MessageServlet() {
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

		System.out.println("MessageServlet");

		int messageIdInt = -1;
		String messageId = request.getParameter("messageId");
		if (messageId != null && !"".equals(messageId)) {
			try {
				messageIdInt = Integer.parseInt(messageId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String op = request.getParameter("op");
		if ("d".equals(op)) {
			deleteMessage(messageIdInt);
			deleteUM(messageIdInt);
		}
		//删除成功
		response.getWriter().print("0");
		System.out.println("MessageServlet END");
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
	 * 删除动态
	 * 
	 * @param messageId
	 *            动态id
	 */
	private void deleteMessage(int messageId) {
		String sql = "DELETE  from message WHERE message.id=?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}

	}

	/**
	 * 从um关系表中删除数据
	 * 
	 * @param messageId
	 */
	private void deleteUM(int messageId) {
		String sql = "DELETE from um where um.messageid=?";

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
	}

}
