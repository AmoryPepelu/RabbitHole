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
 * 查询是否关注了某人:userid本人id，otherid其他人的id Servlet implementation class IsFollowSB
 */
@WebServlet("/IsFollowSB")
public class IsFollowSB extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IsFollowSB() {
		super();
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

		System.out.println("IsFollowServlet");

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

		int id = -1;
		String sql = "SELECT id from follow WHERE userid = (SELECT id from user where name=?) AND otherid=?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			preparedStatement.setInt(2, otherid);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				id = resultSet.getInt("id");
			}
			// System.out.println(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		if (id != -1) {
			// 关注
			response.getWriter().print("0");
		} else {
			// 未关注
			response.getWriter().print("1");
		}
		System.out.println("IsFollowServlet END");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
