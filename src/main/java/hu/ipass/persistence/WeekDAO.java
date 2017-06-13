package hu.ipass.persistence;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import hu.ipass.model.Week;

public class WeekDAO extends BaseDAO {
	
	private List<Week> select(String query) {
		List<Week> results = new ArrayList<Week>();
		
		try (Connection con = super.getConnection()) {
			
			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			
			while (resultSet.next()) {
				int weekID = resultSet.getInt("weekID");
				Date datum = resultSet.getDate("datum");
				Week week = new Week(weekID, datum);
				results.add(week);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public List<Week> selectAll() {
		String query = "select * from week";
		return select(query);
	}
	
	public Week selectByID(int weekID) {
		String query = "select * from week where weekID = " + weekID;	
		return select(query).get(0);
	}
	
	public Week insert(Week w) {
		Week result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "insert into week(datum) values(?)";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setDate(1, w.getDatum());
			stmt.executeUpdate();
			
			result = selectByID(w.getWeekID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Week update(Week w) {
		Week result = null;
		
		try (Connection con = super.getConnection()) {
			
			String query = "update week set datum = ? where weekID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setDate(1, w.getDatum());
			stmt.setInt(2, w.getWeekID());
			stmt.executeUpdate();
			result = selectByID(w.getWeekID());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean delete(int weekID) {
		try (Connection con = super.getConnection()) {
			
			String query = "delete from week where weekID = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, weekID);
			stmt.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}