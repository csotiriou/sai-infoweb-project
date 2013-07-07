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

public class StudentInfoBean implements BeanInterface {

	int deviceCount;
	int messageCount;
	StudentBean studentBean;
	
	
	@Override
	public void loadFromResultSet(ResultSet set) throws SQLException {
		this.deviceCount = set.getInt("deviceCount");
		this.messageCount = set.getInt("messageCount");
		
	}
	
	/**
	 * Will set up a new {@link StudentBean} and will load its contents
	 * from a {@link ResultSet}
	 * @param set
	 * @throws SQLException
	 */
	public void loadStudentBeanFromResultSet(ResultSet set) throws SQLException{
		this.studentBean = new StudentBean();
		studentBean.loadFromResultSet(set);
	}

	@Override
	public JsonElement toJSONElement() {
		JsonElement studentobject = studentBean.toJSONElement();
		JsonObject statsObject = new JsonObject();
		statsObject.add("student", studentobject);
		statsObject.addProperty("deviceCount", Integer.valueOf(deviceCount));
		statsObject.addProperty("messageCount", Integer.valueOf(messageCount));
		return statsObject;
	}
	
	public int getDeviceCount() {
		return deviceCount;
	}
	
	public void setDeviceCount(int deviceCount) {
		this.deviceCount = deviceCount;
	}
	
	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	public void setStudentBean(StudentBean studentBean) {
		this.studentBean = studentBean;
	}
	public StudentBean getStudentBean() {
		return studentBean;
	}

}
