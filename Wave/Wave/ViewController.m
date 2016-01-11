//
//  ViewController.m
//  Wave
//
//  Created by moshuqi on 16/1/7.
//  Copyright © 2016年 msq. All rights reserved.
//

#import "ViewController.h"
#import "YSWaterWaveView.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    CGFloat d = 160;
    CGRect rect = CGRectMake(0, 0, d, d);
    YSWaterWaveView *waterWaveView = [[YSWaterWaveView alloc] initWithFrame:rect];
    
    waterWaveView.center = self.view.center;
    waterWaveView.layer.cornerRadius = d / 2;
    waterWaveView.clipsToBounds = YES;
    
    [self.view addSubview:waterWaveView];
    
    [waterWaveView startWaveToPercent:0.2];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
