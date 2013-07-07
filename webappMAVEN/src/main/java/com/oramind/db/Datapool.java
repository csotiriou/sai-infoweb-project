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

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.oramind.util.Util;

@Deprecated
public class Datapool {
//	DataSource dsPooled = null;
	ComboPooledDataSource cpds = new ComboPooledDataSource();

	private static DataSource dataSource;

	static {
		dataSource = setupDataSource();
	}
	
	private static DataSource setupDataSource() {
		Util.println("setting up datasource");
		String dbName = DBProperties.getInstance().getProperty("dbname");
		String dbHost = DBProperties.getInstance().getProperty("dbhost");
		String dbPort = DBProperties.getInstance().getProperty("dbport");
		String dbuser = DBProperties.getInstance().getProperty("dbuser");
		String dbpass = DBProperties.getInstance().getProperty("dbpass");

		String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		cpds.setJdbcUrl(url);
		cpds.setUser(dbuser);
		cpds.setPassword(dbpass);

		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);
		
		
//		cpds.setMinPoolSize(5);
//		cpds.setAcquireIncrement(5);
//		cpds.setMaxPoolSize(20);
//		cpds.setInitialPoolSize(10);
		cpds.setMaxStatementsPerConnection(30);
		cpds.setCheckoutTimeout(10000);
//		cpds.setIdleConnectionTestPeriod(10);
		cpds.setMaxIdleTime(15);
		cpds.setMaxConnectionAge(20);
		
		
		
		return cpds;
	}
	
	@Deprecated
	public static Connection getOracleConnection() throws SQLException {
    	return dataSource.getConnection();
    }

	
	
}
