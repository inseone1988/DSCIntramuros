package mx.com.vialogika.dscintramuros;

import android.app.Activity;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.content.Context;

interface fab{
    void setFabClickListener();
}

public class dsc_dashboard extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks, fragment_dsc_plantillas.OnFragmentInteractionListener,dsc_elements.OnFragmentInteractionListener,dsc_apostamientos.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private boolean fabpresent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar =getActionBar();
        setContentView(R.layout.activity_dsc_dashboard);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, getFragment(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public Fragment getFragment(int sectionid){
        Fragment fragment = null;
        switch(sectionid){
            case 1:
                fragment = new fragment_dsc_plantillas();
                break;
            case 2:
                fragment = new dsc_elements();
                break;
            case 3:
                fragment = new dsc_apostamientos();
                break;
        }
        return fragment;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static Integer seccion;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            seccion = defineView(sectionNumber);
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public static Integer defineView(int sectionNumber){
            int myView = 1;
            switch(sectionNumber){
                case 1:
                    myView = R.layout.fragment_dsc_plantillas;
                    break;
                case 2:
                    myView = R.layout.fragment_dsc_dashboard;
                    break;
                case 3:
                    myView = R.layout.fragment_dsc_apostamientos;
                    break;
            }

            return myView;
        }

        public void addElementListener(Context context){
            Intent intent = new Intent(context,editElement.class);
            startActivity(intent);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            final View rootView = inflater.inflate(seccion, container, false);
            if (seccion == R.layout.fragment_dsc_dashboard){
                FloatingActionButton fab = rootView.findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){

                      addElementListener(rootView.getContext());
                    }
                });
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((dsc_dashboard) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
