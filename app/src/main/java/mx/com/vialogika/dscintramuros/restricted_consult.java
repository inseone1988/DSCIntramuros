package mx.com.vialogika.dscintramuros;


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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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

    private EditText searchBox;
    private ImageView goSearch;
    private RadioGroup searchTypeContainer;
    private RadioButton selectedSearchType;

    private RecyclerView mReciclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_restricted_consult, container, false);
        getItems(root);
        setListeners();
        return root;
    }


    private void setup(){
        mLayoutManager = new LinearLayoutManager(getActivity());
    }

    private void getItems(View v){
        searchBox = v.findViewById(R.id.search);
        goSearch = v.findViewById(R.id.go_search);
        searchTypeContainer = v.findViewById(R.id.search_type_cont);
        mReciclerView = v.findViewById(R.id.vetados_view);

    }

    private void getSearchString(){
        searchString = searchBox.getText().toString();
    }

    private void setListeners(){
        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        searchTypeContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedSearchType = group.findViewById(checkedId);
            }
        });
    }

    //ReciclerView adapter


    private class VetadoSearchAdapter extends RecyclerView.Adapter<VetadoSearchAdapter.VetadoSearchViewHolder>{


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

        public VetadoSearchAdapter() {

        }

        @NonNull
        @Override
        public VetadoSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vetado_view,parent,false);
            return new VetadoSearchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull VetadoSearchViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
