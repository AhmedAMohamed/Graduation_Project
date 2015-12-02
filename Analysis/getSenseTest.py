import time

__author__ = 'DOHA'
import WSD
text ='''Python is a widely used general-purpose, high-level programming language.
       Its design philosophy emphasizes code readability, and its syntax allows programmers to express concepts in fewer
        lines of code than would be possible in languages such as C++ or Java.
         The language provides constructs intended to enable clear programs on both a small and large scale.
         Python supports multiple programming paradigms, including object-oriented, imperative and functional
         programming or procedural styles.'''
start_time=time.time()
dic=WSD.getSense(text)
print(time.time() - start_time)
print (dic)