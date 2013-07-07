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

import com.oramind.bean.StudentBean;
import com.oramind.bean.StudentInfoBean;

public class StudentInfoDAO extends AbstractDAO {

	
	/**
	 * Gets information about a student.
	 * @param apiKey
	 * @return
	 */
	public StudentInfoBean getStudentInfoForApiKey(String apiKey){
		Connection con = getConnection();
		
		String query = "select " +
				"(select count(*) from device where studentid = (select studentid from device where apiKey like ?)) as deviceCount," +
				" (select count(*) from messageassociations where studentid = (select studentid from device where apikey like ?)) as messageCount;";
		
		StudentInfoBean result = null;
		
		/*
		 * Create a new student dao, by using the same connection, to effectively
		 * use the same connection without setting a new one.
		 */
		StudentDAO dao = new StudentDAO(con);
		StudentBean studentbean = dao.getStudentWithAPIKey(apiKey);
		
		
		if (studentbean != null) {
			try {
				PreparedStatement st = getTrackedPreparedStatement(query, con, false);
				st.setString(1, apiKey);
				st.setString(2, apiKey);
				ResultSet resultSet = getTrackedResultSet(st, QueryType.select);
				while (resultSet.next()) {
					result = new StudentInfoBean();
					result.setStudentBean(studentbean);
					result.loadFromResultSet(resultSet);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				cleanup();
			}
		}
		return result;
	}
}
