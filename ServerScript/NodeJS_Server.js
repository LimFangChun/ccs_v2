var mysql = require('mysql');
var mqtt = require('mqtt');
var serverAddress = 'tcp://172.16.118.222:1883';//change to broker's ip
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
        database: "ccs_master",
        timezone: 'utc'
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
    var FriendManagementModule = require("./FriendManagementModule.js");
	var EndToEndEncrpytionModule = require("./EndToEndEncryptionModule.js");
    var temp = message.toString().split(',');
    switch (temp[0]) {
        case "SEND_FEEDBACK":
            SEND_FEEDBACK(topic, message);
            break;

        //chat module
        case "GET_CHAT_ROOM":
            ChatModule.GET_CHAT_ROOM(topic, message);
            break;
        case "GET_ROOM_MESSAGE":
            ChatModule.GET_ROOM_MESSAGE(topic, message);
            break;
        case "DELETE_CHAT_ROOM":
            ChatModule.DELETE_CHAT_ROOM(topic, message);
            break;
        case "GET_ROOM_INFO":
            ChatModule.GET_ROOM_INFO(topic, message);
            break;
        case "CHAT_BOT":
            ChatModule.CHAT_BOT(topic, message);
            break;
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
        case "MODIFY_MESSAGE":
            ChatModule.MODIFY_MESSAGE(topic, message);
            break;
        case "ADD_PEOPLE_TO_GROUP":
            ChatModule.ADD_PEOPLE_TO_GROUP(topic, message);
            break;
        case "REMOVE_PEOPLE_FROM_GROUP":
            ChatModule.REMOVE_PEOPLE_FROM_GROUP(topic, message);
            break;
        case "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD":
            ChatModule.GET_FRIEND_LIST_FOR_PARTICIPANT_ADD(topic, message);
            break;
        case "GET_PARTICIPANT_LIST_REMOVE":
            ChatModule.GET_PARTICIPANT_LIST_REMOVE(topic, message);
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

        //friend management module
        case "GET_FRIEND_LIST":
            FriendManagementModule.GET_FRIEND_LIST(topic, message);
            break;
        case "COUNT_FRIEND_REQUEST":
            FriendManagementModule.COUNT_FRIEND_REQUEST(topic, message);
            break;
        case "REQ_ADD_FRIEND":
            FriendManagementModule.REQ_ADD_FRIEND(topic, message);
            break;
        case "GET_FRIEND_REQUEST":
            FriendManagementModule.GET_FRIEND_REQUEST(topic, message);
            break;
        case "ADD_FRIEND":
            FriendManagementModule.ADD_FRIEND(topic, message);
            break;
        case "DELETE_FRIEND":
            FriendManagementModule.DELETE_FRIEND(topic, message);
            break;
            
		case "UPDATE_PUBLIC_KEY":
            EndToEndEncrpytionModule.UPDATE_PUBLIC_KEY(topic, message);
            break;
		case "GET_PUBLIC_KEY_ROOM":
            EndToEndEncrpytionModule.GET_PUBLIC_KEY_ROOM(topic, message);
            break;
		case "GET_PUBLIC_KEY":
            EndToEndEncrpytionModule.GET_PUBLIC_KEY(topic, message);
            break;
		case "GET_CHATROOM_SECRET":
            EndToEndEncrpytionModule.GET_CHATROOM_SECRET(topic, message);
            break;
		case "GET_CHATROOM_SECRET_ALL":
            EndToEndEncrpytionModule.GET_CHATROOM_SECRET_ALL(topic, message);
            break;
		case "SET_CHATROOM_SECRET":
            EndToEndEncrpytionModule.SET_CHATROOM_SECRET(topic, message);
            break;
		case "GET_FORBIDDEN_SECRETS":
            EndToEndEncrpytionModule.GET_FORBIDDEN_SECRETS(topic, message);
            break;
        default:
            console.log('Invalid header');
    }
}

function SEND_FEEDBACK(topic, message) {
    console.log("Inserting new feedback...");
    var output = "SEND_FEEDBACK_REPLY,";
    var receivedData = message.toString().substring(message.toString().indexOf(',') + 1);
    receivedData = JSON.parse(receivedData);
    var feedbackMessage = receivedData['message'];
    var user_id = receivedData['user_id'];
    var rate = receivedData['rate'];

    var sql = `INSERT INTO Feedback(message, rate, user_id) VALUES(?, ?, ?)`;
    var input = [feedbackMessage, rate, user_id];

    DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err);
            output += "NO_RESULT";
        } else if (result) {
            console.log(`New feedback has been inserted`);
            output += "SUCCESS";
        } else {
            console.log("Error: " + err);
            output += "NO_RESULT";
        }

        mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}
