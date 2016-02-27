//
//  ViewController.swift
//  MapPath
//
//  Created by moshuqi on 16/2/26.
//  Copyright © 2016年 msq. All rights reserved.
//

import UIKit

class ViewController: UIViewController, MAMapViewDelegate{
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func run(sender : AnyObject) {
        let runningViewController = RunningViewController()
        presentViewController(runningViewController, animated: true, completion: nil)
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
