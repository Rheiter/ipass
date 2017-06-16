package hu.ipass.model;

public class Taak {
	private int taakID;
	private String naam;
	private String omschrijving;
	private double boete;
	private boolean actief;
	
	public Taak(int taakID, String naam, String omschrijving, double boete, boolean actief) {
		super();
		this.taakID = taakID;
		this.naam = naam;
		this.omschrijving = omschrijving;
		this.boete = boete;
		this.actief = actief;
	}
	
	public Taak(int taakID, String naam, String omschrijving, double boete) {
		super();
		this.taakID = taakID;
		this.naam = naam;
		this.omschrijving = omschrijving;
		this.boete = boete;	
	}
	
	public Taak(String naam, String omschrijving, double boete) {
		super();
		this.naam = naam;
		this.omschrijving = omschrijving;
		this.boete = boete;
		this.actief = true;
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
	
	public double getBoete() {
		return boete;
	}

	public void setBoete(double boete) {
		this.boete = boete;
	}

	public boolean isActief() {
		return actief;
	}

	public void setActief(boolean actief) {
		this.actief = actief;
	}

	@Override
	public String toString() {
		return "Taak [taakID=" + taakID + ", naam=" + naam + ", omschrijving=" + omschrijving + ", boete=" + boete + ", actief=" + actief + "]";
	}
}
