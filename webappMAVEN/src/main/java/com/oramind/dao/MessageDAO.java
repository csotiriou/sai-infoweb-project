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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.oramind.bean.MessageBean;
import com.oramind.bean.MessageBeanWeb;
import com.oramind.bean.StudentBean;


public class MessageDAO extends AbstractDAO {

	public MessageDAO(){
		super();
	}

	public MessageDAO(Connection con){
		super(con);
	}


	/**
	 * Sends the current message to the database, and associates each student id found in the argument with this
	 * message
	 * @param messageContent The content of the message.
	 * @param studentIDs The student ids to associate this message with 
	 * @param userID the user ID which sent the message
	 * @param con A {@link Connection} object to use for this sql transaction. May be null. In case of null, a new {@link Connection} will be obtained.
	 */
	public void sendMessagesToDatabase(String messageContent, int [] studentIDs, int userID, Connection con){
		Connection currentConnection = con;
		if (con == null) {
			con = getConnection();
		}
		try {

			currentConnection.setAutoCommit(false);

			String query = "INSERT INTO messages (content, date, userid) VALUES (?, NOW(), ?)";
			String queryPhase2 = "INSERT INTO messageassociations (messageid, studentid) VALUES (?, ?)";


			PreparedStatement st = getTrackedPreparedStatement(query, currentConnection, true);

			st.setString(1, messageContent);
			st.setInt(2, userID);

			ResultSet generatedKey = getTrackedResultSet(st, QueryType.updateReturnGeneratedKeys);

			int newMessageID = -1;
			while (generatedKey.next()) {
				newMessageID = generatedKey.getInt(1);
			}


			PreparedStatement phase2Statement = getTrackedPreparedStatement(queryPhase2, currentConnection, false);
			for(int i: studentIDs){
				phase2Statement.setInt(1, newMessageID);
				phase2Statement.setInt(2, i);
				phase2Statement.addBatch();
			}
			phase2Statement.executeBatch();


			currentConnection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}


	/**
	 * Sends the current message to the database, and associates each student id found in the argument with this
	 * message. Calls {@link #sendMessagesToDatabase(String, int[], int, Connection)} with a null connection as the
	 * argument, to force it to obtain a new one.
	 * @param messageContent The content of the message.
	 * @param studentIDs The student ids to associate this message with 
	 * @param userID the user ID which sent the message
	 */
	public void sendMessagesToDatabase(String messageContent, int [] studentIDs, int userID){
		sendMessagesToDatabase(messageContent, studentIDs, userID, getConnection());
	}


	/**
	 * Returns all messages associated with an api key of a student
	 * @param apiKey the api  key of the student
	 * @param first optional. the offset of the first message returned (for paging)
	 * @param resultCount. the count of the messages returned (for paging)
	 * @return a {@link List} of messages associated with the student.
	 */
	public List<MessageBean> getMessagesForAPIKey(String apiKey, Integer first, Integer resultCount){
		String query = "SELECT msg.messageid, msg.content, msg.userid, msg.date, usr.username AS username " +
				"FROM messageassociations AS assoc " +
				"LEFT OUTER JOIN messages AS msg ON msg.messageid = assoc.messageid " +
				"LEFT OUTER JOIN serviceusr AS usr ON usr.userid = msg.userid " +
				"WHERE assoc.studentid = (SELECT studentid FROM device WHERE apikey LIKE ?)";

		if (first!= null && resultCount != null) {
			query += " limit " + first.intValue() + ", " + resultCount.intValue();
		}
		query +=";";

		List<MessageBean> result = new ArrayList<MessageBean>();

		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setString(1, apiKey);
			ResultSet resultSet = getTrackedResultSet(st, QueryType.select);
			while (resultSet.next()){
				MessageBean message = new MessageBean();
				message.loadFromResultSet(resultSet);
				result.add(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return result;
	}


	/**
	 * Gets the messages for after a date given as the argument
	 * @param apiKey the api key of a user
	 * @param date the date after which to search messages
	 * @return a {@link List} of {@link MessageBean}s. Can also be an empty list.
	 */
	public List<MessageBean> getMessagesForApiKeyAfterDate(String apiKey, Date date){
		List<MessageBean> result = new ArrayList<MessageBean>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = format.format(date);
		
		String query = "SELECT msg.messageid, msg.content, msg.userid, msg.date, usr.username AS username " +
				"FROM messageassociations AS assoc " + 
				"LEFT OUTER JOIN messages AS msg ON msg.messageid = assoc.messageid " +
				"LEFT OUTER JOIN serviceusr AS usr ON usr.userid = msg.userid " +
				"WHERE (assoc.studentid = (SELECT studentid FROM device WHERE apikey LIKE ?) " +
				"AND msg.date > ?);";
		
		try{
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setString(1, apiKey);
			st.setString(2, dateString);
			ResultSet resultSet = getTrackedResultSet(st, QueryType.select);
			while (resultSet.next()){
				MessageBean message = new MessageBean();
				message.loadFromResultSet(resultSet);
				result.add(message);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return result;
	}

	
	
	/**
	 * Returns all students associated with a message. That is, to which
	 * students was this message sent.
	 * @param messageID the message id
	 * @param apiKey an apiKey of a registered device, proving that the student
	 * asking this information is a valid registered user of the service (because he
	 * uses a device which has a valid token)
	 * @return a {@link List} of students.
	 */
	public List<StudentBean> getStudentsAssociatedWithMessage(int messageID, String apiKey){
		String query = "SELECT st.name, st.lastname, st.email, st.studentid, st.registered, st.registrationkey " +
				"FROM messageassociations AS msg LEFT OUTER JOIN students AS st ON msg.studentid = st.studentid " +
				"WHERE (EXISTS (SELECT * FROM device WHERE device.apikey LIKE ? ) )  " +
				"AND msg.messageid = ?;";
		List<StudentBean> students = new ArrayList<StudentBean>();
		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setString(1, apiKey);
			st.setInt(2, messageID);
			ResultSet set  = getTrackedResultSet(st, QueryType.select);

			while (set.next()) {
				StudentBean student = new StudentBean();
				student.loadFromResultSet(set);
				students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			cleanup();
		}
		return students;
	}


	/**
	 * Gets the messages that have been sent from the database. Also includes information
	 * like the name and last name of the student, among others. The returned objects are
	 * instances of {@link MessageBeanWeb}
	 * @param offset
	 * @param count
	 * @return a list of messages that have been sent
	 */
	public List<MessageBeanWeb> getAllMessages(Integer offset, Integer count){
		String query = "SELECT msg.messageid, msg.content, st.name, st.lastname, msg.userid, msg.date " + 
				"FROM messageassociations AS assoc " +
				"LEFT OUTER JOIN messages AS msg ON msg.messageid = assoc.messageid " +
				"LEFT OUTER JOIN students AS st ON st.studentid = assoc.studentid " +
				"limit ? offset ?;";

		List<MessageBeanWeb> result = new ArrayList<MessageBeanWeb>();

		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setInt(1, count.intValue());
			st.setInt(2, offset.intValue());
			ResultSet set  = getTrackedResultSet(st, QueryType.select);

			while (set.next()){
				MessageBeanWeb message = new MessageBeanWeb();
				message.loadFromResultSet(set);
				result.add(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return result;
	}


	/**
	 * Deletes all messages associated with a student ID. It does not delete the message entries, but it deletes
	 * the message association entries found in the messageassociations table in the database.
	 * @param studentID
	 */
	public void deleteAllMessagesForStudentID(int studentID){
		String associationDeletionQuery = "DELETE FROM messageassociations WHERE studentid = ?;";

		try {
			PreparedStatement st = getTrackedPreparedStatement(associationDeletionQuery);
			st.setInt(1, studentID);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}


}
