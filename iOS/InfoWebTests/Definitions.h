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

#ifndef InfoWebTests_Definitions_h
#define InfoWebTests_Definitions_h


/**
 *	@brief	The server address to which this application will talk.
 */
#define SERVER_PREFIX @"http://10.0.1.3"


/**
 *	@brief	The port in which the service runs. Global server uses "8080"
 */
#define SERVER_PORT @"8081"


/**
 *	@brief	The prefix of the web application to serve the iOS application
 */
#define WEB_APPLICATION_PREFIX				@"webapp"


/**
 *	@brief	The servlet to which this application will talk
 */
#define SF_COMMAND_GET_SERVICE_BASE			@"webapp/Service"


/* NAMES FOR PREFERENCE FIELDS. MADE FOR CONVENIENCE (AUTO-COMPLETE SUPPORT) */
#define SF_PREFS_HAS_MADE_REGISTRATION		@"hasMadeRegistration"
#define SF_PREFS_STUDENT_ID					@"studentID"
#define SF_PREFS_STUDENT_DICT				@"studentDictionary"
#define SF_PREFS_API_KEY					@"apikey"



/**
 *	@brief	How many messages will be fetched per page?
 */
#define SF_MESSAGE_PAGE_SIZE				100



/**
 *	@brief	Not yet used
 */
#define SF_ERROR_DOMAIN						@"com.oramind.APIError"


/**
 *	@brief	Notification name that will be thrown when the application has received a remote notification
 */
#define SF_NOTIFICATIONS_DID_RECEIVE_NOTIFICATION	@"didReceiveRemoteNotification"

#define SF_NOTIFICATIONS_DID_ENTER_FOREGROUND		@"didEnterForeGroundNotification"
#define	SF_NOTIFICATIONS_LOGIN_VIEW_DISMISSED		@"loginViewDismisssedNotification"

#endif
