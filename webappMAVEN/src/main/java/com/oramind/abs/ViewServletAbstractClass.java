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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oramind.dao.ServiceDAO;


//@WebServlet("/ViewServletAbstractClass")
/**
 * General servlet inntended for subclass. It serves as the back - end component for any JSP page that needs
 * pre-fetched data to display. It will convert any GET request to a POST request, and will pre-fetch data. When all
 * necessay data is prefetched, it will forward the request to the responsible JSP page, obtainable
 * from {@link ViewServletInterface}{@link #getNextPagePath()} 
 * @author Christos Sotiriou
 *
 */
public abstract class ViewServletAbstractClass extends AbstractBasicServiceServlet  implements ViewServletInterface {
	private static final long serialVersionUID = 1L;	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewServletAbstractClass() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		preProcess(request, response);
		afterProcess(request, response);
	}
	
	/**
	 * Fills the object with data needed to display
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void preProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	
	
	/**
	 * It is called when {@link #preProcess(HttpServletRequest, HttpServletResponse)} is done. Normally, it
	 * will redirect to the JSP file pointed by {@link #getNextPagePath()}. Override to add your own
	 * custom behavior.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void afterProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.getRequestDispatcher(getNextPagePath()).forward(request, response);
	}

	/**
	 * Gets a new {@link ServiceDAO} instance for performing  the necessary preprocess
	 * actions. Repeated calls to this function within the same {@link ViewServletAbstractClass}
	 * instance will result in a new object being returned each time.
	 * @return a {@link ServiceDAO} instance.
	 */
	protected ServiceDAO getServiceDao() {
		return new ServiceDAO();
	}
	
	
	/**
	 * Gets the next page path. This is the page that will be displayed after the pre-process.
	 * 
	 */
	@Override
	public abstract String getNextPagePath();
}
