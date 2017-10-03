import numpy as np
import cv2
import datetime
from pymongo import  MongoClient
import requests
import json
import time
import os
import face_detection
import send_sms

class detection:

    def __init__(self,url,car_number):
        self.cap = cv2.VideoCapture(1)
        self.w = 296
        self.h = 640
        self.url = url
        self.client = MongoClient(self.url,27017)
        self.db = self.client.safe_car
        self.col_account = self.db.account
        self.col_warning = self.db.warning
        self.col_car = self.db.car
        self.warning = 0
        self.recovery = 0
        self.ifwarning = False
        self.has_record = False
        self.warning_id = ''
        self.car_number = car_number

    def save_warning(self, data):
        self.warning_id = self.col_warning.insert_one(data).inserted_id
        print self.warning
        print '*******saving id:' + str(self.warning_id) + ' ********'

    def update_warning(self):
        print self.warning
        self.col_warning.update({'_id':self.warning_id},{'$set':{'illegal_time':self.warning}})
        self.warning = 0
        print '*******updating id:' + str(self.warning_id) + ' ********'

    def get_geolocation(self):
        send_url = 'http://freegeoip.net/json'
        r = requests.get(send_url)
        j = json.loads(r.text)
        lat = j['latitude']
        lon = j['longitude']
        return lat, lon

    def enviro_detect(self):
        print "Start environment detection..."
        pic_count = 0
        # keep detecting before the owen is inside
        while (self.cap.isOpened()):
            ret, frame = self.cap.read()
            if ret == True:
                eye = cv2.CascadeClassifier('haarcascade_eye.xml')
                detected = eye.detectMultiScale(frame, 1.3, 3)
                if detected != () and pic_count < 3:
                    print 'somebody come in...'
                    cv2.imwrite('temp' + str(pic_count) +    '.png', frame)
                    pic_count += 1
                    time.sleep(3)
                elif pic_count >= 3:
                    print 'Pics has been taken...'
                    fd = face_detection.face_detection(self.url,self.car_number)
                    result = fd.detection()
                    print result
                    serious = True
                    for i in result:
                        if i:
                            serious = False
                            break
                    if serious:
                        lat, lon = self.get_geolocation()
                        warning_info = {"car": self.car_number, "time": datetime.datetime.now(),
                                        "geolocation": [lat, lon],
                                        "illegal_time": self.warning, "type": 'car'}
                        self.save_warning(warning_info)
                        print "car warning!!!"
                        for i in range(3):
                            os.remove('temp' + str(i) + '.png')

                        ss = send_sms.send_sms(self.car_number)
                        ss.send("Your car is being stolen!!!")
                        pic_count = 0
                        break

                    else:
                        print "This guy is the owner"
                        for i in range(3):
                            os.remove('temp' + str(i) + '.png')
                        pic_count = 0
                        break

    # pupil detection when user start driving
    def pupil_detection(self,user):
        print "Start pupil detection..."
        start = self.col_account.find_one({"_id":user})['ifStart']
        print start
        count = 0
        while (self.cap.isOpened() and start):
            count += 1
            ret, frame = self.cap.read()
            if ret == True:
                # downsample
                # frameD = cv2.pyrDown(cv2.pyrDown(frame))
                # frameDBW = cv2.cvtColor(frameD,cv2.COLOR_RGB2GRAY)
                # detect face
                frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
                eye = cv2.CascadeClassifier('haarcascade_eye.xml')
                detected = eye.detectMultiScale(frame, 1.3, 3)

                if detected == ():
                    self.warning += 1

                if self.warning >= 40 and not self.has_record:
                    print "========WARNING========"
                    # if the current status of user is warning, we do not do further step
                    # get geolocation. We can use GPS for the improvement
                    lat, lon = self.get_geolocation()
                    # here, we save the current time and current location of the user
                    warning_info = {"user":user,"time": datetime.datetime.now(), "geolocation":[lat, lon],
                                    "illegal_time":self.warning,"type":'person'}
                    self.save_warning(warning_info)
                    self.has_record = True

                if detected != () and self.recovery > 10 and self.has_record:
                    self.update_warning()
                    # after updating warning, reset the warning to 0 and ifwarning to False
                    self.has_record = False
                    print "~~~~~~~NORMAL~~~~~~~"
                elif detected != () and self.recovery <= 10 and self.has_record:
                    self.recovery += 1

                pupilFrame = frame
                pupilO = frame
                windowClose = np.ones((5, 5), np.uint8)
                windowOpen = np.ones((2, 2), np.uint8)
                windowErode = np.ones((2, 2), np.uint8)

                # draw square
                for (x, y, w, h) in detected:
                    cv2.rectangle(frame, (x, y), ((x + w), (y + h)), (0, 0, 255), 1)
                    cv2.line(frame, (x, y), ((x + w, y + h)), (0, 0, 255), 1)
                    cv2.line(frame, (x + w, y), ((x, y + h)), (0, 0, 255), 1)
                    pupilFrame = cv2.equalizeHist(frame[y + int(h * .25):(y + h), x:(x + w)])
                    pupilO = pupilFrame
                    ret, pupilFrame = cv2.threshold(pupilFrame, 55, 255, cv2.THRESH_BINARY)  # 50 ..nothin 70 is better
                    pupilFrame = cv2.morphologyEx(pupilFrame, cv2.MORPH_CLOSE, windowClose)
                    pupilFrame = cv2.morphologyEx(pupilFrame, cv2.MORPH_ERODE, windowErode)
                    pupilFrame = cv2.morphologyEx(pupilFrame, cv2.MORPH_OPEN, windowOpen)

                    # so above we do image processing to get the pupil..
                    # now we find the biggest blob and get the centriod

                    threshold = cv2.inRange(pupilFrame, 250, 255)  # get the blobs
                    contours, hierarchy = cv2.findContours(threshold, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

                    # if there are 3 or more blobs, delete the biggest and delete the left most for the right eye
                    # if there are 2 blob, take the second largest
                    # if there are 1 or less blobs, do nothing

                    if len(contours) >= 2:
                        # find biggest blobcd.pupil_detection(user)
                        maxArea = 0
                        MAindex = 0  # to get the unwanted frame
                        distanceX = []  # delete the left most (for right eye)
                        currentIndex = 0
                        for cnt in contours:
                            area = cv2.contourArea(cnt)
                            center = cv2.moments(cnt)
                            # print 'm10:' + str(center['m10'])
                            # print 'm01:' + str(center['m01'])
                            # print center['m00']
                            # print center['m01']
                            cx, cy = int(center['m10'] / (center['m00'] + 1.0)), int(
                                center['m01'] / (center['m00'] + 1.0))
                            distanceX.append(cx)
                            if area > maxArea:
                                maxArea = area
                                MAindex = currentIndex
                            currentIndex = currentIndex + 1

                        del contours[MAindex]  # remove the picture frame contour
                        del distanceX[MAindex]

                    eye = 'right'

                    if len(contours) >= 2:  # delete the left most blob for right eye
                        if eye == 'right':
                            edgeOfEye = distanceX.index(min(distanceX))
                        else:
                            edgeOfEye = distanceX.index(max(distanceX))
                        del contours[edgeOfEye]
                        del distanceX[edgeOfEye]

                    if len(contours) >= 1:  # get largest blob
                        maxArea = 0
                        for cnt in contours:
                            area = cv2.contourArea(cnt)
                            if area > maxArea:
                                maxArea = area
                                largeBlob = cnt

                    if len(largeBlob) > 0:
                        center = cv2.moments(largeBlob)
                        cx, cy = int(center['m10'] / center['m00']), int(center['m01'] / center['m00'])
                        cv2.circle(pupilO, (cx, cy), 5, 255, -1)

                # show picture
                cv2.imshow('frame', pupilO)
                cv2.imshow('frame2', pupilFrame)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
                # the count reach 100 need around 8 seconds
                # so we set about 8 seconds to check the database
                if count == 100:
                    start = self.col_account.find_one({"_id": user})['ifStart']
                    count = 0



