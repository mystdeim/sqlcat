package sqlcat.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import sqlcat.db.DBDriver;
import sqlcat.util.Exporter;
import sqlcat.util.Importer;
import sqlcat.util.WorkerListener;


public class Controller {
	private String _db_file_path;
	private String _db_type;

	public Controller(View view) {
		_view = view;
		init();
	}
		
	public void loadSettings(Properties props) {
		if (null != props.get(DB_TYPE_FIELD)) _db_type = props.get(DB_TYPE_FIELD).toString();
		else _db_type = "";
		if (null != props.get(SERVER_FIELD)) _server = props.get(SERVER_FIELD).toString();
		else _server = "";
		if (null != props.get(PORT_FIELD)) _port = props.get(PORT_FIELD).toString();
		else _port = "";
		if (null != props.get(DB_FIELD)) _db = props.get(DB_FIELD).toString();
		else _db = "";
		if (null != props.get(LOGIN_FIELD)) _login = props.get(LOGIN_FIELD).toString();
		else _login = "";
		if (null != props.get(PASSWORD_FIELD)) _password = props.get(PASSWORD_FIELD).toString();
		else _password = "";
		if (null != props.get(DB_FILEPATH_FIELD)) _db_file_path = props.get(DB_FILEPATH_FIELD).toString();
		else _db_file_path = "";
		if (null != props.get(EXPORT_FILEPATH_FIELD)) _export_filepath = props.get(EXPORT_FILEPATH_FIELD).toString();
		else _export_filepath = "";
		if (null != props.get(IMPORT_FILEPATH_FIELD)) _import_filepath = props.get(IMPORT_FILEPATH_FIELD).toString();
		else _import_filepath = "";
		if (null != props.get(OBJECTNAME_FIELD)) _objectname = props.get(OBJECTNAME_FIELD).toString();
		else _objectname = "";
		
		updateView();
	}

	public void saveSettings(Properties props) {
		updateDbProps();
		props.put(DB_TYPE_FIELD, _db_type.trim());
		props.put(SERVER_FIELD, _server.trim());
		props.put(DB_FIELD, _db.trim());
		props.put(LOGIN_FIELD, _login.trim());
		props.put(PASSWORD_FIELD, _password.trim());
		props.put(DB_FILEPATH_FIELD, _db_file_path.trim());
		props.put(PORT_FIELD, _port.trim());
		props.put(EXPORT_FILEPATH_FIELD, _export_filepath.trim());
		props.put(IMPORT_FILEPATH_FIELD, _import_filepath.trim());
		props.put(OBJECTNAME_FIELD, _objectname.trim());
	}
	
