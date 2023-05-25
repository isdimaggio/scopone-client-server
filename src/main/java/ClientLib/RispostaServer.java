package ClientLib;

public class RispostaServer {

    private final String statusCode;
    private final String parameter;

    RispostaServer(String risposta){
        this.statusCode = risposta.substring(0, 2);
        this.parameter = risposta.substring(2);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getParameter() {
        return parameter;
    }
}
