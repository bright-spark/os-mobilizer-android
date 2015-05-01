package com.redmart.mobilizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.IOException;

/**
 * Created by Joshua on 29/3/15.
 */
public class AuthActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private static final int RC_RECOVER_AUTH = 1;
    public static final int RC_LOGOUT = 2;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private LinearLayout mSignInLayout;
    private ProgressWheel mProgressBar;
    private SignInButton mSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Plus.API)
            .addScope(Plus.SCOPE_PLUS_LOGIN)
            .build();

        mSignInLayout = (LinearLayout) findViewById(R.id.signInLayout);
        mProgressBar = (ProgressWheel) findViewById(R.id.progressBar);
        mSignInBtn = (SignInButton) findViewById(R.id.signInBtn);

        mSignInBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        MobilizerApp.get().reset();
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                    RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        mSignInLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        if (!mIntentInProgress) {
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else if (requestCode == RC_RECOVER_AUTH) {
            String token = null;
            if (responseCode == RESULT_OK) {
                Bundle extra = intent.getExtras();
                token = extra.getString("authtoken");
            }

            authComplete(token);
        } else if (requestCode == RC_LOGOUT) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
            MobilizerApp.get().reset();
            mSignInLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String accessToken = null;

                try {
                    accessToken = GoogleAuthUtil.getToken(AuthActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient),
                        "oauth2:" + Scopes.PROFILE + " " + Scopes.PLUS_LOGIN + " " + Scopes.PLUS_MOMENTS + " " + Scopes.PLUS_ME);
                } catch (IOException transientEx) {
                    transientEx.printStackTrace();
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), RC_RECOVER_AUTH);
                } catch (GoogleAuthException authEx) {
                    authEx.printStackTrace();
                }

                return accessToken;
            }

            @Override
            protected void onPostExecute(String token) {
                authComplete(token);
            }

        };
        task.execute();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signInBtn && !mGoogleApiClient.isConnecting()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSignInLayout.setVisibility(View.GONE);

            mSignInClicked = true;
            resolveSignInError();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    private void authComplete(String token) {
        if (token != null) {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String name = currentPerson.getDisplayName();
                String image = currentPerson.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                /** Add additional logic here if you want to restrict access to subset of users **/
                MobilizerApp.get().getUser().setName(name);
                MobilizerApp.get().getUser().setEmail(email);
                MobilizerApp.get().getUser().setImageUrl(image);
                MobilizerApp.get().getUser().setAccessToken(token);

                Intent home = new Intent(AuthActivity.this, HomeActivity.class);
                startActivityForResult(home, RC_LOGOUT);
            } else {
                mSignInLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            mSignInLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
