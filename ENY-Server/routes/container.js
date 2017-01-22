var mongo = require("./mongo");
var mongoURL = "mongodb://10.3.16.163:27017/EnyDatabaseMongoDB";
//var mongoSessionConnectURL = "mongodb://heroku_x4rwn6l8:nc5ua8377vca7ihtdt1pni05c9@ds117909.mlab.com:17909/heroku_x4rwn6l8";
var ejs = require("ejs");


exports.updateqty = function(req, res) {
	var username = req.session.username;
	//var username = req.param("username");
	var tagId = req.param("tagId");
	var cur_qty = req.param("cur_qty");
	
	var json_responses;

	mongo.connect(mongoURL, function() {
		var collection_login = mongo.collection('login');
		var collection_containers = mongo.collection('containers');
		var uid; 
		collection_login.findOne({ username : username }, 
								function(err, response) {
									uid = response.uid;
									collection_containers.updateMany(
								    		{uid : uid, tagId : tagId} ,
								   			{$set: { cur_qty: cur_qty }}
										, function(err, items) {	
											if (items) {
												json_responses = {
													"modifiedCount" : items.modifiedCount,
													"statusCode" : 1000
												};
												//res.send(json_responses);
												res.redirect('/homepage');
											} else {
												json_responses = {
													"statusCode" : 999
												};
												res.send(json_responses);
											}
										});

			});

	});
	 //db.containers.updateMany({uid:"0D4pA0",tagId:"331"},{$set:{ "cur_qty":"2" }})
}


exports.containerstatus = function(req, res) {

	var uid = req.param("uid");
	var json_responses;

	mongo.connect(mongoURL, function() {
	
	var collection_containers = mongo.collection('containers');	
	collection_containers.find({uid : uid}, function(err, cursor){
	    	cursor.toArray(function(err, items) {
	                var json = {
	                				count : items.length,
	                				containers : items,
	                				statusCode : 1000

	                			}
	                 res.setHeader('Access-Control-Allow-Origin', '*');			
	                 res.send(json); 
	         });
		});
	});
}

exports.uicontainerstatus = function(req, res) {

	console.log("API called uicontainerstatus.......");
	var username = req.session.username;
	console.log("user " + username);
	var uid;
	//var ctrs;
	mongo.connect(mongoURL, function() {

		var collection_login = mongo.collection('login');
		var collection_containers = mongo.collection('containers');

		collection_login.findOne({
			username : req.session.username
		}, function(err,user){
				if (req.session.username) {	
					uid = user.uid;
					console.log(uid);	
					collection_containers.find({uid : uid}, function(err, cursor){
			    		cursor.toArray(function(err, items) {
			    			if (items.length>0) {
			    			    console.log(items);	
			                 	res.setHeader('Access-Control-Allow-Origin', '*');	
			                 	//res.render("homepage", {items : items});
			                 	res.send(items);		
			    			}
			         	});
					});				
				} else {
					res.setHeader('Access-Control-Allow-Origin', '*');	
					res.redirect('/');
				}
		});
	})
}


exports.registercontainer = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var uid = req.param("uid");
	var tagId = req.param("tagId");
	var content_desc = req.param("content_desc");
	var max_qty = req.param("max_qty");

	console.log(req.param);

	var json_responses;

	mongo.connect(mongoURL, function() {
		var collection_containers = mongo.collection('containers');

		collection_containers.insert({
			uid 			: uid,
			tagId 			: tagId,
			content_desc 	: content_desc,
			max_qty 		: max_qty,
			cur_qty			: max_qty
		}, function(err, container) {
			if (container) {
				json_responses = {
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
};


exports.deregistercontainer = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var uid = req.param("uid");
	var tagIds = req.param("containers");

	//{	
	//	"uid".        :  "value", "1D4pA1", 
	//	"containers"  : [{"content_id":"ADB1"}, {"content_id":"ADB5"}]
	//}
	//db.containers.deleteMany( { uid: "0D4pA0" }, { tagId: { $in: [ "328", "329" ] }} )
	//db.containers.remove( {'tagId':{'$in':["345","347"]} },{"uid" : "0D4pA1"} )

	var tags=[];
	tagIds.forEach(function(tag) {
    	tags.push(tag.tagId);
	});

		var collection_containers = mongo.collection('containers');
		mongo.connect(mongoURL, function() {	
		//collection_containers.deleteMany( {uid:uid}, {tagId:{ $in: tags } },	
		collection_containers.remove({tagId:{$in:tags} ,uid : uid},	
												function(err, response) {
											   		if (response) {
											   			json_responses = {
											   				"deletedCount"  : response.deletedCount,
															"statusCode" 	: 1000
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





/*
exports.deregistercontainer = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var uid = req.param("uid");
	var tagId = req.param("tagId");

	console.log(req.param);

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
*/