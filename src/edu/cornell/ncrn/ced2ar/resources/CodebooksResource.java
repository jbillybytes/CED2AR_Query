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

import edu.cornell.ncrn.ced2ar.dataaccess.BaseXDataAccess;

public class CodebooksResource extends ServerResource {

	private final static Logger logger = Logger.getLogger(CodebooksResource.class.getName());
	
    @Override
    public void doInit() throws ResourceException {
        setNegotiated(true);
    } 
	
	@Get("xml|json")
	public Representation represent(Variant variant) {
		String xquery = "for $ced2ar in collection('CED2AR') /codeBook/docDscr/citation/titlStmt/titl return $ced2ar";
		
		// Get the codebook list
		//TODO: make this more robust (right now it's case sensitive)
		String queryResult = "<xml>" + BaseXDataAccess.getXML(xquery) + "</xml>";

		//TODO: Error checking xquery response, with appropriate status codes. And a declaration. And JSON support.

		if (MediaType.TEXT_XML.equals(variant.getMediaType()) || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			Representation xmlCodebookList = new StringRepresentation(queryResult, MediaType.APPLICATION_XML);
			this.setStatus(Status.SUCCESS_OK);
			return xmlCodebookList;
		} else {
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			logger.warning("Error retrieving Codebook representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
	}
}