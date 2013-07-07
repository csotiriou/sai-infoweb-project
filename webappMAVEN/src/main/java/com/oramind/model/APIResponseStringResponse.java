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
package com.oramind.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Implements a simple {@link APIResponse} where the actual response is only
 * a value associated with a key.
 * @author Christos Sotiriou
 *
 */
public class APIResponseStringResponse extends APIResponse {

	String responseValue = null;
	String responseKey = null;
	
	public APIResponseStringResponse(String key, String value){
		responseValue = value;
		responseKey = key;
	}
	
	
	@Override
	public JsonElement toJSONElement() {
		JsonObject result = new JsonObject();
		result.addProperty(responseKey, responseValue);
		return result;
	}

	
	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}
	public String getResponseValue() {
		return responseValue;
	}
	
	public void setResponseKey(String responseKey) {
		this.responseKey = responseKey;
	}
	
	public String getResponseKey() {
		return responseKey;
	}
}
