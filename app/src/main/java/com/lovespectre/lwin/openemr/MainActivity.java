package com.lovespectre.lwin.openemr;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.preference.PreferenceFragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.lovespectre.lwin.custom.Preferences;
import com.lovespectre.lwin.message.Login;
import com.lovespectre.lwin.message.SignUp;

import static android.app.PendingIntent.getActivity;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    public static Context ct;
   /* public static final int SIGN_UP_ID = Menu.FIRST;
    public static final int EXIT_APP_ID = Menu.FIRST + 2;*/
    protected static final int NOT_CONNECTED_TO_SERVICE = 0;
    protected static final int FILL_BOTH_USERNAME_AND_PASSWORD = 1;
    protected static final int MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT = 2;
    protected static final int NOT_CONNECTED_TO_NETWORK = 3;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ct=getApplicationContext();


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        drawerToggle = setupDrawerToggle();
        setupDrawerContent(nvDrawer);


        mDrawer.setDrawerListener(drawerToggle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new Home();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .commit();


    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }


        });
    }

    public void selectDrawerItem(final MenuItem menuItem) {


        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fts;
        switch (menuItem.getItemId()) {

            case R.id.home:
                fts = fragmentManager.beginTransaction();
                fts.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.flContent, Home.newInstance())
                        .commit();
                //menuItem.setChecked(true);
                setTitle(menuItem.getTitle());

                break;

            case R.id.newpatient:

                fts = fragmentManager.beginTransaction();
                fts.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.flContent, NewPatient.newInstance())
                        .addToBackStack(null)
                        .commit();
                //menuItem.setChecked(true);
                setTitle(menuItem.getTitle());

                break;


            case R.id.showpatient:
                fts = fragmentManager.beginTransaction();
                fts.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.flContent, ShowAllPatient.newInstance())
                        .addToBackStack(null)
                        .commit();

                //menuItem.setChecked(true);
                setTitle(menuItem.getTitle());

                break;

            case R.id.message:
                fts = fragmentManager.beginTransaction();
                fts.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.flContent, Login.newInstance())
                        .addToBackStack(null)
                        .commit();
              /*  Intent i=new Intent(getApplicationContext(), Login.class);
                startActivity(i);*/
               //menuItem.setChecked(true);
               setTitle(menuItem.getTitle());

                break;

            case R.id.setting:
                Intent i2 = new Intent(getApplicationContext(), Preferences.class);
                startActivity(i2);



                break;

            case R.id.exit:
                onBackPressed();

            default:


                break;

        }

        mDrawer.closeDrawers();


    }




    private ActionBarDrawerToggle setupDrawerToggle() {

        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //menu.add(0, SIGN_UP_ID, 0, R.string.sign_up);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // The action bar home/up action should open or close the drawer.
     /*   switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }*/
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, Preferences.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        drawerToggle.syncState();
        super.onPostCreate(savedInstanceState);


    }
    @Override
    protected Dialog onCreateDialog(int id) {
        int message = -1;
        switch (id) {
            case NOT_CONNECTED_TO_SERVICE:
                message = R.string.not_connected_to_service;
                break;
            case FILL_BOTH_USERNAME_AND_PASSWORD:
                message = R.string.fill_both_username_and_password;
                break;
            case MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT:
                message = R.string.make_sure_username_and_password_correct;
                break;
            case NOT_CONNECTED_TO_NETWORK:
                message = R.string.not_connected_to_network;
                break;
            default:
                break;
        }

        if (message == -1) {
            return null;
        } else {
            return new AlertDialogPro.Builder(getApplicationContext())
                    .setMessage(message)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
    				/* User clicked OK so do some stuff */
                        }
                    })
                    .create();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);


    }

    public static Context getContextOfApplication(){
        return ct;
    }


}
