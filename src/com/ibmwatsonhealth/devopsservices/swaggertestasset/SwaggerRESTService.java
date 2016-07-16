package com.ibmwatsonhealth.devopsservices.swaggertestasset;

/**
 * @author Shreejit Nair - Swagger Test Service
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;

/* Author - Shreejit Nair */
/* Swagger TestserviceFramework hooked up as a web service     */
@Path("/")
public class SwaggerRESTService {
	@POST
	@Path("/swaggerTestService")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_XML)
	public Response swaggerREST(InputStream incomingData) {

		int responsecode = -1;
		StringBuilder crunchifyBuilder = new StringBuilder();
		SwaggerExecutionEngine engine;
		String frameworkoutput = null;

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
			String jsonString = crunchifyBuilder.toString();
			JSONObject jsonObject = new JSONObject(jsonString);
			String swaggerPath = jsonObject.getString("swaggerURL");
			engine = new SwaggerExecutionEngine();
			TestFrameworkOutput executionOutput = engine.executeTestFramework(swaggerPath);
			if(executionOutput != null){
				responsecode = executionOutput.getResponseCode();
				String testframeworkoutputPath = System.getProperty("user.dir") + "/SwaggerSuite/SwaggerTest.xml";
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				InputStream inputStream = new FileInputStream(new File(testframeworkoutputPath));
				org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
				StringWriter stw = new StringWriter();
				Transformer serializer = TransformerFactory.newInstance().newTransformer();
				serializer.transform(new DOMSource(doc), new StreamResult(stw));
				frameworkoutput = stw.toString();
				System.out.println("SERVER NEVER FOUND ANY PROBLEM");
				
			}else{
				throw new RuntimeException("Swagger Test framework failed to process the request");
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SERVER THREW AN EXCEPTION");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Swagger Service failed").header("teststatus", 1).build();
		}
		// Send valid xml response back to client
		return Response.status(Response.Status.OK).entity(frameworkoutput).header("teststatus", responsecode).build();
	}

	@GET
	@Path("/help")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService() {

		//Provide a helper for people to understand how to call the service
		return Response.status(200).entity("curl -i -H \"Accept:application/xml\" -X POST -d \"swaggerURL\":\"http://petstore.swagger.io/v2/swagger.json\" http://localhost:9086/SwaggerParser/api/swaggerTestService"
).build();
	}

	@POST
	@Path("/crunchifyService")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crunchifyREST(InputStream incomingData) {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received: " + crunchifyBuilder.toString());

		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}

}
