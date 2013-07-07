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
package com.oramind.personalPleaseDoNotEvaluate;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

public class TestsPNS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String apnsPath = DBProperties.getInstance().getProperty("apnspath");
		String apnsPath = "/Users/Christos Sotiriou/Desktop/apnsdev.p12";
		
		System.out.println("getting file from " + apnsPath);
//		InputStream p12Stream = TestsPNS.class.getClassLoader().getResourceAsStream(apnsPath);

//		if (p12Stream != null) {
			ApnsService service = APNS.newService().withCert(apnsPath, "telecom").withSandboxDestination().build();
			String payload = APNS.newPayload().alertBody("You have a new message! Click here to view it!").build();
			service.push("6907ad1fcaf00f9513d907ee4649c5b4b3451efce3f849495c9bbea328a561ba", payload);	
//		}else{
//			Util.println("error opening p12 file");
//		}
		
	}

}
