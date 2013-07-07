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

#import "CacheTests.h"

@implementation CacheTests

- (void)testsInsertMessages1
{
	expect([self.cache allMessages].count).to.equal(0);
	
	[self.cache createNewMessage];
	expect([self.cache allMessages].count).to.equal(1);
	[self.cache createNewMessage];
	expect([self.cache allMessages].count).to.equal(2);
}

- (void)testsInsertMessages2
{
	expect([self.cache allMessages].count).to.equal(0);
	
	CSMessage *message = [self.cache createNewMessage];
	NSMutableDictionary *dict = [self sampleMessageJSONDictionary];
	[message loadFromJSONDictionary:dict];
	
	expect([self.cache allMessages].count).to.equal(1);
	
	CSMessage *retrievedMessage = [self.cache messageForMessageID:[[dict valueForKey:@"messageID"] integerValue]];
	expect(retrievedMessage).toNot.beNil();
	expect(retrievedMessage.content).to.equal(@"message content!");
	expect(retrievedMessage.userID).to.equal(@1);
	expect(retrievedMessage.studentID).to.equal(@3);
	expect(retrievedMessage.sender).to.equal(@"sender");
}


- (void)testDeleteAllMessages
{
	for (int i = 0; i<100; i++) {
		[self.cache createNewMessage];
	}
	expect([self.cache allMessages].count).to.equal(100);
	[self.cache deleteAllMessages];
	expect([self.cache allMessages].count).to.equal(0);
}


- (NSMutableDictionary *)sampleMessageJSONDictionary
{
	NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
	[dictionary setValue:@1 forKey:@"messageID"];
	[dictionary setValue:@"message content!" forKey:@"content"];
	[dictionary setValue:@1 forKey:@"userID"];
	[dictionary setValue:@([[NSDate date] timeIntervalSince1970]) forKey:@"dateSent"];
	[dictionary setValue:@3 forKey:@"studentID"];
	[dictionary setValue:@"sender" forKey:@"sender"];
	return dictionary;
}
@end
