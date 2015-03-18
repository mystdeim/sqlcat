package viewtotable.util;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public abstract class AbstractWorker {
	
	enum TYPE_DB {
		MSSQL, MYSQL
	}
	
	public AbstractWorker() {
		_ds = new SQLServerDataSource();
	}
	
	public void setParams(String server, String db, String login, String pass, String port, String type_db) {
		_ds.setServerName(server);
		_ds.setDatabaseName(db);
		_ds.setUser(login);
		_ds.setPassword(pass);
		if (port != null && !port.isEmpty()) {
			_ds.setPortNumber(Integer.valueOf(port));
		}
		if (type_db.equals("mssql")) _type_db = TYPE_DB.MSSQL;
		else if (type_db.equals("mysql")) _type_db = TYPE_DB.MYSQL;
	}
	
	public void setRunning(boolean flag) {
		_isRunning = flag;
	}

	protected SQLServerDataSource _ds;
	protected volatile boolean _isRunning;
	protected TYPE_DB _type_db;
	
	protected String humanReadableSize(final long bytes) {
		final int unit = 1000;
		if (bytes < unit) return bytes + " b";
		final short exp = (short) (Math.log(bytes) / Math.log(unit));
		return String.format("%.1f %sb", bytes / Math.pow(unit, exp), "kMGP".charAt(exp - 1));
	}	
	
	protected String escapeTxtData(String txt) {
		if (null != txt) return txt.replaceAll("\r\n", "\n").replaceAll(";\n", ";/\n");
		return txt;
	}
	
	protected String unescapeTxtData(String txt) {
		if (null != txt) return txt.replaceAll(";/\n", ";\n");
		return txt;
	}
	
	// Listener
	// -----------------------------------------------------------------------------------------------------------------
	
	protected List<WorkerListener> _listener = new ArrayList<>();
	
	public void addGeneratorListener(WorkerListener listener) {
		_listener.add(listener);
	}
	
	public void removeGeneratorListener(WorkerListener listener) {
		_listener.remove(listener);
	}
}
