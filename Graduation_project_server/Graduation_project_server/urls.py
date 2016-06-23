from django.conf.urls import patterns, include, url
from django.contrib import admin

from main_app import views

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'Graduation_project_server.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'^$', views.home_page, name='home'),
    url(r'^D3/$', views.d3_page, name='d3'),
    url(r'^Visjs/$', views.visjs_page, name='visjs'),
    url(r'^Mindmup/$', views.mindmup, name='mindmup'),
    url(r'^query/$', views.query_page, name='query_page'),
    url(r'^icon/$', views.noun_project_proxy, name='noun_project_proxy'),
    #url(r'^admin/', include(admin.site.urls)),
)
