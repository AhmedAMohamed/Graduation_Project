import time

__author__ = 'DOHA'
import WSD
text2 ='''Science is a systematic enterprise that creates, builds and organizes knowledge in the form of testable explanations and predictions about the universe.

Contemporary science is typically subdivided into the natural sciences which study the material world, the social sciences which study people and societies, and the formal sciences like mathematics. The formal sciences are often excluded as they do not depend on empirical observations. Disciplines which use science like engineering and medicine may also be considered to be applied sciences.

During the middle ages in the Middle East, foundations for the scientific method were laid by Alhazen. From classical antiquity through the 19th century, science as a type of knowledge was more closely linked to philosophy than it is now and, in fact, in the West the term "natural philosophy" encompassed fields of study that are today associated with science such as physics, astronomy, medicine, among many others.'''
text ='''He made Dalia a surprise. It was a nice cake.'''
start_time=time.time()
dic=WSD.getSense(text)
print(time.time() - start_time)
print (dic)