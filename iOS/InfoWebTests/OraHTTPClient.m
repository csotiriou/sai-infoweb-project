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


#import "OraHTTPClient.h"
#import "SFGlobals.h"
#import "SFUtils.h"
#import "GCDMulticastDelegate.h"
#import "CSJSONHTTPOperation.h"
#import "APIResponseStudent.h"
#import "InfoWebTestIncludes.h"

@interface OraHTTPClient ()

/**
 *	@brief	Multicast Delegate. See documentation.
 */
@property (nonatomic, strong) GCDMulticastDelegate<HTTPClientDelegate> * delegate;

@property (nonatomic, readonly) NSDictionary *currentUserDictionary;
@end

@implementation OraHTTPClient
@dynamic apnsRegistrationDone;


+ (OraHTTPClient *)sharedHTTPClient
{
    static dispatch_once_t onceQueue;
    static OraHTTPClient *oraHTTPClient = nil;
	
    dispatch_once(&onceQueue, ^{ oraHTTPClient = [[self alloc] init]; });
    return oraHTTPClient;
}

- (id)init
{
    self = [super initWithBaseURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@:%@/", SERVER_PREFIX, SERVER_PORT]]];
    if (self) {
		
		self.delegate = (GCDMulticastDelegate<HTTPClientDelegate> *)[[GCDMulticastDelegate alloc] init];
		[self registerHTTPOperationClass:[CSJSONHTTPOperation class]];
		self.isRegistered = NO;
		self.apiKey = nil;
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveRemoteNotification:) name:SF_NOTIFICATIONS_DID_RECEIVE_NOTIFICATION object:nil];
    }
    return self;
}


- (NSDictionary *)currentUserDictionary
{
	return [[NSUserDefaults standardUserDefaults] valueForKey:SF_PREFS_STUDENT_DICT];
}


- (void)preRegisterUsingName:(NSString *)name andLastName:(NSString *)lastname andMail:(NSString *)mail completion:(void (^)(BOOL success, NSDictionary *responseDictionary, BOOL isAlreadyRegistered, NSError *error))block
{
	[self preRegisterUsingName:name andLastName:lastname andMail:mail andMAC:[SFUtils urlEncodedString:[SFGlobals sharedSFGlobals].deviceMACaddress] completion:block];
}

- (void)preRegisterUsingName:(NSString *)name andLastName:(NSString *)lastname andMail:(NSString *)mail andMAC:(NSString *)macAddress completion:(void (^)(BOOL success, NSDictionary *responseDictionary, BOOL isAlreadyRegistered, NSError *error))block
{
	__block __strong NSString *macAddressGiven = macAddress;
	
	[self postPath:SF_COMMAND_GET_SERVICE_BASE parameters:@{@"req": @"registerStudent" , @"name": name, @"lastname" : lastname, @"mail" : mail, @"mac" : macAddress, @"apns" : (self.apnsToken == nil? @"" : self.apnsToken), @"platform" : @"iOS"} success:^(AFHTTPRequestOperation *operation, id responseObject) {
		NSLog(@"sucess: %@", operation.responseString);
		
		NSDictionary *response = [(CSJSONHTTPOperation *)operation responseJSON];
		if ([[response valueForKey:@"error"] boolValue] == YES) {
			NSError *error = [NSError errorWithDomain:NSInvalidArgumentException code:302 userInfo:@{@"errorString": [ response valueForKey:@"errorDescription"]}];
			block(NO, nil, NO, error);
		}else{
			SFDebugLog(@"response: %@", operation.responseString);
			/*
			 The response comes in the form of an APIResponseStudent. We examine the data by initializing such an object,
			 and we make the necessary changes
			 */
			APIResponseStudent *studentResponse = [[APIResponseStudent alloc] init];
			[studentResponse loadFromJSONDictionary:response];
			
			if (studentResponse.student.isRegistered && studentResponse.student.deviceArray.count > 0) {
				BOOL currentDeviceIsRegistered = NO;
				for (APIDevice *device in studentResponse.student.deviceArray) {
					if (device.registered && [device.macAddress isEqualToString:macAddressGiven]) {
						currentDeviceIsRegistered = YES;
						SFDebugLog(@"registered device... api key = %@", device.apiKey);
					}
				}
				block(YES, response, studentResponse.student.isRegistered, nil);
				
				[self.delegate httpClient:self didPreregisterWithStudentObject:studentResponse.student];
			}else{
				block(YES, response, studentResponse.student.isRegistered, nil);
			}
		}
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		NSLog(@"failure: %@", error.localizedDescription);
		block(NO, nil, NO, error);
	}];
	
}


