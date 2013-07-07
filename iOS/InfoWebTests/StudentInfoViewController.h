//
//  StudentInfoViewController.h
//  InfoWebTests
//
//  Created by Christos Sotiriou on 6/16/13.
//  Copyright (c) 2013 Oramind. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface StudentInfoViewController : UIViewController
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UILabel *lastNameLabel;
@property (strong, nonatomic) IBOutlet UILabel *emailLabel;
@property (strong, nonatomic) IBOutlet UILabel *devicesLabel;
@property (strong, nonatomic) IBOutlet UILabel *messagesCountLabel;
@property (strong, nonatomic) IBOutlet UIView *networkActivityView;

@end
