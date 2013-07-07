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
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oramind.abs.AbstractBasicServiceServlet;
import com.oramind.bean.DeviceBean;
import com.oramind.bean.MessageBean;
import com.oramind.bean.StudentBean;
import com.oramind.bean.UserBean;
import com.oramind.dao.DeviceDAO;
import com.oramind.dao.MessageDAO;
import com.oramind.dao.SetupDAO;
import com.oramind.dao.StudentDAO;
import com.oramind.dao.UserDAO;
import com.oramind.exceptions.ServiceException;
import com.oramind.model.APIResponse;
import com.oramind.model.APIResponseGetMessages;
import com.oramind.model.APIResponseStringResponse;
import com.oramind.model.APIResponseUserInfo;
import com.oramind.util.Util;

/**
 * Class for performing tests for testing the mechanics of the service
 * Before calling any other function of this servlet, you must call it with 
 * req=setup , and after you are done with the unit tests, you must call it
 * with req=teardown to destroy any data created during the testing process..
 * 
 * @author Christos Sotiriou
 *
 */
@WebServlet("/StressTests")
public class StressTests extends AbstractBasicServiceServlet {

	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String argument = req.getParameter("req");
		if (argument == null) {
			throw constructExceptionFromException((new ServiceException(500, "no \"req\" argument given")));
		}
		try {
			if (argument.equalsIgnoreCase("setup")) {
				handleSetup(req, resp);
			}else if (argument.equalsIgnoreCase("teardown")){
				handleTearDown(req, resp);
			}else if(argument.equalsIgnoreCase("getTestUser")){ 
				handleGetTestUser(req, resp);
			}else if (argument.equalsIgnoreCase("deviceapikey")) {
				String deviceMAC = req.getParameter("mac");
				String deviceKey = new DeviceDAO().getApiKeyForMac(deviceMAC);
				writeJSON(resp, new APIResponseStringResponse("apiKey", deviceKey));
			}else if(argument.equalsIgnoreCase("getdevice")){
				handleGetDevice(req, resp);
			}else if(argument.equalsIgnoreCase("stressgetmessages")){
				handleStressGetMessages(req, resp);
			}else if(argument.equalsIgnoreCase("sessiontest")){
				handleSessionTests(req, resp);
			}else if(argument.equalsIgnoreCase("sendMessage")){
				handleSendMessageToTestUser(req, resp);
			}else{
				throw constructExceptionFromException(new ServiceException(500, "'req' argumment invalid"));
			}			
		} catch (Exception e) {
			e.printStackTrace();
			throw constructServiceExceptionFromNormalException(500, e);
		}
	}
	
	
	protected void handleGetTestUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String userName = req.getParameter("username");
		String password = req.getParameter("password");
		UserBean bean = new UserDAO().getUserWithCredentials(userName, password);
		writeResponse(resp, new APIResponseUserInfo(bean).toJson());
	}
	
	protected void handleStressGetMessages(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		Util.println("stress test messages being called.");
		
		String apiKey = req.getParameter("key");
		String createSession = req.getParameter("createSession");
		if (createSession.equalsIgnoreCase("yes")) {
			req.getSession(true);
		}
		List<MessageBean> testMessages = new MessageDAO().getMessagesForAPIKey(apiKey, null, null);
		APIResponseGetMessages messagesResponse = new APIResponseGetMessages(testMessages);
		writeJSON(resp, messagesResponse);
	}
	
	/**
	 * Sends a message to the database according to the arguments, and then gets all messages for the student
	 * indicated by the API key and writes a response based on that. To be used only for validation purposes.
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	protected void handleSendMessageToTestUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException{
		String apiKey = req.getParameter("key");
		String message = req.getParameter("message");
		String username = req.getParameter("user");
		
		UserBean serviceUser = new UserDAO().getUserWithUserName(username);
		StudentBean student = new StudentDAO().getStudentWithAPIKey(apiKey);
		MessageDAO dao = new MessageDAO();
		dao.setupForReusingConnections();
		
		dao.sendMessagesToDatabase(message, new int[]{student.getStudentID()}, serviceUser.getUserID());
		List<MessageBean> messages = dao.getMessagesForAPIKey(apiKey, null, null);
		dao.cleanupReusableResources();
		APIResponseGetMessages response = new APIResponseGetMessages(messages);
		writeJSON(resp, response);
	}
	
	protected void handleSessionTests(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		req.getSession(true);
		Util.println("creating session for user agent: " + req.getHeader("User-Agent"));
	}
	
	protected void handleGetDevice(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		String apiKey = req.getParameter("key");
		final DeviceBean bean = new DeviceDAO().getDeviceBeanForAPIKey(apiKey);
		
		APIResponse response = new APIResponse() {
			
			@Override
			public JsonElement toJSONElement() {
				JsonObject result = new JsonObject();
				result.add("device", bean.toJSONElement());
				return result;
			}
		};
		writeJSON(resp, response);
	}


	protected void handleTearDown(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		SetupDAO dao = new SetupDAO();
		try {
			dao.databaseTestTearDown();
		} catch (Exception e) {
			e.printStackTrace();
			throw constructExceptionFromException(e);
		}
	}

	protected void handleSetup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		SetupDAO dao = new SetupDAO();
		try {
			dao.databaseSetup();
		} catch (Exception e) {
			e.printStackTrace();
			throw constructExceptionFromException(e);
		}
	}
}
