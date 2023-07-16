# -*- coding: utf-8 -*-
import json, face_recognition, numpy
from datetime import datetime

from service.DataBaseService import DataBaseService
from service.RabbitQueueService import RabbitQueueService
from config.LoggingConfig import get_logger

UNEXPECTED_SERVER_ERROR = "unexpected_server_error"

queueService = RabbitQueueService()
dbService = DataBaseService()
secondQueueName = queueService.SECOND_REQUEST_QUEUE_NAME
log = get_logger("SecondaryRequestHandler")

def callback(ch, method, properties, body):
    log.debug("Received message from " + secondQueueName + ": " + body.decode())
    obj = json.loads(body.decode())
    uuid = obj["uuid"]
    imageUrl = obj["imageUrl"]
    encoding = obj["encoding"]

    try:
        if not searchInReports(uuid, getEncoding(encoding)):
            makeReport(uuid, encoding, getEncoding(encoding), imageUrl)

    except Exception as ex:
        sendError(uuid, UNEXPECTED_SERVER_ERROR)
        log.error(str(type(ex)) + " : " + str(ex))
    finally:
        ch.basic_ack(delivery_tag = method.delivery_tag)

def searchInReports(uuid, numpyEncoding):
    reports = dbService.getReports()
    for report in reports:
        try:
            reportId = int(report["id"])
            reportEncoding = json.loads(report["image_code"])
            isOnePerson = face_recognition.compare_faces([getEncoding(reportEncoding)], numpyEncoding)[0]

            if isOnePerson:
                sendResponse(uuid, reportId)
                return True
        except Exception as ex:
            log.error(str(type(ex)) + " : " + str(ex))
    return False

def makeReport(uuid, encoding, numpyEncoding, imageUrl):
    codeJson = json.dumps(encoding)
    reportId = dbService.saveReport(datetime.utcnow(), imageUrl, codeJson)

    count = 100
    offset = 0
    links = dbService.getLinks(count, offset)
    while not len(links) == 0:
        for link in links:
            try:
                linkId = int(link["id"])
                url = link["url"]
                title = link["title"]
                description = link["description"]
                publishDate = link["publish_date"]
                jsonObj = json.loads(link["persons"])
                try:
                    if isinstance(jsonObj, list):
                        persons = jsonObj
                    else:
                        persons = jsonObj['persons']
                except TypeError as tex:
                    if jsonObj != '[]': # there are no people in the images
                        log.error('Catch TypeError: persons = ' + str(jsonObj))
                    continue
                log.debug("Process the link: id=" + str(linkId) + ", url=" + url + ", personsCount=" + str(len(persons)))

                for person in persons:
                    try:
                        imageUrl = person["image_url"]
                        presenceRate = person["presence_rate"]
                        personEncoding = json.loads(person["code"])

                        isOnePerson = face_recognition.compare_faces([getEncoding(personEncoding)], numpyEncoding)[0]
                        if isOnePerson:
                            dbService.saveReportLink(reportId, url, title, description, imageUrl, linkId, presenceRate, publishDate)
                            break
                    except Exception as ex:
                        log.error(str(type(ex)) + " : " + str(ex) + "; person = " + str(person))

            except Exception as ex:
                log.error(str(type(ex)) + " : " + str(ex))

        offset = offset + count
        links = dbService.getLinks(count, offset)
    
    sendResponse(uuid, reportId)

def getEncoding(codeArray):
    return numpy.asarray(codeArray)

def sendResponse(uuid, reportId):
    response = {
        'uuid': uuid,
        'reportId': reportId
    }
    send(uuid, response)

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
    log.debug("Start listening to the " + secondQueueName)
    try:
        queueService.channel.basic_consume(queue=secondQueueName, on_message_callback=callback)
        queueService.channel.start_consuming()
    except Exception as ex:
        log.error(str(type(ex)) + " : " + str(ex))

def close():
    global queueService, dbService
    queueService.__exit__()
    dbService.__exit__()
