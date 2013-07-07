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


#import "AFHTTPClient.h"
#import "AFNetworking.h"
#import "APIStudent.h"
#import "APIStudent.h"
#import "APIDevice.h"
#import "APIStatusResponse.h"
#import "ApiResponseStudentInfo.h"


@class OraHTTPClient;

/**
 *	@brief	Protocol whose functions can be implemented by all objects willing to
 *	receive callback notifications and other information from the HTTPClient object.
 */
@protocol HTTPClientDelegate <NSObject>

@optional
- (void)httpClient:(OraHTTPClient *)client didRegisterToRemoteAPNServerWithToken:(NSString *)token;
- (void)httpClient:(OraHTTPClient *)client didHandleRegisterWithStudentObject:(APIStudent *)student;
- (void)httpClient:(OraHTTPClient *)client didPreregisterWithStudentObject:(APIStudent *)student;
- (void)httpClient:(OraHTTPClient *)client didReceivePushNotification:(NSDictionary *)notificationJSON;
@end




/**
 *	@discussion	OraHTTPClient is a concrete instance of AFHTTPClient. DESIGN CHOICE: it is a singleton. The singleton
 * instance is thread-safe, as is it handled by Grand Central Dispatch. Automates actions like wrapping POST data and
 * facilitating the parameter passing with a dictionary-like approach. All network requests to the client are performed
 * asynchronously, and are thread-safe.
 */
@interface OraHTTPClient : AFHTTPClient

@property (nonatomic, strong) NSString *apnsToken;
@property (nonatomic) BOOL apnsRegistrationDone;
@property (nonatomic, strong) NSString *apiKey;
@property (nonatomic) BOOL isRegistered;

/**
 Gets the shared client for use by any part of the application
 @returns the shared OraHTTPClient instance
 */
+ (OraHTTPClient *)sharedHTTPClient;



/**
 *	@brief	Convenience function. Calls the registration method of the server. The APNS token, and the Mac Address passed as arguments will automatically be filled.
 *	@discussion The completion block returns the dictionary and a flag that indicates if the device is already registered. The flag is true if in the list of the student's
 *	registered revices (in the response dictionary), the device with the current MAC is contained. This flag is reserved for later used, and it is not
 *	currently used by the iOS application, because it has not undergone the testing necessary.
 *	
 *	@param 	name 	The name of the student.
 *	@param 	lastname 	The last name of the student
 *	@param 	mail 	The e-mail address of the student
 */
- (void)preRegisterUsingName:(NSString *)name andLastName:(NSString *)lastname andMail:(NSString *)mail completion:(void (^)(BOOL success, NSDictionary *responseDictionary, BOOL isAlreadyRegistered, NSError *error))block;


/**
 *	@brief	Calls the registration method of the server. The APNS token, and the Mac Address passed as arguments will automatically be filled.
 *	@discussion The completion block returns the dictionary and a flag that indicates if the device is already registered. The flag is true if in the list of the student's
 *	registered revices (in the response dictionary), the device with the current MAC is contained. This flag is reserved for later used, and it is not
 *	currently used by the iOS application, because it has not undergone the testing necessary.
 *	
 *	@param 	name 	The name of the student.
 *	@param 	lastname 	The last name of the student
 *	@param 	mail 	The e-mail address of the student
 *	@param	macAddress the macAddress of the device.
 */
- (void)preRegisterUsingName:(NSString *)name andLastName:(NSString *)lastname andMail:(NSString *)mail andMAC:(NSString *)macAddress completion:(void (^)(BOOL success, NSDictionary *responseDictionary, BOOL isAlreadyRegistered, NSError *error))block;


/**
 Gets the registration status for the current device, in the form of an HTTP JSON response. After the request is done,
 completionBLock will be called, holding an APIStatusResponse object, a success flag (YES in case of a successul response), and an
 error object that will indicate the failure reason, in case where the "success" flag is false
 */
- (void)getRegistrationStatusForCurrentDeviceCompletionBlock:(void(^)(BOOL success, APIStatusResponse *status, NSError *error))completionBlock;



/**
 *	@brief	Gets the registration status for the device with the MAC Address given as the argument, in the form of an HTTP JSON response. After the request is done,
 * completionBLock will be called, holding an APIStatusResponse object, a success flag (YES in case of a successul response), and an
 * error object that will indicate the failure reason, in case where the "success" flag is false
 *
 *	@param 	mac the mac address of the device
 */
- (void)getRegistrationStatusForDeviceWithMAC:(NSString *)mac withCompletionBlock:(void(^)(BOOL success, APIStatusResponse *status, NSError *error))completionBlock;



/**
 *	@brief Add a delegate to the object. The addition of an object as a delegate does not increase this object's
 *	retain count. Messages to delegates will be given into the main queue of GCD. In case where the developer wants
 *	to supply an additional queue, the addDelegate:queue: function must be called
 */
