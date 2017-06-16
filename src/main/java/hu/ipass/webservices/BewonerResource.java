package hu.ipass.webservices;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.security.RolesAllowed;
import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.Bewoner;
import hu.ipass.model.BewonerTaak;
import hu.ipass.model.Taak;
import hu.ipass.persistence.BewonerDAO;
import hu.ipass.persistence.BewonerTaakDAO;
import hu.ipass.persistence.TaakDAO;

@Path("bewoners")
public class BewonerResource {
	
	/**
	 * Zet een Bewoner om naar een JsonObjectBuilder
	 * 
	 * @param b - Bewoner
	 * @return - JsonObjectBuilder
	 */
	private JsonObjectBuilder bewonerToJson(Bewoner b) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
			job.add("bewonerID", b.getBewonerID())
				.add("gebruikersnaam", b.getGebruikersnaam())
				.add("wachtwoord", b.getWachtwoord())
				.add("schuld", b.getSchuld());
			
			return job;
	}
	
	/**
	 * Haalt alle bewoners op uit de database.
	 * 
	 * @return - Json array met een json object voor elke bewoner
	 */
	@GET
	@RolesAllowed("user")
	@Produces("application/json")
	public String getAll() {
		BewonerDAO bdao = new BewonerDAO();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		List<Bewoner> bewoners = bdao.selectAll();
		
		if (bewoners == null) {
			throw new WebApplicationException("Geen bewoners");
		}

		for (Bewoner b: bewoners) {
			jab.add(bewonerToJson(b));
		}
		
		JsonArray array = jab.build();
		return array.toString();
	}
	
	// Wordt niet gebruikt ######################################################
	/**
	 * Insert een nieuwe bewoner in de database.
	 * 
	 * @return Json object met de nieuwe bewoner
	 */
	@POST
	@RolesAllowed("user")
	@Produces("application/json")
	public String insert(@FormParam("bewonerID") int bewonerID,
							@FormParam("gebruikersnaam") String gebruikersnaam,
							@FormParam("wachtwoord") String wachtwoord,
							@FormParam("schuld") double schuld) {
		
		BewonerDAO bdao = new BewonerDAO();
		Bewoner result = null;
		Bewoner newBewoner = new Bewoner(bewonerID, gebruikersnaam, wachtwoord, schuld);
		result = bdao.insert(newBewoner);
		
		return bewonerToJson(result).build().toString();
	}
	
	/**
	 * Update de schuld van bewoners.
	 * 
	 * @param sysdate - String met de datum die op dit moment gebruikt wordt.
	 * @return - Json array met json objecten voor elke aangepaste bewoner
	 */
	@PUT
	@RolesAllowed("user")
	@Path("schuld")
	@Produces("application/json")
	public String updateSchuld(@QueryParam("date") String sysdate) {
		BewonerDAO bdao = new BewonerDAO();
		
		List<Bewoner> result = new ArrayList<Bewoner>();
		result = bdao.updateSchuld(sysdate);
		
		JsonArrayBuilder jab = Json.createArrayBuilder();
		
		for (Bewoner b : result) {
			jab.add(bewonerToJson(b));
		}
		
		return jab.build().toString();
	}
	
	/**
	 * Bereken wie geld moet betalen en wie geld moet ontvangen.
	 * 
	 * @return - Json array met een json object voor elke bewoner en 
	 * 		een json array met strings
	 * @throws Exception - Returned alleen als het resetten van de schulden met
	 * 		resetSchuld() gelukt is.
	 */
	@PUT
	@RolesAllowed("user")
	@Path("schuld/uitkeren")
	@Produces("application/json")
	public String uitkeren() throws Exception {
		BewonerDAO bdao = new BewonerDAO();
		
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		List<Bewoner> bewoners = bdao.selectAll();
		int totaal = bdao.selectTotaalSchuld();
		
		// Geef de schuld tijdelijk de waarde van het te ontvangen bedrag
		// en zet de namen in een hashmap
		for (Bewoner b : bewoners) {
			map.put(b.getGebruikersnaam(), new ArrayList<String>());
			b.setSchuld(totaal / bewoners.size() - b.getSchuld());
		}
		
		// Verdeling berekenen en in map zetten
		for (Bewoner b : bewoners) {
			if (b.getSchuld() >= 0) {
				while (b.getSchuld() > 0) {
					for (Bewoner b2 : bewoners) {
						if (b.getBewonerID() == b2.getBewonerID()) {
							continue;
						} else if (b2.getSchuld() >= 0) {
							continue;
						} else if (b2.getSchuld() + b.getSchuld() <= 0) {
							map.get(b.getGebruikersnaam()).add("€" + b.getSchuld() + " te ontvangen van " + b2.getGebruikersnaam());
							map.get(b2.getGebruikersnaam()).add("€" + b.getSchuld() + " te betalen aan " + b.getGebruikersnaam());
							b2.setSchuld(b2.getSchuld() + b.getSchuld());
							b.setSchuld(0);
							break;
						} else {
							map.get(b.getGebruikersnaam()).add("€" + -b2.getSchuld() + " te ontvangen van " + b2.getGebruikersnaam());
							map.get(b2.getGebruikersnaam()).add("€" + -b2.getSchuld() + " te betalen aan " + b.getGebruikersnaam());
							b.setSchuld(b.getSchuld() + b2.getSchuld());
							b2.setSchuld(0);
						}
					}
				}
			}
		}
		
		JsonArrayBuilder stringJab = Json.createArrayBuilder();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		JsonObjectBuilder job = Json.createObjectBuilder();
		
		// HashMap aan json toevoegen
		for (Entry<String, List<String>> e : map.entrySet()) {
			for (String s : e.getValue()) {
				stringJab.add(s);
			}
			job.add("betalingen", stringJab)
				.add("bewoner", e.getKey());
			jab.add(job);
		}
		
		
		if (bdao.resetSchuld() == true) {
			// En versturen
			return jab.build().toString();
		} else {
			throw new Exception("Schuld resetten mislukt");
		}
	}
	
	// Wordt niet gebruikt ######################################################
	/**
	 * Update een bewoner in de database.
	 * 
	 * @return - Json object met de aangepaste bewoner of null
	 */
	@PUT
	@RolesAllowed("user")
	@Path("{bewonerID}")
	@Produces("application/json")
	public String update(@PathParam("bewonerID") int bewonerID,
							@FormParam("gebruikersnaam") String gebruikersnaam,
							@FormParam("wachtwoord") String wachtwoord,
							@FormParam("schuld") double schuld) {
		
		BewonerDAO bdao = new BewonerDAO();
		Bewoner result = null;
		Bewoner newBewoner = new Bewoner(bewonerID, gebruikersnaam, wachtwoord, schuld);
		result = bdao.update(newBewoner);
		
		return bewonerToJson(result).build().toString();
	}
	
	// Wordt niet gebruikt ######################################################
	/**
	 * Verwijder een bewoner uit de database.
	 * 
	 * @param bewonerID - bewonerID van de te verwijderen bewoner
	 * @return - true als het geslaagd is, false als het mislukt is
	 */
	@DELETE
	@RolesAllowed("user")
	@Path("{bewonerID}")
	public boolean delete(@PathParam("bewonerID") int bewonerID) {
		BewonerDAO bdao = new BewonerDAO();
		return bdao.delete(bewonerID);
	}
}