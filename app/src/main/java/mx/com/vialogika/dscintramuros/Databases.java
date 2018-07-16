package mx.com.vialogika.dscintramuros;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SharedMemory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Databases {
    private RequestQueue rq;
    final String urlCall = "https://www.vialogika.com.mx/dscic/requesthandler.php?function=";
    private int OK = 200;
    private int NOTFOUND = 404;
    private JsonRequest  jsonR;
    private JSONObject vetadoData;
    public Databases(){
    }

    public Boolean getDataFromServer(final Context context, String functionName, @Nullable JSONObject jsonrequestparams, @Nullable String GETParams, @Nullable final callbacks cb){
        final Boolean result = false;
        String Url = urlCall + functionName + GETParams;
        rq = Volley.newRequestQueue(context);
        jsonR = new JsonObjectRequest(Request.Method.GET, Url, jsonrequestparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject Response) {
                assert cb != null;
                try {
                    if(SyncDatabaseFromServer(Response,"Elementos")){
                        if(SyncDatabaseFromServer(Response,"Clientes")){
                            if(SyncDatabaseFromServer(Response,"Apostamientos")){
                                cb.onDbUpdateSuccess();
                                //Check preference if autodownload is enabled
                                //TODO:Work in this feature for downloading pictures
                                /**Boolean autoDown = autoDownloadPictures(context);
                                if(autoDown){
                                    //Autodownload no prompt
                                }else{
                                    //Check if user would like us to prompt
                                    Boolean prompt = promptDownloadImg(context);
                                    if(prompt){
                                        //Prompt
                                        promptDownImg(context, new syncProfileImages() {
                                            @Override
                                            public void positiveAnswer() {
                                                cb.onDbUpdateSuccess();
                                            }
                                            @Override
                                            public void negativeAnswer() {
                                                cb.onDbUpdateSuccess();
                                            }
                                        });
                                    }else{
                                        //user dislikes dialogs (Me too)
                                    }
                                }*/
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cb.onResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    new MaterialDialog.Builder(context)
                            .title("Network error")
                            .content("Se ha agotado el tiempo de espera de conexion con el servidor,favor de verificar la conexion a internet.")
                            .positiveText("OK")
                            .show();
                }
                cb.onResponseError(error);
            }
        });
        rq.add(jsonR);
        return result;
    }

    private static void sendPlantilla(JSONObject dataToSave, final Context context, final generic cllBck){
        String url = "https://www.vialogika.com.mx/dscic/raw.php";
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonRequest request = new JsonObjectRequest(JsonRequest.Method.POST, url,dataToSave, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getBoolean("success")){
                        Toast toast = Toast.makeText(context,"Plantilla reportada correctamente",Toast.LENGTH_SHORT);
                        toast.show();
                        cllBck.callback();
                    }else{

                    }
                }catch(JSONException error){
                    Toast toast = Toast.makeText(context, error.toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    new MaterialDialog.Builder(context)
                            .title("Network error")
                            .content("Se ha agotado el tiempo de espera de conexion con el servidor,favor de verificar la conexion a internet.")
                            .positiveText("OK")
                            .show();
                }
            }
        });
        rq.add(request);
    }

    public static boolean plantillaIsSaved(String grupo){
        Boolean isSaved = false;
        String from = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date());
        String to = sNow();
        String[] args = new String[]{grupo,from,to};
        List<Plantillas> pl = Plantillas.find(Plantillas.class,"TURNO = ? AND DATE BETWEEN ? AND ? ",args);
        if(pl.get(0).getSaved().equals("saved")){
            isSaved = true;
        }
        return isSaved;
    }

    public static void deleteElement(long id){
        Elementos el = Elementos.findById(Elementos.class,id);
        el.delete();
    }

    public static void sendIncidence(final Context context,long incid, final callbacks callback){
        String url = "https://www.vialogika.com.mx/dscic/raw.php";
        RequestQueue rq = Volley.newRequestQueue(context);
        JSONObject object = new JSONObject();
        try{
            SiteIncidences incidence = SiteIncidences.findById(SiteIncidences.class,incid);
            Gson gson = new Gson();
            String payload = gson.toJson(incidence);
            object.put("function","saveSiteIncidence");
            object.put("data",payload);
            JsonRequest request = new JsonObjectRequest(JsonRequest.Method.POST, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof TimeoutError || error instanceof NoConnectionError){
                        new MaterialDialog.Builder(context)
                                .title("Network error")
                                .content("Se ha agotado el tiempo de espera de conexion con el servidor,favor de verificar la conexion a internet.")
                                .positiveText("OK")
                                .show();
                    }
                }
            });
            rq.add(request);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private static void saveApsplaces(JSONObject dataToSave, final Context context, final callbacks callback){
        String url = "https://www.vialogika.com.mx/dscic/raw.php";
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonRequest request = new JsonObjectRequest(JsonRequest.Method.POST, url, dataToSave, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getBoolean("success")){
                        callback.onResponse(response);
                    }
                }catch(JSONException error){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    new MaterialDialog.Builder(context)
                            .title("Network error")
                            .content("Se ha agotado el tiempo de espera de conexion con el servidor,favor de verificar la conexion a internet.")
                            .positiveText("OK")
                            .show();
                }
            }
        });
        rq.add(request);
    }

    public static void updatePlId(long plId,long Sid){
        Apostamientos ap = Apostamientos.findById(Apostamientos.class,plId);
        ap.setPlace_id(Sid);
        ap.save();
    }

    public static void savePlantillasPlaces(final long plId, final Context context){
        JSONObject obj = new JSONObject();
        Gson gson = new Gson();
        Apostamientos ap = Apostamientos.findById(Apostamientos.class,plId);
        String data = gson.toJson(ap);
        try{
            obj.put("function","saveApts");
            obj.put("data",data);
        }catch(JSONException error){
            error.printStackTrace();
        }
        saveApsplaces(obj, context, new callbacks() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    updatePlId(plId,response.getLong("id"));
                    Toast toast = Toast.makeText(context,"Guardado correctamente",Toast.LENGTH_SHORT);
                    toast.show();
                }catch(JSONException err){
                    Toast toast = Toast.makeText(context,err.toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onResponseError(VolleyError error) {

            }

            @Override
            public void onDbUpdateSuccess() {

            }
        });

    }

    private static String[] savedIds(List<Plantillas> pl){
        String[] ids = new String[pl.size()];
        for(int i = 0;i < pl.size();i++){
            String id = String.valueOf(pl.get(i).getId());
            ids[i] = id;
        }
        return ids;
    }

    public static void vetadoSearch(String searchString,String searchType,Context context,final callbacks cb){
        try{
            JSONObject wrapper = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("string",searchString);
            data.put("searchtype",searchType);
            wrapper.put("function","searchRestricted");
            wrapper.put("data",data);
            searchVetadoResponse(wrapper, context, new callbacks() {
                @Override
                public void onResponse(JSONObject response) {
                        cb.onResponse(response);
                }

                @Override
                public void onResponseError(VolleyError error) {
                    cb.onResponseError(error);
                }

                @Override
                public void onDbUpdateSuccess() {

                }
            });
        }catch(JSONException e){
            e.printStackTrace();
        }

    }

    public static void searchVetadoResponse(JSONObject data,Context context,final callbacks cb){
        String url = "https://www.vialogika.com.mx/dscic/raw.php";
        RequestQueue mQueue = Volley.newRequestQueue(context);
        JsonRequest rq = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                cb.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onResponseError(error);
            }
        });
        mQueue.add(rq);
    }

    public static void SavePlantillaToServer(String grupo, Context context, final generic callback) throws JSONException{
        String from = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date());
        String to = sNow();
        List<String> toSave = new ArrayList<String>();
        String[] args = new String[]{grupo,from,to};
        String[] incArgs = new String[]{from,to};
        List<Plantillas> pl = Plantillas.find(Plantillas.class,"TURNO = ? AND DATE BETWEEN ? AND ?",args);
        List<Incidences> inc = Incidences.find(Incidences.class,"DATE_TIME BETWEEN ? AND ?",incArgs);
        final String[] mArgs = savedIds(pl);
        JSONObject obj = new JSONObject();
        Gson gson = new Gson();
        obj.put("function","saveplantilla");
        obj.put("data",gson.toJson(pl));
        obj.put("incidences",gson.toJson(inc));
        sendPlantilla(obj, context, new generic() {
            @Override
            public void callback() {
                setPlantillaSaved(mArgs);
                callback.callback();
            }
        });
    }

    public static void setPlantillaSaved(String[] ids){
        for(int i = 0; i < ids.length;i++){
            Plantillas pl = Plantillas.findById(Plantillas.class,Long.valueOf(ids[i]));
            pl.setSaved("saved");
            pl.save();
        }
    }

    public static void saveGhash(Long eId,String hash) {
        Elementos el = Elementos.findById(Elementos.class,eId);
        el.setGuardHash(hash);
        el.save();
    }

    public static Long GID(String guardFullName){
        Long gid = 0L;
        List<Elementos> elems = Elementos.listAll(Elementos.class);
        for(int i = 0; i < elems.size(); i++){
            if(elems.get(i).getGuardFullName().equals(guardFullName)){
                gid = elems.get(i).getId();
            }
        }
        return gid;
    }

    public static String sNow(){
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        return sDate.format(new Date());
    }

    public static Elementos getElemento(String elementName){
        Elementos match = null;
        List<Elementos> elements = Elementos.listAll(Elementos.class);
        for(int i = 0;i < elements.size();i++){
            if (elements.get(i).getGuardFullName().contentEquals(elementName)){
                match = elements.get(i);
            }
        }
        return match;
    }

    public void SaveToClientes(JSONObject ClientesData)throws JSONException{
        String cid = ClientesData.getString("client_id");
        String cSocial = ClientesData.getString("client_social");
        String cName = ClientesData.getString("client_name");
        String cAlias = ClientesData.getString("client_alias");
        String cSite = ClientesData.getString("client_site_id");
        if(!clientExists(cid)){
            Clientes cl = new Clientes(cid,cSocial,cName,cAlias,cSite);
            cl.save();
        }else{

        }

    }

    public Bitmap profileImage(String profile_image_path){
        Bitmap myBitmap = null;
        if(profile_image_path != null){
            File imgFile = new File(profile_image_path);
            if(imgFile.exists()){
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        }
        return myBitmap;
    }

    public Boolean clientExists(String cId){
        Boolean Result = false;
        List<Clientes> cl = Clientes.find(Clientes.class,"SERVER_ID = ?",cId);
        if(cl.size() > 0){
            Result = true;
        }
        return Result;
    }

    public void SaveToAps(JSONObject ApData) throws JSONException{
        Long apid = ApData.getLong("plantilla_place_id");
        Long ppcid = ApData.getLong("plantilla_place_client_id");
        String ppaname = ApData.getString("plantilla_place_apostamiento_name");
        String ppaa = ApData.getString("plantilla_place_apostamiento_alias");
        String ppt = ApData.getString("plantilla_place_type");
        Long ppsid = ApData.getLong("plantilla_place_site_id");
        if(!APExists(apid)){
            Apostamientos ap = new Apostamientos(apid,ppcid,ppaname,ppaa,ppt,ppsid);
            ap.save();
        }else{
            List<Apostamientos> ap = Apostamientos.find(Apostamientos.class,"PLACEID = ?",apid.toString());
            ap.get(0).setApostamiento_name(ppaname);
            ap.get(0).setPlace_client_id(ppcid);
            ap.get(0).setApostamiento_alias(ppaa);
            ap.get(0).setPlace_type(ppt);
            ap.get(0).setSite_id(ppsid);
            Apostamientos.saveInTx(ap.get(0));
        }
    }


    public Boolean APExists(Long ppid){
        Boolean result = false;
        List<Apostamientos> aps = Apostamientos.find(Apostamientos.class,"PLACEID = ?",ppid.toString());
        if(aps.size() > 0){
            result = true;
        }
        return result;

    }


    public void SaveToElementos(JSONObject ElementData) throws JSONException {
        String gHash = ElementData.getString("guard_hash");
        String pname = ElementData.getString("person_name");
        String fname = ElementData.getString("person_fname");
        String lname = ElementData.getString("person_lname");
        String range = ElementData.getString("guard_range");
        if(!elementExists(gHash)){
            Elementos el = new Elementos(gHash,pname,fname,lname,range,null);
            el.save();
        }else{
            List<Elementos> el = Elementos.find(Elementos.class,"GUARD_HASH = ?",gHash);
            el.get(0).setPerson_name(pname);
            el.get(0).setPerson_fname(fname);
            el.get(0).setPerson_lname(lname);
            el.get(0).setGuard_range(range);
            Elementos.saveInTx(el.get(0));
        }

    }

    public Boolean elementExists(String GuardHash){
        Boolean result = false;
        List<Elementos> mEl =  Elementos.find(Elementos.class,"GUARD_HASH = ?",GuardHash);
        if(mEl.size() > 0){
            result = true;
        }
        return result;
    }

    public Boolean SyncDatabaseFromServer(JSONObject Data,String Table) throws JSONException{
        Boolean result = false;
        switch (Table){
            case "Elementos":
                JSONArray element = Data.getJSONArray("elementos");
                int size = element.length();
                for(int i = 0;i < size; i++){
                    JSONObject obj = element.getJSONObject(i);
                    SaveToElementos(obj);
                }
                //cb.onResponse(Data);
                result = true;
                break;
            case "Apostamientos":
                JSONArray aps = Data.getJSONArray("apostamientos");
                int apsize = aps.length();
                for(int i = 0; i < apsize; i++){
                    JSONObject obj = aps.getJSONObject(i);
                    SaveToAps(obj);
                }
                result = true;
                break;
            case "Clientes":
                JSONArray Clients = Data.getJSONArray("clientes");
                int csize = Clients.length();
                for(int i = 0;i < csize; i++){
                    JSONObject obj = Clients.getJSONObject(i);
                    SaveToClientes(obj);
                }
                result = true;
                break;
        }
        return result;
    }

    public static Boolean deleteElementFromServer(Context context,String elementid)throws JSONException{
        JSONObject req = new JSONObject();
        req.put("function","deleteElement");
        req.put("guard_hash",elementid);
        Boolean result = false;
        return result;
    }

    private void promptDownImg(final Context context,final syncProfileImages callback){
        new MaterialDialog.Builder(context)
                .title("Sincronizar Imagenes")
                .content("Deseas sincronizar las imagenes de perfil?")
                .positiveText("OK")
                .negativeText("No,quiza mas tarde")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        changePreference(dialog.isPromptCheckBoxChecked(),dialog.isPromptCheckBoxChecked(),context);
                        callback.positiveAnswer();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        callback.negativeAnswer();
                    }
                })
                .checkBoxPrompt("Recordar eleccion",false,null)
                .show();
    }
    //TODO: Put in activity settings an option to changes this later
    private void changePreference(Boolean rememberOption,Boolean promptNextTime,Context context){
        SharedPreferences sp = context.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("prompt",promptNextTime);
        editor.putBoolean("askForDownloadingProfileImages",rememberOption);
        editor.putBoolean("autoDownloadPictures",false);
        editor.apply();
    }

    public static long getClientId(String clientName){
        List<Clientes> client = Clientes.find(Clientes.class,"NAME = ?",clientName);
        return Long.parseLong(client.get(0).getServerId());
    }

    public static long siteId(Context context){
        SharedPreferences sp = context.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        return sp.getLong("user_site_id",0);
    }

    public static long siteId(String clientName){
        List<Clientes> client = Clientes.find(Clientes.class,"NAME = ?",clientName);
        return Long.parseLong(client.get(0).getSiteId());
    }

    public static long apCons(long clientId){
        String[] params = new String[]{String.valueOf(clientId)};
        long currCount = Apostamientos.count(Apostamientos.class,"PLACECLIENTID = ?",params);
        return currCount + 1;
    }

    public static String siteName(Context context){
        SharedPreferences sp = context.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        return sp.getString("site_name",null);
    }

    public static String providername(Context context){
        SharedPreferences sp = context.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        return sp.getString("user_provider_name",null);
    }

    public static long providerId(Context context){
        SharedPreferences sp = context.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        return sp.getLong("user_provider_id",0L);
    }

    private Boolean downloadProfileImages(Context context){
        SharedPreferences sp = context.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        return  sp.getBoolean("askForDownloadingProfileimages",true);
    }

    private Boolean autoDownloadPictures(Context context){
        SharedPreferences sp = context.getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        return sp.getBoolean("autoDownloadPictures",false);
    }

    private Boolean promptDownloadImg(Context context){
        SharedPreferences sp =context.getSharedPreferences("MainActivity",context.MODE_PRIVATE);
        Boolean prompt = sp.getBoolean("prompt",true);
        return  prompt;
    }

    public static List<String> plantillaNo(){
        String noDatatext = "Grupo 1";
        List<String> mList = new ArrayList<String>();
        String query = "SELECT * FROM PLANTILLAS GROUP BY TURNO";
        List<Plantillas> plantillas = Plantillas.findWithQuery(Plantillas.class,query,new String[]{});
        int pCount = plantillas.size();
        if(pCount > 0 ){
            for(int i = 0; i < pCount; i++){
                String item = plantillas.get(i).getTurno().toString();
                mList.add(item);
            }
        }else{
            mList.add(noDatatext);
        }
        return mList;
    }

    public static List<String> availableElementos(String grupo){
        List<String> al = new ArrayList<String>();
        String from = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date());
        String to = sNow();
        String[] args = new String[]{grupo,from,to};
        List<Elementos> el = Elementos.find(Elementos.class,"GUARD_HASH NOT IN(SELECT GUARDID FROM PLANTILLAS WHERE TURNO = ? AND DATE BETWEEN ? AND ?)",args);
        for(int i = 0; i < el.size(); i++){
            al.add(el.get(i).getGuardFullName());
        }
        return al;
    }

    public static List<Plantillas> asignedElementos(String grupo){
        List<String> al = new ArrayList<String>();
        String from = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date());
        String to = sNow();
        String[] args = new String[]{grupo,from,to};
        return Plantillas.find(Plantillas.class,"TURNO = ? AND DATE BETWEEN ? AND ?",args);

    }

    public static void deleteApFromDb(long id){
        Plantillas pl = Plantillas.findById(Plantillas.class,id);
        pl.delete();
    }

    public static void setPlantillaSaved(long id){
        Plantillas pl = Plantillas.findById(Plantillas.class,id);
        pl.setSaved("saved");
        pl.save();
    }

    public static List<Elementos> unavailableElements(String grupo){
        String from = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date());
        String to = sNow();
        String[] args = new String[]{grupo,from,to};
        return Elementos.find(Elementos.class,"GUARD_HASH IN(SELECT GUARDID FROM PLANTILLAS WHERE TURNO = ? AND DATE BETWEEN ? AND ?)",args);
    }

    public static List<Elementos> listAllElementos(){
        return Elementos.listAll(Elementos.class);
    }

    public static long plGroupCount(String group){
        String[] args = new String[]{group};
        return Plantillas.count(Plantillas.class,"TURNO = ?",args);
    }

    public static long PlantillaNoPlaces(){
        return Apostamientos.count(Apostamientos.class,"id",null);
    }

    public static ArrayList<String> enames(){
        ArrayList<String> elements = new ArrayList<String>();
        List<Elementos> el = Elementos.listAll(Elementos.class);
        if(el.size() >= 1){
            for(int i = 0;el.size() > i;i++){
                elements.add(el.get(i).getGuardFullName());
            }
        }else{
            elements.add("Sin elementos");
        }
        return elements;
    }

    public static ArrayList<String> clientNames(){
        ArrayList<String> clients = new ArrayList<String>();
        List<Clientes> cl = Clientes.listAll(Clientes.class);
        if(cl.size() >= 1){
            for(int i = 0; cl.size() > i; i++){
                clients.add(cl.get(i).getName());
            }
        }else{
            clients.add("No hay clientes");
        }
        return clients;
    }

    public static  ArrayList<String> apNames(){
        ArrayList<String> clientes = new ArrayList<String>();
        List<Apostamientos> cl = Apostamientos.listAll(Apostamientos.class);
        if(cl.size() >= 1){
            for(int i = 0;cl.size() > i;i++){
                clientes.add(cl.get(i).getApostamiento_alias());
            }
        }else{
            clientes.add("Sin elementos");
        }
        return clientes;
    }

    public static List<Plantillas> getTodayGroups(){
        String from = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date());
        String to = sNow();
        String[] args = new String[]{from,to};
        String query = "SELECT * FROM PLANTILLAS WHERE DATE BETWEEN ? AND ? GROUP BY TURNO";
        return Plantillas.findWithQuery(Plantillas.class,query,args);
    }

    private Boolean profilePhotoExists(String Path){
        Boolean exists = false;
        File mFile = new File(Path);
        if(mFile.exists()){
            exists = true;
        }
        return exists;
    }

    private List<Elementos> getRecordsToUpdateImage(){
        List<Elementos> el;
        el = Elementos.find(Elementos.class,"PERSONPHOTOPATH",null);
        return el;
    }

    interface generic{
        void callback();
    }

    interface callbacks{
        void onResponse(JSONObject response);
        void onResponseError(VolleyError error);
        void onDbUpdateSuccess();
    }

    interface syncProfileImages{
        void positiveAnswer();
        void negativeAnswer();
    }

}
