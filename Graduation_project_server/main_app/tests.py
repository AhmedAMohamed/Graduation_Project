from django.core.urlresolvers import resolve
from django.template.loader import render_to_string
from django.test import TestCase
from django.http import HttpRequest

from main_app.views import home_page, d3_page, visjs_page

# Create your tests here.
class HomePageTest(TestCase):
	def test_root_url_resolves_to_home_page(self):
		found = resolve('/')
		self.assertEqual(found.func, home_page)

	def test_home_page_return_the_correct_html(self):
		request = HttpRequest()
		response = home_page(request)
		expected_html = render_to_string('home.html')
		self.assertEqual(response.content.decode(), expected_html)

	def test_home_page_has_two_buttons(self):
		request = HttpRequest()
		response = home_page(request)
		self.assertContains(response, '<a', count=2)

class D3Page(TestCase):
	def test_url_resolves_to_first_page(self):
		found = resolve('/D3/')
		self.assertEqual(found.func, d3_page)

	def test_d3_page_returns_the_correct_html(self):
		request = HttpRequest()
		response = d3_page(request)
		expected_html = render_to_string('d3.html')
		self.assertEqual(response.content.decode(), expected_html)

class VisjsPage(TestCase):
	def test_url_resolves_to_first_page(self):
		found = resolve('/Visjs/')
		self.assertEqual(found.func, visjs_page)

	def test_d3_page_returns_the_correct_html(self):
		request = HttpRequest()
		response = visjs_page(request)
		expected_html = render_to_string('Visjs.html')
		self.assertEqual(response.content.decode(), expected_html)