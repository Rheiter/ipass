package hu.ipass.persistence;

import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.time.temporal.*;
import java.util.*;

import hu.ipass.model.Bewoner;
import hu.ipass.model.BewonerTaak;
import hu.ipass.model.Taak;

public class BewonerTaakDAO extends BaseDAO {
	private BewonerDAO bdao = new BewonerDAO();
	private TaakDAO tdao = new TaakDAO();
	
	private List<BewonerTaak> select(String query) {
		List<BewonerTaak> results = new ArrayList<BewonerTaak>();
		
		try (Connection con = super.getConnection()) {
			
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			while (resultSet.next()) {
				int bewonerID = resultSet.getInt("bewonerID");
				int taakID = resultSet.getInt("taakID");
				Date datum = resultSet.getDate("datum");
				boolean gedaan = resultSet.getBoolean("gedaan");
				Bewoner bewoner = bdao.selectByID(bewonerID);
				Taak taak = tdao.selectByID(taakID);
				results.add(new BewonerTaak(bewoner, taak, datum, gedaan));
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	
	public List<BewonerTaak> selectAll() {
		String query = "select * from bewoner_taak order by datum, bewonerID";
		return select(query);
	}
	
	public BewonerTaak selectByID(BewonerTaak bt) {
		String query = String.format("select * from bewoner_taak where "
				+ "bewonerID = %d and taakID = %d and datum = to_date('" + bt.getDatum() + "', 'YYYY-MM-DD') order by datum, bewonerID",
				bt.getBewoner().getBewonerID(), bt.getTaak().getTaakID());
		return select(query).get(0);
	}
	
	public List<BewonerTaak> selectNext10Weeks(String sysdate) {
		String query = "select * from bewoner_taak where datum >= '"
				+ sysdate + "' order by datum, bewonerID";
		return select(query);
	}
	
	public boolean insert10(int week, String sysdate) {
		List<Bewoner> bewoners = bdao.selectAll();
		List<Taak> taken = tdao.selectAll();
		List<BewonerTaak> bts = selectAll();
		
		int c = 0;
		if (bts.size() > 0) {
			while (true) {
				if (taken.get(bewoners.size() % taken.size()).getTaakID() == bts.get(bts.size() - 1).getTaak().getTaakID()) {
					c += 1;
				} else {
					System.out.println(c);
					break;
				}
			}
		}
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		try (Connection con = super.getConnection()) {
			
			for (int i = 0; i < (10 - week); i++) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(sdf.parse(sysdate));
				cal.add(Calendar.DAY_OF_YEAR, ((week + i) * 7));
				Date sqlDate = new Date(cal.getTimeInMillis());
				java.util.Date datum = new java.util.Date();
				datum.toInstant().plus(((week + i) * 7), ChronoUnit.DAYS);
				
				for (int j = 0; j < bewoners.size(); j++) {
					Bewoner b = bewoners.get(j);
					String query = "insert into bewoner_taak (bewonerID, taakID, datum, gedaan) "
							+ "values (?, ?, date(date_trunc('week', cast(? as timestamp))) + 6, ?)";
					PreparedStatement stmt = con.prepareStatement(query);
					stmt.setInt(1, b.getBewonerID());
					stmt.setInt(2, (taken.get((j + i + c) % taken.size()).getTaakID()));
					stmt.setDate(3, sqlDate);
					stmt.setBoolean(4, false);
					stmt.executeUpdate();
				}
			}
			
			return true;
			
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	public BewonerTaak update(BewonerTaak bt) {
		BewonerTaak result = null;
		
		try (Connection con = super.getConnection()) {
			String query = "update bewoner_taak set gedaan = ? where bewonerID = ? and taakID = ? and datum = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setBoolean(1, bt.isGedaan());
			stmt.setInt(2, bt.getBewoner().getBewonerID());
			stmt.setInt(3, bt.getTaak().getTaakID());
			stmt.setDate(4, bt.getDatum());
			stmt.executeUpdate();
			
			result = selectByID(bt);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
