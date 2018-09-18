
package mx.com.vialogika.mist;

import android.app.Activity;
import android.net.Uri;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.gotev.uploadservice.UploadService;

import java.util.List;


interface fab{
    void setFabClickListener();
}

public class dsc_dashboard extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks, fragment_dsc_plantillas.OnFragmentInteractionListener,dsc_elements.OnFragmentInteractionListener,dsc_apostamientos.OnFragmentInteractionListener,PIEFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private List<Elementos> mElementos;
    private List<Apostamientos> mApostamientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
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
    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public void onFragmentInteraction() {

    }

    @Override
    public List<Elementos> getElemList() {
        return null;
    }

    @Override
    public List<Apostamientos> getApostamientos() {
        return null;
    }

     @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if(position <= 3){
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, getFragment(position + 1))
                    .commit();
        }else{
            if(position == 4){
                new MaterialDialog.Builder(this)
                        .content("Salir de la App")
                        .positiveText("Salir")
                        .negativeText("Cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finishAndRemoveTask();
                            }
                        }).show();
            }
        }
        //restoreActionBar();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                restoreActionBar();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                restoreActionBar();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                restoreActionBar();
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
                fragment = new PIEFragment();
                break;
            case 4:
                fragment = new restricted_consult();
        }
        return fragment;
    }
}
