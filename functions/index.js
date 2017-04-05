'use strict';
const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref("light") 
.onWrite(event => {
	const root = event.data.val();
	const data = root.status;
	console.log('the light button is pressed',data);
	//if(!data.changed()){return}
	
	const clientdata=data;
	console.log('from client : '+clientdata);
	const state = clientdata.split(":")[0];
	const name = clientdata.split(":")[1];
	console.log('from client - status : '+state);
	console.log('from client - name : '+name);
	const onOff = state ? "on" : "off";

	 const payload = {
        notification: {
            title: `IoTApp ${name}`,
            body: `${onOff}`,
            sound: "default"
        }
    };
      const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
};
     console.log('Sending notifications');


            admin.messaging().sendToDevice(root.token, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
});
});

