/**
 * New node file
 */
var mongo = require("./mongo");
var mongoURL = "mongodb://localhost:27017/EnyDatabaseMongoDB";
//var mongoSessionConnectURL = "mongodb://heroku_x4rwn6l8:nc5ua8377vca7ihtdt1pni05c9@ds117909.mlab.com:17909/heroku_x4rwn6l8";
var ejs = require("ejs");
var http = require('http');

exports.deregisteruser = function(req, res) {
	var uid = req.param("uid");
	var username = req.param("username");

	var flag=true;
	var json_responses;

	console.log('API Called deregisteruser...');
	console.log(req.param);

	mongo.connect(mongoURL, function() {
		var collection_login = mongo.collection('login');
		var collection_sessions = mongo.collection('sessions');
		var collection_containers = mongo.collection('containers');

		//Destroy all containers record
		collection_containers.remove(
						{uid : uid},	
						function(err, response) {
					   		if (response) {
					   			console.log("container records deleted...");
					   		}
					   		else {
								console.log("Something went wrong while deleting containers...!");
							} 
		});


		//Making HTTP call to own API /logoutall just to try http req concept, don't try in production!
		// Destroy all sessions for the user
		var postData = JSON.stringify({
		  'username' : username
		});

		var options = {
		  hostname: 'localhost',
		  port: 3000,
		  path: '/logoutall',
		  method: 'POST',
		  headers: {
		    'Content-Type': 'application/json'
		  }
		};

		var req = http.request(options, (res) => {
		  //console.log(`STATUS: ${res.statusCode}`);
		  //console.log(`HEADERS: ${JSON.stringify(res.headers)}`);
		  res.setEncoding('utf8');
		  res.on('data', (chunk) => {
		    console.log(`BODY: ${chunk}`);

		  });
		  res.on('end', () => {
		    console.log('No more data in response.');
		    
		  });
		});

		req.on('error', (e) => {
		  console.log(`problem with request: ${e.message}`);
		});

		req.write(postData);
		req.end();


		//Destroy user account records	
		collection_login.remove(
				{uid : uid}, 
				function(err){			   			
			   		if (err) {
			   			flag = false; 
			   			console.log("Something wrong while deleting login records : " + err);
			   		}
		});	

		if (flag)
			json_responses = { "statusCode" :  1000}
		else 
			json_responses = { "statusCode" :  999}

	 	res.send(json_responses);	
	});
}


exports.updatetoken = function(req, res) {
	var uid = req.body.uid;
	var device_token = req.body.device_token;

	console.log('API Called updatetoken...');
	console.log(req.param);

	var json_responses;

	mongo.connect(mongoURL, function() {
		var collection_login = mongo.collection('login');

		collection_login.update(
    		{ uid : uid},
   			{$set: { device_token: device_token}}
		, function(err, user) {
			if (user) {
				console.log( "Token updated for user " +  user.name);
				json_responses = {
					"statusCode" : 1000
				};
				res.send(json_responses);

			} else {
				console.log("Something wrong while updating token records...!" );
				json_responses = {
					"statusCode" : 999
				};
				res.send(json_responses);
			}
		});
	});
}



exports.list = function(req, res){
  res.send("Yet to be implemented!");
};
