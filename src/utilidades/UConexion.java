package utilidades;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class UConexion {
	private static Connection conexion;
	
	private UConexion() {}
	
	public static Connection getInstancia(){
		if (conexion == null) {
			Properties prop = new Properties();
			InputStream is;
			String driver;
			String server;
			String user;
			String pass;
			try {
				is = new FileInputStream("framework.properties");
				prop.load(is);
				driver = prop.getProperty("driver");
				server = prop.getProperty("server");
				user = prop.getProperty("user");
				pass = prop.getProperty("password");
				
				Class.forName(driver);
				conexion = DriverManager.getConnection(server, user, pass);
				
			} catch (IOException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		return conexion;
	}
}
