package hu.ipass.model;

public class BewonerTaakWeek {
	private Bewoner bewoner;
	private Taak taak;
	private Week week;
	private boolean gedaan;
	
	public BewonerTaakWeek(Bewoner bewoner, Taak taak, Week week, boolean gedaan) {
		super();
		this.bewoner = bewoner;
		this.taak = taak;
		this.week = week;
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

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}
	
	public boolean getGedaan() {
		return gedaan;
	}
	
	public void setGedaan(boolean gedaan) {
		this.gedaan = gedaan;
	}

	@Override
	public String toString() {
		return "BewonerTaakWeek [bewoner=" + bewoner + ", taak=" + taak + ", week=" + week + ", gedaan=" + gedaan + "]";
	}
}
