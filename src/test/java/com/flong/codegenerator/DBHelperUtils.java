package com.flong.codegenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DBHelperUtils {
	private static final Connection conn;

	static {
		try {
			String driverClass = PropertiesHelper.get("jdbc.driver");
			String connectionUrl = PropertiesHelper.get("jdbc.url");
			String username = PropertiesHelper.get("jdbc.username");
			String password = PropertiesHelper.get("jdbc.password");
			Class.forName(driverClass);
			conn = DriverManager.getConnection(connectionUrl, username, password);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ResultSet query(String sql, List<Object> params) {
		System.out.println("sql: " + sql);
		System.out.println("params: " + params);
		try {
			PreparedStatement psmt = conn.prepareStatement(sql);
			if(params != null) {
				for (int i = 0; i < params.size(); i++) {
					psmt.setObject(i+1, params.get(i));
				}
			}	
			return psmt.executeQuery();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void update(String sql, List<Object> params) {
		System.out.println("sql: " + sql);
		System.out.println("params: " + params);
		try {
			PreparedStatement psmt = conn.prepareStatement(sql);
			if(params != null) {
				for (int i = 0; i < params.size(); i++) {
					psmt.setObject(i+1, params.get(i));
				}
			}	
			psmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
