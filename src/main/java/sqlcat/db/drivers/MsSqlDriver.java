package sqlcat.db.drivers;

import java.sql.Connection;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import sqlcat.db.DBDriver;

public class MsSqlDriver extends DBDriver {

	@Override
	public Connection getConnection() throws SQLException {
		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(_server);
		ds.setDatabaseName(_db);
		ds.setUser(_login);
		ds.setPassword(_password);
		if (_port != null) ds.setPortNumber(_port);		
		return ds.getConnection();
	}

}
