var express = require('express');
var http    = require('http');
var fs      = require('fs');
var url     = require('url');
var path    = require('path');
var status  = require('./http_status.js');
var mysql   = require('mysql');
var app     = express();

//Create a connection object with the user details
var connectionPool = mysql.createPool({
    connectionLimit: 1,
    host: "localhost",
    port: 3366,
    user: "root",
    password: "",
    database: "cw1",
    debug: false
});


app.get('/', function (req, res) {
    fs.readFile('public/index.html', function(err, data) {
        res.writeHead(200, {'Content-Type': 'text/html'});
        res.write(data);
        res.end();
    });
})

app.get('/result', function (req, res) {
    fs.readFile('public/result.html', function(err, data) {
        res.writeHead(200, {'Content-Type': 'text/html'});
        res.write(data);
        res.end();
    });
})

//Set up the application to handle GET requests sent to the user path
app.get('/result/models/*', handleGetRequest);//Subfolders
app.get('/result/models', handleGetRequest);



app.use(express.static(__dirname + '/public'));
console.log("\n" + __dirname);

/* Handles GET requests sent to web service.
   Processes path and query string and calls appropriate functions to
   return the data. */
   function handleGetRequest(request, response){

    //Parse the URL
    var urlObj = url.parse(request.url, true);

    //Extract object containing queries from URL object.
    var queries = urlObj.query;

    //Get the pagination properties if they have been set. Will be  undefined if not set.
    var numItems = queries['num_items'];
    var offset = queries['offset'];
    
    //Split the path of the request into its components
    var pathArray = urlObj.pathname.split("/");

    //Get the last part of the path
    var pathEnd = pathArray[pathArray.length - 1];

    //If path ends with 'models' we return all components
    if(pathEnd === 'models'){
        getAllModelsCount(response, numItems, offset, pathEnd);
        return;
    }

    //If path ends with 'models/', we return all components
    if (pathEnd === '' && pathArray[pathArray.length - 2] === 'models'){
        getAllModelsCount(response, numItems, offset, pathEnd);
        return;
    }

    //RegEx
    var regEx = new RegExp('.*?');
    if(regEx.test(pathEnd)){
        getSpecificModelCount(response, numItems, offset, pathEnd);;
        return;
    }

    //The path is not recognized. Return an error message
    response.status(HTTP_STATUS.NOT_FOUND);
    response.send("{error: 'Path not recognized', url: " + request.url + "}");
}


/** Returns all of the models, possibly with a limit on the total number of items returned and the offset (to
 *  enable pagination). This function should be called in the callback of getTotalModelsCount  */
function getAllModels(response, totNumItems, numItems, offset, pathEnd){
    //Select the model data using JOIN to convert foreign keys into useful data.
    var sql = "SELECT brands.manufacturer, models.name, websites.url, prices.price, websites.img " +
    "FROM (((brands " +
    "INNER JOIN models ON brands.id = models.brand_id) " +
    "INNER JOIN websites ON websites.model_id = models.id) " +
    "INNER JOIN prices  ON prices.website_id = websites.id) "; 

    //Limit the number of results returned, if this has been specified in the query string
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY models.id LIMIT " + numItems + " OFFSET " + offset;
    }

    //Execute the query
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Create JavaScript object that combines total number of items with data
        var returnObj = {totNumItems: totNumItems};
        returnObj.models = result; //Array of data from database

        //Return results in JSON format
        response.json(returnObj);
    });
}

function getAllModelsCount(response, numItems, offset, pathEnd){
    var tmpPathEnd = pathEnd;
    var sql = "SELECT COUNT(*) FROM models";

    //Execute the query and call the anonymous callback function.
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Get the total number of items from the result
        var totNumItems = result[0]['COUNT(*)'];

        //Call the function that retrieves all cereals
        getAllModels(response, totNumItems, numItems, offset, tmpPathEnd);
    });
}


function getSpecificModelCount(response, numItems, offset, pathEnd){
    var sql = "";
    var tmpPathEnd = pathEnd;
    if (tmpPathEnd !== undefined){
        var find = tmpPathEnd.replace(/%20/g, " ");
        sql = "SELECT COUNT(*) FROM models WHERE upper(name) LIKE " + "'%" + find.toUpperCase() + "%'";
    }
    //Execute the query and call the anonymous callback function.
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Get the total number of items from the result
        var totNumItems = result[0]['COUNT(*)'];

        //Call the function that retrieves searched product
        getSpecificModel(response, totNumItems, numItems, offset, tmpPathEnd)
    });
}


/** Returns searched  computer componet */
function getSpecificModel(response, totNumItems, numItems, offset, pathEnd){
    var sql ="";

    if (pathEnd !== undefined){ 
        var find = pathEnd.replace(/%20/g, " ");
        //Build SQL query to select computer componet.
        sql = "SELECT brands.manufacturer, models.name, websites.url, prices.price, websites.img " +
            "FROM (((brands " +
            "INNER JOIN models ON brands.id = models.brand_id) " +
            "INNER JOIN websites ON websites.model_id = models.id) " +
            "INNER JOIN prices  ON prices.website_id = websites.id) " +
            "WHERE upper(models.full_name) LIKE " + "'%" + find.toUpperCase() + "%'";    
    }

    //Limit the number of results returned, if this has been specified in the query string
    if(numItems !== undefined && offset !== undefined ){
        sql += "ORDER BY models.id LIMIT " + numItems + " OFFSET " + offset;
    }

    //Execute the query
    connectionPool.query(sql, function (err, result) {

        //Check for errors
        if (err){
            //Not an ideal error code, but we don't know what has gone wrong.
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            return;
        }

        //Create JavaScript object that combines total number of items with data
        var returnObj = {totNumItems: totNumItems};
        returnObj.models = result; //Array of data from database

        //Return results in JSON format
        response.json(returnObj);
    });
}

var server = app.listen(8080, function () {
    var port = server.address().port
    console.log("Server is running at http://localhost:" + port)
})