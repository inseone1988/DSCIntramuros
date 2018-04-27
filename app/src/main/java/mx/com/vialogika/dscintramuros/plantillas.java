package mx.com.vialogika.dscintramuros;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class plantillas {

    @PrimaryKey(autoGenerate = true)
    private int plantilla_id;

    @ColumnInfo(name = "provider_id")
    private int noProveedor;

    @ColumnInfo(name = "place_id")
    private int noApostamiento;

    @ColumnInfo(name = "guard-id")
    private int idGuardia;

    @ColumnInfo(name = "incidence_id")
    private int noIncidencia;

    @ColumnInfo(name = "covered_guard_id")
    private int idGuardiaCubre;

    @ColumnInfo(name = "guard_job")
    private String tipoElemento;

    @ColumnInfo(name = "date")
    private String fecha;

    @ColumnInfo(name = "turno")
    private String turno;

    public plantillas(){}


    public int getPlantilla_id() {
        return plantilla_id;
    }

    public void setPlantilla_id(int plantilla_id) {
        this.plantilla_id = plantilla_id;
    }

    public int getNoProveedor() {
        return noProveedor;
    }

    public void setNoProveedor(int noProveedor) {
        this.noProveedor = noProveedor;
    }

    public int getNoApostamiento() {
        return noApostamiento;
    }

    public void setNoApostamiento(int noApostamiento) {
        this.noApostamiento = noApostamiento;
    }

    public int getIdGuardia() {
        return idGuardia;
    }

    public void setIdGuardia(int idGuardia) {
        this.idGuardia = idGuardia;
    }

    public int getNoIncidencia() {
        return noIncidencia;
    }

    public void setNoIncidencia(int noIncidencia) {
        this.noIncidencia = noIncidencia;
    }

    public int getIdGuardiaCubre() {
        return idGuardiaCubre;
    }

    public void setIdGuardiaCubre(int idGuardiaCubre) {
        this.idGuardiaCubre = idGuardiaCubre;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
