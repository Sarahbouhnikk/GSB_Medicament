package com.example.gsb_medicament;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    //private static final String PREF_NAME = "UserPref";
    //private static final String KEY_USER_STATUS = "UserStatus";
    private EditText ediTextDenomination,ediTextFormePharmaceutique,ediTextTitulaire,ediTextDenominationSubstance;
    private Button btnSearch,btnDeconnexion,btnQuitter;
    private Spinner spinner;
    private ListView listViewResults;
    private DatabaseHelper dbHelper;


    //**CONSTRUCTEUR**//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//le lie avec la vue xml
        //on va initialiser les composants de l'UI user iterfzce interface utilisateur
        ediTextDenomination=findViewById(R.id.edit_text_denomination_du_medicament);
        ediTextFormePharmaceutique=findViewById(R.id.edit_text_forme_pharmacutique);
        ediTextTitulaire=findViewById(R.id.edit_text_titulaire);
        ediTextDenominationSubstance=findViewById(R.id.edit_text_denomination_substance);
        btnSearch=findViewById(R.id.btn_rechercher);
        btnDeconnexion=findViewById(R.id.btn_deconnexion);
        btnQuitter=findViewById(R.id.btn_quitterlapplication);
        listViewResults=findViewById(R.id.list_view);
        spinner=findViewById(R.id.spinner_1);

        dbHelper = new DatabaseHelper(this);// Initialize the database helper

        // Set up the spinner with Voies_dadministration data
        setupVoiesAdminSpinner(); //parametrer la lise deroulante avec la liste des voies admin appel de la fonction qui permet de remplir les valeur dans le spinner
        // Set up the click listener for the search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the search and update the ListView
                performSearch();
                cacherClavier();
            }
        });
     /*
        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the selected item
                Medicament selectedMedicament = (Medicament) adapterView.getItemAtPosition(position);
                // Show composition of the selected medicament
                afficherCompositionMedicament(selectedMedicament);

            }
        }
        */

    }


    //**METHODE**//
    /*
    private boolean isUserAuthenticated( ){
        SharedPreferences Preferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        //pref name c'est ce qu on a decla au dessus on va verifier sur shared preferences qui va verifier les pref de l aplli
        String userStatus = Preferences.getString(KEY_USER_STATUS,"");
        return "Authentification=OK".equals(KEY_USER_STATUS);
    }

     */

    private void performSearch(){
        //fonction qui se declenche des que je clik sur le bouton rechercher qui recup les info saisi

        String denomination = ediTextDenomination.getText().toString().trim();//on recup les info saisi dans la vue
        String formePharmaceutique = ediTextFormePharmaceutique.getText().toString().trim(); //enlever les espace sinon erreur dans requetevsql
        String titulaires = ediTextTitulaire.getText().toString().trim();
        String denominationsubstance = ediTextDenominationSubstance.getText().toString().trim();
        String voiesAdmin = spinner.getSelectedItem().toString().trim();
        //les resultats de recherche dans une liste
        List<Medicament> searchResults = dbHelper.searchMedicament(denomination,formePharmaceutique,titulaires,denominationsubstance,voiesAdmin);
        MedicamentAdapter adapter = new MedicamentAdapter(this, searchResults);
        listViewResults.setAdapter(adapter);
    }


    private void setupVoiesAdminSpinner() {
        // Fetch distinct Voies_dadministration data from the database and populate the spinner
        List<String> voiesAdminList = dbHelper.getDistinctVoiesAdmin();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, voiesAdminList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    public void cacherClavier() {
        // Obtenez le gestionnaire de fenetre
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Obtenez la vue actuellement focalisÃ©e, qui devrait Ãªtre la vue avec le clavier
        View vueCourante = getCurrentFocus();

        // VÃ©rifiez si la vue est non nulle pour Ã©viter les erreurs
        if (vueCourante != null) {
            // Masquez le clavier
            imm.hideSoftInputFromWindow(vueCourante.getWindowToken(), 0);
        }
    }



}