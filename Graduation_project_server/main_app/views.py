from django.shortcuts import render

# Create your views here.

def home_page(request):
	return render(request, 'home.html')

def d3_page(request):
	return render(request, 'd3.html')

def visjs_page(request):
	return render(request, 'Visjs.html')

def mindmup(request):
	return render(request, 'Mindmup.html')