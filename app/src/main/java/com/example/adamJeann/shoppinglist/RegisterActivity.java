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


public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    SharedPreferences sharedPreferences = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mLastNameView;
    private EditText mFirstNameView;
    private View mProgressView;
    private View mRegisterFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email_register);
        mFirstNameView = (AutoCompleteTextView) findViewById(R.id.firstName_register);
        mLastNameView = (AutoCompleteTextView) findViewById(R.id.lastName_register);


        if(!isOnline()){
            Toast toast = Toast.makeText(getApplicationContext(), "Please Check your internet access!", Toast.LENGTH_LONG);
            toast.show();

        }

        mPasswordView = (EditText) findViewById(R.id.password_register);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        Button btnLogin = (Button) findViewById(R.id.switchLogin);

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
        mFirstNameView.setError(null);

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

        // Check for a valid firstName.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            url = Urls.WS_SUBSCRIBE_URL+"?email="+email+"&password="+password+"&firstname="+firstName+"&lastname="+lastName;
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

                            Toast toast = Toast.makeText(getApplicationContext(), "You successfully register", Toast.LENGTH_LONG);
                            toast.show();

                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));

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
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
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

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

