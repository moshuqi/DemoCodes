#coding:utf-8

import urllib2
import re
import zlib
import os

class Cartoon:
    def __init__(self, url):
        self.base_url = "http://www.xeall.com/shenshi"
        self.url = url

        content = self.get_content(self.url)
        if not content:
            print "Cartoon init failed."
            return

        self.title = self.get_title(content)
        self.page_url_arr = self.get_page_url_arr(content)

        # 标记每次下载图片时,是否先检查本地已存在对应图片
        self.need_check_pic = False


    def get_content(self, url):
        # 打开网页
        try:
            request = urllib2.Request(url)
            response = urllib2.urlopen(request, timeout=20)

            # 将网页内容解压缩
            decompressed_data = zlib.decompress(response.read(), 16 + zlib.MAX_WBITS)

            # 网页编码格式为 gb2312
            content = decompressed_data.decode('gb2312', 'ignore')
            # print content
            return content
        except Exception, e:
            print e
            print "open url: " + url + " failed."
            return None

    def get_title(self, content):
        # 获取漫画名称
        pattern = re.compile('name="keywords".*?content="(.*?)".*?/', re.S)
        result = re.search(pattern, content)

        if result:
            title = result.groups(1)
            print "title: " + title[0]
            return title[0]
        else:
            print "获取标题失败。"
            return None

    def get_page_url_arr(self, content):
        # 获取包含每一页漫画url的数组
        pattern = re.compile('class="pagelist">(.*?)</ul>', re.S)
        result = re.search(pattern, content)
        page_list = result.groups(1)

        pattern = re.compile('<a href=\'(.*?)\'>.*?</a>', re.S)
        items = re.findall(pattern, page_list[0])

        arr = []
        for item in items:
            page_url = self.base_url + "/" + item
            arr.append(page_url)
            # print item

        # pagelist中还包含了上一页和下一页，根据网页格式可知分别在开始和结束，所以去掉首尾元素避免重复
        arr.pop(0)
        arr.pop(0)
        arr.pop(len(arr) - 1)
        print arr
        print self.title + " total pages: " + str(len(arr))
        return arr

    def get_pic_url(self, page_url):
        # 获取每一页中图片的url
        content = self.get_content(page_url)
        if not content:
            return None

        pattern = re.compile('<img alt.*?src="(.*?)".*?/>', re.S)
        result = re.search(pattern, content)

        if result:
            pic = result.groups(1)
            # print  "Picture url: " + pic[0]
            return pic[0]
        else:
            print "获取图片地址失败。"
            print "url: " + page_url
            return None

    def save(self, path):
        dir_path = path + "/" + self.title
        self.create_dir_path(dir_path)

        # 判断是否已经下载过
        list = os.listdir(dir_path)
        if len(list) >= len(self.page_url_arr):
            print self.title + " has been downloaded."
            return

        # 获取图片时会偶尔出现请求超时的情况,会导致一部漫画存在部分缺失,此时文件夹中已存在大部分图片
        # 当前已存在图片大于一定数量时判定为存在少数缺页情况,这时候通过判断只对未存在图片进行请求
        if len(list) >= (len(self.page_url_arr) / 2):
            print "每张图片下载前先检查本地是否已存在."
            self.need_check_pic = True

        for i in range(0, len(self.page_url_arr)):
            page_url = self.page_url_arr[i]
            pic_url = self.get_pic_url(page_url)
            if pic_url == None:
                continue

            pic_path = dir_path + "/" + str(i + 1) + ".jpg"
            if (self.need_check_pic):
                exists = os.path.exists(pic_path)
                if exists:
                    print "pic: " + pic_url + " exists."
                    continue

            self.save_pic(pic_url, pic_path)

        print self.title + " fetch finished."

    def create_dir_path(self, path):
        # 以漫画名创建文件夹
        exists = os.path.exists(path)
        if not exists:
            print "创建文件夹"
            os.makedirs(path)
        else:
            print "文件夹已存在"

    def save_pic(self, pic_url, path):
        # 将图片保存到指定文件夹中
        req = urllib2.Request(pic_url)
        req.add_header('User-Agent', 'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36')
        req.add_header('GET', pic_url)

        try:
            print "save pic url:" + pic_url
            resp = urllib2.urlopen(req, timeout=20)
            data = resp.read()
            # print data

            fp = open(path, "wb")
            fp.write(data)
            fp.close
            print "save pic finished."
        except Exception, e:
            print e
            print "save pic: " + pic_url + " failed."



