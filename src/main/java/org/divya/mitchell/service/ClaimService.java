package org.divya.mitchell.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.divya.mitchell.database.DatabaseConnection;
import org.divya.mitchell.model.CauseOfLossCode;
import org.divya.mitchell.model.LossInfoType;
import org.divya.mitchell.model.MitchellClaimType;
import org.divya.mitchell.model.StatusCode;
import org.divya.mitchell.model.VehicleInfoType;
import org.divya.mitchell.model.VehicleListType;

public class ClaimService {
	
	public ClaimService(){
		
	}
	
	//Connecting to database and inserting data based on the request from the client
	public MitchellClaimType addClaim(MitchellClaimType claim) {
		System.out.println("Hello");
		DatabaseConnection db = new DatabaseConnection();
		Connection conn = db.getConnection();
		int claimId = addToClaim(claim, conn);
		claim.setId(claimId);
		List<VehicleInfoType> vehicleList = claim.getVehicles().getVehicleDetails();
		for(VehicleInfoType vehicle: vehicleList){
			addToVehicle(vehicle, claim.getClaimNumber(), conn);
		}
		System.out.println(claimId);
		return claim;
	}
	
	
	public int addToClaim(MitchellClaimType claimType, Connection conn){
		int claimId = 0;
		try {
			Statement stmt = conn.createStatement();
			
			String sql = "INSERT INTO claim (ClaimNumber, ClaimantFirstName, "
					+ "ClaimantLastName, Status, LossDate,"+
					"CauseOfLoss, ReportedDate, LossDescription, AdjusterID) "
					+ "VALUES ('"+claimType.getClaimNumber() +"','" +
					claimType.getClaimantFirstName()+"','" +
					claimType.getClaimantLastName()+"','" + 
					claimType.getStatus()+"','" +
					claimType.getLossDate().getYear()+"-"+claimType.getLossDate().getMonth()
					+"-"+ claimType.getLossDate().getDay()+" "+ claimType.getLossDate().getHour()
					+":"+ claimType.getLossDate().getMinute()+":"+ claimType.getLossDate().getSecond()+"','" +
					claimType.getLossInfo().getCauseOfLoss().value() +"','" +
					claimType.getLossInfo().getReportedDate() +"','" +
					claimType.getLossInfo().getLossDescription()+"','" +
					claimType.getAssignedAdjusterID() +"');";
			
			stmt.executeUpdate(sql);
			
			sql = "SELECT ClaimID FROM claim WHERE ClaimNumber= '"+ claimType.getClaimNumber() +"';";
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				claimId = rs.getInt("ClaimID");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return claimId;
	}
	
	public void addToVehicle(VehicleInfoType vehicleInfo, String claimNum, Connection conn){
		try {
			Statement stmt = conn.createStatement();
			String sql = "INSERT INTO vehicle (Vin, ClaimNum, ModelYear, "
					+ "Make, Model, Engine,Color, LicPlate"
					+ ", LicPlateState, LicPlateExpDate, DamageDesp, Mileage) "
					+ "VALUES ('"+vehicleInfo.getVin() +"','" +
					claimNum +"','"+
					vehicleInfo.getModelYear()+"','" +
					vehicleInfo.getMakeDescription()+"','" + 
					vehicleInfo.getModelDescription()+"','" +
					vehicleInfo.getEngineDescription()+"','" +
					vehicleInfo.getExteriorColor() +"','" +
					vehicleInfo.getLicPlate()+"','" +
					vehicleInfo.getLicPlateState()+"','" +
					vehicleInfo.getLicPlateExpDate() +"','" +
					vehicleInfo.getDamageDescription()+"','" +
					vehicleInfo.getMileage() +"');";
			
			stmt.executeUpdate(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	//Connect to database and retrieve the claim details based on the Claim Number
	@SuppressWarnings({ "deprecation", "static-access" })
	public MitchellClaimType getClaim(String ClaimNum){
		DatabaseConnection db = new DatabaseConnection();
		Connection conn = db.getConnection();
		MitchellClaimType claim = new MitchellClaimType();
		System.out.println(ClaimNum);
		claim.setClaimNumber(ClaimNum);
		try{
			Date dob=null;
			DateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			GregorianCalendar cal = new GregorianCalendar();
			Statement stmt =  conn.createStatement();
			String sql = "SELECT * FROM claim WHERE ClaimNumber='"+claim.getClaimNumber()+"';";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				System.out.println("Found the result");
				
				claim.setId(rs.getInt("ClaimID"));
				claim.setClaimNumber(rs.getString("ClaimNumber"));
				claim.setClaimantFirstName(rs.getString("ClaimantFirstName"));
				claim.setClaimantLastName(rs.getString("ClaimantLastName"));
				claim.setStatus(StatusCode.fromValue(rs.getString("Status")));
				
				
				dob=df.parse( rs.getString("LossDate").replace('T', ' ') );
				cal.setTime(dob);
				XMLGregorianCalendar xmlDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize();
				claim.setLossDate(xmlDate1);
				
				LossInfoType loss = new LossInfoType();
				loss.setCauseOfLoss(CauseOfLossCode.fromValue(rs.getString("CauseOfLoss")));
				dob=df.parse( rs.getString("ReportedDate").replace('T', ' ') );
				cal.setTime(dob);
				xmlDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize();
				loss.setReportedDate(xmlDate1);
				loss.setLossDescription(rs.getString("LossDescription"));
				claim.setLossInfo(loss);
				
				claim.setAssignedAdjusterID((long) rs.getInt("AdjusterID"));
				
			}
			
			VehicleListType list = new VehicleListType();
			VehicleInfoType vehicle = new VehicleInfoType();
			
			sql = "SELECT * FROM vehicle WHERE ClaimNum ='"+ ClaimNum+"';";
			
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				vehicle.setVin(rs.getString("Vin"));
				vehicle.setModelYear(rs.getInt("ModelYear"));
				vehicle.setMakeDescription(rs.getString("Make"));
				vehicle.setModelDescription(rs.getString("Model"));
				vehicle.setEngineDescription(rs.getString("Engine"));
				vehicle.setExteriorColor(rs.getString("Color"));
				vehicle.setLicPlate(rs.getString("LicPlate"));
				vehicle.setLicPlateState(rs.getString("LicPlateState"));
				
				DateFormat df1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String licDate =  rs.getString("LicPlateExpDate");
				
				licDate = licDate.replace('T', ' ');
				licDate = licDate.replace('Z', ' ');
				licDate = licDate.trim();
				dob=df1.parse(licDate);
				cal.setTime(dob);
				XMLGregorianCalendar xmlDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize();
				vehicle.setLicPlateExpDate(xmlDate1);
				
				vehicle.setDamageDescription(rs.getString("DamageDesp"));
				vehicle.setMileage(rs.getInt("Mileage"));
				
				list.getVehicleDetails().add(vehicle);
				
			}
			claim.setVehicles(list);
			
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} 
		
		return claim;
	}
	
	
	//Retrieve the list of Vehicles with the same claim number
	@SuppressWarnings({ "deprecation", "static-access" })
	public List<MitchellClaimType> getClaimByDate(String startDate, String endDate){
		DatabaseConnection db = new DatabaseConnection();
		Connection conn = db.getConnection();
		System.out.println("In getClaimByDate");
		List<MitchellClaimType> claimList = new ArrayList<MitchellClaimType>();
		java.sql.Date start = null;
		java.sql.Date end = null;
		Date dob = null;
		GregorianCalendar cal = new GregorianCalendar();
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Date parsedUtilsDate = null;
		java.util.Date parsedUtileDate = null;
		try {
			if(startDate == null){
				startDate = "0000-00-00 00:00:00";
			}
			if(endDate == null){
				end = (java.sql.Date) new Date();
			}
			parsedUtilsDate = df.parse(startDate); 
			parsedUtileDate = df.parse(endDate);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		start= new java.sql.Date(parsedUtilsDate.getTime());
		end = new java.sql.Date(parsedUtileDate.getTime());
		try {
			Statement stmt = conn.createStatement();
			
			String sql = "SELECT * FROM claim WHERE LossDate >'"+start+"' AND LossDate <'"+ end +"';";
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				System.out.println("In the while");
				MitchellClaimType claim = new MitchellClaimType();
				claim.setId(rs.getInt("ClaimID"));
				claim.setClaimNumber(rs.getString("ClaimNumber"));
				claim.setClaimantFirstName(rs.getString("ClaimantFirstName"));
				claim.setClaimantLastName(rs.getString("ClaimantLastName"));
				claim.setStatus(StatusCode.fromValue(rs.getString("Status")));
				
				dob=df.parse( rs.getString("LossDate").replace('T', ' ') );
				cal.setTime(dob);
				XMLGregorianCalendar xmlDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize();
				claim.setLossDate(xmlDate1);
				
				LossInfoType loss = new LossInfoType();
				loss.setCauseOfLoss(CauseOfLossCode.fromValue(rs.getString("CauseOfLoss")));
				dob=df.parse( rs.getString("ReportedDate").replace('T', ' ') );
				cal.setTime(dob);
				xmlDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize();
				loss.setReportedDate(xmlDate1);
				loss.setLossDescription(rs.getString("LossDescription"));
				claim.setLossInfo(loss);
				
				claim.setAssignedAdjusterID((long) rs.getInt("AdjusterID"));
				System.out.println("Claim number "+ claim.getClaimNumber());
				claimList.add(claim);
			}
			for(MitchellClaimType cl : claimList){
				String claimNum = cl.getClaimNumber();
				VehicleListType list = new VehicleListType();
				VehicleInfoType vehicle = new VehicleInfoType();
				
				sql = "SELECT * FROM vehicle WHERE ClaimNum ='"+ claimNum+"';";
				
				rs = stmt.executeQuery(sql);
				while(rs.next()){
					vehicle.setVin(rs.getString("Vin"));
					vehicle.setModelYear(rs.getInt("ModelYear"));
					vehicle.setMakeDescription(rs.getString("Make"));
					vehicle.setModelDescription(rs.getString("Model"));
					vehicle.setEngineDescription(rs.getString("Engine"));
					vehicle.setExteriorColor(rs.getString("Color"));
					vehicle.setLicPlate(rs.getString("LicPlate"));
					vehicle.setLicPlateState(rs.getString("LicPlateState"));
					
					DateFormat df1=new SimpleDateFormat("yyyy-MM-dd-hh:mm");
					String licDate =  rs.getString("LicPlateExpDate");
					
					dob=df1.parse(licDate);
					cal.setTime(dob);
					XMLGregorianCalendar xmlDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize();
					vehicle.setLicPlateExpDate(xmlDate1);
					
					vehicle.setDamageDescription(rs.getString("DamageDesp"));
					vehicle.setMileage(rs.getInt("Mileage"));
					
					list.getVehicleDetails().add(vehicle);
					
				}
				cl.setVehicles(list);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return claimList;
	}
	
	
	//Remove a tuple from the Claim database based on the claim number 
	public int deleteClaim(String ClaimNum){
		DatabaseConnection db = new DatabaseConnection();
		Connection conn = db.getConnection();
		MitchellClaimType claim = new MitchellClaimType();
		System.out.println(ClaimNum);
		claim.setClaimNumber(ClaimNum);
		String sql = "DELETE FROM claim WHERE ClaimNumber='"+claim.getClaimNumber()+"';";
		int row = 0;
		try {
			Statement stmt =  conn.createStatement();
			row = stmt.executeUpdate(sql);
			if(row!=0){
				sql = "DELETE FROM vehicle WHERE ClaimNum ='"+ ClaimNum+"';";
			}
			row = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	
	
	//Read the vehicle details from the database based the Claim number and Vin 
	@SuppressWarnings({ "deprecation", "static-access" })
	public VehicleInfoType readVehicleData(String Claim, String Vehicle){
		DatabaseConnection db = new DatabaseConnection();
		Connection conn = db.getConnection();
		VehicleInfoType vehicle = new VehicleInfoType();
		System.out.println(Claim);
		try{
			Date dob=null;
			GregorianCalendar cal = new GregorianCalendar();
			Statement stmt =  conn.createStatement();
			
			String sql = "SELECT * FROM vehicle WHERE ClaimNum='"+Claim+"' AND Vin='"+Vehicle+"';";
			ResultSet rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				vehicle.setVin(rs.getString("Vin"));
				vehicle.setModelYear(rs.getInt("ModelYear"));
				vehicle.setMakeDescription(rs.getString("Make"));
				vehicle.setModelDescription(rs.getString("Model"));
				vehicle.setEngineDescription(rs.getString("Engine"));
				vehicle.setExteriorColor(rs.getString("Color"));
				vehicle.setLicPlate(rs.getString("LicPlate"));
				vehicle.setLicPlateState(rs.getString("LicPlateState"));
				
				DateFormat df1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String licDate =  rs.getString("LicPlateExpDate");
				
				licDate = licDate.replace('T', ' ');
				licDate = licDate.replace('Z', ' ');
				licDate = licDate.trim();
				dob=df1.parse(licDate);
				cal.setTime(dob);
				XMLGregorianCalendar xmlDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), dob.getHours(),dob.getMinutes(),dob.getSeconds(),DatatypeConstants.FIELD_UNDEFINED, cal.getTimeZone().LONG).normalize();
				vehicle.setLicPlateExpDate(xmlDate1);
				
				vehicle.setDamageDescription(rs.getString("DamageDesp"));
				vehicle.setMileage(rs.getInt("Mileage"));
				
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} 
		
		return vehicle;
	}
	
	
	//Update the database based on the column values to be changed in the database
	public MitchellClaimType updateClaimDetails(MitchellClaimType claim){
		MitchellClaimType claimtemp = checkAndUpdateClaim(claim);
		MitchellClaimType claimUptoDate = updateDatabase(claimtemp);
		return claimUptoDate;
	}
	
	public MitchellClaimType checkAndUpdateClaim(MitchellClaimType claim){
		MitchellClaimType oldClaim = getClaim(claim.getClaimNumber());
		
		if(claim.getClaimantFirstName() == null){
			claim.setClaimantFirstName(oldClaim.getClaimantFirstName());
		}
		
		if(claim.getClaimantLastName() == null){
			claim.setClaimantLastName(oldClaim.getClaimantLastName());
		}
		
		claim.setId(oldClaim.getId());
		
		if(claim.getLossDate() == null){
			claim.setLossDate(oldClaim.getLossDate());
		}
		
		if(claim.getLossInfo() == null){
			claim.setLossInfo(oldClaim.getLossInfo());
		}
		
		if(claim.getStatus() == null){
			claim.setStatus(oldClaim.getStatus());
		}
		
		if(claim.getAssignedAdjusterID()== null){
			claim.setAssignedAdjusterID(oldClaim.getAssignedAdjusterID());
		}
		
		if(claim.getVehicles() == null){
			claim.setVehicles(oldClaim.getVehicles());
		}
		else{
			List<VehicleInfoType> vehicleList = oldClaim.getVehicles().getVehicleDetails();
			List<VehicleInfoType> newVehicleList = claim.getVehicles().getVehicleDetails();
			VehicleListType list = new VehicleListType();
			int i = 0;
			for(VehicleInfoType vehicle: newVehicleList){
				
				if(vehicle.getDamageDescription()== null){
					vehicle.setDamageDescription(vehicleList.get(i).getDamageDescription());
				}
				
				if(vehicle.getEngineDescription() == null){
					vehicle.setEngineDescription(vehicleList.get(i).getEngineDescription());
				}
				
				if(vehicle.getExteriorColor()== null){
					vehicle.setExteriorColor(vehicleList.get(i).getExteriorColor());
				}
				
				if(vehicle.getLicPlate() == null){
					vehicle.setLicPlate(vehicleList.get(i).getLicPlate());
				}
				
				if(vehicle.getLicPlateExpDate()== null){
					vehicle.setLicPlateExpDate(vehicleList.get(i).getLicPlateExpDate());
				}
				
				if(vehicle.getLicPlateState()== null){
					vehicle.setLicPlateState(vehicleList.get(i).getLicPlateState());
				}
				
				if(vehicle.getMakeDescription() == null){
					vehicle.setMakeDescription(vehicleList.get(i).getMakeDescription());
				}
				
				if(vehicle.getMileage() == null){
					vehicle.setMileage(vehicleList.get(i).getMileage());
				}
				
				if(vehicle.getModelDescription() == null){
					vehicle.setModelDescription(vehicleList.get(i).getModelDescription());
				}
				
				if(vehicle.getModelYear() == 0){
					vehicle.setModelYear(vehicleList.get(i).getModelYear());
				}
				
				i++;
				list.getVehicleDetails().add(vehicle);
			}
			claim.setVehicles(list);
		}
		
		return claim;
	}
	
	public MitchellClaimType updateDatabase(MitchellClaimType claim) {
		System.out.println("Hello");
		DatabaseConnection db = new DatabaseConnection();
		Connection conn = db.getConnection();
		updateDataClaim(claim, conn);
		List<VehicleInfoType> vehicleList = claim.getVehicles().getVehicleDetails();
		for(VehicleInfoType vehicle: vehicleList){
			updateDataVehicle(vehicle, claim.getClaimNumber(), conn);
		}
		return claim;
	}
	
	public int updateDataClaim(MitchellClaimType claimType, Connection conn){
		int claimId = 0;
		try {
			Statement stmt = conn.createStatement();
			
			String sql = "UPDATE claim SET ClaimantFirstName='"+claimType.getClaimantFirstName()+"', " +
					"ClaimantLastName='"+claimType.getClaimantLastName()+"', " + 
					"Status='"+claimType.getStatus()+"', " +
					"LossDate='"+claimType.getLossDate().getYear()+"-"+claimType.getLossDate().getMonth()
					+"-"+ claimType.getLossDate().getDay()+" "+ claimType.getLossDate().getHour()
					+":"+ claimType.getLossDate().getMinute()+":"+ claimType.getLossDate().getSecond()+"', " +
					"CauseOfLoss='"+claimType.getLossInfo().getCauseOfLoss().value() +"', " +
					"ReportedDate='"+claimType.getLossInfo().getReportedDate() +"', " +
					"LossDescription='"+claimType.getLossInfo().getLossDescription()+"', " +
					"AdjusterID='"+claimType.getAssignedAdjusterID() +"' "
					+ "WHERE ClaimNumber='"+claimType.getClaimNumber() +"';";
			
			stmt.executeUpdate(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return claimId;
	}
	
	public void updateDataVehicle(VehicleInfoType vehicleInfo, String claimNum, Connection conn){
		try {
			Statement stmt = conn.createStatement();
			String sql = "UPDATE vehicle SET ModelYear='"+vehicleInfo.getModelYear()+"', " +
					"Make='"+vehicleInfo.getMakeDescription()+"', " + 
					"Model='"+vehicleInfo.getModelDescription()+"', " +
					"Engine='"+vehicleInfo.getEngineDescription()+"', " +
					"Color='"+vehicleInfo.getExteriorColor() +"', " +
					"LicPlate='"+vehicleInfo.getLicPlate()+"', " +
					"LicPlateState='"+vehicleInfo.getLicPlateState()+"', " +
					"LicPlateExpDate='"+vehicleInfo.getLicPlateExpDate() +"', " +
					"DamageDesp='"+vehicleInfo.getDamageDescription()+"', " +
					"Mileage='"+vehicleInfo.getMileage() +
					"' WHERE ClaimNum='"+claimNum +"';";
			
			stmt.executeUpdate(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
