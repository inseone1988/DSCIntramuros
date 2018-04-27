package mx.com.vialogika.dscintramuros;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;


@Database(entities = {elements.class,plantillas.class},version = 2)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract elementsDao eDao();

    public abstract plantillasDao pDao();

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,AppDatabase.class,"intramuros")
                    .fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    };

    public static void destroyInstance(){
        INSTANCE = null;
    }

}
