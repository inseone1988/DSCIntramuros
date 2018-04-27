package mx.com.vialogika.dscintramuros;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface plantillasDao {

    @Query("SELECT * FROM plantillas")
    List<plantillas> getAll();

    @Insert
    void insert(plantillas plantilla);

}
