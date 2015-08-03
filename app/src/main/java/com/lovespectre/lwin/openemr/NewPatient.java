package com.lovespectre.lwin.openemr;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lovespectre.lwin.custom.JsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lwin on 5/29/15.
 */
public class NewPatient extends Fragment {

    EditText firstName;
    EditText lastName;
    EditText inputCity;
    TextView txtDate;
    String sex;

    private RadioButton rd;

    JsonParser jsonParser = new JsonParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private static String url_create_patient;

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.add_patient, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String ipAddress = prefs.getString("IP", null);

        // url to create new patient
        url_create_patient = "http://" + ipAddress + "/openemr/create_patient.php";

        // Edit Text
        firstName = (EditText) view.findViewById(R.id.firstName);
        lastName = (EditText) view.findViewById(R.id.lastName);
        inputCity = (EditText) view.findViewById(R.id.inputCity);

        final ImageButton btncalender = (ImageButton) view.findViewById(R.id.calender);
        final RadioGroup rdsex = (RadioGroup) view.findViewById(R.id.rdsex);
        final RadioButton rdmale = (RadioButton) view.findViewById(R.id.radio_male);
        final RadioButton rdfemale = (RadioButton) view.findViewById(R.id.radio_female);
        final Button btnCreate = (Button) view.findViewById(R.id.btnCreate);

      
        getActivity().setTitle("New Patient");
        btncalender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog picker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {


                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtDate = (TextView) getActivity().findViewById(R.id.showdate);
                        txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
                picker.show();
            }
        });

        rdsex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radio_male) {
                    sex = "Male";
                    Toast.makeText(getActivity(), "You selected " + sex, Toast.LENGTH_SHORT).show();
                } else {
                    sex = "Female";
                    Toast.makeText(getActivity(), "You selected " + sex, Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

             /*   ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {*/


                    // fetch data
                    // creating new patient in background thread
                    new CreateNewPatient().execute();

               /* } else {
                    // display error
                    Toast.makeText(getActivity(), "No host connection found", Toast.LENGTH_SHORT).show();
                }*/


            }
        });

        return view;
    }


    // Background Async Task to Create new Patient

    class CreateNewPatient extends AsyncTask<String, String, String> {


        //Before starting background thread Show Progress Dialog

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Creating Patient..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        //Creating patient

        protected String doInBackground(String... args) {
            String fname = firstName.getText().toString();
            String lname = lastName.getText().toString();
            String city = inputCity.getText().toString();
            String dob = txtDate.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fname", fname));
            params.add(new BasicNameValuePair("lname", lname));
            params.add(new BasicNameValuePair("DOB", dob));
            params.add(new BasicNameValuePair("sex", sex));
            params.add(new BasicNameValuePair("city", city));
            // getting JSON Object
            // Note that create product url accepts POST method

            //  Log.i("Create Response", url_create_patient + fname + lname + city + dob + sex);
            JSONObject json = jsonParser.makeHttpRequest(url_create_patient, "POST", params);


            // check log cat fro response
            Log.i("Create Response", json.toString());

            // check for success tag
            try {


                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    // successfully created patient
                    FragmentManager fragmentManager = getFragmentManager();
                    Fragment fragment = new ShowAllPatient();
                    fragmentManager.beginTransaction()
                            .replace(R.id.flContent, fragment)
                            .addToBackStack(null)
                            .commit();

                    // closing this screen
                    // finish();
                } else {
                    // finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        //After completing background task Dismiss the progress dialog

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }

    public static NewPatient newInstance() {

        NewPatient newpatient = new NewPatient();
        return newpatient;
    }


   /* @Override
    public void onBackPressed() {
       super.onBackPressed();

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }*/
}
