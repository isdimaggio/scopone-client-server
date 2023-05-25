package ClientLib;

public class ScoponeException extends Exception{

    // CODICI DI ERRORE
    public static final int ERR_BAD_RESPONSE = -2;
    public static final int ERR_INSTANCE_INVALID = -1;
    public static final int ERR_SOCKET = 0;
    public static final int ERR_USER_ALREADY_EXISTS = 1;
    public static final int ERR_SERVER_FULL = 2;
    public static final int ERR_SERVER_GENERIC = 3;
    public static final int ERR_LOBBY_NOT_FULL = 4;
    public static final int ERR_GAME_FINISHED = 5;


    private final int errorCode;

    public ScoponeException(String errorMessage, int errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
