package edu.cornell.ncrn.ced2ar.resources;
import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.cornell.ncrn.ced2ar.SearchQuery;
import edu.cornell.ncrn.ced2ar.dataaccess.BaseXDataAccess;

public class SearchResource extends ServerResource {
	
	private final static Logger logger = Logger.getLogger(SearchResource.class.getName());
			
	@Override
    public void doInit() throws ResourceException {
        setNegotiated(true);
    } 
    
	@Get("xml|json")
	public Representation represent(Variant variant) {		
		String returnField = (String)getQuery().getFirstValue("return");
		//logger.info(returnField + " is the return field.");
		String where = (String) getQuery().getFirstValue("where");
		String sort = (String) getQuery().getFirstValue("sort");
		String limit = (String) getQuery().getFirstValue("limit");

		SearchQuery sQuery;
		try {
			sQuery = new SearchQuery(returnField, where, sort, limit);

			// validate incoming request
			sQuery.validateRequest();

			if (sQuery.ERROR_MSG != null && sQuery.ERROR_MSG != "") {
				logger.warning("Error parsing search request: "
						+ sQuery.ERROR_MSG);
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						sQuery.ERROR_MSG);
			} else {
				// TODO: add different representations (see other resource
				// classes)
				// TODO: add error handling in case the xquery doesn't return
				// any results, etc..,
				String xquery = sQuery.getXquery();
				String queryResult = BaseXDataAccess.getXML(xquery);
				
				logger.info(xquery);
				Representation xmlVariable = new StringRepresentation("<xml>"
						+ queryResult + "</xml>", MediaType.APPLICATION_XML);
				this.setStatus(Status.SUCCESS_OK);
				return xmlVariable;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					e.getMessage());			
		}
	}
}