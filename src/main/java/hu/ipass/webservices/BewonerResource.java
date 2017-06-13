package hu.ipass.webservices;

import java.util.*;

import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.Bewoner;
import hu.ipass.persistence.BewonerDAO;

@Path("/bewoners")
public class BewonerResource {
	
	private JsonObjectBuilder bewonerToJson(Bewoner b) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
			job.add("bewonerID", b.getBewonerID())
				.add("gebruikersnaam", b.getGebruikersnaam())
				.add("wachtwoord", b.getWachtwoord())
				.add("schuld", b.getSchuld());
			
			return job;
	}
	
	@GET
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
	
	
	@POST
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
	
	@PUT
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
	
	@DELETE
	@Path("{bewonerID}")
	public boolean delete(@PathParam("bewonerID") int bewonerID) {
		BewonerDAO bdao = new BewonerDAO();
		return bdao.delete(bewonerID);
	}
}