	private void init() {	
		_view.getDBTypeComboBox().addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeConSettings();
			}
		});
		changeConSettings();
		_view.getExportStartButton().addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDbProps();
				start_export();
			}
		});
		_view.getImportStopButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stop_export();
			}
		});
		
		_view.getImportStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDbProps();
				start_import();
			}
		});
		_view.getImportStopButton().addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				stop_import();
			}
		});
		_view.getCheckConnectionButton().addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDbProps();
				checkConnection();
			}
		});
		_timer_runnable = new TimerRunnable();
		_exporter = new Exporter();
		_exporter.addGeneratorListener(new WorkerListener() {			
			@Override
			public void start(String msg) {
				setStatusMsg(msg);
			}			
			@Override
			public void processing(String msg) {
				setStatusMsg(msg);
			}			
			@Override
			public void finishItem(String msg) {
				setStatusMsg(msg);
				_view.getExportLogArea().setText(String.format("%s%s\n", 
						_view.getExportLogArea().getText(), msg));
			}
			@Override
			public void completed(String msg) {
				stopTimer();
				setStatusMsg(msg);
				_view.getExportLogArea().setText(String.format("%s%s\n", 
						_view.getExportLogArea().getText(), msg));
			}
		});
		_importer = new Importer();
		_importer.addGeneratorListener(new WorkerListener() {			
			@Override
			public void start(String msg) {
				setStatusMsg(msg);
			}			
			@Override
			public void processing(String msg) {
				setStatusMsg(msg);
			}			
			@Override
			public void finishItem(String msg) {}
			@Override
			public void completed(String msg) {
				stopTimer();
				setStatusMsg(msg);				
			}
		});
		_view.getCheckBoxAllTables().addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {					
					@Override
					public void run() {
						boolean flag = !_view.getCheckBoxAllTables().isSelected();
						_view.setEnableObjectName(flag);
					}
				});
			}
		});
	}
	
	private void updateView() {
		_view.getDBTypeComboBox().setSelectedItem(_db_type);
		_view.setServerName(_server);
		_view.setDbName(_db);
		_view.setLogin(_login);
		_view.setPassword(_password);
		_view.setDbFilePath(_db_file_path);
		_view.setPort(_port);
		_view.setObjectName(_objectname);
		_view.setFilePathExport(_export_filepath);
		_view.setFilePathImport(_import_filepath);
	}
	
	private void updateDbProps() {
		_db_type = _view.getDBType();
		_server = _view.getServerName();
		_db = _view.getDbName();
		_login = _view.getLogin();
		_password = _view.getPassword();
		_db_file_path = _view.getDbFilePath();
		_port = _view.getPort();
		_objectname = _view.getObjectName();
		_export_filepath = _view.getFilePathExport();
		_import_filepath = _view.getFilePathImport();
	}
	
	private void start_export() {
		_importer.setRunning(true);
		new Thread(new ExportRunnable()).start();
	}
	
	private void stop_export() {
		_exporter.setRunning(false);
		stopTimer();
	}
	
	private void start_import() {
		_importer.setRunning(true);
		new Thread(new ImportRunnable()).start();
	}
	
	private void stop_import() {
		_importer.setRunning(false);
		stopTimer();
	}
	
	private void checkConnection() {
		new Thread(new CheckingConRunnable()).start();
	}
	
	private void showSuccess() {
		EventQueue.invokeLater(new Runnable() {				
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Success!");
			}
		});
	}
	
	private void showError(final Exception e) {
		e.printStackTrace();
		EventQueue.invokeLater(new Runnable() {				
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, String.format(
					"<html><body><p style='width: %dpx;'>%s</p></body></html>", 
					_view.getContent().getSize().width-50, 
					e.getMessage().replaceAll("\n", "<br />")), 
					"Error", JOptionPane.OK_OPTION);	
			}
		});
	}
	
	private void setStatusMsg(final String msg) {
		EventQueue.invokeLater(new Runnable() {			
			@Override
			public void run() {
				_view.setStatusMsg(msg);
			}
		});
	}
	
	private void changeConSettings() {
		EventQueue.invokeLater(new Runnable() {					
			@Override
			public void run() {
				String type = _view.getDBType();
				if (type.equals("sqlite")) _view.getPanelCon2();	
				else _view.getPanelCon1();
			}
		});
	}

	private static final String DB_TYPE_FIELD = Controller.class.getCanonicalName() + ".db_type";
	private static final String SERVER_FIELD = Controller.class.getCanonicalName() + ".server";
	private static final String DB_FIELD = Controller.class.getCanonicalName() + ".db";
	private static final String LOGIN_FIELD = Controller.class.getCanonicalName() + ".login";
	private static final String PASSWORD_FIELD = Controller.class.getCanonicalName() + ".password";
	private static final String DB_FILEPATH_FIELD = Controller.class.getCanonicalName() + ".db_file_path";
	private static final String PORT_FIELD = Controller.class.getCanonicalName() + ".port";
	private static final String EXPORT_FILEPATH_FIELD = Controller.class.getCanonicalName() + ".export_filepath";
	private static final String OBJECTNAME_FIELD = Controller.class.getCanonicalName() + ".objectname";
	private static final String IMPORT_FILEPATH_FIELD = Controller.class.getCanonicalName() + ".import_filepath";

	private View _view;
	
	private String _server;
	private String _db;
	private String _login;
	private String _password;
	private String _port;
	private String _export_filepath;
	private String _import_filepath;
	private String _objectname;
	private Exporter _exporter;
	private Importer _importer;
	
	private TimerRunnable _timer_runnable;
	private long _start_time;
	
	private class CheckingConRunnable implements Runnable {

		@Override
		public void run() {
			startTimer();
			EventQueue.invokeLater(new Runnable() {			
				@Override
				public void run() {
					_view.setStatusMsg("Checking connetion...");
					_view.setEnableControls(false);
				}
			});	
			
			String db_type = _view.getDBType();
					
			DBDriver driver = DBDriver.getDriver(db_type);
			driver.setServerName(_server);
			driver.setDatabaseName(_db);
			driver.setUser(_login);
			driver.setPassword(_password);
			if (_port != null && !_port.isEmpty()) driver.setPortNumber(Integer.valueOf(_port));
			
			try {
				driver.getConnection();
				EventQueue.invokeLater(new Runnable() {			
					@Override
					public void run() {
						_view.setStatusMsg("Connection is Ok");
					}
				});				
				showSuccess();
			} catch (SQLException e) {			
				EventQueue.invokeLater(new Runnable() {			
					@Override
					public void run() {
						_view.setStatusMsg("Connection failed");
					}
				});
				showError(e);
			} finally {		
				stopTimer();	
				EventQueue.invokeLater(new Runnable() {			
					@Override
					public void run() {
						_view.setReadyStatus();
					}
				});	
			}
		}
		
	}
	
	private class ExportRunnable implements Runnable {
		@Override
		public void run() {
			startTimer();
			EventQueue.invokeLater(new Runnable() {				
				@Override
				public void run() {
					_view.activateOneControl(_view.getExportStopButton());
					_view.getExportLogArea().setText("");
				}
			});
			
			try {
				_exporter.setParams(_server, _db, _login, _password, _port, _view.getDBType(), _db_file_path);
				_exporter.generate(_objectname, _export_filepath, _view.isCreateAllTables(), _view.isCreateTable());
				
				showSuccess();
			} catch (Exception e) {
				showError(e);
			} finally {
				stopTimer();
				EventQueue.invokeLater(new Runnable() {				
					@Override
					public void run() {
						_view.setReadyStatus();
					}
				});
			}
		}		
	}
	
	private class ImportRunnable implements Runnable {
		@Override
		public void run() {
			startTimer();
			EventQueue.invokeLater(new Runnable() {				
				@Override
				public void run() {
					_view.activateOneControl(_view.getImportStopButton());
				}
			});
			
			try {
				_importer.setParams(_server, _db, _login, _password, _port, _view.getDBType(), _db_file_path);
				_importer.generate(_import_filepath);
				
				showSuccess();
			} catch (Exception e) {
				showError(e);
			} finally {
				stopTimer();
				EventQueue.invokeLater(new Runnable() {				
					@Override
					public void run() {
						_view.setReadyStatus();
					}
				});
			}
		}		
	}
	
	private void startTimer() {
		_start_time = System.currentTimeMillis();
		_timer_runnable.running(true);
		new Thread(_timer_runnable).start();
	}
	
	private void stopTimer() {
		_timer_runnable.running(false);
		setTimerString();
	}
	
	private class TimerRunnable implements Runnable {
		
		private volatile boolean _running = true;
		
		public void running(boolean flag) {
			_running = flag;
		}
		
		@Override
		public void run() {
			while (_running) {
				setTimerString();	
				try {
					Thread.sleep((long) ((1 + 9 * Math.random()) * 10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			
			}
		}		
	}
	
	private void setTimerString() {
		EventQueue.invokeLater(new Runnable() {				
			@Override
			public void run() {
				_view.setTimerString(formatTimeDiff(_start_time));
			}
		});		
	}
	
	private String formatTimeDiff(long start) {
		long delta = System.currentTimeMillis() - start;
		
		long h = TimeUnit.MILLISECONDS.toHours(delta);
		long m = TimeUnit.MILLISECONDS.toMinutes(delta - TimeUnit.HOURS.toMillis(h));
		long s = TimeUnit.MILLISECONDS.toSeconds(delta - TimeUnit.HOURS.toMillis(h) - TimeUnit.MINUTES.toMillis(m));
		long ms = TimeUnit.MILLISECONDS.toMillis(delta 
				- TimeUnit.HOURS.toMillis(h) - TimeUnit.MINUTES.toMillis(m) - TimeUnit.SECONDS.toMillis(s));
		
		return String.format("%02d:%02d:%02d.%03d", h,m,s,ms);
	}
}
