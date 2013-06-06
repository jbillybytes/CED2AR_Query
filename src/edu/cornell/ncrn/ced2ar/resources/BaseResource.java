package edu.cornell.ncrn.ced2ar.resources;
import java.util.HashMap;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import edu.cornell.ncrn.ced2ar.CED2AR_QUERY;

public class BaseResource extends ServerResource {

	@Get
	public Representation represent() {
		HashMap<String,String> pageData = new HashMap<String,String>();
		pageData.put("version", CED2AR_QUERY.version.toString());
		pageData.put("apiUrl", CED2AR_QUERY.apiUrl);
		Representation home = new StringRepresentation("Welcome!", MediaType.APPLICATION_XHTML);
		return home;
	}
	
}
