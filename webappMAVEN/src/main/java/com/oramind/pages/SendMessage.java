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
package com.oramind.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oramind.abs.ViewServletAbstractClass;
import com.oramind.bean.DeviceBean;
import com.oramind.bean.UserBean;
import com.oramind.dao.ServiceDAO;
import com.oramind.db.DBProperties;
import com.oramind.messengers.PushAndroidService;
import com.oramind.messengers.PushiOSService;
import com.oramind.util.Util;

/**
 * Servlet implementation class SendMessage
 */
@WebServlet("/SendMessage")
public class SendMessage extends  ViewServletAbstractClass{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMessage() {
        super();
        
    }

	@Override
	public String getNextPagePath() {
		return "/Panel";
	}

	@Override
	public void preProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Util.println("request character encoding: " + request.getCharacterEncoding());
		boolean shouldSkipPush = (DBProperties.getInstance().getProperty("disablepushmessages").equalsIgnoreCase("true"));
		
		UserBean currentUser = (UserBean) request.getSession().getAttribute("user");
		String [] studentIDs = request.getParameterValues("stID");
		
		String content = request.getParameter("content");
		String utf8Content = content.trim();
		
		int [] studentIDIntegers = Util.toIntArray(studentIDs);
		
		ServiceDAO messageDAO = new ServiceDAO();
		
		if (!shouldSkipPush) {
			List<DeviceBean> devices = messageDAO.getdevicesForStudentIDs(studentIDIntegers, true);
			List<String> iOSDeviceTokens = new ArrayList<String>();
			List<String> googleDeviceTokens = new ArrayList<String>();
			
			/*
			 * Separate iOS from android devices (to decide the certificate with which to sign the message)
			 */
			for (DeviceBean deviceBean : devices) {
				addAPNSIfNotNull(deviceBean, (deviceBean.getPlatform().equalsIgnoreCase("ios")? iOSDeviceTokens : googleDeviceTokens));
			}
			
			Util.println("ios devices: " + iOSDeviceTokens.size());
			Util.println("android devices: " + googleDeviceTokens.size());
			
			Util.println("ios tokens: " + iOSDeviceTokens);
			Util.println("android tokens: " + googleDeviceTokens);
			if (iOSDeviceTokens.size() > 0) {
				try {
					PushiOSService pushiOS = new PushiOSService(iOSDeviceTokens, request.getServletContext());
					pushiOS.sendMessage();	
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
			if (googleDeviceTokens.size() > 0) {
				try {
					PushAndroidService pushAndroid = new PushAndroidService();
					pushAndroid.setDeviceTokens(googleDeviceTokens);
					pushAndroid.setGeneralMessage("you have a new message!");
					pushAndroid.sendMessage();	
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
		}
		
		/*
		 * Regardless what happened to the push messages, we must also send the messages
		 * to the database
		 */
		if (studentIDIntegers.length > 0) {
			messageDAO.sendMessagesToDatabase(utf8Content, studentIDIntegers, currentUser.getUserID());			
		}
	}
	
	/**
	 * Utility function. Inserts an apns token to the list given as the argument, if the token is not null, and
	 * if it is also not empty, or does not contain "null".
	 * @param bean
	 * @param list
	 */
	void addAPNSIfNotNull(DeviceBean bean, List<String> list){
		if (bean.getApnsIdentifier() != null && !Util.mysqlIsNull(bean.getApnsIdentifier())) {
			list.add(bean.getApnsIdentifier());	
		}
	}
	
	@Override
	public void afterProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("result", "result is true");
		response.sendRedirect(request.getContextPath() + getNextPagePath() + "?success=true");
	}

}
