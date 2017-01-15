package com.example.adamJeann.shoppinglist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import Util.MyAsyncTask;
import Util.Urls;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    SharedPreferences sharedPreferences = null;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    //private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_login);

        mPasswordView = (EditText) findViewById(R.id.password_login);

        //check if the phone is connected to network
        if(!isOnline()){
            Toast toast = Toast.makeText(getApplicationContext(), "Please Check your internet access!", Toast.LENGTH_LONG);
            toast.show();
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button btnRegister = (Button) findViewById(R.id.switchRegister);

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreference", MODE_PRIVATE);
        String token = sharedPreferences.getString("tokenUser", null);

        //check if the user is previously logged and redirect it to the home activity
        if(token != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }

    }





    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        final MyAsyncTask asyncTask = new MyAsyncTask();

        String url;

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            url = Urls.WS_CONNECT_URL+"?email="+email+"&password="+password;
            url = url.replace(" ","");
            asyncTask.execute(url);


            asyncTask.setListener(new IRequestListener() {

                @Override
                public void onSuccess(JSONObject object) {
                    sharedPreferences = getSharedPreferences("mySharedPreference", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String firstName;
                    String lastName;
                    String email;
                    String token;
                    String msg;


                    mEmailView.setError(null);
                    mPasswordView.setError(null);
                    View focusView;
                    View focusView2;

                    try {


                        String codeTxt = object.getString("code");
                        int code = Integer.parseInt(codeTxt);


                        if(code == 0){
                            JSONObject resultObject = object.getJSONObject("result");
                            firstName = resultObject.getString("firstname");
                            lastName = resultObject.getString("lastname");
                            email = resultObject.getString("email");
                            token = resultObject.getString("token");
                            System.out.println("email : " + email);
                            editor.putString("firstName",firstName);
                            editor.putString("lastName",lastName);
                            editor.putString("email",email);
                            editor.putString("tokenUser",token);
                            editor.commit();

                            showProgress(true);

                            Toast toast = Toast.makeText(getApplicationContext(), "You successfully logged", Toast.LENGTH_LONG);
                            toast.show();

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        }else{

                            msg = object.getString("msg");

                            mPasswordView.setError(getString(R.string.error_invalid_password));
                            focusView = mPasswordView;
                            mEmailView.setError(getString(R.string.error_invalid_email));
                            focusView2 = mEmailView;
                            focusView.requestFocus();
                            focusView2.requestFocus();
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}

