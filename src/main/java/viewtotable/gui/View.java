package viewtotable.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class View {
	
	private JTextField _file_db_field;
	private JPanel _b_main;

	public View() {
		init();
	}
	
	// Getter
	// -----------------------------------------------------------------------------------------------------------------
	
	public JPanel getContent() {
		return _content;
	}
	
	public String getServerName() {
		return _server_field.getText().trim();
	}
	
	public void setServerName(String txt) {
		_server_field.setText(txt);
	}
	
	public String getDbName() {
		return _db_field.getText().trim();
	}
	
	public void setDbName(String txt) {
		_db_field.setText(txt);
	}
	
	public String getLogin() {
		return _login_field.getText().trim();
	}
	
	public void setLogin(String txt) {
		_login_field.setText(txt);
	}
	
	public String getPassword() {
		return new String(_pass_field.getPassword()).trim();
	}
	
	public void setPassword(String txt) {
		_pass_field.setText(txt);
	}
	
	public String getDbFilePath() {
		return _file_db_field.getText().trim();
	}
	
	public void setDbFilePath(String txt) {
		_file_db_field.setText(txt);
	}
	
	public String getPort() {
		return _port_field.getText().trim();
	}
	
	public void setPort(String txt) {
		_port_field.setText(txt);
	}
	
	public void setEnableObjectName(boolean flag) {
		_name_object.setEnabled(flag);
	}
	
	public String getObjectName() {
		return _name_object.getText().trim();
	}
	
	public void setObjectName(String txt) {
		_name_object.setText(txt);
	}
	
	public String getFilePathExport() {
		return _file_path_export.getText().trim();
	}
	
	public String getFilePathImport() {
		return _file_path_import.getText().trim();
	}
	
	public void setFilePathExport(String path) {
		_file_path_export.setText(path);
	}
	
	public void setFilePathImport(String path) {
		_file_path_import.setText(path);
	}
	
	public JButton getExportStartButton() {
		return _export_start_button;
	}
	
	public JButton getExportStopButton() {
		return _export_stop_button;
	}
	
	public JButton getImportStartButton() {
		return _import_start_button;
	}
	
	public JButton getImportStopButton() {
		return _import_stop_button;
	}
	
	public JButton getCheckConnectionButton() {
		return _check_connection;
	}
	
	public void setStatusMsg(String msg) {
		_status_label.setText(msg);
	}
	
	public void setTimerString(String txt) {
		_timer_label.setText(txt);
	}
	
	public JTextArea getExportLogArea() {
		return _export_log_area;
	}
	
	public JCheckBox getCheckBoxAllTables() {
		return _check_all;
	}
	
	public JComboBox<String> getDBTypeComboBox() {
		return _type_db;
	}
	
	// Specific
	// -----------------------------------------------------------------------------------------------------------------
	
	public void setReadyStatus() {
		_check_connection.setEnabled(true);
		_export_start_button.setEnabled(true);
		_export_stop_button.setEnabled(false);
		_import_start_button.setEnabled(true);
		_import_stop_button.setEnabled(false);
	}
	
	public void activateOneControl(JComponent com) {
		setEnableControls(false);
		com.setEnabled(true);
	}
	
	public void setEnableControls(boolean flag) {
		_check_connection.setEnabled(flag);
		_export_start_button.setEnabled(flag);
		_export_stop_button.setEnabled(flag);
		_import_start_button.setEnabled(flag);
		_import_stop_button.setEnabled(flag);
	}
	
	public String getDBType() {
		return _type_db.getSelectedItem().toString();
	}
	
	// Private
	// -----------------------------------------------------------------------------------------------------------------
	
	private void init() {
		_content = new JPanel(new BorderLayout());
		_tabs = new JTabbedPane();
		_tabs.addTab("Settings", getSettingsTab());
		_tabs.addTab("Export", getExportTab());
		_tabs.addTab("Import", getImportTab());
		_content.add(_tabs, BorderLayout.CENTER);
		_content.add(getStatusPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel getSettingsTab() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel type_panel = new JPanel(new BorderLayout());
		type_panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		type_panel.add(new JLabel("type db: ", Label.RIGHT), BorderLayout.WEST);
		_type_db = new JComboBox<String>();
		_type_db.addItem("mssql");
		_type_db.addItem("oracle");
		_type_db.addItem("sqlite");
		type_panel.add(_type_db, BorderLayout.CENTER);
		main.add(type_panel, BorderLayout.NORTH);
					

		_server_field = new JTextField(20);
		_db_field = new JTextField(20);
		_login_field = new JTextField(20);
		_pass_field = new JPasswordField(20);
		_port_field = new JTextField(20);
		_file_db_field = new JTextField(20);
		
		_b_main = new JPanel(new BorderLayout());
		_b_main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		main.add(_b_main);
		
		JPanel check_panel = new JPanel(new BorderLayout());
		check_panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		_check_connection = new JButton("Check connection");
		check_panel.add(_check_connection, BorderLayout.WEST);
		main.add(check_panel, BorderLayout.SOUTH);
				
		return main;
	}
		
	public void getPanelCon1() {
		_b_main.removeAll();
		JPanel body = new JPanel(new GridBagLayout());
		
		GridBagConstraints c_left = new GridBagConstraints(0,0,1,1,0,0,
				GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),0,0);
		GridBagConstraints c_right = new GridBagConstraints(1,0,1,1,100,0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),0,0);		

		body.add(new JLabel("server:"), c_left);		
		body.add(_server_field, c_right);		
		
		c_left.gridy++;
		body.add(new JLabel("database:"), c_left);
		c_right.gridy++;
		body.add(_db_field, c_right);
		
		c_left.gridy++;
		body.add(new JLabel("login:"), c_left);
		c_right.gridy++;
		body.add(_login_field, c_right);
		
		c_left.gridy++;
		body.add(new JLabel("password:"), c_left);
		c_right.gridy++;
		body.add(_pass_field, c_right);
		
		c_left.gridy++;
		body.add(new JLabel("port:"), c_left);
		c_right.gridy++;
		body.add(_port_field, c_right);
		
		c_left.gridy++;
		c_left.gridheight++;
		c_left.weighty = 100;
		body.add(new JPanel(), c_left);
		
		_b_main.add(body, BorderLayout.CENTER);
		_b_main.revalidate();
	}
	
	public void getPanelCon2() {
		_b_main.removeAll();
		JPanel body = new JPanel(new GridBagLayout());
		
		GridBagConstraints c_left = new GridBagConstraints(0,0,1,1,0,0,
				GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),0,0);
		GridBagConstraints c_right = new GridBagConstraints(1,0,1,1,100,0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5, 5, 5, 5),0,0);		

		body.add(new JLabel("file:"), c_left);		
		
		JPanel db_field = new JPanel(new BorderLayout());
		db_field.add(_file_db_field, BorderLayout.CENTER);
		JButton path_button = getFileButton(_file_db_field);
		db_field.add(path_button, BorderLayout.EAST);
		body.add(db_field, c_right);	
		
		c_left.gridy++;
		c_left.gridheight++;
		c_left.weighty = 100;
		body.add(new JPanel(), c_left);

		_b_main.add(body, BorderLayout.CENTER);
		_b_main.revalidate();
	}
	
	private JPanel getExportTab() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		
		JPanel field_panel = new JPanel(new BorderLayout());
		field_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		_name_object = new JTextField();
		field_panel.add(new Label("Object name: "), BorderLayout.WEST);
		field_panel.add(_name_object, BorderLayout.CENTER);
		top.add(field_panel);
		
		JPanel all_tables_panel = new JPanel(new BorderLayout());
		all_tables_panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
		_check_all = new JCheckBox("All tables and views in dbo scheme");
		all_tables_panel.add(_check_all, BorderLayout.CENTER);
		top.add(all_tables_panel);
		
		JPanel file_panel = new JPanel(new BorderLayout());
		file_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		_file_path_export = new JTextField();
		file_panel.add(new Label("File path: "), BorderLayout.WEST);

		JPanel path_panel = new JPanel(new BorderLayout()); 
		_file_path_export = new JTextField();
		path_panel.add(_file_path_export, BorderLayout.CENTER);
		JButton path_button = getFileButton(_file_path_export);
		path_panel.add(path_button, BorderLayout.EAST);
		file_panel.add(path_panel, BorderLayout.CENTER);
		
		top.add(file_panel);
		
		JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		button_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		_export_start_button = new JButton("Generate");
		button_panel.add(_export_start_button);
		_export_stop_button = new JButton("Stop");
		_export_stop_button.setEnabled(false);
		button_panel.add(_export_stop_button);
		top.add(button_panel);
		
		main.add(top, BorderLayout.NORTH);
		
		_export_log_area = new JTextArea();
		_export_log_area.setEditable(false);
		JScrollPane spane = new JScrollPane(_export_log_area);
		main.add(spane, BorderLayout.CENTER);
		
		return main;
	}
	
	private JPanel getImportTab() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
				
		JPanel file_panel = new JPanel(new BorderLayout());
		file_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		file_panel.add(new Label("File path: "), BorderLayout.WEST);
		
		JPanel path_panel = new JPanel(new BorderLayout()); 
		_file_path_import = new JTextField();
		path_panel.add(_file_path_import, BorderLayout.CENTER);
		JButton path_button = getFileButton(_file_path_import);
		path_panel.add(path_button, BorderLayout.EAST);
		file_panel.add(path_panel, BorderLayout.CENTER);
		
		top.add(file_panel);
		
		JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		button_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		_import_start_button = new JButton("Run");
		button_panel.add(_import_start_button);
		_import_stop_button = new JButton("Stop");
		_import_stop_button.setEnabled(false);
		button_panel.add(_import_stop_button);
		top.add(button_panel);
		
		main.add(top, BorderLayout.NORTH);
		
		return main;
	}
	
	private JPanel _content;
	private JTabbedPane _tabs;
	
	private JTextField _server_field;
	private JTextField _db_field;
	private JTextField _login_field;
	private JPasswordField _pass_field;
	private JTextField _port_field;
	private JButton _check_connection;
	
	private JTextField _name_object;
	private JTextField _file_path_export;
	private JTextField _file_path_import;
	private JTextArea _export_log_area;
	private JButton _export_start_button;
	private JButton _export_stop_button;
	private JCheckBox _check_all;
	private JButton _import_start_button;
	private JButton _import_stop_button;
	private JComboBox<String> _type_db;
	
	// Bottom
	private JLabel _status_label;
	private JLabel _timer_label;
	
	private JPanel getStatusPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		_status_label = new JLabel("ready");
		panel.add(_status_label, BorderLayout.WEST);
		
		_timer_label = new JLabel("00:00:00.000");
		panel.add(_timer_label, BorderLayout.EAST);
		
		return panel;
	}	
	
	private JButton getFileButton(final JTextField path_field) {
		JButton path_button = new JButton("File...");
		final JFileChooser fc = new JFileChooser();
		path_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String path = path_field.getText().trim();
				if ("" != path) fc.setCurrentDirectory(new File(path));
				int returnVal = fc.showOpenDialog(_content);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            path_field.setText(file.getPath());
		        } 
			}
		});
		return path_button;
	}
}
