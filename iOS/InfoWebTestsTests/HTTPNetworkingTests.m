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


#import "HTTPNetworkingTests.h"
#import "APIResponseStudent.h"

@implementation HTTPNetworkingTests

- (void)testInstantiation
{
	NSURL *actualURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@:%@/", SERVER_PREFIX, SERVER_PORT]];
	expect(actualURL).to.equal(self.client.baseURL);
	
	expect(self.client.isRegistered).to.beFalsy();
	expect(self.client.apiKey).to.beNil();
	expect(self.client.apnsToken).to.beNil();
}


/**
 *	@brief	tests the registration status for the test device. We expect it to be true
 */
- (void)testCheckRegistrationStatusIsTrue

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	__block __strong NSError *error1;
	__block BOOL result = NO;
	__block __strong APIStatusResponse *responseFromServer = nil;
	
	[self.client getRegistrationStatusForDeviceWithMAC:kTestDeviceMACAddress withCompletionBlock:^(BOOL success, APIStatusResponse *status, NSError *error) {
		error1 = error;
		result = success;
		responseFromServer = status;
	}];
	
	expect(result).will.beTruthy();
	expect(responseFromServer).willNot.beNil();
	expect(responseFromServer.isRegistered).will.beTruthy();
	expect(kTestDeviceMACAddress).will.equal(responseFromServer.macAddress);
	expect(kTestDeviceAPIKey).will.equal(responseFromServer.apiKey);
}

/**
 *	@brief	Test the registration status for a device that does not exist
 */
- (void)testCheckRegistrationStatusIsFalse

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	__block __strong NSError *error1;
	__block BOOL result = NO;
	__block __strong APIStatusResponse *responseFromServer = nil;
	
	[self.client getRegistrationStatusForDeviceWithMAC:@"iDon'tExist" withCompletionBlock:^(BOOL success, APIStatusResponse *status, NSError *error) {
		error1 = error;
		result = success;
		responseFromServer = status;
	}];
	
	expect(result).will.beTruthy();
	expect(responseFromServer).willNot.beNil();
	expect(responseFromServer.isRegistered).will.beFalsy();
	expect(@"iDon'tExist").will.equal(responseFromServer.macAddress);
}


/**
 *	@brief	Tests the getting of messages for the test API key
 */
- (void)testGetMessages
{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	__block __strong NSArray *messagesArray = nil;
	__block __strong NSError *operationError = nil;
	__block BOOL successFlag = NO;
	
	[self.client getMessagesForAPIKey:kTestDeviceAPIKey withStart:0 andNumberOfResults:10 completionBlock:^(BOOL success, NSArray *messagesJSONArray, NSError *error) {
		successFlag = success;
		messagesArray = messagesJSONArray;
		operationError = error;
	}];
	
	expect(successFlag).will.beTruthy();
	expect(operationError).will.beNil();
	expect(messagesArray).will.notTo.beNil();
	expect(messagesArray.count).will.beGreaterThan(0);
	
	
	NSDictionary *dict = [messagesArray objectAtIndex:0];
	CSMessage *message = [self.cache createNewMessage];
	[message loadFromJSONDictionary:dict];
	
	expect(@"testContent").will.equal(message.content);
	expect(@"testRoot").will.equal(message.sender);
}



/**
 *	@brief	tests the Get method of the API key for the MAC address of the test device.
 */
- (void)testGetApiKeyForMac

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	CSJSONHTTPOperation *operation = [self postOperationForPath:[WEB_APPLICATION_PREFIX stringByAppendingPathComponent:kTestsServletPath] andParameters:@{@"req": @"deviceapikey", @"mac" : kTestDeviceMACAddress}];
	
	__block __strong APIResponse *response = nil;
	__block __strong NSError *responseError = nil;
	
	[operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
		SFDebugLog(@"operation response string: %@", operation.responseString);
		NSDictionary *json = ((CSJSONHTTPOperation *)operation).responseJSON;
		SFDebugLog(@"json string: %@",json);
		APIResponse *r = [[APIResponse alloc] init];
		[r loadFromJSONDictionary:json];
		response = r;
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		responseError = error;
	}];
	[operation start];
	
	expect(responseError).beNil();
	expect(response).willNot.beNil();
	expect(@"apiKeyTest").will.equal(response.responseDataDictionary[@"apiKey"]);
}


