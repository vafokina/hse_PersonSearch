# -*- coding: utf-8 -*-
from configparser import RawConfigParser

class Properties:
    config : RawConfigParser = RawConfigParser()
    with open('src\\application.ini', 'r') as configfile:
        config.write(configfile)
    t = config.sections()
    #config.read('application.ini')
    a = config.get('rabbitmq',"rabbitmq.username")

    def __init__(self):
        self.config = RawConfigParser()
        self.config.read('application.ini')