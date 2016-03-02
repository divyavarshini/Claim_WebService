package org.divya.mitchell.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RunWith(Suite.class)
@SuiteClasses({ GetServiceTest.class })
public class GetHttpServiceTest {
	
	@Test
	public void checkGetHTTP(){
		Client client = Client.create();

		WebResource webResource = client
		   .resource("http://localhost:8080/MitchellCoding/webresources/claims/22c9c23bac142856018ce14a26b6c299");

		ClientResponse response = webResource.accept("application/xml")
	               .get(ClientResponse.class);
		
		assertEquals(response.getStatus(), 200);
	}
}