/**
 *	@brief	Obtains the device information for the test device.
 */
- (void)testGetDeviceInfo

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	__block __strong APIResponse *apiResponse = nil;
	
	CSJSONHTTPOperation *operation = [self postOperationForPath:[WEB_APPLICATION_PREFIX stringByAppendingPathComponent:kTestsServletPath] andParameters:@{@"req": @"getdevice", @"key" : kTestDeviceAPIKey }];
	
	[operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
		APIResponse *generalResponse = [[APIResponse alloc] init];
		[generalResponse loadFromJSONDictionary:((CSJSONHTTPOperation *)operation).responseJSON];
		apiResponse = generalResponse;
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		
	}];
	[operation start];
	
	expect(apiResponse).willNot.beNil();
}


#if SF_TESTS_RUN_STRESS_TESTS == 1
/**
 *	@brief	Creates many user agents while making the requests. The different user agents will force the
 *	server to open a new session for each user.
 */
- (void)testUserAgent

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	__block int completedOperations = 0;
	__block int failedOperations = 0;
	
	for (int i = 0; i<100; i++) {
		CSJSONHTTPOperation *operation = [self postOperationForPath:[WEB_APPLICATION_PREFIX stringByAppendingPathComponent:kTestsServletPath] andParameters:@{@"req": @"sessiontest"} andUserAgent:[NSString stringWithFormat:@"TestAgent%i", i]];
		[operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
			completedOperations++;
		} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
			failedOperations++;
		}];
		[self.operationQueue addOperation:operation];
	}
	expect(completedOperations).will.beGreaterThan(90);
	expect(failedOperations).will.beLessThan(10);
	expect(self.operationQueue.operationCount).will.equal(0);
}


/**
 *	@brief	Obtains the messages without opening a new session.
 *	In localhost this method is expected to have 100% success. However, while performinig
 *	tests over the internet, a failure rate has been observed
 */
- (void)testStressGetMessagesWithoutCreatingSession

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeoutLong];
	
	__block int completedOperations = 0;
	__block int failedOperations = 0;
	
	for (int i = 0; i<10; i++) {
		CSJSONHTTPOperation *operation = [self postOperationForPath:[WEB_APPLICATION_PREFIX stringByAppendingPathComponent:kTestsServletPath] andParameters:@{@"req": @"stressgetmessages", @"key" : kTestDeviceAPIKey, @"createSession" : @"no"} andUserAgent:[NSString stringWithFormat:@"TestAgent%i", i]];
		
		[operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
			CSJSONHTTPOperation *jsonOperation = (CSJSONHTTPOperation *)operation;
			APIResponse *apiResponse = [[APIResponse alloc] init];
			[apiResponse loadFromJSONDictionary:jsonOperation.responseJSON];
			if (apiResponse.responseDataDictionary != nil) {
				completedOperations++;
			}else{
				failedOperations++;
			}
		} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
			failedOperations++;
		}];
		[self.operationQueue addOperation:operation];
	}
	
	expect(completedOperations).will.beGreaterThan(5);
	expect(failedOperations).will.beLessThan(5);
	expect(self.operationQueue.operationCount).will.equal(0);
}


/**
 *	@brief	Tests the previous function, but this time, it will open a new session
 *	for each getter. In localhost this method is expected to have 100% success. However, while performinig
 *	tests over the internet, a failure rate has been observed
 */
