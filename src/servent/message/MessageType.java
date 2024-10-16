package servent.message;

public enum MessageType {
    NEW_NODE, WELCOME, SORRY, UPDATE, POISON,
    ADD_FILE, ASK_GET, TELL_GET, TELL_PORT_FILES, REMOVE_FILE,
    FILE_REMOVED,
    PING, PONG, IS_ALIVE,
    NO_PERMISSION, ADD_FRIEND,
    REMOVE_NODE, FAILED_NODE, FAILED_NODE_CONFIRM,
    SUZUKI_REQUEST, SUZUKI_GIVE_TOKEN,
    DONE_REORGANIZING,
    SEND_BACKUP, GET_PORT_FILES
}
