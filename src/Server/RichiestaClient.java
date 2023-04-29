package Server;

public class RichiestaClient {

    private final int command;
    private final String parameter;

    public static final int LOGIN_COMMAND = 1;
    public static final int DISTRIBUZ_COMMAND = 2;
    public static final int QUIT_COMMAND = 3;

    public RichiestaClient(String richiesta) throws Exception{
        int command1 = 0;

        if (richiesta.length() < 3){
            throw new Exception("Richiesta non valida");
        }

        // i primi due caratteri sono il comando
        String comando = richiesta.substring(0, 2).toUpperCase();

        switch (comando) {
            case "AC" -> command1 = LOGIN_COMMAND;
            case "DS" -> command1 = DISTRIBUZ_COMMAND;
            case "QU" -> command1 = QUIT_COMMAND;
            default -> throw new Exception("Invalid command " + comando);
        }

        // prendi il resto della stringa
        command = command1;
        this.parameter = richiesta.substring(3);
    }

    public int getCommand() {
        return command;
    }

    public String getParameter() {
        return parameter;
    }

}
