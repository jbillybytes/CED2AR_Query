package edu.cornell.ncrn.ced2ar;

import org.restlet.Application;
import org.restlet.Restlet;

import org.restlet.routing.Router;

import edu.cornell.ncrn.ced2ar.resources.BaseResource;
import edu.cornell.ncrn.ced2ar.resources.CodebookResource;
import edu.cornell.ncrn.ced2ar.resources.CodebooksResource;
import edu.cornell.ncrn.ced2ar.resources.SearchResource;
import edu.cornell.ncrn.ced2ar.resources.VariableResource;
import edu.cornell.ncrn.ced2ar.resources.VariablesResource;

public class CED2AR_QUERY extends Application {
	public final static String apiUrl = "/";
	public final static String version = "1.0.0";
		
	@Override
	public synchronized Restlet createInboundRoot()  {
		Router router = new Router(getContext());

		router.attach(apiUrl, BaseResource.class);
		router.attach(apiUrl + "search", SearchResource.class);
		router.attach(apiUrl + "search/", SearchResource.class);
		router.attach(apiUrl + "codebooks", CodebooksResource.class);
		router.attach(apiUrl + "codebooks/", CodebooksResource.class);
		router.attach(apiUrl + "codebooks/{codebookId}", CodebookResource.class);
		router.attach(apiUrl + "codebooks/{codebookId}/", CodebookResource.class);
		router.attach(apiUrl + "codebooks/{codebookId}/variables", VariablesResource.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/variables/", VariablesResource.class);		
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableId}", VariableResource.class);
		router.attach(apiUrl + "codebooks/{codebookId}/variables/{variableId}/", VariableResource.class);
		
		return router;
	}
	

}