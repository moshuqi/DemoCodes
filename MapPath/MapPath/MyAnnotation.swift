//
//  MyAnnotation.swift
//  MapPath
//
//  Created by moshuqi on 16/2/27.
//  Copyright © 2016年 msq. All rights reserved.
//

import Foundation

class MyAnnotation: NSObject, MAAnnotation {
    
    var coordinate: CLLocationCoordinate2D {
        get {
            return self.coordinate
        }
        set(newCoord) {
            self.coordinate = newCoord
        }
    }
    
    init(coord: CLLocationCoordinate2D) {
        super.init()
        self.coordinate = coord
        
    }
    
    
}
