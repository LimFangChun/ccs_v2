var connector = require("./NodeJS_Server.js");

var GET_FRIEND_LIST = function(topic, message){
    console.log('Getting friend list...');
    var output = "GET_FRIEND_LIST_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];

    var sql = `SELECT User.user_id, display_name, User.status, last_online,
                    course, academic_year, tutorial_group
                FROM User INNER JOIN Student ON Student.user_id = User.user_id 
                WHERE User.user_id IN (
                    SELECT friend_id as 'user_id'
                    FROM Friendship
                    WHERE Friendship.user_id = ? AND Friendship.Status = 'Friend')
                ORDER BY 
                CASE 
                    WHEN User.status = 'Online' THEN 1
                    ELSE 2
                END, 
                User.last_online DESC,
                User.display_name`;
    var input = [user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result || result.length > 0) {
            output += JSON.stringify(result);
            console.log(`Got friend list for user:".${user_id}.`);
        } else {
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var COUNT_FRIEND_REQUEST = function(topic, message){
    console.log('Counting friend requests..');
    var output = "COUNT_FRIEND_REQUEST_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];

    var sql = `SELECT COUNT(*) AS count_result FROM Friendship 
                WHERE user_id = ? AND status = 'Pending' AND sender_id <> ?`;
    var input = [user_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result || result.length > 0) {
            output += result[0][0];
            console.log(`Friends count: ".${result[0][0]}.`);
        } else {
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var REQ_ADD_FRIEND = function(topic, message){
    console.log('Requesting to add friends...');
    var output = "REQ_ADD_FRIEND_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var friend_id = receivedData[2];

    var sql = `INSERT INTO Friendship (user_id, friend_id, sender_id) 
                VALUES ?`;
    var input = [[user_id, friend_id, user_id], [friend_id, user_id, user_id]];

    connector.DB_CONNECTION.query(sql, [input], function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result) {
            output += "SUCCESS";
            console.log(`${user_id} sent friend request to ${friend_id}. feelsgoodman`);
        } else {
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var GET_FRIEND_REQUEST = function(topic, message){
    console.log('Getting friend request...');
    var output = "GET_FRIEND_REQUEST_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];

    var sql = `SELECT User.user_id, display_name, User.status, last_online, 
                    course, academic_year, tutorial_group 
                FROM Student INNER JOIN User ON User.user_id = Student.user_id 
            WHERE User.user_id IN (
                SELECT friend_id
                FROM Friendship INNER JOIN User ON Friendship.user_id = User.user_id 
                INNER JOIN Student ON Student.user_id = User.user_id 
                WHERE Friendship.user_id = ? AND Friendship.Status = 'Pending' AND 
                Friendship.sender_id <> ?
                )
            ORDER BY 
                CASE 
                    WHEN User.status = 'Online' THEN 1
                    ELSE 2
                END,
            last_online DESC,
            User.display_name`;
    var input = [user_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result || result.length > 0) {
            output += JSON.stringify(result);
            console.log(`Found friend request for user: ${user_id}`);
        } else {
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var ADD_FRIEND = function(topic, message){
    console.log('Adding friends...');
    var output = "ADD_FRIEND_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var friend_id = receivedData[2];

    var sql = `UPDATE Friendship 
                    SET status = 'Friend' 
                WHERE 
                    (user_id = ? AND friend_id = ?) OR 
                    (user_id = ? AND friend_id = ?)`;
    var input = [user_id, friend_id, friend_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result) {
            output += "SUCCESS";
            console.log(`${user_id} sent friend request to ${friend_id}. FeelsGoodMan`);
        } else {
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var DELETE_FRIEND = function(topic, message){
    console.log('Deleting friends...');
    var output = "DELETE_FRIEND_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var friend_id = receivedData[2];

    var sql = `DELETE FROM Friendship 
                WHERE 
                    (user_id = ? AND friend_id = ?) OR 
                    (user_id = ? AND friend_id = ?)`;
    var input = [user_id, friend_id, friend_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result) {
            output += "SUCCESS";
            console.log(`${user_id} and ${friend_id} are no more friends. FeelsSadMan`);
        } else {
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

module.exports = {
    GET_FRIEND_LIST,
    COUNT_FRIEND_REQUEST,
    REQ_ADD_FRIEND,
    GET_FRIEND_REQUEST,
    ADD_FRIEND,
    DELETE_FRIEND
}