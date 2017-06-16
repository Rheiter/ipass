package hu.ipass.persistence;

import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;

import hu.ipass.model.Bewoner;
import hu.ipass.model.BewonerTaak;
import hu.ipass.model.Taak;

public class BewonerTaakDAO extends BaseDAO {
	
	/**
	 * Zet gegevens uit de database om in een BewonerTaak POJO.
	 * 
	 * @param query - select sql query
	 * @return - Lijst met BewonerTaken
	 */
	private List<BewonerTaak> select(String query) {
		BewonerDAO bdao = new BewonerDAO();
		TaakDAO tdao = new TaakDAO();
		
		List<BewonerTaak> results = new ArrayList<BewonerTaak>();
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Gebruik de query op de database
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			// Maak een BewonerTaak POJO voor elke rij van het resultaat
			while (resultSet.next()) {
				int bewonerID = resultSet.getInt("bewonerID");
				int taakID = resultSet.getInt("taakID");
				Date datum = resultSet.getDate("datum");
				boolean gedaan = resultSet.getBoolean("gedaan");
				Bewoner bewoner = bdao.selectByID(bewonerID);
				Taak taak = tdao.selectByID(taakID);
				results.add(new BewonerTaak(bewoner, taak, datum, gedaan));
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
	 * Select alle BewonerTaken uit de database
	 * @return - Lijst met alle BewonerTaken
	 */
	public List<BewonerTaak> selectAll() {
		String query = "select * from bewoner_taak order by datum, bewonerID";
		return select(query);
	}
	
	/**
	 * Select BewonerTaak uit de database met een bepaald bewonerID, taakID
	 * en datum
	 * 
	 * @return - BewonerTaak met bepaald bewonerID, taakID en datum
	 */
	public BewonerTaak selectByID(BewonerTaak bt) {
		String query = String.format("select * from bewoner_taak where "
				+ "bewonerID = %d and taakID = %d and datum = to_date('" + bt.getDatum() + "', 'YYYY-MM-DD') order by datum, bewonerID",
				bt.getBewoner().getBewonerID(), bt.getTaak().getTaakID());
		return select(query).get(0);
	}
	
	/**
	 * Select BewonerTaken uit de database met een bepaald bewonerID
	 * @param bewonerID bewonerID
	 * @return  Lijst met BewonerTaken met een bepaald bewonerID
	 */
	public List<BewonerTaak> selectByBewonerID(int bewonerID) {
		String query = "select * from bewoner_taak where bewonerID = " + bewonerID;
		return select(query);
	}
	/**
	 * Selecteer de BewonerTaken die bij vorige week horen
	 * 
	 * @param sysdate - huidige datum in het systeem
	 * @return - Lijst met BewonerTaken die bij vorige week horen
	 */
	public List<BewonerTaak> selectLastWeek(String sysdate) {
		String query = "select * from bewoner_taak where datum = "
				+ "date(date_trunc('week', cast('" + sysdate + "' as timestamp))) - 1"
				+ "order by bewonerID";
		return select(query);
	}
	
	/**
	 * Selecteer de BewonerTaken die bij deze week horen
	 * 
	 * @param sysdate - huidige datum in het systeem
	 * @return - Lijst met BewonerTaken die bij deze week horen
	 */
	public List<BewonerTaak> selectThisWeek(String sysdate) {
		String query = "select * from bewoner_taak where datum = "
				+ "date(date_trunc('week', cast('" + sysdate + "' as timestamp))) + 6"
				+ "order by bewonerID";
		return select(query);
	}
	
	/**
	 * Insert een BewonerTaak in de database
	 * 
	 * @return - de nieuwe BewonerTaak of null
	 */
	public BewonerTaak insert(BewonerTaak bt) {
		BewonerTaak result = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik hem op de database
			String query = "insert into bewoner_taak (bewonerID, taakID, datum) "
					+ "values(?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, bt.getBewoner().getBewonerID());
			stmt.setInt(2, bt.getTaak().getTaakID());
			stmt.setDate(3, bt.getDatum());
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Haal de nieuwe BewonerTaak op uit de database
		result = selectByID(bt);
		return result;
	}
	
	/**
	 * Voegt voor elke bewoner een nieuwe BewonerTaak in in de database als het de
	 * eerste week is.
	 * 
	 * @param sysdate - Huidige datum in het systeem
	 * @return - De ingevoerde BewonerTaken
	 */
	public List<BewonerTaak> insertFirstWeek(String sysdate) {
		BewonerDAO bdao = new BewonerDAO();
		TaakDAO tdao = new TaakDAO();
		
		List<Bewoner> bewoners = bdao.selectAll();
		List<Taak> taken = tdao.selectAll();
		
		// Voeg taak "Vrij" toe tot er evenveel taken als bewoners zijn
		while (taken.size() < bewoners.size()) {
			taken.add(tdao.selectVrij());
		}
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Zet de huidige systeemdatum om naar een java.sql.Date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			java.util.Date date = sdf.parse(sysdate);
			Date sqlDate = new Date(date.getTime());
			
			// Maak een query voor elke bewoner en gebruik deze op de database
			for(int i = 0; i < bewoners.size(); i ++) {
				
				Bewoner b = bewoners.get(i);
				
				String query = "insert into bewoner_taak (bewonerID, taakID, datum, gedaan) "
						+ "values (?, ?, date(date_trunc('week', cast(? as timestamp))) + 6, ?)";
				
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setInt(1, b.getBewonerID());
				// Bereken welke taak de bewoner moet doen
				stmt.setInt(2, (taken.get(i % taken.size()).getTaakID()));
				stmt.setDate(3, sqlDate);
				stmt.setBoolean(4, false);
				stmt.executeUpdate();
				
				stmt.close();
			}

			con.close();
			
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		
		// Haal de nieuwe BewonerTaken op uit de database
		return selectThisWeek(sysdate);
	}
	
	/**
	 * Voegt voor elke bewoner een nieuwe BewonerTaak in in de database als het 
	 * niet de eerste week is.
	 * 
	 * @param sysdate - Huidige datum in het systeem
	 * @return - De ingevoerde BewonerTaken
	 */
	public List<BewonerTaak> insertWeek(String sysdate) {
		TaakDAO tdao = new TaakDAO();
		BewonerDAO bdao = new BewonerDAO();
		
		List<BewonerTaak> bts = selectLastWeek(sysdate);
		List<Taak> taken = tdao.selectAll();
		List<Bewoner> bewoners = bdao.selectAll();
		
		// Voeg taak "Vrij" toe tot er evenveel taken als bewoners zijn
		while (taken.size() < bewoners.size()) {
			taken.add(tdao.selectVrij());
		}
		
		// Bereken welke taak de eerste bewoner vorige week had
		BewonerTaak bt = bts.get(0);
		int shift = 0;
		for (int i = 0; i < taken.size(); i ++) {
			if (bt.getTaak().getTaakID() == taken.get(i).getTaakID()) {
				shift = i;
			}
		}
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {

			// Zet de systeemdatum om in een java.sql.Date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			java.util.Date date = sdf.parse(sysdate);
			Date sqlDate = new Date(date.getTime());
			
			// Maak een query voor elke bewoner en gebruik deze op de database
			for(int i = 0; i < bts.size(); i ++) {
				
				String query = "insert into bewoner_taak (bewonerID, taakID, datum, gedaan) "
						+ "values (?, ?, date(date_trunc('week', cast(? as timestamp))) + 6, ?)";
				
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setInt(1, bts.get(i).getBewoner().getBewonerID());
				// Bereken welke taak de bewoner moet doen
				stmt.setInt(2, taken.get((i + shift + 1) % taken.size()).getTaakID());
				stmt.setDate(3, sqlDate);
				stmt.setBoolean(4, false);
				stmt.executeUpdate();

				stmt.close();
			}

			con.close();
			
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		
		// Haal de nieuwe BewonerTaken op
		return selectThisWeek(sysdate);
	}
	
	/**
	 * Update attribuut gedaan van een BewonerTaak
	 * 
	 * @return - De aangepaste BewonerTaak
	 */
	public BewonerTaak update(BewonerTaak bt) {
		BewonerTaak result = null;
		
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik hem op de database
			String query = "update bewoner_taak set gedaan = ? where bewonerID = ? and taakID = ? and datum = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setBoolean(1, bt.isGedaan());
			stmt.setInt(2, bt.getBewoner().getBewonerID());
			stmt.setInt(3, bt.getTaak().getTaakID());
			stmt.setDate(4, bt.getDatum());
			stmt.executeUpdate();

			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Haal de aangepaste BewonerTaak op uit de database
		result = selectByID(bt);
		return result;
	}
	
	/**
	 * Verwijder een BewonerTaak uit de database
	 * 
	 * @return - True als het geslaagd is, false als het mislukt is
	 */
	public boolean delete(BewonerTaak bt) {
		// Maak connectie met de database
		try (Connection con = super.getConnection()) {
			
			// Maak de query en gebruik hem op de database
			String query = "delete from bewoner_taak where "
					+ "bewonerID = ? and taakID = ? and datum = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, bt.getBewoner().getBewonerID());
			stmt.setInt(2, bt.getTaak().getTaakID());
			stmt.setDate(3, bt.getDatum());
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
}