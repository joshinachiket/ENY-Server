/**
 * New node file
 */
var mongo = require("./mongo");

var mongoURL = "mongodb://localhost:27017/EnyDatabaseMongoDB";
//var mongoSessionConnectURL = "mongodb://heroku_x4rwn6l8:nc5ua8377vca7ihtdt1pni05c9@ds117909.mlab.com:17909/heroku_x4rwn6l8";

var ejs = require("ejs");
var randomstring = require("randomstring");
var http = require('http');



exports.mobilelogin = function(req, res) {
	// These two variables come from the webpage login.html
	var username = req.param("username");
	//var uid = req.param("uid");
	var password = req.param("password");
	var device_token = req.param("device_token");

	console.log("username " + username);
	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_login = mongo.collection('login');

		collection_login.findOne({
			username : username,
			password : password
		}, function(err, user) {
			if (user) {
				console.log("user sapadala " + user);
				req.session.username = user.username;
				var uid = user.uid;
				collection_login.update({uid: uid}, {$set:{device_token:device_token}}, 
					function(err, response){
						if (err) {
							console.log("FALSE");
							json_responses = {
								"statusCode" : 999
							};
						res.send(json_responses);
						} else {
							json_responses = {
								"statusCode" : 1000,
								"uid"		 : user.uid,
								"name"		 : user.name,
								"address"	 : user.address
							};
						} 
						res.send(json_responses);
					});
			}
			else {
					json_responses = {
								"statusCode" : 999
					};
					res.send(json_responses);
				}
		});
	});
}


exports.checkLogin = function(req, res) {
	// These two variables come from the webpage login.html
	var username = req.param("username");
	var password = req.param("password");

	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_login = mongo.collection('login');

		collection_login.findOne({
			username : username,
			password : password
		}, function(err, user) {
			if (user) {
				console.log("Hi " + user.username);
				// This way subsequent requests will know the user is logged in.
				req.session.username = user.username;
				console.log(req.session.username + " IS THE SESSION OWNER");
				json_responses = {
					"statusCode" : 1000,
					"uid"		 : user.uid,
					"name"		 : user.name,
					"address"	 : user.address
				};
				//res.send(json_responses);
				res.redirect('/');
			} else {
				console.log("FALSE");
				json_responses = {
					"statusCode" : 999
				};
				res.send(json_responses);
			}
		});
	});
};



// Redirects to the homepage
exports.redirectToHomepage = function(req, res) {
	// Checks before redirecting whether the session is valid
	if (req.session.username) {
		// Set these headers to notify the browser not to maintain any cache for
		// the page being loaded
		res
				.header(
						'Cache-Control',
						'no-cache, private, no-store, must-revalidate, max-stale=0, post-check=0, pre-check=0');
		res.render("homepage", {
			username : req.session.username
		});
	} else {
		res.redirect('/');
	}
};

// Logout the user - invalidate the session
exports.logout = function(req, res) {
	req.session.destroy();
	res.redirect('/');
};

// Logout the user - invalidate the session
exports.mobilelogout = function(req, res) {

	var username = req.param("username");
	var uid = req.param("uid");

	console.log(username);
	console.log(uid);

	var json_responses;
		mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_login = mongo.collection('login');

		collection_login.updateMany(
								    		{uid : uid} ,
								   			{$set: { device_token: null }}
										, function(err, items) {	

											if (items) {
												console.log(items.modifiedCount);
												console.log("TRUE");
												json_responses = {
													"modifiedCount" : items.modifiedCount,
													"statusCode" : 1000
												};
												res.send(json_responses);

											} else {
												console.log("FALSE");
												json_responses = {
													"statusCode" : 999
												};
												res.send(json_responses);
											}
										});


	});

	req.session.destroy();

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
		  	//json_responses.statusSessionsDeleted=1000;
		    console.log('No more data in response.');
		    
		  });
		});

		req.on('error', (e) => {
		  console.log(`problem with request: ${e.message}`);
		  //json_responses.statusSessionsDeleted= 999;
		});

		// write data to request body
		req.write(postData);
		
		req.end();
	//res.redirect('/');
};




exports.register = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var name = req.param("name");
	var address = req.param("address");
	var username = req.param("username");
	var password = req.param("password");
	var device_token = req.param("device_token");
	var eny_token = req.param("eny_token");
	var uid = randomstring.generate(6);

	//console.log(req.params);

	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_login = mongo.collection('login');

		collection_login.insert({
			name 		: name,
			address 	: address,
			username 	: username,
			password 	: password,
			device_token: device_token,
			eny_token	: eny_token,
			uid 		: uid
		}, function(err, user) {
			if (user) {
				json_responses = {
					"statusCode" : 1000,
					"uid"		 : uid
				};
				res.send(json_responses);

			} else {
				console.log("FALSE");
				json_responses = {
					"statusCode" : 999
				};
				res.send(json_responses);
			}
		});
	});
};


exports.logoutall = function(req, res) {
	var username = req.param("username");

	
	var collection_session= mongo.collection('sessions');
	//db.sessions.find({"session":"{\"cookie\":{\"originalMaxAge\":null,\"expires\":null,\"httpOnly\":true,\"path\":\"/\"},\"username\":\"a\"}"})
	var user_sessions = [];
	collection_session.find({"session":"{\"cookie\":{\"originalMaxAge\":null,\"expires\":null,\"httpOnly\":true,\"path\":\"/\"},\"username\":\"" + username + "\"}"}, 
							function(err, cursor) {
	    						cursor.toArray(function(err, sessions) {
									sessions.forEach(function(item){
										user_sessions.push(item._id);
										collection_session.remove({"_id" : item._id},
											function(err){
												if (err) {
													console.log("something has gone wrong!");
													res.send({"statusCode" : 999});
												}
												else {
													console.log("Session destroyed!");	
												}
											});
									});
									res.send({"statusCode" : 1000});
								});
							
	         				});
	//res.redirect('/');
	}

//collection_session.remove( {'tagId':{'$in':["345","347"]} },{"uid" : "0D4pA1"} )

exports.registercontainer = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var tagId 			= req.param("tagId");
	var content_desc 	= req.param("content_desc");
	var max_qty 		= req.param("max_qty");
	var uid 			= req.param("uid");

	console.log(req);

	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_containers = mongo.collection('containers');

		collection_containers.insert({
			tagId 		: tagId,
			content_desc: content_desc,
			max_qty 	: max_qty,
			uid			: uid
		}, function(err, user) {
			if (user) {
				// This way subsequent requests will know the user is logged in.
				req.session.username = user.username;
				console.log(req.session.username + " IS THE SESSION OWNER");
				json_responses = {
					"statusCode" : 200
				};
				res.send(json_responses);

			} else {
				console.log("FALSE");
				json_responses = {
					"statusCode" : 401
				};
				res.send(json_responses);
			}
		});
	});
};

