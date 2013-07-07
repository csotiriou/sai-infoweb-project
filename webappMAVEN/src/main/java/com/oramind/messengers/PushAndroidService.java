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

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.oramind.db.DBProperties;

/**
 * Concrete implementation of an {@link AbstractMessenger} aiming at sending push notification messages
 * to an Android device.
 * @author Christos Sotiriou
 */
public class PushAndroidService extends AbstractMessenger{


	@Override
	public MessageOperationResult sendMessage() {
		String apiKey = DBProperties.getInstance().getProperty("gcmapikey");
		Sender sender = new Sender(apiKey);
		Message message = new Message.Builder().build();

		MulticastResult result = null;
		MessageOperationResult pushResult = null;
		boolean allOK = true;
		try {
			result = sender.send(message, getDeviceTokens(), 1);

			for (Result currentResult : result.getResults()) {
				if (currentResult.getErrorCodeName() == null) {
				}else{
					allOK = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pushResult = new MessageOperationResult(allOK);
		return pushResult;
	}



}
