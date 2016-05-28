//
//  MyButton.m
//  AutoTest
//
//  Created by moshuqi on 16/5/28.
//  Copyright © 2016年 msq. All rights reserved.
//

#import "MyButton.h"

@interface MyButton ()

@property (nonatomic, assign) NSInteger count;

@end

@implementation MyButton

- (id)init
{
    self = [super init];
    if (self)
    {
        self.count = 0;
        [self setTitle:[NSString stringWithFormat:@"%@", @(self.count)] forState:UIControlStateNormal];
        [self setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    }
    
    return self;
}

- (void)click
{
    self.count ++;
    [self setTitle:[NSString stringWithFormat:@"%@", @(self.count)] forState:UIControlStateNormal];
}

@end
