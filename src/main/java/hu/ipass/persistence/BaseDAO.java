package hu.ipass.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDAO {
	
	protected static Connection getConnection() {
	    Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			
			String username = "pisvtaeuldtswo";
			String password = "a023eb13107af1cddd79af5e0319493ce0707a32fe51bb827d6de1a94a013ea6";
		    String dbUrl = "a023eb13107af1cddd79af5e0319493ce0707a32fe51bb827d6de1a94a013ea6";

		    con = DriverManager.getConnection(dbUrl, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return con;
	}
}