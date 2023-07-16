# -*- coding: utf-8 -*-
import psycopg2
from psycopg2 import sql
from psycopg2.extras import DictCursor

from config.LoggingConfig import get_logger

class DataBaseService:

    DATABASE_NAME = "postgres"
    USER = "postgres"
    PASS = "docker"
    HOST = "localhost"
    PORT = 5432

    GET_REPORTS_SQL = "SELECT * FROM report ORDER BY id"
    GET_LINKS_SQL = "SELECT * FROM link WHERE persons IS NOT NULL AND persons NOT LIKE '\"[]\"' ORDER BY id LIMIT {} OFFSET {}"
    #GET_LINKS_SQL = "SELECT * FROM link WHERE persons IS NOT NULL ORDER BY id LIMIT {} OFFSET {}"
    INSERT_REPORT_SQL = "INSERT INTO report (update_date, image_url, image_code) VALUES (%s, %s, %s) RETURNING id"
    INSERT_REPORT_LINK_SQL = "INSERT INTO report_link (report_id, url, title, description, image_url, source_link_id, presence_rate, publish_date) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
    INSERT_LINK_SQL = "INSERT INTO link (url, title, description, update_date, publish_date, images_url, persons) VALUES (%s, %s, %s, %s, %s, %s, %s) RETURNING id"

    connection = None
    #cursor = None

    log = get_logger("DataBaseService")

    def __init__(self):
        try:
            self.log.info("Start connection to PostgreSQL")
            self.connection = psycopg2.connect(dbname=self.DATABASE_NAME, user=self.USER, 
                password=self.PASS, host=self.HOST, port=self.PORT)
            #self.cursor = self.connection.cursor()
        except Exception as ex:
            self.log.error(str(type(ex)) + " : " + str(ex))

    def __enter__(self):
        return self

    def __exit__(self):
        try:
            self.log.info("Close connection to PostgreSQL")
            #self.cursor.close()
            self.connection.close()
        except Exception as ex:
            self.log.error(str(type(ex)) + " : " + str(ex))

    def getReports(self):
        self.log.debug("Select all reports. Sql statement : " + self.GET_REPORTS_SQL)
        with self.connection.cursor(cursor_factory=DictCursor) as cursor:
            cursor.execute(self.GET_REPORTS_SQL)
            records = cursor.fetchall()
            self.connection.commit()
            return records

    def getLinks(self, count, offset):
        self.log.debug("Select " + str(count) + " links starting from " + str(offset) + ". Sql statement : " + self.GET_LINKS_SQL + " [" + str(count) + "," + str(offset) + "]")
        with self.connection.cursor(cursor_factory=DictCursor) as cursor:
            stmt = sql.SQL((self.GET_LINKS_SQL).format(count, offset))
            cursor.execute(stmt)
            records = cursor.fetchall()
            self.connection.commit()
            return records

    def saveReport(self, updateDate, imageUrl, imageCode):
        params = (updateDate, imageUrl, imageCode)
        self.log.debug("Insert report. Sql statement : " + self.INSERT_REPORT_SQL + " " + str(params))
        with self.connection.cursor(cursor_factory=DictCursor) as cursor:
            cursor.execute(self.INSERT_REPORT_SQL, params)
            id = cursor.fetchone()[0]
            self.connection.commit()
            return id

    def saveReportLink(self, reportId, url, title, description, imageUrl, sourceLinkId, presenceRate, publishDate):
        params = (reportId, url, title, description, imageUrl, sourceLinkId, presenceRate, publishDate)
        self.log.debug("Insert report link. Sql statement : " + self.INSERT_REPORT_LINK_SQL + " " + str(params))
        with self.connection.cursor(cursor_factory=DictCursor) as cursor:
            cursor.execute(self.INSERT_REPORT_LINK_SQL, params)
            self.connection.commit()
    
    def saveLink(self, url, title, description, updateDate, publishDate, imagesUrl, persons):
        params = (url, title, description, updateDate, publishDate, imagesUrl, persons)
        self.log.debug("Insert link. Sql statement : " + self.INSERT_LINK_SQL + " " + str(params))
        with self.connection.cursor(cursor_factory=DictCursor) as cursor:
            cursor.execute(self.INSERT_LINK_SQL, params)
            id = cursor.fetchone()[0]
            self.connection.commit()
            return id
            
    