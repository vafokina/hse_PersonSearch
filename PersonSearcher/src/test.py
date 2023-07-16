import datetime
import pika, sys, os, json, face_recognition

from service.DataBaseService import DataBaseService

def main():
  db = DataBaseService()
  

  db.__exit__()
  pass

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)