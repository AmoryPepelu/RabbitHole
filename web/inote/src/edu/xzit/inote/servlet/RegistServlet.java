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
import edu.xzit.inote.model.User;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class RegistServlet
 */
@WebServlet("/RegistServlet")
public class RegistServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegistServlet() {
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

		System.out.println("RegistServlet");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "";
		sql = "SELECT id from user WHERE user.name=?";
		int userid = -1;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				userid = resultSet.getInt("id");
			}
		} catch (Exception e) {

		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		if (userid > 0) {
			// 注册失败
			NoteBack noteBack = new NoteBack("1", "注册失败，用户名已存在",
					ErrorCode.USER_EXIT);
			Gson gson = new Gson();
			Type type = new TypeToken<NoteBack>() {
			}.getType();
			String resJsonString = gson.toJson(noteBack, type);
			response.getWriter().print(resJsonString);
			System.out.println("注册失败" + "user name=" + userName + ",password="
					+ password + ",json result=" + resJsonString);
		} else {

			User user = new User(-1, userName, null, AppUtil.getDateString(),
					password, null, null, null, 1);
			insertUser(user);
			// 注册成功
			NoteBack noteBack = new NoteBack("0", "注册成功", ErrorCode.SUCCESS);
			Gson gson = new Gson();
			Type type = new TypeToken<Object>() {
			}.getType();
			String resJsonString = gson.toJson(noteBack, type);
			response.getWriter().print(resJsonString);
			System.out.println("注册成功" + "user name=" + userName + ",password="
					+ password + ",json result=" + resJsonString);
		}
		System.out.println("user id=" + userid);
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
	 * 向数据库中插入数据
	 * 
	 * @param user
	 */
	private void insertUser(User user) {
		String SQL = "insert into user(name,date,PASSWORD,sex) VALUES(?,?,?,?)";

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(SQL);
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getDate());
			preparedStatement.setString(3, user.getPassword());
			preparedStatement.setInt(4, user.getSex());
			preparedStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
	}
}
