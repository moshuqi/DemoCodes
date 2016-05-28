//
//  ViewController.m
//  AutoTest
//
//  Created by moshuqi on 16/5/28.
//  Copyright © 2016年 msq. All rights reserved.
//

#import "ViewController.h"
#import "MyButton.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self addButtons];
}

- (void)addButtons
{
    CGFloat w = CGRectGetWidth(self.view.frame);
    CGFloat h = CGRectGetHeight(self.view.frame);
    
    CGFloat distance = 20;      // 按钮之间、按钮和屏幕边缘之间的间距
    NSInteger rowCount = 9;    // 行数
    NSInteger columnCount = 5;  // 列数
    
    CGFloat btnW = (w - distance * (columnCount + 1)) / columnCount;    // 按钮宽度
    CGFloat btnH = (h - distance * (rowCount + 1)) / rowCount;          // 按钮高度
    
    // 将按钮添加到界面上
    for (NSInteger i = 0; i < (rowCount * columnCount); i++)
    {
        CGFloat x = (i % columnCount) * (btnW + distance) + distance;
        CGFloat y = (i / columnCount) * (btnH + distance) + distance;
        CGRect frame = CGRectMake(x, y, btnW, btnH);
        
        MyButton *btn = [[MyButton alloc] init];
        btn.frame = frame;
        
        [btn addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:btn];
    }
}

- (void)clickBtn:(id)sender
{
    MyButton *btn = (MyButton *)sender;
    [btn click];
}

@end
