package edu.xzit.inote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.xzit.inote.utils.DBUtils;

/**
 * 取消关注某人 Servlet implementation class CancelFollowSB
 */
@WebServlet("/CancelFollowSB")
public class CancelFollowSB extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CancelFollowSB() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		System.out.println("CancelFollowSB");

		int otherid = 0;
		String userName = request.getParameter("userName");
		String otherIdString = request.getParameter("otherId");
		if (otherIdString != null && !"".equals(otherIdString)) {
			try {
				otherid = Integer.parseInt(otherIdString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String sql = "delete from follow where userid =(SELECT id from user where name=?) and otherid =?";

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			preparedStatement.setInt(2, otherid);
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
		System.out.println("CancelFollowSB END");
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
