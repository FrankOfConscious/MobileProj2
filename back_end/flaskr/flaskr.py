##############################
# Designed by Chi Zhang
# 2017.9.28
# Flask server for app
##############################

from flask import Flask, request, jsonify
from pymongo import MongoClient


url = 'mongodb://cosmosmobile:RSVODsHVGLWhO3mlt2RFCGSORxXCho7ht08YSKak2yWlqVxOkJx7i5eLmCfDALw0bxhzqFJWoEdRE' \
                   'h5v6C9I8Q==@cosmosmobile.documents.azure.com:10255/?ssl=true&replicaSet=globaldb'
client = MongoClient(url)
overall_db = client.safe_car
acc_col = overall_db.account
app = Flask(__name__)

@app.route('/', methods=['POST'])
def test():
    username = request.form['username']
    password = request.form['password']
    return 'username:'+username+';;;'+'password:'+password

#login
@app.route('/login', methods=['POST'])
def login():
    print 'Thiurls is login operation'
    username = request.form['username']
    password = request.form['password']
    print username
    print password
    login_succ = False
    for i in acc_col.find():
        if i['_id'] == username and str(i['password']) == password:
            return jsonify({'login_status':True})
    return jsonify({'login_status':False})

# the entrance of getProfile operation from app
@app.route('/getProfile', methods=['GET'])
def getProfile():
    print 'This is a get profile operation'
    request_user = str(request.args.getlist('username')[0])
    return_profile = {}
    for i in acc_col.find():
        if i['_id'] == request_user:
            return_profile['mobile'] = i['mobile']
            return_profile['car'] = i['car']
            return_profile['email'] = i['email']
            break
    print return_profile
    return jsonify({'profile':return_profile})

# the entrance of check_server operation from app
@app.route('/check_server', methods=['GET'])
def checkServer():
    print "checking server..."
    request_user = str(request.args.getlist('username')[0])
    return_info = {}
    for i in acc_col.find():
        if i['_id'] == request_user:
            return_info['ifWarning'] = i['ifWarning']
            return_info['ifRecovery'] = i['ifRecovery']
            return_info['ifStart'] = i['ifStart']
            break
    print return_info
    # print return_info
    return jsonify({'info':return_info})

# the entrance of signup operation from app
@app.route('/signup',methods=['POST'])
def signup():
    print 'This is signup operation'
    username = request.form['username']
    password = request.form['password']
    car = request.form['car']
    mobile = request.form['mobile']
    email = request.form['email']
    face = request.form['face']
    ifWarning = request.form['ifWarning']
    ifRecovery = request.form['ifRecovery']
    ifStart = request.form['ifStart']
    index = request.form['index']

    signup_succ = True
    print username
    #if username exist,
    for i in acc_col.find():
        if i['_id'] == username:
            signup_succ = False
            break
    print "here"
    print signup_succ
    if signup_succ:
        signup_dict = {'_id':username, 'password':password,'car':car,'mobile':mobile,'email':email,'face':face,
                       'ifWarning':ifWarning,'ifRecovery':ifRecovery,'ifStart':ifStart,'index':index,'warning_cache':0}
        acc_col.insert_one(signup_dict)
        return jsonify({'signup_status':True})
    else:
        return jsonify({'signup_status':False})

# the entrance of update_start operation from app
@app.route('/update_start',methods=['GET'])
def update_start():
    print "Turn on the user ifStart attribute"
    request_user = request.args.getlist('username')[0]
    for i in acc_col.find():
        if i['_id'] == request_user:
            acc_col.update_one({'_id': request_user}, {'$set': {'ifStart': True}})
    return jsonify({})

# the entrance of update_end operation from app
@app.route('/update_end',methods=['GET'])
def update_end():
    print "Turn off the user ifStart attribute"
    request_user = str(request.args.getlist('username')[0])
    for i in acc_col.find():
        if i['_id'] == request_user:
            acc_col.update_one({'_id': request_user}, {'$set': {'ifStart': False}})
    return jsonify({})

# the entrance of get_result operation from app
@app.route('/get_result',methods=['GET'])
def get_result():
    print "getting final result..."
    request_user = request.args.getlist('username')[0]
    warning_times = 0
    index = ''
    for i in acc_col.find():
        if i['_id'] == request_user:
            warning_times = i['warning_cache']

    if warning_times == 0:
        index = 'S'
    elif warning_times>0 and warning_times <=2:
        index = 'A'
    elif warning_times > 2 and warning_times <=4:
        index = 'B'
    else:
        index = 'C'
    return jsonify({'waring_times':warning_times,'index':index})

# default ip is localhost and port is 5001
if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True, port=5001)