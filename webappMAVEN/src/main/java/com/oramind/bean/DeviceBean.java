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
package com.oramind.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oramind.abs.BeanInterface;

public class DeviceBean implements BeanInterface{
	Integer deviceID;
	String apnsIdentifier;
	String macAddress;
	int studentID;
	String platform;
	boolean registered;
	String apiKey;


	public DeviceBean(){
		deviceID = new Integer(0);
		apnsIdentifier = null;
		macAddress = null;
		studentID  = 0;
	}

	public DeviceBean(int id, String name, String apnString, String macAdd, int iD){
		deviceID = id;
		apnsIdentifier = name;
		macAddress = macAdd;
		apnsIdentifier = apnString;
		studentID = iD;
	}


	@Override
	public String toString() {
		return new String("id= " + deviceID +
				", deviceName= " + apnsIdentifier + 
				", MACADDRESS= " + macAddress + 
				", studentID: " + studentID + 
				", platform: " + platform +
				", registered: " + registered);
	}


	public void loadFromResultSet(ResultSet set) throws SQLException{
		deviceID = Integer.valueOf(set.getInt("deviceid"));
		apnsIdentifier = set.getString("identifier");
		macAddress = set.getString("macaddress");
		studentID = set.getInt("studentid");
		platform = set.getString("platform");
		registered = (set.getInt("registered") == 0? false : true);
		apiKey = set.getString("apikey");
	}



	public JsonElement toJSONElement() {
		JsonObject result = new JsonObject();
		result.addProperty("deviceID", deviceID);
		result.addProperty("apns", apnsIdentifier);
		result.addProperty("macAddress", macAddress);
		result.addProperty("studentID", studentID);
		result.addProperty("platform", platform);
		result.addProperty("registered", Boolean.valueOf(registered));
		result.addProperty("apikey", apiKey);
		return result;
	}


	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public int getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}
	public void setStudentID(int studentID) {
		this.studentID = studentID;
	}
	public int getStudentID() {
		return studentID;
	}
	public String getApnsIdentifier() {
		return apnsIdentifier;
	}

	public void setApnsIdentifier(String apnsIdentifier) {
		this.apnsIdentifier = apnsIdentifier;
	}
	
	public boolean isRegistered(){
		return registered;
	}
	
	public void setRegistered(boolean reg){
		registered = reg;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}


	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}

}
