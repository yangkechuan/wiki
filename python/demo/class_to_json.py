#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json

author = '夜微凉'

'''
--------------------------------------
            class转json
--------------------------------------
'''


"""
简单情况：
1.先定义一个类
2.实例化类
3.通过class.__dict__方法把class转成dict
4.通过json库的dumps方法把dict转成json
"""


class User:
    def __init__(self, name, age):
        """
        转换后的格式：
        {
            "age": 10,
            "name": "user"
        }
        :param name: name
        :param age:  age
        """
        self.name = name
        self.age = age

user = User('user', 10)
dict_user = user.__dict__
json_user = json.dumps(dict_user, indent=4)
print(json_user)


"""
复杂情况：
json如果有多层结构
例如外层的address也是一个json，则新建一个Address类
同时Address中的phone是一个json数组，则新建一个Phone类
"""


class Customer:
    def __init__(self, name, grade, age, home, office, num):
        """
        转换后的格式：
        {
            "address": {
                "home": "111",
                "office": "aaa",
                "phone": [
                    {
                        "num": "1234567"
                    }
                ]
            },
            "age": 15,
            "grade": "A",
            "name": "john"
        }
        :param name: name
        :param grade: grade
        :param age: age
        :param home: home
        :param office: office
        :param num: num
        """
        self.name = name
        self.grade = grade
        self.age = age
        self.address = Address(home, office, num)


class Address:
    def __init__(self, home, office, num):
        self.home = home
        self.office = office
        self.phone = [Phone(num)]


class Phone:
    def __init__(self, num):
        self.num = num

customer = Customer('john', 'A', 15, '111', 'aaa', '1234567')
json_str = json.dumps(customer, default=lambda o: o.__dict__, sort_keys=True, indent=4)
print(json_str)
