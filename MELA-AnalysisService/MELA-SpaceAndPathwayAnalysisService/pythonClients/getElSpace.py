import urllib, urllib2, sys, httplib

url = "/MELA/REST_WS"
HOST_IP="localhost"

 

if __name__=='__main__':
	connection =  httplib.HTTPConnection(HOST_IP+':8080')
        #create keyspace
        body_content = '<MonitoredElement id="CloudService" level="SERVICE"/>'
        headers={
	        'Content-Type':'application/xml; charset=utf-8',
                'Accept':'application/json, multipart/related'
	}
 
	connection.request('POST', url+'/elasticityspace', body=body_content,headers=headers,)
	result = connection.getresponse()
        print result.read()
 

 

