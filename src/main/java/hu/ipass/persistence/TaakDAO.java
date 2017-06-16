package hu.ipass.persistence;

import java.sql.*;
import java.util.*;

import hu.ipass.model.Taak;

public class TaakDAO extends BaseDAO {
	
	/**
	 * Zet gegevens uit de database om in een Taak POJO.
	 * 
	 * @param query - select sql query
	 * @return - Lijst met Taken
	 */
	private List<Taak> select(String query) {
		List<Taak> results = new ArrayList<Taak>();
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Gebruik de query op de database
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			// Maak een Taak POJO voor elke rij van het resultaat
			while (resultSet.next()) {
				int taakID = resultSet.getInt("taakID");
				String naam = resultSet.getString("naam");
				String omschrijving = resultSet.getString("omschrijving");
				double boete = resultSet.getDouble("boete");
				boolean actief = resultSet.getBoolean("actief");
				Taak taak = new Taak(taakID, naam, omschrijving, boete, actief);
				results.add(taak);
			}
			
			resultSet.close();
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	/**
	 * Select alle taken uit de database die actief zijn
	 * @return - Lijst met alle taken die actief zijn
	 */
	public List<Taak> selectAll() {
		String query = "select * from taak where actief = true order by taakID";
		return select(query);
	}
	
	/**
	 * Select taak uit de database met een bepaald taakID
	 * 
	 * @return - Taak met bepaald taakID
	 */
	public Taak selectByID(int taakID) {
		String query = "select * from taak where taakID = " + taakID;	
		return select(query).get(0);
	}
	
	/**
	 * Selecteer de laatst toegevoegde actieve taak
	 * 
	 * @return - Laatst toegevoeggde actieve taak
	 */
	public Taak selectLast() {
		String query = "select * from taak where actief = true order by taakID desc limit 1";
		return select(query).get(0);
	}

	/**
	 * Selecteer de taak met taakID 9999
	 * 
	 * @return - Taak met taakID 9999
	 */
	public Taak selectVrij() {
		return selectByID(9999);
	}
	
	/**
	 * Voeg een nieuwe taak toe aan de database
	 * 
	 * @return - Nieuwe taak of null
	 */
	public Taak insert(Taak t) {
		Taak result = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik hem op de database
			String query ="insert into taak(naam, omschrijving, boete) values(?,?,?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, t.getNaam());
			stmt.setString(2, t.getOmschrijving());
			stmt.setDouble(3, t.getBoete());
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
			// Haal de laatst toegevoegde taak op
			result = selectLast();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Update een taak in de database
	 * 
	 * @return - De aangepaste taak uit de database of null
	 */
	public Taak update(Taak t) {
		Taak result = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik deze op de database
			String query = "update taak set naam = ?, omschrijving = ? , boete = ? "
					+ "where taakID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, t.getNaam());
			stmt.setString(2, t.getOmschrijving());
			stmt.setDouble(3, t.getBoete());
			stmt.setInt(4, t.getTaakID());
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
			// Haal de aangepaste taak op uitde database
			result = selectByID(t.getTaakID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Zet een taak in de database op niet actief
	 * 
	 * @return - Aangepaste taak uit de database of null
	 */
	public Taak delete(int taakID) {
		Taak result = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik hem op de database
			String query = "update taak set actief = false where taakID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, taakID);
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
			// Haal de aangepaste taak op uit de datbase
			result = selectByID(taakID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}