package com.bingo.qiniu.process;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bingo.qiniu.model.QiniuKey;
import com.bingo.qiniu.utils.JDBCUtil;
import com.bingo.sql.config.TableObject;
import com.bingo.sql.process.Mapper;
import com.bingo.sql.process.SqlUtil;

public class DBProcessor {

	private static final String TABLE_EXIT_SQL = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='%s' ";

	private Connection connection;

	private Mapper mapper;

	private String path;

	public DBProcessor(String path) {
		this.path = path;
		mapper = new Mapper();
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void connect() throws ClassNotFoundException, SQLException {
		connection = JDBCUtil.getSqlLiteConn(path);
		if (!keyTableExits()) {
			TableObject table = mapper.createTable(QiniuKey.class);
			String sql = SqlUtil.createTable(table);
			Statement statement = connection.createStatement();
			statement.execute(sql);
			statement.close();
		}
	}

	public boolean connected() {
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<QiniuKey> getAllQiniuKeys() {
		try {
			TableObject table = mapper.createTable(QiniuKey.class);
			String select = SqlUtil.select(table);
			Statement statement = connection.createStatement();
			ResultSet set = statement.executeQuery(select);
			List<QiniuKey> keys = new ArrayList<>();
			while (set.next()) {
				QiniuKey key = JDBCUtil.createObject(set, QiniuKey.class);
				if (key != null) {
					keys.add(key);
				}
			}
			set.close();
			statement.close();
			return keys;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void saveQiniuKey(QiniuKey key) {
		TableObject table = mapper.createTable(key);
		String sql = SqlUtil.replaceInto(table);
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.execute(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean keyTableExits() {
		TableObject table = mapper.createTable(QiniuKey.class);
		String sql = String.format(TABLE_EXIT_SQL, table.getName());
		try {
			Statement statement = connection.createStatement();

			ResultSet set = statement.executeQuery(sql);
			boolean r = false;
			if (set.next()) {
				int count = set.getInt(1);
				r = count == 1;
			}
			set.close();
			statement.close();
			return r;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
