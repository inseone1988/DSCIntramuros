package mx.com.vialogika.dscintramuros;

public class MaterialDialogPayload {

    private String MODE;
    private String Grupo;


    public MaterialDialogPayload(String mode, String grupo) {
        MODE = mode;
        Grupo = grupo;
    }

    public String getMODE() {
        return MODE;
    }

    public void setMODE(String MODE) {
        this.MODE = MODE;
    }

    public String getGrupo() {
        return Grupo;
    }

    public void setGrupo(String grupo) {
        Grupo = grupo;
    }
}
