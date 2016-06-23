from django.shortcuts import render
from django.http import JsonResponse, HttpResponseNotFound


from requests import get
from requests_oauthlib import OAuth1

from rpc import NLPEngineClient

nlp_engine = NLPEngineClient()
auth = OAuth1('df79faafc9b9436fa3ff4a6a361e1ac1', 'c057d65aae2b4adbaaa418e44baccf28')
# Create your views here.

def home_page(request):
	return render(request, 'home.html')

def d3_page(request):
	return render(request, 'd3.html')

def visjs_page(request):
	return render(request, 'Visjs.html')

def mindmup(request):
	return render(request, 'Mindmup.html')

def query_page(request):
	query = request.GET.get('query', None)
	json = None
	if query:
		try:
			json = nlp_engine.call(query)
		except Exception:
			nlp_engine = NLPEngineClient()
			json = nlp_engine.call(query)
	return render(request, 'query.html', {'json': json})

def noun_project_proxy(request):
	query = request.GET.get('term', None)
	if query:
		response = get('http://api.thenounproject.com/icon/' + query, auth=auth)
		if response.status_code == 404:
			return HttpResponseNotFound()
		return JsonResponse(response.json())
	else:
		return HttpResponseNotFound();