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

import edu.xzit.inote.utils.DBUtils;

/**
 * 加载赞列表
 * Servlet implementation class LoadLikesServlet
 */
@WebServlet("/LoadLikesServlet")
public class LoadLikesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadLikesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		System.out.println("LoadLikesServlet");
		// 只要获得message的id
		int messageIdInt = 1;
		String messageIdString = request.getParameter("messageId");
		if (messageIdString != null && !"".equals(messageIdString)) {
			try {
				messageIdInt = Integer.parseInt(messageIdString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String sql = "SELECT `user`.`name` from `user`,`like` where `like`.userid=`user`.id AND `like`.messageid=?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		LinkedList<String> likes = new LinkedList<String>();
		String name;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageIdInt);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				// System.out.println("id=" + id);
				name = resultSet.getString("name");
				// System.out.println("user :name=" + name);
				likes.add(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		Gson gson = new Gson();
		Type type = new TypeToken<LinkedList<String>>() {
		}.getType();
		String resJsonString = gson.toJson(likes, type);
		response.getWriter().print(resJsonString);
		System.out.println("LoadLikesServlet END");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}

}
