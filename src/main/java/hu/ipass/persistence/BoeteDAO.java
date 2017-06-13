package hu.ipass.persistence;

import java.sql.*;
import java.util.*;

import hu.ipass.model.Boete;

public class BoeteDAO extends BaseDAO {
	
	private List<Boete> select(String query) {
		List<Boete> results = new ArrayList<Boete>();
		
		try (Connection con = super.getConnection()) {
			
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			while (resultSet.next()) {
				int boeteID = resultSet.getInt("boeteID");
				double bedrag = resultSet.getDouble("bedrag");
				Boete boete = new Boete(boeteID, bedrag);
				results.add(boete);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public List<Boete> selectAll() {
		String query = "select * from boete";
		return select(query);
	}
	
	public Boete selectByID(int boeteID) {
		String query = "select * from boete where boeteID = " + boeteID;	
		return select(query).get(0);
	}
	
	public Boete insert(Boete b) {
		Boete result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "insert into boete(bedrag) values (?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setDouble(1, b.getBedrag());
			stmt.executeUpdate();
			
			result = selectByID(b.getBoeteID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Boete update(Boete b) {
		Boete result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "update boete set bedrag = ? where boeteID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setDouble(1, b.getBedrag());
			stmt.setInt(2, b.getBoeteID());
			stmt.executeUpdate();
			result = selectByID(b.getBoeteID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean delete(int boeteID) {
		try (Connection con = super.getConnection()) {
			
			String query = "delete from boete where boeteID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, boeteID);
			stmt.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}