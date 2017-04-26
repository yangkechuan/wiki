# -*- coding: UTF-8 -*-

"""
    tinypng
    developers api:https://tinypng.com/developers
    python api:https://tinypng.com/developers/reference/python
"""

try:
    import tinify
except Exception as e:
    raise e
import os

tinify.key = 'LXhAob_Lp3kVZznpVI6nbNrLqbP7zzWY'

path = os.getcwd()
allow_suffix = ['.png', '.jpg']


def tiny_png(path):
    for file in os.listdir(path):
        if os.path.isfile(file):
            file_name, file_suffix = os.path.splitext(file)
            if file_suffix in allow_suffix:
                with open(file, 'rb') as f:
                    try:
                        # 二进制上传
                        # f_data = f.read()
                        # result_data = tinify.from_buffer(f_data).to_buffer()

                        #本地上传
                        tinify.from_file(file).to_file(file)
                        print(file + ' success')
                    # 到达上限
                    except tinify.errors.AccountError as e:
                        raise e
                    # 连接错误
                    except tinify.errors.ClientError:
                        pass
                    # 服务端问题
                    except tinify.errors.ServerError as e:
                        pass
                    # 其他异常
                    except Exception:
                        pass

if __name__ == '__main__':
    try:
        #验证key
        tinify.validate
        tiny_png(path=path)
    except tinify.Error as e:
        raise e
