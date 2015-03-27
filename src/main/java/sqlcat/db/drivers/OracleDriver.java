package sqlcat.db.drivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import sqlcat.db.DBDriver;

public class OracleDriver extends DBDriver {
	
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (_port == null) _port = 1521;
		return DriverManager.getConnection("jdbc:oracle:thin:@" + _server + ":" + _port.toString() + ":" + _db, _login, _password);
	}

}
