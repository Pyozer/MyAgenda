package com.pyozer.myagenda.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.helper.MyCookieManager;
import com.pyozer.myagenda.helper.Utils;
import com.pyozer.myagenda.preferences.PrefManagerConfig;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class SignInActivity extends AppCompatActivity {

    private static final String FORM_LOGIN_URL = "https://cas.univ-lemans.fr/cas/login";

    private PrefManagerConfig prefManagerConfig;
    private MyCookieManager myCookieManager;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManagerConfig = new PrefManagerConfig(this);
        if(!prefManagerConfig.getSessionId().equals(""))
            startActivity(new Intent(SignInActivity.this, MainActivity.class));

        setContentView(R.layout.activity_signin);
        // Set up the login form.
        mUsernameView = findViewById(R.id.signin_login);

        mPasswordView = findViewById(R.id.signin_password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.signin_submit);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.signin_form);
        mProgressView = findViewById(R.id.login_progress);

        myCookieManager = MyCookieManager.getInstance();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            login(username, password);
        }
    }
    private void login(String username, String password) {
        showProgress(true);


        RequestQueue queue = Volley.newRequestQueue(this);



        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET,FORM_LOGIN_URL, response -> {

            String lt = Utils.getStringBetween(response, "<input type=\"hidden\" name=\"lt\" value=\"", "\" />");
            String _eventId = "submit";
            String submit = "LOGIN";

            StringRequest loginRequest = new StringRequest(Request.Method.POST, FORM_LOGIN_URL, (Response.Listener<String>) responseLogin -> {

                String result = "Connexion réussi !";
                boolean isSuccess = true;

                if(responseLogin.contains("<div id=\"status\" class=\"errors\">")) {
                    result = Utils.getStringBetween(responseLogin, "<div id=\"status\" class=\"errors\">", "</div>").trim();

                    isSuccess = false;
                }

                Snackbar.make(findViewById(android.R.id.content), result, Snackbar.LENGTH_LONG).show();

                if(!isSuccess) {
                    prefManagerConfig.setSessionId("");
                } else {
                    prefManagerConfig.setSessionId(myCookieManager.getCookieValue("JSESSIONID"));
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                }

                showProgress(false);

            }, error -> {
                showProgress(false);
                error.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();
                    params.put("_eventId", _eventId);
                    params.put("lt", lt);
                    params.put("submit", submit);
                    params.put("username", username);
                    params.put("password", password);
                    Log.d("PARAMS", params.toString());
                    return params;
                }
            };

            queue.add(loginRequest);

        }, error -> {
            showProgress(false);
            error.printStackTrace();
            Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.e("COOKIE", response.headers.toString());

                return super.parseNetworkResponse(response);
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
    }
}

