package Server;

public class RichiestaClient {

    private final int command;
    private final String parameter;

    public static final int LOGIN_COMMAND = 1;
    public static final int DISTRIBUZ_COMMAND = 2;
    public static final int QUIT_COMMAND = 3;
    public static final int MOVE_REQUEST_COMMAND = 4;
    public static final int MOVE_PLAY_COMMAND = 5;
    public static final int GET_STATE_COMMAND = 6;
    public static final int GET_WIN_COMMAND = 7;

    public RichiestaClient(String richiesta) throws Exception{
        int command1;

        if (richiesta.length() < 3){
            throw new Exception("Richiesta non valida");
        }

        // i primi due caratteri sono il comando
        String comando = richiesta.substring(0, 2).toUpperCase();

        switch (comando) {
            case "AC" -> command1 = LOGIN_COMMAND;
            case "DS" -> command1 = DISTRIBUZ_COMMAND;
            case "QU" -> command1 = QUIT_COMMAND;
            case "TU" -> command1 = MOVE_REQUEST_COMMAND;
            case "GC" -> command1 = MOVE_PLAY_COMMAND;
            case "ST" -> command1 = GET_STATE_COMMAND;
            case "VP" -> command1 = GET_WIN_COMMAND;
            default -> throw new Exception("Invalid command " + comando);
        }

        // prendi il resto della stringa
        command = command1;
        this.parameter = richiesta.substring(2);
    }

    public int getCommand() {
        return command;
    }

    public String getParameter() {
        return parameter;
    }

}
