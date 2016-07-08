package com.ibmwatsonhealth.devopsservices.swaggertestasset;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.ibmwatsonhealth.devopsservices.swaggertestasset.EndpointOperationType;
import com.ibmwatsonhealth.devopsservices.swaggertestasset.SwaggerUtility;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

public class TestNGTestSuite {

	private SoftAssert softAssert = new SoftAssert();

	@SuppressWarnings("rawtypes")
	@DataProvider(name = "dbconfig")
	public Object[][] provideDbConfig(ITestContext context) throws IOException {

		Map<Object, Object> map = SwaggerUtility
		.getSwaggerData(context.getCurrentXmlTest().getParameter("swaggerPath"));
		//Map<Object, Object> map = SwaggerUtility.getSwaggerData("http://169.44.118.61:8080/TomcatWebAppForChefNode/swagger.json");
		Object[][] arr = new Object[map.size()][2];
		Set entries = map.entrySet();
		Iterator entriesIterator = entries.iterator();
		int i = 0;
		while (entriesIterator.hasNext()) {

			Map.Entry mapping = (Map.Entry) entriesIterator.next();
			EndpointOperationType endpointtype = (EndpointOperationType) mapping.getKey();
			arr[i][0] = endpointtype.getEndpoint() + "," + endpointtype.getOperation();
			arr[i][1] = mapping.getValue();
			i++;
		}
		return arr;
	}

	// Run tests once for every endpoint and operation combination
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(dataProvider = "dbconfig")
	public void executeRestfulTest(String endpoint, Object obj) {

		try {
			String endpointOutput = endpoint.split(",")[0];
			String endpointOperationOutput = endpoint.split(",")[1];
			Map<Object, Object> dataMap = (Map) obj;
			dataMap.get("response");
			String url = dataMap.get("baseURI") + endpointOutput;

			switch (endpointOperationOutput.toLowerCase()) {

			case "get":

				if (TestNGTestSuite.hasPathParameterResolved(endpointOutput)) {
					Response rep = RestAssured.given().contentType("application/json").when().get(url);
					Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
					
				} else {
					Assert.fail("DOCUMENTATION ERROR:Path parameter is unresolved for endpoint " + endpointOutput);
				}

				break;

			case "post":

				if (TestNGTestSuite.hasPathParameterResolved(endpointOutput)) {
					if (dataMap.get("validdata") != null) {

						// Valid data
						String schema = (String) ((Map) dataMap.get("validdata")).get("schema");
						String body = (String) ((Map) dataMap.get("validdata")).get("data");
						

						Response rep = RestAssured.given().contentType(schema).when().body(body).post(url);
						softAssert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);

						// Invalid data
						Response invalidrep = RestAssured.given()
								.contentType((String) ((Map) dataMap.get("validdata")).get("schema")).when()
								.body((String) ((Map) dataMap.get("invaliddata")).get("data")).post(url);
						System.out.println(invalidrep.getStatusCode());
						softAssert.assertEquals(invalidrep.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

					} else {
						Assert.fail("DOCUMENTATION ERROR : Example data is missing or no Consumes section for endpoint"
								+ endpointOutput + " and operation " + endpointOperationOutput.toLowerCase());
					}
					
				} else {
					Assert.fail("DOCUMENTATION ERROR: Path parameter is unresolved for endpoint " + endpointOutput);
				}
				break;

			case "put":
				
				if (TestNGTestSuite.hasPathParameterResolved(endpointOutput)) {
					if (dataMap.get("validdata") != null) {

						// Valid data
						String schema = (String) ((Map) dataMap.get("validdata")).get("schema");
						String body = (String) ((Map) dataMap.get("validdata")).get("data");
						

						Response rep = RestAssured.given().contentType(schema).when().body(body).put(url);
						softAssert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);

						// Invalid data
						Response invalidrep = RestAssured.given()
								.contentType((String) ((Map) dataMap.get("validdata")).get("schema")).when()
								.body((String) ((Map) dataMap.get("invaliddata")).get("data")).put(url);
						System.out.println(invalidrep.getStatusCode());
						softAssert.assertEquals(invalidrep.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

					} else {
						Assert.fail("DOCUMENTATION ERROR : Example data is missing or no Consumes section for endpoint"
								+ endpointOutput + " and operation " + endpointOperationOutput.toLowerCase());
					}
					
				} else {
					Assert.fail("DOCUMENTATION ERROR: Path parameter is unresolved for endpoint " + endpointOutput);
				}
				break;

			case "delete":
				if (TestNGTestSuite.hasPathParameterResolved(endpointOutput)) {
					Response rep = RestAssured.given().contentType("application/json").when().delete(url);
					Assert.assertEquals(rep.getStatusCode(), HttpStatus.SC_OK);
					
				} else {
					Assert.fail("DOCUMENTATION ERROR:Path parameter is unresolved for endpoint " + endpointOutput);
				}
				break;
			default:
				Assert.fail("SWAGGER SERVICE FAILURE: Currentl version does not support operation "
						+ endpointOperationOutput.toLowerCase());
			}

		} catch (Exception e) {
			Assert.fail(" SWAGGER SERVICE FAILURE: Something went wrong with the test method");
		}
	}

	public static boolean hasPathParameterResolved(String endPoint) {

		if (endPoint.toLowerCase().contains("{") || endPoint.toLowerCase().contains("}")) {
			return false;
		} else {
			return true;
		}

	}

}
