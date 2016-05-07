package edu.xzit.inote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.xzit.inote.utils.AppUtil;
import edu.xzit.inote.utils.DBUtils;

/**
 * 点赞，取消赞 Servlet implementation class LikeServlet code : 0: 删除赞成功,
 * 1:删除赞失败,3:增加赞成功,4:增加赞失败
 */
@WebServlet("/LikeServlet")
public class LikeServlet extends HttpServlet {

	private final String TAG = LikeServlet.class.getSimpleName();

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LikeServlet() {
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

		System.out.println("LikeServlet");
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

		String userName = request.getParameter("userName");
		// 操作类型：del：删除赞，add添加赞
		String op = request.getParameter("op");

		System.out.println(TAG + ",userName=" + userName + "messageId="
				+ messageIdInt + ",op=" + op);

		if ("del".equals(op)) {
			if (delLike(messageIdInt, userName)) {
				// 删除成功
				response.getWriter().print("0");
			} else {
				// 删除失败
				response.getWriter().print("1");
			}

		} else if ("add".equals(op)) {
			if (addLike(messageIdInt, userName)) {
				// 增加成功
				response.getWriter().print("3");
			} else {
				// 增加失败
				response.getWriter().print("4");
			}

		}

		System.out.println("LikeServlet END");
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
	 * 添加赞
	 * 
	 * @param messageId
	 * @param userName
	 * @return boolean 成功：true ，失败：false
	 */
	private boolean addLike(int messageId, String userName) {
		String sql = "INSERT INTO `like`(`like`.messageid,`like`.userid,`like`.date)"
				+ " VALUES(?,(SELECT `user`.id FROM `user` WHERE `user`.`name`=?),?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			preparedStatement.setString(2, userName);
			preparedStatement.setString(3, AppUtil.getDateStringWithSecond());
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		// 增加成功
		return true;
	}

	/**
	 * 删除赞
	 * 
	 * @param messageId
	 * @param userName
	 */
	private boolean delLike(int messageId, String userName) {
		String sql = "DELETE FROM `like` WHERE `like`.messageid=?"
				+ " AND `like`.userid=(SELECT id FROM `user` WHERE `user`.`name`=?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageId);
			preparedStatement.setString(2, userName);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		// 增加成功
		return true;
	}
}
