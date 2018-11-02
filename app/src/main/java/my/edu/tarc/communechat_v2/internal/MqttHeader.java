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

    //TODO remove this
//    public static final String CHAT_ROOM = "CHAT_ROOM";
//    public static final String RECEIVE_MESSAGE = "RECEIVE_MESSAGE";
//    public static final String SEND_MESSAGE = "SEND_MESSAGE";
//    public static final String ADD_GROUP_CHAT_ROOM = "ADD_GROUP_CHAT_ROOM";
}
