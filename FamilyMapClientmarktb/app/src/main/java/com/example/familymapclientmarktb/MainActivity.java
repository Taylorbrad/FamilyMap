package com.example.familymapclientmarktb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Client.DataCache;
import Client.ServerProxy;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.PersonResult;
import Result.RegisterResult;


public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());



        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        LoginFragment loginFragment = new LoginFragment();

        if(fragment == null) {
            loginFragment.registerListener(this);

            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentFrameLayout, loginFragment)
                    .commit();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem settingsButton = menu.findItem(R.id.settingsMenuItem);
        MenuItem searchButton = menu.findItem(R.id.searchMenuItem);

        settingsButton.setVisible(false);
        searchButton.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu)
    {
        switch(menu.getItemId()) {
            case R.id.settingsMenuItem:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.searchMenuItem:
                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(searchIntent);
                return true;

            default:
                return super.onOptionsItemSelected(menu);
        }
    }

    @Override
    public void notifyDone() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = new MapFragment();


        fragmentManager
                .beginTransaction()
                .add(R.id.fragmentFrameLayout, mapFragment)
                .commit();

    }
}