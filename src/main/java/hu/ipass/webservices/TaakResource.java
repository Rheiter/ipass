package hu.ipass.webservices;

import java.util.*;

import javax.annotation.security.RolesAllowed;
import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.Taak;
import hu.ipass.persistence.TaakDAO;

@Path("taken")
public class TaakResource {
	
	/**
	 * Zet een Taak om naar een JsonObjectBuilder
	 * 
	 * @param t - Taak
	 * @return - JsonObjectBuilder
	 */
	private JsonObjectBuilder taakToJson(Taak t) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
			job.add("taakID", t.getTaakID())
				.add("naam", t.getNaam())
				.add("omschrijving", t.getOmschrijving())
				.add("boete", t.getBoete())
				.add("actief", t.isActief());
			
			return job;
	}
	
	/**
	 * Haalt alle taken die actief zijn op uit de database.
	 * 
	 * @return - Json array met een json object voor elke taak
	 */
	@GET
	@RolesAllowed("user")
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
	
	/**
	 * Insert een nieuwe taak in de database.
	 * 
	 * @return Json object met de nieuwe taak
	 */
	@POST
	@RolesAllowed("user")
	@Produces("application/json")
	public String insert(@FormParam("naam") String naam,
							@FormParam("omschrijving") String omschrijving,
							@FormParam("boete") double boete) {
		
		TaakDAO tdao = new TaakDAO();
		Taak result = null;
		Taak newTaak = new Taak(naam, omschrijving, boete);
		result = tdao.insert(newTaak);
		
		return taakToJson(result).build().toString();
	}
	
	/**
	 * Update een taak in de database.
	 * 
	 * @return - Json object met de aangepaste taak of null
	 */
	@PUT
	@RolesAllowed("user")
	@Path("{taakID}")
	@Produces("application/json")
	public String update(@PathParam("taakID") int taakID,
							@FormParam("naam") String naam,
							@FormParam("omschrijving") String omschrijving,
							@FormParam("boete") double boete) {
		
		TaakDAO tdao = new TaakDAO();
		Taak newTaak = new Taak(taakID, naam, omschrijving, boete);
		Taak result = tdao.update(newTaak);
		
		return taakToJson(result).build().toString();
	}
	
	/**
	 * Verwijder een taak uit de database.
	 * 
	 * @return - true als het geslaagd is, false als het mislukt is
	 */
	@DELETE
	@RolesAllowed("user")
	@Path("{taakID}")
	@Produces("application/json")
	public String delete(@PathParam("taakID") int taakID) {
		TaakDAO tdao = new TaakDAO();
		
		Taak result = tdao.delete(taakID);
		
		return taakToJson(result).build().toString();
	}
}