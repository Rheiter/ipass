package hu.ipass.webservices;

import java.io.IOException;
import java.sql.Date;
import java.text.*;
import java.util.*;

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
	
	private JsonObjectBuilder btToJson(BewonerTaak bt) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
		job.add("bewonerID", bt.getBewoner().getBewonerID())
			.add("taakID", bt.getTaak().getTaakID())
			.add("datum", bt.getDatum().toString())
			.add("gedaan", bt.isGedaan());
			
		return job;
	}
	
	@GET
	@Path("next10")
	@Produces("application/json")
	public String next10(@QueryParam("date") String sysdate) {		
		JsonArrayBuilder jab = Json.createArrayBuilder();
		JsonObjectBuilder job = Json.createObjectBuilder();
		JsonArrayBuilder takenJab = Json.createArrayBuilder();
		JsonObjectBuilder taakJob = Json.createObjectBuilder();
		
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		List<BewonerTaak> bts = btdao.selectNext10Weeks(sysdate);
		List<Date> weken = new ArrayList<Date>();
		
		if (bts == null) {
			throw new WebApplicationException("Geen bewoner-taak");
		}
		
		Date datum = null;
		for (BewonerTaak bt: bts) {
			
			if (datum != null && datum.before(bt.getDatum())) {
				job.add("datum", datum.toString())
					.add("taken", takenJab);
				jab.add(job);
			}
			
			datum = bt.getDatum();
			if (!(weken.contains(datum))) {
				weken.add(datum);
			}
			
			taakJob.add("bewoner", bt.getBewoner().getGebruikersnaam())
					.add("bewonerID", bt.getBewoner().getBewonerID())
					.add("taak", bt.getTaak().getNaam())
					.add("taakID", bt.getTaak().getTaakID())
					.add("omschrijving", bt.getTaak().getOmschrijving())
					.add("gedaan", bt.isGedaan());
			takenJab.add(taakJob);	
		}
		
		if (weken.size() < 10) {
			if (btdao.insert10(weken.size(), sysdate) == true) {
				return next10(sysdate);
			}
			
		}

		job.add("datum", datum.toString())
			.add("taken", takenJab);
		jab.add(job);
		
		JsonArray array = jab.build();
		
		return array.toString();			
	}
	
	@GET
	@Produces("application/json")
	public String getAll() {
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		List<BewonerTaak> bts = btdao.selectAll();
		
		if (bts == null) {
			throw new WebApplicationException("Geen bewoner-taken");
		}

		for (BewonerTaak bt : bts) {
			jab.add(btToJson(bt));
		}
		
		JsonArray array = jab.build();
		return array.toString();
	}
	
	@PUT
	//@Produces("application/json")
	public String update(String jsonString) throws ParseException, JsonProcessingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = mapper.readTree(jsonString);
		int bewonerID = jsonObj.get("bewonerID").asInt();
		int taakID = jsonObj.get("taakID").asInt();
		String datumString = jsonObj.get("datum").asText();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date datum = new Date(sdf.parse(datumString).getTime());
		boolean gedaan = jsonObj.get("gedaan").asBoolean();
		
		BewonerTaakDAO btdao = new BewonerTaakDAO();
		BewonerDAO bdao = new BewonerDAO();
		TaakDAO tdao = new TaakDAO();
		
		Bewoner bewoner = bdao.selectByID(bewonerID);
		Taak taak = tdao.selectByID(taakID);
		BewonerTaak bt = new BewonerTaak(bewoner, taak, datum, gedaan);
		
		BewonerTaak result = btdao.update(bt);
		
		return btToJson(result).build().toString();
	}
}
