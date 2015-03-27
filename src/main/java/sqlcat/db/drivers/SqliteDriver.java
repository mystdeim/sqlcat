package sqlcat.db.drivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import sqlcat.db.DBDriver;

public class SqliteDriver extends DBDriver {
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + _file_db);
	}

}
