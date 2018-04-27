package mx.com.vialogika.dscintramuros;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class elements {

    @PrimaryKey(autoGenerate = true)
    private int element_id;

    @ColumnInfo(name = "Created")
    private String created;

    @ColumnInfo(name = "guard_person_id")
    private String pid;

    @ColumnInfo(name = "person_name")
    private String nombre;

    @ColumnInfo(name = "person_fname")
    private String fname;

    @ColumnInfo(name = "person_lname")
    private String lname;

    @ColumnInfo(name = "guard_range")
    private String guard_range;

    @ColumnInfo(name = "person_photo_path")
    private String photo_path;

    public elements(){

    }

    public elements(String PID,String date, String personName, String personFname,String personLname,String elementJob, String elementProfileImage ){
        this.pid = PID;
        this.created = date;
        this.nombre = personName;
        this.fname = personLname;
        this.lname = personFname;
        this.guard_range = elementJob;
        this.photo_path = elementProfileImage;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {

        this.photo_path = photo_path;
    }

    public int getElement_id() {

        return element_id;
    }

    public void setElement_id(int element_id) {
        this.element_id = element_id;
    }

    public String getGuard_range(){
        return guard_range;
    }

    public void setGuard_range(String guard_range) {
        this.guard_range = guard_range;
    }

    public String getPid(){
        return pid;
    }

    public void setPid(String Pid){
        this.pid = Pid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
