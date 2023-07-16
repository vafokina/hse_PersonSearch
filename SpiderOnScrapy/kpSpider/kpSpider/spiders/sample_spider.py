import scrapy

from datetime import datetime
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor

class NewsSpider(CrawlSpider):
    name = 'sample'
    start_urls = ['https://zen.yandex.ru/media/diva/top14-luchshih-jenskih-duhov-camye-populiarnye-duhi-u-jenscin-6154e30548745b42e403546b']
    allowed_domains = ['zen.yandex.ru/media/diva']

    rules = (
        Rule(LinkExtractor(allow=r".*"), callback='parse_items', follow=True),
        Rule(LinkExtractor(allow=r".*"), follow=True),
    )

    def parse_items (self, response):
        url = response.url
        title = response.xpath('/html/head/title/text()').get().strip()
        description = response.xpath("//meta[@name='description']/@content")[0].extract().strip()
        images = response.xpath('//img/@src').extract() 

        yield{
            'im_count':len(images),
            'url':url,
            'title':title,
            'description':description,
            'image_urls':images,
            'scraping_date':datetime.now()
        }
        