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

#import "LoginViewController.h"
#import "OraHTTPClient.h"
#import "SFUtils.h"
#import "SFGlobals.h"
#import "KBKeyboardHandlerDelegate.h"
#import "KBKeyboardHandler.h"
#import "APIStudent.h"
#import "APIDevice.h"
#import "CSCache.h"

@interface LoginViewController () <HTTPClientDelegate, KBKeyboardHandlerDelegate, UITextFieldDelegate>
@property (nonatomic, strong) KBKeyboardHandler *keyboard;

@end

@implementation LoginViewController


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
	if (self = 	[super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
		[[OraHTTPClient sharedHTTPClient] addDelegate:self];
		self.keyboard = [[KBKeyboardHandler alloc] init];
		self.keyboard.delegate = self;
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationDidEnterForeground:) name:UIApplicationWillEnterForegroundNotification object:nil];
	}
	return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.macAddressTextField.text = [[SFGlobals sharedSFGlobals] deviceMACaddress];
	self.apnKey.text = [OraHTTPClient sharedHTTPClient].apnsToken;
	self.activityIndicator.hidesWhenStopped = YES;
	[self.activityIndicator stopAnimating];
	self.title = @"Registration";
	self.infoLabel.text = @"";
	
}

- (void)viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
	[self checkForRegistration];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)checkForRegistration
{
	[self showRegistrationCheckView];
	[[OraHTTPClient sharedHTTPClient] getRegistrationStatusForCurrentDeviceCompletionBlock:^(BOOL success, APIStatusResponse *status, NSError *error) {
		if (success) {
			[self dismissRegistrationCheckView];
			SFDebugLog(@"client key: %@", status.apiKey);
			if (status.isRegistered == YES) {
				SFDebugLog(@"is registered");
				[self.parentViewController dismissViewControllerAnimated:YES completion:^{
					SFDebugLog(@"dismissed because we are already registered");
					[[NSNotificationCenter defaultCenter] postNotificationName:SF_NOTIFICATIONS_LOGIN_VIEW_DISMISSED object:self];
				}];
			}else{
				[self dismissRegistrationCheckView];
				[[[UIAlertView alloc] initWithTitle:@"Device not registered" message:@"Please register this device with the service. If you have registered another device, give the same e-mail to this registration form, too." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] show];
				/*
				 Delete messages left from previews sessions. If this doesn't run, then it's possible that all data has been deleted
				 from the online database, and a new registration has been made, but the database keeps the old ones. This is not likely to
				 happen during everyday usage, but it happens often in developer and debugging sessions where registrations and de-registrations
				 are happening quite often...
				 */
				[[CSCache sharedCSCache] deleteAllMessages];
			}
		}else{
			[[[UIAlertView alloc] initWithTitle:@"Error while connecting to service" message:@"Please check your internet connectivity and restart the application" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] show];
		}
	}];
}

- (IBAction)redoAction:(id)sender {
	if (![SFUtils isValidMailAddress:self.emailTextField.text]) {
		[[[UIAlertView alloc] initWithTitle:@"Invalid e-mail address" message:nil delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] show];
		return;
	}
	[self setViewToLoadingRegistration];
	
	[[OraHTTPClient sharedHTTPClient] preRegisterUsingName:self.firstNameTextField.text andLastName:self.lastNameTextField.text andMail:self.emailTextField.text completion:^(BOOL success, NSDictionary *responseDictionary, BOOL isAlreadyRegistered, NSError *error) {
		[self dismissRegistrationCheckView];
		[self setViewToNetworkingIdle];
		if (success) {
			self.infoLabel.text = @"Please check your emails";
		}else{
			SFDebugLog(@"failure. All login procedures cancelled.");
			[[[UIAlertView alloc] initWithTitle:@"Error" message:error.localizedDescription delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
			[self setViewToNetworkingIdle];
		}
	}];
	
}

- (void)httpClient:(OraHTTPClient *)client didRegisterToRemoteAPNServerWithToken:(NSString *)token
{
	SFDebugLog(@"did register to remote apns notification with token: %@", token);
	self.apnKey.text = token;
}

- (void)showRegistrationCheckView
{
	[self.view addSubview:self.initialLoaderView];
	self.initialLoaderView.frame = self.view.bounds;
}

- (void)dismissRegistrationCheckView
{
	[UIView animateWithDuration:1.0f animations:^{
		self.initialLoaderView.alpha = 0.0f;
	} completion:^(BOOL finished) {
		[self.initialLoaderView removeFromSuperview];
	}];
}

- (void)setViewToLoadingRegistration
{
	self.redoButton.hidden = YES;
	[self.activityIndicator startAnimating];
}

- (void)setViewToNetworkingIdle
{
	self.redoButton.hidden = NO;
	[self.activityIndicator stopAnimating];
}

- (void)keyboardSizeChanged:(CGSize)delta
{
	CGRect frame = self.view.frame;
    frame.size.height -= delta.height;
    self.view.frame = frame;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
	[textField resignFirstResponder];
	return YES;
}

- (void)applicationDidEnterForeground:(NSNotification *)notification{
	SFDebugLog(@"getting student info...");
	[self checkForRegistration];
}


- (void)dealloc
{
	self.keyboard.delegate = nil;
    [[OraHTTPClient sharedHTTPClient] removeDelegate:self];
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
