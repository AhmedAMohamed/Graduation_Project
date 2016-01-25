# -*- coding: utf-8 -*-
"""
Created on Fri Oct 30 22:57:20 2015

@author: DOHA
"""
#import nltk
import jsonpickle
import json

from nltk.wsd import lesk
from nltk import sent_tokenize, word_tokenize
import sys
import time
from pywsd.lesk import *
from nltk.corpus import semcor
from nltk import pos_tag
import pywsd.utils as ut
import nltk




#wordnet_pos_ta
def wordnet_pos_code(tag):
    if tag.startswith('N'):
        return 'n'
    elif tag.startswith('V'):
        return 'v'

    #elif tag.startswith('J'):
    #    return 'adj'
    #elif tag.startswith('RB'):
        #return 'adv'
    else:
        return 'none'

def getSense(text):
    print(text)
    sents=sent_tokenize(text)

    print(sents)

    dict = {}

    sent_index = 0
    word_index = 1

    for sent in sents:
        print sent
        print "Sent index is "
        sent_index+=1
        print(sent_index)
        sent = sent.translate(None, string.punctuation)


        words=word_tokenize(sent)

        tagged_words=pos_tag(words)

        for word in tagged_words:

            pos = ut.penn2morphy(word[1])
            index=str(word)+" "+str(word_index)+" "+str(sent_index)



            if(pos!="none"):


                WW=adapted_lesk(sent,word[0],pos)


                if(WW.__class__ == nltk.corpus.reader.wordnet.Synset):


                    #Doha
                    print word[0]
                    dict[index]=WW.definition()
                    print "definition"
                    print dict[index]


                else:

                    dict[index]="None"
                    print "None"
                    print word[0]



                word_index+=1
            '''
            else:
                 WW2=adapted_lesk(sent,word[0])
                 print (WW2.__class__ + "      word sense class")
                 if(WW2.__class__ != None):
                    dict[index] = WW2.definition()
                 else:
                    dict[index]="None"
                 word_index+=1
                 print dict[index],word[0]

            '''

    return json.dumps(dict)