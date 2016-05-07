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
 * 对评论的操作 Servlet implementation class CommentServlet 0:删除成功
 */
@WebServlet("/CommentServlet")
public class CommentServlet extends HttpServlet {

	private final String TAG = CommentServlet.class.getSimpleName();
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentServlet() {
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

		System.out.println("CommentServlet");

		// 操作类型:d:delete,reply:回复他人,com:仅仅是评论
		String op = request.getParameter("op");

		// 事件分发
		if ("d".equals(op)) {
			// 删除评论
			deleteComment(request, response);
		} else if ("reply".equals(op)) {
			// 回复他人评论
			replyComment(request, response);
		} else if ("com".equals(op)) {
			// 发表评论
			postComment(request, response);
		}

		System.out.println("CommentServlet END");
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
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void deleteComment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		int commentIdInt = -1;

		String commentId = request.getParameter("commentId");
		if (commentId != null && !"".equals(commentId)) {
			try {
				commentIdInt = Integer.parseInt(commentId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String sql = "DELETE FROM comment WHERE comment.id=?";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, commentIdInt);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print(e.getMessage());
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		// 删除成功
		response.getWriter().print("0");
	}

	/**
	 * 发表评论，用post请求，防止有空格
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void postComment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		int messageIdInt = -1;
		String messageId = request.getParameter("messageId");
		if (messageId != null && !"".equals(messageId)) {
			try {
				messageIdInt = Integer.parseInt(messageId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String userName = request.getParameter("userName");

		int stateInt = -1;
		String state = request.getParameter("state");
		if (state != null && !"".equals(state)) {
			try {
				stateInt = Integer.parseInt(state);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String content = request.getParameter("content");
		System.out.println(TAG + ",postComment,messageIdInt=" + messageIdInt
				+ ",userName=" + userName + ",state=" + stateInt + ",content="
				+ content);

		String sql = "INSERT INTO `comment`(messageid,`comment`.userid,`comment`.content,`comment`.state,`comment`.date) "
				+ "VALUES(?,(SELECT id from `user` WHERE `user`.`name`=?),?,?,?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, messageIdInt);
			preparedStatement.setString(2, userName);
			preparedStatement.setString(3, content);
			preparedStatement.setInt(4, stateInt);
			preparedStatement.setString(5, AppUtil.getDateStringWithSecond());

			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		// 插入成功
		response.getWriter().print("0");
	}

	/**
	 * 回复评论，用post请求，防止有空格
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void replyComment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		int messageIdInt = -1;
		String messageId = request.getParameter("messageId");
		if (messageId != null && !"".equals(messageId)) {
			try {
				messageIdInt = Integer.parseInt(messageId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 被回复的人的id
		int otherIdInt = -1;
		String otherId = request.getParameter("otherId");
		if (otherId != null && !"".equals(otherId)) {
			try {
				otherIdInt = Integer.parseInt(otherId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String content = request.getParameter("content");
		String userName = request.getParameter("userName");
		// 回复他人固定为1
		int state = 1;

		System.out.println(TAG + ",replyComment messageIdInt=" + messageIdInt
				+ ",otherIdInt=" + otherIdInt + ",content=" + content
				+ ",userName=" + userName);

		String sql = " INSERT INTO `comment`(`comment`.messageid,`comment`.userid,`comment`.otherid,`comment`.content,`comment`.date,`comment`.state) "
				+ " VALUES(?,(SELECT `user`.id FROM `user` WHERE "
				+ " `user`.name=?),?,?,?,?)";
		Connection connection = DBUtils.getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			// message id
			preparedStatement.setInt(1, messageIdInt);
			// user name
			preparedStatement.setString(2, userName);
			// other id
			preparedStatement.setInt(3, otherIdInt);
			// content
			preparedStatement.setString(4, content);
			// date
			preparedStatement.setString(5, AppUtil.getDateStringWithSecond());
			// state
			preparedStatement.setInt(6, state);

			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			// 回复失败
			response.getWriter().print("1");
			return;
		} finally {
			DBUtils.closeAll(connection, preparedStatement, null);
		}
		// 回复成功
		response.getWriter().print("0");
	}

}
