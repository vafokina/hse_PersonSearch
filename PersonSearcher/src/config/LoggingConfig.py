# -*- coding: utf-8 -*-
import logging

def get_logger(name = None):
    logger = logging.getLogger(name)
    logger.setLevel(logging.DEBUG)
 
    h = logging.StreamHandler()
    fmt = '%(asctime)s - %(levelname)s - [%(threadName)s] %(name)s : %(message)s'
    formatter = logging.Formatter(fmt)
    h.setFormatter(formatter)
 
    logger.addHandler(h)
    return logger