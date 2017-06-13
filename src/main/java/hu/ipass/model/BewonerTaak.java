package hu.ipass.model;

import java.sql.Date;

public class BewonerTaak {
	private Bewoner bewoner;
	private Taak taak;
	private Date datum;
	private boolean gedaan;
	
	public BewonerTaak(Bewoner bewoner, Taak taak, Date datum, boolean gedaan) {
		super();
		this.bewoner = bewoner;
		this.taak = taak;
		this.datum = datum;
		this.gedaan = gedaan;
	}

	public Bewoner getBewoner() {
		return bewoner;
	}

	public void setBewoner(Bewoner bewoner) {
		this.bewoner = bewoner;
	}

	public Taak getTaak() {
		return taak;
	}

	public void setTaak(Taak taak) {
		this.taak = taak;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public boolean isGedaan() {
		return gedaan;
	}

	public void setGedaan(boolean gedaan) {
		this.gedaan = gedaan;
	}

	@Override
	public String toString() {
		return "BewonerTaak [bewoner=" + bewoner + ", taak=" + taak + ", datum=" + datum + ", gedaan=" + gedaan + "]";
	}
}
