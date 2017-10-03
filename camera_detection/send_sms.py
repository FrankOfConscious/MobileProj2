from twilio.rest import Client
from pymongo import MongoClient

class send_sms():
    def __init__(self,car_number):
        self.account_sid = "AC6a9f1adae3509e5e0238d80219fc7501"
        self.account_tok = "b4c023fe6e005363cf12c539a19c85a3"
        self.from_number = "+61428594161"
        self.client = MongoClient()
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