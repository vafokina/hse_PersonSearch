# -*- coding: utf-8 -*-
import io, json, requests, face_recognition

from service.RabbitQueueService import RabbitQueueService
from config.LoggingConfig import get_logger

NO_PERSON_ERROR = "no_person_error"
MORE_THAN_ONE_PERSON_ERROR = "more_than_one_person_error"
UNEXPECTED_SERVER_ERROR = "unexpected_server_error"

queueService = RabbitQueueService()
firstQueueName = queueService.FIRST_REQUEST_QUEUE_NAME
secondQueueName = queueService.SECOND_REQUEST_QUEUE_NAME
log = get_logger("PrimaryRequestHandler")

def callback(ch, method, properties, body):
    log.debug("Received message from " + firstQueueName + ": " + body.decode())
    obj = json.loads(body.decode())
    imageUrl = obj["imageUrl"]
    uuid = obj["uuid"]

    try:
        response = requests.get(imageUrl)
        image_to_encode = face_recognition.load_image_file(io.BytesIO(response.content))
        encodings = face_recognition.face_encodings(image_to_encode)

        error = None
        if len(encodings) == 0:
            error = NO_PERSON_ERROR
        elif len(encodings) > 1:
            error = MORE_THAN_ONE_PERSON_ERROR
        if error:
            sendError(uuid, error)
            return

        response = {
            'uuid': uuid,
            'imageUrl': imageUrl,
            'encoding': encodings[0].tolist()
        }
        send(secondQueueName, response)

    except Exception as ex:
        sendError(uuid, UNEXPECTED_SERVER_ERROR)
        log.error(str(type(ex)) + " : " + str(ex))
    finally:
        ch.basic_ack(delivery_tag = method.delivery_tag)

def sendError(uuid, error):
    response = {
        'uuid': uuid,
        'errorCode': error
    }
    send(uuid, response)

def send(queueName, response):
    try:
        str = json.dumps(response)
        log.debug("Sending message to " + queueName + ": " + str)
        queueService.channel.basic_publish(exchange='', routing_key=queueName, body=str)
    except Exception as ex:
        log.error(str(type(ex)) + " : " + str(ex))

def start():
    global queueService
    log.debug("Start listening to the " + firstQueueName)
    try:
        queueService.channel.basic_consume(queue=firstQueueName, on_message_callback=callback)
        queueService.channel.start_consuming()
    except Exception as ex:
        log.error(str(type(ex)) + " : " + str(ex))

def close():
    global queueService
    queueService.__exit__()