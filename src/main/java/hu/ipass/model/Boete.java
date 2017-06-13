package hu.ipass.model;

public class Boete {
	private int boeteID;
	private double bedrag;
	
	public Boete(int boeteID, double bedrag) {
		super();
		this.boeteID = boeteID;
		this.bedrag = bedrag;
	}

	public int getBoeteID() {
		return boeteID;
	}

	public void setBoeteID(int boeteID) {
		this.boeteID = boeteID;
	}

	public double getBedrag() {
		return bedrag;
	}

	public void setBedrag(double bedrag) {
		this.bedrag = bedrag;
	}

	@Override
	public String toString() {
		return "Boete [boeteID=" + boeteID + ", bedrag=" + bedrag + "]";
	}
}
