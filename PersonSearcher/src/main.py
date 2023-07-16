# -*- coding: utf-8 -*-
import sys, os, time, threading

from config.LoggingConfig import get_logger

log = get_logger("Main")
primaryRequestHandlerThread = None
secondaryRequestHandlerThread = None

def main():
    global primaryRequestHandlerThread, secondaryRequestHandlerThread
    from handler import PrimaryRequestHandler 
    from handler import SecondaryRequestHandler 

    primaryRequestHandlerThread = threading.Thread(target=PrimaryRequestHandler.start) 
    secondaryRequestHandlerThread = threading.Thread(target=SecondaryRequestHandler.start) 
    primaryRequestHandlerThread.start()
    secondaryRequestHandlerThread.start()

def exit():
    from handler import PrimaryRequestHandler 
    from handler import SecondaryRequestHandler 

    PrimaryRequestHandler.close()
    SecondaryRequestHandler.close()

if __name__ == '__main__':
    try:
        log.info('Application is running. To exit press CTRL+C')
        main()
        while True: time.sleep(1)
    except KeyboardInterrupt:
        exit()
        log.info('Application is closed')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
