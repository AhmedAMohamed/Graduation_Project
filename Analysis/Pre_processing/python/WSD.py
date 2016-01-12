# -*- coding: utf-8 -*-
"""
Created on Fri Oct 30 22:57:20 2015

@author: DOHA
"""
#import nltk



from nltk.wsd import lesk
from nltk import sent_tokenize, word_tokenize
import sys
import time
from pywsd.lesk import *
from nltk.corpus import semcor
from nltk import pos_tag






#wordnet_pos_ta
def wordnet_pos_code(tag):
    if tag.startswith('N'):
        return 'n'
    elif tag.startswith('V') or tag.startswith('M'):
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
    #tup = ()
    sent_index = 0
    word_index = 0

    for sent in sents:
        words=word_tokenize(sent)
        tagged_words=pos_tag(words)

        for word in tagged_words:
            pos=wordnet_pos_code(word[1])
            #print pos
            if(pos!="none"):

                dict[(word,sent_index,word_index)] = adapted_lesk(sent,word[0],pos)
                word_index+=1
            else:
                dict[(word,sent_index,word_index)] = adapted_lesk(sent,word[0])
                word_index+=1
    sent_index+=1
    return dict

#getSense(text2)

