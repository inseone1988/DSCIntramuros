 package mx.com.vialogika.dscintramuros;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

 public class EditPlantilla extends AppCompatActivity {
    private String mGroup;
    private RecyclerView mRecyclerview;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> notAvailableguards;
    private List<Aps> mAps;
    private String groupToEdit = "Grupo 1";
    private ArrayAdapter<String> pl;
    private ArrayAdapter<String> mAdapterElementos;
    private ArrayAdapter<String> mAdapterAps;
    private ArrayAdapter<String> mAdapterClients;
    private final int plantillaTotal = (int) Databases.PlantillaNoPlaces();
    private String MODE = "new";
    private int mFaltan;
    private boolean plComplete = false;
    private List<String> plList = new ArrayList<String>();
    private List<String> gNames;
    private List<String> apnames = Databases.apNames();
    private List<String> clnames = Databases.clientNames();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plantilla);
        final View rootview = findViewById(android.R.id.content);
        setFab(rootview);
        getintentExtras(getIntent());
        ActionBar bar = getSupportActionBar();
        bar.setTitle(groupToEdit);
        getAsignedAps(groupToEdit, new db() {
            @Override
            public void onDataRetrieved() {
                mAdapter = new ElementoAdapter(mAps);
                mRecyclerview = rootview.findViewById(R.id.edit_plantilla);
                mLayoutManager = new LinearLayoutManager(getApplication());
                mRecyclerview.setLayoutManager(mLayoutManager);
                mRecyclerview.setAdapter(mAdapter);
            }
        });
    }

     private void getintentExtras(Intent intent) {
        if(intent.hasExtra("MODE")){
            MODE = intent.getStringExtra("MODE");
            groupToEdit = intent.getStringExtra("EditPlantilla");
            if(intent.hasExtra("PlantillaFaltan")){
                mFaltan = plantillaTotal - intent.getIntExtra("PlantillaFaltan",0);
                if(mFaltan <= 0){
                    plComplete = true;
                }
            }
        }
         getGuardNames();
         testMessage();
     }

     private void getAsignedAps(String grupo,db callback){
        List<Plantillas> el = Databases.asignedElementos(grupo);
        mAps = new ArrayList<Aps>();
        for(int i = 0; i < el.size();i++){
            long apid = el.get(0).getId();
            String photo = el.get(i).getPhotoProfile();
            String elName = el.get(i).getGuardName();
            String apName = el.get(i).getApName();
            Aps ap = new Aps(apid,elName,apName,photo);
            mAps.add(ap);
        }
        callback.onDataRetrieved();
     }

     private void testMessage(){
         new MaterialDialog.Builder(this)
                 .title("Editando " + groupToEdit)
                 .content("entrado en modo " + this.MODE)
                 .show();
     }

     private void getGuardNames(){
        if(MODE.equals("new")){
            gNames = Databases.enames();
        }else{
            gNames = Databases.availableElementos(groupToEdit);
        }


     }

     private void setFab(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab_add_ap);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog dialog = new MaterialDialog.Builder(v.getContext())
                            .title(R.string.add_apostamiento_text)
                            .customView(R.layout.add_plantilla_apostamiento,true)
                            .positiveText(R.string.ok)
                            .autoDismiss(false)
                            .negativeText(R.string.save_text)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    saveapt(dialog);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    guardarPlantilla(dialog);
                                }
                            })
                            .show();
                    setSpinnersData(dialog);
                    setupDialogAfterLoad(dialog);
                    setAddPlantillaOnClick(getDialogView(dialog));
                    setCounters(dialog);
                }
            });

    }

    private void guardarPlantilla(MaterialDialog dialog){
        View v = dialog.getCustomView();
        Spinner sp = v.findViewById(R.id.plantilla_no);
        String grupo = sp.getSelectedItem().toString();
        dialog.hide();
        confirmGuardar(grupo);
    }

    private void setupDialogAfterLoad(MaterialDialog dialog){
        Spinner spGuards = dialog.getCustomView().findViewById(R.id.guardia_name);
        Spinner spApName = dialog.getCustomView().findViewById(R.id.apostamiento_name);
        Spinner clienteName = dialog.getCustomView().findViewById(R.id.cliente_name);
        if(Databases.plantillaIsSaved(groupToEdit)){
            dialog.getActionButton(DialogAction.NEGATIVE).setEnabled(false);
        }
        if(spGuards.getCount() < 1){
            spGuards.setEnabled(false);
            spApName.setEnabled(false);
            clienteName.setEnabled(false);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
        if(spApName.getCount() < 1){
            spGuards.setEnabled(false);
            spApName.setEnabled(false);
            clienteName.setEnabled(false);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
        if(spGuards.getCount() < 1){
            spGuards.setEnabled(false);
            spApName.setEnabled(false);
            clienteName.setEnabled(false);
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
    }

    private void confirmGuardar(final String grupo){
        new MaterialDialog.Builder(this)
                .title(R.string.guardar_plantilla_text)
                .content(R.string.guardar_advice_text)
                .positiveText(R.string.enviar_text)
                .negativeText(R.string.cancel_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try{
                            Databases.SavePlantillaToServer(grupo,getApplicationContext());
                        }catch(JSONException error){
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .show();
    }

    private Boolean isEmptyList(String ddSelected){
        Boolean isEmpty = true;
        if(!ddSelected.equals("Presiona +")){
            isEmpty = false;
        }
        return isEmpty;
    }

    public void saveapt(MaterialDialog dialog){
        View v = dialog.getCustomView();
        String grupo = getSpinnerText(v,R.id.plantilla_no);
        String Nombre = getSpinnerText(v,R.id.guardia_name);
        String ap_name = getSpinnerText(v,R.id.apostamiento_name);
        String clname = getSpinnerText(v,R.id.cliente_name);
        Long provid = Databases.providerId(this);
        PlantillaPlace pp = new PlantillaPlace(grupo,Nombre,ap_name,clname);
        pp.setProvId(String.valueOf(provid));
        long apid = pp.save();
        if(apid != 0L){
            Aps insertedAp = new Aps(apid,Nombre,ap_name,null);
            mAps.add(insertedAp);
            mAdapter.notifyDataSetChanged();
            removeSpinnerItem(dialog,Nombre,"elemento");
            removeSpinnerItem(dialog,ap_name,"apost");
            adjustCounter(dialog);
        }
    }

    public String getSpinnerText(View v,int spResource){
        Spinner sp = v.findViewById(spResource);
        return sp.getSelectedItem().toString();
    }

    public int getSpinnerCount(View v,int spResource){
        Spinner sp = v.findViewById(spResource);
        return sp.getAdapter().getCount();
    }

    private void setAddPlantillaOnClick(final View mainview){
        Button btn = mainview.findViewById(R.id.add_plantilla_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddPlantillaNumber(mainview);
            }
        });
    }

    private long elementoId(String needle){
        long gid = 0;
        List<Elementos> els = Databases.listAllElementos();
        for(int i = 0;i < els.size();i++){
            if(els.get(i).getGuardFullName().equals(needle)){
                gid = els.get(i).getId();
            }
        }
        return gid;
    }

    private void changeSpinnerItem(String olValue,String newValue){
        pl.remove(olValue);
        pl.add(newValue);
        pl.notifyDataSetChanged();
    }

    private void setCounters(MaterialDialog dialog){
        View v = dialog.getCustomView();
        TextView total = v.findViewById(R.id.gTotal_Text);
        TextView faltan = v.findViewById(R.id.gCount_text);
        MDButton btn = dialog.getActionButton(DialogAction.POSITIVE);
        int count = Integer.valueOf(faltan.getText().toString());
        total.setText(Integer.toString(plantillaTotal));
        if(mFaltan <= 0){
            if(count < 1){
                if(!plComplete){
                    mFaltan = plantillaTotal;
                    faltan.setText(Integer.toString(plantillaTotal));
                }else{
                    btn.setEnabled(false);
                }
            }
        }else{
            faltan.setText(Integer.toString(mFaltan));
        }
    }

    private void adjustCounter(MaterialDialog dialog){
        View v = dialog.getCustomView();
        TextView faltan = v.findViewById(R.id.gCount_text);
        MDButton btn = dialog.getActionButton(DialogAction.POSITIVE);
        mFaltan = mFaltan-1;
        faltan.setText(Integer.toString(mFaltan));
        if(mFaltan == 0){
            btn.setEnabled(false);
            plComplete = true;
        }
    }

    private void removeSpinnerItem(String value){
        pl.remove(value);
        pl.notifyDataSetChanged();
    }

    private boolean removeSpinnerItem(MaterialDialog dialog,String value,String wich){
        switch(wich){
            case "elemento":
                Spinner sp = dialog.getCustomView().findViewById(R.id.guardia_name);
                mAdapterElementos.remove(value);
                mAdapterElementos.notifyDataSetChanged();
                if(sp.getCount() < 1){
                    sp.setEnabled(false);
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
                break;
            case "apost":
                Spinner spin = dialog.getCustomView().findViewById(R.id.cliente_name);
                mAdapterAps.remove(value);
                mAdapterAps.notifyDataSetChanged();
                if(spin.getCount() < 1){
                    spin.setEnabled(false);
                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
                break;
        }
        return true;
    }

    private void setAddPlantillaNumber(View v){
        String plantillaText;
        int spcount = getSpinnerCount(v,R.id.plantilla_no);
        int consCount = spcount + 1;
        String spText = getSpinnerText(v,R.id.plantilla_no);
        if(!isEmptyList(spText) && spcount >= 1){
            plantillaText = "Grupo " + consCount;
            pl.add(plantillaText);
            pl.notifyDataSetChanged();
        }else{
            plantillaText = "Grupo " + spcount;
            changeSpinnerItem("Presiona +",plantillaText);
        }
    }

    public boolean ValidateGroup(String grupo){
        boolean passed = false;
        if(!grupo.equals("Presiona +")){
            passed = true;
        }
        return passed;
    }


    private void setSpinnerData(Spinner sp,@Nullable String wich,List<String> data){
        switch(wich){
            case "elementos":
                    if(mAdapterElementos == null){
                        mAdapterElementos = new ArrayAdapter<String>(getApplication(),R.layout.spinner_item,gNames);
                    }
                mAdapterElementos.setDropDownViewResource(R.layout.dsc_spinner_dropdown);
                sp.setAdapter(mAdapterElementos);
            break;
            case "apostamientos":
                if(mAdapterAps == null){
                    mAdapterAps = new ArrayAdapter<String>(getApplication(),R.layout.spinner_item,data);
                }
                mAdapterAps.setDropDownViewResource(R.layout.dsc_spinner_dropdown);
                sp.setAdapter(mAdapterAps);
                break;
            case "clientes":
                if(mAdapterClients == null){
                    mAdapterClients = new ArrayAdapter<String>(getApplication(),R.layout.spinner_item,data);
                }
                mAdapterClients.setDropDownViewResource(R.layout.dsc_spinner_dropdown);
                sp.setAdapter(mAdapterClients);
                break;
        }
    }

    public View getDialogView(MaterialDialog dialog){
        return dialog.getCustomView();
    }

    private void setPlSpinnerData(View v){
        Spinner clients = v.findViewById(R.id.cliente_name);
        Spinner elements = v.findViewById(R.id.guardia_name);
        Spinner aps = v.findViewById(R.id.apostamiento_name);
        setSpinnerData(aps,"apostamientos",apnames);
        setSpinnerData(elements,"elementos",gNames);
        setSpinnerData(clients,"clientes",clnames);
        plList.add(groupToEdit);
        pl = new ArrayAdapter<String>(getApplication(),R.layout.spinner_item,plList);
        Spinner sp = v.findViewById(R.id.plantilla_no);
        pl.setDropDownViewResource(R.layout.dsc_spinner_dropdown);
        sp.setAdapter(pl);
        sp.setEnabled(false);
    }

    private void setSpinnersData(MaterialDialog dialog){
        View v = dialog.getCustomView();
        setPlSpinnerData(v);
    }

    //ReciclerView Adapter
    public class ElementoAdapter extends RecyclerView.Adapter<ElementoAdapter.ElementoViewHolder>{
        String SPACE = " ";
        private List<Aps> mDataset;
        int currPosition;

        public ElementoAdapter(List<Aps> elemento){
            this.mDataset = elemento;
        }

        @Override
        public void onBindViewHolder(ElementoViewHolder VH,int position){
            currPosition = position -1;
            final Aps guard = mDataset.get(position);
            if(guard != null){
                Bitmap guardProfilePhoto = profileImage(guard.getPerson_photo());
                VH.element_fullname.setText(guard.getPerson_name());
                VH.element_apt.setText(guard.getPerson_apt());
                VH.element_action_menu.setImageResource(R.drawable.baseline_delete_black_18);
                VH.element_action_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAp(guard.getApid());
                    }
                });
                if(guardProfilePhoto != null){
                    VH.element_photo.setImageBitmap(guardProfilePhoto);
                }else{
                    VH.element_photo.setImageResource(R.drawable.profile_no_camera);
                }

            }
        }

        private void deleteAp(long id){
            Databases.deleteApFromDb(id);
            EditPlantilla.this.mAps.remove(currPosition);
            EditPlantilla.this.mAdapter.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ElementoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.guard_item_view,parent,false);
            return new ElementoViewHolder(itemview);
        }

        public class ElementoViewHolder extends RecyclerView.ViewHolder{
            CardView cv;
            TextView element_fullname;
            TextView element_apt;
            TextView elementid;
            ImageView element_photo;
            ImageView element_action_menu;
            public ElementoViewHolder(View view){
                super(view);
                cv = (CardView) view.findViewById(R.id.element_item);
                element_photo = (ImageView) view.findViewById(R.id.person_photo);
                element_fullname = (TextView) view.findViewById(R.id.person_name);
                element_apt = (TextView) view.findViewById(R.id.person_apt);
                element_action_menu = (ImageView) view.findViewById(R.id.element_menu);
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

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
