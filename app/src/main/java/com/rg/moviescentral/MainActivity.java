package com.rg.moviescentral;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    SignInButton signInButton;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    ProgressBar progressBar;
    public static final int RC_SIGN_IN = 8180;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = (SignInButton) findViewById(R.id.google_signin_button);
        signInButton.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser != null){
            startActivity(new Intent(this,Home.class));
            finish();
        }

        //change the request token id for production
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
//        System.out.println(getString(R.string.default_web_client_id) + "-----------------------");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

    }

    public void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("sign in","i am here for sign in");
                            startActivity(new Intent(MainActivity.this, Home.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            progressBar.setVisibility(View.GONE);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                Log.d("hello",result.getStatus().toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser != null){
            startActivity(new Intent(this,Home.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.google_signin_button:
                progressBar.setVisibility(View.VISIBLE);
                signIn();
                return;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void nextActivity(View v){
        startActivity(new Intent(this,Home.class));
    }
}
