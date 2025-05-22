package com.example.gsb_medicaments;

//classe qui contient toute les fonction pour permetrre une meilleur organisation du code

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//***DECLARATION DE LA CLASSE***//
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicaments.db";
    private String DATABASE_PATH; // vérifie si la BDD existe
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHelper sInstance; // permet la connexion à la BDD
    private Context mycontext;
    private static final String PREMIERE_VOIE= "choisir une voie d'administartion";

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    //***CONSTRUCTEUR***//
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext = context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkdatabase()) { // si database existe pas
            // copy db de 'assets' vers DATABASE_PATH
            Log.d("APP", "BDD a copier"); // classe qui permet d'afficher une erreur
            copydatabase(); // fonction qui copie la BDD au bonne endroit dans l'app

        }
    }
    //***METHODE***//
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Define the tables and necessary structures
        // Note: You should execute the appropriate CREATE TABLE queries here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            //Log.d("debug", "onUpgrade() : oldVersion=" + oldVersion + ",newVersion=" + newVersion);
            mycontext.deleteDatabase(DATABASE_NAME);
            copydatabase();
        }
    } // onUpgrade

    // TODO: Implement methods to interact with the database, such as fetching distinct Voies_dadministration


    private boolean checkdatabase() { // fonction qui vérifie si dans la BDD existe en fonction du nom
        // retourne true/false si la bdd existe dans le dossier de l'app
        File dbfile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbfile.exists();
    }

    private void copydatabase() {

        final String outFileName = DATABASE_PATH + DATABASE_NAME;

        //AssetManager assetManager = mycontext.getAssets();
        InputStream myInput;

        try {
            // Ouvre le fichier de la  bdd de 'assets' en lecture
            myInput = mycontext.getAssets().open(DATABASE_NAME);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if(!pathFile.exists()) {
                if(!pathFile.mkdirs()) {
                    Toast.makeText(mycontext, "Erreur : copydatabase(), pathFile.mkdirs()", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Ouverture en écriture du fichier bdd de destination
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfert de inputfile vers outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture
            Log.d("APP","BDD copiée");
            myOutput.flush();
            myOutput.close();
            myInput.close();

        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d("ERROR","erreur copie de la base");
            Toast.makeText(mycontext, "Erreur : copydatabase()", Toast.LENGTH_SHORT).show();
        }

        // on greffe le numéro de version
        try{
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        }
        catch(SQLiteException e) {
            // bdd n'existe pas
        }

    }

    public List<Medicament> searchMedicament(String denomination, String formePharmaceutique, String titulaires, String denominationsubstance, String voiesAdmin) {
        List<Medicament> medicamentList = new ArrayList<>();
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add("%" + denomination + "%");
        selectionArgs.add("%" + formePharmaceutique + "%");
        selectionArgs.add("%" + titulaires + "%");
        selectionArgs.add("%" + removeAccents(denominationsubstance) + "%");
        SQLiteDatabase db = this.getReadableDatabase();
        String finSQL ="";
        String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(upper(Denomination_substance), 'Â','A'),'Ä','A'),'À','A'),'É','E'),'Á','A'),'Ï','I'), 'Ê','E'),'È','E'),'Ô','O'),'Ü','U'), 'Ç','C' ) LIKE ?" ;
        // La requête SQL de recherche
        String query = "SELECT *,(select count(*) from CIS_COMPO_bdpm c where c.Code_CIS=m.Code_CIS) as nb_molecule FROM CIS_bdpm m  WHERE " +
                "Denomination_du_medicament LIKE ? AND " +
                "Forme_pharmaceutique LIKE ? AND " +
                "Titulaires LIKE ? AND " +
                "Code_CIS IN (" +SQLSubstance+ ")" +
                finSQL;
        if (!voiesAdmin.equals(PREMIERE_VOIE)) {
            finSQL = "AND Voies_dadministration LIKE ?";//va etre executer dans le code sql si une voiedadmin a ete selectionner
            selectionArgs.add("%" + voiesAdmin + "%");
        }//le point d interro c les param

        Cursor cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));//cursor contain result query

        if (cursor.moveToFirst()) {//si cursor contient encore des donnees
            do {
                int codeCIS = cursor.getInt(cursor.getColumnIndex("Code_CIS"));
                String denominationMedicament = cursor.getString(cursor.getColumnIndex("Denomination_du_medicament"));
                String formePharmaceutiqueMedicament = cursor.getString(cursor.getColumnIndex("Forme_pharmaceutique"));
                String voiesAdminMedicament = cursor.getString(cursor.getColumnIndex("Voies_dadministration"));
                String titulairesMedicament = cursor.getString(cursor.getColumnIndex("Titulaires"));
                String statutAdminMedicament = cursor.getString(cursor.getColumnIndex("Statut_administratif_de_lAMM"));
                //on recup tt les result de la requete dans des variable

                Medicament medicament = new Medicament();//on cree un objet medicament pour chaque medicament,cette ligne permet d instancier un new object
                medicament.setCodeCIS(codeCIS);//on utilise les setteur de la class medicament
                medicament.setDenomination(denominationMedicament);
                medicament.setFormePharmaceutique(formePharmaceutiqueMedicament);
                medicament.setVoiesAdmin(voiesAdminMedicament);
                medicament.setTitulaires(titulairesMedicament);
                medicament.setStatutAdministratif(statutAdminMedicament);
                medicament.setNbMolecule(getnbMolecules(codeCIS));

                medicamentList.add(medicament);//tte les mediacment resultat de la requete soit stocker dans une liste
            } while (cursor.moveToNext());//move to next c'est jusqua que ya plus de donnes
        } else {
            Toast.makeText(mycontext, "Aucun résultat", Toast.LENGTH_LONG).show();
        }//toast permet d afficher si ya pas de result aucun result
        cursor.close();
        db.close();
        return medicamentList;//ducoup la liste avec tt les objet medicament est retourne
    }


    private String removeAccents(String input) {
        if (input == null) {
            return null;
        }

        // Normalisation en forme de décomposition (NFD)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Remplacement des caractères diacritiques
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    private int getnbMolecules(int codeCIS){
        //veir recupere la liste des compo pour un mediacment donne
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CIS_compo_bdpm WHERE Code_CIS = ?",
                new String[]{String.valueOf(codeCIS)});
        cursor.moveToFirst();
        return cursor.getInt(0);


    }

    public List<String> getDistinctVoiesAdmin() {
        List<String> voiesAdminList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT UPPER(Voies_dadministration) FROM CIS_bdpm WHERE Voies_dadministration NOT LIKE '%;%' ORDER BY Voies_dadministration", null);
        voiesAdminList.add(PREMIERE_VOIE);
        if (cursor.moveToFirst()) {
            do {

                String voieAdmin = cursor.getString(0).toString();
                voiesAdminList.add(voieAdmin);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return voiesAdminList;
    }

}



