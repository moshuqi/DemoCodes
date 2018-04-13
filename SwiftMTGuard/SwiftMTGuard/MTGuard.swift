//
//  MTGuard.swift
//  SwiftMTGuard
//
//  Created by HD on 2017/9/14.
//  Copyright © 2017年 msq. All rights reserved.
//

import UIKit

class MTGuard {
    public func hook() {
        let obj_class = objc_getClass("UIView");
        let cls: AnyClass = obj_class as! AnyClass
        
        var ignoreMethods: [String] = ["retain", "release", "dealloc", ".cxx_destruct"];
        
        var propertyCount:UInt32 = 0
        let properties: UnsafeMutablePointer<objc_property_t?> = class_copyPropertyList(cls, &propertyCount)
        for index in 0..<propertyCount {
            let p: objc_property_t = properties[Int(index)]!;
            let pName: UnsafePointer<Int8> = property_getName(p)
            let name = String(cString: pName)
            ignoreMethods.append(name)
            
            print(name)
        }
        free(properties)
        
        var methodCount:UInt32 = 0
        let methodList: UnsafeMutablePointer<Method?> = class_copyMethodList(cls, &methodCount);
        for index in 0..<methodCount {
            let m: Method = methodList[Int(index)]!;
            let name: String = NSStringFromSelector(method_getName(m))
            
            if !name.hasPrefix("_") {
                var needIgnore = false
                for ignoreMethod in ignoreMethods {
                    if name == ignoreMethod {
                        needIgnore = true;
                        continue;
                    }
                }
                
                if (!needIgnore) {
                    replace(cls: cls, methodName: name)
                }
            }
            print(name)
        }
        free(methodList)
        
        print("xixi")
    }
    
    func replace(cls: AnyClass, methodName: String) {
        let selector: Selector = NSSelectorFromString(methodName)
        
        let method: Method = class_getInstanceMethod(cls, selector)
        let typeDescription = method_getTypeEncoding(method)
        
        let originalImp: IMP = class_getMethodImplementation(cls, selector)
        let msgForwardImp: IMP = class_getMethodImplementation(cls, NSSelectorFromString("notExistMethodName"))
        
        class_replaceMethod(cls, selector, msgForwardImp, typeDescription);
        print("dd")
    }
    
    override func
}
