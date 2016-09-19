package com.pyozer.myagenda;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HttpRequest {
    private MainActivity mainActivity = null;
    private UpdateActivity updateActivity = null;

    SharedPreferences preferences;

    public HttpRequest(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
    }

    public HttpRequest(UpdateActivity updateActivity) {
        this.updateActivity = updateActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(updateActivity);
    }

    public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls);
            } catch (IOException e) {
                if(mainActivity != null) {
                    doInBackgroundMainActivityError(urls);
                } else if(updateActivity != null) {
                    return updateActivity.getString(R.string.no_connexion_github);
                }
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            if (updateActivity != null) {
                updateActivity.checkUpdate.setEnabled(false);
            } else if(mainActivity != null) {

            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(mainActivity != null) {
                onExecuteMainActivity(result);
            } else if(updateActivity != null) {
                onExecuteUpdateActivity(result);
            }
        }
    }

    /**
     * On effectue la requête et récupère le contenu
     * @param params
     * @return
     * @throws IOException
     */
    private String downloadUrl(String... params) throws IOException {
        InputStream is = null;
        // Défini le max de caractère à récupérer
        //int len = 100000;
        String ReadTimeout = params[1];
        String ConnectTimeout = params[2];

        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Integer.parseInt(ReadTimeout));
            conn.setConnectTimeout(Integer.parseInt(ConnectTimeout));
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Début requete
            conn.connect();
            //int response = conn.getResponseCode();
            is = conn.getInputStream();

            String contentAsString = convertStreamToString(is);
            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Convertion du InputStream en String.
     * @param is
     * @return
     * @throws IOException
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * On prépare les url pour la requete
     */
    protected void prepareUrlRequest() {

        String groupe = preferences.getString("groupe", "A");
        String groupeTD = preferences.getString("groupeTD", "B");
        String nbWeeks = preferences.getString("nbWeeks", "1");

        // On fait la requete
        String readTimeOut = "8000";
        String connectTimeout = "8000";

        String url = "http://interminale.fr.nf/MyAgenda/get_json.php?grp=" + groupe + "&grpTD=" + groupeTD + "&nbWeeks=" + nbWeeks;

        url += "&display=group";

        new DownloadWebpageTask().execute(url, readTimeOut, connectTimeout);
    }

    /**
     * ERREUR GESTION AVANT LA REQUETE
     * @param urls
     * @return
     */
    public void doInBackgroundMainActivityError(String... urls) {
        String erreur = mainActivity.getString(R.string.no_connexion_client);

        Snackbar.make(mainActivity.mainactivity_layout, erreur, Snackbar.LENGTH_LONG).show();
    }

    /**
     * GESTION APRES REQUETE FINI
     * @param jsonStr
     */
    public void onExecuteMainActivity(String jsonStr) {
        /* A la fin de la requete on retire le loader */
        mainActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        //Log.e("REQUETE", jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray all_agenda = jsonObj.getJSONArray("android");

                for (int i = 0; i < all_agenda.length(); i++) {
                    if(i == 0) {
                        JSONObject cInit = all_agenda.getJSONObject(0);
                    } else {
                        JSONObject cInit = all_agenda.getJSONObject(i - 1);
                    }
                    String PREVIOUSDAY = cInit.getString("DAY");
                    
                    JSONObject c = all_agenda.getJSONObject(i);
                    String DAY = c.getString("DAY");
                    String DATE = c.getString("DATE");
                    String SUMMARY = c.getString("SUMMARY");
                    String LOCATION = c.getString("LOCATION");
                    String DESCRIPTION = c.getString("DESCRIPTION");

                    HashMap<String, String> agenda = new HashMap<>();

                    if(PREVIOUSDAY != DAY) {
                        agenda.put("DATE", DAY);
                        agenda.put("SUMMARY", "");
                        agenda.put("LOCATION", "");.
                        
                        i--;
                    } else {
                        agenda.put("DATE", DATE);
                        agenda.put("SUMMARY", SUMMARY);
                        agenda.put("LOCATION", LOCATION + " - " + DESCRIPTION);
                    }
                    mainActivity.agendaList.add(agenda);

                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            mainActivity, mainActivity.agendaList,
                            R.layout.list_item, new String[]{"SUMMARY", "LOCATION",
                            "DATE"}, new int[]{R.id.summary,
                            R.id.location, R.id.dtstart});

                    mainActivity.listView.setAdapter(adapter);

                }
            } catch (final JSONException e) {
                Log.e("JSON", "Json parsing error: " + e.getMessage());

            }
        }
    }

    public void onExecuteUpdateActivity(String result) {
        // On enlève la dialog de chargement
        updateActivity.progressDialog.dismiss();
        // On affiche le dialog pour donner le résultat du check
        updateActivity.showAlertDialog(result);
        // On réactive le bouton pour check les majs
        updateActivity.checkUpdate.setEnabled(true);
    }
}
