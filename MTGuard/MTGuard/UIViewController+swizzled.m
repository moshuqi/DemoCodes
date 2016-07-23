//
//  UIViewController+swizzled.m
//  MTGuard
//
//  Created by moshuqi on 16/7/23.
//  Copyright © 2016年 msq. All rights reserved.
//

#import "UIViewController+swizzled.h"
#import <objc/runtime.h>

@implementation UIViewController (swizzled)

+ (void)load
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        Class class = [self class];
        
        SEL originalSelector = @selector(viewWillAppear:);
        SEL swizzledSelector = @selector(swizzled_viewWillAppear);
        
        Method originalMethod = class_getInstanceMethod(class, originalSelector);
        Method swizzledMethod = class_getInstanceMethod(class, swizzledSelector);
        
        BOOL didAddMethod =
        class_addMethod(class,
                        originalSelector,
                        method_getImplementation(swizzledMethod),
                        method_getTypeEncoding(swizzledMethod));
        
        if (didAddMethod)
        {
            class_replaceMethod(class,
                                swizzledSelector,
                                method_getImplementation(originalMethod),
                                method_getTypeEncoding(originalMethod));
        }
        else
        {
            method_exchangeImplementations(originalMethod, swizzledMethod);
        }

    });
}

- (void)swizzled_viewWillAppear
{
    NSLog(@"swizzled the method!");
    
    [self swizzled_viewWillAppear];
}


@end
