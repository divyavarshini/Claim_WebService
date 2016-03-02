# MitchellCoding
Mitchell Coding Challenge

WebService with the RestFul Api implemented using Jax-RS , JAXB, Apache Tomcat as server and MySQL database at the backend. 
The Whole project is built on MAVEN with the dependencies and used JUnit to do the unit and integration testing.

REST API:

*Create a new Claim: POST -> /MitchellTest/webapi/ClaimService/claims
  Updated both the Claim and Vehicle table
*Read a claim: GET -> /MitchellTest/webapi/ClaimService/claims/{claimnumber}
  Implemented using the Path Parameters
*Get a list of Claims between range of loss date -> GET : /MitchellTest/webapi/ClaimService/claims?startDate={strt}&endDate={end} 
  Implemented using the Query Parameters
*Update a Claim : PUT -> /MitchellTest/webapi/ClaimService/claims/{claimnumber}
  Implemented using the Path Parameters
*Delete a Claim : DELETE -> /MitchellTest/webapi/ClaimService/claims/{claimnumber}
  Implemented using the Path Parameters and also deleted the related vehicles from the vehicle table
*Get a specific vehicle from a specific claim: GET -> /MitchellTest/webapi/ClaimService/claims/{claimnumber}/vehicles/{vinNum}
  Implemented using the the Path paramters and accessing the vehicles database.
