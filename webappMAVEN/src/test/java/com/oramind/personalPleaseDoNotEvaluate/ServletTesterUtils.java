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

import java.io.IOException;
import java.util.Map;

import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

public class ServletTesterUtils {

	public static ServletTester createServletTester() {
		return new ServletTester();
	}
	
	public static void initServlet(ServletTester servlet, String resourceBase, Class servletClass, String servletContext) throws Exception {
		initServlet(servlet, resourceBase, servletClass, servletContext, null);
	}
	
	public static void initServlet(ServletTester servlet, String resourceBase, Class servletClass, String servletContext, Map<String,String> initParams) throws Exception {
    	servlet.setResourceBase(resourceBase);
    	ServletHolder servletHolder = servlet.addServlet(servletClass, servletContext); 
    	if (initParams != null) {
    		for (String param : initParams.keySet()) {
    			servletHolder.setInitParameter(param, initParams.get(param));
    		}
    	}
    	servlet.start();
    }
	
	public static HttpTester makeRequest(ServletTester servlet, String uri) throws IOException, Exception {
		return makeRequest(servlet, uri, null);
	}
	
    public static HttpTester makeRequest(ServletTester servlet, String uri, Map<String,String> headers) throws IOException, Exception {
    	HttpTester request = new HttpTester(); 
    	request.setMethod("GET");
    	request.setURI(uri);
    	request.setVersion("HTTP/1.0");
    	if (headers != null) {
    		for (String header : headers.keySet()) {
    			request.setHeader(header, headers.get(header));
    		}
    	}
    	
    	HttpTester response = new HttpTester();
    	response.parse(servlet.getResponses(request.generate()));
    	
    	return response;
    }

}
