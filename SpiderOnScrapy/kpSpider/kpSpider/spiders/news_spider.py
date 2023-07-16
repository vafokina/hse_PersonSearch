import dateparser
import warnings

from datetime import datetime, timedelta
from scrapy import Request
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor

from selenium import webdriver
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support.ui import WebDriverWait

import logging
from selenium.webdriver.remote.remote_connection import LOGGER
LOGGER.setLevel(logging.WARNING)
logging.getLogger("urllib3").setLevel(logging.WARNING)

from ..items import KpspiderItem

warnings.filterwarnings(
    "ignore",
    message="The localize method is no longer necessary, as this time zone supports the fold attribute",
)

class NewsSpider(CrawlSpider):
    name = 'kp_all_news'
    allowed_domains = ['perm.kp.ru']
    start_urls = ['https://www.perm.kp.ru/']

    rules = (
        Rule(LinkExtractor(allow=r".*?/daily/[0-9.]+/\d+"), callback='parse_items', follow=True),
        Rule(LinkExtractor(allow=()), follow=True),
    )

    def __scroll_down_page(self, driver, speed=8):
        current_scroll_position, new_height= 0, 1
        while current_scroll_position <= new_height:
            current_scroll_position += speed
            driver.execute_script("window.scrollTo(0, {});".format(current_scroll_position))
            new_height = driver.execute_script("return document.body.scrollHeight")

    def parse_items (self, response):
        # ignore if redirected to not allowed domain
        if self.allowed_domains[0] not in response.url:
            self.logger.debug("Skip a reference " + response.url)
            yield Request(response.url)
            return

        url = response.url
        title = response.xpath('/html/head/title/text()').get().strip()
        description = response.xpath("//meta[@name='description']/@content")[0].extract().strip()
        images = response.xpath('//div[@data-gtm-el="content-body"]/*//img/@src').extract() # аналогичное response.css('div[data-gtm-el="content-body"]').css('img::attr(src)').extract()  
        publish_date_str = response.css('span.styled__Time-sc-j7em19-1::text').get().strip()

        publish_date = dateparser.parse(publish_date_str, [r'%d %B %Y %H:%M'], ['ru']) - timedelta(hours=5)

        if len(images) > 1 | images[0].startswith('//'):
            self.logger.info("START SELENIUM DRIVER")
            driver = webdriver.Chrome(ChromeDriverManager().install())
            self.logger.info("Selenium GET " + response.url)
            driver.get(response.url)
            self.__scroll_down_page(driver, speed=25)
            
            wait = WebDriverWait(driver, 20)
            images_selenium = driver.find_elements_by_xpath('//div[@data-gtm-el="content-body"]/*//img')
            images = []
            for img in images_selenium: 
                image = img.get_attribute('src').strip()
                images.append(image)
            self.logger.debug("SELENIUM: Extract images " + str(images))
            
            self.logger.info("CLOSE SELENIUM DRIVER")
            driver.close()
            
        yield KpspiderItem(url=url, title=title, description=description, 
            update_date=datetime.utcnow(), publish_date=publish_date, images_url=images)
        