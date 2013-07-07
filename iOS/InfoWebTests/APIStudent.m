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

#import "APIStudent.h"
#import "APIDevice.h"
#import "SFGlobals.h"

@implementation APIStudent

- (id)init
{
    self = [super init];
    if (self) {
        self.deviceArray = [NSMutableArray array];
    }
    return self;
}

- (void)loadFromJSONDictionary:(NSDictionary *)dictionary
{
	self.lastName = [dictionary valueForKey:@"lastName"];
	self.name = [dictionary valueForKey:@"name"];
	self.email = [dictionary valueForKey:@"email"];
	self.studentID = [[dictionary valueForKey:@"studentID"] stringValue];
	self.registeredBoolean = [NSNumber numberWithBool:[[dictionary valueForKey:@"registered"] boolValue]];
	
	/*
	 If there is a device dictionary present, load it.
	 */
	if ([dictionary valueForKey:@"devices"] != NULL) {
		for (NSDictionary *deviceDict in [dictionary valueForKey:@"devices"]) {
			APIDevice *newDevice = [[APIDevice alloc] init];
			[newDevice loadFromJSONDictionary:deviceDict];
			[self.deviceArray addObject:newDevice];
		}
	}
}

- (APIDevice *)currentDevice{
	for (APIDevice *device in self.deviceArray) {
		if ([device.macAddress isEqualToString:[SFGlobals sharedSFGlobals].deviceMACaddress]) {
			return device;
		}
	}
	return nil;
}


- (BOOL)isRegistered
{
	return self.registeredBoolean.boolValue;
}

- (NSDictionary *)toJSONDictionary
{
	NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithDictionary:
								 @{@"lastName": self.lastName,
								 @"name" : self.name,
								 @"email" : self.email,
								 @"studentID" : self.studentID,
								 @"registered" : self.registeredBoolean
								 }];
	return [dict copy];
}
@end
