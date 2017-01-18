/**
 * New node file
 */
var mongo = require("./mongo");
var mongoURL = "mongodb://localhost:27017/EnyDatabaseMongoDB";
//var mongoSessionConnectURL = "mongodb://heroku_x4rwn6l8:nc5ua8377vca7ihtdt1pni05c9@ds117909.mlab.com:17909/heroku_x4rwn6l8";
var ejs = require("ejs");

exports.containerstatus = function(req, res) {
	var uid = req.body.uid;

	console.log(req.body);

	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_containers = mongo.collection('containers');
		collection_containers.find({uid : uid}
			, function(err, cntrs) {
			if (cntrs) {
				console.log(cntrs);
				json_responses = {
					"statusCode" : 1000,
					"containers" : cntrs
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


exports.registercontainer = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var uid = req.param("uid");
	var tagId = req.param("tagId");
	var content_desc = req.param("content_desc");
	var max_qty = req.param("max_qty");

	console.log(req.params);

	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_containers = mongo.collection('containers');

		collection_containers.insert({
			uid 			: uid,
			tagId 			: tagId,
			content_desc 	: content_desc,
			max_qty 		: max_qty
		}, function(err, container) {
			if (container) {
				console.log(container);
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
};



exports.deregistercontainer = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var uid = req.param("uid");
	var tagId = req.param("tagId");

	console.log(req.params);

	var json_responses;
	console.log('CONNECTED TO MONGO AT: ' + mongoURL);
	var collection_containers = mongo.collection('containers');

	mongo.connect(mongoURL, function() {	
		collection_containers.findOneAndDelete({
													$and: [{uid:uid}, {tagId:tagId}]
											   },	function(err, container) {
											   		if (container) {
											   			console.log("DELETED CONTAINET tagID :" + container.tagId + " uid " + uid);
											   			json_responses = {
															"statusCode" : 1000
														};
														res.send(json_responses);
											   		}
											   		else {
											   			json_responses = {
															"statusCode" : 999
														};
														res.send(json_responses);
											   		} 
											   });
	});

};
