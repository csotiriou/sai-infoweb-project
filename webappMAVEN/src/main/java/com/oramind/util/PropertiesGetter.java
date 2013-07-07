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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesGetter {
	Properties properties = null;
	

	
	public PropertiesGetter(){
		this.loadPropertiesFromLocalFileLocation("config/rules.properties");
	}
	
	public PropertiesGetter(String fileLocation, boolean isLocalFile) {
		if (isLocalFile) {
			this.loadPropertiesFromLocalFileLocation(fileLocation);
		} else {
			try {
				FileInputStream stream = new FileInputStream(fileLocation);
				this.loadPropertiesFromInputStream(stream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadPropertiesFromInputStream(InputStream stream){
		try {
			this.properties = new Properties();
			this.properties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPropertiesFromLocalFileLocation(String location){
		InputStream inputStream = PropertiesGetter.class.getClassLoader().getResourceAsStream(location);
		this.loadPropertiesFromInputStream(inputStream);
	}
	
	
	
	public String allPropertiesAsString(){
		String result = "";
		
		Enumeration<Object> enumeration = properties.keys();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			result += key + ": " + properties.getProperty(key) + "\n";
		}
		return result;
	}
	
	public int getPropertiesSize(){
		return this.properties.size();
	}
	
	public Enumeration<?> getPropertiesEnumeration(){
		return this.properties.propertyNames();
	}
	
	
	public String getProperty(String propertyName){
		String prop = properties.getProperty(propertyName);
		return prop;
	}
}
