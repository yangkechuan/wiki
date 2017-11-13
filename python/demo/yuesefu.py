#!/usr/bin/env python
# -*- coding:utf-8 -*-

author = "夜微凉"

"""
Python实现约瑟夫环

用for遍历lists, 同时设定一个游标，当游标为7时把该数加到删除列表中
在一次循环结束后，计算新的lists数据和长度

注意点：
1.如果在for循环中修改count的值，会导致先判断while,致使for循环重新开始
2.如果判断要删除的数值之后直接在for循环中删除，会导致循环索引偏差，所以采用一个删除数组
3.lists和del_list如果采用求差集的方式，注意最后的lists数据顺序是否符合预期
"""


def yue_se_fu(lists):
    count = len(lists)
    del_list = []
    if count <= 1:
        return lists
    cursor = 0
    while count > 1:
        for i in lists:
            cursor += 1
            if cursor % 7 == 0:
                del_list.append(i)
                cursor = 0
        lists = [i for i in lists if i not in del_list]
        print(lists)
        print(cursor)
        count = len(lists)
    return lists, del_list, len(del_list)


L = list(range(1, 51))
print(yue_se_fu(L))
