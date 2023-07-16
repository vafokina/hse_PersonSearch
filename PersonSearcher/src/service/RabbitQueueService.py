# -*- coding: utf-8 -*-
import pika

from config.LoggingConfig import get_logger

class RabbitQueueService:

    USER = "admin"
    PASS = "admin"
    HOST = "localhost"
    PORT = 5672
    FIRST_REQUEST_QUEUE_NAME = "request_queue"
    SECOND_REQUEST_QUEUE_NAME = "report_queue"

    log = get_logger("RabbitQueueService")

    connection = None
    channel = None

    def __init__(self):
        try:
            self.log.info("Start connection to RabbitMQ")
            credentials = pika.PlainCredentials(self.USER, self.PASS)
            self.connection = pika.BlockingConnection(pika.ConnectionParameters(host=self.HOST, port=self.PORT, credentials=credentials))
            self.channel = self.connection.channel()

            self.channel.queue_declare(queue=self.FIRST_REQUEST_QUEUE_NAME, durable=True)
            self.channel.queue_declare(queue=self.SECOND_REQUEST_QUEUE_NAME, durable=True)
        except Exception as ex:
            self.log.error(str(type(ex)) + " : " + str(ex))

    def __enter__(self):
        return self

    def __exit__(self):
        try:
            self.log.info("Close connection to RabbitMQ")
            self.channel.close()
            self.connection.close()
        except Exception as ex:
            self.log.error(str(type(ex)) + " : " + str(ex))
    