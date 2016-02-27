//
//  RunningViewController.swift
//  MapPath
//
//  Created by moshuqi on 16/2/26.
//  Copyright © 2016年 msq. All rights reserved.
//

import UIKit

class RunningViewController: UIViewController, MAMapViewDelegate {
    
    @IBOutlet weak var mapView: MAMapView!
    var coordinateArray: [CLLocationCoordinate2D] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        initMapView()
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        startLocation()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return UIStatusBarStyle.LightContent
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
    @IBAction func cancel(sender: UIButton) {
        endLocation()
        dismissViewControllerAnimated(true, completion: nil)
    }
    
    @IBAction func done(sender: UIButton) {
        endLocation()
        dismissViewControllerAnimated(true, completion: nil)
    }

    func initMapView()
    {
        mapView.delegate = self
        mapView.zoomLevel = 15.5
        mapView.distanceFilter = 3.0
        mapView.desiredAccuracy = kCLLocationAccuracyBestForNavigation
    }
    
    func startLocation()
    {
        // 开始定位
        mapView.showsUserLocation = true
        mapView.userTrackingMode = MAUserTrackingMode.Follow
        mapView.pausesLocationUpdatesAutomatically = false
        mapView.allowsBackgroundLocationUpdates = true
    }
    
    func endLocation()
    {
        // 定位结束
        mapView.showsUserLocation = false
    }
    
    func updatePath () {
        
        // 每次获取到新的定位点重新绘制路径
        
        // 移除掉除之前的overlay
        let overlays = self.mapView.overlays
        self.mapView.removeOverlays(overlays)
        
        let polyline = MAPolyline(coordinates: &self.coordinateArray, count: UInt(self.coordinateArray.count))
        self.mapView.addOverlay(polyline)
        
        // 将最新的点定位到界面正中间显示
        let lastCoord = self.coordinateArray[self.coordinateArray.count - 1]
        self.mapView.setCenterCoordinate(lastCoord, animated: true)
    }
    
    // MARK: MAMapViewDelegate
    
    
    func mapView(mapView: MAMapView, didUpdateUserLocation userLocation: MAUserLocation, updatingLocation: Bool)
    {
        // 地图每次有位置更新时的回调
        
        if updatingLocation {
            // 获取新的定位数据
            let coordinate = userLocation.coordinate
            
            // 添加到保存定位点的数组
            self.coordinateArray.append(coordinate)
           
            updatePath()
        }
    }
    
    func mapView(mapView: MAMapView!, viewForOverlay overlay: MAOverlay!) -> MAOverlayView! {
        if overlay.isKindOfClass(MAPolyline) {
            let polylineView = MAPolylineView(overlay: overlay)
            polylineView.lineWidth = 6
            polylineView.strokeColor = UIColor(red: 4 / 255.0, green:  181 / 255.0, blue:  108 / 255.0, alpha: 1.0)
            
            return polylineView
        }
        
        return nil
    }
}
