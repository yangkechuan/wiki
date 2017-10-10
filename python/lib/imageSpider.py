#!/usr/bin/env python
# -*- coding:utf-8 -*-

import requests
import re
import urllib

author = "夜微凉"

header = """
--------------------------------------
       python 抓图
1.抓取百度图片
2.地址：http://image.baidu.com/
3.任意搜索后，复制地址输入到脚本
4.把高清图下载到当前路径下
5.百度是ajax加载,只抓取第一页
--------------------------------------
"""

print(header)

count = 0


def get_urls(url):
    """url为百度图片下的地址
    默认下载的是高清图
    url格式类似于:
        https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&sf=2&fmq=1480332039000_R_D&pv=&ic=0&nc=1&z=&se=&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=%E5%BE%AE%E8%B7%9D%E6%91%84%E5%BD%B1
    """
    headers = {'User-agent': 'Mozilla/5.0 (Windows NT 6.2; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0'}
    text = requests.get(url, headers=headers).text
    pic_url = re.compile(r'\"objURL\":\"(http.*?jpg)\"')
    pict_urls = pic_url.findall(text)
    n = 0
    for url in pict_urls:
        download_image(url, str(n)+'.jpg')
        n += 1
    print("共下载图片" + str(count) + "张")


def download_image(url, name):
    try:
        global count
        print("正在下载：" + url)
        urllib.request.urlretrieve(url, name, schedule)
        count += 1
    except:
        pass


def schedule(a, b, c):
    """
    :param a: 已经下载的数据块
    :param b: 数据块的大小
    :param c: 远程文件的大小
    :return: null
    """
    per = 100.0 * a * b / c
    if per > 100:
        per = 100
    per = round(per, 2)
    print('当前下载进度：' + str(per))


if __name__ == '__main__':
    print('请输入网址:')
    urls = input()
    get_urls(urls)

end = """
--------------------------------------
            运行结束
            感谢使用
            author:夜微凉
--------------------------------------
"""

print(end)
