package mx.com.vialogika.dscintramuros;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Apostamientos extends SugarRecord {
@Ignore
private int no_apostamientos;

private Long place_id;
private Long place_client_id;
private String apostamiento_name;
private String apostamiento_alias;
private String place_type;
private Long site_id;

public Apostamientos(){

}

public Apostamientos(Long apId,Long cuentaId,String nombreApostamiento,String apostamientoAlias,String placeType,Long siteId){
    this.place_id = apId;
    this.place_client_id = cuentaId;
    this.apostamiento_name = nombreApostamiento;
    this.apostamiento_alias = apostamientoAlias;
    this.place_type = placeType;
    this.site_id = siteId;
}

    public Long getPlace_id() {
        return place_id;
    }

    public void setPlace_id(Long place_id) {
        this.place_id = place_id;
    }

    public Long getPlace_client_id() {
        return place_client_id;
    }

    public void setPlace_client_id(Long place_client_id) {
        this.place_client_id = place_client_id;
    }

    public String getApostamiento_name() {
        return apostamiento_name;
    }

    public void setApostamiento_name(String apostamiento_name) {
        this.apostamiento_name = apostamiento_name;
    }

    public String getApostamiento_alias() {
        return apostamiento_alias;
    }

    public void setApostamiento_alias(String apostamiento_alias) {
        this.apostamiento_alias = apostamiento_alias;
    }

    public String getPlace_type() {
        return place_type;
    }

    public void setPlace_type(String place_type) {
        this.place_type = place_type;
    }

    public Long getSite_id() {
        return site_id;
    }

    public void setSite_id(Long site_id) {
        this.site_id = site_id;
    }

    public int getNo_apostamientos() {
        return no_apostamientos;
    }

    public void setNo_apostamientos(int no_apostamientos) {
        this.no_apostamientos = no_apostamientos;
    }
}
