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
# print(json_user)


"""
复杂情况：
json如果有多层结构
例如外层的address也是一个json，则新建一个Address类
同时Address中的phone是一个json数组，则新建一个Phone类

**Address和Phone可以是外部类，也可以是内部类**
"""


class Customer:
    def __init__(self, name, grade, age, home=None, office=None, num=None):
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
        self.address = self.Address(home, office, num)

    def __str__(self):
        return 'customer name:{name}, grade:{grade}, age:{age}, home:{home}, office:{office}, num:{num} '.\
            format(name=self.name, grade=self.grade, age=self.age, home=self.address.home, office=self.address.office
                   , num=self.address.phone[0].num)
    __repr__ = __str__

    class Address:
        def __init__(self, home, office, num):
            self.home = home
            self.office = office
            self.phone = [self.Phone(num)]

        def __str__(self):
            return 'address home:{home}, office:{office}, num:{num}'.format(home=self.home, office=self.office
                                                                            , num=self.phone[0].num)
        __repr__ = __str__

        class Phone:
            def __init__(self, num):
                self.num = num

            def __str__(self):
                return 'phone num:{num}'.format(num=self.num)
            __repr__ = __str__


customer = Customer('john', 'A', 15, 'home', 'office', 1234567)
json_str = json.dumps(customer, default=lambda o: o.__dict__, sort_keys=True, indent=4)
print(json_str)
