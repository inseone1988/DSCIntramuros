 package mx.com.vialogika.dscintramuros;

import android.app.Activity;
import android.app.Application;
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
import android.widget.AutoCompleteTextView;
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
     private String MODE = "new";
     private String groupToEdit = "Grupo 1";
     private List<String> gNames;
     private List<String> notAvailableguards;
     private List<Aps> mAps;
     private final int plantillaTotal = (int) Databases.PlantillaNoPlaces();
     private int mFaltan;
     private boolean plComplete = false;


     private RecyclerView mRecyclerview;
     private RecyclerView.Adapter mAdapter;
     private RecyclerView.LayoutManager mLayoutManager;

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
                mAdapter = new ElementoAdapter(mAps, new events() {
                    @Override
                    public void onApDeleted(int position) {
                        mAps.remove(position);
                        mRecyclerview.removeViewAt(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyItemRangeChanged(position,mAps.size());
                    }
                });
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
                 .content("Entrado en modo " + this.MODE)
                 .show();
     }

     private void setFab(View view){
        final Context mContext = this;
        FloatingActionButton fab = view.findViewById(R.id.fab_add_ap);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EditPlaces(mContext, getDialogPayload(), new actions() {
                        @Override
                        public void onApostamientoSaved(EditPlaces instance) {
                            mAps.add(instance.getLastSavedAp());
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onIncidenceConfirm(EditPlaces instance) {

                        }

                        @Override
                        public void onSaveToDb(EditPlaces instance) {
                            instance.hideDialog();
                            confirmGuardar(instance.getGrupo());
                        }

                        @Override
                        public int checkHasElements() {
                            return mAps.size() ;
                        }
                    });
                }
            });

    }

    private MaterialDialogPayload getDialogPayload(){
        return new MaterialDialogPayload(MODE,groupToEdit);
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
                            Databases.SavePlantillaToServer(grupo, getApplicationContext(), new Databases.generic() {
                                @Override
                                public void callback() {
                                    finish();
                                }
                            });
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

    //ReciclerView Adapter
    public class ElementoAdapter extends RecyclerView.Adapter<ElementoAdapter.ElementoViewHolder>{
        String SPACE = " ";
        private List<Aps> mDataset;
        int currPosition;
        private events callbacks;

        public ElementoAdapter(List<Aps> elemento,events mEvents){
            this.callbacks = mEvents;
            this.mDataset = elemento;
        }

        @Override
        public void onBindViewHolder(ElementoViewHolder VH, final int position){
            currPosition = position;
            final Aps guard = mDataset.get(position);
            if(guard != null){
                Bitmap guardProfilePhoto = profileImage(guard.getPerson_photo());
                VH.element_fullname.setText(guard.getPerson_name());
                VH.element_apt.setText(guard.getPerson_apt());
                VH.element_action_menu.setImageResource(R.drawable.baseline_delete_black_18);
                VH.element_action_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAp(guard.getApid(),position);
                    }
                });
                if(guardProfilePhoto != null){
                    VH.element_photo.setImageBitmap(guardProfilePhoto);
                }else{
                    VH.element_photo.setImageResource(R.drawable.profile_no_camera);
                }
            }
        }

        private void deleteAp(long id,int position){
            Databases.deleteApFromDb(id);
            callbacks.onApDeleted(position);
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
    interface events{
        void onApDeleted(int position);
    }
}
