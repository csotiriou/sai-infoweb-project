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
import java.util.ArrayList;
import java.util.List;

import com.oramind.bean.StudentBean;
import com.oramind.util.Util;

public class StudentDAO extends AbstractDAO {


	public StudentDAO(){
		super();
	}

	public StudentDAO(Connection conn){
		super(conn);
	}

	/**
	 * Deletes a student with a specified ID. Also deletes this student's messages and
	 * devices from the database
	 * @param studentID
	 * @throws Exception
	 */
	public void deleteStudentWithStudentID(int studentID){
		/*
		 * Generate a new connection to re-use between objects.
		 */
		Connection connection = getConnection();
		
		String studentDeletionQuery = "DELETE FROM students WHERE studentid= ?;";
		
		
		DeviceDAO deviceDao = new DeviceDAO(connection);
		deviceDao.deleteDevicesAssociatedWithStudent(studentID);
		
		MessageDAO messageDao = new MessageDAO(connection);
		messageDao.deleteAllMessagesForStudentID(studentID);
		
		try {
			PreparedStatement st = getTrackedPreparedStatement(studentDeletionQuery, connection, false);
			st.setInt(1, studentID);
			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}


	/**
	 * Gets a student with the specified ID
	 * @param studentID
	 * @return a {@link StudentBean}, or null of a student was not found
	 */
	public StudentBean getStudentWithID(int studentID){
		String query = "SELECT * FROM students WHERE studentid=? LIMIT 1;";

		StudentBean bean = null;
		try {
			Connection reusableConnection = getConnection();

			PreparedStatement st = getTrackedPreparedStatement(query, false);
			st.setInt(1, studentID);

			ResultSet resultSet = getTrackedResultSet(st, QueryType.select);

			DeviceDAO deviceDAO = new DeviceDAO(reusableConnection);
			while (resultSet.next()) {
				bean = new StudentBean();
				bean.loadFromResultSet(resultSet);
				bean.setDevices(deviceDAO.getDevicesForStudentID(studentID));	
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return bean;
	}


	/**
	 * Gets a {@link StudentBean} associated with an e-mail
	 * @param email
	 * @return a {@link StudentBean} or null of no student was found.
	 */
	public StudentBean getStudentWithEmail(String email){
		String query = "SELECT * FROM students WHERE email=? LIMIT 1;";
		StudentBean bean = null;
		Connection reusableConnection = getConnection();

		try{
			PreparedStatement st = getTrackedPreparedStatement(query, reusableConnection, false);
			st.setString(1, email);
			ResultSet resultSet = getTrackedResultSet(st, QueryType.select);

			DeviceDAO deviceDAO = new DeviceDAO(reusableConnection);

			while (resultSet.next()) {
				bean = new StudentBean();
				bean.loadFromResultSet(resultSet);
				bean.setDevices(deviceDAO.getDevicesForStudentID(bean.getStudentID()));					
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return bean;
	}



	

	/**
	 * Gets a student who is associated with a device id passed as the argument
	 * @param apiKey the api key to associate the student with
	 * @return a {@link StudentBean}, or null if no such row was found
	 */
	public StudentBean getStudentWithAPIKey(String apiKey){
		String query = "select * from students where studentid = (select studentid from device where apiKey like ?)";
		StudentBean result = null;
		try{
			PreparedStatement statement = getTrackedPreparedStatement(query);
			statement.setString(1, apiKey);
			ResultSet resultSet = getTrackedResultSet(statement, QueryType.select);
			while(resultSet.next()){
				result = new StudentBean();
				result.loadFromResultSet(resultSet);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return result;
	}
	
	public List<StudentBean> getAllStudents(){
		return getStudents("%", "%", "%");
	}


	/**
	 * Gets the students of the service, by specifying arguments. Note that the arguments can also be null. In case where
	 * a null argument is given, it counts as a "wildcard", meaning that the search is broadened.
	 * 
	 * @param name The name of the student. Can also be null.
	 * @param lastname The last name of the student. Can also be null.
	 * @param email The e-mail of the student. Can also be null.
	 * @return a list of {@link StudentBean}s
	 */
	public List<StudentBean> getStudents(String name, String lastname, String email){
		List<StudentBean> result = new ArrayList<StudentBean>();

		String query = "SELECT * FROM students WHERE name LIKE ? AND lastname LIKE ? AND email LIKE ?";
		try {
			PreparedStatement statement = getTrackedPreparedStatement(query);
			statement.setString(1, name == null? "%" : name);
			statement.setString(2, lastname == null? "%" : lastname);
			statement.setString(3, email == null ? "%" : email);

			Util.println(query);
			ResultSet resultSet = getTrackedResultSet(statement, QueryType.select);

			if (resultSet != null) {
				while (resultSet.next()){
					StudentBean newStudent = new StudentBean();
					newStudent.loadFromResultSet(resultSet);
					result.add(newStudent);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return result;
	}
}
