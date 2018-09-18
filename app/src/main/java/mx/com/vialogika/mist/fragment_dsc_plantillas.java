package mx.com.vialogika.mist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_dsc_plantillas.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */

interface  plantilla{
    void onDataRetrieved();
}
public class fragment_dsc_plantillas extends Fragment {
    private OnFragmentInteractionListener mListener;
    private List<GruposView> mGrupos;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mGroup;

    public fragment_dsc_plantillas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View rootview = inflater.inflate(R.layout.fragment_dsc_plantillas, container, false);
        setFab(rootview);
        initList(new db() {
            @Override
            public void onDataRetrieved() {
                if(mGrupos.size() == 0){
                    setNoDataImage(rootview);
                }
                if((mGrupos != null) || (mGrupos.size() != 0)){
                    mAdapter = new PlantillasAdapter(mGrupos);
                    mRecyclerView = rootview.findViewById(R.id.plantillas_view);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    setNoDataImage(rootview);
                }
            }
        });
        // Inflate the layout for this fragment
        return rootview;
    }

    private void setNoDataImage(View v){
        ImageView iv = v.findViewById(R.id.nDataImg);
        iv.setImageResource(R.drawable.advicelogo);
    }
    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        mGrupos.clear();
        initList(new db() {
            @Override
            public void onDataRetrieved() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setNoDataText(View v){
        TextView tv = v.findViewById(R.id.no_data_text);
        tv.setText(R.string.no_items_to_show);
    }

    private void initList(db callback){
        String sitename = Databases.siteName(getActivity());
        String providername = Databases.providername(getActivity());
        List<Plantillas> grupos = Databases.getTodayGroups();
        String plTotal = String.valueOf(Databases.PlantillaNoPlaces());
        if(grupos.size() > 0){
            for(int i = 0; i < grupos.size();i++){
                String grupo = grupos.get(i).getTurno();
                String reported = Long.toString(Databases.plGroupCount(grupo));
                GruposView gv = new GruposView(grupos.get(i).getSaved(),i + 1,sitename,providername,plTotal,reported,grupo);
                if(mGrupos == null){
                    mGrupos = new ArrayList<GruposView>();
                }
                mGrupos.add(gv);
            }
        }else{
            mGrupos = new ArrayList<GruposView>();
        }
        callback.onDataRetrieved();
    }

    private void setFab(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab_add_plantilla);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mGroup == null){
                    if(mGrupos.isEmpty()){
                        mGroup = "Grupo 1";
                    }else{
                        int gconsec = mGrupos.size() + 1;
                        mGroup = "Grupo " + String.valueOf(gconsec);
                    }
                }else{
                    int gconsec = mGrupos.size() + 1;
                    mGroup = "Grupo " + String.valueOf(gconsec);
                }
                Intent intent = new Intent(getActivity(),EditPlantilla.class);
                intent.putExtra("MODE","new");
                intent.putExtra("EditPlantilla",mGroup);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class PlantillasAdapter extends RecyclerView.Adapter<PlantillasAdapter.PlantillasViewHolder>{

        private List<GruposView> mDataset;

        public PlantillasAdapter(List<GruposView> gruposViews){
            this.mDataset = gruposViews;
        }

        public class PlantillasViewHolder extends RecyclerView.ViewHolder{

            RelativeLayout rLayout;
            TextView plTextView;
            TextView sitename;
            TextView provName;
            TextView plCount;
            TextView plTotal;

            public PlantillasViewHolder(View view){
                super(view);
                rLayout = view.findViewById(R.id.group_info);
                plTextView = view.findViewById(R.id.plantilla_no_text);
                sitename = view.findViewById(R.id.plantilla_site_text);
                provName = view.findViewById(R.id.plantilla_provider_text);
                plCount = view.findViewById(R.id.plantilla_count);
                plTotal = view.findViewById(R.id.plantilla_total);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull PlantillasViewHolder holder, int position) {
            GruposView gv = mDataset.get(position);
            String isSaved = gv.getSaved();
            int[] plInfo = new int[]{Integer.parseInt(gv.getPlantillaTotal()),Integer.parseInt(gv.getPlantillaCount())};
            holder.plTextView.setText(String.valueOf(gv.getGpoNo()));
            holder.sitename.setText(gv.getSiteName());
            holder.provName.setText(gv.getProviderName());
            holder.plCount.setText(gv.getPlantillaCount());
            holder.plTotal.setText(gv.getPlantillaTotal());
            if(!isSaved.equals("saved")){
                setRlayoutonClick(holder.rLayout,gv.getGrupo(),plInfo);
            }else{
                holder.plTextView.setTextColor(Color.parseColor("#2AC007"));
                showNoEditableToast(holder.rLayout);
            }

        }

        @NonNull
        @Override
        public PlantillasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View pInfoView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.plantilla_view,parent,false);
            return new PlantillasViewHolder(pInfoView);
        }



        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        private void setRlayoutonClick(RelativeLayout rl, final String groupToEdit,final int[] plInfo){
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),EditPlantilla.class);
                    intent.putExtra("MODE","edit");
                    intent.putExtra("EditPlantilla",groupToEdit);
                    intent.putExtra("PlantillaTotal",plInfo[0]);
                    intent.putExtra("PlantillaFaltan",plInfo[1]);
                    startActivity(intent);
                }
            });
        }

        private void showNoEditableToast(RelativeLayout rl){
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getActivity(),"No editable",Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
