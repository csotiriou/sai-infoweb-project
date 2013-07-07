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

#import <Foundation/Foundation.h>

/**
 *	@brief	Small utility functions
 */
@interface SFUtils : NSObject



/**
 *	@brief	Encodes the receiver into a url encoded equivalent for making requests
 *
 *	@param 	unescaped 	the original string to encode
 *
 *	@return	a url encoded string
 */
+ (NSString *)urlEncodedString:(NSString *)unescaped;


/**
 *	@brief	Determines if an e-mail address is a valid e-mail address. 
 *	Algotithm taken from http://stackoverflow.com/questions/800123/best-practices-for-validating-email-address-in-objective-c-on-ios-2-0
 *
 *	@param 	email 	the e-mail
 *
 *	@return	YES if this is a valid e-mail address. NO otherwise.
 */
+ (BOOL)isValidMailAddress:(NSString *)email;


@end
