var connector = require("./NodeJS_Server.js");

var FIND_BY_ADDRESS = function (topic, message) {
    console.log('Finding friend by address, city and state...');
    var output = "FIND_BY_ADDRESS_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var city_id = receivedData[2];

    var sql = `SELECT
    User.user_id, User.display_name, User.status, User.last_online, 
    Student.course, Student.academic_year, Student.tutorial_group, last_latitude, last_longitude
    FROM User 
        INNER JOIN City ON User.city_id = City.city_id 
        INNER JOIN State ON City.state_id = State.state_id 
        INNER JOIN Student ON User.user_id = Student.user_id 
    WHERE 
        State.state_id = (SELECT DISTINCT state_id 
                FROM City 
                WHERE city_id = ?) AND 
        User.user_id NOT IN 
            (
                SELECT Friendship.friend_id 
                FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
                WHERE Friendship.user_id = ?
            ) AND
        User.user_id <> ?
    ORDER BY 
        CASE 
            WHEN User.status = 'Online' THEN 1
            ELSE 2
        END, 
        CASE 
            WHEN User.city_id = ? THEN 1 
            ELSE 2
        END, 
        DATE(last_online) DESC, 
        User.display_name`;

    //prevent sql injections
    var inserts = [city_id, user_id, user_id, city_id];

    executeQuery(sql, inserts, topic, output);
}

var FIND_BY_PROGRAMME = function (topic, message) {
    console.log('Finding friend by address, city and state...');
    var output = "FIND_BY_PROGRAMME_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var faculty = receivedData[2];
    var course = receivedData[3];
    var tutorial_group = receivedData[4];

    var sql = `SELECT
                User.user_id, User.display_name, User.status, User.last_online, 
                Student.course, Student.academic_year, Student.tutorial_group, last_latitude, last_longitude
            FROM Student INNER JOIN User ON User.user_id = Student.user_id 
            WHERE 
                faculty = ? AND 
                course = ? AND
                User.user_id NOT IN 
                (
                    SELECT Friendship.friend_id
                    FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
                    WHERE Friendship.user_id = ?
                ) AND
                User.user_id <> ?
            ORDER BY 
            CASE 
                WHEN User.status = 'Online' THEN 1
                ELSE 2
            END,  
            CASE 
                WHEN faculty = ? THEN 1
                ELSE 2
            END, 
            CASE 
                WHEN course = ? THEN 1
                ELSE 2
            END, 
            CASE 
                WHEN tutorial_group = ? THEN 1
                ELSE 2
            END,
            DATE(last_online) DESC,
            User.display_name`;

    //prevent sql injections
    var inserts = [faculty, course, user_id, user_id, faculty, course, tutorial_group];

    executeQuery(sql, inserts, topic, output);
}

var FIND_BY_TUTORIAL_GROUP = function (topic, message) {
    console.log('Finding friends by tutorial group...');
    var output = "FIND_BY_TUTORIAL_GROUP_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var faculty = receivedData[2];
    var course = receivedData[3];
    var tutorial_group = receivedData[4];
    var intake = receivedData[5];
    var academic_year = receivedData[6];

    var sql = `SELECT
    User.user_id, User.display_name, User.status, User.last_online, 
    Student.course, Student.academic_year, Student.tutorial_group, last_latitude, last_longitude
FROM Student INNER JOIN User ON User.user_id = Student.user_id 
WHERE 
    faculty = ? AND 
    course = ? AND 
    intake = ? AND 
    academic_year = ? AND 
    tutorial_group = ? AND
    User.user_id NOT IN 
        (
            SELECT Friendship.friend_id
            FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
            WHERE Friendship.user_id = ? AND Friendship.Status = 'Friend'
        ) AND
    User.user_id <> ?
ORDER BY 
    CASE 
        WHEN User.status = 'Online' THEN 1
        ELSE 2
    END,
    last_online DESC,
    User.display_name`;

    //prevent sql injections
    var inserts = [faculty, course, intake, academic_year, tutorial_group, user_id, user_id];

    executeQuery(sql, inserts, topic, output);
}

var FIND_BY_AGE = function (topic, message) {
    console.log('Finding friends by age...');
    var output = "FIND_BY_AGE_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var year = receivedData[2];
    var faculty = receivedData[3];
    var course = receivedData[4];

    var sql = `SELECT
    User.user_id, User.display_name, User.status, User.last_online, 
    Student.course, Student.academic_year, Student.tutorial_group, last_latitude, last_longitude
FROM Student INNER JOIN User ON User.user_id = Student.user_id 
WHERE 
    CAST(SUBSTRING(nric, 1, 2) AS INTEGER) = ? AND 
    User.user_id NOT IN 
        (
            SELECT Friendship.friend_id
            FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
            WHERE Friendship.user_id = ?
        ) AND
    User.user_id <> ?
ORDER BY 
    CASE 
        WHEN User.status = 'Online' THEN 1
        ELSE 2
    END,  
    CASE 
        WHEN faculty = ? THEN 1
        ELSE 2
    END, 
    CASE 
        WHEN course = ? THEN 1
        ELSE 2
    END,
    DATE(last_online) DESC,
    User.user_id`;

    //prevent sql injections
    var inserts = [year, user_id, user_id, faculty, course];

    executeQuery(sql, inserts, topic, output);
}

