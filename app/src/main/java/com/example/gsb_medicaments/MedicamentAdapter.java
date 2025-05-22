package com.example.gsb_medicaments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class MedicamentAdapter extends ArrayAdapter<Medicament> {
    private TextView tvCodeCIS,tvDenomination,tvFormePharmaceutique, tvVoieAdmin,tvTitulaire,tvStatusAdmin,tvNbMolecule;
    public MedicamentAdapter(Context context, List<Medicament> medicaments) {
        super(context, 0, medicaments);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Medicament medicament = getItem(position);
        tvCodeCIS = convertView.findViewById(R.id.text_view_code_cis);
        tvDenomination=convertView.findViewById(R.id.text_view_denomination);
        tvFormePharmaceutique=convertView.findViewById(R.id.text_view_forme_pharmaceutique);
        tvVoieAdmin=convertView.findViewById(R.id.text_view_voie_admin);
        tvTitulaire=convertView.findViewById(R.id.text_view_titulaire);
        tvStatusAdmin=convertView.findViewById(R.id. text_view_status_administartif);
        tvNbMolecule=convertView.findViewById(R.id.text_view_nb_molecule);

        tvCodeCIS.setText(String.valueOf(medicament.getCodeCIS()));
        tvDenomination.setText(String.valueOf(medicament.getDenomination()));
        tvFormePharmaceutique.setText(String.valueOf(medicament.getFormePharmaceutique()));
        tvVoieAdmin.setText(String.valueOf(medicament.getVoiesAdmin()));
        tvTitulaire.setText(String.valueOf(medicament.getTitulaires()));
        tvStatusAdmin.setText(String.valueOf(medicament.getStatutAdministratif()));
        tvNbMolecule.setText(String.valueOf(medicament.getnbMolecules()));
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_medicament, parent, false);
        }
//cette class va heriter de medicament cette classe est en lien avec l objet medicament et va affecter a la vue les resultat
// un ta    }bleau qui va adapter un emseble de tableau une resultat de recherche

        //here le reste du codee
        return convertView;


    }
}
