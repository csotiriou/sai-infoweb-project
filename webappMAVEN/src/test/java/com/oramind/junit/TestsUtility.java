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
package com.oramind.junit;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.oramind.util.Util;

/**
 * Tests the {@link Util} class' methods
 * @author Christos Sotiriou
 *
 */
public class TestsUtility {

	@Test
	public void dateByAddingMinutesTests() {
		Date currentDate = new Date();
		Date afterDate = Util.dateByAddingMinutesToDate(currentDate, 60);
		Date beforeDate = Util.dateByAddingMinutesToDate(currentDate, -60);
		
		Assert.assertEquals(60 * 60 * 1000, afterDate.getTime() - currentDate.getTime());
		Assert.assertEquals( - (60 * 60 * 1000), beforeDate.getTime() - currentDate.getTime());
	}
	
	
	@Test
	public void urlLastPathTests(){
		String url1 = "wwww.example.com/test";
		Assert.assertEquals("test", Util.getLastBitFromUrl(url1));
		String url2 = "http://wwww.example.com/test.jsp";
		Assert.assertEquals("test.jsp", Util.getLastBitFromUrl(url2));
	}
	
	@Test
	public void lastPathFromURLTests(){
		String url1 = "www.example.com/test";
		Assert.assertEquals("www.example.com", Util.getParentFromURL(url1));
		String url2 = "www.example.com/test/tesFile.jsp";
		Assert.assertEquals("www.example.com/test", Util.getParentFromURL(url2));
		String url3 = "www.example.com";
		Assert.assertEquals("", Util.getParentFromURL(url3));
	}

}
