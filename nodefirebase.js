var firebase = require('firebase-admin');
var request = require('request');

var API_KEY = "AAAAqOAI1-Q:APA91bE6oVEyepwbjki-gAQ5UKUDts386Q_KjbiEq2TeupKy214Ky4qRBA_uWw9JKvceHVEOPaCsQ4WoSzeuRZhVTWQBTyAPCYzyRaf9U0Fc486exWHQd58Mfwdoxaq-xLPrgw3A9RvS"; // Your Firebase Cloud Messaging Server API key
  
// Fetch the service account key JSON file contents
var serviceAccount = require("/home/mukesh/AndroidStudioProjects/IOTAPP_Mobile/myapplication-d473f-firebase-adminsdk-4jkjl-c2d6230073.json");

// Initialize the app with a service account, granting admin privileges
firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://myapplication-d473f.firebaseio.com/"
});
ref = firebase.database().ref();

function listenForNotificationRequests() {
  var requests = ref.child('notificationRequests');
  requests.on('child_added', function(requestSnapshot) {
    var request = requestSnapshot.val();
    sendNotificationToUser(
      request.username, 
      request.message,
      function() {
        requestSnapshot.ref.remove();
      }
    );
  }, function(error) {
    console.error(error);
  });
};

function sendNotificationToUser(username, message, onSuccess) {
  request({
    url: 'https://fcm.googleapis.com/fcm/send',
    method: 'POST',
    headers: {
      'Content-Type' :' application/json',
      'Authorization': 'key='+API_KEY
    },
    body: JSON.stringify({
      notification: {
        title: message
      },
      to : '/topics/user_'+username
    })
  }, function(error, response, body) {
    if (error) { console.error(error); }
    else if (response.statusCode >= 400) { 
      console.error('HTTP Error: '+response.statusCode+' - '+response.statusMessage); 
    }
    else {
      onSuccess();
    }
  });
}

// start listening
listenForNotificationRequests();