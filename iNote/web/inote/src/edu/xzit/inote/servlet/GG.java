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

import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class GG
 */
@WebServlet("/GG")
public class GG extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GG() {
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
		System.out.println("this is a servlet");
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "";
		sql = "SELECT * from user";
		int userid=0;
		try {
			preparedStatement = connection.prepareStatement(sql);

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				  userid = resultSet.getInt("id");
			}
		} catch (Exception e) {

		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		
		Gson gson = new Gson();
		Type type = new TypeToken<Object>() {
		}.getType();
		String resJsonString = gson.toJson(String.valueOf(userid), type);
		response.getWriter().print(resJsonString+"blaballbalbla");
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
