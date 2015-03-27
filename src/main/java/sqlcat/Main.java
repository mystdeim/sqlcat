package sqlcat;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.UIManager;

import sqlcat.gui.Controller;
import sqlcat.gui.View;

public class Main {

	public static void main(String[] args) {
		final int min_height = 600;
		final float k = 1.6f;
		
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        
        int tmp_height = (int) (size.height/2f);
        if (tmp_height < min_height) tmp_height = min_height;        
        final int height = tmp_height;
        
        final int width = (int) (height * k);
        final int x = (int) (size.width/2f - width/2f);
        final int y = (int) (size.height/2f - height/2f);

		
        final JFrame frame = new JFrame("ViewToTable");
        final View view = new View();
        final Controller controller = new Controller(view);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.getContentPane().add(view.getContent());
				frame.setBounds(x, y, width, height);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						Properties defaultProps = new Properties();
						controller.saveSettings(defaultProps);
						saveSettings(defaultProps);
					}
				});
				controller.loadSettings(loadSettings());
			}
		});
	}
	
	public static void saveSettings(Properties defaultProps) {
		try (FileOutputStream out = new FileOutputStream(FILE_SETTINGS)) {
			defaultProps.store(out, "ViewToTable");
		} catch (IOException e) {
	        e.printStackTrace();
        }
	}
	
	public static Properties loadSettings() {
		final Properties defaultProps = new Properties();
		Path path = Paths.get(FILE_SETTINGS);
		if (Files.isRegularFile(path)) {
			try (FileInputStream in = new FileInputStream(FILE_SETTINGS)) {
				defaultProps.load(in);
			} catch (IOException e) {
		        e.printStackTrace();
	        }
		}
		return defaultProps;
	}	

	private static final String FILE_SETTINGS = "settings";

}
