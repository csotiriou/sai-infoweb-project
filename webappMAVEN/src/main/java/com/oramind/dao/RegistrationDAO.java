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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.oramind.bean.RegistrationBean;
import com.oramind.util.Util;

public class RegistrationDAO extends AbstractDAO{


	@Deprecated
	public RegistrationBean getRegistrationWithToken(String registrationToken){
		String query = "SELECT * FROM registration WHERE registrationtoken = ?";

		RegistrationBean resultBean = null;

		try {
			PreparedStatement statement = getTrackedPreparedStatement(query);
			statement.setString(1, registrationToken);
			ResultSet resultSet = getTrackedResultSet(statement, QueryType.select);

			while (resultSet.next()) {
				resultBean = new RegistrationBean();
				resultBean.loadFromResultSet(resultSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return resultBean;
	}

	/**
	 * Registers the device and student, both associated with the registrationToken, passed as the argument.
	 * Calls the stored procedure "handleRegistration"
	 * @param registrationToken The registration token as the argument
	 */
	public void handleRegistration(String registrationToken){
		String query = "call handleRegistration(?)";
		try{
			CallableStatement statement = getTrackedCallableStatement(query);
			statement.setString(1, registrationToken);
			statement.execute();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}
	
	/**
-	 * 	IN studentName varchar(48),
-		IN surname varchar(48),
-		IN studentEmail varchar(70),
-		IN apns varchar(128),
-		IN mac varchar(128),
-		IN plat varchar(10),
-		OUT alreadyExists INT,
-		OUT outStudentID INT,
-		OUT regToken varchar(128),
-		OUT devID INT,
-		OUT regID INT
+	 * Insert a new student into the database. This stage is handled by a stored procedure (due to its complex nature). The
+	 * signature of the stored procedure is as follows:
+	 * <ul><li>IN studentName varchar(48),</li>
+		<li>IN surname varchar(48),</li>
+		<li>IN studentEmail varchar(70),</li>
+		<li>IN apns varchar(128),</li>
+		<li>IN mac varchar(128),</li>
+		<li>IN plat varchar(10),</li>
+		</ul>
+		And the returned values are as follows:<br>
+		<ul><li>OUT alreadyExists INT,</li>
+		<li>OUT outStudentID INT,</li>
+		<li>OUT regToken varchar(128),</li>
+		<li>OUT devID INT,</li>
+		<li>OUT regID INT</li>
+		</ul>

	 * @param name
	 * @param lastName
	 * @param email
	 * @param apns
	 * @param macAddress
	 * @param platform
	 * @return a {@link RegistrationBean} containing information about the student and the device added
	 */
	public RegistrationBean registerNewStudentAndDevice(String name, String lastName, String email, String apns, String macAddress, String platform){
		Util.println("registering student with device:");
		String query = "{CALL registerStudent(?,?,?,?,?,?,?,?,?,?,?)}";
		CallableStatement statement;
		RegistrationBean result = null;
		try {
			statement = getTrackedCallableStatement(query);
			statement.setString(1, name);
			statement.setString(2, lastName);
			statement.setString(3, email);
			statement.setString(4, apns);
			statement.setString(5, macAddress);
			statement.setString(6, platform);

			statement.registerOutParameter(7, Types.INTEGER);
			statement.registerOutParameter(8, Types.INTEGER);
			statement.registerOutParameter(9, Types.VARCHAR);
			statement.registerOutParameter(10, Types.INTEGER);
			statement.registerOutParameter(11, Types.INTEGER);

			statement.execute();

			boolean alreadyExists = statement.getBoolean(7);
			int studentID = statement.getInt(8);
			String registrationToken = statement.getString(9);
			int deviceID = statement.getInt(10);
			int registrationID = statement.getInt(11);

			if (alreadyExists) {
				Util.println("registration bean is null, because of trying to register different students with the same device");
				return null;
			}else{
				result = new RegistrationBean();
				result.setDeviceID(deviceID);
				result.setRegistrationID(registrationID);
				result.setRegistrationToken(registrationToken);
				result.setStudentID(studentID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return result;
	}
}
