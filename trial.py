import time
import wikipedia
import requests
import json

'''
ny = wikipedia.suggest("inter") # return string of a suggestion for search returns a string
print(ny)
'''
'''
ny = wikipedia.search('ahmed',5,True) # search in wiki for the word and returns a list limited to the number in the second param
print(ny)
'''
'''
ny = wikipedia.page('internet')  #
print((ny))
'''
'''
ny = wikipedia.random() # takes a param number of pages want to get randomly & returns pages titles
print(ny)
'''
'''
wikipedia.set_lang('ar') # set the language of the search and the returned pages
ny = wikipedia.page('احمد')
print(ny.content)
'''
'''
wikipedia.set_rate_limiting() # set minimum waiting time for getting result from wiki
'''
'''
ny = wikipedia.summary('internet', 0,150) # first param is the page title to summarize second if less than 10 and not equal 0 or less
print(ny)
'''
title = input()
t1 = time.time()
page = wikipedia.page(title)
t2 = time.time()
print(t2 - t1)
content = page.content
t3 = time.time()
print(t3 - t2)

list_of_content = []
list_of_sections = {}
list_of_con_sum = {}
#html = page.html()
#cat = page.categories

for item in content.split("\n"):
    if "==" in item:
        list_of_content.append(item.strip().replace('=','').strip())

for it in list_of_content:
    list_of_sections[it.split('Edit')[0]] = page.section(it)
for it in list_of_sections:
    if len(list_of_sections[it]) < 1:
        continue
    r = requests.post("http://autosummarizer.com/index.php", data={'text': list_of_sections[it]})
    text = r.text
    index_first , index_second = text.find('<div id="position">') , text.find('<br><br></b></div>')
    text = text[index_first:index_second]
    final_text = ""

    for line in text.split('<br><br>'):
        line = line.replace('<div id="position"><b>',' ')
        line.strip()
        final_text = final_text + '\n' + line

    list_of_con_sum[it] = final_text

json_topic_sum = json.dumps(list_of_con_sum)
json_topics = json.dumps(list_of_content)
print(json_topics)

#for key in list_of_con_sum:
    #print(key)
    #print(list_of_con_sum[key][3:])
    #print()

#for key in list_of_sections:
#    if len(list_of_sections[key]) < 1:
    #    print(key)


#r = requests.post("http://autosummarizer.com/index.php", data={'text': list_of_sections['Licensing of repositories']})
#text = r.text
#index_first , index_second = text.find('<div id="position">') , text.find('<br><br></b></div>')
#text = text[index_first:index_second]
#print(text)