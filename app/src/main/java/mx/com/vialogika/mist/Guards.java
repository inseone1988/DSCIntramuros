package mx.com.vialogika.mist;

public class Guards {

    String guard_full_name, guard_apt;
    int guard_photo;

    public Guards(){

    }

    public Guards(String nombre,String puesto,int foto){
        this.guard_full_name = nombre;
        this.guard_apt = puesto;
        this.guard_photo = foto;
    }

    public String getGuard_full_name(){
        return guard_full_name;
    }

    public void setGuard_full_name(String nombre){
        guard_full_name = nombre;
    }

    public String getGuard_apt(){
        return guard_apt;
    }

    public void setGuard_apt(String guard_apt) {
        this.guard_apt = guard_apt;
    }

    public int getGuard_photo(){
        return guard_photo;
    }

    public void setGuard_photo(int guard_photo) {
        this.guard_photo = guard_photo;
    }
}
