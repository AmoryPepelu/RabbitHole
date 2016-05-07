package edu.xzit.inote.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class FF
 */
@WebServlet("/FF")
@MultipartConfig
public class FF extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FF() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// E:\workspace\Biyesheji\picture

		System.out.println("begin");
		String paString = request.getContextPath();
		System.out.println("path=" + paString);
		for (Part part : request.getParts()) {
			// 只处理上传文件区段

			part.write("E:\\aa.png");
			System.out.println("part write");
			// if (part.getName().startsWith("file")) {
			// String fileName = getFileName(part);
			// part.write(fileName);
			// }
		}
		System.out.println("end");
	}

	private String getFileName(Part part) {
		String header = part.getHeader("Content-Disposition");
		String fileName = header.substring(header.indexOf("filename=\"") + 10,
				header.lastIndexOf("\""));
		header.lastIndexOf("\"");
		return fileName;
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
