package edu.xzit.inote.servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.xzit.inote.model.ErrorCode;
import edu.xzit.inote.model.NoteBack;
import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
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
		System.out.println("LoginServlet");
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT PASSWORD from USER WHERE USER.NAME=?";
		String pw = "";

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				pw = resultSet.getString("password");
			}
		} catch (Exception e) {

		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		NoteBack noteBack = null;
		if ("".equals(pw)) {
			// 数据库无此人
			noteBack = new NoteBack("1", "用户名错误", ErrorCode.USER_NOT_EXIT);
		} else if (pw.equals(password)) {
			// 登录成功
			noteBack = new NoteBack("0", "登录成功", ErrorCode.SUCCESS);
		} else {
			// 密码错误
			noteBack = new NoteBack("1", "密码错误",
					ErrorCode.USER_LOGIN_PASSWORD_WRONG);
		}
		Gson gson = new Gson();
		Type type = new TypeToken<Object>() {
		}.getType();
		String resJsonString = gson.toJson(noteBack, type);
		response.getWriter().print(resJsonString);
		System.out.println("LoginServlet END");
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
