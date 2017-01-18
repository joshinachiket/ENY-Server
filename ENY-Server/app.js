
/**
 * Module dependencies.
 */

var express			= require('express');
var routes			= require('./routes');
var user			= require('./routes/user');
var http			= require('http');
var path			= require('path');

var mongoSessionConnectURL = "mongodb://localhost:27017/EnyDatabaseMongoDB";
//var mongoSessionConnectURL = "mongodb://heroku_x4rwn6l8:nc5ua8377vca7ihtdt1pni05c9@ds117909.mlab.com:17909/heroku_x4rwn6l8";
var expressSession = require("express-session");
var mongoStore = require("connect-mongo")(expressSession);
var mongo = require("./routes/mongo");
var login = require("./routes/login");
var container = require("./routes/container");
var user = require("./routes/user");


var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());

app.use(expressSession({
	secret: 'eny-hackathon-serve',
	resave: false,  //don't save session if unmodified
	saveUninitialized: false,	// don't create session until something stored
	duration: 30 * 60 * 1000,    
	activeDuration: 5 * 60 * 1000,
	store: new mongoStore({
		url: mongoSessionConnectURL
	})
}));

app.use(express.cookieParser());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));


// development only
if ('development' === app.get('env')) {
  app.use(express.errorHandler());
}


//GET REQUESTS
app.get('/', routes.index);
app.get('/users', user.list);
app.get('/homepage',login.redirectToHomepage);

//POST REQUESTS
app.post('/checklogin', login.checkLogin);
app.post('/register', login.register);
app.post('/registercontainer', container.registercontainer);
app.post('/deregistercontainer', container.deregistercontainer);
app.post('/logout', login.logout);
app.post('/updatetoken', user.updatetoken);
app.post('/containerstatus', container.containerstatus);

//connect to the mongo collection session and then createServer
mongo.connect(mongoSessionConnectURL, function(){
	console.log('CONNECTED TO MONGO AT: ' + mongoSessionConnectURL);
	http.createServer(app).listen(app.get('port'), function(){
		console.log('EXPRESS SERVER LISTENING ON PORT ' + app.get('port'));
	});  
});
