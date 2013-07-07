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


#import "UtilityTests.h"
#import "SFUtils.h"
#import "NSDictionary+SFAdditions.h"
@implementation UtilityTests

- (void)testsUtilJSONWithNull
{
	NSString *jsonString = @"{ \"one\": null, \"two\": \"hello\", \"three\": 3 }";
	NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:[jsonString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:NULL];
	
	expect([dict valueForKey:@"one"]).to.beInstanceOf([NSNull class]);
	expect([dict jsonFixedValueForKey:@"one"]).to.beNil();
	expect([dict jsonFixedValueForKey:@"two"]).to.equal(@"hello");
	expect([dict jsonFixedValueForKey:@"three"]).to.equal(@3);
}

- (void)testsUrlEncoding
{
	NSString *stringToEncode = @"string (blank) + another value <with>:anotherSymbol";
	NSString *encodedString = [SFUtils urlEncodedString:stringToEncode];
	expect(encodedString).to.equal(@"string%20%28blank%29%20%2B%20another%20value%20%3Cwith%3E%3AanotherSymbol");
}

- (void)testEmailValid
{
	NSString *emailValid = @"test@test.test";
	NSString *emailValid2 = @"mastoras@gmail.com";
	
	expect([SFUtils isValidMailAddress:emailValid]).to.beTruthy();
	expect([SFUtils isValidMailAddress:emailValid2]).to.beTruthy();
}

- (void)testEmailInvalid
{
	NSString *emailInValid = @"test@";
	NSString *emailInValid2 = @"mastoras-gmail.com.co.uk";
	
	expect([SFUtils isValidMailAddress:emailInValid]).to.beFalsy();
	expect([SFUtils isValidMailAddress:emailInValid2]).to.beFalsy();
}

@end
