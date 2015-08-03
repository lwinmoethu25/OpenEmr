package com.lovespectre.lwin.openemr;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.lovespectre.lwin.custom.JsonParser;
import com.lovespectre.lwin.custom.MyBaseAdapter;
import com.lovespectre.lwin.custom.ShowItem;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lwin on 5/29/15.
 */
public class ShowAllPatient extends Fragment {


    //Progress Dialog
    private ProgressDialog pDialog;

    //Creating JSON Parser object
    JsonParser jParser = new JsonParser();

    //url to get all patient list
    private static String url_all_patient;


    //JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PATIENT = "patient_data";
    private static final String TAG_PID = "id";
    private static final String TAG_FNAME = "fname";
    private static final String TAG_LNAME = "lname";
    private static final String TAG_CITY = "city";

    //patient JSONArray
    JSONArray patient = null;

    EditText inputSearch;

    ListView listView;

    ArrayList<ShowItem> myList = new ArrayList<ShowItem>();
    View view;

    ArrayList<HashMap<String, String>> patientList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.all_patient, container, false);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String ipAddress = prefs.getString("IP", null);

        url_all_patient = "http://" + ipAddress + "/openemr/get_all_patient.php";

        getActivity().setTitle("Show Patient");


       /* ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {*/


            // fetch data
            //Loading patients in Background Thread
            new LoadAllPatient().execute();


       /* } else {
            // display error
            Toast.makeText(getActivity(), "No host connection found", Toast.LENGTH_SHORT).show();
        }*/


        ListView lv = (ListView) view.findViewById(R.id.list);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getActivity(), UpdatePatient.class);

                // sending pid to next activity
                in.putExtra(TAG_PID, pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);

            }
        });


        return view;

    }


    // Response from UpdatePatient Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted patient
           // reload this screen again



           FragmentManager fragmentManager = getFragmentManager();
            Fragment fragment = new Home();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContent,fragment)
                    .commit();

            Toast.makeText(getActivity(),"The record have been updated",Toast.LENGTH_SHORT).show();

                  }

    }

    public static Fragment newInstance() {

        ShowAllPatient showAllPatient = new ShowAllPatient();
        return showAllPatient;
    }


    class LoadAllPatient extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading patients. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        // getting All patients from url
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_patient, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Patients: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    patient = json.getJSONArray(TAG_PATIENT);

                    // looping through All Products
                    for (int i = 0; i < patient.length(); i++) {
                        JSONObject c = patient.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String fname = c.getString(TAG_FNAME);
                        String lname = c.getString(TAG_LNAME);
                        String city = c.getString(TAG_CITY);
                        Log.i("List Item:" + fname, lname);

                        ShowItem item = new ShowItem();
                        item.setId(id);
                        item.setFname(fname);
                        item.setLname(lname);
                        item.setCity(city);
                        myList.add(item);


                    }
                } else {
                    // no patient found
                    // Launch Add New patient Activity
                   /* Intent i = new Intent(getActivity(),
                            NewPatient.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new NewPatient();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        //After completing background task Dismiss the progress dialog

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all patients
            final Context context = getActivity();
            try {
                // updating UI from Background Thread
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        listView = (ListView) view.findViewById(R.id.list);

                        listView.setAdapter(new MyBaseAdapter(context, myList));


                        inputSearch = (EditText) view.findViewById(R.id.search);
                        inputSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                ArrayList<ShowItem> tempList = new ArrayList<ShowItem>();
                                String searchString = inputSearch.getText().toString();

                                if (searchString.length() > 0) {
                                    for (int i = 0; i < myList.size(); i++) {
                                        String strfname = myList.get(i).getFname();
                                        String strlname = myList.get(i).getLname();
                                        String strcity = myList.get(i).getCity();
                                        if ((searchString.equalsIgnoreCase(strfname) || searchString.equalsIgnoreCase(strlname) || searchString.equalsIgnoreCase(strcity))) {
                                            tempList.add(myList.get(i));
                                        }
                                    }
                                    listView.setAdapter(new MyBaseAdapter(context, tempList));
                                } else {
                                    listView.setAdapter(new MyBaseAdapter(context, myList));
                                }

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });


                        pDialog.dismiss();

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        }



    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_patient);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String ipAddress = prefs.getString("IP", null);

        url_all_patient = "http://" + ipAddress + "/openemr/get_all_patient.php";

*//*
          if (isNetworkAvailable(context)) {
              // available network

          } else {
              // no network
              Toast.makeText(this,"No Network Available",Toast.LENGTH_SHORT).show();
              finish();
          }*//*


        //Loading patients in Background Thread
        new LoadAllPatient().execute();

        ListView lv = (ListView) findViewById(R.id.list);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), UpdatePatient.class);

                // sending pid to next activity
                in.putExtra(TAG_PID, pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);

            }
        });


    }

    // Response from UpdatePatient Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted patient
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }


    class LoadAllPatient extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowAllPatient.this);
            pDialog.setMessage("Loading patients. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        *//**
     * getting All patients from url
     *//*
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_patient, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Patients: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    patient = json.getJSONArray(TAG_PATIENT);

                    // looping through All Products
                    for (int i = 0; i < patient.length(); i++) {
                        JSONObject c = patient.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String fname = c.getString(TAG_FNAME);
                        String lname = c.getString(TAG_LNAME);
                        String city = c.getString(TAG_CITY);
                        Log.i("List Item:" + fname, lname);

                        ShowItem item = new ShowItem();
                        item.setId(id);
                        item.setFname(fname);
                        item.setLname(lname);
                        item.setCity(city);
                        myList.add(item);


                    }
                } else {
                    // no patient found
                    // Launch Add New patient Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewPatient.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        */

    /**
     * After completing background task Dismiss the progress dialog
     * *
     *//*
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all patients

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    listView = (ListView) findViewById(R.id.list);

                    listView.setAdapter(new MyBaseAdapter(context, myList));

                    inputSearch = (EditText) findViewById(R.id.search);
                    inputSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            ArrayList<ShowItem> tempList = new ArrayList<ShowItem>();
                            String searchString = inputSearch.getText().toString();

                            if (searchString.length() > 0) {
                                for (int i = 0; i < myList.size(); i++) {
                                    String strfname = myList.get(i).getFname();
                                    String strlname = myList.get(i).getLname();
                                    String strcity = myList.get(i).getCity();
                                    if ((searchString.equalsIgnoreCase(strfname) || searchString.equalsIgnoreCase(strlname) || searchString.equalsIgnoreCase(strcity))) {
                                        tempList.add(myList.get(i));
                                    }
                                }
                                listView.setAdapter(new MyBaseAdapter(context, tempList));
                            } else {
                                listView.setAdapter(new MyBaseAdapter(context, myList));
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });



                    pDialog.dismiss();

                }
            });

        }

    }
*/
    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }


}
