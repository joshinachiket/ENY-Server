/**
 * New node file
 */
var mongo = require("./mongo");
//var mongoURL = "mongodb://localhost:27017/EnyDatabaseMongoDB";
var mongoURL = "mongodb://heroku_0z017gpr:dshkpnq53po2r0hgh4r3h8qjne@ds117909.mlab.com:17909/heroku_0z017gpr";
var ejs = require("ejs");


exports.updatetoken = function(req, res) {
	//var uid = req.param("uid");
	//var device_token = req.param("device_token");
	var uid = req.body.uid;
	var device_token = req.body.device_token;

	console.log(req.body.uid);
	console.log(req.body.device_token);
	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_login = mongo.collection('login');

		collection_login.update(
    		{ uid : uid},
   			{$set: { device_token: device_token }}
		, function(err, user) {
			if (user) {
				console.log( "Token updated for user " +  user.name);
				json_responses = {
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
}

/*
 * GET users listing.
 */

exports.list = function(req, res){
  res.send("respond with a resource");
};
