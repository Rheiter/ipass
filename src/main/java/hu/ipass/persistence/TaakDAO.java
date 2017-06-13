package hu.ipass.persistence;

import java.sql.*;
import java.util.*;

import hu.ipass.model.Taak;

public class TaakDAO extends BaseDAO {
	
	private List<Taak> select(String query) {
		List<Taak> results = new ArrayList<Taak>();
		
		try (Connection con = super.getConnection()) {
			
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			while (resultSet.next()) {
				int taakID = resultSet.getInt("taakID");
				String naam = resultSet.getString("naam");
				String omschrijving = resultSet.getString("omschrijving");
				Taak taak = new Taak(taakID, naam, omschrijving);
				results.add(taak);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public List<Taak> selectAll() {
		String query = "select * from taak";
		return select(query);
	}
	
	public Taak selectByID(int taakID) {
		String query = "select * from taak where taakID = " + taakID + " order by taakID";	
		return select(query).get(0);
	}
	
	public Taak selectLast() {
		String query = "select * from taak order by taakID desc limit 1";
		return select(query).get(0);
	}
	
	public Taak insert(Taak t) {
		Taak result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query ="insert into taak(naam, omschrijving) values(?,?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, t.getNaam());
			stmt.setString(2, t.getOmschrijving());
			stmt.executeUpdate();
			
			result = selectLast();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Taak update(Taak t) {
		Taak result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "update taak set naam = ?, omschrijving = ? "
					+ "where taakID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, t.getNaam());
			stmt.setString(2, t.getOmschrijving());
			stmt.setInt(3, t.getTaakID());
			stmt.executeUpdate();
			result = selectByID(t.getTaakID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean delete(int taakID) {
		try (Connection con = super.getConnection()) {
			
			String query = "delete from taak where taakID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, taakID);
			stmt.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}