package mx.com.vialogika.dscintramuros;

import org.json.JSONObject;

import java.util.List;

public class PlantillaPlace {
    private long id;
    private long guardId;
    private String date;
    private String icId;
    private String Cgid;
    private String ghash;
    private String gJob;
    private Long siteId;
    private Long plantId;
    private Long apId;
    private String provId;
    private String grupo;
    private String tiempo;

    public PlantillaPlace(String Grupo,String guardFullName,String Apostamiento,String tiempo){
        this.guardId = Databases.GID(guardFullName);
        this.date = Databases.sNow();
        this.grupo = Grupo;
        this.tiempo = tiempo;
        this.apId = Apostamientos.find(Apostamientos.class,"APOSTAMIENTOALIAS = ?",Apostamiento).get(0).getPlace_id();
        setGuardInfo(guardId);
    }

    public long getGuardId() {
        return guardId;
    }

    public void setGuardId(String fullname) {
        this.guardId = Databases.GID(fullname);
    }

    private void setGuardInfo(long gid){
        Elementos el = Elementos.findById(Elementos.class,this.guardId);
        this.ghash = el.getGuardHash();
        this.gJob = el.getGuard_range();
    }

    public String getProvId() {
        return provId;
    }

    public void setProvId(String provId) {
        this.provId = provId;
    }

    public long getPlantId() {
        return plantId;
    }

    public void setPlantId(long plantId) {
        this.plantId = plantId;
    }

    public String getGhash(){
        return this.ghash;
    }

    public long save(){
        Plantillas pl = new Plantillas(this.plantId,this.provId,String.valueOf(this.siteId),String.valueOf(this.apId),this.ghash,this.icId,this.Cgid,this.gJob,this.date,this.grupo,this.tiempo);
        pl.setSaved("not saved");
        pl.save();
        return pl.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIcId(String icId) {
        this.icId = icId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getTiempo() {
        return tiempo;
    }


}
