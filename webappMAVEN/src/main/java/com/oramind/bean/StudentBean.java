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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oramind.abs.BeanInterface;

public class StudentBean implements HasInvariant, BeanInterface{
	String name;
	String lastName;
	String email;
	Integer studentID;
	List<DeviceBean> devices = new ArrayList<DeviceBean>();
	
	String registrationKey;
	boolean registered;
	
	

	public JsonElement toJSONElement() {
		JsonObject result = new JsonObject();
		result.addProperty("name", name);
		result.addProperty("lastName", lastName);
		result.addProperty("email", email);
		result.addProperty("studentID", studentID);
		result.addProperty("registered", Boolean.valueOf(registered));

		JsonArray deviceArray = new JsonArray();
		for (DeviceBean dev : devices) {
			deviceArray.add(dev.toJSONElement());
		}
		result.add("devices", deviceArray);

		return result;
	}
	
	
	public void loadFromResultSet(ResultSet set) throws SQLException{
		setName(set.getString("name"));
		setEmail(set.getString("email"));
		setLastName(set.getString("lastname"));
		setStudentID(set.getInt("studentid"));
		setRegistered(set.getBoolean("registered"));
		setRegistrationKey(set.getString("registrationKey"));
	}
	
	public List<String> getDeviceTokens(){
		List<String> result = new ArrayList<String>();
		for (DeviceBean device : devices) {
			result.add(device.getApnsIdentifier());
		}
		return result;
	}
	

	public boolean hasInvariant() {
		return studentID != null && email != null;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setStudentID(Integer studentID) {
		this.studentID = studentID;
	}
	
	public void setStudentID(int studentID) {
		this.studentID = Integer.valueOf(studentID);
	}
	
	public List<DeviceBean> getDevices() {
		return devices;
	}
	public void setDevices(List<DeviceBean> devices) {
		this.devices = devices;
	}
	
	public int  getStudentID() {
		return studentID.intValue();
	}
	
	public String getRegistrationKey() {
		return registrationKey;
	}


	public void setRegistrationKey(String registrationKey) {
		this.registrationKey = registrationKey;
	}


	public boolean isRegistered() {
		return registered;
	}


	public void setRegistered(boolean registered) {
		this.registered = registered;
	}


	@Override
	public String toString() {
		return this.lastName + " " + this.name;
	}
}
