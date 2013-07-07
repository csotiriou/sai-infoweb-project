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
package com.oramind.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.oramind.db.DBProperties;

/**
 * Utility class. Implements utility functions used throughout the 
 * web application 
 */
public final class Util {

	static boolean loggingEnabled;
	static {
		loggingEnabled = Boolean.valueOf(DBProperties.getInstance().getProperty("loggingEnabled")).booleanValue();
	}

	/**
	 * Returns the last path component from a valid URI
	 * @param url the URI to process 
	 * @return the last path component of a URI
	 **/
	public static String getLastBitFromUrl(final String url){
		return url.replaceFirst(".*/([^/?]+).*", "$1");
	}


	/**
	 * Returns a new date by adding 'minutes'
	 * @param date The date to add minutes to
	 * @param minutes the minutes to add
	 * @return a new {@link Date} object, with 'minutes' added to it.
	 */
	public static Date dateByAddingMinutesToDate(Date date, int minutes){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
	
	/**
	 * Gets the parent URL of the current path
	 * Example An input of "http://example.com/f1/alpha.jsp" will
	 * give as a result "http://example.com/f1/"
	 * @param path the path to find its parent
	 * @return full parent URL of the current path
	 */
	public static String getParentFromURL(final String path){
		if ((path == null) || path.equals("") || path.equals("/")){
			return "";
		}
		int lastSlashPos = path.lastIndexOf('/');
		if (lastSlashPos >= 0){
			return path.substring(0, lastSlashPos); //strip off the slash
		}
		else{
			return ""; //we expect people to add  + "/somedir on their own
		}
	}

	/**
	 * For debugging purposes. Will output the fields of a {@link ResultSet}
	 * @param resultSet
	 * @throws SQLException
	 */
	public static void showResultSetFields(ResultSet resultSet) throws SQLException{
		ResultSetMetaData rsMetaData = resultSet.getMetaData();
	    int numberOfColumns = rsMetaData.getColumnCount();

	    // get the column names; column indexes start from 1
	    for (int i = 1; i < numberOfColumns + 1; i++) {
	      String columnName = rsMetaData.getColumnName(i);
	      // Get the name of the column's table name
	      String tableName = rsMetaData.getTableName(i);
	      Util.println("column name=" + columnName + " table=" + tableName + "");
	    }
	}

	/**
	 * Takes an array of Strings and will convert it to an array of ints
	 * The Strings MUST be string representations of numbers, otherwise the
	 * behavior will be undefined
	 * @param arrayOfIntStrings
	 * @return an array of ints
	 */
	public static int [] toIntArray(final String [] arrayOfIntStrings){
		int []result = new int[arrayOfIntStrings.length];
		for (int i = 0; i < arrayOfIntStrings.length; i++) {
			result[i] = Integer.valueOf(arrayOfIntStrings[i]).intValue();
		}
		return result;
	}

	
	/**
	 * Tests if a string is null by SQL standards. A null SQL string means that in java, it will
	 * either be a null {@link String}, or a String whose content is "null"
	 * @param obj
	 * @return true if a {@link String} is null by SQL standards.
	 */
	public static boolean mysqlIsNull(Object obj){
		if (obj == null) {
			return true;
		}
		if (obj.getClass() == String.class) {
			if ( ((String)obj).contains("null") ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the file extension of a path given. This will be either a URL path, or a path
	 * in a system file. 
	 * @param fileName The file path
	 * @return the file extension of the path given. If no extension is found, it returns an
	 * empty {@link String}
	 */
	public static String fileExtensionFromPath(String fileName){
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	
	
	
	/**
	 * Prints to the command line, depending on whether 'loggingEnabled' in the DB.properties is set to 'true'
	 * @param logString
	 */
	public static void println(String logString){
		if (loggingEnabled) {
			System.out.println(logString);
			Logger.getRootLogger().setLevel(Level.OFF);
			Logger.getRootLogger().debug(logString);
		}
	}
}
