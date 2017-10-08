##############################
# Designed by Chi Zhang
# 2017.10.1
# the main entrance for the owner
# authentication and pupil detection.
# The module "camera_detection" and
# this main class are all ran on the raspberry pi3
##############################


from camera_detection import camera_detection
from pymongo import MongoClient

# the entrance of detection program which is ran on Raspberry Pi3
class detection_start:
    def __init__(self):
        self.url = 'mongodb://cosmosmobile:RSVODsHVGLWhO3mlt2RFCGSORxXCho7ht08YSKak2yWlqVxOkJx7i5eLmCfDALw0bxhzqFJWoEdRE' \
                   'h5v6C9I8Q==@cosmosmobile.documents.azure.com:10255/?ssl=true&replicaSet=globaldb'
        # self.url = 'localhost'
        self.client = MongoClient(self.url)
        self.db = self.client.safe_car
        self.col = self.db.account

    def start(self, car_number):
        cd = camera_detection.detection(self.url, car_number)
        if_pupil_detection = {}
        for i in self.col.find({"car":car_number}):
            if_pupil_detection[i['_id']] = i['ifStart']

        while(True):
            print if_pupil_detection
            # if no owner start journey, only environment detection starts
            while(not True in if_pupil_detection.values()):
                cd.enviro_detect()
                for i in self.col.find({"car": car_number}):
                    if_pupil_detection[i['_id']] = i['ifStart']

            # if one user start the journey, the program will start pupil detection
            user = ''
            print "User has started..."
            for i in if_pupil_detection:
                if if_pupil_detection[i] == True:
                    user = i
                    break
            print user
            cd.pupil_detection(user)
            for i in self.col.find({"car": car_number}):
                if_pupil_detection[i['_id']] = i['ifStart']

ds = detection_start()
ds.start(1)