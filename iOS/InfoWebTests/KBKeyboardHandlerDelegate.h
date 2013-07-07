//
//  KBKeyboardHandlerDelegate.h
//  InfoWebTests
//
//  Created by Christos Sotiriou on 4/20/13.
//  Copyright (c) 2013 Oramind. All rights reserved.
// http://stackoverflow.com/questions/1775860/uitextfield-move-view-when-keyboard-appears

#import <Foundation/Foundation.h>

@protocol KBKeyboardHandlerDelegate
- (void)keyboardSizeChanged:(CGSize)delta;

@end