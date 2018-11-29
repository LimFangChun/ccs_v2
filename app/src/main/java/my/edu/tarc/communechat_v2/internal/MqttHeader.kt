package my.edu.tarc.communechat_v2.internal

object MqttHeader {
    //general
    const val SUCCESS = "SUCCESS"
    const val NO_RESULT = "NO_RESULT"
    const val DUPLICATED = "DUPLICATED"
    const val LOGIN = "LOGIN"
    const val LOGIN_REPLY = "LOGIN_REPLY"
    const val REGISTER_USER = "REGISTER_USER"
    const val REGISTER_USER_REPLY = "REGISTER_USER_REPLY"
    const val UPDATE_USER_STATUS = "UPDATE_USER_STATUS"
    const val UPDATE_STUDENT = "UPDATE_STUDENT"
    const val UPDATE_USER = "UPDATE_USER"
    const val NO_REPLY = "NO_REPLY"

    //Chatting module
    const val GET_CHAT_ROOM = "GET_CHAT_ROOM"
    const val GET_CHAT_ROOM_REPLY = "GET_CHAT_ROOM_REPLY"
    const val GET_ROOM_MESSAGE = "GET_ROOM_MESSAGE"
    const val GET_ROOM_MESSAGE_REPLY = "GET_ROOM_MESSAGE_REPLY"
    const val SEND_ROOM_MESSAGE = "SEND_ROOM_MESSAGE"
    const val SEND_ROOM_IMAGE = "SEND_ROOM_IMAGE"
    const val CREATE_CHAT_ROOM = "CREATE_CHAT_ROOM"
    const val CREATE_CHAT_ROOM_REPLY = "CREATE_CHAT_ROOM_REPLY"
    const val DELETE_CHAT_ROOM = "DELETE_CHAT_ROOM"
    const val DELETE_CHAT_ROOM_REPLY = "DELETE_CHAT_ROOM_REPLY"
    const val GET_ROOM_INFO = "GET_ROOM_INFO"
    const val GET_ROOM_INFO_REPLY = "GET_ROOM_INFO_REPLY"
    const val GET_PARTICIPANT_LIST_REMOVE = "GET_PARTICIPANT_LIST_REMOVE"
    const val GET_PARTICIPANT_LIST_REMOVE_REPLY = "GET_PARTICIPANT_LIST_REMOVE_REPLY"
    const val GET_FRIEND_LIST_FOR_PARTICIPANT_ADD = "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD"
    const val GET_FRIEND_LIST_FOR_PARTICIPANT_ADD_REPLY = "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD_REPLY"
    const val ADD_PEOPLE_TO_GROUP = "ADD_PEOPLE_TO_GROUP"
    const val ADD_PEOPLE_TO_GROUP_REPLY = "ADD_PEOPLE_TO_GROUP_REPLY"
    const val REMOVE_PEOPLE_FROM_GROUP = "REMOVE_PEOPLE_FROM_GROUP"
    const val REMOVE_PEOPLE_FROM_GROUP_REPLY = "REMOVE_PEOPLE_FROM_GROUP_REPLY"

    //Friend Management module
    const val COUNT_FRIEND_REQUEST = "COUNT_FRIEND_REQUEST"
    const val COUNT_FRIEND_REQUEST_REPLY = "COUNT_FRIEND_REQUEST_REPLY"
    const val GET_FRIEND_LIST = "GET_FRIEND_LIST"
    const val GET_FRIEND_LIST_REPLY = "GET_FRIEND_LIST_REPLY"
    const val GET_FRIEND_REQUEST = "GET_FRIEND_REQUEST"
    const val GET_FRIEND_REQUEST_REPLY = "GET_FRIEND_REQUEST_REPLY"
    const val REQ_ADD_FRIEND = "REQ_ADD_FRIEND"
    const val REQ_ADD_FRIEND_REPLY = "REQ_ADD_FRIEND_REPLY"
    const val ADD_FRIEND = "ADD_FRIEND"
    const val ADD_FRIEND_REPLY = "ADD_FRIEND_REPLY"
    const val DELETE_FRIEND = "DELETE_FRIEND"
    const val DELETE_FRIEND_REPLY = "DELETE_FRIEND_REPLY"
    const val SEARCH_USER = "SEARCH_USER"
    const val SEARCH_USER_REPLY = "SERACH_USER_REPLY"

    //Find friend module
    const val FIND_BY_ADDRESS = "FIND_BY_ADDRESS"
    const val FIND_BY_ADDRESS_REPLY = "FIND_BY_ADDRESS_REPLY"
    const val FIND_BY_PROGRAMME = "FIND_BY_PROGRAMME"
    const val FIND_BY_PROGRAMME_REPLY = "FIND_BY_PROGRAMME_REPLY"
    const val FIND_BY_TUTORIAL_GROUP = "FIND_BY_TUTORIAL_GROUP"
    const val FIND_BY_TUTORIAL_GROUP_REPLY = "FIND_BY_TUTORIAL_GROUP_REPLY"
    const val FIND_BY_AGE = "FIND_BY_AGE"
    const val FIND_BY_AGE_REPLY = "FIND_BY_AGE_REPLY"
    const val FIND_BY_LOCATION = "FIND_BY_LOCATION"
    const val FIND_BY_LOCATION_REPLY = "FIND_BY_LOCATION_REPLY"
    const val UPDATE_LOCATION = "UPDATE_LOCATION"
    const val ADVANCED_SEARCH = "ADVANCED_SEARCH"
    const val ADVANCED_SEARCH_REPLY = "ADVANCED_SEARCH_REPLY"

    //security, encryption module
    const val UPDATE_PUBLIC_KEY = "UPDATE_PUBLIC_KEY"
    const val GET_PUBLIC_KEY = "GET_PUBLIC_KEY"
    const val GET_PUBLIC_KEY_ROOM = "GET_PUBLIC_KEY_ROOM"
    const val GET_PUBLIC_KEY_REPLY = "GET_PUBLIC_KEY_REPLY"
    const val SET_CHATROOM_SECRET = "SET_CHATROOM_SECRET"
    const val SEND_CHATROOM_SECRET = "SEND_CHATROOM_SECRET"
    const val GET_CHATROOM_SECRET = "GET_CHATROOM_SECRET"
    const val GET_CHATROOM_SECRET_ALL = "GET_CHATROOM_SECRET_ALL"
    const val GET_CHATROOM_SECRET_ALL_REPLY = "GET_CHATROOM_SECRET_ALL_REPLY"
    const val GET_CHATROOM_SECRET_REPLY = "GET_CHATROOM_SECRET_REPLY"
    const val GET_FORBIDDEN_SECRETS = "GET_FORBIDDEN_SECRETS"
    const val GET_FORBIDDEN_SECRETS_REPLY = "GET_FORBIDDEN_SECRETS_REPLY"

    //profile module
    const val GET_USER_PROFILE = "GET_USER_PROFILE"
    const val GET_USER_PROFILE_REPLY = "GET_USER_PROFILE_REPLY"
    const val SET_USER_PROFILE = "SET_USER_PROFILE"
}
