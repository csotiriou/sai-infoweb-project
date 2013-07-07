/*******************************************************************************
 * Copyright (c) 2013, Christos Sotiriou
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 * -- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * -- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.oramind.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.oramind.util.Util;


public class DBConfig {
	final String SQL_SCRIPT = "config/ConfigScript.sql";

	Connection con = null;



	/**
	 * Creates the basic directory structure the application needs to
	 * run
	 */
	public void createDBStructure(){

		Statement st = null;
		ResultSet rs = null;

		String dbName = DBProperties.getInstance().getProperty("dbname");
		String dbHost = DBProperties.getInstance().getProperty("dbhost");
		String dbPort = DBProperties.getInstance().getProperty("dbport");

		String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/";

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


		try {

			con = DriverManager.getConnection(url, DBProperties.getInstance().getProperty("dbuser"), DBProperties.getInstance().getProperty("dbpass"));
			st = con.createStatement();
			//			st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
			String databaseCreationString = "CREATE DATABASE IF NOT EXISTS " + dbName + ";";
			String databaseSelectString = "USE " + dbName + ";"; 

			Util.println(databaseCreationString);
			Util.println(databaseSelectString);
			st.executeUpdate(databaseCreationString);
			st.execute(databaseSelectString);


			String studentCreationString = "CREATE TABLE `students` ( `studentid` int(11) NOT NULL AUTO_INCREMENT, " +
					"`name` varchar(45) DEFAULT NULL, " +
					"`lastname` varchar(45) NOT NULL, " +
					"`email` varchar(128) NOT NULL, " +
					"`apikey` varchar(128) DEFAULT NULL, " +
					"`registered` tinyint(4) NOT NULL DEFAULT '0', " +
					"`registrationkey` varchar(128) DEFAULT NULL, PRIMARY KEY (`studentid`), " +
					"UNIQUE KEY `email_UNIQUE` (`email`), " +
					"UNIQUE KEY `registrationkey_UNIQUE` (`registrationkey`), " +
					"UNIQUE KEY `apikey_UNIQUE` (`apikey`))";

			String deviceTableCreationString = "CREATE TABLE `device` (" +
					" `deviceid` int(11) NOT NULL AUTO_INCREMENT, " +
					"`identifier` TEXT NOT NULL," +
					" `studentid` int(11) DEFAULT NULL, " +
					" `macaddress` varchar(45) NOT NULL," +
					" `platform` varchar(45) NOT NULL," +
					"`registered` tinyint(4) NOT NULL DEFAULT '0'," +
					" `apikey` varchar(128), " +
					" PRIMARY KEY (`deviceid`), " +
					" UNIQUE KEY `deviceid_UNIQUE` (`deviceid`), " +
					" UNIQUE KEY `macaddress_UNIQUE` (`macaddress`));";

			String serviceCreationUserString = "CREATE  TABLE `oradb`.`serviceusr` (" +
					"`userid` INT NOT NULL AUTO_INCREMENT ," + 
					"`username` VARCHAR(45) NOT NULL ," + 
					"`userlevel` TINYINT NOT NULL DEFAULT 1 ," +
					"`userpass` VARCHAR(45) NOT NULL DEFAULT ''," +
					"PRIMARY KEY (`userid`, `username`) ," +
					"UNIQUE INDEX `username_UNIQUE` (`username` ASC) );";

			String messagesCreationString = "CREATE  TABLE `oradb`.`messages` (" +
					"`messageid` INT NOT NULL AUTO_INCREMENT , " +
					"`content` VARCHAR(256) NOT NULL , " +
					"`userid` INT NOT NULL , " +
					"`date` DATETIME NOT NULL , " +
					"`studentid` int(11) NOT NULL," +
					"PRIMARY KEY (`messageid`, `date`) ," +
					"UNIQUE INDEX `messageid_UNIQUE` (`messageid` ASC) );";

			String registrationCreationString = "CREATE TABLE `registration` ( " + 
					"`registrationid` int(11) NOT NULL AUTO_INCREMENT," + 
					"`studentid` int(11) NOT NULL," + 
					"`registrationtoken` varchar(128) NOT NULL, " +
					"`deviceid` int(11) NOT NULL, " +
					"PRIMARY KEY (`registrationid`), " +
					"UNIQUE KEY `registrationtoken_UNIQUE` (`registrationtoken`)," +
					"UNIQUE KEY `deviceid_UNIQUE` (`deviceid`)," +
					"UNIQUE KEY `registrationid_UNIQUE` (`registrationid`));";

			String registrationProcedureString = "CREATE PROCEDURE `handleRegistration`(IN devid INT, IN stid INT, IN regid INT) " +
					"BEGIN " + 
					"UPDATE device SET registered='1' where deviceid=devid;" +
					"UPDATE students SET registered='1' where studentid=stid; " +
					"DELETE FROM registration WHERE registrationid=regid; " +
					"END;";

			String authenticationString = "CREATE TABLE `authentication` ( " +
					"`authenticationid` int(11) NOT NULL AUTO_INCREMENT, " +
					" `token` varchar(128) NOT NULL," +
					" `studentid` int(11) DEFAULT NULL," +
					" `expiry` datetime NOT NULL," +
					" PRIMARY KEY (`authenticationid`,`token`)," +
					" UNIQUE KEY `token_UNIQUE` (`token`) );";

			String apiKeysString = "CREATE TABLE `apikeys` (" +
					" `keyid` int(11) NOT NULL AUTO_INCREMENT," +
					" `keytoken` varchar(128) NOT NULL," +
					" PRIMARY KEY (`keyid`,`keytoken`)," +
					" UNIQUE KEY `idapikeys_UNIQUE` (`keyid`)," +
					" UNIQUE KEY `keytoken_UNIQUE` (`keytoken`) );";




			Util.println(studentCreationString);
			Util.println(deviceTableCreationString);
			Util.println(serviceCreationUserString);
			Util.println(messagesCreationString);
			Util.println(registrationCreationString);
			Util.println(registrationProcedureString);
			Util.println(authenticationString);
			Util.println(apiKeysString);


			//create all necessary tables
			st.executeUpdate(studentCreationString);
			st.executeUpdate(deviceTableCreationString);
			st.executeUpdate(serviceCreationUserString);
			st.executeUpdate(messagesCreationString);
			st.executeUpdate(registrationCreationString);
			st.execute(registrationProcedureString);
			st.executeUpdate(authenticationString);
			st.executeUpdate(apiKeysString);

			//insert the root user
			st.executeUpdate("INSERT INTO `oradb`.`serviceusr` (`username`, `userlevel`) VALUES ('root', '1');");



		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	public void createDBStructureFromScriptFile() throws SQLException, FileNotFoundException, IOException{
		Util.println("creating database...");

		BufferedReader br = null;
		Connection dbConn = getConnection();

		CallableStatement stmt = null;
		String line = null;
//		String url = "???";
		StringBuffer sql = null;
		try {
			InputStream is = DBConfig.class.getClassLoader().getResourceAsStream(SQL_SCRIPT);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is);
				br = new BufferedReader(isr);
				sql = new StringBuffer();
				while ((line = br.readLine()) != null) {
					Util.println(line);
					sql.append(line);
					sql.append(" ");
				}
//				dbConn = DriverManager.getConnection(url);
				stmt = dbConn.prepareCall(sql.toString());
				stmt.execute();
			}else{
				Util.println("not found!");
			}
		}
		catch (Exception x) {
			System.err.println("Exception caught: " + x);
			x.printStackTrace();
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (Exception x) {
					System.err.println("Failed to close resource.");
					x.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception x) {
					System.err.println("Failed to close statement.");
					x.printStackTrace();
				}
			}
			if (dbConn != null) {
				try {
					dbConn.close();
				}
				catch (Exception x) {
					System.err.println("Failed to close DB connection.");
					x.printStackTrace();
				}
			}
		}
	}

	private Connection getConnection() throws SQLException{
		if (this.con == null || this.con.isClosed() == true) {
			//			String dbName = DBProperties.getInstance().getProperty("dbname");
			String dbHost = DBProperties.getInstance().getProperty("dbhost");
			String dbPort = DBProperties.getInstance().getProperty("dbport");

			String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/";

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			con = DriverManager.getConnection(url, DBProperties.getInstance().getProperty("dbuser"), DBProperties.getInstance().getProperty("dbpass"));
		}
		return con;
	}

}
