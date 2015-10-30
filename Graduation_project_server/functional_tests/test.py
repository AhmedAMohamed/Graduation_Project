from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from django.contrib.staticfiles.testing import StaticLiveServerTestCase
import sys

class HomePageTest(StaticLiveServerTestCase):
	@classmethod
	def setUpClass(cls):
		for arg in sys.argv:
			if 'liveserver' in arg:
				cls.server_url = 'http://' +arg.split('=')[1]
				return
		super(HomePageTest, HomePageTest).setUpClass()
		cls.server_url = cls.live_server_url

	@classmethod
	def tearDownClass(cls):
		if cls.server_url == cls.live_server_url:
			super(HomePageTest, HomePageTest).tearDownClass()

	def setUp(self):
		self.browser = webdriver.Firefox()

	def tearDown(self):
		self.browser.quit()

	def test_home_page_loaded_correctly(self):
		#User go to the home page
		self.browser.get(self.server_url)

		#User sees title "Graduation project"
		self.assertIn('Graduation project', self.browser.title)

		#User sees too buttons


		#First big button leads to a new page

		#Second big button leads to another page


