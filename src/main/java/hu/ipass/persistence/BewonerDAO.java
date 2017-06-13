package hu.ipass.persistence;

import java.sql.*;
import java.util.*;

import hu.ipass.model.Bewoner;

public class BewonerDAO extends BaseDAO {
	
	private List<Bewoner> select(String query) {
		List<Bewoner> results = new ArrayList<Bewoner>();
		
		try (Connection con = super.getConnection()) {
			
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			while (resultSet.next()) {
				int bewonerID = resultSet.getInt("bewonerID");
				String gebruikersnaam = resultSet.getString("gebruikersnaam");
				String wachtwoord = resultSet.getString("wachtwoord");
				double schuld = resultSet.getDouble("schuld");
				Bewoner bewoner = new Bewoner(bewonerID, gebruikersnaam, wachtwoord, schuld);
				results.add(bewoner);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public List<Bewoner> selectAll() {
		String query = "select * from bewoner order by bewonerID";
		return select(query);
	}
	
	public Bewoner selectByID(int bewonerID) {
		String query = "select * from bewoner where bewonerID = " + bewonerID + " order by bewonerID";
		List<Bewoner> result = select(query);		
		return result.get(0);
	}
	
	public Bewoner insert(Bewoner b) {
		Bewoner result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "insert into bewoner(gebruikersnaam, "
					+ "wachtwoord, schuld) values(?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, b.getGebruikersnaam());
			stmt.setString(2, b.getWachtwoord());
			stmt.setDouble(3, b.getSchuld());
			stmt.executeUpdate();
			
			result = selectByID(b.getBewonerID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Bewoner update(Bewoner b) {
		Bewoner result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "update bewoner set gebruikersnaam = ?, wachtwoord = ?, "
					+ "schuld = ? where bewonerID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, b.getGebruikersnaam());
			stmt.setString(2, b.getWachtwoord());
			stmt.setDouble(3, b.getSchuld());
			stmt.setInt(4, b.getBewonerID());
			stmt.executeUpdate();
			
			result = selectByID(b.getBewonerID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean delete(int bewonerID) {
		try (Connection con = super.getConnection()) {
			
			String query = "delete from bewoner where bewonerID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, bewonerID);
			stmt.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