- (void)addDelegate:(id<HTTPClientDelegate>) delegate;

/**
 *	@brief	Add a delegate to the object. The addition of an object as a delegate does not increase this object's
 *	retain count
 *
 *	@param 	queue An instance of dispatch_queue_t. When a message is about to be sent into all messages,
 *	it will use this queue to be executed.
 */
- (void)addDelegate:(id<HTTPClientDelegate>) delegate queue:(dispatch_queue_t)queue;

/**
 *	@brief	Remove a delegate from the delegate queue.
 */
- (void)removeDelegate:(id<HTTPClientDelegate>) delegate;


/**
 *	@brief Will hold this token, as the current APNSToken, and will also dispatch a message to all delegates
 *	that a token was retrieved from the push notification service.
 *
 *	@param 	token an APNS token
 */
- (void)registeredToAPNSServerWithToken:(NSString*)token;



/**
 *	@brief	Convenience function. Makes a request to the server using the current API key and gets the messages for this API key
 *
 *	@param 	offset 	The offset of the returned rows. Used for pagination
 *	@param 	resultNum 	The count of messages to be returned
 *	@param completionBlock Callback handler. It will be called when the request is completed or failed. the 'success' field of the block will indicate if an error
 *	occured. In this case, the 'error' parameter will hold the error reason. In case of success, the 'messagesJSONArray' parameter will hold NSDictionary instances.
 */
- (void)getMessagesForCurrentAPIKeyWithStart:(int)offset andNumberOfResults:(int)resultNum completionBlock:(void (^)(BOOL success, NSArray *messagesJSONArray, NSError *error))completionBlock;


/**
 *	@brief	Makes a request to the server using the current API key and gets the messages for this API key
 *
 *	@param 	offset 	The offset of the returned rows. Used for pagination
 *	@param 	resultNum 	The count of messages to be returned
 *	@param	apiKey the API key with which to make the request. An invalid API key means than no results will be returned.
 *	@param completionBlock Callback handler. It will be called when the request is completed or failed. the 'success' field of the block will indicate if an error
 *	occured. In this case, the 'error' parameter will hold the error reason. In case of success, the 'messagesJSONArray' parameter will hold NSDictionary instances.
 */
- (void)getMessagesForAPIKey:(NSString *)apiKey withStart:(int)offset andNumberOfResults:(int)resultNum completionBlock:(void (^)(BOOL success, NSArray *messagesJSONArray, NSError *error))completionBlock;


/**
 *	@brief	Makes a request to the server using the current API key and gets the messages for this API key which were sent after the timestamp
 *	provided as an argument
 *
 *	@param 	apiKey 	The device's API key
 *	@param 	timestamp 	a date as a unix timestamp
 */
- (void)getMessagesForApiKey:(NSString *)apiKey afterTimeStamp:(NSTimeInterval)timestamp completionBlock:(void(^)(BOOL success, NSArray *messagesJSONArray, NSError *error))completionBlock;


/**
 *	@brief	Gets the details of a message designated with a message ID;
 *
 *	@param 	messageID 	The messageID of the message
 *	@param	completionBlock Callback handler. It will be called when the request is completed or failed. the 'success' field of the block will indicate if an error
 *	occured. In this case, the 'error' parameter will hold the error reason. In case of success, the 'studentArray' parameter will hold CSStudent instances, which are
 *	the students this messages was sent to.
 */
- (void)getMessageDetailsForCurrentAPIKeyForMessageID:(int)messageID completionBlock:(void (^)(BOOL success, NSArray *studentArray, NSError *error))completionBlock;


/**
 *	@brief	gets the student with designated by this studentID
 *
 *	@param 	studentID 	The student ID
 *	@param	completionBlock Callback handler. It will be called when the request is completed or failed. the 'success' field of the block will indicate if an error
 *	occured. In this case, the 'error' parameter will hold the error reason. In case of success, the 'student' parameter will hold a CSStudent instance.
 *	THIS FUNCTION IS DEPRECATED
 */
- (void)getStudentInfoWithStudentID:(NSString *)studentID completionBlock:(void(^)(BOOL success, APIStudent *student, NSError *error))completionBlock;


/**
 *	@brief	Gets the student info that corresponds to an API key.
 *
 *	@param 	apiKey 	an API key.
 *	@param	completionBlock Callback handler. It will be called when the request is completed or failed. the 'success' field of the block will indicate if an error
 *	occured. In this case, the 'error' parameter will hold the error reason. In case of success, the 'studentInfo' parameter will hold a StudentInfoResponse instance.
 */
- (void)getStudentInfoForAPIKey:(NSString *)apiKey completionblock:(void(^)(BOOL success, ApiResponseStudentInfo *studentInfo, NSError *error))completionBlock;

@end
