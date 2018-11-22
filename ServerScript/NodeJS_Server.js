var mysql = require('mysql');
var mqtt = require('mqtt');
var serverAddress = 'tcp://172.16.114.123:1883';//change to broker's ip
var mqttClient = mqtt.connect(serverAddress);
var DB_CONNECTION;

initializeMqttConnection();
initializeDbConnection();

module.exports = {
    mqttClient,
    DB_CONNECTION
}

process.on('SIGINT', () => {
    console.log('================================================');
    console.log('Process Interrupted, exiting');

    //disconnect from mqtt broker
    mqttClient.end();
    console.log('Disconnected from mqtt broker at ' + serverAddress);

    //disconnect from database
    DB_CONNECTION.end();
    console.log('Disconected from database');

    console.log('================================================');

    //kill the program
    process.exit();
});

function initializeMqttConnection() {
    mqttClient.on('connect', function () {
        mqttClient.subscribe('/MY/TARUC/CCS/000000001/PUB/#');
        console.log('================================================');
        console.log('Node.js has connected to mqtt broker at ' + serverAddress);
    });

    mqttClient.on('message', function (topic, message) {
        console.log('================================================');
        console.log("Receiving message");
        console.log("Topic: " + topic);
        console.log("Message: " + message.toString().substring(0, 100) + "...");
        processReceivedData(topic, message);
    });
}

function initializeDbConnection() {
    DB_CONNECTION = mysql.createConnection({
        host: "localhost",
        user: "ccs_main",
        password: "123456",
        database: "ccs_master"
    });

    DB_CONNECTION.connect(function (err) {
        if (err) throw err;
        console.log("Connected to database");
        console.log('================================================\n');
    });
}

function processReceivedData(topic, message) {
    var FindFriendModule = require("./FindFriendModule.js");
    var temp = message.toString().split(',');
    switch (temp[0]) {
        case "SEND_ROOM_MESSAGE":
            SEND_ROOM_MESSAGE(topic, message);
            break;
        case "FIND_BY_ADDRESS":
            FindFriendModule.FIND_BY_ADDRESS(topic, message);
            break;
        case "FIND_BY_PROGRAMME":
            FindFriendModule.FIND_BY_PROGRAMME(topic, message);
            break;
        case "FIND_BY_TUTORIAL_GROUP":
            FindFriendModule.FIND_BY_TUTORIAL_GROUP(topic, message);
            break;
        case "FIND_BY_AGE":
            FindFriendModule.FIND_BY_AGE(topic, message);
            break;
        case "FIND_BY_LOCATION":
            FindFriendModule.FIND_BY_LOCATION(topic, message);
            break;
        case "UPDATE_LOCATION":
            FindFriendModule.UPDATE_LOCATION(topic, message);
            break;
        case "ADVANCED_SEARCH":
            FindFriendModule.ADVANCED_SEARCH(topic, message);
            break;
        default:
            console.log('Invalid header');
            console.log('================================================');
    }
}

function SEND_ROOM_MESSAGE(topic, message) {
    console.log('Storing received room message...');
    var output = "NO_PUB,";

    var receivedData = message.toString().substring(message.toString().indexOf(',') + 1);
    var messageJSON = JSON.parse(receivedData);

    var sql = `INSERT INTO Message (message, sender_id, room_id) 
                        VALUES (?, ?, ?)`;
    var input = [messageJSON['message'], messageJSON['sender_id'], messageJSON['room_id']];

    DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result || result.length > 0) {
            output += "SUCCESS";
        } else {
            output += "NO_RESULT";
        }
        console.log("Message has been stored");
        console.log('Output: ' + output);
    });
}
