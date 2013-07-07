//
//  StudentInfoViewController.m
//  InfoWebTests
//
//  Created by Christos Sotiriou on 6/16/13.
//  Copyright (c) 2013 Oramind. All rights reserved.
//

#import "StudentInfoViewController.h"
#import "APIStudent.h"
#import "OraHTTPClient.h"

@interface StudentInfoViewController ()

@end

@implementation StudentInfoViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.title = @"Student Info";
}

- (void)viewWillAppear:(BOOL)animated
{
	[super viewWillAppear:animated];
	[self requestServerInfo];
}


- (void)showNetworkActivityView
{
	self.networkActivityView.hidden = NO;
}

- (void)hideNetworkActivityView
{
	self.networkActivityView.hidden = YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}


- (void)requestServerInfo
{
	[self showNetworkActivityView];
	SFDebugLog(@"requesting server info...");
	[[OraHTTPClient sharedHTTPClient] getStudentInfoForAPIKey:[OraHTTPClient sharedHTTPClient].apiKey completionblock:^(BOOL success, ApiResponseStudentInfo *studentInfo, NSError *error) {
		[self hideNetworkActivityView];
		
		if (success) {
			SFDebugLog(@"success");
			self.nameLabel.text = studentInfo.student.name;
			self.lastNameLabel.text = studentInfo.student.lastName;
			self.emailLabel.text = studentInfo.student.email;
			self.devicesLabel.text = [NSString stringWithFormat:@"%li", (long)studentInfo.deviceCount];
			self.messagesCountLabel.text = [NSString stringWithFormat:@"%li", (long)studentInfo.messagesReceived];
		}else{
			SFDebugLog(@"failure");
		}
	}];
}
@end
