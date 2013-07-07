//
//  AppDelegate.m
//  InfoWebTests
//
//  Created by Christos Sotiriou on 3/31/13.
//  Copyright (c) 2013 Oramind. All rights reserved.
//

#import "AppDelegate.h"


#import "AFNetworking.h"
#import "OraHTTPClient.h"
#import "MessagesTableViewController.h"
#import "SFGlobals.h"
#import "LoginViewController.h"
#import "CustomTabViewController.h"
#import "StudentInfoViewController.h"


@interface AppDelegate ()
@property (nonatomic, strong) AFHTTPRequestOperation *currentOperation;

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
	[SFGlobals sharedSFGlobals];
	

	UINavigationController *messagesNav = [[UINavigationController alloc] initWithRootViewController:[[MessagesTableViewController alloc] initWithStyle:UITableViewStylePlain]];
	UINavigationController *studentInfoNav = [[UINavigationController alloc] initWithRootViewController:[[StudentInfoViewController alloc] initWithNibName:NSStringFromClass([StudentInfoViewController class]) bundle:nil]];
	
	CustomTabViewController *tabBarController = [[CustomTabViewController alloc] init];
	tabBarController.viewControllers = @[messagesNav, studentInfoNav];
	
	((UITabBarItem*)[tabBarController.tabBar.items objectAtIndex:0]).image = [UIImage imageNamed:@"mail"];
	((UITabBarItem*)[tabBarController.tabBar.items objectAtIndex:1]).image = [UIImage imageNamed:@"student_icon"];
	
	((UITabBarItem*)[tabBarController.tabBar.items objectAtIndex:1]).title = @"Student Info";
	((UITabBarItem*)[tabBarController.tabBar.items objectAtIndex:0]).title = @"Messages";
	
	self.window.rootViewController = tabBarController;
	
    [self.window makeKeyAndVisible];
	
	[[UIApplication sharedApplication] registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound];
	

    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
	// Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
	// Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
	// Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
	// If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
	// Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
	[[NSNotificationCenter defaultCenter] postNotificationName:SF_NOTIFICATIONS_DID_ENTER_FOREGROUND object:self];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
	// Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
	// Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
	NSLog(@"did register notifications: %@", deviceToken.description);
	NSString *normalizedDeviceToken = [[[deviceToken.description stringByReplacingOccurrencesOfString:@"<" withString:@""] stringByReplacingOccurrencesOfString:@">" withString:@""] stringByReplacingOccurrencesOfString:@" " withString:@""];
	
	[[OraHTTPClient sharedHTTPClient] registeredToAPNSServerWithToken:normalizedDeviceToken];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
	NSLog(@"did fail to register for remote notifications %@", error.localizedDescription);
}


- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
	NSLog(@"remote notification received: %@", userInfo.description);
	[[NSNotificationCenter defaultCenter] postNotificationName:SF_NOTIFICATIONS_DID_RECEIVE_NOTIFICATION object:self userInfo:userInfo];
}


#pragma mark - Core Data

- (NSManagedObjectModel *)managedObjectModel
{
	if (_managedObjectModel != nil)
		{
        return _managedObjectModel;
		}
	
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"PersistenceModel" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

/**
 Returns the persistent store coordinator for the application.
 If the coordinator doesn't already exist, it is created and the application's store added to it.
NOTE: if DEBUG_TEST is defined in the target's build settings, the test location for the database will be used.
 */
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
    if (_persistentStoreCoordinator != nil)
		{
        return _persistentStoreCoordinator;
		}
#ifdef DEBUG_TEST
    NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"test_db.sqlite"];
#else
	NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"db.sqlite"];
#endif
    NSError *error = nil;
    _persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error])
		{

        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
		}
    
    return _persistentStoreCoordinator;
}

- (NSURL *)applicationDocumentsDirectory
{
    NSArray *searchPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentPath = [searchPaths lastObject];
	
    return [NSURL fileURLWithPath:documentPath];
}

@end
