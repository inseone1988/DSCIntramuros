package mx.com.vialogika.mist.DialogsImp;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import mx.com.vialogika.mist.R;
import mx.com.vialogika.mist.Utils.CustomAutoCompleteTextView;

public class PlantillaEditDialog extends MaterialDialog.Builder {

    private TextView totalguards, nocovered;
    private EditText elementName, elementAp;
    private Spinner incidentType, incidentreason;
    private MaterialDialog             dialog;
    private View                       customView;
    private CustomAutoCompleteTextView gedit, apedit;

    private List<String> elementos;   //Elementos
    private List<String> apList;      //Apostaamientos
    private List<String> incList;     //Incidencias
    private List<String> incTypeList; //Tipod e incidencia

    private ArrayAdapter guardAdapter, apAdapter, incAdapter, incReasonAdapter;


    public PlantillaEditDialog(@NonNull Context context) {
        super(context);
        this.title(R.string.assign_ap);
        this.customView(R.layout.add_plantilla_apostamiento, true);
        this.positiveText("Agregar guardia");
        this.negativeText("Guardar plantilla");
        init();
    }

    private void init() {
        initAutocompletes();
    }

    private void getCustomView() {
        customView = dialog.getCustomView();
    }

    private void getInputViews() {
        totalguards = customView.findViewById(R.id.gTotal_Text);
        nocovered = customView.findViewById(R.id.gCount_text);
        //elementName = customView.findViewById();
        //elementAp = customView.findViewById();
        incidentType = customView.findViewById(R.id.incidencia);
        incidentreason = customView.findViewById(R.id.tipoincidncia);
    }

    private void initAutocompletes() {
        LinearLayout ll  = customView.findViewById(R.id.gllayout);
        LinearLayout ll1 = customView.findViewById(R.id.apLinLayout);
        gedit = new CustomAutoCompleteTextView(getContext());
        gedit.setHint(R.string.guard_name);
        gedit.setLayoutParams(ll.getLayoutParams());
        apedit = new CustomAutoCompleteTextView(getContext());
        apedit.setHint(R.string.element_place);
        apedit.setLayoutParams(ll1.getLayoutParams());
        ll.addView(gedit);
        ll1.addView(apedit);
    }

    private void getDialog() {
        dialog = this.build();
    }
}
