package com.example.gsb_medicine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextDenomination , editTextforme_pharmaceutique,editTextTitulaire,editTextdenomination_substance;
    private Spinner spinner;
    private Button buttonrechercher, buttonquitter,buttondeconnexion;
    private ListView listView;
    private DatabaseHelper dbHelper;
    private static final String PREF_NAME="userPref";
    private static final String KEY_USER_STATUS="userStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //initialiser les composant des interface utilisateur(medoc,liste deroulante)
        editTextDenomination= findViewById(R.id.edit_text_denomination);
        editTextforme_pharmaceutique=findViewById(R.id.edit_text_forme_pharmaceutique);
        editTextTitulaire = findViewById(R.id.edit_text_titulaire);
        editTextdenomination_substance= findViewById(R.id.edit_text_denomination_substance);
        spinner = findViewById(R.id.action_bar_spinner_liste_deroulante);
        buttonrechercher = findViewById(R.id.btn_rechercher);
        buttonquitter =findViewById(R.id.btn_quitter);
        buttondeconnexion = findViewById(R.id.btn_cle_deconnexion);
        listView = findViewById(R.id.listView);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Set up the spinner with Voies_dadministration data
        //setupVoiesAdminSpinner();

        // Set up the click listener for the search button
        buttonrechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the search and update the ListView
                performSearch();
                //cacherClavier();
            }
        });

    }
    private boolean isUserAuthenticated() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userStatus = preferences.getString(KEY_USER_STATUS, "");

        // VÃ©rifiez si la chaÃ®ne d'Ã©tat de l'utilisateur est "authentification=OK"
        return "authentification=OK".equals(userStatus);
    }

    private void performSearch() {
        private void cacherClavier() {
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
        String denomination = editTextDenomination.getText().toString().trim();
        String formePharmaceutique = editTextforme_pharmaceutique.getText().toString().trim();
        String titulaires = editTextTitulaire.getText().toString().trim();
        String denomination_substance =  editTextdenomination_substance.getText().toString().trim();
        String voiesAdmin = spinner.getSelectedItem().toString().trim();
        List<Medicament> searchResults = dbHelper.searchMedicament(denomination, formePharmaceutique,titulaires,denomination_substance, voiesAdmin);

        MedicamentAdapter adapter = new MedicamentAdapter(this, searchResults);
        listView.setAdapter(adapter);
    }
    //on met string et pas edit texte parce que mtnt on veux pouvoir voir avec le getTex les composant et tostring permet de mettre en str tt ce qui a etait recuperer
}
}

//        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//           @Override
//           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//               // Get the selected item
//               Medicament selectedMedicament = (Medicament) adapterView.getItemAtPosition(position);
//               // Show composition of the selected medicament
//               afficherCompositionMedicament(selectedMedicament);
//           }
//       }