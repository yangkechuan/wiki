# -*- coding:utf-8 -*-

__author__ = '夜微凉'

import logging


# create logger and set global level
logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

formatter = logging.Formatter(fmt='[%(asctime)s] %(levelname)s [%(filename)s : %(funcName)s, %(lineno)d] %(message)s',
                              datefmt='%Y-%m-%d %H:%M:%S')

# terminal log
handle_term = logging.StreamHandler()
handle_term.setLevel(logging.DEBUG)
handle_term.setFormatter(formatter)

# file log
log_file_name = 'test.log'
handle_file = logging.FileHandler(log_file_name)
handle_file.setLevel(logging.DEBUG)
handle_file.setFormatter(formatter)

logger.addHandler(handle_term)
logger.addHandler(handle_file)


if __name__ == '__main__':
    logger.info('info')
    logger.debug('debug')
    logger.warning('warning')
    logger.error('error')
