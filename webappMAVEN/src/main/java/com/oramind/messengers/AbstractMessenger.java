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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMessenger implements MessagingInterface{
	
	
	List<String> deviceTokens = new ArrayList<String>();
	
	
	String actualMessage = null;
	
	/**
	 * Message that will be displayed when the application is not active
	 */
	String generalMessage = null;
	
	
	
//	public String getActualMessage() {
//		return actualMessage;
//	}
//	
//	public void setActualMessage(String actualMessage) {
//		this.actualMessage = actualMessage;
//	}
	
	public List<String> getDeviceTokens() {
		return deviceTokens;
	}
	
	public void setDeviceTokens(List<String> deviceTokens) {
		this.deviceTokens = deviceTokens;
	}
	
	public String getGeneralMessage() {
		return generalMessage;
	}
	public void setGeneralMessage(String generalMessage) {
		this.generalMessage = generalMessage;
	}

	@Override
	public abstract  MessageOperationResult sendMessage() throws Exception;
}
