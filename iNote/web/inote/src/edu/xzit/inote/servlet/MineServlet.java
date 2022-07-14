package edu.xzit.inote.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.xzit.inote.model.User;
import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.DBUtils;

/**
 * Servlet implementation class MineServlet
 */
@WebServlet("/MineServlet")
@MultipartConfig
public class MineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MineServlet() {
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
		System.out.println("MineServlet");

		String op = request.getParameter("op");
		if ("changeNickName".equals(op)) {
			changeNickName(request, response);
		} else if ("changeSign".equals(op)) {
			changeSign(request, response);
		} else if ("headerImage".equals(op)) {
			// 修改用户头像
			upLoadHeaderImage(request, response);
		} else if ("updateView".equals(op)) {
			// 更新用户显示数据
			updateView(request, response);
		}

		System.out.println("MineServlet END");
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
	 * 改变昵称
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void changeNickName(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");

		String nickName = request.getParameter("nickName");
		System.out.println("nick name:" + nickName);

		String sql = "UPDATE `user` SET `user`.nikename=? WHERE `user`.`name` =?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, nickName);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			// 操作失败
			response.getWriter().print("1");
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		// 操作成功
		response.getWriter().print("0");
	}

	/**
	 * 修改签名
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void changeSign(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");
		String sign = request.getParameter("sign");
		String sql = "UPDATE `user` SET `user`.introduction=? WHERE `user`.`name` =?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, sign);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			// 操作失败
			response.getWriter().print("1");
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		// 操作成功
		response.getWriter().print("0");
	}

	/**
	 * 修改用户头像
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void upLoadHeaderImage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");
		String path = "";
		String imageName = AppUtil.getUUID() + ".jpg";
		for (Part part : request.getParts()) {
			// 只处理上传文件区段
			if ("content".equals(part.getName())) {
				continue;
			}
			// 图片存入服务器用getRealPath，图片从数据库取出文件名返回给客户端时用request.getContextPath() +
			// "images" + File.separator+filename
			path = request.getServletContext().getRealPath("/") + "images"
					+ File.separator + imageName;
			part.write(path);
			System.out.println("part write path:" + path);
		}
		String sql = "UPDATE `user` SET `user`.picture=? WHERE `user`.`name` =?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, imageName);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			// 操作失败
			response.getWriter().print("1");
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		response.getWriter().print(imageName);
//		System.out.println("imagename="+imageName);
	}

	/**
	 * 更新显示数据
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void updateView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");
		String sql = "SELECT * FROM `user` WHERE `user`.`name`=?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int id = 0, sex = 0;
		String name = "";
		String nikename = "";
		String introduction = "";
		String picture = "", phone = "", date = "", mail = "";
		User user = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				id = resultSet.getInt("id");
				// System.out.println("id=" + id);
				name = resultSet.getString("name");
				nikename = resultSet.getString("nikename");
				introduction = resultSet.getString("introduction");
				picture = resultSet.getString("picture");
				date = resultSet.getString("date");
				user = new User(id, name, nikename, date, "", phone, picture,
						introduction, sex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeAll(connection, preparedStatement, resultSet);
		}
		if (user == null) {
			// 获取用户信息失败
			response.getWriter().print("1");
			return;
		}
		Gson gson = new Gson();
		Type type = new TypeToken<User>() {
		}.getType();
		String resJsonString = gson.toJson(user, type);
		response.getWriter().print(resJsonString);

	}
}
