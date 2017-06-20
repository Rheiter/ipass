package hu.ipass.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDAO {
	
	protected static Connection getConnection() {
	    Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			
			String username = "ipass";
			String password = "ipass";
		    String dbUrl = "jdbc:postgresql://localhost:5432/ipass";

		    con = DriverManager.getConnection(dbUrl, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		return con;
	}
}