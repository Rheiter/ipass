package hu.ipass.webservices;

import java.util.*;

import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.Boete;
import hu.ipass.persistence.BoeteDAO;

@Path("/boetes")
public class BoeteResource {
	
	private JsonObjectBuilder boeteToJson(Boete b) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
			job.add("boeteID", b.getBoeteID())
				.add("bedrag", b.getBedrag());
			
			return job;
	}
	
	@GET
	@Produces("application/json")
	public String getAll() {
		BoeteDAO bdao = new BoeteDAO();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		List<Boete> boetes = bdao.selectAll();
		
		if (boetes == null) {
			throw new WebApplicationException("Geen boetes");
		}

		for (Boete b: boetes) {
			jab.add(boeteToJson(b));
		}
		
		JsonArray array = jab.build();
		return array.toString();
	}
	
	
	@POST
	@Produces("application/json")
	public String insert(@FormParam("boeteID") int boeteID,
							@FormParam("bedrag") int bedrag) {
		
		BoeteDAO bdao = new BoeteDAO();
		Boete result = null;
		Boete newBoete= new Boete(boeteID, bedrag);
		result = bdao.insert(newBoete);
		
		return boeteToJson(result).build().toString();
	}
	
	@PUT
	@Path("{boeteID}")
	@Produces("application/json")
	public String update(@PathParam("boeteID") int boeteID,
							@FormParam("bedrag") int bedrag) {
		
		BoeteDAO bdao = new BoeteDAO();
		Boete result = null;
		Boete newBoete = new Boete(boeteID, bedrag);
		result = bdao.update(newBoete);
		
		return boeteToJson(result).build().toString();
	}
	
	@DELETE
	@Path("{boeteID}")
	public boolean delete(@PathParam("boeteID") int boeteID) {
		BoeteDAO bdao = new BoeteDAO();
		return bdao.delete(boeteID);
	}
}