#coding:utf-8

import urllib2
import re
import zlib
from Cartoon import *

class Gentleman:
    def __init__(self, url, path):
        exists = os.path.exists(path)
        if not exists:
            print "文件路径无效."
            exit(0)

        self.base_url = url
        self.path = path
        content = self.get_content(url)

        self.page_url_arr = self.get_page_url_arr(content)

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
            print "打开网页: " + url + "失败."
            return None

    def get_page_url_arr(self, content):
        # 每一页展示部分漫画封面,获取所有这些页面,返回包含这些页面url的数组

        # 总页数和每一页对应的url在 select 控件里,先将内容全取出来
        pattern = re.compile('name=\'sldd\'.*?>(.*?)</select>', re.S)
        result = re.search(pattern, content)
        option_list = result.groups(1)
        # print option_list

        # 再获取每一页的url
        pattern = re.compile('value=\'(.*?)\'.*?</option>', re.S)
        items = re.findall(pattern, option_list[0])

        arr = []
        for item in items:
            # print item
            page_url = self.base_url + '/' + item
            arr.append(page_url)

        # print arr
        print "total pages: " + str(len(arr))
        return arr

    def get_cartoon_arr(self, url):
        # 获取每一页所包含的漫画
        content = self.get_content(url)
        if not content:
            print "获取网页失败."
            return None

        # 先获取包含漫画信息的内容
        pattern = re.compile('class="piclist listcon".*?>(.*?)</ul>', re.S)
        result = re.search(pattern, content)
        cartoon_list = result.groups(1)

        # 再获取每部漫画的url
        pattern = re.compile('href="/shenshi/(.*?)".*?class="pic show"', re.S)
        items = re.findall(pattern, cartoon_list[0])

        arr = []
        for item in items:
            # print item
            page_url = self.base_url + '/' + item
            arr.append(page_url)

        return arr

    def hentai(self):
        # 遍历每一页的内容
        for i in range(0, len(self.page_url_arr)):
            # 获取每一页漫画的url
            cartoon_arr = self.get_cartoon_arr(self.page_url_arr[i])
            print "page " + str(i + 1) + ":"
            print cartoon_arr
            for j in range(0, len(cartoon_arr)):
                cartoon = Cartoon(cartoon_arr[j])
                cartoon.save(self.path)
            print "======= page " + str(i + 1) + " fetch finished ======="