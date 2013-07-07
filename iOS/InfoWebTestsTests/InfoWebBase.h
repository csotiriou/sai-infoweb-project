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

#import <SenTestingKit/SenTestingKit.h>
#import "InfoWebTestIncludes.h"
#import "OraHTTPClient.h"
#import "Definitions.h"
#import <OCMock/OCMock.h>
#import "CSMessage.h"
#import "CSCache.h"
#import "CSJSONHTTPOperation.h"


/**
 *	@brief	Class which implements the besic set up and tear down methods, and also provides convenience methods
 *	for testing all aspects of the serrvice
 */
@interface InfoWebBase : SenTestCase


/**
 *	@brief	The OraHTTPClient instance to perform tests with
 */
@property (nonatomic, strong) OraHTTPClient *client;

/**
 *	@brief	The cache instance to perform tests with. After each tests, its contents get purged
 */
@property (nonatomic, strong) CSCache *cache;

/**
 *	@brief	An operation queue to make tests. Useful for making stress tests for concurrency
 */
@property (nonatomic, strong) NSOperationQueue *operationQueue;



/**
 *	@brief	Convenience function. Asks the local http client to create a new JSON operation with a relative path, and a dictionary of parameters
 *
 *	@param 	path 	The relative path to make the request
 *	@param 	dict 	a dictionary of parameters
 *
 *	@return	a CSJSONHTTPOperation instance.
 */
- (CSJSONHTTPOperation *)postOperationForPath:(NSString *)path andParameters:(NSDictionary *)dict;

/**
 *	@brief	Convenience function. Asks the local http client to create a new JSON operation with a relative path, and a dictionary of parameters
 *
 *	@param 	path 	The relative path to make the request
 *	@param 	parameters 	a dictionary of parameters
 *	@param 	userAgent 	An optional user agent. If nil, it will use the default one, provided by the HTTP client
 *
 *	@return	an NSMutableURLRequest to be supplied into an asynchronous operation
 */
- (NSMutableURLRequest *)requestForPath:(NSString *)path andParameters:(NSDictionary *)parameters andUserAgent:(NSString *)userAgent;


/**
 *	@brief	Convenience function. Asks the local http client to create a new JSON operation with a relative path, and a dictionary of parameters
 * and an optional user agent to make the request.
 *
 *	@param 	path 	The relative path to make the request
 *	@param 	dict 	a dictionary of parameters
 *	@param 	userAgent 	An optional user agent. If nil, it will use the default one, provided by the HTTP client
 *
 *	@return	a CSJSONHTTPOperation instance.
 */
- (CSJSONHTTPOperation *)postOperationForPath:(NSString *)path andParameters:(NSDictionary *)dict andUserAgent:(NSString *)userAgent;


@end
