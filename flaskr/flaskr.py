from flask import Flask, request, jsonify
from pymongo import MongoClient

client = MongoClient()
overall_db = client.safe_car
acc_col = overall_db.account

# create the application object
app = Flask(__name__)
#test
@app.route('/', methods=['POST'])
def test():
    username = request.form['username']
    password = request.form['password']
    return 'username:'+username+';;;'+'password:'+password

#login
@app.route('/login', methods=['POST'])
def login():
    print 'This is login operation'
    username = request.form['username']
    password = request.form['password']
    print username
    print password
    login_succ = False
    for i in acc_col.find():
        if i['_id'] == username and str(i['password']) == password:
            return jsonify({'login_status':True})
    return jsonify({'login_status':False})

@app.route('/getProfile', methods=['GET'])
def getProfile():
    print 'This is a get profile operation'
    request_user = str(request.args.getlist('username')[0])
    print request_user
    return_profile = {}
    for i in acc_col.find():
        print i['_id']
        print request_user
        if i['_id'] == request_user:
            return_profile['mobile'] = i['mobile']
            return_profile['car'] = i['car']
            return_profile['email'] = i['email']
            break
    return jsonify({'profile':return_profile})

#signup
@app.route('/signup',methods=['POST'])
def signup():
    print 'This is signup operation'
    username = request.form['username']
    password = request.form['password']
    address = request.form['address']
    mobile = request.form['mobile']
    email = request.form['email']
    signup_succ = True
    print username
    #if username exist,
    for i in acc_col.find():
        if i['username'] == username:
            signup_succ = False
            break
    if signup_succ:
        signup_dict = {'username':username, 'password':password,'address':address,'mobile':mobile,'email':email}
        acc_col.insert_one(signup_dict)
        return 'Signup successfully'

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)