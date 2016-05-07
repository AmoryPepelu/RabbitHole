package edu.xzit.inote.servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.xzit.inote.model.FindData;
import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class FindServlet
 */
@WebServlet("/FindServlet")
public class FindServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final int pageSize = 5;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FindServlet() {
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

		System.out.println("FindServlet");
		int currentPageInt = 1;

		// 搜索条件：1.非本人 2.都是没有关注过的人3.每页五条数据
		String sql = "SELECT user.id,user.NAME,user.nikename,user.introduction,user.picture from user"
				+ " WHERE user.id not in (SELECT id from user WHERE name = ?)"
				+ "  and user.id not in"
				+ " (SELECT otherid from follow WHERE userid = "
				+ " (SELECT id from user WHERE name =?))" + " limit ?,?";

		String currentPage = request.getParameter("currentPage");
		if (currentPage != null && !"".equals(currentPage)) {
			try {
				currentPageInt = Integer.parseInt(currentPage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int startpage = (currentPageInt - 1) * pageSize;

		// System.out.println("start page =" + startpage);

		String userName = request.getParameter("userName");

		// System.out.println("userName=" + userName);

		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		ArrayList<FindData> list = new ArrayList<FindData>();
		FindData findData = null;
		int id = 0;
		String name = "";
		String nikename = "";
		String introduction = "";
		String picture = "";

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			preparedStatement.setString(2, userName);
			preparedStatement.setInt(3, startpage);
			preparedStatement.setInt(4, pageSize);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				id = resultSet.getInt("id");
				// System.out.println("id=" + id);
				name = resultSet.getString("name");
				nikename = resultSet.getString("nikename");
				introduction = resultSet.getString("introduction");
				picture = resultSet.getString("picture");
				findData = new FindData(id, name, nikename, introduction,
						picture);
				list.add(findData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<FindData>>() {
		}.getType();
		String resJsonString = gson.toJson(list, type);
		response.getWriter().print(resJsonString);
		System.out.println("FindServlet END");
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
