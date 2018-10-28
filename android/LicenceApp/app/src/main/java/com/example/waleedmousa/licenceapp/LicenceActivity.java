package com.example.waleedmousa.licenceapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 * implements LoaderCallbacks<Cursor>
 */
public class LicenceActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mUserKeyView;
    private View mProgressView;
    private View mLoginFormView;
    private final float LOCAL_VERSION =1.0f ;
    DatabaseReference activationsDatabaseReference ;

    private ArrayList<Character> allChars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);
        // Set up the  form.
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.userName);
//        populateAutoComplete();

        mUserKeyView = (EditText) findViewById(R.id.userKey);
//        mUserKeyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button mGetActivationKeyButtom = (Button) findViewById(R.id.activation_key_button);
        mGetActivationKeyButtom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        activationsDatabaseReference = database.getReference("Activations");

        setVersionListener(database);
        initAllChars(); // for the activation key generation
        checkCorrectVersion();
    }


//    public boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivity =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (connectivity == null) {
//            return false;
//        } else {
//            NetworkInfo[] info = connectivity.getAllNetworkInfo();
//            if (info != null) {
//                for (int i = 0; i < info.length; i++) {
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//    public boolean hasActiveInternetConnection(Context context) {
//
//        if (isNetworkAvailable(context)) {
//            try {
//                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
//                urlc.setRequestProperty("User-Agent", "Test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                return (urlc.getResponseCode() == 200);
//            } catch (IOException e) {
//                Log.e("network", "Error checking internet connection", e);
//            }
//        } else {
//            Log.d("network", "No network available!");
//        }
//        return false;
//    }
    private void checkCorrectVersion() {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            float globalVersion = sharedPref.getFloat(getString(R.string.global_version_key), LOCAL_VERSION);
            if (globalVersion > LOCAL_VERSION)
                createErrorAlert();

    }
    private void setVersionListener (FirebaseDatabase database){
        database.getReference("version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    Float globalVersion  = dataSnapshot.getValue(Float.class);
                    if(globalVersion != LOCAL_VERSION) {
                        writeKeyValue( getString(R.string.global_version_key) , globalVersion);
                        if(globalVersion>LOCAL_VERSION)
                            createErrorAlert();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                try {
                    Log.w("hi", "Failed to read value.", error.toException());
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    private void writeKeyValue(final String key ,final float value){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat(key, value);
                editor.commit();
            }
        });

    }
    public void initAllChars(){

        allChars = new ArrayList<>( );

        for(int i = 48 ; i < 58 ; i++) {
            allChars.add((char)(i)) ; // add numbers
        }


        for(int i = 65 ; i < 91 ; i++) {
            allChars.add((char)(i)) ; // add numbers
        }
    }
    public String getActivationKey(long serialNumber){

        Random gen = new Random(serialNumber) ;

        ArrayList<Character> shuffeledChars = (ArrayList)allChars.clone() ;
        Collections.shuffle((ArrayList)shuffeledChars,gen) ;
        String output = "";
        for (int i = 1; i < 17; i++) {
            output+=allChars.get(Math.abs(gen.nextInt())%36) ;
            if (i % 4 == 0 && i < 16)
                output += "-";
        }
        return output;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    return true;
                }
            }
        }
        return super.dispatchTouchEvent( event );
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
        mUserNameView.setError(null);
        mUserKeyView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String userKey = mUserKeyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(userKey) && !isUserKeyValid(userKey)) {
            mUserKeyView.setError(getString(R.string.error_invalid_user_key));
            focusView = mUserKeyView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isUserNameValid(userName)) {
            mUserNameView.setError(getString(R.string.error_invalid_user_name));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(userKey);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUserNameValid(String userName) {
        return true;
    }

    // check that the key is a number
    private boolean isUserKeyValid(String userKey) {
//       return userKey.matches("\\d+(?:\\.\\d+)?") ; check if its a number
        try {
            Long.parseLong(userKey , 16) ;
        }catch (NumberFormatException e) {
            return false;
        }
        return true ;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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


    public void createActivationKeyAlert (final String activationKey) {
        try {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(LicenceActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(LicenceActivity.this);
            }
            builder.setTitle("Activation Key")
                    .setMessage(activationKey)
                    .setPositiveButton("Add Key", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String userName = mUserNameView.getText().toString();
                            addActivationToDataBase(userName , activationKey);
                            // continue with delete
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

        }catch (Exception e){
            System.out.print("Hi Mother father: " + e);
        }
    }


    public void createErrorAlert () {

        final String errorMsg = "This version of the application is outdated.\n" +
                "please contact your software provider." ;
        try {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(LicenceActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(LicenceActivity.this);
            }
            builder.setTitle("Error")
                    .setMessage(errorMsg)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            moveTaskToBack(true);
                        }
                    })
                    .setIcon(android.R.drawable.stat_notify_error)
                    .setCancelable(false)
                    .show();

        }catch (Exception e){
            System.out.print("Hi Mother father: " + e);
        }
    }
    public void addActivationToDataBase(String userName , String activationKey) {
        String id = activationsDatabaseReference.push().getKey();

        Activation activation = new Activation(activationKey , userName) ;
        activationsDatabaseReference.child(id).setValue(activation) .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("fireBase" , "success data base writing") ;
//                    createActivationKeyAlert("data written");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fireBase" , "failed because of e: " + e) ;
            }
        }); ;

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

//        private final String mUserName;
        private final String mUserKey;

        UserLoginTask(String userKey) {
//            mUserName = userName;
            mUserKey = userKey;
        }

        @Override
        protected String doInBackground(Void... params) {
            return getActivationKey(Long.parseLong(mUserKey , 16)) ;
        }

        @Override
        protected void onPostExecute(final String activationKey) {
            mAuthTask = null;
            showProgress(false);

            if(activationKey != null) {
                createActivationKeyAlert(activationKey);
            }

//            if (success) {
//                finish();
//            } else {
//                mUserKeyView.setError(getString(R.string.error_incorrect_password));
//                mUserKeyView.requestFocus();
//            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

