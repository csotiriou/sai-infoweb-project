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


#import "CSCache.h"
#import "AppDelegate.h"
#import "CSMessage.h"

@interface CSCache ()
@property (nonatomic, strong) dispatch_queue_t queue;
@end

@implementation CSCache
@synthesize managedObjectContext = _managedObjectContext;

+ (CSCache *)sharedCSCache
{
    static dispatch_once_t onceQueue;
    static CSCache *cSCache = nil;
	
    dispatch_once(&onceQueue, ^{ cSCache = [[self alloc] init]; });
    return cSCache;
}


- (id)init
{
    self = [super init];
    if (self) {
		/**
		 Create a serial queue that will accomodate all requests.
		 */
        self.queue = dispatch_queue_create("com.oramind.cacheQueue", NULL);
		[self createManagedObjectContext];
    }
    return self;
}


- (NSArray *)allMessages
{
	NSFetchRequest *request = [[NSFetchRequest alloc] init];
	request.returnsObjectsAsFaults = NO;
	request.entity = [NSEntityDescription entityForName:NSStringFromClass([CSMessage class]) inManagedObjectContext:self.managedObjectContext];
	NSArray *results = [self.managedObjectContext executeFetchRequest:request error:NULL];
	return results;
}

- (CSMessage *)insertNewMesssageForJSONDictionary:(NSDictionary *)dictionary
{
	CSMessage *result = [self messageForMessageID:[[dictionary valueForKey:@"messageID"] integerValue]];
	if (!result) {
		result = [self createNewMessage];
		[result loadFromJSONDictionary:dictionary];
	}
	return result;
}

- (CSMessage *)messageForMessageID:(NSInteger)messageID
{
	NSFetchRequest *request = [[NSFetchRequest alloc] initWithEntityName:NSStringFromClass([CSMessage class])];
	request.predicate = [NSPredicate predicateWithFormat:@"messageID == %i", messageID];
	
	NSArray *results = [self.managedObjectContext executeFetchRequest:request error:NULL];
	if (results.count > 0) {
		return [results objectAtIndex:0];
	}
	return nil;
}

- (void)deleteAllMessages
{
	NSArray *objectsToDelete = [self allMessages];
	for (CSMessage *message in objectsToDelete) {
		[self.managedObjectContext deleteObject:message];
	}
	[self saveContext];
}

- (CSMessage *)createNewMessage
{
	return (CSMessage *)[NSEntityDescription insertNewObjectForEntityForName:NSStringFromClass([CSMessage class]) inManagedObjectContext:self.managedObjectContext];
}

- (void)saveContext
{
	[self.managedObjectContext save:NULL];
}

- (void)rollBack
{
	[self.managedObjectContext rollback];
}


/**
 Returns the managed object context for the application.
 If the context doesn't already exist, it is created and bound to the persistent store coordinator for the application.
 */
- (NSManagedObjectContext *)managedObjectContext
{
	if (_managedObjectContext != nil){
        return _managedObjectContext;
	}
    
    [self createManagedObjectContext];
	
    return _managedObjectContext;
}

- (void)createManagedObjectContext
{
	/*
	 Get the persistent store coordinator from the app delegate, and create a new NSManagedObjectContext from that.
	 */
	NSPersistentStoreCoordinator *coordinator = ((AppDelegate *)[UIApplication sharedApplication].delegate).persistentStoreCoordinator;
    if (coordinator != nil){
		_managedObjectContext = [[NSManagedObjectContext alloc] init];
        [_managedObjectContext setPersistentStoreCoordinator:coordinator];
	}
}



- (NSURL *)applicationDocumentsDirectory
{
    NSArray *searchPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentPath = [searchPaths lastObject];
	
    return [NSURL fileURLWithPath:documentPath];
}
@end
