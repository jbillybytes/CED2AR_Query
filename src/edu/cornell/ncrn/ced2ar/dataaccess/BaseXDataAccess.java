package edu.cornell.ncrn.ced2ar.dataaccess;
import java.io.IOException;

import org.basex.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BaseXDataAccess  {

	public BaseXDataAccess() {

	}
	
	public static String getXML(String xquery) {

		String response = "";
		
		String baseUrl = "http://rschweb.ciserrsch.cornell.edu:8080/BaseX73/rest?query=";
		HttpURLConnection uConn = null;
		BufferedReader bd = null;
		StringBuilder sb = null;
		try {
			URL url = new URL(baseUrl + URLEncoder.encode(xquery, "UTF-8"));
			// User and password.
			//6-6-13 Removed username and password - B. Perry
			String user = "";
			String pw ="";
			
			// Encode user name and password pair with a base64 implementation.
			String encoded = Base64.encode(user + ":" + pw);
			
			// Basic access authentication header to connection request.		
			uConn = (HttpURLConnection) url.openConnection();
			uConn.setRequestProperty("Authorization", "Basic " + encoded);
			uConn.setRequestMethod("GET");
			uConn.setDoOutput(true);
			uConn.setReadTimeout(10000);

			uConn.connect();

			bd = new BufferedReader(new InputStreamReader(
			uConn.getInputStream()));
			sb = new StringBuilder();
			String line = null;
			
			while ((line = bd.readLine()) != null) {
				sb.append(line + "\n");
			}

			response = sb.toString();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bd = null;
			sb = null;
			uConn = null;
		}

		return response;
	}
}