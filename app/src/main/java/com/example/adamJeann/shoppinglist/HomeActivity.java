package com.example.adamJeann.shoppinglist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Util.MyAsyncTask;
import Util.Urls;
import models.ShoppingList;

import static Util.Urls.WS_LIST_SHOPPINGLIST_URL;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String token;
    SharedPreferences sharedPreferences = null;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreference", MODE_PRIVATE);
        token = sharedPreferences.getString("tokenUser", null);

        listView = (ListView) findViewById(R.id.listCard);
        attemptGetShoppingList();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences myPrefs = getSharedPreferences("mySharedPreference", 0);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.clear();
                    editor.commit();

                    dialog.dismiss();

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();  // This call is missing.
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void attemptGetShoppingList() {

        final MyAsyncTask asyncTask = new MyAsyncTask();

        String url;

        url = WS_LIST_SHOPPINGLIST_URL + "?token=" + token;

        asyncTask.execute(url);

        asyncTask.setListener(new IRequestListener() {

            @Override
            public void onSuccess(JSONObject object) {
                sharedPreferences = getSharedPreferences("mySharedPreference", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String name;
                String createdDate;
                Boolean completed;


                try {

                    String codeTxt = object.getString("code");
                    int code = Integer.parseInt(codeTxt);

                    if(code == 0){
                        JSONArray shoppingListArray = object.getJSONArray("result");
                        System.out.println((shoppingListArray));
                        System.out.println((shoppingListArray.length()));

                        ArrayList<ShoppingList> cards = new ArrayList<ShoppingList>();
                        for(int i=0; i < shoppingListArray.length() ; i++) {
                            JSONObject cardObject;
                            cardObject = shoppingListArray.getJSONObject(i);
                            int id = cardObject.getInt("id");
                            name = cardObject.getString("name");
                            createdDate = cardObject.getString("created_date");

                            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                            Date date = null;

                            try {
                                date = sdf.parse(createdDate);
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }*/

                            ShoppingList sl = new ShoppingList(name, createdDate);
                            cards.add(sl);
                        }
                        System.out.println(cards);
                        ArrayAdapter<ShoppingList  > mArrayAdapter = new ArrayAdapter<ShoppingList>(HomeActivity.this,
                                android.R.layout.simple_list_item_1, cards);
                        listView.setAdapter(mArrayAdapter);


                    }


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {
                Log.e("Error","test error onFailed");
            }
        });

    }
}
