var mysql = require('mysql');
var mqtt = require('mqtt');
var serverAddress = 'tcp://172.22.11.153:1883';
var mqttClient = mqtt.connect(serverAddress);
var DB_CONNECTION;

initializeMqttConnection();
initializeDbConnection();

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
	var EndToEndEncryptionModule = require("./EndToEndEncryptionModule.js");
    var temp = message.toString().split(',');
    switch (temp[0]) {
        case "SEND_ROOM_MESSAGE":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            SEND_ROOM_MESSAGE(topic, message);
            break;
        case "FIND_BY_ADDRESS":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            FindFriendModule.FIND_BY_ADDRESS(topic, message);
            break;
        case "FIND_BY_PROGRAMME":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            FindFriendModule.FIND_BY_PROGRAMME(topic, message);
            break;
        case "FIND_BY_TUTORIAL_GROUP":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            FindFriendModule.FIND_BY_TUTORIAL_GROUP(topic, message);
            break;
        case "FIND_BY_AGE":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            FindFriendModule.FIND_BY_AGE(topic, message);
            break;
        case "FIND_BY_LOCATION":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            FindFriendModule.FIND_BY_LOCATION(topic, message);
            break;
        case "UPDATE_LOCATION":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            FindFriendModule.UPDATE_LOCATION(topic, message);
            break;
        case "UPDATE_PUBLIC_KEY":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            EndToEndEncryptionModule.UPDATE_PUBLIC_KEY(topic, message);
            break;
        case "GET_PUBLIC_KEY":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            EndToEndEncryptionModule.GET_PUBLIC_KEY(topic, message);
            break;
        case "GET_PUBLIC_KEY_ROOM":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            EndToEndEncryptionModule.GET_PUBLIC_KEY_ROOM(topic, message);
            break;
        case "GET_CHATROOM_SECRET":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            EndToEndEncryptionModule.GET_CHATROOM_SECRET(topic, message);
            break;
        case "GET_CHATROOM_SECRET_ALL":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            EndToEndEncryptionModule.GET_CHATROOM_SECRET_ALL(topic, message);
            break;
        case "SET_CHATROOM_SECRET":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            EndToEndEncryptionModule.SET_CHATROOM_SECRET(topic, message);
            break;
        case "GET_FORBIDDEN_SECRETS":
            console.log('================================================');
            console.log("Receiving message");
            console.log("Topic: " + topic);
            console.log("Message: " + message);
            EndToEndEncryptionModule.GET_FORBIDDEN_SECRETS(topic, message);
            break;
    }
}

function SEND_ROOM_MESSAGE(topic, message) {
    console.log('Storing received room message...');
    var output = "NO_PUB,";

    var receivedData = message.toString().substring(message.toString().indexOf(',') + 1);
    var messageJSON = JSON.parse(receivedData);

    var sql = `INSERT INTO Message (message, sender_id, room_id, message_type, media) 
                        VALUES (?, ?, ?, ?, ?)`;
    var input = [messageJSON['message'], messageJSON['sender_id'], messageJSON['room_id'], messageJSON['message_type'], messageJSON['media']];

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