- (void)testStressGetMessagesCreateSession

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeoutLong];
	
	__block int completedOperations = 0;
	__block int failedOperations = 0;
	
	for (int i = 0; i<10; i++) {
		CSJSONHTTPOperation *operation = [self postOperationForPath:[WEB_APPLICATION_PREFIX stringByAppendingPathComponent:kTestsServletPath] andParameters:@{@"req": @"stressgetmessages", @"key" : kTestDeviceAPIKey, @"createSession" : @"yes"} andUserAgent:[NSString stringWithFormat:@"TestAgent%i", i]];
		
		[operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
			CSJSONHTTPOperation *jsonOperation = (CSJSONHTTPOperation *)operation;
			APIResponse *apiResponse = [[APIResponse alloc] init];
			[apiResponse loadFromJSONDictionary:jsonOperation.responseJSON];
			if (apiResponse.responseDataDictionary != nil) {
				completedOperations++;
			}else{
				failedOperations++;
			}
		} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
			SFDebugLog(@"error: %@", error.localizedDescription);
			failedOperations++;
		}];
		[self.operationQueue addOperation:operation];
	}
	
	expect(completedOperations).will.beGreaterThan(5);
	expect(failedOperations).will.beLessThan(5);
	expect(self.operationQueue.operationCount).will.equal(0);
}
#endif


/**
 *	@brief	Sends a test message to the database, and verifies the output.
 *	After the message is sent to the server, the servlet responds with all messages
 *	corresponding to the test student, so that the test can verify that the message was
 *	actually inserted
 */
- (void)testSendMessageToDatabase

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	__block BOOL operationSuccess = NO;
	__block BOOL messageWasActuallyInserted = NO;
	
	NSTimeInterval timestamp = [[NSDate date] timeIntervalSince1970];
	/**
	 Construct a unique message content, so that we can identify later (mark it with a timestamp)
	 */
	NSString *message = [NSString stringWithFormat:@"testContent:%f", timestamp];
	
	[self.client postPath:[WEB_APPLICATION_PREFIX stringByAppendingPathComponent:kTestsServletPath] parameters:@{@"req" : @"sendMessage", @"key" : kTestDeviceAPIKey, @"message" : message, @"user" : kTestUserName} success:^(AFHTTPRequestOperation *operation, id responseObject) {
		
		CSJSONHTTPOperation *operationJSON = (CSJSONHTTPOperation *)operation;
		NSDictionary *responseDict = [operationJSON responseJSON];
		SFDebugLog(@"responsedict: %@", responseDict);
		operationSuccess = YES;
		
		APIResponse *apiResponse = [[APIResponse alloc] init];
		[apiResponse loadFromJSONDictionary:responseDict];
		
		for (NSDictionary *messageDict in [apiResponse.responseDataDictionary valueForKey:@"messages"]) {
			if ([[messageDict valueForKey:@"content"] isEqualToString:message]) {
				messageWasActuallyInserted = YES;
			}
		}
		
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		SFDebugLog(@"failure: %@", error.localizedDescription);
		operationSuccess = NO;
	}];
	
	expect(operationSuccess).will.beTruthy();
	expect(messageWasActuallyInserted).will.beTruthy();
	
}

/**
 *	@brief	Gets the test student info and performs a validation on the data returned.
 */
- (void)testGetStudentInfo

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	__block BOOL operationSuccess = NO;
	__block __strong ApiResponseStudentInfo *studentInfoResponse = nil;
	__block __strong NSError *operationError = nil;
	
	[self.client getStudentInfoForAPIKey:kTestDeviceAPIKey completionblock:^(BOOL success, ApiResponseStudentInfo *studentInfo, NSError *error) {
		operationSuccess = success;
		studentInfoResponse = studentInfo;
		operationError = error;
	}];
	
	expect(operationSuccess).will.beTruthy();
	expect(studentInfoResponse).willNot.beNil();
	expect(operationError).to.beNil();
	expect(operationError).will.beNil();
	
	expect(studentInfoResponse.deviceCount).will.equal(1);
	expect(studentInfoResponse.messagesReceived).will.beGreaterThanOrEqualTo(100);
}



