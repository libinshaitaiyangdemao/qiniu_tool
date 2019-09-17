package com.bingo.qiniu.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

import com.bingo.sql.utils.Reflections;

public class JDBCUtil {

	/**
	 * 创建sqlLite文件
	 * 
	 * @return
	 */
	public static String creatSqlLiteDb(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
			file = new File(path);
			file.createNewFile();
		} else {
			file = new File(path);
		}
		return file.getPath();
	}

	/**
	 * 获取sqlLite连接对象
	 * 
	 * @param FilePath
	 * @return
	 * @throws ClassNotFoundException
	 * @throws java.sql.SQLException
	 */
	public static Connection getSqlLiteConn(String FilePath) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection("jdbc:sqlite:" + FilePath);
	}

	public static Connection getMysqlConn(String url, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * 关闭Connection
	 * 
	 * @param conn
	 */
	public static void closeConn(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭Statement
	 * 
	 * @param stat
	 */
	public static void closeStatement(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭ResultSet
	 * 
	 * @param res
	 */
	public static void closeResultSet(ResultSet res) {
		if (res != null) {
			try {
				res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开启事务
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public static void openTransaction(Connection conn) throws SQLException {
		if (conn != null && conn.getAutoCommit()) {
			conn.setAutoCommit(false);
		}
	}

	/**
	 * 提交事务
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public static void commitTransaction(Connection conn) throws SQLException {
		if (conn != null && !conn.getAutoCommit()) {
			conn.commit();
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public static void rollbackTransaction(Connection conn) throws SQLException {
		if (conn != null && !conn.getAutoCommit()) {
			conn.rollback();
		}
	}

	/*
	 * 返回长度为【strLength】的随机数，在前面补0
	 */
	public static String getFixLenthString(int strLength) {
		Random rm = new Random();
		// 获得随机数
		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
		// 将获得的获得随机数转化为字符串
		String fixLenthString = String.valueOf(pross);
		// 返回固定的长度的随机数
		return fixLenthString.substring(1, strLength + 1);
	}

	public static <T> T createObject(ResultSet result, Class<T> clazs) throws SQLException, IllegalArgumentException, IllegalAccessException {
		Object object = Reflections.createObject(clazs);
		List<Field> fields = Reflections.getAccessibleFields(clazs);
		for (Field field : fields) {
			Object val = null;
			if (field.getType().equals(Integer.class)) {
				val = result.getInt(field.getName());
			} else if (field.getType().equals(String.class)) {
				val = result.getString(field.getName());
				if (val != null) {
					val = ((String) val).trim();
				}
			}
			if (val != null) {
				field.set(object, val);
			}
		}
		return (T) object;
	}

}
