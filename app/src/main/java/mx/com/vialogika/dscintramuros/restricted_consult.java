package mx.com.vialogika.dscintramuros;


import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.databinding.ObservableList;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link restricted_consult#newInstance} factory method to
 * create an instance of this fragment.
 */
public class restricted_consult extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String searchString;
    private String searchType;
    private JSONObject vetadosData;
    private List<Vetado> vetados;

    private EditText searchBox;
    private ImageView goSearch;
    private RadioGroup searchTypeContainer;
    private RadioButton selectedSearchType;

    private RecyclerView mReciclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout ll;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public restricted_consult() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment restricted_consult.
     */
    // TODO: Rename and change types and number of parameters
    public static restricted_consult newInstance(String param1, String param2) {
        restricted_consult fragment = new restricted_consult();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void initializeVetados(){
        vetados = new ObservableArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            View root = null;
            initializeVetados();
            root = inflater.inflate(R.layout.fragment_restricted_consult, container, false);
            getItems(root);
            setup();
            setListeners();
        return root;
    }

    private void getNetworkVetadoData(String searchString,String searchType){
        Databases.vetadoSearch(searchString, searchType, getActivity(), new Databases.callbacks() {
            @Override
            public void onResponse(JSONObject response) {
                setAndMapResponse(response);
            }

            @Override
            public void onResponseError(VolleyError error) {

            }

            @Override
            public void onDbUpdateSuccess() {

            }
        });
    }

    private void setAndMapResponse(JSONObject response){

        try{
            if(response.getBoolean("success")){
                JSONArray payload = response.getJSONArray("payload");
                for(int i = 0;i < payload.length();i++){
                    response = payload.getJSONObject(i);
                    Vetado vt = new Vetado(response.getInt("idpersons"),response.getString("due_date"),response.getString("person_fullname"),response.getString("provider_alias"),response.getString("restriction_obs"),response.getString("restriction_type"));
                    vetados.add(vt);
                    mAdapter.notifyDataSetChanged();
                    if(vetados.size() != 0){
                        ll.setVisibility(View.GONE);
                        mReciclerView.setVisibility(View.VISIBLE);
                    }else{
                        ll.setVisibility(View.VISIBLE);
                        mReciclerView.setVisibility(View.GONE);
                    }
                }
            }else{
                Toast.makeText(getActivity(),"No se han encontrado coincidencias",Toast.LENGTH_SHORT).show();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void setup(){
        mAdapter = new VetadoSearchAdapter(vetados);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mReciclerView.setLayoutManager(mLayoutManager);
        mReciclerView.setAdapter(mAdapter);

    }

    private void getItems(View v){
        searchBox = v.findViewById(R.id.search);
        goSearch = v.findViewById(R.id.go_search);
        searchTypeContainer = v.findViewById(R.id.search_type_cont);
        selectedSearchType = v.findViewById(searchTypeContainer.getCheckedRadioButtonId());
        mReciclerView = v.findViewById(R.id.vetados_view);
        ll = v.findViewById(R.id.empty_vetados);
    }

    private void getSearchString(){
        searchString = searchBox.getText().toString();
        searchType = selectedSearchType.getText().toString();
    }

    private void setListeners(){
        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchResults();
                getSearchString();
                if(searchString.equals("")){
                    Toast.makeText(getActivity(),"Ingresa un termino de busqueda",Toast.LENGTH_SHORT).show();
                }else{
                    getNetworkVetadoData(searchString,searchType);
                    clearSearchBox();
                }
            }
        });

        searchTypeContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedSearchType = group.findViewById(checkedId);
            }
        });
    }

    private void clearSearchBox(){
        searchBox.setText("");
    }

    private void clearSearchResults(){
        vetados.clear();
        mAdapter.notifyDataSetChanged();
    }

    //ReciclerView adapter


    private class VetadoSearchAdapter extends RecyclerView.Adapter<VetadoSearchAdapter.VetadoSearchViewHolder>{

        private List<Vetado> mDataset;


        public class VetadoSearchViewHolder extends RecyclerView.ViewHolder{
            CardView container;
            TextView person_name,person_provider,tipo_veto;
            public VetadoSearchViewHolder(View v){
                super(v);
                container = v.findViewById(R.id.vetado_container);
                person_name = v.findViewById(R.id.person_name);
                person_provider = v.findViewById(R.id.person_provider);
                tipo_veto = v.findViewById(R.id.tipo_veto);
            }
        }

        public VetadoSearchAdapter(List<Vetado> vetados) {
                mDataset = vetados;
        }

        @NonNull
        @Override
        public VetadoSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.vetado_view,parent,false);
            return new VetadoSearchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull VetadoSearchViewHolder holder, int position) {
            Resources res = getResources();
            Vetado vt = mDataset.get(position);
            String pText = res.getString(R.string.provider_vetado_placeholder,vt.getProvider_alias());
            String tVeto = res.getString(R.string.tipo_veto_placeholder,vt.getRestriction_type());
            holder.person_name.setText(vt.getPerson_fullname());
            holder.person_provider.setText(pText);
            holder.tipo_veto.setText(tVeto);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
