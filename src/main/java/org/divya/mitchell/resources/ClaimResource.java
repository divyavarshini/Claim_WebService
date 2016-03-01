package org.divya.mitchell.resources;


import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.divya.mitchell.model.MitchellClaimType;
import org.divya.mitchell.model.VehicleInfoType;
import org.divya.mitchell.service.ClaimService;


@Path("/claims")
public class ClaimResource {
	
	ClaimService claimService = new ClaimService();
	
	//Adds the claim to database and sends success or failure response and the claim created.
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_XML)
	public String createClaim(MitchellClaimType claim, @Context UriInfo uriInfo){
		MitchellClaimType newClaim = claimService.addClaim(claim);
		String newID = String.valueOf(newClaim.getId());
		//URI uri = uriInfo.getAbsolutePathBuilder().path(newID).build();
		return "POST Success: " + newID;
	}
	
	//Reads and displays the claim details from the database based on the claim number entered in the path. 
	@GET
	@Path("/{claimnumber}")
	@Produces(MediaType.TEXT_XML)
	public MitchellClaimType readClaim(@PathParam("claimnumber") String ClaimNum){
		if(ClaimNum == null){
			return null;
		}
		return claimService.getClaim(ClaimNum);
	}
	
	//Reads and displays the claim details in the XML format
	//from the claim database based on the query parameters passed in the path
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<MitchellClaimType> getClaimDateRange(@QueryParam("startdate")String startDate, 
			@QueryParam("enddate")String endDate){
		return claimService.getClaimByDate(startDate, endDate);
	}
	
	//Delete the claim and vehicle tuples from the database based on the claim number passed in the path  
	@DELETE
	@Path("/{claimnumber}")
	@Produces(MediaType.TEXT_XML)
	public String deleteClaim(@PathParam("claimnumber") String ClaimNum){
		if(ClaimNum == null){
			return null;
		}
		int flag = claimService.deleteClaim(ClaimNum);
		if(flag == 0){
			return "Delete Success: false";
		}
		else{
			return "Delete Success: true";
		}
	}
	
	//Reads and Displays the vehicle info in the XML format from the vehicle database based on the 
	//claim number and vin details given in the path
	@GET
	@Path("/{claimnumber}/{vinNum}")
	@Produces(MediaType.TEXT_XML)
	public VehicleInfoType readVehicle(@PathParam("claimnumber") String ClaimNum, @PathParam("vinNum") String VehicleNum){
		VehicleInfoType vehicle = claimService.readVehicleData(ClaimNum,VehicleNum);
		return vehicle;
	}
	
	//Update the claim and vehicle info based on the XML file input and send a success or failure message.
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_XML)
	public String updateClaim(MitchellClaimType claim, @Context UriInfo uriInfo){
		MitchellClaimType newClaim = claimService.updateClaimDetails(claim);
		String newID = String.valueOf(newClaim.getId());
		return "Updated claim: "+newID ;
	}
}
