package hu.ipass.webservices;

import java.io.IOException;
import java.sql.Date;
import java.text.*;
import java.util.*;

import javax.annotation.security.RolesAllowed;
import javax.json.*;
import javax.ws.rs.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import hu.ipass.model.Bewoner;
import hu.ipass.model.BewonerTaak;
import hu.ipass.model.Taak;
import hu.ipass.persistence.BewonerDAO;
import hu.ipass.persistence.BewonerTaakDAO;
import hu.ipass.persistence.TaakDAO;

@Path("bewoner-taak")
public class BewonerTaakResource {
	
	/**
	 * Zet een BewonerTaak om naar een JsonObjectBuilder
	 * 
	 * @return - JsonObjectBuilder
	 */
	private JsonObjectBuilder btToJson(BewonerTaak bt) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
		job.add("bewonerID", bt.getBewoner().getBewonerID())
			.add("taakID", bt.getTaak().getTaakID())
			.add("datum", bt.getDatum().toString())
			.add("gedaan", bt.isGedaan());
			
		return job;
	}
	
	/**
	 * Haalt de BewonerTaken van deze week op en berekent de komende 10 weken
	 * 
	 * @return - Json array met voor elke week een json object met de datum en
	 * 		een json array met BewonerTaken
	 */
	@GET
	@RolesAllowed("user")
	@Produces("application/json")
	public String getAll(@QueryParam("date") String sysdate) {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		JsonObjectBuilder job = Json.createObjectBuilder();
		JsonArrayBuilder takenJab = Json.createArrayBuilder();
		JsonObjectBuilder taakJob = Json.createObjectBuilder();
		
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		TaakDAO tdao = new TaakDAO();
		BewonerDAO bdao = new BewonerDAO() ;
		
		// Haal de BewonerTaken van deze week op
		List<BewonerTaak> bts = btdao.selectThisWeek(sysdate);
		List<Taak> taken = tdao.selectAll();
		List<Bewoner> bewoners = bdao.selectAll();
		
		// Voeg taak "Vrij" toe tot er evenveel taken als bewoners zijn
		while (taken.size() < bewoners.size()) {
			taken.add(tdao.selectVrij());
		}
				
		// Maak nieuwe BewonerTaken als er nog geen zijn voor deze week
		if (bts.size() == 0) {
			// Haal de BewonerTaken op van vorige week
			bts = btdao.selectLastWeek(sysdate);
			if (bts.size() == 0) {
				// Als er nog geen zijn, maak BewonerTaken voor de eerste week
				bts = btdao.insertFirstWeek(sysdate);
			} else {
				// Anders maak BewonerTaken die aansluiten op vorige week
				bts = btdao.insertWeek(sysdate);
			}
		}
		
		Date datum = null;
		// Maar een json object voor elke BewonerTaak van deze week
		for (BewonerTaak bt : bts) {
			datum = bt.getDatum();
			taakJob.add("bewoner", bt.getBewoner().getGebruikersnaam())
					.add("bewonerID", bt.getBewoner().getBewonerID())
					.add("taak", bt.getTaak().getNaam())
					.add("taakID", bt.getTaak().getTaakID())
					.add("omschrijving", bt.getTaak().getOmschrijving())
					.add("boete", bt.getTaak().getBoete())
					.add("gedaan", bt.isGedaan());
			// Voeg het json object toe aan het taken array van deze week
			takenJab.add(taakJob);
		}
		
		// Voeg de datum en het taken array toe aan het json object van deze week
		job.add("datum", datum.toString())
			.add("taken", takenJab);
		jab.add(job);
		
		// Bereken welke taak de eerste bewoner deze week heeft en sla het verschil
		// tussen het nummer van de bewoner en het nummer van de taak op
		BewonerTaak bt = bts.get(0);
		int shift = 0;
		for (int i = 0; i < taken.size(); i ++) {
			if (bt.getTaak().getTaakID() == taken.get(i).getTaakID()) {
				shift = i;
			}
		}
		
		// Maak ook een json object voor de komende 9 weken
		for (int c = 1; c < 10; c ++) {
			
			// Maak een json object voor elke BewonerTaak
			for (int i = 0; i < bts.size(); i ++) {
				
				// Bereken de datum van de volgende week
				datum = bts.get(0).getDatum();
				Calendar cal = Calendar.getInstance();
				cal.setTime(datum);
				cal.add(Calendar.DAY_OF_YEAR, (c * 7));
				datum =  new Date(cal.getTimeInMillis());
				
				taakJob.add("bewoner", bts.get(i).getBewoner().getGebruikersnaam())
						.add("bewonerID", bts.get(i).getBewoner().getBewonerID())
						.add("taak", taken.get((i + c + shift) % taken.size()).getNaam())
						.add("taakID", taken.get((i + c + shift) % taken.size()).getTaakID())
						.add("omschrijving", taken.get((i + c + shift) % taken.size()).getOmschrijving())
						.add("boete", taken.get((i + c + shift) % taken.size()).getBoete());
				// Voeg het json object toe aan de taken array van deze week
				takenJab.add(taakJob);
			}
			
		// Voeg de datum en het taken array toe aan het json object van volgende week
		job.add("datum", datum.toString())
			.add("taken", takenJab);
		jab.add(job);
		}
		
		// Verstuur json array
		return jab.build().toString();
	}
	
	/**
	 * Update een BewonerTaak
	 * @param jsonString - BewonerTaak in een json string
	 * @return - json object met de aangepaste BewonerTaak
	 */
	@PUT
	@RolesAllowed("user")
	@Produces("application/json")
	public String update(String jsonString) throws ParseException, JsonProcessingException, IOException {
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		BewonerDAO bdao = new BewonerDAO();
		TaakDAO tdao = new TaakDAO();
		
		// Haal de waarden uit de json string
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = mapper.readTree(jsonString);
		int bewonerID = jsonObj.get("bewonerID").asInt();
		int taakID = jsonObj.get("taakID").asInt();
		boolean gedaan = jsonObj.get("gedaan").asBoolean();
		String datumString = jsonObj.get("datum").asText();
		
		// Zet de datum string om naar een java.sql.Date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date datum = new Date(sdf.parse(datumString).getTime());
		
		// Haal de bewoner en taak op
		Bewoner bewoner = bdao.selectByID(bewonerID);
		Taak taak = tdao.selectByID(taakID);
		// Maak een nieuwe bewonerTaak
		BewonerTaak bt = new BewonerTaak(bewoner, taak, datum, gedaan);
		
		// Update de BewonerTaak in de database en sla het resultaat op
		BewonerTaak result = btdao.update(bt);
		
		// Verstuur json object
		return btToJson(result).build().toString();
	}
	
	/**
	 * Update de BewonerTaken van de huidige week. Als in deze week de taak "Vrij"
	 * voorkomt, wordt de eerste instantie hiervan vervangen door de nieuwe taak.
	 * 
	 * @param sysdate - De huidige systeemdatum
	 * @param taakID - TaakID van nieuwe taak
	 * @return - Json array met de (eventueel) aangepaste BewonerTaken
	 */
	@PUT
	@RolesAllowed("user")
	@Path("nieuwe-taak/{taakID}")
	@Produces("application/json")
	public String updateThisWeekNewTaak (@PathParam("taakID") int taakID, String sysdate) {
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		TaakDAO tdao = new TaakDAO();
		
		List<BewonerTaak> bts = btdao.selectThisWeek(sysdate);
		Taak taak = tdao.selectByID(taakID);
		
		// Als de taak "Vrij" voorkomt in de huidige week
		for (BewonerTaak bt : bts) {
			if (bt.getTaak().getTaakID() == 9999) {
				// Verwijder de taak uit de database
				btdao.delete(bt);
				// Pas de taak aan
				bt.setTaak(taak);
				// Insert de nieuwe taak in de database
				btdao.insert(bt);
				// Alleen de eerste keer
				break;
			}
		}
		
		JsonArrayBuilder jab = Json.createArrayBuilder();
		
		// Bouw de json array
		for (BewonerTaak bt: bts) {
			jab.add(btToJson(bt));
		}
		
		// Verstuur json array
		return jab.build().toString();
	}
	
	/**
	 * Update de BewonerTaken van de huidige week. De verwijderde taak wordt
	 * vervangen door de taak "Vrij"
	 * 
	 * @param sysdate - De huidige systeemdatum
	 * @param taakID - TaakID van verwijderde taak
	 * @return - Json array met de (eventueel) aangepaste BewonerTaken
	 */
	@PUT
	@RolesAllowed("user")
	@Path("verwijder-taak/{taakID}")
	@Produces("application/json")
	public String updateThisWeekDelTaak (@PathParam("taakID") int taakID, String sysdate) {
		System.out.println("verwijder resource");
		
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		TaakDAO tdao = new TaakDAO();
		BewonerDAO bdao = new BewonerDAO();
		
		List<Taak> taken = tdao.selectAll();
		List<Bewoner> bewoners = bdao.selectAll();
		List<BewonerTaak> bts = btdao.selectThisWeek(sysdate);
		Taak vrij = tdao.selectByID(9999);
		
		// Vind de verwijderde taak in de huidige week in de database
		for (BewonerTaak bt : bts) {
			if (bt.getTaak().getTaakID() == taakID) {
				// Verwijder de taak uit de huidige week in de database
				btdao.delete(bt);
				// Als er minder taken dan bewoners zijn
				if (taken.size() < bewoners.size()) {
					// Verander de taak naar "Vrij"
					bt.setTaak(vrij);
					// Insert de nieuwe taak in de database
					btdao.insert(bt);
				
				// Als er meer taken dan bewoners zijn	
				} else {
					// Vind een taak die niet in deze week zit
					List<Integer> takenInWeek = new ArrayList<Integer>();
					
					for (BewonerTaak bt2 : bts) {
						takenInWeek.add(bt2.getTaak().getTaakID());
					}
					
					for (Taak t : taken) {
						if (!(takenInWeek.contains(t.getTaakID()))) {
							// Pas de taak aan
							bt.setTaak(t);
							// Insert de nieuwe taak in de database
							btdao.insert(bt);
						}
					}
				}
			}
		}
		
		JsonArrayBuilder jab = Json.createArrayBuilder();
		
		// Bouw de json array
		for (BewonerTaak bt: bts) {
			jab.add(btToJson(bt));
		}
		
		// Verstuur json array
		return jab.build().toString();
	}
}
