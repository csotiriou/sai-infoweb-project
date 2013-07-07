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

#import "InfoWebBase.h"
#import "Expecta.h"
#import "OraHTTPClient.h"
#import "Definitions.h"

@interface InfoWebBase ()
@end

@implementation InfoWebBase

- (void)setUp
{
	SFDebugLog(@"setting up tests...");
	self.client = [[OraHTTPClient alloc] init];
	self.cache = [[CSCache alloc] init];
	
	self.operationQueue = [[NSOperationQueue alloc] init];
	self.operationQueue.maxConcurrentOperationCount = 10;
	
}

- (void)tearDown
{
	[self.client cancelAllHTTPOperationsWithMethod:@"POST" path:nil];
	[self.client cancelAllHTTPOperationsWithMethod:@"GET" path:nil];
	self.client = nil;
	[self.cache rollBack];
	[self.cache deleteAllMessages];
	self.cache = nil;
	[self.operationQueue cancelAllOperations];
	self.operationQueue = nil;
}

- (NSMutableURLRequest *)requestForPath:(NSString *)path andParameters:(NSDictionary *)parameters andUserAgent:(NSString *)userAgent
{
	NSMutableURLRequest *request = [self.client requestWithMethod:@"POST" path:path parameters:parameters];
	if (userAgent != nil) {
		[request setValue:userAgent forHTTPHeaderField:@"User-Agent"];
	}
	return request;
}

- (CSJSONHTTPOperation *)postOperationForPath:(NSString *)path andParameters:(NSDictionary *)dict andUserAgent:(NSString *)userAgent
{
	NSMutableURLRequest *request = [self requestForPath:path andParameters:dict andUserAgent:userAgent];
	return [[CSJSONHTTPOperation alloc] initWithRequest:request];
}


- (CSJSONHTTPOperation *)postOperationForPath:(NSString *)path andParameters:(NSDictionary *)dict
{
	NSURLRequest *request = [self requestForPath:path andParameters:dict andUserAgent:nil];
	CSJSONHTTPOperation *result = [[CSJSONHTTPOperation alloc] initWithRequest:request];
	return result;
}


@end
