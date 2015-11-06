package com.datamining.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 
 * @author sumit
 *
 */
public class DBConnection {
	public static Properties properties = null;

	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(
					properties.getProperty("db.servername"),
					properties.getProperty("db.username"),
					properties.getProperty("db.password"));
		} catch (SQLException se) {
			System.err.println("Error in Connecting to DataBase " + se.getMessage());
		}
		return conn;
	}

	public static void cleanupConnection(Statement stmt, Connection con) {
		try {
			if (null != stmt) {
				stmt.close();
			}
			if (null != con) {
				con.close();
			}
		} catch (SQLException se) {
			System.err.println("Error Releasing Connection " + se.getMessage());
		}
	}
}