- (void)getMessagesForCurrentAPIKeyWithStart:(int)offset andNumberOfResults:(int)resultNum completionBlock:(void (^)(BOOL success, NSArray *messagesJSONArray, NSError *error))completionBlock
{
	[self getMessagesForAPIKey:self.apiKey withStart:offset andNumberOfResults:resultNum completionBlock:completionBlock];
}


- (void)getMessagesForAPIKey:(NSString *)apiKey withStart:(int)offset andNumberOfResults:(int)resultNum completionBlock:(void (^)(BOOL success, NSArray *messagesJSONArray, NSError *error))completionBlock
{
	NSString *offsetString = [NSString stringWithFormat:@"%i", offset];
	NSString *countString = [NSString stringWithFormat:@"%i", resultNum];
	[self postPath:SF_COMMAND_GET_SERVICE_BASE parameters:@{@"req": @"getMessages", @"token" : apiKey, @"offset": offsetString, @"count" : countString} success:^(AFHTTPRequestOperation *operation, id responseObject) {
		NSDictionary *responseDictionary = [(AFJSONRequestOperation*)operation responseJSON];
		if ([[responseDictionary valueForKey:@"error"] boolValue] == YES) {
			NSString *errorDescription = [responseDictionary valueForKey:@"errorDescription"];
			NSError *error = [[NSError alloc] initWithDomain:SF_ERROR_DOMAIN code:410 userInfo:@{@"errorDescription": errorDescription}];
			completionBlock(NO, nil, error);
		}else{
			NSArray *messages = [responseDictionary valueForKeyPath:@"response.messages"];
			completionBlock(YES, messages, nil);
		}
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		completionBlock(NO, nil, error);
	}];
}

- (void)getMessagesForApiKey:(NSString *)apiKey afterTimeStamp:(NSTimeInterval)timestamp completionBlock:(void(^)(BOOL success, NSArray *messagesJSONArray, NSError *error))completionBlock
{
	[self postPath:SF_COMMAND_GET_SERVICE_BASE parameters:@{@"req": @"getMessagesAfterDate", @"token" : apiKey, @"timestamp" : [NSNumber numberWithDouble:timestamp] } success:^(AFHTTPRequestOperation *operation, id responseObject) {
		NSDictionary *responseDictionary = [(AFJSONRequestOperation*)operation responseJSON];
		if ([[responseDictionary valueForKey:@"error"] boolValue] == YES) {
			NSString *errorDescription = [responseDictionary valueForKey:@"errorDescription"];
			NSError *error = [[NSError alloc] initWithDomain:SF_ERROR_DOMAIN code:410 userInfo:@{@"errorDescription": errorDescription}];
			completionBlock(NO, nil, error);
		}else{
			NSArray *messages = [responseDictionary valueForKeyPath:@"response.messages"];
			completionBlock(YES, messages, nil);
		}
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		completionBlock(NO, nil, error);
	}];
}


- (void)getMessageDetailsForCurrentAPIKeyForMessageID:(int)messageID completionBlock:(void (^)(BOOL success, NSArray *studentArray, NSError *error))completionBlock
{
	NSString *messageIDString = [NSString stringWithFormat:@"%i", messageID];
	
	[self postPath:SF_COMMAND_GET_SERVICE_BASE parameters:@{@"req": @"messageDetails", @"token" : self.apiKey, @"messageid" : messageIDString} success:^(AFHTTPRequestOperation *operation, id responseObject) {
		SFDebugLog(@"%@", operation.responseString);
		NSDictionary *responseDictionary = ((CSJSONHTTPOperation *)operation).responseJSON;
		
		NSMutableArray *ccStudentsArray = [NSMutableArray array];
		for (NSDictionary *dict in [responseDictionary valueForKey:@"response"]) {
			APIStudent *newStudent = [[APIStudent alloc] init];
			[newStudent loadFromJSONDictionary:dict];
			[ccStudentsArray addObject:newStudent];
		}
		completionBlock(YES, ccStudentsArray, nil);
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		completionBlock(NO, nil, error);
	}];
}





