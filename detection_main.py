from camera_detection import camera_detection
from pymongo import MongoClient

class detection_start:
    def __init__(self):
        # self.url = 'mongodb://cosmosmobile:RSVODsHVGLWhO3mlt2RFCGSORxXCho7ht08YSKak2yWlqVxOkJx7i5eLmCfDALw0bxhzqFJWoEdRE' \
        #            'h5v6C9I8Q==@cosmosmobile.documents.azure.com:10255/?ssl=true&replicaSet=globaldb'
        self.url = 'localhost'
        self.client = MongoClient()
        self.db = self.client.safe_car
        self.col = self.db.account

    def start(self, car_number):
        cd = camera_detection.detection(self.url, car_number)
        if_pupil_detection = {}
        for i in self.col.find({"car":car_number}):
            if_pupil_detection[i['_id']] = i['ifStart']

        while(True):
            # if no owner start journey, only environment detection starts
            while(not True in if_pupil_detection.values()):
                cd.enviro_detect()
                for i in self.col.find({"car": car_number}):
                    if_pupil_detection[i['_id']] = i['ifStart']
            # if one user start the journey, the program will start pupil detection
            user = ''
            for i in if_pupil_detection:
                if if_pupil_detection[i] == True:
                    user = i
                    break
            print user
            cd.pupil_detection(user)
            for i in self.col.find({"car": car_number}):
                if_pupil_detection[i['_id']] = i['ifStart']

ds = detection_start()
ds.start(0)