'use strict';
const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
exports.sendConnectionNotification=functions.database.ref("rpi/connected")
.onWrite(event =>{
    const connect=event.data.val();
    console.log('rpi status is ',connect);
    const payload={
        notification:{
            title: `IoTApp`,
            body: `rpi status is ${connect}`,
            sound: "default"
        }
    };
     const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
};
         console.log('Sending notifications from rpi status');
return admin.messaging().sendToTopic("rpi_status", payload, options);    

});


exports.sendNotification = functions.database.ref("light") 
.onWrite(event => {
	const root = event.data.val();
	const data = root.status;
	console.log('the light button is pressed',data);
	//if(!data.changed()){return}
	
	const clientdata=data;
	console.log('from client : '+clientdata);
	var state = clientdata.split(":")[0];
	const name = clientdata.split(":")[1];
	console.log('from client - status : '+state);
	console.log('from client - name : '+name);
	var onOff = "off";
    if (state == "true"){onOff="on";}
    console.log('from OnOFF - name : '+onOff);

	 const payload = {
        notification: {
            title: `IoTApp`,
            body: `${name} has turned the light ${onOff}`,
            sound: "default"
        }
    };
      const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
};
     console.log('Sending notifications');
     
return admin.messaging().sendToTopic("Power_Notifications", payload, options);    

        admin.messaging().sendToDevice(root.token, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
});
});