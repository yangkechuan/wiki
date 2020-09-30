#!/usr/bin/env python
# -*- coding:utf-8 -*-

author = "夜微凉"

"""
--------------------------------------
       rsa模块的加签验签、加密解密
1.需要安装rsa模块
2.rsa用私钥加签，公钥验签
3.rsa用公钥加密，私钥解密
4.生成的加密、加签数据为bytes，需要通过base64转为str
--------------------------------------
"""
try:
    import rsa
except Exception:
    raise Exception('请安装rsa模块')
import base64


class RsaUtil:
    @staticmethod
    def sign(message, private_key, hash='SHA-1'):
        """RSA签名
        :param message: str 要加密的信息
        :param private_key: 私钥
        :param hash: 加密方式
        :return: str 加密后的信息
        """
        encode_msg = message.encode()
        sign_msg = rsa.sign(message=encode_msg
                            , priv_key=private_key
                            , hash=hash)
        return (base64.b64encode(sign_msg)).decode('utf-8')

    @staticmethod
    def verify(message, signature, public_key):
        """验签
        :param message: 原始msg，需要encode('utf-8')编码
        :param signature: type : block，传入str,需要转码
        :param public_key: 公钥
        :return: bool
        """
        signature_msg = base64.b64decode(signature.encode('utf-8'))
        verify_msg = rsa.verify(message=message.encode('utf-8')
                                , signature=signature_msg
                                , pub_key=public_key)
        return verify_msg

    @staticmethod
    def news_keys(length=1024, raw=False):
        """生产一对公私钥
        :param length: 生成长度
        :param raw: 是否是rsa模块生成的原始数据格式，默认为False
        :return: tuple
        """
        (public_key, private_key) = rsa.newkeys(length)
        if raw is False:
            public_key = public_key.save_pkcs1().decode()
            private_key = private_key.save_pkcs1().decode()
        return public_key, private_key

    @staticmethod
    def save_private_key(private_key, private_key_filename='private_key.pem'):
        """
        保存私钥
        :param private_key: 私钥,rsa模块生成的原始私钥
        :param private_key_filename: 文件名
        :return: null
        """
        with open(private_key_filename, 'w+') as f:
            f.write(private_key.save_pkcs1().decode())

    @staticmethod
    def save_public_key(public_key, public_key_filename='public_key.pem'):
        """
        保存公钥
        :param public_key: 公钥，rsa模块生成的原始私钥
        :param public_key_filename: 文件名
        :return: null
        """
        with open(public_key_filename, 'w+') as f:
            f.write(public_key.save_pkcs1().decode())

    @staticmethod
    def load_private_key(private_key_filename='private_key.pem'):
        """
        加载文件中的私钥
        :param private_key_filename: 
        :return: class
        """
        with open(private_key_filename, 'rb') as f:
            private_key = rsa.PrivateKey.load_pkcs1(f.read())
        return private_key

    @staticmethod
    def load_public_key(public_key_filename='public_key.pem'):
        """
        加载文件中的公钥
        :param public_key_filename: 
        :return: class
        """
        with open(public_key_filename, 'rb') as f:
            public_key = rsa.PublicKey.load_pkcs1(f.read())
        return public_key

    @staticmethod
    def encrypt(message, public_key, raw=False):
        """
        加密
        :param message: str 要加密的信息
        :param public_key: 公钥
        :param raw: 是否返回原始数据，默认False,返回str
        :return: str or bytes
        """
        encode_msg = message.encode()
        crypto_msg = rsa.encrypt(encode_msg, public_key)
        if raw is False:
            crypto_msg = (base64.b64encode(crypto_msg)).decode('utf-8')
        return crypto_msg

    @staticmethod
    def decrypt(crypto, private_key, raw=False):
        """
        解密
        :param crypto: str or bytes ,要解密的信息，如果raw为False,此项为str
        :param private_key: 私钥
        :param raw: crypto是否为原始数据格式
        :return: str
        """
        if raw is False:
            crypto = base64.b64decode(crypto.encode('utf-8'))
        message = rsa.decrypt(crypto, private_key)
        return message.decode()


if __name__ == '__main__':
    """
        使用示例
        1.生成一对新的公私钥
        2.保存
        3.加载
        4.加密、解密
        5.加签、验签
    """
    public_key_n, private_key_n = RsaUtil.news_keys(raw=True)
    RsaUtil.save_public_key(public_key_n)
    RsaUtil.save_private_key(private_key_n)

    public_key = RsaUtil.load_public_key()
    private_key = RsaUtil.load_private_key()

    msg = 'hello'
    crypto = RsaUtil.encrypt(msg, public_key)
    print(crypto)

    message = RsaUtil.decrypt(crypto, private_key)
    print(message)

    sign_msg = RsaUtil.sign(msg, private_key)
    print(sign_msg)

    print(RsaUtil.verify(msg, sign_msg, public_key))
