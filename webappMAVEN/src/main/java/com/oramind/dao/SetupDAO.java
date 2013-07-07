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
package com.oramind.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.oramind.bean.UserBean;

/**
 * DAO for setting up the database to accept stress tests, from the device or the server.
 * @author Christos Sotiriou
 *
 */
public class SetupDAO extends AbstractDAO {
	
	public SetupDAO(){
		super();
	}
	
	public SetupDAO(Connection conn){
		super(conn);
	}
	
	
	/**
	 * Sets up the database for accepting test calls from the device.
	 * Note that the code is simple, although the cyclomatic complexity is high. The code first creates
	 * a new user of the service, then a test student, then a device for this student, then some messages for
	 * this student, send by the user created.
	 * @throws Exception
	 */
	public void databaseSetup() throws Exception{
		Connection localConnection = getConnection();
        setReusableConnection(localConnection);
        
		String sqlInsertStudents = "INSERT INTO oradb.students (name, lastname, email, registered) VALUES ('TestUser', 'test', 'testemail@test.com', 1);";
		String deviceSetup = "INSERT INTO oradb.device (identifier, studentid, macaddress, platform, registered, apikey) VALUES ('6907ad1fcaf00f9513d907ee4649c5b4b3451efce3f849495c9bbea328a561ba', ?, 'testaddress', 'ios', 1, 'apiKeyTest');";
		
		UserBean bean  = setupAndReturnTestUser();

		/**
		 * Create a new test user, or get the existing one, if present.
		 */
		if (bean == null) {
			bean = new UserDAO(localConnection).getUserWithCredentials("testRoot", "testRoot");
		}
		if (bean == null) {
			throw new Exception("Could not set up database, because a new student could not be inserted, and a test student also could not be retrieved");
		}else{
			try {
				/*
				 * Create the test student
				 */
				PreparedStatement studentStatement = getTrackedPreparedStatement(sqlInsertStudents, true);
				ResultSet insertedStudents = getTrackedResultSet(studentStatement, QueryType.updateReturnGeneratedKeys);
				
				if (insertedStudents != null) {
					int studentIDInserted = -1;
					while (insertedStudents.next()) {
						studentIDInserted = insertedStudents.getInt(1);
					}
					/*
					 * If the student was successfully created, then insert a device and associate it with the student.
					 */
					if (studentIDInserted >= 0) {
						int deviceidInserted = -1;
						
						PreparedStatement deviceStatement = getTrackedPreparedStatement(deviceSetup, true);
						deviceStatement.setInt(1, studentIDInserted);
						ResultSet insertedDevices = getTrackedResultSet(deviceStatement, QueryType.updateReturnGeneratedKeys);
						
						/*
						 * If the device was successfully inserted, also insert 100 test messages to the database. 
						 */
						if (insertedDevices != null) {
							while (insertedDevices.next()) {
								deviceidInserted = insertedDevices.getInt(1);
							}
							if (deviceidInserted >= 0) {
								for (int i = 0; i < 100; i++) {
									new MessageDAO(localConnection).sendMessagesToDatabase("testContent", new int[]{ studentIDInserted}, bean.getUserID(), this.getConnection());	
								}
							}
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				cleanup();
				cleanupReusableResources();
			}
		}
		
	}
	
	
	/**
	 * Destroys everything that the {@link #databaseSetup()} created.
	 * @throws Exception
	 */
	public void databaseTestTearDown() throws Exception{
        Connection newConnection = getConnection();
		
        String deleteMessageAssoc = "DELETE FROM messageassociations WHERE studentid IN (SELECT studentid FROM students WHERE email LIKE '%test%');";
        String deleteMessages = "DELETE FROM messages WHERE content LIKE '%testContent%';";
		String serviceUserDelete = "DELETE FROM serviceusr WHERE username LIKE '%testRoot%';";
        String studentDelete = "DELETE FROM students WHERE email like '%test%';";
        String deviceDelete = "DELETE FROM device WHERE macaddress LIKE '%test%';";
        
		try{
			newConnection.setAutoCommit(false);
			Statement st = getTrackedStatement(newConnection);
			
			st.addBatch(deleteMessageAssoc);
			st.addBatch(deleteMessages);
			st.addBatch(serviceUserDelete);
			st.addBatch(studentDelete);
			st.addBatch(deviceDelete);
			st.executeBatch();
            newConnection.commit();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
            cleanupReusableResources();
		}
	}
	
	/**
	 * Inserts a new student into the database, and returns the newly created {@link UserBean}
	 * @return
	 */
	private UserBean setupAndReturnTestUser(){
		String query = "INSERT INTO serviceusr(username, userpass, userlevel) VALUES ('testRoot', 'testRoot', 1);";
		UserBean bean = null;
		try {
			PreparedStatement studentStatement = getTrackedPreparedStatement(query, true);
			ResultSet insertedUsers = getTrackedResultSet(studentStatement, QueryType.updateReturnGeneratedKeys);
			if (insertedUsers != null) {
				bean = new UserDAO(getConnection()).getUserWithCredentials("testRoot", "testRoot");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return bean;
	}
	
}
