package mx.com.vialogika.dscintramuros;

public class Aps {

    private long apid;
    private String person_name;
    private String person_apt;
    private String person_photo;

    public Aps(long apid,String pname,String pApt,String pPhoto){
        this.apid = apid;
        this.person_name = pname;
        this.person_apt = pApt;
        this.person_photo = pPhoto;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_apt() {
        return person_apt;
    }

    public void setPerson_apt(String person_apt) {
        this.person_apt = person_apt;
    }

    public String getPerson_photo() {
        return person_photo;
    }

    public void setPerson_photo(String person_photo) {
        this.person_photo = person_photo;
    }

    public long getApid() {
        return apid;
    }

    public void setApid(long apid) {
        this.apid = apid;
    }
}
