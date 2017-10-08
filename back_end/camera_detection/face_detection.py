##############################
# Designed by Chi Zhang
# 2017.9.26
# The class is for face detection using Azure.
# Comparing the detected person with owner
# recorded face data
##############################


import httplib, urllib, json
from pymongo import MongoClient

class face_detection:
    def __init__(self,url,car_number):
        self.client = MongoClient(url)
        self.db = self.client.safe_car
        self.col_account = self.db.account
        self.subscription_key = '3d1f79d0d3c343048a8fa746e67c6ea8'
        self.headers = {
            'Content-Type': 'application/octet-stream',
            'Ocp-Apim-Subscription-Key': self.subscription_key,
        }
        self.params = urllib.urlencode({
            'returnFaceId': 'true',
            'returnFaceLandmarks': 'false',
            'returnFaceAttributes': 'age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise',
        })
        self.car_number = car_number
    # using Azure face API to compare the detected person face data with the owner face data which saved in the CosmosDB
    def detection(self):
        faceIds_owner = []
        for person in self.col_account.find():
            if person['car'] == self.car_number and person['face'] != "":
                faceIds_owner.append(str(person['face']))
        bodys_detected = []
        # read cache pictures
        for pic_num in range(3):
            pic = 'temp'+str(pic_num)+'.png'
            f = open(pic,'rb')
            body = f.read()
            bodys_detected.append(body)
            f.close()
        faceIds_detected = []
        # send pics data to Azure
        for body in bodys_detected:
            conn = httplib.HTTPSConnection('southeastasia.api.cognitive.microsoft.com')
            conn.request("POST", "/face/v1.0/detect?%s" % self.params, body, self.headers)
            response = conn.getresponse()
            data = response.read()
            parsed = json.loads(data)
            try:
                faceId = str(parsed[0]['faceId'])
                faceIds_detected.append(faceId)
            except:
                pass
        result = []

        self.params = urllib.urlencode({
        })
        # set Content-Type to json or octet-stream depending on the data type
        self.headers = {
            'Content-Type': 'application/json',
            'Ocp-Apim-Subscription-Key': self.subscription_key,
        }
        print "detected ID:"+" ".join(faceIds_detected)
        print "owner face ID:"+" ".join(faceIds_owner)
        for i in faceIds_detected:
            for j in faceIds_owner:
                body = {'faceId1':i,'faceId2':j}
                body = str(body)
                conn = httplib.HTTPSConnection('southeastasia.api.cognitive.microsoft.com')
                conn.request("POST", "/face/v1.0/verify?%s" % self.params, body, self.headers)
                response = conn.getresponse()
                if 'true' in response.read():
                    result.append(True)
                else:
                    result.append(False)
        self.headers = {
            'Content-Type': 'application/octet-stream',
            'Ocp-Apim-Subscription-Key': self.subscription_key,
        }
        self.params = urllib.urlencode({
            'returnFaceId': 'true',
            'returnFaceLandmarks': 'false',
            'returnFaceAttributes': 'age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise',
        })
        return result