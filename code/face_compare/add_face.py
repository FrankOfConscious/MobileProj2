import httplib, urllib, base64

headers = {
    # Request headers
    'Content-Type': 'application/json',
    'Ocp-Apim-Subscription-Key': '3d1f79d0d3c343048a8fa746e67c6ea8',
}

params = urllib.urlencode({
    # Request parameters
    'personGroupId':'vaildface',
    'personId':'Chi Zhang',
})

pic = open('chi2.jpg','rb')
body = pic.read()

try:
    conn = httplib.HTTPSConnection('southeastasia.api.cognitive.microsoft.com')
    conn.request("POST", "/face/v1.0/persongroups/{personGroupId}/persons/{personId}/persistedFaces?%s" % params, body, headers)
    response = conn.getresponse()
    data = response.read()
    print(data)
    conn.close()
except Exception as e:
    print("[Errno {0}] {1}".format(e.errno, e.strerror))