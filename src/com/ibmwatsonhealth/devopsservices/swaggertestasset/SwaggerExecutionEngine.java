package com.ibmwatsonhealth.devopsservices.swaggertestasset;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.testng.TestNG;
import org.testng.collections.Lists;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SwaggerExecutionEngine {

	@SuppressWarnings("deprecation")
	public TestFrameworkOutput executeTestFramework(String someJsonString) {

		TestFrameworkOutput output = null;
		TestNG myTestNG = new TestNG();
		myTestNG.setUseDefaultListeners(true);
		myTestNG.setVerbose(3);
		XmlSuite mySuite = new XmlSuite();
		mySuite.setName("SwaggerSuite");
		XmlTest myTest = new XmlTest(mySuite);
		myTest.setName("SwaggerTest");
		myTest.addParameter("swaggerPath", someJsonString);
		List<XmlClass> myClasses = new ArrayList<XmlClass>();
		myClasses.add(new XmlClass("com.ibmwatsonhealth.devopsservices.swaggertestasset.TestNGTestSuite"));
		myTest.setXmlClasses(myClasses);
		List<XmlTest> myTests = new ArrayList<XmlTest>();
		myTests.add(myTest);
		mySuite.setTests(myTests);
		List<XmlSuite> mySuites = new ArrayList<XmlSuite>();
		mySuites.add(mySuite);
		myTestNG.setXmlSuites(mySuites);
		System.out.println(System.getProperty("user.dir"));
		myTestNG.setOutputDirectory(System.getProperty("user.dir"));
		myTestNG.run();
		
		try {
			System.out.println("Start with the swagger output file extraction");
			String testframeworkoutputPath = System.getProperty("user.dir") + "/SwaggerSuite/SwaggerTest.xml";
			System.out.println("Test results are present in " + System.getProperty("user.dir"));
			File file = new File(testframeworkoutputPath);
			// Set output of test framework here
			output = new TestFrameworkOutput();
			output.setResponseCode(myTestNG.getStatus());
			System.out.println(myTestNG.getStatus());
			// output.setJunitoutput(junitresult);

		} catch (Exception e) {
			// Something went wrong in the test framework
			System.out.println("Something went wrong within swagger execution engine");
			e.printStackTrace();
			output = null;
		}
		return output;

	}

}
