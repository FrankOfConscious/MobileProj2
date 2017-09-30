from camera_detection import pupil_detection

url = 'mongodb://cosmosmobile:RSVODsHVGLWhO3mlt2RFCGSORxXCho7ht08YSKak2yWlqVxOkJx7i5eLmCfDALw0bxhzqFJWoEdREh5v6C9I8Q==@\
cosmosmobile.documents.azure.com:10255/?ssl=true&replicaSet=globaldb'

user_name = "Chi Zhang"
car_number = 0
# cd = pupil_detection.pupil_detection(url, user_name)
# cd.detection()

cd = pupil_detection.pupil_detection(url,user_name,car_number)
cd.enviro_detect()