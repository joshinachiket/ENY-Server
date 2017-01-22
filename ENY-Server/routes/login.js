/**
 * New node file
 */
var mongo = require("./mongo");

var mongoURL = "mongodb://10.3.16.163:27017/EnyDatabaseMongoDB";
//var mongoSessionConnectURL = "mongodb://heroku_x4rwn6l8:nc5ua8377vca7ihtdt1pni05c9@ds117909.mlab.com:17909/heroku_x4rwn6l8";

var ejs = require("ejs");
var randomstring = require("randomstring");
var http = require('http');

exports.mobilelogin = function(req, res) {
	var username = req.param("username");
	var password = req.param("password");
	var device_token = req.param("device_token");

	console.log('API Called mobilelogin...');
	console.log(req.param);

	var json_responses;

	mongo.connect(mongoURL, function() {
		var collection_login = mongo.collection('login');

		collection_login.findOne({
			username : username,
			password : password
		}, function(err, user) {
			if (user) {
				//console.log("user sapadala ";
				req.session.username = user.username;
				var uid = user.uid;
				collection_login.update({uid: uid}, {$set:{device_token:device_token}}, 
					function(err, response){
						if (err) {
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
			}else {
					json_responses = {
						"statusCode" : 999
					};
					res.send(json_responses);
			}
		});
	});
}


exports.checklogin = function(req, res) {
	var username = req.param("username");
	var password = req.param("password");
	var json_responses;

	console.log('API Called checklogin...');
	console.log(req.param);

	mongo.connect(mongoURL, function() {
		var collection_login = mongo.collection('login');
		collection_login.findOne({
			username : username,
			password : password
		}, function(err, user) {
			if (user) {
				req.session.username = user.username;

				/*
					json_responses = {
						"statusCode" : 1000,
						"uid"		 : user.uid,
						"name"		 : user.name,
						"address"	 : user.address
					};
					res.send(json_responses);
				*/
				res.redirect('/');
			} else {
				json_responses = {
					"statusCode" : 999
				};
			res.send(json_responses);
			}
		});
	});
};


exports.redirectToHomepage = function(req, res) {
	// Checks before redirecting whether the session is valid

	console.log('API Called redirectToHomepage...');
	console.log( "redirectToHomepage "  + req.session.username);
	

	if (req.session.username) {
		res.header( 'Cache-Control', 'no-cache, private, no-store, must-revalidate, max-stale=0, post-check=0, pre-check=0');							
		res.render("homepage", {username : req.session.username});
	} else {
		res.redirect('/');
	}

};



/*
exports.redirectToHomepage = function(req, res) {
	// Checks before redirecting whether the session is valid

	console.log('API Called redirectToHomepage...');
	console.log( "redirectToHomepage "  + req.session.username);
	var uid;
	mongo.connect(mongoURL, function() {
		var collection_login = mongo.collection('login');
		collection_login.findOne({
			username : req.session.username
		}, function(err,user){

			if (req.session.username) {
				res.header( 'Cache-Control', 'no-cache, private, no-store, must-revalidate, max-stale=0, post-check=0, pre-check=0');							
				res.render("homepage", {uid : req.session.uid});
				//res.render("homepage", {username : req.session.username});
				console.log(req.session.uid);
			} else {
				res.redirect('/');
			}
		});

	})

};
*/

exports.logout = function(req, res) {
	req.session.destroy();
	res.redirect('/');
};

exports.mobilelogout = function(req, res) {

	var username = req.param("username");
	var uid = req.param("uid");

	console.log('API Called mobilelogout...');
	console.log(req.param);

	var json_responses;
		mongo.connect(mongoURL, function() {
		var collection_login = mongo.collection('login');
		collection_login.updateMany(
				{uid : uid} ,{$set: { device_token: null }}
			, function(err, items) {	

				if (items) {
					console.log("TRUE");
					json_responses = {
						"modifiedCount" : items.modifiedCount,
						"statusCode" : 1000
					};
					res.send(json_responses);

				} else {
					json_responses = {
						"statusCode" : 999
					};
					res.send(json_responses);
				}
			});
	});

	//req.session.destroy();
	/*
	//Internally calls to logoutall API, don't implement such code in prod, use MQ
	var postData = JSON.stringify({
		  'username' : username
	});

	var options = {
	  hostname: '10.3.16.163',
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
	*/
};




exports.register = function(req, res) {

	var name = req.param("name");
	var address = req.param("address");
	var username = req.param("username");
	var password = req.param("password");
	var device_token = req.param("device_token");
	var eny_token = req.param("eny_token");
	var uid = randomstring.generate(6);

	console.log('API Called register...');
	console.log(req.param);

	var json_responses;

	mongo.connect(mongoURL, function() {
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

	console.log('API Called logoutall...');
	console.log(req.param);
	
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
	}

//collection_session.remove( {'tagId':{'$in':["345","347"]} },{"uid" : "0D4pA1"} )

exports.registercontainer = function(req, res) {
	var tagId 			= req.param("tagId");
	var content_desc 	= req.param("content_desc");
	var max_qty 		= req.param("max_qty");
	var uid 			= req.param("uid");
	var json_responses;

	console.log('API Called registercontainer...');
	console.log(req.param);

	mongo.connect(mongoURL, function() {
		var collection_containers = mongo.collection('containers');

		collection_containers.insert({
			tagId 		: tagId,
			content_desc: content_desc,
			max_qty 	: max_qty,
			uid			: uid
		}, function(err, user) {
			if (user) {
				req.session.username = user.username;
				json_responses = {
					"statusCode" : 200
				};
				res.send(json_responses);

			} else {
				json_responses = {
					"statusCode" : 401
				};
				res.send(json_responses);
			}
		});
	});
};

