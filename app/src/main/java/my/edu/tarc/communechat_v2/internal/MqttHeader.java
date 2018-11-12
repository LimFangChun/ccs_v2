package my.edu.tarc.communechat_v2.internal;

public final class MqttHeader {
    //general
    public static final String SUCCESS = "SUCCESS";
    public static final String NO_RESULT = "NO_RESULT";
    public static final String DUPLICATED = "DUPLICATED";
    public static final String LOGIN = "LOGIN";
    public static final String LOGIN_REPLY = "LOGIN_REPLY";
    public static final String REGISTER_USER = "REGISTER_USER";
    public static final String REGISTER_USER_REPLY = "REGISTER_USER_REPLY";
    public static final String UPDATE_USER_STATUS = "UPDATE_USER_STATUS";
    public static final String UPDATE_STUDENT = "UPDATE_STUDENT";
    public static final String UPDATE_USER = "UPDATE_USER";
    public static final String NO_REPLY = "NO_REPLY";

    //Chatting module
    public static final String GET_CHAT_ROOM = "GET_CHAT_ROOM";
    public static final String GET_CHAT_ROOM_REPLY = "GET_CHAT_ROOM_REPLY";
    public static final String GET_ROOM_MESSAGE = "GET_ROOM_MESSAGE";
    public static final String GET_ROOM_MESSAGE_REPLY = "GET_ROOM_MESSAGE_REPLY";
    public static final String SEND_ROOM_MESSAGE = "SEND_ROOM_MESSAGE";
    public static final String ROOM_MESSAGE_ARRIVE = "ROOM_MESSAGE_ARRIVE";
    public static final String CREATE_CHAT_ROOM = "CREATE_CHAT_ROOM";
    public static final String CREATE_CHAT_ROOM_REPLY = "CREATE_CHAT_ROOM_REPLY";
    public static final String DELETE_CHAT_ROOM = "DELETE_CHAT_ROOM";
    public static final String DELETE_CHAT_ROOM_REPLY = "DELETE_CHAT_ROOM_REPLY";
    public static final String GET_ROOM_INFO = "GET_ROOM_INFO";
    public static final String GET_ROOM_INFO_REPLY = "GET_ROOM_INFO_REPLY";
    public static final String GET_PARTICIPANT_LIST_REMOVE = "GET_PARTICIPANT_LIST_REMOVE";
    public static final String GET_PARTICIPANT_LIST_REMOVE_REPLY = "GET_PARTICIPANT_LIST_REMOVE_REPLY";
    public static final String GET_FRIEND_LIST_FOR_PARTICIPANT_ADD = "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD";
    public static final String GET_FRIEND_LIST_FOR_PARTICIPANT_ADD_REPLY = "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD_REPLY";
    public static final String ADD_PEOPLE_TO_GROUP = "ADD_PEOPLE_TO_GROUP";
    public static final String ADD_PEOPLE_TO_GROUP_REPLY = "ADD_PEOPLE_TO_GROUP_REPLY";
    public static final String REMOVE_PEOPLE_FROM_GROUP = "REMOVE_PEOPLE_FROM_GROUP";
    public static final String REMOVE_PEOPLE_FROM_GROUP_REPLY = "REMOVE_PEOPLE_FROM_GROUP_REPLY";

    //Friend Management module
    public static final String COUNT_FRIEND_REQUEST = "COUNT_FRIEND_REQUEST";
    public static final String COUNT_FRIEND_REQUEST_REPLY = "COUNT_FRIEND_REQUEST_REPLY";
    public static final String GET_FRIEND_LIST = "GET_FRIEND_LIST";
    public static final String GET_FRIEND_LIST_REPLY = "GET_FRIEND_LIST_REPLY";
    public static final String GET_FRIEND_REQUEST = "GET_FRIEND_REQUEST";
    public static final String GET_FRIEND_REQUEST_REPLY = "GET_FRIEND_REQUEST_REPLY";
    public static final String REQ_ADD_FRIEND = "REQ_ADD_FRIEND";
    public static final String REQ_ADD_FRIEND_REPLY = "REQ_ADD_FRIEND_REPLY";
    public static final String ADD_FRIEND = "ADD_FRIEND";
    public static final String ADD_FRIEND_REPLY = "ADD_FRIEND_REPLY";
    public static final String DELETE_FRIEND = "DELETE_FRIEND";
    public static final String DELETE_FRIEND_REPLY = "DELETE_FRIEND_REPLY";
    public static final String SEARCH_USER = "SEARCH_USER";
    public static final String SEARCH_USER_REPLY = "SERACH_USER_REPLY";

    //Find friend module
    public static final String FIND_BY_ADDRESS = "FIND_BY_ADDRESS";
    public static final String FIND_BY_ADDRESS_REPLY = "FIND_BY_ADDRESS_REPLY";
    public static final String FIND_BY_PROGRAMME = "FIND_BY_PROGRAMME";
    public static final String FIND_BY_PROGRAMME_REPLY = "FIND_BY_PROGRAMME_REPLY";
    public static final String FIND_BY_TUTORIAL_GROUP = "FIND_BY_TUTORIAL_GROUP";
    public static final String FIND_BY_TUTORIAL_GROUP_REPLY = "FIND_BY_TUTORIAL_GROUP_REPLY";
    public static final String FIND_BY_AGE = "FIND_BY_AGE";
    public static final String FIND_BY_AGE_REPLY = "FIND_BY_AGE_REPLY";
    public static final String FIND_BY_LOCATION = "FIND_BY_LOCATION";
    public static final String FIND_BY_LOCATION_REPLY = "FIND_BY_LOCATION_REPLY";
    public static final String UPDATE_LOCATION = "UPDATE_LOCATION";

    //security, encryption module
    public static final String UPDATE_PUBLIC_KEY = "UPDATE_PUBLIC_KEY";
    public static final String UPDATE_PUBLIC_KEY_REPLY = "UPDATE_PUBLIC_KEY_REPLY";
    public static final String GET_PUBLIC_KEY = "GET_PUBLIC_KEY";
    public static final String GET_PUBLIC_KEY_ROOM = "GET_PUBLIC_KEY_ROOM";
    public static final String GET_PUBLIC_KEY_REPLY = "GET_PUBLIC_KEY_REPLY";
    public static final String SET_CHATROOM_SECRET = "SET_CHATROOM_SECRET";
    public static final String SEND_CHATROOM_SECRET = "SEND_CHATROOM_SECRET";
    public static final String GET_CHATROOM_SECRET = "GET_CHATROOM_SECRET";
    public static final String GET_CHATROOM_SECRET_ALL = "GET_CHATROOM_SECRET_ALL";
    public static final String GET_CHATROOM_SECRET_REPLY = "GET_CHATROOM_SECRET_REPLY";

    //profile module
    public static final String GET_USER_PROFILE = "GET_USER_PROFILE";
    public static final String GET_USER_PROFILE_REPLY = "GET_USER_PROFILE_REPLY";
    //TODO remove this
//    public static final String CHAT_ROOM = "CHAT_ROOM";
//    public static final String RECEIVE_MESSAGE = "RECEIVE_MESSAGE";
//    public static final String SEND_MESSAGE = "SEND_MESSAGE";
//    public static final String ADD_GROUP_CHAT_ROOM = "ADD_GROUP_CHAT_ROOM";
}
