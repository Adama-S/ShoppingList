package com.example.adamJeann.shoppinglist;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class CartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mShoppingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mShoppingListName = (EditText) findViewById(R.id.shoppingListName);

        Button mCreateButton = (Button) findViewById(R.id.shoppingListAddButton);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateShoppingList();
            }
        });
    }

    private void attemptCreateShoppingList() {
        String name = mShoppingListName.getText().toString();

        String token = "88b34e862aa58698f2a950512dcfc277";

        String url = "http://appspaces.fr/esgi/shopping_list/shopping_list/create.php?token=" + token + "&name=" + name;
        (new CreateShoppingListTask()).execute(url);
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

    private class CreateShoppingListTask extends AsyncTask<String, Void, String> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected String doInBackground(String... urls) {
            URL url;
            StringBuilder sb = null;
            String result = null;
            try {
                url = new URL(urls[0]);

                System.out.println("Urls : -- " + Arrays.toString(urls));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);

                BufferedReader br;
                conn.connect();

                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                } else {
                    br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                }

                sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);

                }
                br.close();

                result = sb.toString();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return parseJson(result);
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */

        private String parseJson(String result) {


            if (result != null) {
                JSONObject jsonResponse;
                try {

                    jsonResponse = new JSONObject(result);

                    int code = jsonResponse.getInt("code");
                    System.out.println("Code : -- " + code);

                    if (code == 0) {
                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        System.out.println("Result : -- " + jsonObject);
                        String token = jsonObject.getString("token");
                        String Name = jsonObject.getString("name");
                        System.out.println("Token : -- " + token);
                        System.out.println("Name : -- " + Name);
                        result = null;

                    } else {
                        result = jsonResponse.getString("msg");

                        System.out.println("MsgError : -- " + result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return result;
            }

            return null;
        }

        protected void onPostExecute(String result) {

            if(result != null){
                Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                toast.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "You succefully created", Toast.LENGTH_LONG);
                toast.show();
            }


        }
    }
}