- (void)getStudentInfoWithStudentID:(NSString *)studentID completionBlock:(void (^)(BOOL, APIStudent *, NSError *))completionBlock
{
	[self postPath:@"webapp/Service" parameters:@{@"req": @"studentInfo", @"studentid" : studentID} success:^(AFHTTPRequestOperation *operation, id responseObject) {
		SFDebugLog(@"sucess: %@", operation.responseString);
		NSDictionary *response = [(CSJSONHTTPOperation *)operation responseJSON];
		APIStudent *student = [[APIStudent alloc] init];
		[student loadFromJSONDictionary:[response valueForKey:@"student"]];
		completionBlock(YES, student, nil);
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		completionBlock(NO, nil, error);
		
	}];
}


- (void)getRegistrationStatusForCurrentDeviceCompletionBlock:(void(^)(BOOL success, APIStatusResponse *status, NSError *error))completionBlock
{
	NSString *macAddress = [SFGlobals sharedSFGlobals].deviceMACaddress;
	[self getRegistrationStatusForDeviceWithMAC:macAddress withCompletionBlock:completionBlock];
}

- (void)getRegistrationStatusForDeviceWithMAC:(NSString *)mac withCompletionBlock:(void (^)(BOOL, APIStatusResponse *, NSError *))completionBlock
{
	[self postPath:@"webapp/Service" parameters:@{@"req" : @"registerStatus", @"mac" : mac} success:^(AFHTTPRequestOperation *operation, id responseObject) {
		NSDictionary *dict = ((CSJSONHTTPOperation *)operation).responseJSON;
		APIStatusResponse *status = [[APIStatusResponse alloc] init];
		[status loadFromJSONDictionary:dict];
		if (status.isRegistered) {
			self.apiKey = status.apiKey;
			self.isRegistered = YES;
		}
		completionBlock(YES, status, nil);
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		completionBlock(NO, nil, error);
	}];
}

- (void)getStudentInfoForAPIKey:(NSString *)apiKey completionblock:(void(^)(BOOL success, ApiResponseStudentInfo *studentInfo, NSError *error))completionBlock;
{
	[self postPath:SF_COMMAND_GET_SERVICE_BASE parameters:@{@"req": @"studentInfo", @"key" : apiKey} success:^(AFHTTPRequestOperation *operation, id responseObject) {
		NSDictionary *dict = ((CSJSONHTTPOperation *)operation).responseJSON;
		if ([[dict valueForKey:@"error"] boolValue] == YES) {
			[[[UIAlertView alloc] initWithTitle:@"Error" message:[dict valueForKey:@"errorDescription"] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] show];
			completionBlock(NO, nil, operation.error);
		}else{
			ApiResponseStudentInfo *response = [[ApiResponseStudentInfo alloc] init];
			//			[response loadFromJSONDictionary:[dict valueForKey:@"response"]];
			[response loadFromJSONDictionary:dict];
			completionBlock(YES, response, nil);
		}
	} failure:^(AFHTTPRequestOperation *operation, NSError *error) {
		SFDebugLog(@"error: %@", error.localizedDescription);
		[[[UIAlertView alloc] initWithTitle:@"Error" message:error.localizedDescription delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] show];
		completionBlock(NO, nil, error);
	}];
}

#pragma mark - Delegates And Delegation Handling

- (void)didReceiveRemoteNotification:(NSNotification *)notification
{
	NSDictionary *notificationDict = notification.userInfo;
	SFDebugLog(@"did receive notification: %@", notificationDict.description);
	[self.delegate httpClient:self didReceivePushNotification:notificationDict];
}

- (void)registeredToAPNSServerWithToken:(NSString *)token
{
	self.apnsToken = token;
	[self.delegate httpClient:self didRegisterToRemoteAPNServerWithToken:token];
}

- (void)addDelegate:(id<HTTPClientDelegate>)delegate
{
	[self addDelegate:delegate queue:dispatch_get_main_queue()];
}

- (void)removeDelegate:(id<HTTPClientDelegate>)delegate
{
	[self.delegate removeDelegate:delegate];
}

- (void)addDelegate:(id<HTTPClientDelegate>) delegate queue:(dispatch_queue_t)queue
{
	[self.delegate addDelegate:delegate delegateQueue:queue];
}

- (BOOL)apnsRegistrationDone
{
	return _apnsToken == nil;
}

/**
 Since we are using ARC, this will never get called, but we are implementing it for the sake of completion
 */
- (void)dealloc

{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