/**
 *	@brief	Obtains the test user info and validates the data returned.
 */
- (void)testGetUserInfo

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	__block BOOL operationSuccess = NO;
	__block __strong NSDictionary *userResponse = nil;
	__block __strong NSError *operationError = nil;
	
	CSJSONHTTPOperation *operation = [self postOperationForPath:[WEB_APPLICATION_PREFIX stringByAppendingPathComponent:kTestsServletPath] andParameters:@{@"req": @"getTestUser", @"username" : @"testRoot", @"password" : @"testRoot"}];
	[operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
		operationSuccess = YES;
		NSDictionary *dict = [(CSJSONHTTPOperation *)operation responseJSON];
		userResponse = dict;
		operationError = nil;
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		operationSuccess = NO;
		operationError = error;
	}];
	[operation start];
	
	expect(operationSuccess).will.beTruthy();
	expect(userResponse).willNot.beNil();
	expect(operationError).will.beNil();
	
	expect([userResponse valueForKey:@"code"]).will.equal(@200);
	expect([userResponse valueForKeyPath:@"response.permissionlevel"]).will.equal(@1);
	expect([userResponse valueForKeyPath:@"response.username"]).will.equal(@"testRoot");
}


/**
 *	@brief	Tests the registration. MAKE SURE THAT THE SERVER IS DEPLOYED
 *	WITH 'allowRegistrationWithoutConfirmationEmail' SET TO 'true'!!
 */
- (void)testsNewTestRegistration

{
	[Expecta setAsynchronousTestTimeout:kAsynchronousTimeout];
	
	NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970];
	
	NSString *testEmail = [NSString stringWithFormat:@"test@test%f.com", timeInterval];
	NSString *testMAC = [kTestDeviceMACAddress stringByAppendingFormat:@"%f", timeInterval];
	
	__block BOOL operationSuccess = NO;
	__block __strong NSDictionary *responseDict = nil;
	__block BOOL isRegistered = NO;
	__block __strong APIResponseStudent *studentResponse = nil;
	
	[self.client preRegisterUsingName:@"test" andLastName:@"test" andMail:testEmail andMAC:testMAC completion:^(BOOL success, NSDictionary *responseDictionary, BOOL isAlreadyRegistered, NSError *error) {
		operationSuccess = success;
		responseDict = responseDictionary;
		isRegistered = isAlreadyRegistered;
		studentResponse = [[APIResponseStudent alloc] init];
		[studentResponse loadFromJSONDictionary:responseDictionary];
	}];
	
	expect(operationSuccess).will.beTruthy();
	expect(isRegistered).will.beTruthy(); //make sure you have allowed registrations to be completed without an e-mail confirmation in the server properties...!
	expect(responseDict).willNot.beNil();
	expect(studentResponse).willNot.beNil();
	
	expect(studentResponse.student.email).will.equal(testEmail);
	expect(studentResponse.student.name).will.equal(@"test");
	expect(studentResponse.student.lastName).will.equal(@"test");
	expect(studentResponse.student.studentID.integerValue).will.beGreaterThan(0);
	
	/*
	 the array will contain 1 device (because we are testing in a confined environment), and it will be registered
	 */
	expect(studentResponse.student.deviceArray.count).will.beGreaterThan(0);
	expect(((APIDevice *)[studentResponse.student.deviceArray objectAtIndex:0]).registered).will.beTruthy();
	expect(((APIDevice *)[studentResponse.student.deviceArray objectAtIndex:0]).macAddress).will.equal(testMAC);
	expect(((APIDevice *)[studentResponse.student.deviceArray objectAtIndex:0]).platform).will.equal(@"ios");
}

@end
