package hu.ipass.model;

import java.sql.Date;

public class Week {
	private int weekID;
	private Date datum;
	
	public Week(int weekID, Date datum) {
		super();
		this.weekID = weekID;
		this.datum = datum;
	}

	public int getWeekID() {
		return weekID;
	}

	public void setWeekID(int weekID) {
		this.weekID = weekID;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	@Override
	public String toString() {
		return "Week [weekID=" + weekID + ", datum=" + datum + "]";
	}
}
