package viewtotable.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

public class Importer extends AbstractWorker {
	
	public void generate(String file_path) throws IOException, SQLException {
		try (SQLServerConnection con = (SQLServerConnection) _ds.getConnection()) {
			con.setAutoCommit(false);
			runScript(con, file_path);
		} 
	}
	
	private static final String DEFAULT_DELIMITER = ";";
	private static final int COMMIT_STEP = 1_000;

	private void runScript(Connection conn, String file_path) throws IOException, SQLException {
		startMsg();
		StringBuffer command = null;
		float line_current = 1;
		float line_count = getCountLines(file_path);
		long index_cmd = 0;
		long index_next = getRand();
		try (Reader reader = new BufferedReader(new FileReader(file_path)); 
				LineNumberReader lineReader = new LineNumberReader(reader)) {
			String line = null;
			while (_isRunning && (line = lineReader.readLine()) != null) {
				if (command == null) command = new StringBuffer();
				String trimmedLine = line.trim();
				
				if (trimmedLine.length() < 1) {
					// Do nothing
				} else if (trimmedLine.endsWith(DEFAULT_DELIMITER)) {				
					command.append(line.substring(0, line.lastIndexOf(DEFAULT_DELIMITER)));
					try (Statement statement = conn.createStatement()) {
						String cmd_sql = unescapeTxtData(command.toString());
						statement.execute(cmd_sql);
						index_cmd++;
						
						if (index_cmd > index_next) {
							index_next += getRand();
							processing(index_cmd, 100 * line_current / line_count);
						}
						
						if (index_cmd % COMMIT_STEP == 0) conn.commit();
						
						command = null;
					} catch (SQLException e) {
						throw new SQLException(String.format("%s;\n problem on %d line with:\n '%s'", e.getMessage(), 
								lineReader.getLineNumber(), command.toString()));
					}
				} else {
					command.append(line);
					command.append("\n");
				}
				line_current++;	
			}		
		}
		conn.commit();
		completedMsg(index_cmd, 100 * line_current / line_count);
	}
	
	private long getRand() {
		return (long) (10 + (Math.random() * 90));
	}
	
	private long getCountLines(String file_path) throws IOException {
		long id = 0;
		try (Reader reader = new BufferedReader(new FileReader(file_path));
				LineNumberReader lineReader = new LineNumberReader(reader)) {
			while (_isRunning && lineReader.readLine() != null) id++;
		}
		return id;
	}
	
	// Messages
	// -----------------------------------------------------------------------------------------------------------------
	
	private void startMsg() {
		for (WorkerListener listener : _listener) {
			listener.start(String.format("Import started"));
		}
	}
	
	private void processing(long n, float percent) {
		for (WorkerListener listener : _listener) {
			listener.processing(String.format("%d statement was executed (%3.1f%%)", n, percent));
		}
	}
	
	private void completedMsg(long n, float percent) {
		for (WorkerListener listener : _listener) {
			listener.completed(String.format("Import finished, %d statements were executed (%3.1f%%)", n, percent));
		}
	}	
}
