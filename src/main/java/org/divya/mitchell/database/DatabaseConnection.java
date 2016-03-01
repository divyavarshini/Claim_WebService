package org.divya.mitchell.database;

import java.sql.*;

public class DatabaseConnection {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String URL = "jdbc:mysql://localhost:3306/mitchellclaim";
	
	static final String USER = "root";
	static final String PSWD = "123456";
	
	
	public Connection getConnection() {
		Connection conn = null;
		try{
			Class.forName(JDBC_DRIVER).newInstance();
			conn = DriverManager.getConnection(URL, USER, PSWD);
		}catch(SQLException e){
			e.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		} catch (IllegalAccessException il) {
			il.printStackTrace();
		} catch (ClassNotFoundException ce) {
			ce.printStackTrace();
		}
		return conn;
	}
	
	
	
}
