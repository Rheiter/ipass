package hu.ipass.webservices;

import java.util.*;

import javax.annotation.security.RolesAllowed;
import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.Bewoner;
import hu.ipass.model.BewonerTaak;
import hu.ipass.model.Taak;
import hu.ipass.persistence.BewonerDAO;
import hu.ipass.persistence.BewonerTaakDAO;
import hu.ipass.persistence.TaakDAO;

@Path("statistiek")
public class StatistiekResource {
	
	@GET
	@RolesAllowed("user")
	@Produces("application/json")
	public String statistiek() {
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		BewonerDAO bdao = new BewonerDAO();
		
		List<BewonerTaak> bts = btdao.selectAll();
		List<Bewoner> bewoners = bdao.selectAll();
		
		List<List<Integer>> outerList = new ArrayList<List<Integer>>();
		
		Calendar outerCal = null;
		
		// Voor elke BewonerTaak
		for (BewonerTaak bt : bts) {
			
			// Bepaal de datum en jaar, maand, dag
			Calendar innerCal = Calendar.getInstance();
			innerCal.setTime(bt.getDatum());
			int year = innerCal.get(Calendar.YEAR);
			int month = innerCal.get(Calendar.MONTH);
			int day = innerCal.get(Calendar.DAY_OF_MONTH);
			
			// Als deze datum al langsgeweest is
			if (innerCal.equals(outerCal)) {
				// Als de taak gedaan is, verhoog aantal gedaan met 1
				if (bt.isGedaan() == true) {
					List<Integer> innerList = outerList.get(outerList.size() - 1);
					innerList.set(3, innerList.get(3) + 1);
					outerList.set(outerList.size() - 1, innerList);
				}
			// Als de datum nieuw is
			} else {
				// Maak een nieuwe lijst met jaar, maand, dag en 0 gedaan
				List<Integer> innerList = new ArrayList<Integer>();
				innerList.addAll(Arrays.asList(year, month, day, 0));
				// Als de taak gedaan is, verhoog gedaan met 1
				if (bt.isGedaan() == true) {
					innerList.set(3, innerList.get(3) + 1);
				}
				// En voeg deze toe aan de outerList
				outerList.add(innerList);
				// Pas outerCal aan
				outerCal = innerCal;
			}
		}
		
		JsonArrayBuilder innerJab = Json.createArrayBuilder();
		JsonArrayBuilder outerJab = Json.createArrayBuilder();
		
		// Maak de json array
		for (List<Integer> list : outerList) {
			// Voeg ook percentage toe
			list.add((int) (list.get(3) / (double) bewoners.size() * 100));
			for (int i : list) {
				innerJab.add(i);
			}
			outerJab.add(innerJab);
		}
		
		// En verstuur de json array
		return outerJab.build().toString();
	}
	
	/**
	 * Haalt de data op uit de database die nodig is voor de taken statistiek
	 * 
	 * @return json array met voor elke taak een json array met de naam van de taak,
	 * 		het aantal keren gedaan en niet gedaan. 
	 */
	@GET
	@Path("taken")
	@RolesAllowed("user")
	@Produces("application/json")
	public String takenStatistiek() {
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		TaakDAO tdao = new TaakDAO();
		
		// Haal alle BewonerTaken van deze bewoner
		List<BewonerTaak> bts = btdao.selectAll();
		
		// Maak een lijst met alle taakID's van de actieve taken
		List<Integer> taken = new ArrayList<Integer>();
		// Maak een ArrayList met HashMaps met de taak, gedaan en niet gedaan
		List<Map<String, Object>> mapArray = new ArrayList<Map<String, Object>>();
		for (Taak t : tdao.selectAll()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taak", t.getNaam());
			map.put("gedaan", 0);
			map.put("nietGedaan", 0);
			mapArray.add(map);
			taken.add(t.getTaakID());
		}
		
		// Kijk voor elke BewonerTaak of de taak in de lijst met huidige taken zit
		for (BewonerTaak bt : bts) {
			if (taken.contains(bt.getTaak().getTaakID())) {
				// Vind de bijbehorende map in de lijst met hashmaps
				for (Map<String, Object> map : mapArray) {
					if (bt.getTaak().getNaam().equals(map.get("taak"))) {
						// En update gedaan
						if (bt.isGedaan() == true) {
							map.put("gedaan", (Integer) map.get("gedaan") + 1);
						// Of niet gedaan
						} else {
							map.put("nietGedaan", (Integer) map.get("nietGedaan") + 1);
						}
					}
				}
			}
		}
		
		JsonArrayBuilder innerJab = Json.createArrayBuilder();
		JsonArrayBuilder outerJab = Json.createArrayBuilder();
		
		// Maak de header kolom
		innerJab.add("Taak")
				.add("Gedaan")
				.add("Niet gedaan");
		outerJab.add(innerJab);
		
		// Maak de kolommen met data
		for (Map<String, Object> map : mapArray) {
			innerJab.add((String) map.get("taak"))
					.add((Integer) map.get("gedaan"))
					.add((Integer) map.get("nietGedaan"));
			outerJab.add(innerJab);
		}
		
		return outerJab.build().toString();
	}
}
