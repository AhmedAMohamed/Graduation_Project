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






def getSense(text):
    print(text)
    sents=sent_tokenize(text)

    print(sents)

    dict = {}


    sent_index = 0


    for sent in sents:


        sent_index+=1

        sent = sent.translate(None, string.punctuation)


        words=word_tokenize(sent)

        tagged_words=pos_tag(words)
        word_index=0
        for word in tagged_words:

            word_index += 1

            pos = ut.penn2morphy(word[1])

            index=str(word_index)+" "+str(sent_index)
           # print str(word)+" "+str(index)


            if(pos!=''):


                WW=adapted_lesk(sent,word[0],pos)



                if(WW.__class__ == nltk.corpus.reader.wordnet.Synset):



                    dict[index]=WW.definition()



                else:

                    dict[index]="None"

            else: dict[index]="None"





    return json.dumps(dict)
