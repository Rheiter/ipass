package hu.ipass.persistence;

import java.sql.*;
import java.util.*;

import hu.ipass.model.Bewoner;

public class BewonerDAO extends BaseDAO {
	
	/**
	 * Zet gegevens uit de database om in een Bewoner POJO.
	 * 
	 * @param query - select sql query
	 * @return - Lijst met Bewoners
	 */
	private List<Bewoner> select(String query) {
		List<Bewoner> results = new ArrayList<Bewoner>();
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Voer de query uit op de database
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			// Maak een Bewoner POJO voor elke rij van het resultaat
			while (resultSet.next()) {
				int bewonerID = resultSet.getInt("bewonerID");
				String gebruikersnaam = resultSet.getString("gebruikersnaam");
				String wachtwoord = resultSet.getString("wachtwoord");
				double schuld = resultSet.getDouble("schuld");
				Bewoner bewoner = new Bewoner(bewonerID, gebruikersnaam, wachtwoord, schuld);
				results.add(bewoner);
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
	 * Select alle bewoners uit de database.
	 * @return - Lijst met alle bewoners
	 */
	public List<Bewoner> selectAll() {
		String query = "select * from bewoner order by bewonerID";
		return select(query);
	}
	
	/**
	 * Select bewoner uit de database met een bepaald bewonerID
	 * 
	 * @return - Bewoner met bepaald bewonerID
	 */
	public Bewoner selectByID(int bewonerID) {
		String query = "select * from bewoner where bewonerID = " + bewonerID + " order by bewonerID";
		List<Bewoner> result = select(query);		
		return result.get(0);
	}
	
	/**
	 * Select bewoner uit de database met een bepaalde gebruikersnaam
	 * 
	 * @return - Bewoner met bepaalde gebruikersnaam
	 */
	public Bewoner selectByGebruikersnaam(String gebruikersnaam) {
		String query = "select * from bewoner where gebruikersnaam = '" + gebruikersnaam + "' order by bewonerID";
		List<Bewoner> result = select(query);		
		return result.get(0);
	}
	
	/**
	 * Haalt de totale schuld van alle bewoners op uit de database.
	 * 
	 * @return - Totale schuld van alle bewoners
	 */
	public int selectTotaalSchuld() {
		int result = 0;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Voer de query uit op de database
			String query = "select sum(schuld) as totaal from bewoner";
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet resultSet = stmt.executeQuery();
			
			// Sla het resultaat op
			while (resultSet.next()) {
				result = resultSet.getInt("totaal");
			}
			
			resultSet.close();
			stmt.close();
			con.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// Wordt niet gebruikt ######################################################
	/**
	 * Insert een bewoner in de database
	 * 
	 * @return - de nieuwe bewoner of null
	 */
	public Bewoner insert(Bewoner b) {
		Bewoner result = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik hem op de database
			String query = "insert into bewoner(gebruikersnaam, "
					+ "wachtwoord, schuld) values(?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, b.getGebruikersnaam());
			stmt.setString(2, b.getWachtwoord());
			stmt.setDouble(3, b.getSchuld());
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
			// Haal de nieuwe bewoner op uit de database
			result = selectByID(b.getBewonerID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// Wordt niet gebruikt ######################################################
	/**
	 * Update een bewoner in de database
	 * 
	 * @return - De aangepaste bewoner
	 */
	public Bewoner update(Bewoner b) {
		Bewoner result = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query n gebruik hem op de database
			String query = "update bewoner set gebruikersnaam = ?, wachtwoord = ?, "
					+ "schuld = ? where bewonerID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, b.getGebruikersnaam());
			stmt.setString(2, b.getWachtwoord());
			stmt.setDouble(3, b.getSchuld());
			stmt.setInt(4, b.getBewonerID());
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
			// Sla de aangepaste bewoner op
			result = selectByID(b.getBewonerID());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Zet de schuld van alle bewoners in de database op 0
	 * 
	 * @return - True als het geslaagd is, false als het mislukt is
	 */
	public boolean resetSchuld() {		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Gebruik de query op de database
			String query = "update bewoner set schuld = 0";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);

			stmt.close();
			con.close();
			
			// Gelukt
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			// Niet gelukt
			return false;
		}
	}
	
	/**
	 * Update de schuld van alle bewoners in de database
	 * 
	 * @param sysdate - Te gebruiken datum
	 * @return - Lijst met aangepaste bewoners
	 */
	public List<Bewoner> updateSchuld(String sysdate) {
		List<Bewoner> result = new ArrayList<Bewoner>();
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Gebruik de query op de database
			String query = "update bewoner b set schuld = schuld + "
					+ "(select t.boete from taak t join bewoner_taak bt "
					+ "on (bt.taakid = t.taakid) where bt.bewonerid = b.bewonerid "
					+ "and datum = date(date_trunc('week', cast('" + sysdate + "' as timestamp))) + 6) "
					+ "where b.bewonerid in (select bt.bewonerid from bewoner_taak bt "
					+ "where bt.gedaan = false and datum = date(date_trunc('week', cast('" + sysdate + "' as timestamp))) + 6)";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);

			stmt.close();
			con.close();
			
			// Haal alle bewoners op uit de database
			result = selectAll();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// Wordt niet gebruikt
	/**
	 * Verwijder een bewoner met bepaald bewonerID uit de database
	 * 
	 * @return - True als het geslaagd is, false als het mislukt is
	 */
	public boolean delete(int bewonerID) {
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik hem op de database
			String query = "delete from bewoner where bewonerID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, bewonerID);
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
			// Gelukt
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			// Niet gelukt
			return false;
		}
	}
	
	/**
	 * Vindt de rol die bij een bepaalde gebruikersnaam en wachtwoord hoort.
	 * 
	 * @return - Rol
	 */
	public String findRoleForUsernameAndPassword(String gebruikersnaam, String wachtwoord) {
		String role = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
		
			// Maak de query en gebruik hem op de database
			String query = "select rol from bewoner where gebruikersnaam = ? and wachtwoord = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, gebruikersnaam);
			stmt.setString(2, wachtwoord);
			ResultSet resultSet = stmt.executeQuery();
			
			// Sla het resultaat op
			if (resultSet.next()) {
				role = resultSet.getString("rol");
			}
			
			stmt.close();
			con.close();
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return role;
	}
}