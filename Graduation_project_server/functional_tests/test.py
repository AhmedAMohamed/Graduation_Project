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
		home_page_url = self.server_url + '/'
		self.browser.get(home_page_url)

		#User sees title "Graduation project"
		self.assertEqual('Graduation project', self.browser.title,
			'Wrong title: %s.' % self.browser.title)

		#User sees 3 buttons
		buttons = self.browser.find_elements_by_tag_name('a')
		self.assertEqual(len(buttons), 3, 'Missing buttons')

		#First button leads to a new page
		first_button = buttons[0]
		first_button.click()
		first_button_url = self.browser.current_url
		self.assertNotEqual(home_page_url, first_button_url,
			'URL didn\'t change going to the next page')

		#User go back
		self.browser.back()
		self.assertEqual(self.browser.current_url, home_page_url,
			'Should be %s url instead %s' % (home_page_url, self.browser.current_url,))

		#User still sees 3 buttons
		buttons = self.browser.find_elements_by_tag_name('a')
		self.assertEqual(len(buttons), 3, 'Missing buttons')

		#Second button leads to another page
		second_button = buttons[1]
		second_button.click()
		second_button_url = self.browser.current_url
		self.assertNotEqual(home_page_url, second_button_url,
			'second url is the same as the home')
		self.assertNotEqual(first_button, second_button_url,
			'second url is the same as the first button')

		#User go back
		self.browser.back()
		self.assertEqual(self.browser.current_url, home_page_url,
			'Should be %s url instead %s' % (home_page_url, self.browser.current_url,))

		#User still sees 3 buttons
		buttons = self.browser.find_elements_by_tag_name('a')
		self.assertEqual(len(buttons), 3, 'Missing buttons found %d' % len(buttons))

		#Third button leads to another page
		second_button = buttons[2]
		second_button.click()
		second_button_url = self.browser.current_url
		self.assertNotEqual(home_page_url, second_button_url,
			'third url is the same as the home')
		self.assertNotEqual(first_button, second_button_url,
			'third url is the same as the first button')

	def test_layout_is_loaded_correctly(self):
		self.browser.get(self.server_url)
		self.browser.set_window_size(1024, 768)

		title = self.browser.find_element_by_tag_name('h1')
		self.assertNotAlmostEqual(title.location['x'], 0, delta=10)
		self.assertAlmostEqual(
			title.location['x'] + title.size['width'] / 2,
			512,
			delta=5
		)

	def test_server_respond_to_query(self):
		self.browser.get(self.server_url + '/query')
		input_box = self.browser.find_element_by_id('query')

		# Enter a query
		input_box.send_keys('Ahmed is late')
		input_box.send_keys(Keys.ENTER)

		json = self.browser.find_element_by_id('json').text
		self.assertNotEqual(json, '')



