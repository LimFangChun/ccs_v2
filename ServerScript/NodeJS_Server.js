var mysql = require('mysql');
var mqtt = require('mqtt');
var serverAddress = 'tcp://172.22.6.237:1883';//change to broker's ip
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
    var ChatModule = require("./ChatModule.js");
    var temp = message.toString().split(',');
    switch (temp[0]) {
        //chat module
        case "SEND_ROOM_MESSAGE":
            ChatModule.SEND_ROOM_MESSAGE(topic, message);
            break;
        case "SEND_ROOM_IMAGE":
            ChatModule.SEND_ROOM_IMAGE(topic, message);
            break;
        case "DOWNLOAD_IMAGE":
            ChatModule.DOWNLOAD_IMAGE(topic, message);
            break;
        case "PIN_MESSAGE":
            ChatModule.PIN_MESSAGE(topic, message);
            break;
        case "UNPIN_MESSAGE":
            ChatModule.UNPIN_MESSAGE(topic, message);
            break;
        case "GET_PINNED_MESSAGE":
            ChatModule.GET_PINNED_MESSAGE(topic, message);
            break;
        case "DELETE_MESSAGE":
            ChatModule.DELETE_MESSAGE(topic, message);
            break;

        //find friend module
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


