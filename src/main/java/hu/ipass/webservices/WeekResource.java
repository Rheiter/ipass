package hu.ipass.webservices;

import java.sql.Date;
import java.util.List;

import javax.json.*;
import javax.ws.rs.*;

import hu.ipass.model.Week;
import hu.ipass.persistence.WeekDAO;

@Path("/weken")
public class WeekResource {
	
	private JsonObjectBuilder weekToJson(Week w) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		
			job.add("weekID", w.getWeekID())
				.add("datum", w.getDatum().toString());
			
			return job;
	}
	
	@GET
	@Produces("application/json")
	public String getAll() {
		WeekDAO wdao = new WeekDAO();
		JsonArrayBuilder jab = Json.createArrayBuilder();
		List<Week> weken = wdao.selectAll();
		
		if (weken == null) {
			throw new WebApplicationException("Geen weken");
		}

		for (Week w : weken) {
			jab.add(weekToJson(w));
		}
		
		JsonArray array = jab.build();
		return array.toString();
	}
	
	
	@POST
	@Produces("application/json")
	public String insert(@FormParam("weekID") int weekID,
							@FormParam("datum") Date datum) {
		
		WeekDAO wdao = new WeekDAO();
		Week result = null;
		Week newWeek= new Week(weekID, datum);
		result = wdao.insert(newWeek);
		
		return weekToJson(result).build().toString();
	}
	
	@PUT
	@Path("{weekID}")
	@Produces("application/json")
	public String update(@PathParam("weekID") int weekID,
							@FormParam("datum") Date datum) {
		
		WeekDAO wdao = new WeekDAO();
		Week result = null;
		Week newWeek= new Week(weekID, datum);
		result = wdao.update(newWeek);
		
		return weekToJson(result).build().toString();
	}
	
	@DELETE
	@Path("{weekID}")
	public boolean delete(@PathParam("weekID") int weekID) {
		WeekDAO wdao = new WeekDAO();
		return wdao.delete(weekID);
	}
}