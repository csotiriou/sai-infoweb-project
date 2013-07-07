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

#import "MessageDetailsViewController.h"
#import "OraHTTPClient.h"
#import "JSTokenButton.h"
#import "JSTokenField.h"
#import "APIStudent.h"
#import "CSMessage.h"
#import "StudentDetailsView.h"
#import <QuartzCore/QuartzCore.h>

@interface MessageDetailsViewController ()
@property (nonatomic) int messageID;
@property (nonatomic, strong) NSMutableArray *studentArray;
@property (nonatomic, strong) JSTokenField *fromField;
@property (strong, nonatomic) JSTokenField *tokenField;
@property (nonatomic, strong) CSMessage *message;
@property (nonatomic, strong) UITextView *textView;
@property (nonatomic, strong) UILabel *dateTextField;
@end

@implementation MessageDetailsViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil andMessage:(CSMessage *)message
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
		self.message = message;
		self.studentArray = [NSMutableArray array];
		
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleTokenFieldFrameDidChange:) name:JSTokenFieldFrameDidChangeNotification object:nil];
	self.title = @"Message Details";
	
}

- (void)viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
	[self refresh];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/**
 Removes the current view hierarchy and rebuilds
 the views.
 */
- (void)refreshViews

{
	for (UIView *view in self.view.subviews) {
		[view removeFromSuperview];
	}
	
	self.fromField = [[JSTokenField alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 20)];
	self.fromField.textField.enabled = NO;
	self.fromField.label.text = @"From: ";
	[self.fromField addTokenWithTitle:self.message.sender representedObject:nil];
	[self.view addSubview:self.fromField];
	
	
	self.tokenField = [[JSTokenField alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.fromField.frame), CGRectGetWidth(self.view.frame), 20)];
	self.tokenField.textField.enabled = NO;
	self.tokenField.label.text = @"To: ";
	[self.view addSubview:self.tokenField];
	
	/*
	 Add a button for each student
	 */
	for (APIStudent *student in self.studentArray) {
		[self.tokenField addTokenWithTitle:[NSString stringWithFormat:@"%@ %@", student.name, student.lastName] representedObject:student];
	}
	
	/*
	 Make every button respond to a touch
	 */
	for (JSTokenButton *button in self.tokenField.tokens) {
		[button addTarget:self action:@selector(didPressToken:) forControlEvents:UIControlEventTouchUpInside];
	}
	
	
	NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
	dateFormatter.dateFormat = @"yyyy/MM/dd hh:mm";
	
	CGRect dateRect = CGRectMake(0, CGRectGetMaxY(self.tokenField.frame), CGRectGetWidth(self.view.frame), 59);
	self.dateTextField = [[UILabel alloc] initWithFrame:dateRect];
	self.dateTextField.text = [NSString stringWithFormat:@"  Date sent: %@", [dateFormatter stringFromDate:self.message.date]];
	self.dateTextField.font = [UIFont systemFontOfSize:15];
	[self.view addSubview:self.dateTextField];
	
	
	CGRect textViewRect = self.dateTextField.frame;
	textViewRect.origin.y = CGRectGetMaxY(textViewRect);
	
	self.textView = [[UITextView alloc] initWithFrame:textViewRect];
	[self.view addSubview:self.textView];
	self.textView.font = [UIFont systemFontOfSize:14];
	self.textView.editable = NO;
	self.textView.text = self.message.content;
	
	textViewRect.size.height = CGRectGetHeight(self.view.frame) - CGRectGetMaxY(self.dateTextField.frame);
	self.textView.frame = textViewRect;
	
	[self layoutDateAndTextViews];
}

/**
 *	@brief	Reorder the views so that everything is visible
 */
- (void)layoutDateAndTextViews

{
	CGRect dateRect = CGRectMake(0, CGRectGetMaxY(self.tokenField.frame), CGRectGetWidth(self.view.frame), 59);
	self.dateTextField.frame = dateRect;

	CGRect textViewRect = self.dateTextField.frame;
	textViewRect.origin.y = CGRectGetMaxY(textViewRect);
	self.textView.frame = textViewRect;
}

- (void)refresh
{
	[[OraHTTPClient sharedHTTPClient] getMessageDetailsForCurrentAPIKeyForMessageID:self.message.messageID.intValue completionBlock:^(BOOL success, NSArray *studentArray, NSError *error) {
		[self.studentArray setArray:studentArray];
		[self refreshViews];
	}];
}

/**
 *	@brief	Notification called when a JSTokenField changes its frame due to its contents
 *
 *	@param 	note the notification
 */
- (void)handleTokenFieldFrameDidChange:(NSNotification *)notification

{
	if ([notification.object isEqual:self.tokenField]) {
		SFDebugLog(@"frame did change: %@", NSStringFromCGRect(self.tokenField.frame));
		[self layoutDateAndTextViews];
	}
}

- (void)didPressToken:(JSTokenButton *)tokenButton
{
	APIStudent *student = tokenButton.representedObject;
	SFDebugLog(@"did press token for: %@", student.email);
	[self presentStudentDetailsForStudent:student];
}

#define kDismissButton 100
/**
 *	@brief	Setup a view and display student information. Will take the student details view that already
 *	exists in the XIB, it will fill it with information and then display it with a popup animation
 *
 *	@param 	student The student to display information about
 */
- (void)presentStudentDetailsForStudent:(APIStudent *)student

{
	UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
	button.frame = self.view.bounds;
	button.tag = kDismissButton;
	button.alpha = 0.0f;
	button.backgroundColor = [UIColor whiteColor];
	[button addTarget:self action:@selector(dismissStudentDetailsView) forControlEvents:UIControlEventTouchUpInside];
	[self.view addSubview:button];
	
	CGRect properDetailsFrame = CGRectMake(19, 138, 283, 84);
	self.studentDetailsView.alpha = 0.0f;
	self.studentDetailsView.frame = CGRectInset(properDetailsFrame, 10, 10);
	[self.view addSubview:self.studentDetailsView];
	
	self.studentDetailsView.nameLabel.text = student.name;
	self.studentDetailsView.lastNameLabel.text = student.lastName;
	self.studentDetailsView.emailLabel.text = student.email;
	
	[UIView animateWithDuration:0.2f animations:^{
		self.studentDetailsView.frame = CGRectInset(properDetailsFrame, -10, -10);
		self.studentDetailsView.alpha = 1.0f;
		button.alpha = 0.5;
	} completion:^(BOOL finished) {
		[UIView animateWithDuration:0.2f animations:^{
			self.studentDetailsView.frame = properDetailsFrame;
		}];
	}];
}

/**
 *	@brief	Hide the student details view with an animation
 */
- (void)dismissStudentDetailsView

{
	[UIView animateWithDuration:0.3 animations:^{
		self.studentDetailsView.alpha = 0.0f;
	} completion:^(BOOL finished) {
		[self.studentDetailsView removeFromSuperview];
	}];
	[[self.view viewWithTag:kDismissButton] removeFromSuperview];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
