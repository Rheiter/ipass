package hu.ipass.webservices;

import java.util.List;

import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.BewonerTaakWeek;
import hu.ipass.persistence.BewonerTaakWeekDAO;

@Path("btw")
public class BewonerTaakWeekResource {
	
	private JsonObjectBuilder btwToJson(BewonerTaakWeek btw) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
			job.add("bewonerID", btw.getBewoner().getBewonerID())
				.add("TaakID", btw.getTaak().getTaakID())
				.add("weekID", btw.getWeek().getWeekID())
				.add("gedaan", btw.getGedaan());
			
			return job;
	}
	
	@GET
	@Produces("application/json")
	public String selectAll() {
		BewonerTaakWeekDAO btwdao = new BewonerTaakWeekDAO();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		List<BewonerTaakWeek> btws = btwdao.selectAll();
		
		if (btws == null) {
			throw new WebApplicationException("Geen bewoner-taak-week");
		}

		for (BewonerTaakWeek btw: btws) {
			jab.add(btwToJson(btw));
		}
		
		JsonArray array = jab.build();
		return array.toString();
	}
}
