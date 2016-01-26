from django.shortcuts import render
from rpc import NLPEngineClient

nlp_engine = NLPEngineClient()
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