#!/usr/bin/python
# Filename:tzh.py
urlHead='http://www.jianshu.com'
import urllib2
import re
firstLayer=urllib2.urlopen(urlHead).read()
file0=open('file0','w')
print(firstLayer)
file0.write(firstLayer)
file0.flush()
file0.close()
pStr='<a class="title" href="(.*?)" target="_blank">(.*?)</a>'
pStrObj=re.compile(pStr)
urlList=pStrObj.findall(firstLayer)
for x in urlList:
  url=urlHead+x[0]
  print(url)
  fileStr=urllib2.urlopen(url).read()
  newFile=open('file_test/'+x[1],'w')
  newFile.write(fileStr)
  newFile.flush()
  newFile.close()
print('process has finished!')
