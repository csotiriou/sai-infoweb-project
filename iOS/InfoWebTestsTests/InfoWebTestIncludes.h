/*******************************************************************************
 Copyright (c) 2013, Christos Sotiriou
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modification, are permitted
 provided that the following conditions are met:
 
 -- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 -- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

#define EXP_SHORTHAND
#import <Foundation/Foundation.h>
#import "Expecta.h"

/**
 *	@brief	The api key of the device to be tested. Must match the one in the setup servlet
 */
#define kTestDeviceAPIKey		@"apiKeyTest"

/**
 *	@brief	The device test MAC address to be tested
 */
#define kTestDeviceMACAddress	@"testaddress"

/**
 *	@brief	The servlet name which holds the tests
 */
#define kTestsServletPath		@"StressTests"


/**
 @brief the user name of the test service user
 */
#define kTestUserName			@"testRoot"

/**
 *	@brief	Disables the stress tests on the server. Disable this if you want to test for
 *	functionality and not multithreading capabilities. NOTE sometimes enabling this causes the registration to fail (too many
 *	threads and sessions?)
 */
#define SF_TESTS_RUN_STRESS_TESTS 0


/**
 *	@brief	Asynchronous timeout for the tests
 */
#define kAsynchronousTimeout		5.0

/**
 *	@brief	Asynchronous timeout for tests that last a little longer.
 */
#define kAsynchronousTimeoutLong	10.0
