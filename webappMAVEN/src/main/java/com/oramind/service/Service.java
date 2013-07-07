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
package com.oramind.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.oramind.abs.AbstractBasicServiceServlet;
import com.oramind.bean.MessageBean;
import com.oramind.bean.RegistrationBean;
import com.oramind.bean.StudentBean;
import com.oramind.bean.StudentInfoBean;
import com.oramind.dao.ServiceDAO;
import com.oramind.db.DBProperties;
import com.oramind.exceptions.ServiceException;
import com.oramind.messengers.MailMessenger;
import com.oramind.model.APIResponse;
import com.oramind.model.APIResponseDeviceStatus;
import com.oramind.model.APIResponseGetMessages;
import com.oramind.model.APIResponseMessageDetails;
import com.oramind.model.APIResponseStudentRegistration;
import com.oramind.model.ApiResponseStudentInfoStats;
import com.oramind.util.Util;

/**
 * Servlet implementation class Service. It's the core of the API that will be used by the
 * mobile client.
 */
@WebServlet("/Service")
public class Service extends AbstractBasicServiceServlet {
	private static final long serialVersionUID = 1L;



	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Service() {
		super();
		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		throw constructServiceExceptionFromNormalException(500, new Exception("This API can only be used by POST requests"));
//		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Util.println("context path:" + request.getContextPath());

		if (request.getParameter("req") != null) {
			response.setContentType("text/json");
			String requestKind = request.getParameter("req");
			try {
				if(requestKind.equalsIgnoreCase("registerStudent")){
					handleRegisterStudent(request, response);
				}else if (requestKind.equalsIgnoreCase("getMessages")){
					handleGetMessagesProtected(request, response);
				}else if(requestKind.equalsIgnoreCase("getMessagesAfterDate")){
					handleGetMessagesAfterDate(request, response);
				}else if (requestKind.equalsIgnoreCase("mail")){
					handleSendMail(request, response);
				}else if (requestKind.equalsIgnoreCase("studentInfo")){
					handleGetStudentInfo(request, response);
				}else if (requestKind.equalsIgnoreCase("registerStatus")){
					handleRegisterStatus(request, response);
				}else if (requestKind.equalsIgnoreCase("messageDetails")){
					handleGetMessageDetails(request, response);
				}else if (requestKind.equalsIgnoreCase("test")){
					response.getWriter().write("test");
				}else{
					throw constructExceptionFromException(new ServiceException(500, "Unrecognized command"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				String throwCause = "";
				if (e.getMessage() == null) {
					throwCause = "Wrong arguments given for request";
				}else{
					throwCause = e.getMessage();
				}
				throw constructExceptionFromException(new ServiceException(500, throwCause));
			}

		}else{
			throw constructExceptionFromException(new ServiceException(500, "Unrecognized command"));
		}
	}
	
	/**
	 * Gets the messages related to a student, indicated by a student with a a specific api key, after the date
	 * specified by the argument
	 * Necessary arguments:
	 * <ul>
	 * <li>a device api key</li>
	 * <li>a unix timestamp</li>
	 * </ul>
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void handleGetMessagesAfterDate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String apiKey = request.getParameter("token");
		String timestamp = request.getParameter("timestamp");
		
		Date d = new Date(Long.valueOf(timestamp) * 1000);
		
		List<MessageBean> messages = getNewServiceDao().getMessagesForApiKeyAfterDate(apiKey, d);
		APIResponseGetMessages messagesResponse = new APIResponseGetMessages(messages);
		writeJSON(response, messagesResponse);
	}


	/**
	 * Gets the info for a student, identified by an API key.
	 * Necessary arguments:
	 * <ul><li>key : the API key of the device of a student </li></ul>
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void handleGetStudentInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String apiKey = request.getParameter("key");
		if (apiKey == null) {
			throw constructExceptionFromException(new ServiceException(500, "no api key provided"));
		}else{
			StudentInfoBean bean =  new ServiceDAO().getStudentInfoForAPIKey(apiKey);
			writeResponse(response, new ApiResponseStudentInfoStats(bean).toJson());
		}
	}

	@Deprecated
	protected void handleSendMail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ServiceException{

		try {
			String method = request.getParameter("method");

			MailMessenger messenger = new MailMessenger("hello world", "csotiriou86@gmail.com");
			if (method.equalsIgnoreCase("ssl")) {
				Util.println("sending message using SSL");
				messenger.sendMessageUsingSSL();
			}else if (method.equalsIgnoreCase("tls")){
				messenger.sendMessage();	
			}else{
				messenger.sendOramindMessage();
			}

		} catch (Exception e) {
			throw new ServiceException(500, "mail could not be sent: " + e.getMessage());
		}
	}

	/**
	 * Registers a new student. Parameters that must have been passed are: <br>
	 * <ul>
	 * <li>name : The name of the student</li>
	 * <li>lastname : The last name of the student</li>
	 * <li>mail : A valid student e-mail to send the confirmation to</li>
	 * <li>apns : The google or apple push notification token</li>
	 * <li>mac : The mac address of the device to register</li>
	 * <li>platform : The platform of the device</li>
	 * </ul>
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws ServiceException
	 */
	protected void handleRegisterStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ServiceException{
		String studentName = request.getParameter("name");
		String studentLastName = request.getParameter("lastname");
		String studentMail = request.getParameter("mail");
		String deviceAPN = request.getParameter("apns");
		String deviceMAC = URLDecoder.decode(request.getParameter("mac"), "utf-8");
		String devicePlatform = request.getParameter("platform").toLowerCase();

		RegistrationBean regBean  = null;
		ServiceDAO serviceDao = getNewServiceDao();
		
		regBean = serviceDao.registerNewStudentAndDevice(studentName, studentLastName, studentMail, deviceAPN, deviceMAC, devicePlatform);

		if (regBean != null) {
			Gson gson = new Gson();
			
			Util.println(gson.toJson(regBean));
			
			/*
			 * If the flag is set, register the student immediately 
			 */
			if (DBProperties.getInstance().getProperty("allowRegistrationWithoutConfirmationEmail").equalsIgnoreCase("true")) {
				serviceDao.handleRegistrationWithRegistrationToken(regBean.getRegistrationToken());
			}else{
				/*
				 * Or, if the flag is not set, send an e-mail confirmation.
				 */
				try {
					/*
					 * Construct the HTML message to appear as a clickable link
					 */
					String html = "register! \n" + Util.getParentFromURL(request.getRequestURL().toString()) + "/Registration?token=" + regBean.getRegistrationToken();
					
					MailMessenger mailMEssenger = new MailMessenger(html, studentMail);
					if (DBProperties.getInstance().getProperty("useOramindService").equalsIgnoreCase("true")) {
						Util.println("sending oramind message...");
						mailMEssenger.sendOramindMessage();
					}else{
						Util.println("sending gmail message");
						mailMEssenger.sendMessage();
					}
					Util.println("email sending was successful");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/**
			 * Get the student that was just created
			 */
			StudentBean student = getNewServiceDao().getStudentWithID(regBean.getStudentID());

			if (student != null) {
				/*
				 * If the student was indeed created, print the response.
				 */
				APIResponseStudentRegistration registrationMessage = new APIResponseStudentRegistration(student);	
				writeJSON(response, registrationMessage);
			}else{
				throw constructServiceExceptionFromNormalException(500, new Exception("Student was not eventually created"));
			}	
		}else{
			throw constructServiceExceptionFromNormalException(500, new Exception("Operation could not be completed"));
		}
	}
	
	
	/**
	 * Gets the message details of a message identified with a message ID.
	 * <ul>
	 * <li>token : The token of a device of a student</li>
	 * <li>messageid : message id to get the details for</li>
	 * </ul>
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void handleGetMessageDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String apiKey = request.getParameter("token");
		String messageid = request.getParameter("messageid");
		int messageID = Integer.valueOf(messageid).intValue();

		List<StudentBean> beans = getNewServiceDao().getStudentsAssociatedWithMessage(apiKey, messageID);
		APIResponseMessageDetails messageDetails = new APIResponseMessageDetails(beans);
		response.getWriter().write(messageDetails.toJson());
	}

	/**
	 * prints to the output the register status of the device. <br>
	 * Necessary arguments:
	 * <ul>
	 * <li>mac : The mac address of the device</li>
	 * </ul> 
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void handleRegisterStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String macAddress = request.getParameter("mac");
		String apikey = getNewServiceDao().getApiKeyForMac(macAddress);
		APIResponseDeviceStatus apiResponse = new APIResponseDeviceStatus();
		apiResponse.setRegistered(apikey != null);
		apiResponse.setApiKey(apikey);
		apiResponse.setMacAddress(macAddress);
		response.getWriter().write(apiResponse.toJson());
	}

	/**
	 * Gets the messages associated with a student<br>
	 * Arguments:
	 * <ul>
	 * <li>token : an API token to get messages for</li>
	 * <li>offset: (optional) Start getting messages from this result count</li>
	 * <li>count: (optional) how many results will get returned</li>
	 * </ul>
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void handleGetMessagesProtected(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String apiKey = request.getParameter("token");
		String offset = request.getParameter("offset");
		String count = request.getParameter("count");

		Integer offsetINT = offset != null? Integer.valueOf(offset) : null;
		Integer countINT = count != null? Integer.valueOf(count) : null;
		List<MessageBean> messageList = getNewServiceDao().getMessagesForAPIKey(apiKey, offsetINT , countINT);
		APIResponse apiRes = new APIResponseGetMessages(messageList);
		response.getWriter().write(apiRes.toJson());
	}

}
