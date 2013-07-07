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
package com.oramind.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.oramind.bean.UserBean;

public class UserDAO extends AbstractDAO {
	
	public UserDAO(Connection connection){
		super(connection);
	}
	
	public UserDAO(){
		super();
	}
	
	
	/**
	 * Gets a service user with the specified user name.
	 * @param username
	 * @return a {@link UserBean} or null if a user with the specified name is not found.
	 */
	public UserBean getUserWithUserName(String username){
		String query = "SELECT * FROM serviceusr WHERE username LIKE ?";
		UserBean bean = null;
		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setString(1, username);
			
			ResultSet set = getTrackedResultSet(st, QueryType.select);
			while (set.next()) {
				bean = new UserBean();
				bean.loadFromResultSet(set);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		
		return bean;
	}
	
	/**
	 * Gets a user specified by a name and password.
	 * @param user
	 * @param pass
	 * @return a {@link UserBean} filled with information, or null if no such user was found.
	 */
	public UserBean getUserWithCredentials(String user, String pass){
		String query = "SELECT * FROM serviceusr WHERE " +
				" username LIKE ?" + 
				" AND userpass LIKE ?" + 
				" LIMIT 1;";
		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setString(1, user);
			st.setString(2, pass);
			
			ResultSet result = getTrackedResultSet(st, QueryType.select);

			while (result.next()) {
				UserBean userbean = new UserBean();
				userbean.loadFromResultSet(result);
				return userbean;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			cleanup();
		}
		return null;
	}
	
}
