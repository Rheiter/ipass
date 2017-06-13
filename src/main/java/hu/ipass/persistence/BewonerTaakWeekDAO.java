package hu.ipass.persistence;

import java.sql.*;
import java.util.*;

import hu.ipass.model.*;

public class BewonerTaakWeekDAO extends BaseDAO {
	private BewonerDAO bdao = new BewonerDAO();
	private TaakDAO tdao = new TaakDAO();
	private WeekDAO wdao = new WeekDAO();
	
	private List<BewonerTaakWeek> select(String query) {
		List<BewonerTaakWeek> results = new ArrayList<BewonerTaakWeek>();
		
		try (Connection con = super.getConnection()) {
			
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			while (resultSet.next()) {
				int bewonerID = resultSet.getInt("bewonerID");
				int taakID = resultSet.getInt("taakID");
				int weekID = resultSet.getInt("weekID");
				boolean gedaan = resultSet.getBoolean("gedaan");
				Bewoner bewoner = bdao.selectByID(bewonerID);
				Taak taak = tdao.selectByID(taakID);
				Week week = wdao.selectByID(weekID);
				results.add(new BewonerTaakWeek(bewoner, taak, week, gedaan));
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public List<BewonerTaakWeek> selectAll() {
		String query = "select * from bewoner_taak_week";
		return select(query);
	}
	
	public BewonerTaakWeek selectByID(BewonerTaakWeek btw) {
		String query = String.format("select * from bewoner_taak_week where "
				+ "bewonerID = %d and taakID = %d and weekID = %d",
				btw.getBewoner().getBewonerID(), btw.getTaak().getTaakID(), btw.getWeek().getWeekID());
		List<BewonerTaakWeek> result = select(query);		
		return result.get(0);
	}
	
	public BewonerTaakWeek insert(BewonerTaakWeek btw) {
		BewonerTaakWeek result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "insert into bewoner_taak_week(bewonerID, taakID, "
					+ "weekID, gedaan) values (?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, btw.getBewoner().getBewonerID());
			stmt.setInt(2, btw.getTaak().getTaakID());
			stmt.setInt(3, btw.getWeek().getWeekID());
			stmt.setBoolean(4, btw.getGedaan());
			stmt.executeUpdate();
			
			result = selectByID(btw);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public BewonerTaakWeek update(BewonerTaakWeek btw) {
		BewonerTaakWeek result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "update bewoner_taak_week set gedaan = ? where "
					+ "bewonerID = ? and taakID = ? and weekID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setBoolean(1, btw.getGedaan());
			stmt.setInt(2, btw.getBewoner().getBewonerID());
			stmt.setInt(3, btw.getTaak().getTaakID());
			stmt.setInt(4, btw.getWeek().getWeekID());
			stmt.executeUpdate();
			
			result = selectByID(btw);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean delete(BewonerTaakWeek btw) {
		try (Connection con = super.getConnection()) {
			
			String query = "delete from bewoner where bewonerID = ? and "
					+ "taakID = ? and weekID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, btw.getBewoner().getBewonerID());
			stmt.setInt(2, btw.getTaak().getTaakID());
			stmt.setInt(3, btw.getWeek().getWeekID());
			stmt.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}