package mx.com.vialogika.mist;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class Plantillas extends SugarRecord<Plantillas> {
    @Ignore
    private Long plcount;
    @Ignore
    private String guardName;
    @Ignore
    private String apName;
    @Ignore
    private String photoProfile;

    private Long plantillaid;
    private String provider_id;
    private String site_id;
    private String place_id;
    private String guard_id;
    private String incidence_id;
    private String covered_guard_id;
    private String guard_job;
    private String date; // String date format YYYY-MM-DD HH:MM:SS
    private String turno; //Grupo 1, Grupo 2, Grupo 3 etc
    private String saved;
    private String tiempo;


    public Plantillas(){

    }

    public Plantillas(Long plId,String provId,String siteid,String placeid,String guardid,String incid,String cgid,String guardjob,String date,String turno,String tiempoType){
        this.plantillaid = plId;
        this.provider_id = provId;
        this.site_id = siteid;
        this.place_id = placeid;
        this.guard_id = guardid;
        this.incidence_id = incid;
        this.covered_guard_id = cgid;
        this.guard_job = guardjob;
        this.date = date;
        this.turno = turno;
        this.tiempo = tiempoType;
    }

    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getGuard_id() {
        return guard_id;
    }

    public void setGuard_id(String guard_id) {
        this.guard_id = guard_id;
    }

    public String getIncidence_id() {
        return incidence_id;
    }

    public void setIncidence_id(String incidence_id) {
        this.incidence_id = incidence_id;
    }

    public String getCovered_guard_id() {
        return covered_guard_id;
    }

    public void setCovered_guard_id(String covered_guard_id) {
        this.covered_guard_id = covered_guard_id;
    }

    public String getGuard_job() {
        return guard_job;
    }

    public void setGuard_job(String guard_job) {
        this.guard_job = guard_job;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public Long getPlantillaid() {
        return plantillaid;
    }

    public void setPlantillaid(Long plantillaid) {
        this.plantillaid = plantillaid;
    }

    public Long getPlcount() {
        return plcount;
    }

    public void setPlcount(Long plcount) {
        this.plcount = plcount;
    }

    public String getGuardName() {
        List<Elementos> el = Elementos.find(Elementos.class,"GUARD_HASH = ?",guard_id);
        guardName = el.get(0).getGuardFullName();
        return guardName;
    }

    public String getApName() {
        List<Apostamientos> ap = Apostamientos.find(Apostamientos.class,"PLACEID = ?",place_id);
        this.apName = ap.get(0).getApostamiento_name();
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }

    public String getPhotoProfile() {
        List<Elementos> el = Elementos.find(Elementos.class,"GUARD_HASH = ?",this.guard_id);
        this.photoProfile = el.get(0).getPerson_photo_path();
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }
}
