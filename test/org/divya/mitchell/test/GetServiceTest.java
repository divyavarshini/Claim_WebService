package org.divya.mitchell.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.divya.mitchell.model.MitchellClaimType;
import org.divya.mitchell.service.ClaimService;
import org.junit.Test;

public class GetServiceTest {
	@Test
	public void testGetService() throws JAXBException{
		JAXBContext jaxbContext = JAXBContext.newInstance(MitchellClaimType.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MitchellClaimType claimTest = (MitchellClaimType) jaxbUnmarshaller.unmarshal(new File("D:\\create-claim.xml"));
		ClaimService service = new ClaimService();
		MitchellClaimType claim = service.getClaim("22c9c23bac142856018ce14a26b6c299");
		assertEquals(claimTest.getClaimNumber(), claim.getClaimNumber());
	}
}
