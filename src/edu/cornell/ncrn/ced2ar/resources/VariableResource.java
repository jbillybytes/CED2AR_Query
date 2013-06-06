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

public class VariableResource extends ServerResource{
	
	private final static Logger logger = Logger.getLogger(VariableResource.class.getName());

    @Override
    public void doInit() throws ResourceException {
        setNegotiated(true);
    } 
	
	@Get("xml|json")
	public Representation represent(Variant variant) {	
		
		// get the codebook id
		// TODO: error checking, should 404 if the codebook does not exist
		String codebookId = (String) getRequestAttributes().get("codebookId");

		if (codebookId == null || codebookId.length() == 0) {
			String message = " \"" + codebookId + "\" is an invalid codebookId";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
		
		// get the variable id
		// TODO: error checking, should 404 if the variable does not exist
		String variableId = (String) getRequestAttributes().get("variableId");

		if (variableId == null || variableId.length() == 0) {
			String message = " \"" + variableId + "\" is an invalid variableId";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}
			
		String xquery = "	let $ced2ar := collection('CED2AR') " +
				"for $codebook in $ced2ar/codeBook " +
				"where lower-case($codebook/docDscr/citation/titlStmt/titl) = '" + codebookId.toLowerCase() + "' " +
				"return " + 
				"<codeBook> { $codebook/docDscr }<dataDscr> { " +
				"for $var in $codebook/dataDscr/var " +
				"where  $var[lower-case(@name) = '" + variableId.toLowerCase() + "']        return   $var " + 
				"} </dataDscr></codeBook>";

		String queryResult = "<xml>" + BaseXDataAccess.getXML(xquery) + "</xml>";

		//TODO: Error checking xquery response, with appropriate status codes. And a declaration. And JSON support.

		if (MediaType.TEXT_XML.equals(variant.getMediaType()) || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
			Representation xmlVariable = new StringRepresentation(queryResult, MediaType.APPLICATION_XML);
			this.setStatus(Status.SUCCESS_OK);
			return xmlVariable;
		} else {
			String message = " \"" + variant.getMediaType() + "\" is not supported";
			logger.warning("Error retrieving variable representation: " + message);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, message);
		}	
		
	}
		
}