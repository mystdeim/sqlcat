package viewtotable.db;

import java.sql.Connection;
import java.sql.SQLException;

import viewtotable.db.drivers.MsSqlDriver;
import viewtotable.db.drivers.OracleDriver;
import viewtotable.db.drivers.SqliteDriver;

public abstract class DBDriver {
	
	public static DBDriver getDriver(String type) {
		if (type.equals("mssql")) return new MsSqlDriver();
		else if (type.equals("sqlite")) return new SqliteDriver();
		else if (type.equals("oracle")) return new OracleDriver();
		else return null;
	}
	
	public void setServerName(String server) {
		_server = server;
	}
	
	public void setDatabaseName(String db) {
		_db = db;
	}
	
	public void setUser(String login) {
		_login = login;
	}
	
	public void setPassword(String password) {
		_password = password;
	}
	
	public void setPortNumber(int port) {
		_port = port;
	}
	
	public void setFileDb(String path) {
		_file_db = path;
	}

	public abstract Connection getConnection() throws SQLException;
	
	protected String _server;
	protected String _db;
	protected String _login;
	protected String _password;
	protected String _file_db;
	protected Integer _port;
	
}
