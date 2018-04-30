package mx.com.vialogika.dscintramuros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;


interface apCallbacks{
    void onApget(int listSize);
}

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link dsc_apostamientos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class dsc_apostamientos extends Fragment {
    private OnFragmentInteractionListener mListener;
    private List<Apostamientos> aplist;
    private RecyclerView mReciclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public dsc_apostamientos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_dsc_apostamientos,container,false);
        setFab(rootview);
        getApostamientos(new apCallbacks(){
            @Override
            public void onApget(int listsize) {
                if(listsize > 0){
                    mAdapter = new apAdapter(aplist);
                    mReciclerView = rootview.findViewById(R.id.apostamientos_view);
                    mReciclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mReciclerView.setLayoutManager(mLayoutManager);
                    mReciclerView.setAdapter(mAdapter);
                }else{
                    setNoDataText(dsc_apostamientos.this.getActivity(),rootview);
                }

            }
        });
        return rootview;
    }

    public void setFab(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab_add_apostamientos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              MaterialDialog md = new MaterialDialog.Builder(getActivity())
                .title("Nuevo apostamiento")
                .customView(R.layout.add_apostamiento_client,true)
                .positiveText("guardar")
                .negativeText("Cancelar")
                .build();
              final View Dialogview = md.getCustomView();
              md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                      saveApostamiento(Dialogview);
                  }
              });
              setClientSpinner(Dialogview);
              setApTypeSpinner(Dialogview);
              md.show();
            }
        });
    }

    public void saveApostamiento(View v){
        String SPACE = " ";
        Number UNO = 1;
        EditText apname = v.findViewById(R.id.ap_name);
        Spinner aptype = v.findViewById(R.id.ap_type);
        Spinner apcliente = v.findViewById(R.id.client_select);
        String ap = apname.getText().toString();
        String apType = aptype.getSelectedItem().toString();
        String apCliente = apcliente.getSelectedItem().toString();
        Apostamientos newAp = new Apostamientos(UNO.longValue(),UNO.longValue(),apType + SPACE +  apCliente,ap,apType,UNO.longValue());
        newAp.save();
    }

    public int getlistsize(){
        return aplist.size();
    }

    public void setApTypeSpinner(View v){
        String[] aptypes = new String[]{"Filtro","Accesos/Aduanas","Otro","Especial"};
        ArrayAdapter<String> aa = new ArrayAdapter<String>(dsc_apostamientos.this.getActivity(),android.R.layout.simple_spinner_item,aptypes);
        Spinner sp = v.findViewById(R.id.ap_type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(aa);
    }

    public void setClientSpinner(View v){
        String[] clients = new String[]{"Natura","Hersheys","Pernod Ricard","NIKE","General"};
        ArrayAdapter<String> aa = new ArrayAdapter<String>(dsc_apostamientos.this.getActivity(),android.R.layout.simple_spinner_item,clients);
        Spinner sp = v.findViewById(R.id.client_select);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(aa);
    }

    public void setNoDataText(Context context,View view){
        TextView tv = (TextView) view.findViewById(R.id.no_data_text);
        tv.setText(R.string.no_items_to_show);
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void getApostamientos(apCallbacks callback){
        List<Apostamientos> ap = Apostamientos.listAll(Apostamientos.class);
        aplist = ap;
        callback.onApget(aplist.size());
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

    public class apAdapter extends RecyclerView.Adapter<apAdapter.apViewHolder>{

        private List<Apostamientos> mDataset;
        private String SPACE = "";

        public apAdapter(List<Apostamientos> ap){
            mDataset = ap;
        }

        @NonNull
        @Override
        public apAdapter.apViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.apostamientos_container,parent,false);

            return new apViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull apViewHolder holder, int position) {
            final Apostamientos ap = mDataset.get(position);
            holder.no_guardias.setText(ap.getSite_id().toString());
            holder.nombre_cliente.setText(ap.getApostamiento_name());
            holder.ap_alias.setText(ap.getApostamiento_alias());
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class apViewHolder extends RecyclerView.ViewHolder{
            CardView cv;
            TextView no_guardias;
            TextView nombre_cliente;
            TextView ap_alias;
            ListView lv;
            public apViewHolder(View view){
                super(view);
                cv = (CardView) view.findViewById(R.id.ap_card);
                no_guardias = (TextView) view.findViewById(R.id.place_no_of_items);
                nombre_cliente = (TextView) view.findViewById(R.id.place_client_name);
                ap_alias = (TextView) view.findViewById(R.id.ap_alias);
            }
        }



    }


}
