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

#import "MessagesTableViewController.h"
#import "CSCache.h"
#import "MessageDetailsViewController.h"


@interface MessagesTableViewController ()
@property (nonatomic, strong) NSMutableArray *messages;
@property (nonatomic, strong) NSDateFormatter *dateFormatter;
@end

@implementation MessagesTableViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
		self.messages = [NSMutableArray array];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refresh) name:UIApplicationWillEnterForegroundNotification object:nil];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginViewWasDismissed:) name:SF_NOTIFICATIONS_LOGIN_VIEW_DISMISSED object:nil];
		self.dateFormatter = [[NSDateFormatter alloc] init];
		self.dateFormatter.dateFormat = @"MM/dd/yyyy HH:mm";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
	self.title = @"Messages";
	
	UIButton *loadMoreButton = [UIButton buttonWithType:UIButtonTypeCustom];
	[loadMoreButton setTitle:[NSString stringWithFormat:@"Load %i more...", SF_MESSAGE_PAGE_SIZE] forState:UIControlStateNormal];
	loadMoreButton.frame = CGRectMake(0, 0, self.view.frame.size.width, 60);
	[loadMoreButton setTitleColor:[UIColor colorWithRed:0.20f green:0.32f blue:0.94f alpha:1.00f] forState:UIControlStateNormal];

	[loadMoreButton addTarget:self action:@selector(requestMore) forControlEvents:UIControlEventTouchUpInside];
	
	[self.tableView setTableFooterView:loadMoreButton];
}

- (void)viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return self.messages.count;
}


#define kContentViewTag 100
#define kDateViewTag	101
#define kSenderViewTag	102
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (!cell) {
		cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
		CGFloat rowHeight = [self tableView:tableView heightForRowAtIndexPath:indexPath];
		CGRect contentRect = CGRectMake(5, 5, CGRectGetWidth(tableView.frame), rowHeight - 40);
		
		UILabel *contentLabel = [[UILabel alloc] initWithFrame:contentRect];
		contentLabel.tag= kContentViewTag;
		contentLabel.font = [UIFont systemFontOfSize:14];
		contentLabel.numberOfLines = 2;
		[cell.contentView addSubview:contentLabel];
		
		CGRect fromRect = CGRectOffset(contentRect, 0, CGRectGetHeight(contentRect) + 10);
		fromRect.size.width = CGRectGetWidth(tableView.frame) / 2;
		
		UILabel *fromLabel = [[UILabel alloc] initWithFrame:fromRect];
		fromLabel.font = [UIFont systemFontOfSize:12];
		fromLabel.tag = kSenderViewTag;
		[cell.contentView addSubview:fromLabel];
		
		CGRect dateRect = CGRectOffset(fromRect, CGRectGetWidth(fromRect), 0);
		dateRect.size.width -= 20;
		UILabel *dateLabel = [[UILabel alloc] initWithFrame:dateRect];
		dateLabel.font = [UIFont systemFontOfSize:12];
		dateLabel.tag = kDateViewTag;
		dateLabel.textAlignment = NSTextAlignmentRight;
		[cell.contentView addSubview:dateLabel];
	}
	
	cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
	
	UILabel *contentLabel = (UILabel *)[cell.contentView viewWithTag:kContentViewTag];
	UILabel *dateLabel = (UILabel *)[cell.contentView viewWithTag:kDateViewTag];
	UILabel *fromLabel = (UILabel *)[cell.contentView viewWithTag:kSenderViewTag];
	
	CSMessage *currentMessage = [self.messages objectAtIndex:indexPath.row];
	contentLabel.text = currentMessage.content;
	dateLabel.text = [self.dateFormatter stringFromDate:currentMessage.date];
	fromLabel.text = [NSString stringWithFormat: @"Sent by: %@", currentMessage.sender];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
	return 60;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	CSMessage *message = [self.messages objectAtIndex:indexPath.row];
	MessageDetailsViewController *details = [[MessageDetailsViewController alloc] initWithNibName:NSStringFromClass([MessageDetailsViewController class]) bundle:nil andMessage:message];
	[self.navigationController pushViewController:details animated:YES];
}

#pragma mark - Functions
- (void)refresh
{
	[self loadNewestMessages];
}

- (void)requestMore
{
	[self loadMoreMessages];
	[self startLoading];
}

