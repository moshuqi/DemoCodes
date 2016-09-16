#coding:utf-8

from Cartoon import *
from Gentleman import *

# cartoon = Cartoon("http://www.xeall.com/shenshi/6895.html")
# cartoon.save("/Users/moshuqi/Desktop/test")

# http://www.xeall.com/ribenmanhua/
url = "http://www.xeall.com/shenshi"

# enter your path
save_path = ""

gentleman = Gentleman(url, save_path)
gentleman.hentai()




