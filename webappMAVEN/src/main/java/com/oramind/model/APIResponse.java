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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oramind.abs.APIResponseInterface;
import com.oramind.util.Util;

import config.ErrorCodes;

/**
 * Implements the {@link APIResponseInterface}. Every class extending from {@link APIResponse}
 * is responsible for serializing its own information by overriding the {@link APIResponseInterface#toJSONElement()}
 * function. By calling {@link #toJson()}, the subclass' {@link APIResponseInterface#toJSONElement()} is called, and
 * is constucting a response string, embedding it into the general format of an {@link APIResponse}.
 * 
 * 
 * General sample of a JSON response:
 * 
 * <pre>{
 * 	"error": true in case of error. False, otherwise
 * 	"errorDescription": 'The description of a possible error. Null if there is no error'
 * 	"code": 500,
 *	"response": the actual response (the sublass is responsible for the format)
 *	}</pre>
 * 
 * 
 * @author Christos Sotiriou
 *
 */
public abstract class APIResponse implements APIResponseInterface{
	
	/**
	 * Indicates if the response contains an error message
	 */
	boolean error = false;
	
	/**
	 * If {@link #error} is true, then this value contains a human-readable
	 * description of the error cause
	 */
	String errorDescription = null;
	
	/**
	 * Each request has a response with a response code. The response code is always
	 * visible. A value of 200, indicates that no error has happened. Apart from the
	 * possible internet error codes that can be returned, there are also some API
	 * specific codes that can be returned, viewable in {@link ErrorCodes} class 
	 */
	int responseCode = 200;


	private Gson getGSONObject(){
		Gson gson = new GsonBuilder()
		.serializeNulls()
		.setPrettyPrinting()
		.create();
		return gson;
	}
	
	
	/**
	 * Serializes the object, wrapping up the data contained in the specific subclass of
	 * an {@link APIResponse}, to a {@link JsonElement}, ready to be converted to
	 * a JSON string. Calls {@link #toJSONElement()} to form the extra data for inclusion, 
	 * accessible by the "response" tag of the returned response JSON string
	 * @return
	 */
	private JsonElement serialize() {
		Util.println("calling serialize to " + getClass().getName().toString());
		JsonObject obj = new JsonObject();
        obj.addProperty("error", this.error);
        obj.addProperty("errorDescription", errorDescription);
        obj.addProperty("code", new Integer(responseCode));
        obj.add("response", toJSONElement());
        return obj;
	}
	
	
	@Override
	public String toJson() {
		Gson gson= getGSONObject();
		return gson.toJson(this.serialize());
	}
	
	
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getErrorDescription() {
		return errorDescription;
	}

	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	
	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	@Override
	public String toString() {
		return toJson();
	}
}