//source for formula to calculate distance between 2 longitude and latitude
//https://gist.github.com/Usse/4086343
var FIND_BY_LOCATION = function (topic, message) {
    console.log('Finding friends by location and gps...');
    var output = "FIND_BY_LOCATION_REPLY,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var longitude = receivedData[2];
    var latitude = receivedData[3];

    var sql = `SELECT 
    User.user_id, User.display_name, User.status, User.last_online, 
    Student.course, Student.academic_year, Student.tutorial_group, last_latitude, last_longitude, 
    (((acos(sin((? * pi()/180)) 
        * sin((last_latitude * pi()/180))
        + cos((? * pi()/180)) 
        * cos((last_latitude * pi()/180))
        * cos(((? - last_longitude) * pi()/180)))) * 180/pi())*60*1.1515) AS distance
FROM Student INNER JOIN User ON User.user_id = Student.user_id  
WHERE
    User.user_id <> ? AND 
    User.user_id NOT IN 
        (
            SELECT Friendship.friend_id AS 'user_id'
            FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
            WHERE Friendship.user_id = ?
        )
ORDER BY 
    CASE 
        WHEN User.status = 'Online' THEN 1
        ELSE 2
    END,
    distance
LIMIT 20`;

    //prevent sql injections
    var inserts = [latitude, latitude, longitude, user_id, user_id];

    executeQuery(sql, inserts, topic, output);
}

var ADVANCED_SEARCH = function (topic, message) {
    console.log('Executing advanced saerch');
    var output = "ADVANCED_SEARCH_REPLY,";

    var receivedData = message.toString().substring(message.toString().indexOf(',') + 1);
    var dataJson = JSON.parse(receivedData);
    var user_id = dataJson['user_id'];
    var tutorial_group = dataJson['tutorial_group'];
    var year = dataJson['academic_year'];
    var course = dataJson['course'];
    var faculty = dataJson['faculty'];

    //generate query
    var sql = `SELECT
    User.user_id, User.display_name, User.status, User.last_online, 
    Student.course, Student.academic_year, Student.tutorial_group, last_latitude, last_longitude
FROM Student INNER JOIN User ON User.user_id = Student.user_id 
WHERE 
    User.user_id <> ?`;

    var inserts = [user_id];

    //if not empty string
    if (faculty.toString().trim()) {
        sql += " AND faculty = ?";
        inserts.push(faculty);
    }

    if (course.toString().trim()) {
        sql += " AND course = ?";
        inserts.push(course);
    }

    if (year.toString().trim()) {
        sql += " AND academic_year = ?";
        inserts.push(year);
    }

    if (tutorial_group.toString().trim()) {
        sql += " AND tutorial_group = ?";
        inserts.push(tutorial_group);
    }

    sql += " ORDER BY DATE(last_online) DESC, student_id";

    executeQuery(sql, inserts, topic, output);
}

var UPDATE_LOCATION = function (topic, message) {
    console.log('Finding friends by location and gps...');
    var output = "NO_PUB,";

    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];
    var longitude = receivedData[2];
    var latitude = receivedData[3];

    var sql = `UPDATE User SET last_longitude = ?, last_latitude = ? WHERE user_id = ?`;

    //prevent sql injections
    var inserts = [longitude, latitude, user_id];

    connector.DB_CONNECTION.query(sql, inserts, function (err, result) {
        if (err) {
            console.log(err);
            output += "NO_RESULT";
        } else if (result || result.length > 0) {
            output += JSON.stringify(result);
            console.log(output.substring(0, 200) + "...");
        } else {
            output += "NO_RESULT";
        }
    });
}

function executeQuery(sql, inserts, topic, output) {
    connector.DB_CONNECTION.query(sql, inserts, function (err, result) {
        if (err) {
            console.log(err);
            output += "NO_RESULT";
        } else if (result || result.length > 0) {
            output += JSON.stringify(result);
            console.log(output.substring(0, 200) + "...");
        } else {
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
    });
}

//export all the functions to be called in main script, NodeJS_Server.js
module.exports = {
    FIND_BY_ADDRESS,
    FIND_BY_PROGRAMME,
    FIND_BY_TUTORIAL_GROUP,
    FIND_BY_AGE,
    FIND_BY_LOCATION,
    UPDATE_LOCATION,
    ADVANCED_SEARCH
}