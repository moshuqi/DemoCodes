//
//  MainThreadGuard.m
//  MTGuard
//
//  Created by moshuqi on 16/7/23.
//  Copyright © 2016年 msq. All rights reserved.
//

#import "MainThreadGuard.h"
#import <objc/runtime.h>
#import <objc/message.h>

@implementation MainThreadGuard

static void replace(Class cls, NSString *selectorName);
static void myForwardInvocation(id slf, SEL selector, NSInvocation *invocation);

+ (void)load
{
#if defined (MAIN_THREAD_GUARD_CRASH)
    // replace method
    
    id objc_class = objc_getClass("UIView");
    Class class = [objc_class class];
    
    NSMutableArray *ignoreMethods = [NSMutableArray arrayWithArray:@[@"retain", @"release", @"dealloc", @".cxx_destruct"]];
    
    unsigned int propertyCount = 0;
    objc_property_t *properties = class_copyPropertyList(class, &propertyCount);
    
    for(int i = 0; i < propertyCount; i++)
    {
        objc_property_t property = properties[i];
        [ignoreMethods addObject:@(property_getName(property))];
    }
    free(properties);
    
    unsigned int methodCount = 0;
    Method *methodList = class_copyMethodList(class, &methodCount);
    
    for (int i = 0; i < methodCount; i++)
    {
        Method method = methodList[i];
        NSString *methodName = NSStringFromSelector(method_getName(method));
        
        if (![methodName hasPrefix:@"_"])
        {
            BOOL needIgnore = NO;
            for (NSString *ignoreMethod in ignoreMethods) {
                if ([methodName isEqualToString:ignoreMethod]) {
                    needIgnore = YES;
                    continue;
                }
            }
            
            if (!needIgnore)
            {
                replace(class, methodName);
            }
        }
    }
    
    free(methodList);
#endif
}

static void replace(Class cls, NSString *selectorName)
{
    SEL selector = NSSelectorFromString(selectorName);
    
    Method method = class_getInstanceMethod(cls, selector);
    const char *typeDescription = (char *)method_getTypeEncoding(method);
    
    IMP originalImp = class_getMethodImplementation(cls, selector);
    IMP msgForwardIMP = _objc_msgForward;
    
    if (typeDescription[0] == '{')
    {
        NSMethodSignature *methodSignature = [NSMethodSignature signatureWithObjCTypes:typeDescription];
        if ([methodSignature.debugDescription rangeOfString:@"is special struct return? YES"].location != NSNotFound) {
            msgForwardIMP = (IMP)_objc_msgForward_stret;
        }
    }
    
    class_replaceMethod(cls, selector, msgForwardIMP, typeDescription);
    
    if (class_getMethodImplementation(cls, @selector(forwardInvocation:)) != (IMP)myForwardInvocation)
    {
        class_replaceMethod(cls, @selector(forwardInvocation:), (IMP)myForwardInvocation, typeDescription);
    }
    
    if (class_respondsToSelector(cls, selector))
    {
        NSString *originalSelectorName = [NSString stringWithFormat:@"ORIG_%@", selectorName];
        SEL originalSelector = NSSelectorFromString(originalSelectorName);
        if(!class_respondsToSelector(cls, originalSelector))
        {
            class_addMethod(cls, originalSelector, originalImp, typeDescription);
        }
    }
}

static void myForwardInvocation(id slf, SEL selector, NSInvocation *invocation)
{
    if (![NSThread currentThread].isMainThread)
    {
        NSLog(@"%@",[NSThread callStackSymbols]);
        
        NSMutableArray *array = [NSMutableArray array];
        [array addObject:nil];
    }
    
    NSString *selectorName = NSStringFromSelector(invocation.selector);
    NSString *origSelectorName = [NSString stringWithFormat:@"ORIG_%@", selectorName];
    SEL origSelector = NSSelectorFromString(origSelectorName);
    
    invocation.selector = origSelector;
    [invocation invoke];
}


@end
