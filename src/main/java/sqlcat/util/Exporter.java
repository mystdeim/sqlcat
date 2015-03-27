package sqlcat.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exporter extends AbstractWorker {
	
	private static final int MAX_FETCH_SIZE = 100;
	
	public void generate(String objects_name, String file_path, boolean all, boolean table_def) throws IOException, SQLException {
		Charset charset = Charset.forName("utf-8");
		Path path = Paths.get(file_path);
		try (Connection con = _ds.getConnection();
				BufferedWriter writer = Files.newBufferedWriter(path, charset)) {	
			con.setAutoCommit(false);
			
			String[] objects = null;
			if (all) {
				DatabaseMetaData meta = con.getMetaData();
				ResultSet rs = meta.getTables(null, "dbo", "%", new String[] {"TABLE", "VIEW"});
				List<String> tmp_arr = new ArrayList<>();
				while (rs.next()) tmp_arr.add(rs.getString(3));
				objects = tmp_arr.toArray(new String[tmp_arr.size()]);
			} else {
				objects = objects_name.split(",");		
			}
			if (null != objects) {
				for (String object : objects) {	
					// to avoid read the entire result into memory
					generate(object.trim(), con, writer, table_def);
				}
			}
		} 
		
		completed(String.format("File '%s' created, size: %s", file_path, humanReadableSize(Files.size(path))));
	}	
	
	private void generate(String table_name, Connection con, Writer writer, boolean table_def) throws SQLException, IOException {
		_types = new HashMap<String, String>();		
		String sql = "select * from " + table_name;			
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			startMsg(table_name);
			
			if (isStopped()) return;
			ResultSet rs = ps.executeQuery();
			
			if (isStopped()) return;
			Long row_count = getCount(table_name, con);
			foundMsg(table_name, row_count);

			if (isStopped()) return;

			ResultSetMetaData meta = rs.getMetaData();
			for (int i=1; i<=meta.getColumnCount(); i++) {
				_types.put(meta.getColumnName(i),  meta.getColumnTypeName(i));
			}
			if (table_def) createTable(table_name, rs, writer, meta);
			
			long row_exported = 0;
			if (null != row_count) {
				row_exported = createInserts(table_name, rs, row_count, writer);
			}
			finishMsg(table_name, row_exported, row_count);
		}
	}
	
	private long getCount(String table_name, Connection con) throws SQLException, IOException {
		String sql = "select count(*) from " + table_name;			
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setFetchSize(MAX_FETCH_SIZE);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {				
				return rs.getLong(1);
			}
		}
		return 0;
	}
	
	private void createTable(String table_name, ResultSet rs, Writer writer, ResultSetMetaData meta) throws SQLException, IOException {
		writer.write(String.format(
				"if exists (select * from INFORMATION_SCHEMA.TABLES where TABLE_NAME = '%1$s') drop table %1$s;\n", 
				table_name));
		writer.write(String.format("create table %s (\n", table_name));
		for (int i=1; i<=meta.getColumnCount(); i++) {
			writer.write(String.format("\t[%s]", meta.getColumnName(i)));
			writer.write(String.format(" [%s]", meta.getColumnTypeName(i)));
			if (meta.getColumnTypeName(i).equals("nvarchar")) writer.write("(max)");
			writer.write(" NULL");
			if (i != meta.getColumnCount()) writer.write(",");
			writer.write("\n");
		}
		writer.write(");");
	}
	
	private long createInserts(String table_name, ResultSet rs, long row_count, Writer writer) throws IOException {	
		long row_number = 0;	
		try {
			ResultSetMetaData meta = rs.getMetaData();
			List<String> columns = new ArrayList<>();
			for (int i=1; i<=meta.getColumnCount(); i++) columns.add(meta.getColumnName(i));
			String columns_str = columnString(columns);
			writer.write("\n");
			
			final long max_index = 100;
			long step = 0;
			long index = 1;
			if (row_count > 0) step = row_count / max_index; 
			
			while (rs.next()) {			
				if (isStopped()) return 0;	
				writer.write(String.format("INSERT INTO %s (%s) VALUES ", table_name, columns_str));
				writer.write("(");
				for (int i=0; i<columns.size(); i++) {					
					if (_types.get(columns.get(i)).equals("datetime")) {
						String val = rs.getString(columns.get(i));
						if (val == null || val.isEmpty() || val.equals("1970-01-01 00:00:00.0")) writer.write("NULL");
						else {
							writer.write(String.format("CONVERT(datetime, '%s', 120)", rs.getString(columns.get(i))));	
						}
					} else {
						writer.write(getValue(rs.getString(columns.get(i))));	
					}					
					if (i < columns.size()-1) writer.write(",");
				}
				writer.write(");\n");
				row_number++;
				if (step > 0 && row_number >= index * step) {
					exportingMsg(table_name, row_number, row_count);
					index++;
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}	
		return row_number;
	}
	
	private String columnString(List<String> list) {
		StringBuilder bu = new StringBuilder();
		for (int i=0; i<list.size(); i++) {
			bu.append(String.format("[%s]", list.get(i)));
			if (i < list.size()-1) bu.append(",");
		}
		return bu.toString();
	}
	
	private String getValue(String val) {
		if (val == null) {
			return "NULL";
		} else {
			return escapeTxtData(String.format("'%s'", val.replaceAll("'", "''")));
		}
	}
	
	private Map<String, String> _types;
	
	
	public boolean isStopped() {
		return _isStopped;
	}
	
	public void setStopped(boolean flag) {
		_isStopped = flag;
	}
	
	private boolean _isStopped = false;
	
	// Actions
	// -----------------------------------------------------------------------------------------------------------------
	
	private void startMsg(String table_name) {
		for (WorkerListener listener : _listener) {
			listener.start(String.format("Send query to '%s'", table_name));
		}
	}
	
	private void foundMsg(String table_name, Long rows) {
		for (WorkerListener listener : _listener) {
			listener.processing(String.format("%d were found from '%s'", rows, table_name));
		}
	}
	
	private void exportingMsg(String table_name, long exported, long rows) {
		for (WorkerListener listener : _listener) {
			listener.processing(String.format("Exporting %d/%d from '%s'", exported, rows, table_name));
		}
	}
	
	private void finishMsg(String table_name, long row_exported, long row_count) {
		finishMsg(String.format("Export %d/%d rows from '%s' finished", row_exported, row_count, table_name));
	}
	
	private void finishMsg(String str) {
		for (WorkerListener listener : _listener) {
			listener.finishItem(str);
		}
	}
	
	private void completed(String str) {
		for (WorkerListener listener : _listener) {
			listener.completed(str);
		}
	}
}
