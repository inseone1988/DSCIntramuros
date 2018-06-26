package mx.com.vialogika.dscintramuros;

import android.support.annotation.Nullable;

import com.orm.SugarRecord;

import java.util.UUID;


public class Incidences extends SugarRecord {
    private String MUID = UUID.randomUUID().toString();
    private String dateTime;
    private String incidenceName;
    private String incidenceType;
    private String incidenceObs;


    public Incidences(){

    }

    public Incidences(String dateTime, String incidenceName, String incidenceType,@Nullable String incidenceObs) {
        this.dateTime = dateTime;
        this.incidenceName = incidenceName;
        this.incidenceType = incidenceType;
        this.incidenceObs = incidenceObs;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getIncidenceName() {
        return incidenceName;
    }

    public void setIncidenceName(String incidenceName) {
        this.incidenceName = incidenceName;
    }

    public String getIncidenceType() {
        return incidenceType;
    }

    public void setIncidenceType(String incidenceType) {
        this.incidenceType = incidenceType;
    }

    public String getIncidenceObs() {
        return incidenceObs;
    }

    public void setIncidenceObs(String incidenceObs) {
        this.incidenceObs = incidenceObs;
    }

    public String getMUID() {
        return MUID;
    }
}
