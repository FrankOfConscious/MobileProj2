##############################
# Designed by Chi Zhang
# 2017.9.27
# Class for sending warning messages to the owner
##############################

from twilio.rest import Client
from pymongo import MongoClient


# sending SMS to the recipient from Twilio virtual number
class send_sms():
    def __init__(self,car_number):
        self.url = 'mongodb://cosmosmobile:RSVODsHVGLWhO3mlt2RFCGSORxXCho7ht08YSKak2yWlqVxOkJx7i5eLmCfDALw0bxhzqFJWoEdRE' \
                   'h5v6C9I8Q==@cosmosmobile.documents.azure.com:10255/?ssl=true&replicaSet=globaldb'
        self.account_sid = "AC6a9f1adae3509e5e0238d80219fc7501"
        self.account_tok = "b4c023fe6e005363cf12c539a19c85a3"
        self.from_number = "+61428594161"
        self.client = MongoClient(self.url)
        self.db = self.client.safe_car
        self.col = self.db.account
        self.car_number = car_number

    def send(self, message):
        for i in self.col.find({"car":self.car_number}):
            to_number = i["mobile"]
            print "Sending message..."
            client = Client(self.account_sid, self.account_tok)
            client.messages.create(
                to=to_number,
                from_=self.from_number,
                body=message
            )
            print "Message has sent to "+ str(to_number)