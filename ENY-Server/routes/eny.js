var FCM = require('fcm-push');

exports.buttonclicked = function(req, res) {
	console.log("button clicked");

	var serverKey = 'cclOUm4ReqU:APA91bFQTm5YPDLvbu-8dvYhDnjE4AJoTPNtMxFr4sfF7nQ16NOulEHrcIWMrIvlHGSTGGRD3qQBsJKJp-rFxTWC_8xM9x4988iOEVmGdGvAHYCj3lCZ69eMBRLl4DeK7QaWjylVkVcD';
	var fcm = new FCM(serverKey);

	var message = {
    	to: 'cclOUm4ReqU:APA91bFQTm5YPDLvbu-8dvYhDnjE4AJoTPNtMxFr4sfF7nQ16NOulEHrcIWMrIvlHGSTGGRD3qQBsJKJp-rFxTWC_8xM9x4988iOEVmGdGvAHYCj3lCZ69eMBRLl4DeK7QaWjylVkVcD', // required fill with device token or topics
    	collapse_key: 'your_collapse_key',
    	data: {
        	key: 'value'
    	},
    	notification: {
        	title: 'Node Js title',
        	body: 'Node Js body'
    	}
	};

	//callback style
	fcm.send(message, function(err, response){
    	if (err) {
        	console.log("Something has gone wrong!");
    	} else {
        	console.log("Successfully sent with response: ", response);
    	}
	});

	var json_responses;
	res.send({"status_code" : "1000"});

}



