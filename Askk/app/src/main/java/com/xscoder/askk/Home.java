package com.xscoder.askk;

/**

 Askk [XServer]

 © XScoder 2020
 All Rights reserved

 * IMPORTANT *
 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE
 IS A SERIOUS COPYRIGHT INFRINGEMENT, AND YOU WILL BE
 LEGALLY PROSECUTED

 **/

import android.actionsheet.demo.com.khoiron.actionsheetiosforandroid.ActionSheet;
import android.actionsheet.demo.com.khoiron.actionsheetiosforandroid.Interface.ActionSheetCallBack;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mb3364.http.RequestParams;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.amirs.JSON;
import static com.xscoder.askk.XServerSDK.ANDROID_DEVICE_TOKEN;
import static com.xscoder.askk.XServerSDK.GRAY;
import static com.xscoder.askk.XServerSDK.MAIN_COLOR;
import static com.xscoder.askk.XServerSDK.MULTIPLE_PERMISSIONS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_ANSWERS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_CREATED_AT;
import static com.xscoder.askk.XServerSDK.QUESTIONS_IS_ANONYMOUS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_QUESTION;
import static com.xscoder.askk.XServerSDK.QUESTIONS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.QUESTIONS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.QUESTIONS_USER_POINTER;
import static com.xscoder.askk.XServerSDK.QUESTIONS_VIEWS;
import static com.xscoder.askk.XServerSDK.TAG;
import static com.xscoder.askk.XServerSDK.USERS_AVATAR;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.XSCurrentUser;
import static com.xscoder.askk.XServerSDK.XSGetArrayFromJSONArray;
import static com.xscoder.askk.XServerSDK.XSGetDateFromString;
import static com.xscoder.askk.XServerSDK.XSGetPointer;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSQuery;
import static com.xscoder.askk.XServerSDK.XSRemoveDuplicatesFromArray;
import static com.xscoder.askk.XServerSDK.categoriesArray;
import static com.xscoder.askk.XServerSDK.colorsArray;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.permissions;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.roundLargeNumber;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class Home extends AppCompatActivity {
    // VARIABLES //
    Context ctx = this;
    JSON currentUser;
    List<JSON>questionsArray = new ArrayList<>();
    String selectedCategory = categoriesArray[0];
    String searchTxt = "";

                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        ANDROID_DEVICE_TOKEN = Objects.requireNonNull(task.getResult()).getToken();
                        Log.i(TAG, "DEVICE TOKEN: " + ANDROID_DEVICE_TOKEN);

                        // Register the device Token - for Push Notifications
                        RequestParams params = new RequestParams();
                        params.put("tableName", "Users");
                        params.put("ID_id", currentUser.key("ID_id").stringValue());
                        params.put("ST_androidDeviceToken", ANDROID_DEVICE_TOKEN);
                        // Reset app icon badge to 0
                        params.put("NU_badge", "0");
                        XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                            @Override
                            public void done(String e, JSON obj) {
                                if (e == null) { Log.i(TAG, "DEVICE TOKEN REGISTERED");
                        }}});
                }});
            } //./ If

            if (mustReload){
                mustReload = false;
                queryQuestions();
            }
        }}); // ./ XSCurrentUser



        // Check Permissions
        if (!checkPermissions()) { checkPermissions(); }

    }






    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        searchEditText = findViewById(R.id.hSearchEditText);
        searchEditText.setTypeface(popRegular);
        categoryTxt = findViewById(R.id.hCategoryTxt);
        categoryTxt.setTypeface(popBold);
        questionsListView = findViewById(R.id.hQuestionsListView);
        postQuestionButton = findViewById(R.id.hPostQuestionButton);


        //-----------------------------------------------
        // MARK - TAB BAR BUTTONS
        //-----------------------------------------------
        Button tab1 = findViewById(R.id.tab2);
        Button tab2 = findViewById(R.id.tab3);

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, NotificationsScreen.class));
        }});

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, Account.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isCurrentUser", true);
                extras.putBoolean("showBackButton", false);
                i.putExtras(extras);
                startActivity(i);
        }});



        //-----------------------------------------------
        // MARK - SEARCH BY KEYWORDS
        //-----------------------------------------------
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // Call query
                    if (!searchEditText.getText().toString().matches("")) {
                        searchTxt = searchEditText.getText().toString();
                        queryQuestions();
                        dismissKeyboard();
                    } else { simpleAlert("Please type something.", ctx); }

                    return true;
                } return false;
        }});


        // Default Category
        categoryTxt.setText(categoriesArray[0].toUpperCase());


        // Call function
        setupCategoriesScrollView();



        // Get Current User
        XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
            // Current User IS LOGGED IN!
            if (currUser != null) { currentUser = currUser; }

            // Call query
            queryQuestions();
        }}); // ./ XSCurrentUser




        //-----------------------------------------------
        // MARK - POST QUESTION BUTTON
        //-----------------------------------------------
    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "ALL PERMISSIONS GRANTED!");
            }
        }
    }



} // ./ end
