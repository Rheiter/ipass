package hu.ipass.model;

public class Bewoner {
	private int bewonerID;
	private String gebruikersnaam;
	private String wachtwoord;
	private double schuld;
	
	public Bewoner(int bewonerID, String gebruikersnaam, String wachtwoord, double schuld) {
		super();
		this.bewonerID = bewonerID;
		this.gebruikersnaam = gebruikersnaam;
		this.wachtwoord = wachtwoord;
		this.schuld = schuld;
	}

	public int getBewonerID() {
		return bewonerID;
	}

	public void setBewonerID(int bewonerID) {
		this.bewonerID = bewonerID;
	}

	public String getGebruikersnaam() {
		return gebruikersnaam;
	}

	public void setGebruikersnaam(String gebruikersnaam) {
		this.gebruikersnaam = gebruikersnaam;
	}

	public String getWachtwoord() {
		return wachtwoord;
	}

	public void setWachtwoord(String wachtwoord) {
		this.wachtwoord = wachtwoord;
	}

	public double getSchuld() {
		return schuld;
	}

	public void setSchuld(double schuld) {
		this.schuld = schuld;
	}

	@Override
	public String toString() {
		return "Bewoner [bewonerID=" + bewonerID + ", gebruikersnaam=" + gebruikersnaam + ", wachtwoord=" + wachtwoord
				+ ", schuld=" + schuld + "]";
	}
}
