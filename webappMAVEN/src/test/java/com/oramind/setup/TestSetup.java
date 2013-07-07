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
package com.oramind.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.oramind.dao.SetupDAO;
import com.oramind.db.DBProperties;

/**
 * Run this program as a java application to set up the server to accept tests
 * from the iOS application
 * @author Christos Sotiriou
 *
 */
public class TestSetup {

	
	public static void main(String[] args) {
		String dbName = DBProperties.getInstance().getProperty("dbname");
		String dbHost = DBProperties.getInstance().getProperty("dbhost");
		String dbPort = DBProperties.getInstance().getProperty("dbport");
		String dbuser = DBProperties.getInstance().getProperty("dbuser");
		String dbpass = DBProperties.getInstance().getProperty("dbpass");
		
		String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
		
		Connection con =null;
		
		try {
			con = DriverManager.getConnection(url, dbuser, dbpass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		SetupDAO dao = new SetupDAO(con);
		try {
			dao.databaseSetup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
