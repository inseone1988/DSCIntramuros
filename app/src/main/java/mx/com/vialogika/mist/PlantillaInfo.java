package mx.com.vialogika.mist;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlantillaInfo {

    private int plantillaId;
   // private int noGuardias;
    private String siteName;
    private String providerName;
    private String noPlaces;
    private String placesCount;

    //Default public constructor
    public PlantillaInfo(Context context){

    }

    public static Long getLastPlantillaId(){
        String query = "SELECT * FROM PLANTILLAS ORDER BY PLANTILLAID DESC LIMIT 1";
        List<Plantillas> plList = Plantillas.findWithQuery(Plantillas.class,query);
        return plList.get(0).getPlantillaid();
    }

    public static String getSiteName(Context context){
        return Databases.siteName(context);
    }

    public static String getProviderName(Context context){
       return Databases.providername(context);
    }

    public static Long plantillaTotalElements(){
        return Apostamientos.count(Apostamientos.class,null,null);
    }



    public static List<Plantillas> getPlantillacount(){
        SimpleDateFormat sFrom = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String from = sFrom.format(new Date());
        String to = Databases.sNow();
        String[] wArgs = new String[]{from,to};
        String wClause = "DATE between ? and ?";
        return  Plantillas.find(Plantillas.class,wClause,wArgs);

    }

    public static Long plantillacount(String[] noPlantilla){
        return Plantillas.count(Plantillas.class,"PLANTILLAID = ?",noPlantilla);
    }

}
