# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html

import psycopg2, json
from psycopg2 import sql
from psycopg2.extras import DictCursor

# useful for handling different item types with a single interface
from itemadapter import ItemAdapter

class KpspiderPipeline(object):
    DATABASE_NAME = "postgres"
    USER = "postgres"
    PASS = "docker"
    HOST = "localhost"
    PORT = 5432

    TABLE_NAME = "link"

    INSERT_LINK_SQL = "INSERT INTO link (url, title, description, update_date, publish_date, images_url, persons) VALUES (%s, %s, %s, %s, %s, %s, %s) RETURNING id"

    connection = None
    #cursor = None

    def __init__(self):
        try:
            self.connection = psycopg2.connect(dbname=self.DATABASE_NAME, user=self.USER, 
                password=self.PASS, host=self.HOST, port=self.PORT)
            #self.cursor = self.connection.cursor()
        except Exception as ex:
            print(str(type(ex)) + " : " + str(ex))
 
    #def open_spider(self, spider):
    #    pass
 
    def close_spider(self, spider):
        try:
            #self.cursor.close()
            self.connection.close()
        except Exception as ex:
            self.log.error(str(type(ex)) + " : " + str(ex))
 
    def process_item(self, item, spider):
        images_url = json.dumps(item["images_url"])
        id = self.__saveLink(item["url"], item["title"], item["description"], 
            item["update_date"], item["publish_date"], images_url, None, spider)
        spider.logger.debug("Successfully inserted link with id=" + str(id))
        return item

    def __saveLink(self, url, title, description, updateDate, publishDate, imagesUrl, persons, spider):
        params = (url, title, description, updateDate, publishDate, imagesUrl, persons)
        spider.logger.debug("Insert link. Sql statement : " + self.INSERT_LINK_SQL + " " + str(params))
        with self.connection.cursor(cursor_factory=DictCursor) as cursor:
            cursor.execute(self.INSERT_LINK_SQL, params)
            id = cursor.fetchone()[0]
            self.connection.commit()
            return id
