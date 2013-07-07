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

import java.util.Date;
import java.util.List;

import com.oramind.bean.DeviceBean;
import com.oramind.bean.MessageBean;
import com.oramind.bean.MessageBeanWeb;
import com.oramind.bean.RegistrationBean;
import com.oramind.bean.StudentBean;
import com.oramind.bean.StudentInfoBean;
import com.oramind.bean.UserBean;
import com.oramind.service.StressTests;


/**
 * DAO that abstracts calls to other DAOS. for the time being, it is only implementing the functionality found in the main servlets
 * that build the main service. Other servlets such as {@link StressTests} call individual DAOs on their own.
 * 
 * DESIGN CONCERNS:
 * Maybe getting rid of a central dao makes more sense, especially since after the creation of this DAO, {@link AbstractDAO} was
 * created adding reusable connection capabilities. 
 */
public class ServiceDAO {



	public UserBean getUserWithCredentials(String user, String pass){
		UserDAO dao = new UserDAO();
		return dao.getUserWithCredentials(user, pass);
	}



	public List<DeviceBean> getDevicesForStudentID(int studentID){
		return new DeviceDAO().getDevicesForStudentID(studentID);
	}

	/**
	 * Returns a list of {@link DeviceBean} which correspond to some studentIDs
	 * @param studentIDs the array of ints indicating the student ids to search for
	 * @param filterUnregistered if true, then only the registered devices will be returned. if false, all devices will be returned.
	 * @return the list of devices found.
	 */
	public List <DeviceBean> getdevicesForStudentIDs(int studentIDs[], boolean filterUnregistered){
		return new DeviceDAO().getdevicesForStudentIDs(studentIDs, filterUnregistered);
	}
	
	
	/**
	 * Gets a device with the specified ID
	 * @param deviceID
	 * @return
	 */
	public DeviceBean getDeviceWithID(int deviceID){
		return new DeviceDAO().getDeviceWithID(deviceID);
	}

	/**
	 * Deletes a device with the specified id
	 * @param deviceID
	 */
	public void deleteDeviceWithID(int deviceID){
		DeviceDAO dao = new DeviceDAO();
		dao.deleteDevice(deviceID);
	}


	public boolean deviceMACisAlreadyAssociated(String MACAddress, String currentMail){
		DeviceDAO devdao = new DeviceDAO();
		return devdao.deviceMACisAlreadyAssociated(MACAddress, currentMail);
	}


	/**
	 * Registers a new student with the following information
	 * @param name The name of the student
	 * @param lastName the last name of the student
	 * @param email the e-mail of the student
	 * @param apns the push notification token of the device
	 * @param macAddress the mac address of the device
	 * @param platform the platform of the device
	 * @return a {@link RegistrationBean} indicating information about the new registration. Use this to perform additional queries
	 * relevant to this registration
	 */
	public RegistrationBean registerNewStudentAndDevice(String name, String lastName, String email, String apns, String macAddress, String platform){
		RegistrationDAO dao = new RegistrationDAO();
		RegistrationBean regBean = dao.registerNewStudentAndDevice(name, lastName, email, apns, macAddress, platform);
		return regBean;
	}

	
	/**
	 * Deletes a student specified by his/her ID
	 * @param studentID
	 */
	public void deleteStudentWithStudentID(int studentID){
		StudentDAO dao = new StudentDAO();
		dao.deleteStudentWithStudentID(studentID);
	}


	/**
	 * Returns a {@link StudentBean} which corresponds to the studentID. It will contain the
	 * {@link StudentBean}{@link #getDevicesForStudentID(int)} with the student's devices which
	 * correspond to an array of {@link DeviceBean}
	 * @param studentID the student's ID
	 * @return a {@link StudentBean} with information about the student and devices
	 */
	public StudentBean getStudentWithID(int studentID){
		StudentDAO stDao = new StudentDAO();
		return stDao.getStudentWithID(studentID);
	}


	/**
	 * Gets information and statistics about a student specified by a device's api key 
	 * @param apiKey the api Key of the device that asks the information
	 * @return a {@link StudentInfoBean} with statistics about the student
	 */
	public StudentInfoBean getStudentInfoForAPIKey(String apiKey){
		StudentInfoDAO dao = new StudentInfoDAO();
		return dao.getStudentInfoForApiKey(apiKey);
	}

	/**
	 * Returns a list with all students registered to the database
	 * @return a {@link List} of {@link StudentBean}. Can also be empty.
	 */
	public List<StudentBean> getAllStudents(){
		return getStudents(null, null, null);
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
		StudentDAO studentDao = new StudentDAO();
		return studentDao.getAllStudents();
	}

	/**
	 * Sends messages to the database
	 * @param messageContent The content of the message.
	 * @param studentIDs an array of student ids. A message can be sent into many student
	 * @param userID the user ID which sends the message.
	 */
	public void sendMessagesToDatabase(String messageContent, int [] studentIDs, int userID){
		MessageDAO messageDAO = new MessageDAO();
		messageDAO.sendMessagesToDatabase(messageContent, studentIDs, userID);
	}

	/**
	 * Gets a list of {@link MessageBean}s for a student specified by a device with an api key
	 * given as the argument
	 * @param apiKey
	 * @param first
	 * @param resultCount
	 * @return
	 */
	public List<MessageBean> getMessagesForAPIKey(String apiKey, Integer first, Integer resultCount){
		MessageDAO messageDAO = new MessageDAO();
		return messageDAO.getMessagesForAPIKey(apiKey, first, resultCount);
	}
	
	
	/**
	 * Returns a list of {@link MessageBean}s that will were sent after the date given as the argument
	 * @param apiKey
	 * @param d the date
	 * @return {@link List} of {@link MessageBean}
	 */
	public List<MessageBean> getMessagesForApiKeyAfterDate(String apiKey, Date d){
		return new MessageDAO().getMessagesForApiKeyAfterDate(apiKey, d);
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
	public List<StudentBean> getStudentsAssociatedWithMessage(String apiKey, int messageID){
		MessageDAO dao = new MessageDAO();
		List<StudentBean> list = dao.getStudentsAssociatedWithMessage(messageID, apiKey);
		return list;
	}

	/**
	 * Gets the messages that have been sent from the database. Also includes information
	 * like the name and last name of the student, among others. The returned objects are
	 * instances of {@link MessageBeanWeb}
	 * @param offset
	 * @param count
	 * @return a list of messages that have been sent
	 */
	public List<MessageBeanWeb> getMessagesSent(Integer offset, Integer count){
		return new MessageDAO().getAllMessages(offset, count);
	}
	
	/**
	 * Returns the api key associated with a device, identified by that device's
	 * mac address.
	 * @param macAddress
	 * @return
	 */
	public String getApiKeyForMac(String macAddress){
		DeviceDAO dao = new DeviceDAO();
		return dao.getApiKeyForMac(macAddress);
	}

	/**
	 * Registers the device and student, both associated with the registrationToken, passed as the argument.
	 * Calls the stored procedure "handleRegistration"
	 * @param registrationToken The registration token as the argument
	 */
	public void handleRegistrationWithRegistrationToken(String token){
		RegistrationDAO dao = new RegistrationDAO();
		dao.handleRegistration(token);
	}
	
	
}
