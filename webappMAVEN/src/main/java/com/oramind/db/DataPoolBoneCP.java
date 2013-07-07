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

import java.sql.Connection;
import java.sql.SQLException;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import com.oramind.util.Util;



/**
 * Datasource used for getting connections from a BoneCP
 * datapool. It is supposed to be a sessionwide object Note that BoneCP does not support reconnect,
 * so if you try to connect to a non-existing service, and you activate the service afterwards,
 * you need to also restart the server. This limitation originates from the framework.
 * @author Christos Sotiriou
 *
 */
public class DataPoolBoneCP {

	private static BoneCPDataSource dataSource = null;
	private static boolean isShutDown = true;


	private static BoneCPDataSource setupDataSource(){
		Util.println("setting up data source...");
		String dbName = DBProperties.getInstance().getProperty("dbname");
		String dbHost = DBProperties.getInstance().getProperty("dbhost");
		String dbPort = DBProperties.getInstance().getProperty("dbport");
		String dbuser = DBProperties.getInstance().getProperty("dbuser");
		String dbpass = DBProperties.getInstance().getProperty("dbpass");
		String otherArguments = DBProperties.getInstance().getProperty("mysqlPortURLArguments");
		
		String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + otherArguments;
		Util.println("url: " + url);

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(url); 
		config.setUsername(dbuser); 
		config.setPassword(dbpass);
		
		/*
		 * config taken from http://blog.progs.be/139/jdbc-connection-pooling
		 */
		config.setMinConnectionsPerPartition(10);
		config.setMaxConnectionsPerPartition(30);
		config.setPartitionCount(3);
		config.setAcquireIncrement(5);
		config.setStatementsCacheSize(10);
		config.setReleaseHelperThreads(3);
		config.setConnectionTimeoutInMs(1000 * 20);

			

		BoneCPDataSource ds = new BoneCPDataSource(config);
		isShutDown = false;

		return ds;
	}


	public static void shutDown(){
		Util.println("Custom data source shutting down...");
		if (dataSource != null) {
			dataSource.close();
			dataSource = null;
		}
		isShutDown = true;
	}

	public static Connection getOracleConnection() throws SQLException {
		if (isShutDown) {
			if (dataSource == null) {
				dataSource = setupDataSource();
			}
		}
		return dataSource.getConnection();
	}}
