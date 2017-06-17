package hu.ipass.webservices;

import java.security.Key;
import java.util.Calendar;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import hu.ipass.persistence.BewonerDAO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;

@Path("authentication")
public class AuthenticationResource {
	final static public Key key = MacProvider.generateKey();
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("gebruikersnaam") String username,
										@FormParam("wachtwoord") String password) {
		try {
			// Authenticate the user against the database
			BewonerDAO bdao = new BewonerDAO();
			String role = bdao.findRoleForUsernameAndPassword(username, password);
	
			if (role == null) { throw new IllegalArgumentException("No user found!"); }
	
			// Issue a token for the user
			Calendar expiration = Calendar.getInstance();
			expiration.add(Calendar.MINUTE, 300);
	
			String token = Jwts.builder()
					.setSubject(username)
					.claim("role", role)
					.setExpiration(expiration.getTime())
					.signWith(SignatureAlgorithm.HS512, key)
					.compact();
			// Return the token on the response
			return Response.ok(token).build();
		} catch (JwtException | IllegalArgumentException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	@GET
	@Path("check")
	@RolesAllowed("user")
	public void check() {
		
	}
}