package mx.com.vialogika.dscintramuros;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Elementos extends SugarRecord<Elementos>{
    //Hashed guard ids mean guards saved to server;
    @Ignore
    private String guardFullName;

    private String guardHash;
    private String person_name;
    private String person_fname;
    private String person_lname;
    private String guard_range;
    private String person_photo_path;


public Elementos(){

    }

public Elementos(String element_name,String element_fname,String element_lname,String element_position,String element_photo_path){
this.person_name = element_name;
this.person_fname = element_fname;
this.person_lname = element_lname;
this.guard_range = element_position;
this.person_photo_path = element_photo_path;
}

public Elementos(String guard_hash,String element_name,String element_fname,String element_lname,String element_position,@Nullable String element_photo_path){
    this.guardHash = guard_hash;
    this.person_name = element_name;
this.person_fname = element_fname;
this.person_lname = element_lname;
this.guard_range = element_position;
this.person_photo_path = element_photo_path;
}


    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_fname() {
        return person_fname;
    }

    public void setPerson_fname(String person_fname) {
        this.person_fname = person_fname;
    }

    public String getPerson_lname() {
        return person_lname;
    }

    public void setPerson_lname(String person_lname) {
        this.person_lname = person_lname;
    }

    public String getGuard_range() {
        return guard_range;
    }

    public void setGuard_range(String guard_range) {
        this.guard_range = guard_range;
    }

    public String getPerson_photo_path() {
        return person_photo_path;
    }

    public void setPerson_photo_path(String person_photo_path) {
        this.person_photo_path = person_photo_path;
    }

    public String getGuardHash() {
        return guardHash;
    }

    public void setGuardHash(String guardHash) {
        this.guardHash = guardHash;
    }

    public String getGuardFullName() {
        String SPACE = " ";
        return this.person_name + SPACE + this.person_fname + SPACE + this.person_lname;

    }

    public void setGuardFullName(String guardFullName) {
        this.guardFullName = guardFullName;
    }
}
