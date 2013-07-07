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
import java.util.ArrayList;
import java.util.List;

import com.oramind.bean.DeviceBean;
import com.oramind.bean.StudentBean;

public class DeviceDAO extends AbstractDAO {

	
	public DeviceDAO(){
		super();
	}
	
	public DeviceDAO(Connection conn){
		super(conn);
	}

	/**
	 * Gets the {@link List} of {@link DeviceBean} associated with a student.
	 * @param studentID The student's ID
	 * @return a {@link List} of {@link DeviceBean} associated with this {@link StudentBean}
	 */
	public List<DeviceBean> getDevicesForStudentID(int studentID){
		String query = "SELECT * FROM device WHERE studentid=? ;";
		List<DeviceBean> result = new ArrayList<DeviceBean>();
		try {
			PreparedStatement st = getTrackedPreparedStatement(query, false);
			st.setInt(1, studentID);

			ResultSet resultSet = getTrackedResultSet(st, QueryType.select);
			if (resultSet != null) {
				while (resultSet.next()){
					DeviceBean device = new DeviceBean();
					device.loadFromResultSet(resultSet);
					result.add(device);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();	
		}
		return result;
	}
	

	/**
	 * Returns a list of {@link DeviceBean} associated with a student ID;
	 * @param studentIDs
	 * @param filterUnregistered
	 * @return
	 */
	public List <DeviceBean> getdevicesForStudentIDs(int studentIDs[], boolean filterUnregistered){
		String inValues = "(";
		for (int i : studentIDs) {
			inValues += i + ",";
		}
		inValues = inValues.substring(0, inValues.length() - 1);
		inValues += ")";

		String query = "SELECT * FROM device WHERE studentid IN " + inValues +  (filterUnregistered? " AND registered='1' ": " ") + " ORDER BY studentid";
		ResultSet result = null;


		List<DeviceBean> list = new ArrayList<DeviceBean>();
		try {
			PreparedStatement st = getTrackedPreparedStatement(query, false);
			result = getTrackedResultSet(st, QueryType.select);
			while (result.next()) {
				DeviceBean newDevice = new DeviceBean();
				newDevice.loadFromResultSet(result);
				list.add(newDevice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();	
		}
		return list;
	}
	
	
	/**
	 * Returns device info based on an API key
	 * @param apiKey
	 * @return a {@link DeviceBean}
	 */
	public DeviceBean getDeviceBeanForAPIKey(String apiKey){
		String query = "SELECT * FROM device WHERE apikey like ?;";
		
		DeviceBean bean = null;
		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setString(1, apiKey);
			
			ResultSet set = getTrackedResultSet(st, QueryType.select);
			
			while (set.next()) {
				bean = new DeviceBean();
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
	 * Gets a {@link DeviceBean} associated with a device ID;
	 * @param deviceID
	 * @return a {@link DeviceBean}, or null is nothing was found
	 */
	@Deprecated
	public DeviceBean getDeviceWithID(int deviceID){
		String query = "SELECT * FROM device WHERE deviceid=?";
		ResultSet result = null;
		DeviceBean newDevice = null;
		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setInt(1, deviceID);
			result = getTrackedResultSet(st, QueryType.select);
			while (result.next()) {
				newDevice = new DeviceBean();
				newDevice.loadFromResultSet(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cleanup();	
		}
		return newDevice;
	}
	
	
	/**
	 * Checks wether the device MAC address is associated with another mail. In this case, we
	 * need to handle that.
	 * @param MACAddress
	 * @param currentMail
	 * @return
	 */
	@Deprecated
	public boolean deviceMACisAlreadyAssociated(String MACAddress, String currentMail){
		String query = "select (exists (select d.studentid from device d,students s" + 
				" where d.studentid = s.studentid" +
				" and s.email not like ?" +
				" and d.macaddress like ?" + 
				") ); ";
		boolean exists = false;
		try {
			PreparedStatement statement = getTrackedPreparedStatement(query);
			statement.setString(1, currentMail);
			statement.setString(2, MACAddress);
			ResultSet result = getTrackedResultSet(statement, QueryType.select);
			while(result.next()){
				exists = result.getBoolean(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}
	
	
	/**
	 * Returns the api key associated with a device, identified by that device's
	 * mac address.
	 * @param macAddress
	 * @return
	 */
	public String getApiKeyForMac(String macAddress){
		String query = "SELECT apikey FROM device WHERE macaddress like ? AND registered=1 limit 1;";
		String macAddressResult = null;
		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setString(1, macAddress);
			ResultSet set = getTrackedResultSet(st, QueryType.select);
			while (set.next()) {
				macAddressResult = set.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		return macAddressResult;
	}

	
	
	/**
	 * Deletes the devices that a student with the specified student ID has.
	 * @param studentID
	 */
	public void deleteDevicesAssociatedWithStudent(int studentID){
		String deviceDeletionQuery = "DELETE FROM device WHERE studentid = ?";
		try {
			PreparedStatement st = getTrackedPreparedStatement(deviceDeletionQuery);
			st.setInt(1, studentID);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}
	
	
	/**
	 * Deletes a device based on its ID int he database.
	 * @param deviceID the deviceid of the device to be deleted.
	 */
	public void deleteDevice(int deviceID){
		String query = "DELETE FROM device WHERE deviceid = ?";
		try {
			PreparedStatement st = getTrackedPreparedStatement(query);
			st.setInt(1, deviceID);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cleanup();
		}
		
	}
}
