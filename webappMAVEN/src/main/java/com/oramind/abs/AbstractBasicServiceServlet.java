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
package com.oramind.abs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.oramind.dao.ServiceDAO;
import com.oramind.exceptions.ServiceException;
import com.oramind.model.APIResponse;
import com.oramind.pages.ErrorPage;

/**
 * Servlet implementation class AbstractBasicServiceServlet
 */
public abstract class AbstractBasicServiceServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	/**
	 * Constructs a new {@link ServletException} from a general {@link Exception} object, in
	 * order to be thrown higher to the message chain. Commonly used for being handled by the
	 * {@link ErrorPage}
	 * @param e
	 * @return
	 */
	protected ServletException constructExceptionFromException(Exception e){
		return new ServletException(e);
	}
	
	
	/**
	 * Convenience function. Constructs a {@link ServletException} which contains a {@link ServiceException} to be handled by
	 * an {@link ErrorPage}. Calls {@link #constructExceptionFromException(Exception)}.
	 * @param errorInternetCode
	 * @param e the {@link Exception} instance to be contained.
	 * @return a {@link ServletException} containing a {@link ServiceException}.
	 */
	protected ServletException constructServiceExceptionFromNormalException(int errorInternetCode, Exception e){
		return constructExceptionFromException(new ServiceException(errorInternetCode, e.getClass().getName() + "was thrown with message: " + e.getMessage()));
	}
	
	
	/**
	 * Returns a ready-to-use {@link ServiceDAO} object for access to database
	 * and request handling. Each time this method is called,
	 * it returns a new {@link ServiceDAO} object. 
	 * @return a new {@link ServiceDAO} object.
	 */
	protected ServiceDAO getNewServiceDao(){
		return new ServiceDAO();
	}

	/**
	 * Outputs a json string, taken from an {@link APIResponse} subclass
	 * @param resp the {@link HttpServletResponse} object to get the printer.
	 * @param apiResp the {@link APIResponse} object to construct the json from
	 * @throws IOException
	 */
	protected void writeJSON(HttpServletResponse resp, APIResponse apiResp) throws IOException{
		resp.getWriter().write(apiResp.toJson());
	}
	
	/**
	 * Outputs a string to the output printer.
	 * @param resp
	 * @param stringToWrite
	 * @throws IOException
	 */
	protected void writeResponse(HttpServletResponse resp, String stringToWrite) throws IOException{
		resp.getWriter().write(stringToWrite);
	}
}
