package mx.com.vialogika.dscintramuros;

import com.orm.SugarRecord;

public class Grupos extends SugarRecord {
    private String grupoName;
    private int guardCount;

    public Grupos(){

    }

    public Grupos(String grupoName, int guardCount) {
        this.grupoName = grupoName;
        this.guardCount = guardCount;
    }

    public String getGrupoName() {
        return grupoName;
    }

    public int getGuardCount() {
        return guardCount;
    }

}
