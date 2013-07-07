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
package com.oramind.messengers;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.oramind.db.DBProperties;
import com.oramind.util.Util;


/**
 * Concrete subclass of an {@link AbstractMessenger}, aiming at sending push notifications to
 * an iOS device.
 * @author Christos Sotiriou
 *
 */
public class PushiOSService extends AbstractMessenger {
	ServletContext contextPath = null;
	

	/**
	 * Initializer. 
	 * @param tokens A list of device tokens, to send the push notification to.
	 * @param servletContext The servlet context that calls this class. It will be used
	 * to create relative path to this {@link ServletContext}, in order to locate the
	 * .p12 file to sign the notification with.
	 */
	public PushiOSService(List<String> tokens, ServletContext servletContext){
		setDeviceTokens(tokens);
		contextPath = servletContext;
	}

	@Override
	public MessageOperationResult sendMessage() {
		String apnsPath = null;
		if (DBProperties.getInstance().getProperty("useProductionAPNS").equalsIgnoreCase("true")) {
			apnsPath = DBProperties.getInstance().getProperty("apnsProductionPath");
		}else{
			apnsPath = DBProperties.getInstance().getProperty("apnspath");
		}
		
		
		Util.println("getting file from " + apnsPath);
		
		InputStream p12Stream = contextPath.getResourceAsStream(apnsPath);

		ApnsService service = APNS.newService().withCert(p12Stream, "telecom").withSandboxDestination().build();
		String payload = APNS.newPayload().alertBody((getGeneralMessage() == null? "You have a new message! Click here to view it!" : generalMessage)).build();
		service.push(deviceTokens, payload);
		
		return new MessageOperationResult(true);
	}
	
	
}