- (void)loadFromDatabase
{
	NSArray *allMessages = [[CSCache sharedCSCache] allMessages];
	[self.messages setArray:allMessages];
	[self sortMessagesArrayByDescending:self.messages];
	[self.tableView reloadData];
}



/**
 *	@brief	Loads additional messages from the online database.
 *
 *	@param 	eraseOld 	if YES, the messages that already exist in the list will be released.
 */
- (void)loadMoreMessages


{	
	if ([OraHTTPClient sharedHTTPClient].isRegistered) {
		[[OraHTTPClient sharedHTTPClient] getMessagesForCurrentAPIKeyWithStart:MAX((NSInteger)self.messages.count-1, 0) andNumberOfResults:SF_MESSAGE_PAGE_SIZE completionBlock:^(BOOL success, NSArray *messagesJSONArray, NSError *error) {
			if (success) {
				NSMutableArray *messagesTemp = [NSMutableArray array];
				SFDebugLog(@"success");
				[self insertNewMessagesUniqueFromDictionaries:messagesJSONArray];
				
				[[CSCache sharedCSCache] saveContext];
				[self.messages addObjectsFromArray:messagesTemp];
				[self sortMessagesArrayByDescending:self.messages];
				[self stopLoading];
				[self.tableView reloadData];
			}else{
				SFDebugLog(@"failure! %@", error.description);
				[[[UIAlertView alloc] initWithTitle:@"Error while downloading data" message:[NSString stringWithFormat:@"Downloading data failed. Error: %@", error.description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] show];
				[self stopLoading];
			}
		}];
	}
}

/**
 *	@brief	Gets the messages that were delivered after the newest message's date
 */
- (void)loadNewestMessages
{
	if ([OraHTTPClient sharedHTTPClient].isRegistered) {
		NSMutableArray *array = [self.messages mutableCopy];
		[self sortMessagesArrayByDescending:array];
		if (array.count >0) {
			CSMessage *currentMessage = [array objectAtIndex:0];
			NSTimeInterval timeStamp = [currentMessage.date timeIntervalSince1970];
			[[OraHTTPClient sharedHTTPClient] getMessagesForApiKey:[OraHTTPClient sharedHTTPClient].apiKey afterTimeStamp:timeStamp completionBlock:^(BOOL success, NSArray *messagesJSONArray, NSError *error) {
				if (success) {
					[self insertNewMessagesUniqueFromDictionaries:messagesJSONArray];
					[[CSCache sharedCSCache] saveContext];
					
					[self sortMessagesArrayByDescending:self.messages];
					[self stopLoading];
					[self.tableView reloadData];
				}else{
					SFDebugLog(@"error: %@", error);
				}
			}];
		}else{
			[self loadMoreMessages];
		}
	}
}

/**
 *	@brief	Inserts new messages in the database that do not already exist.
 *
 *	@param 	array 	an array of dictionaries, to be parsed as CSMessage instances
 */
- (void)insertNewMessagesUniqueFromDictionaries:(NSArray *)array

{
	for (NSDictionary *dict in array) {
		if (![[CSCache sharedCSCache] messageForMessageID:[[dict valueForKey:@"messageID"] integerValue]]) {
			CSMessage *newMessage = [[CSCache sharedCSCache] insertNewMesssageForJSONDictionary:dict];
			[self.messages addObject:newMessage];
		}
	}
}

/**
 *	@brief	Sorts messages found in an array by date, descending
 *
 *	@param 	array 	an array of CSMessage instances
 */
- (void)sortMessagesArrayByDescending:(NSMutableArray *)array

{
	[array sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
		CSMessage *message1 = obj1;
		CSMessage *message2 = obj2;
		NSComparisonResult comparisonResult =  [message1.date compare:message2.date];
		switch (comparisonResult) {
			case NSOrderedAscending:
				return NSOrderedDescending;
				break;
			case NSOrderedDescending:
				return NSOrderedAscending;
				break;
			default:
				return NSOrderedSame;
		}
		return NSOrderedSame;
	}];
}


#pragma mark - Delegations

- (void)loginViewWasDismissed:(NSNotification *)notification
{
	if ([OraHTTPClient sharedHTTPClient].isRegistered) {
		[self loadFromDatabase];
		[self refresh];
	}
}


- (void)httpClient:(OraHTTPClient *)client didReceivePushNotification:(NSDictionary *)notificationJSON
{
	[self loadNewestMessages];
}


#pragma mark -
- (void)dealloc
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
