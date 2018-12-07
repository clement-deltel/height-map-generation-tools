/**
 * DaoModels.java
 * Purpose: 
 *
 * HeighMapGenerationTools
 * @author 
 * @version 1.0
 */

package models.database;

import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class DaoModel {
	//Declare DB objects 
	DBConnectionManager con = null;
	static Statement stmt = null;
	
	/**
	 * Check if table exists and return the following error code :
	 * 	-1 : should not happen,
	 * 	 1 : table does not exist
	 * 	 2 : table exists and is empty
	 *   3 : table exists and is not empty
	 * @param tableName Name of table
	 * @return Error Code
	 */
	public static int checkExistingTable(String tableName) {
		try {
			DatabaseMetaData dbmd = DBConnectionManager.getConnection().getMetaData();
			ResultSet tables = dbmd.getTables(null, null, tableName, null);
			DBConnectionManager.getConnection().close();
			
			// Check if "tableName" table is already there
			if (tables.next()) {
				//System.out.println("The table already exists...");
				stmt = DBConnectionManager.getConnection().createStatement();
				String sql = "SELECT COUNT(*) FROM "+tableName;
				ResultSet rs = stmt.executeQuery(sql);
				int rows = 0;
				while(rs.next()){
					rows = rs.getInt(1);
				}
				if(rows == 0) {
					//System.out.println("The table is empty.");
					DBConnectionManager.getConnection().close();
					return 2;
				}
				else {
					//System.out.println("The table is not empty.");
					//System.out.println("There are "+rows+" rows in your table.");
					DBConnectionManager.getConnection().close();
					return 3;
				}
			}
			else {
				//System.out.println("There is no table called "+tableName+ " in this database.");
				DBConnectionManager.getConnection().close(); 
				return 1;
			}
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		return -1;
	}

	/**
	 * This function perform the creation of the first table about the map.
	 */
	public static void createTableMapParameters() {
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			// First table for the map parameters
			String sql = "CREATE TABLE HEIGHTMAP_PARAMETERS (" + 
					"Map_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " + 
					"Algorithm_name VARCHAR(50), " +
					"Height INT, " + 
					"Width INT, " +
					"Map_parameters VARCHAR(5000), " +
					"Time TIME, " +
					"Date DATE, " +
					"PRIMARY KEY (Map_id))";
			// Execute create query
			stmt.executeUpdate(sql);
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/**
	 * This function perform the creation of the second table about the map.
	 */
	public static void createTableMapStatistics() {
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			// Second table for the map statistics
			String sql = "CREATE TABLE HEIGHTMAP_STATISTICS (" + 
					"Stat_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) REFERENCES HEIGHTMAP_PARAMETERS(Map_id), " + 
					"Algorithm_name VARCHAR(50), " +
					"Max_value SMALLINT, " + 
					"Min_value SMALLINT, " +
					"Average_value DOUBLE PRECISION, " +
					"Median_value SMALLINT, " +
					"Height_histogram VARCHAR(5000), " +
					"Date DATE, " +
					"Time TIME," +
					"PRIMARY KEY (Stat_id))";
			// Execute create query
			stmt.executeUpdate(sql);
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/**
	 * This function perform the creation of the table for the license numbers.
	 */
	public static void createTableLicenses() {
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			// Execute create query
			String sql = "CREATE TABLE LICENSES (" + 
					"License_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " + 
					"License_number VARCHAR(30), " +
					"License_type INTEGER, " +
					"Date DATE," + 
					"Authorized_use_time INTEGER, " +
					"PRIMARY KEY (License_id))";
			stmt.executeUpdate(sql);
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/**
	 * This function insert into the table licenses only the admin license number 
	 */
	public static void insertTableLicensesAdmin() {
		try {
			// Execute inserting query
			String sql = "INSERT INTO LICENSES(License_number, License_type, Date, Authorized_use_time) VALUES (?, ?, ?, ?)";
			PreparedStatement ps = DBConnectionManager.getConnection().prepareStatement(sql);
			// Set fields
			ps.setString(1, "1234-1234-1234-1234");
			ps.setString(2, "2");
			ps.setDate(3, Date.valueOf(LocalDate.now()));
			ps.setString(4, "0");
			ps.executeUpdate();
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/**
	 * This function insert into the table licenses the license numbers
	 * with different authorized use time.
	 * @param licenseNumber
	 * @param licenseType
	 * @param authorizedUseTime
	 */
	public static int insertTableLicenses(String licenseNumber, String licenseType, String authorizedUseTime) {
		ResultSet rs = null;
		try {	
			// Check if the license already exist in the database
			stmt = DBConnectionManager.getConnection().createStatement();
			String sql = "SELECT License_number FROM LICENSES WHERE License_number = "+licenseNumber;
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				DBConnectionManager.getConnection().close();
				return 0;
			}
			else {
				// Execute inserting query
				sql = "INSERT INTO LICENSES(License_number, License_type, Authorized_use_time) VALUES (?, ?, ?)";
				PreparedStatement ps = DBConnectionManager.getConnection().prepareStatement(sql);
				// Set fields
				ps.setString(1, licenseNumber);
				ps.setString(2, licenseType);
				ps.setString(3, authorizedUseTime);
				ps.executeUpdate();
				DBConnectionManager.getConnection().close();
				return 1;
			}
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * This function insert into the table licenses the license numbers
	 * with different authorized use time.
	 */
	public static void ValidateLicense(String licenseNumber) {
		try {	
			// Execute inserting query
			String sql = "UPDATE LICENSES SET Date VALUES (?)";
			PreparedStatement ps = DBConnectionManager.getConnection().prepareStatement(sql);
			// Set fields
			ps.setDate(1, Date.valueOf(LocalDate.now()));
			ps.executeUpdate();
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * This method perform the insertion of data into the two tables.
	 */
	//TODO generate string values mapParameters and heightHistogram
	public static void insertTablesMap(String algorithmName, String height, String width, String mapParameters, String maxValue, String minValue, String averageValue, String medianValue, String heightHistogram) {
		try {
			String sql = "INSERT INTO HEIGHTMAP_PARAMETERS (Algorithm_name, Height, Width, Map_parameters, Date, Time) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = DBConnectionManager.getConnection().prepareStatement(sql);
			// Set fields
			ps.setString(1, algorithmName);
			ps.setString(2, height);
			ps.setString(3, width);
			ps.setString(4, mapParameters);
			ps.setDate(5, Date.valueOf(LocalDate.now()));
			ps.setTime(6, Time.valueOf(LocalTime.now()));
			ps.executeUpdate();
					
			sql = "INSERT INTO HEIGHTMAP_STATISTICS(Algorithm_name, Max_value, Min_value, Average_value, Median_value, Height_histogram, Date, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			ps = DBConnectionManager.getConnection().prepareStatement(sql);
			// Set fields
			ps.setString(1, algorithmName);
			ps.setString(2, maxValue);
			ps.setString(3, minValue);
			ps.setString(4, averageValue);
			ps.setString(5, medianValue);
			ps.setString(6, mapParameters);
			ps.setDate(7, Date.valueOf(LocalDate.now()));
			ps.setTime(8, Time.valueOf(LocalTime.now()));
			ps.executeUpdate();
			
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) { 
			se.printStackTrace();  
		}
	}

	/**
	 * This function allow the user to delete the last generated map from the database.
	 */
	//TODO Solve the problem with the foreign key when I delete a map
	public static void deleteLastMap() {
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			// Last map deletion
			String sql = "DELETE FROM HEIGHTMAP_PARAMETERS WHERE Map_id=(SELECT MAX(Map_id) FROM HEIGHTMAP_PARAMETERS)";
			stmt.executeUpdate(sql);
			sql = "DELETE FROM HEIGHTMAP_STATISTICS WHERE Stat_id=(SELECT MAX(Stat_id) FROM HEIGHTMAP_STATISTICS)";
			stmt.executeUpdate(sql);
			
			DBConnectionManager.getConnection().close(); 
		}
		catch (SQLException se) { // Handle errors for JDBC
			se.printStackTrace();
		}
	}
	
	/** 	  
	 * This function allows the user to retrieve the parameters for a map previously created
	 *  
	 * @param mapId id of the map the user want to retrieve
	 * @return ResultSet object used for creating output, 
	 * contains data including the algorithm name, the height and width of the map, but also the map parameters.
	 */
	public static ResultSet retrieveMapParameters(String mapId) {
		ResultSet rs = null;
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			String sql = "SELECT Algorithm_name, Height, Width, Map_parameters FROM HEIGHTMAP_PARAMETERS WHERE Map_id="+mapId;
			rs = stmt.executeQuery(sql);
			DBConnectionManager.getConnection().close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return rs;
	}
	/**
	 * This function all the data from the two first table in order to display
	 * @return
	 */
	public static ResultSet retrieveFullMapParameters() {
		ResultSet rs = null;
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			String sql = "SELECT Map_id, Algorithm_name, Height, Width, Map_parameters, Date, Time FROM HEIGHTMAP_PARAMETERS";
			rs = stmt.executeQuery(sql);	
			DBConnectionManager.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ResultSet retrieveFullMapStatistics() {
		ResultSet rs = null;
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			String sql = "SELECT Stat_id, Algorithm_name, Max_value, Min_value, Average_value, Median_value, Height_histogram, Date, Time FROM HEIGHTMAP_STATISTICS";
			rs = stmt.executeQuery(sql);		
			DBConnectionManager.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 
	 * @param licenseNumber
	 * @return
	 */
	public static ResultSet retrieveLicense(String licenseNumber) {
		ResultSet rs = null;
		try {
			// Database connection
			stmt = DBConnectionManager.getConnection().createStatement();
			// Execute retrieve query
			String sql = "SELECT License_number, License_type, Authorized_use_time FROM LICENSES WHERE License_number='"+licenseNumber+"'";
			rs = stmt.executeQuery(sql);
			// Close database connection
			DBConnectionManager.getConnection().close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return rs;
	}

	/**
	 * This function delete the table HEIGHTMAP_PARAMETERS from the database
	 */
	public static void deleteTableMapParameters() {
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			String sql = "DELETE FROM HEIGHTMAP_PARAMETERS";
			// Execute delete query
			stmt.executeUpdate(sql);
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/**
	 * This function delete the table HEIGHTMAP_STATISTICS from the database
	 */
	public static void deleteTableMapStatistics() {
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			String sql = "DELETE FROM HEIGHTMAP_STATISTICS";
			stmt.executeUpdate(sql);
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
	/**
	 * This function delete the table LICENSES from the database
	 */
	public static void deleteTableLicenses() {
		try {
			stmt = DBConnectionManager.getConnection().createStatement();
			String sql = "DELETE FROM LICENSES";
			stmt.executeUpdate(sql);
			DBConnectionManager.getConnection().close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}
}



/*
sql = "INSERT INTO LICENSES(License_number, License_type, Authorized_use_time) " + 
		"VALUES ('1234-1234-1234-1234', '2', '0')," //50 years license
		+ "('5483-1890-4832-0233', '1', '3600')," // 1 hour license
		+ "('1234-5678-9101-1121', '1', '86400')," // 24 hour license
		+ "('3242-5262-7282-9303', '1', '604800')," // 1 week license
		+ "('1323-3343-5363-7383', '1', '2592000')," // 1 month license
		+ "('9405-0515-2535-4555', '1', '7776000')," // 3 months license
		+ "('6061-7989-5608-8870', '1', '15552000')," // 6 months license
		+ "('6961-3289-5028-1266', '1', '31536000')"; // 1 year license
*/
