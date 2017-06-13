package hu.ipass.model;

public class Taak {
	private int taakID;
	private String naam;
	private String omschrijving;
	
	public Taak(int taakID, String naam, String omschrijving) {
		super();
		this.taakID = taakID;
		this.naam = naam;
		this.omschrijving = omschrijving;
	}
	
	public int getTaakID() {
		return taakID;
	}
	
	public void setTaakID(int taakID) {
		this.taakID = taakID;
	}
	
	public String getNaam() {
		return naam;
	}
	
	public void setNaam(String naam) {
		this.naam = naam;
	}
	
	public String getOmschrijving() {
		return omschrijving;
	}
	
	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}
	
	@Override
	public String toString() {
		return "Taak [taakID=" + taakID + ", naam=" + naam + ", omschrijving=" + omschrijving + "]";
	}
}
