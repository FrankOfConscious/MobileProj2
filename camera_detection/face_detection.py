import httplib, urllib, base64, json
from pymongo import MongoClient
import cv2

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

    def detection(self):
        faceIds_owner = []
        for person in self.col_account.find():
            if person['car'] == self.car_number and person['face'] != "":
                faceIds_owner.append(str(person['face']))
        bodys_detected = []
        for pic_num in range(5):
            pic = 'temp'+str(pic_num)+'.png'
            f = open(pic,'rb')
            body = f.read()
            bodys_detected.append(body)
            f.close()
        faceIds_detected = []
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
        self.headers = {
            'Content-Type': 'application/json',
            'Ocp-Apim-Subscription-Key': self.subscription_key,
        }
        print faceIds_detected
        print faceIds_owner
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