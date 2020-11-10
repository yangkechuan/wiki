# -*- coding:utf-8 -*-

"""
求 100 以内的素数

python for... else... 用法：

for x in range(5):
    if x == 2:
        print(2)
        break
else:
    print('run else')

上述代码，如果缺少 break 时，执行结果：

    >>2
    >>run else

当具备 break 条件时，执行结果：
    >>2


即：当迭代完成后，没有 break , 则执行后续 else ，如果迭代对象提前退出迭代， else 不会被执行
"""


def prime_number():
    count = 0
    for i in range(2, 100):
        for j in range(2, i):
            if i % j == 0:
                break
        else:
            count += 1
            print(i)

    print('1 ~ 100 共有素数 {} 个'.format(count))


if __name__ == '__main__':
    prime_number()
