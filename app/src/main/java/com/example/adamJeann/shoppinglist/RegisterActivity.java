package com.example.adamJeann.shoppinglist;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RegisterLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mLastNameView;
    private EditText mFirstNameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_register);
        mFirstNameView = (AutoCompleteTextView) findViewById(R.id.firstname_register);
        mLastNameView = (AutoCompleteTextView) findViewById(R.id.lastName_register);


        if(!isOnline()){
            Toast toast = Toast.makeText(getApplicationContext(), "Please Check your internet access!", Toast.LENGTH_LONG);
            toast.show();

        }

        mPasswordView = (EditText) findViewById(R.id.password_register);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });




        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private class RegisterLoginTask extends AsyncTask<String, Void, String> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected String doInBackground(String... urls) {
            URL url;
            StringBuilder sb;
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
            /** The system calls this to perform work in the UI thread and delivers
             * the result from doInBackground() */

        private String parseJson(String result){


            if(result != null){
                JSONObject jsonResponse;
                try {

                    jsonResponse = new JSONObject(result);

                    int code = jsonResponse.getInt("code");
                    System.out.println("Code : -- " + code);

                    if(code == 0){
                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        System.out.println("Result : -- " + jsonObject);
                        String token = jsonObject.getString("token");
                        String firstName = jsonObject.getString("firstname");
                        String lastName = jsonObject.getString("lastname");
                        String email = jsonObject.getString("email");
                        System.out.println("Token : -- " + token);
                        System.out.println("FirstName : -- " + firstName);
                        System.out.println("LastName : -- " + lastName);
                        System.out.println("Email : -- " + email);

                        //set the return data of api in session variable
                        SharedPreferences sharedPreferences = getSharedPreferences("Register_pref",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("MY_TOKEN", token);
                        editor.putString("MY_FIRSTNAME", firstName);
                        editor.putString("MY_LASTNAME", lastName);
                        editor.putString("MY_EMAIL", email);

                        editor.commit();

                        result = null;

                    }else{
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
                Toast toast = Toast.makeText(getApplicationContext(), "You succefully register", Toast.LENGTH_LONG);
                toast.show();
                /*startActivity(new Intent(RegisterActivity.this, LoginActivity.class));*/

                SharedPreferences sharedPreferences = getSharedPreferences("Register_pref", MODE_PRIVATE);
                String email = sharedPreferences.getString("MY_EMAIL", null);

                String url = "http://appspaces.fr/esgi/shopping_list/account/subscribe.php?email=" + email + "&password=" + mPasswordView.getText().toString();
                url = url.replace(" ","");
                (new RegisterLoginTask()).execute(url);
            }


        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            String url = "http://appspaces.fr/esgi/shopping_list/account/subscribe.php?email=" + email + "&password=" + password + "&firstname=" + firstName + "&lastname=" + lastName;
            url = url.replace(" ","");
            (new RegisterLoginTask()).execute(url);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


}

