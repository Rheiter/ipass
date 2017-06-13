package hu.ipass.webservices;

import java.util.*;

import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.Taak;
import hu.ipass.persistence.TaakDAO;

@Path("/taken")
public class TaakResource {
	
	private JsonObjectBuilder taakToJson(Taak t) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
			job.add("taakID", t.getTaakID())
				.add("naam", t.getNaam())
				.add("omschrijving", t.getOmschrijving());
			
			return job;
	}
	
	@GET
	@Produces("application/json")
	public String getAll() {
		TaakDAO tdao = new TaakDAO();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		List<Taak> taken = tdao.selectAll();
		
		if (taken == null) {
			throw new WebApplicationException("Geen taken");
		}

		for (Taak t : taken) {
			jab.add(taakToJson(t));
		}
		
		JsonArray array = jab.build();
		return array.toString();
	}
	
	
	@POST
	@Produces("application/json")
	public String insert(@FormParam("naam") String naam,
							@FormParam("omschrijving") String omschrijving) {
		
		TaakDAO tdao = new TaakDAO();
		Taak result = null;
		Taak newTaak = new Taak(0, naam, omschrijving);
		result = tdao.insert(newTaak);
		
		return taakToJson(result).build().toString();
	}
	
	@PUT
	@Path("{taakID}")
	@Produces("application/json")
	public String update(@PathParam("taakID") int taakID,
							@FormParam("naam") String naam,
							@FormParam("omschrijving") String omschrijving) {
		
		TaakDAO tdao = new TaakDAO();
		Taak result = null;
		Taak newTaak = new Taak(taakID, naam, omschrijving);
		result = tdao.update(newTaak);
		
		return taakToJson(result).build().toString();
	}
	
	@DELETE
	@Path("{taakID}")
	public boolean delete(@PathParam("taakID") int taakID) {
		TaakDAO tdao = new TaakDAO();
		return tdao.delete(taakID);
	}
}