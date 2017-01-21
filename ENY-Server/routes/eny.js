
var FCM = require('fcm-push');
var mongo = require("./mongo");
var mongoURL = "mongodb://localhost:27017/EnyDatabaseMongoDB";
var http = require('http');

/*
,
	notification: {
		title: 'ENY\'s Smart Kitchen',
		body: 'It\'s about time to buy some new grocery!'
	}

*/

exports.buttonclicked = function(req, res) {
	console.log("ENY Button clicked...");
	console.log('API Called buttonclicked...');
	console.log(req.param);

	var eny_token = req.param("eny_token");
	var button_user = req.param("button_user");
	var collection_login = mongo.collection('login');
	var registration_ids=[], uids=[], str = '', path;

	collection_login.find({"eny_token" : eny_token}, 
		function(err, cursor) {	
	    	cursor.toArray(function(err, logins) {
    			logins.forEach(function(item){
    				if (typeof item.device_token !== 'undefined' && item.device_token !== null){
    					registration_ids.push(item.device_token);
    					uids.push(item.uid);	
    				}
    				uid = uids[0];
    			});
	    		path = '/containerstatus/'+ uid ;
	    		console.log("path " + path);

                var serverKey = "AAAAiHQexwI:APA91bHEWakbIeulhDKH-4WVhrWmXVLqBlWKJQ0yZ6vMViapJ_qBKuhSw_ygB-y0kyzVhXzJqEAIs24RzWUMJqhbB0bZ_Ie6sQzujV6HSmz3WWganwSXBlBg-AngcNRPxB6h99KB3t-3";
                var fcm = new FCM(serverKey);
				
				var options = {
				  host: 'localhost',
				  path: '/containerstatus/'+ uid ,
				  port: '3000',
				  method: 'GET'
				};

				callback = function(response) {
				  	response.on('data', function (chunk) {
				    	str += chunk;
				  	});

					response.on('end', function () {
						var message = {
			    			registration_ids : registration_ids,
			    			collapse_key: 'your_collapse_key',
				    		data: {
				    			button_user : button_user,
				        		title: 'ENY\'s Smart Kitchen',
				        		body: 'It\'s about time to buy some new grocery!',
				        		info : str
				    		}
						};
						console.log(message);
						fcm.send(message, function(err, response){
		    				if (err) {
		        				console.log("Something has gone wrong!");
		        				res.send({"statusCode" : "999"});
		    				} else {
		        				console.log("Successfully sent with response: ", response);
		        				res.send({"statusCode" : "1000"});
		    				}
						});	
						
					});
				}
				var req = http.request(options, callback);
				req.end();
	      });
	});
}


/*
exports.buttonclicked = function(req, res) {
	console.log("button clicked");
	console.log(req.param("button_id"));

	console.log('CONNECTED TO MONGO AT: ' + mongoURL);
	var collection_login = mongo.collection('login');

	collection_login.find({"username" : "akshay"}, 
			function(err, cursor) {
	    		cursor.toArray(function(err, logins) {
                var serverKey = "AAAAiHQexwI:APA91bHEWakbIeulhDKH-4WVhrWmXVLqBlWKJQ0yZ6vMViapJ_qBKuhSw_ygB-y0kyzVhXzJqEAIs24RzWUMJqhbB0bZ_Ie6sQzujV6HSmz3WWganwSXBlBg-AngcNRPxB6h99KB3t-3";
                var fcm = new FCM(serverKey);
                var message = {
		    		//registration_ids : [serverKey1, serverKey2]
		    		to : "ezlfOJrJW6U:APA91bFDLR0HGJxggP2kLl6MiSOQ73L6E5hFvtPLfNN8OdvDaoM_L5OfJkjbtKUNZf_mo0AvgFkczidH2DQyBm6n5SGNNhN4HIY4mbJMeJK4LTCN-0wYjHl7_Rgga40O1Y2NlBLwX0m7",
		    		collapse_key: 'your_collapse_key',
			    	data: {
			        	key: 'value'
			    	},
			    	notification: {
			        	title: 'title',
			        	body: 'body'
		    		}
				};

				fcm.send(message, function(err, response){
	    			if (err) {
	        			console.log("Something has gone wrong!");
	        			res.send({"statusCode" : "999"});
	    			} else {
	        		console.log("Successfully sent with response: ", response);
	        		res.send({"statusCode" : "1000"});
	    			}
				});	


	      	});
	});
}
*/


