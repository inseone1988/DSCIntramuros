package mx.com.vialogika.dscintramuros;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


interface db{
   void onDataRetrieved();
}



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link dsc_elements.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class dsc_elements extends Fragment {
    private OnFragmentInteractionListener mListener;
    private List<Elementos> guardsList;
    private RecyclerView mReciclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public dsc_elements() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_dsc_elements, container, false);
        setFab(rootview);
        getGuardsList(new db() {
            @Override
            public void onDataRetrieved() {
                mAdapter = new ElementAdapter(guardsList);
                mReciclerView = rootview.findViewById(R.id.elements_view);
                mReciclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mReciclerView.setLayoutManager(mLayoutManager);
                mReciclerView.setAdapter(mAdapter);
            }
        });
        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void getlist() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
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
    public void onResume() {
        super.onResume();

    }

    public void getGuardsList(db mCallback) {
        List<Elementos> mylist = Elementos.findWithQuery(Elementos.class,"SELECT * FROM Elementos");
        guardsList = mylist;
        mCallback.onDataRetrieved();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**public void setData(){
     * ---Testing  data---
        Guards guard = new Guards("Javier Ramirez","App Developer",R.drawable.profile_no_camera);
        guardsList.add(guard);
        guard = new Guards("Ana Castro","Central de Seguridad",R.drawable.profile_no_camera);
        guardsList.add(guard);
        guard = new Guards("Tania Nuñez","Central de Seguridad",R.drawable.profile_no_camera);
        guardsList.add(guard);
        mAdapter.notifyDataSetChanged();
    }*/

    public void setFab(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab_add_alements);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),editElement.class);
                startActivity(intent);
            }
        });
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
        void onFragmentInteraction();
        List<Elementos> getElemList();
        List<Apostamientos> getApostamientos();
    }

    public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementViewHolder>{
        String SPACE = " ";

        private List<Elementos> mDataset;

        public ElementAdapter(List<Elementos> guards){
            mDataset = guards;
        }

        @Override
        public void onBindViewHolder(ElementViewHolder holder,int position){
            final Elementos guard = mDataset.get(position);
            final int deletePosition = position;
            if(guard != null){
                Bitmap guardProfilePhoto = profileImage(guard.getPerson_photo_path());
                holder.element_fullname.setText(guard.getGuardFullName());
                holder.element_apt.setText(guard.getGuard_range());
                holder.element_action_menu.setImageResource(R.drawable.ic_delete_forever_black_24dp);
                holder.element_action_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .title(R.string.delete_title)
                                .content(R.string.delete_message)
                                .positiveText(R.string.ok)
                                .negativeText(R.string.cancel_text)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        deleteElement(guard.getId(),deletePosition);
                                    }
                                })
                                .show();
                    }
                });
                if(guardProfilePhoto != null){
                    holder.element_photo.setImageBitmap(guardProfilePhoto);
                }else{
                    holder.element_photo.setImageResource(R.drawable.profile_no_camera);
                }

            }else{

            }
        }

        @Override
        public ElementAdapter.ElementViewHolder onCreateViewHolder(ViewGroup parent,int viewtype){
            View itemview = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.guard_item_view,parent,false);
            return new ElementViewHolder(itemview);
        }

        public class ElementViewHolder extends RecyclerView.ViewHolder{
            CardView cv;
            TextView element_fullname;
            TextView element_apt;
            TextView elementid;
            ImageView element_photo;
            ImageView element_action_menu;

           public ElementViewHolder(View view){

                super(view);
                    cv = (CardView) view.findViewById(R.id.element_item);
                    element_photo = (ImageView) view.findViewById(R.id.person_photo);
                    element_fullname = (TextView) view.findViewById(R.id.person_name);
                    element_apt = (TextView) view.findViewById(R.id.person_apt);
                    element_action_menu = (ImageView) view.findViewById(R.id.element_menu);
            }
        }

        public void deleteElement(Long element_id,final int position){
            //First try to notify server flag element
            final Elementos element = Elementos.findById(Elementos.class,element_id);
            Databases.requestDeleteElement(element.getGuardHash(), getActivity(), new Databases.callbacks() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        if(response.getBoolean("success")){
                            element.delete();
                            mDataset.remove(position);
                            dsc_elements.this.mAdapter.notifyItemRemoved(position);
                            dsc_elements.this.mAdapter.notifyItemRangeChanged(position,mDataset.size());
                            Toast.makeText(getActivity(),R.string.delete_success,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),response.getString("error"),Toast.LENGTH_SHORT).show();
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
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

        @Override
        public int getItemCount(){
            return mDataset.size();
        }

    }
}
