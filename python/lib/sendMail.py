#!/usr/bin/env python
# -*-coding:UTF-8

import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

author = "夜微凉"


"""
    1.用户名和密码根据自己情况去输入
    2.可以发送附件,抄送邮件
"""

usr = "*********"
password = "*********"
to = "*********"
# _cc = "*********"

msg = MIMEMultipart()
msg["Subject"] = "Subject"
msg["From"] = usr
msg["To"] = to
# msg['Cc'] = _cc
msg["Accept-Language"] = "zh-CN"
msg["Accept-Charset"] = "ISO-8859-1,utf-8"


'正文'
part = MIMEText('<h1>hello python</h1>', 'html')
msg.attach(part)


'附件'
# part = MIMEApplication(open('file_name','rb').read())
# part.add_header('Content-Disposition','attachment',filename = 'file_name')
# msg.attach(part)


def send_mail():
    """端口默认25
    SMTP根据自己的邮箱类型去输入
    :return: void
    """
    s = smtplib.SMTP("******", timeout=30)
    s.login(usr, password)
    s.sendmail(usr, to, msg.as_string())
    s.close()

if __name__ == '__main__':
    send_mail()

