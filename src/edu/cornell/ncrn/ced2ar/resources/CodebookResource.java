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

public class CodebookResource extends ServerResource {

	private final static Logger logger = Logger.getLogger(CodebookResource.class.getName());

	@Get("xml|json")
	public Representation represent(Variant variant) {
		String codebookId = (String) getRequestAttributes().get("codebookId");

		String xquery;

		if (codebookId == null || codebookId.length() == 0) {
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			logger.warning("Error retrieving Codebook representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}

		// TODO: throw 404 if referenced codebook doesn't exist in database. 
		
		// Get the individual codebook
		//TODO: make this more robust (right now it's case sensitive)
		
		xquery = "for $ced2ar in collection('CED2AR')/codeBook where $ced2ar/docDscr/citation/titlStmt/titl='"
				+ codebookId + "' return $ced2ar";
		
		Representation xmlCodebook = new StringRepresentation("<xml>" + BaseXDataAccess.getXML(xquery) + "</xml>", MediaType.APPLICATION_XML);

		//TODO: Error checking xquery response, with appropriate status codes. JSON support.
		
		if (MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			this.setStatus(Status.SUCCESS_OK);
			return xmlCodebook;
		} else {
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			logger.warning("Error retrieving Codebook representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
	}

}