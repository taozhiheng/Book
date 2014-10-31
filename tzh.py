#!/usr/bin/python
# Filename:tzh.py
urlhead='http://www.jianshu.com'
import urllib2
import re

#chang html to markdown
def html_to_markdown(html):
  mark=html
  #cut head and tail
  heads='<div class=\"container\">'
  heado=re.compile(heads)
  iter=heado.finditer(mark)
  for x in iter:
    pass
  index=x.start()
  mark=mark[index:]
  tails='<!-- Functions -->'
  tailo=re.compile(tails)
  indexo=tailo.search(mark)
  index=indexo.start()
  mark=mark[:index]
  
  #replace title
  titles='<h1.*?>(.*?)</h1>'
  titleo=re.compile(titles,re.S)
  mark=titleo.sub(r'#\1',mark)
  #replace h2
  h2s='<h2.*?>(.*?)</h2>'
  h2o=re.compile(h2s,re.S)
  mark=h2o.sub(r'##\1',mark)
  #replace h3
  h3s='<h3.*?>(.*?)</h3>'
  h3o=re.compile(h3s,re.S)
  mark=h3o.sub(r'###\1',mark)
  #replace h4
  h4s='<h4.*?>(.*?)</h4>'
  h4o=re.compile(h4s,re.S)
  mark=h4o.sub(r'####\1',mark)
  #replace br
  br='<br>'
  bro=re.compile(br)
  mark=bro.sub('\n',mark)
  #replace img
  br='<img.*?src=\"(.*?)\".*?>'
  bro=re.compile(br,re.S)
  mark=bro.sub(r'![picture](\1)',mark)
  #replace blockquote
  quotes='<blockquote>(.*?)</blockquote>'
  quoteo=re.compile(quotes,re.S)
  mark=quoteo.sub(r'>\1',mark)
  #replace ol
  ols='(<ol.*?>.*?)<li.*?>(.*?)</li>(.*?</ol>)'
  olo=re.compile(ols,re.S)
  mark=olo.sub(r'\1 1.\2\3',mark)
  #replace li
  lis='<li.*?>(.*?)</li>'
  lio=re.compile(lis,re.S)
  mark=lio.sub(r'+\1',mark)
  #remove ul|ol
  ls='<ul.*?>|</ul>|<ol.*?>|</ol>'
  lo=re.compile(ls,re.S)
  mark=lo.sub('',mark)
  #lis='<li.*?>(.*?)</li>'
  #lio=re.compile(lis,re.S)
  mark=lio.sub(r'+\1',mark)
  #replace span 
  sps='<span.*?>\s*?(\S*?)\s*?</span>'
  spo=re.compile(sps,re.S)
  mark=spo.sub(r'+\1',mark)
  #remove span
  rsps='<span.*?>\s.*?</span>'
  rspo=re.compile(rsps,re.S)
  mark=rspo.sub('',mark)
  #remove hr ul
  hus='<hr.*?>|<hr>|<ol.*?>|</ol>|<!--.*?-->'
  huo=re.compile(hus,re.S)
  mark=huo.sub('',mark)
  #remove
  ss='<a.*?>|</a>|<i.*?>|</i>|<div.*?>|</div>|<b.*?>|</b>'
  so=re.compile(ss,re.S)
  mark=so.sub('',mark) 
  #replace para
  contents='<p.*?>(.*?)</p>'
  contento=re.compile(contents,re.S)
  mark=contento.sub(r'  \1\n',mark)
  return mark


colurllist=['/collection/GQ5FAs', \
'/collection/ec7f078605ab', \
'/collection/f16b3d483ec2']
#to search title
titlestr='<title>(.*?) (.*?)</title>'
titleobj=re.compile(titlestr)
#to search next layer url
contentstr='<h4><a href=\"(.*?)\" target=\"_blank\">(.*?)</a></h4>'
contentobj=re.compile(contentstr)
#to search the hide content url
hidestr='data-url=\"(.*?)\"'
hideobj=re.compile(hidestr,re.S)
for colurl in colurllist:
  #colhtml:every page html_content
  #colres:title object
  #urlres:url set of passages 
  colhtml=urllib2.urlopen(urlhead+colurl).read()
  colres=titleobj.search(colhtml)
  urlres=contentobj.findall(colhtml)
  print colres.group(1)
  i=1
  
  while True :
    for x in urlres:
      url=urlhead+x[0]
      #print(url)
      filestr=urllib2.urlopen(url).read()
      newFile=open('mygit/'+colres.group(1)+'/'+x[1]+'.markdown', \
      'w')
      filestr=html_to_markdown(filestr)
      newFile.write(filestr)
      newFile.flush()
      newFile.close()
      print x[1]+'...'+str(i)+'/108'
      i+=1
    #hideres:hide object to search next page
    hideres=hideobj.search(colhtml)
    if (hideres is None) | i>100:
      break;
    hideurl=hideres.group(1)
    colhtml=urllib2.urlopen(urlhead+hideurl).read()
    urlres=contentobj.findall(colhtml)
   
print('process has finished!')
