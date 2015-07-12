package com.lovespectre.lwin.openemr;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;

/**
 * Created by lwin on 6/20/15.
 */
public class Home extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.home, container, false);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        EditText ip = (EditText) view.findViewById(R.id.iptxt);
        ip.setText(prefs.getString("IP", null));

        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                prefs.edit().putString("IP", s.toString()).commit();
            }
        });


        Button btnNew = (Button) view.findViewById(R.id.btnNew);
        Button btnShow = (Button) view.findViewById(R.id.btnShow);
        TextView marquee = (TextView) view.findViewById(R.id.marqueeText);
        marquee.setSelected(true);

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fts;
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new NewPatient();
                fts = fragmentManager.beginTransaction();

                fts.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fts;
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new ShowAllPatient();
                fts = fragmentManager.beginTransaction();

                fts.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.flContent, fragment)
                         .addToBackStack(null)
                        .commit();

            }
        });


        return view;
    }


    public void onBackPressed() {
        if (getActivity().isTaskRoot()) {
            AlertDialogPro.Builder builder = new AlertDialogPro.Builder(getActivity());
            builder.setTitle("Exit")
                    .setIcon(R.drawable.ic_exit_to_app_black_24dp)
                    .setMessage("Are you sure to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Home.super.getActivity().onBackPressed();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialogPro alert = builder.create();
            alert.show();

        } else {
            super.getActivity().onBackPressed();
        }
    }


    public static Home newInstance() {

        Home fragment = new Home();
        return fragment;
    }
}
