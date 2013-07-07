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
#import <CoreData/CoreData.h>
#import "CSMessage.h"


@interface CSCache : NSObject
@property (nonatomic, readonly) NSManagedObjectContext * managedObjectContext;


/**
 Returns the singleton object associated with the database.
 @return A CSCache object, ready to access the database
 @discussion When used as a singleton, this class is thread safe, but the managed object context used by the class is not. In
 order to ensure multithreading safety, developers can instantiate a new instance of CSCache as they see fit. Each instance of 
 a CSCache will have its own managed object context.
 */
+ (CSCache *)sharedCSCache;


/**
 Gets all CSMessages that are stored in the database and returns them into an array
 @param userID the user id (NSNumber)
 @return a sorted array of CSMessage;
 @discussion the Array will be sorted. Multithreading will be automatically handled by the class. Method is thread-safe.
 */
- (NSArray *)allMessages;


/**
 Creates and inserts a new CSMessage, whose elements are going to be retrieved by the JSON dictionary
 passed as an argument. A CSMessage's ID is unique. An attempt to insert a dictionary of another message
 with an existing message ID will result in the existing message in the database being returned.
 @param dictionary a JSON dictionary
 @return the resulted CSMessage
 @discussion The dictionary passed must comply with the API.
 */
- (CSMessage *)insertNewMesssageForJSONDictionary:(NSDictionary *)dictionary;


/**
 Gets a messages stored in the database with the specified message ID.
 @param messageID the message ID
 @return the message found. nil in case where no such message exists
 */
- (CSMessage *)messageForMessageID:(NSInteger)messageID;


/**
 *	@brief	Deletes all the messages from the database. Warning: The context is automatically saved after this operation, so when calling
 *	this function, there is no going back!
 */
- (void)deleteAllMessages;


/**
 *	@brief	Creates a new CSMessage instance, associatiated and modelled after the cache's managed object context. Note that the new CSMessage will NOT
 *	saved into the database, unless the #saveContext function is used.
 *
 *	@return	a new CSMessage.
 */
- (CSMessage *)createNewMessage;


/**
 *	@brief	Rolls back changes. Any unsaved objects will be permanently lost. After calling this method, make sure you do not keep any references to any objects
 *	added after the last save state of the database, because they will be invalid afterwards.
 */
- (void)rollBack;


/**
 Saves the changes into the databse.
 */
- (void)saveContext;

@end
