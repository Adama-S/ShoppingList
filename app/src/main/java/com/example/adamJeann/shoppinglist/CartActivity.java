package com.example.adamJeann.shoppinglist;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import Util.MyAsyncTask;
import Util.Urls;
import models.ShoppingList;

import static android.R.string.cancel;

public class CartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SharedPreferences sharedPreferences = null;
    private EditText mShoppingListName;
    private String token;
    private ShoppingList shoppingList;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        final Intent intent = getIntent();

        mShoppingListName = (EditText) findViewById(R.id.shoppingListName);
        Button mActionButton = (Button) findViewById(R.id.shoppingListButton);

        if(intent != null) {
            shoppingList = (ShoppingList) intent.getSerializableExtra("ShoppingList");
            System.out.println(shoppingList);
            mActionButton.setText("Update");
            mShoppingListName.setText(shoppingList.name);
        }else{
            mActionButton.setText("Creation");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreference", MODE_PRIVATE);
        token = sharedPreferences.getString("tokenUser", null);

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intent != null) {
                    attemptUpdateShoppingList(shoppingList);
                }else {
                    attemptCreateShoppingList();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void attemptCreateShoppingList() {

        final MyAsyncTask asyncTask = new MyAsyncTask();

        String url;

        mShoppingListName.setError(null);

        boolean cancel = false;
        View focusView = null;

        String name = mShoppingListName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mShoppingListName.setError(getString(R.string.error_field_required));
            focusView = mShoppingListName;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            url = Urls.WS_CREATE_SHOPPINGLIST_URL + "?token=" + token + "&name=" + name;
            url = url.replace(" ","");
            asyncTask.execute(url);


            asyncTask.setListener(new IRequestListener() {

                @Override
                public void onSuccess(JSONObject object) {
                    sharedPreferences = getSharedPreferences("mySharedPreference", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String token;
                    String name;
                    String msg;


                    mShoppingListName.setError(null);
                    View focusView;

                    try {

                        String codeTxt = object.getString("code");
                        int code = Integer.parseInt(codeTxt);

                        if(code == 0){
                            JSONObject resultObject = object.getJSONObject("result");
                            name = resultObject.getString("name");
                            editor.putString("Name",name);
                            editor.commit();

                            Toast toast = Toast.makeText(getApplicationContext(), "You successfully create shopping List", Toast.LENGTH_LONG);
                            toast.show();

                            startActivity(new Intent(CartActivity.this, HomeActivity.class));

                        }else{

                            msg = object.getString("msg");

                            mShoppingListName.setError(getString(R.string.error_invalid_password));
                            focusView = mShoppingListName;
                            focusView.requestFocus();

                            Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                            toast.show();
                        }


                    } catch (JSONException e) {
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

    private void attemptUpdateShoppingList(ShoppingList shoppingList) {

        final MyAsyncTask asyncTask = new MyAsyncTask();

        String url;

        mShoppingListName.setError(null);

        boolean cancel = false;
        View focusView = null;

        String name = mShoppingListName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mShoppingListName.setError(getString(R.string.error_field_required));
            focusView = mShoppingListName;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            url = Urls.WS_EDIT_SHOPPINGLIST_URL + "?token=" + token + "&id=" + shoppingList.id + "&name=" + name;
            url = url.replace(" ","");
            asyncTask.execute(url);


            asyncTask.setListener(new IRequestListener() {

                @Override
                public void onSuccess(JSONObject object) {
                    sharedPreferences = getSharedPreferences("mySharedPreference", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String name;
                    String msg;


                    mShoppingListName.setError(null);
                    View focusView;

                    try {

                        String codeTxt = object.getString("code");
                        int code = Integer.parseInt(codeTxt);

                        if(code == 0){

                            Toast toast = Toast.makeText(getApplicationContext(), "You successfully update shopping List", Toast.LENGTH_LONG);
                            toast.show();

                            startActivity(new Intent(CartActivity.this, HomeActivity.class));

                        }else{

                            msg = object.getString("msg");

                            mShoppingListName.setError(getString(R.string.error_invalid_password));
                            focusView = mShoppingListName;
                            focusView.requestFocus();

                            Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                            toast.show();
                        }


                    } catch (JSONException e) {
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Cart Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class CreateShoppingListTask {

    }
}
