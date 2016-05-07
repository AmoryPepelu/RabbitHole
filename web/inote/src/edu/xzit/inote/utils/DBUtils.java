package edu.xzit.inote.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/inote?useSSL=false";
			String user = "root";
			String password = "123456";
			return DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void closeAll(Connection connection, Statement statement,
			ResultSet resultSet) {
		 if (resultSet != null) {
             try {
                 resultSet.close();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             // 将其设置为null便于垃圾回收
             resultSet = null;
         }
         if (statement != null) {
             try {
                 statement.close();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             statement = null;
         }
         if (connection != null) {
             try {
                 connection.close();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             connection = null;
         }
	}
}
