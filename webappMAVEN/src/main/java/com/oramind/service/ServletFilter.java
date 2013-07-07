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
package com.oramind.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oramind.bean.UserBean;
import com.oramind.db.DBProperties;
import com.oramind.util.Util;

/**
 * Filter preventing unauthorized access to the service's internals
 * @author Christos Sotiriou
 *
 */
public class ServletFilter implements Filter{

	/**
	 * The pages that concern the API for the mobile device. These pages allow access from external users,
	 * and filter authorization using a method different than looking at the session object (since in these pages
	 * there is no session object created)
	 */
	List<String> apiJavaPages;

	/**
	 * These are resource extension types allowed to be loaded by a web page,
	 * for example "png", "jpeg", etc.
	 */
	List<String> resourceExtensionTypes;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		/*
		 * Set the request to utf 8
		 */
		req.setCharacterEncoding("UTF-8");

		/*
		 * Set the response to UTF-8
		 */
		res.setCharacterEncoding("UTF-8");


		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String requestURI = request.getRequestURI();

		Util.println("request uri: " + requestURI);

		String lastPathComponent = Util.getLastBitFromUrl(requestURI);
		if ( !apiJavaPages.contains(lastPathComponent)) {
			handleWebInterfaceRequest(request, response, chain);
		}else{
			handleAPIRequest(request, response, chain);
		}
	}

	/**
	 * Redirect to the login page.
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	public void handleLoginWebRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
		Util.println("----HANDLING LOGIN WEB RESPONSE----");
		String ipAddress = request.getLocalAddr();
		int port = request.getLocalPort();
		String localPrefix = request.getContextPath();

		String contextPath = request.getContextPath();
		Util.println("context path: " + contextPath);

		//Log the IP address and current timestamp.
		Util.println("IP "+ipAddress + ", Time " + new Date().toString());

		Util.println("request uri: " + request.getRequestURI());
		String newURL = "http://" + ipAddress + ":" + port + localPrefix + "/html/login.jsp";
		Util.println("redirecting to: " + newURL);
		response.sendRedirect(newURL);
	}



	/**
	 * Load the requested jsp page, if the user is authorized. In any other case, redirect to the login page.
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	public void handleWebInterfaceRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{

		UserBean user = (UserBean)request.getSession().getAttribute("user");
		if ( user == null &&  !request.getRequestURI().contains("ogin")) {

			String lastPathComponent = Util.getLastBitFromUrl(request.getRequestURI());
			int dotIndex = lastPathComponent.lastIndexOf(".");
			if (dotIndex != -1) {
				//we have a request for a resource (example: general.css);
				if (resourceExtensionTypes.contains(Util.fileExtensionFromPath(lastPathComponent))) {
					chain.doFilter(request, response);
				}else{
					handleLoginWebRequest(request, response, chain);				}
			}else{
				handleLoginWebRequest(request, response, chain);
			}

		}else{
			chain.doFilter(request, response);
		}
	}

	/**
	 * Allow access to the page.
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	public void handleAPIRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
		chain.doFilter(request, response);	
	}




	@Override
	public void init(FilterConfig arg0) throws ServletException {
		apiJavaPages = new ArrayList<String>();
		resourceExtensionTypes = new ArrayList<String>();

		apiJavaPages.add("Login");
		apiJavaPages.add("Service");
		apiJavaPages.add("Serv");
		apiJavaPages.add("GCMSender");
		apiJavaPages.add("Registration");
		apiJavaPages.add("Auth");
		apiJavaPages.add("ErrorPage");
		apiJavaPages.add("ExceptionCausingPage");
		apiJavaPages.add("StressTests");

		if (DBProperties.getInstance().getProperty("external3rdPartyTestsAllowed").equalsIgnoreCase("true")) {
			apiJavaPages.add("StressTests");
		}

		resourceExtensionTypes.add("css");
		resourceExtensionTypes.add("png");
		resourceExtensionTypes.add("jpeg");
		resourceExtensionTypes.add("jpg");

	}

	@Override
	public void destroy() {
	}

}
