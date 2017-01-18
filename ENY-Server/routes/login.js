/**
 * New node file
 */
var mongo = require("./mongo");
//var mongoURL = "mongodb://localhost:27017/EnyDatabaseMongoDB";
var mongoURL = "mongodb://heroku_x4rwn6l8:nc5ua8377vca7ihtdt1pni05c9@ds117909.mlab.com:17909/heroku_x4rwn6l8";
var ejs = require("ejs");

exports.checkLogin = function(req, res) {
	// These two variables come from the webpage login.html
	var username = req.param("username");
	var password = req.param("password");

	console.log(username);
	console.log(password);

	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_login = mongo.collection('login');

		collection_login.findOne({
			username : username,
			password : password
		}, function(err, user) {
			if (user) {
				console.log("hi: " + user.username);
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

exports.register = function(req, res) {
	// These two variables come from the form on
	// the views/login.hbs page
	var name = req.param("name");
	var address = req.param("address");
	var username = req.param("username");
	var password = req.param("password");
	var device_token = req.param("device_token");

	console.log(req);

	var json_responses;

	mongo.connect(mongoURL, function() {
		console.log('CONNECTED TO MONGO AT: ' + mongoURL);
		var collection_login = mongo.collection('login');

		collection_login.insert({
			name 		: name,
			address 	: address,
			username 	: username,
			password 	: password,
			device_token: device_token
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
