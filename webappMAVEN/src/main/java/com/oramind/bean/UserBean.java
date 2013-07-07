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




public class UserBean implements BeanInterface {
	String name = null;
	String sessionID = null;
	
	boolean loggedIn = false;
	
	int userID = 0;
	int userLevel = 0;
	
	
	@Override
	public void loadFromResultSet(ResultSet resultSet) throws SQLException{
		name = resultSet.getString("username");
		userID = resultSet.getInt("userid");
		userLevel = resultSet.getInt("userlevel");
	}
	
	@Override
	public JsonElement toJSONElement() {
		JsonObject result = new JsonObject();
		result.addProperty("username", name);
		result.addProperty("userid", new Integer(userID));
		result.addProperty("permissionlevel", new Integer(userLevel));
		return result;
	}
	
	
	public int getUserID() {
		return userID;
		
	}
	
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	
	public int getUserLevel() {
		return userLevel;
	}
	
	public boolean isLoggedIn(){
		return loggedIn;
	}
	
	public void setLoggedIn(boolean logIn){
		loggedIn = logIn;
	}



	
}
