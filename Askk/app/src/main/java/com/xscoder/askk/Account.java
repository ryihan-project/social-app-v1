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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mb3364.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.amirs.JSON;

import static com.xscoder.askk.XServerSDK.ANSWERS_ANSWER;
import static com.xscoder.askk.XServerSDK.ANSWERS_CREATED_AT;
import static com.xscoder.askk.XServerSDK.ANSWERS_IS_ANONYMOUS;
import static com.xscoder.askk.XServerSDK.ANSWERS_QUESTION_POINTER;
import static com.xscoder.askk.XServerSDK.ANSWERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.ANSWERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.ANSWERS_USER_POINTER;
import static com.xscoder.askk.XServerSDK.USERS_EDUCATION;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_LOCATION;
import static com.xscoder.askk.XServerSDK.USERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.roundLargeNumber;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class Account extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

   // VIEWS //
   Button backButton, questionsButton, answersButton, editProfileButton, reportUserButton, logoutButton;
   CircleImageView avatarImg;
   TextView fullnameTxt, usernameTxt, locationTxt, educationTxt;
   ListView QAListView;
   SwipeRefreshLayout refreshControl;
   RelativeLayout tabBar;


   // VARIABLES //
   Context ctx = this;
   JSON currentUser;
   boolean isCurrentUser = true;
   boolean showBackbutton = false;
   boolean isQuestions = true;
   JSON userObj;
   List<JSON>QAArray = new ArrayList<>();





   //-----------------------------------------------
   // MARK - ON START
   //-----------------------------------------------
   @Override
   protected void onStart() {
      super.onStart();

      // Get Current User
      XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
         // Current User IS LOGGED IN!
         if (currUser != null) {
            currentUser = currUser;

            // Call function
            if (isCurrentUser) { showUserDetails(currentUser);
            } else { showUserDetails(userObj); }

            Log.i(TAG, "MY_VARIABLE: " + isCurrentUser);

            if (mustReload){
               mustReload = false;

               // Call query
               callQuery();
            }

         // Current User IS LOGGED OUT
         } else { startActivity(new Intent(ctx, Intro.class)); }
      }}); // ./ XSCurrentUser

   }

      //-----------------------------------------------
      // MARK - TAP CELL -> SEE QUESTIONS OR ANSWERS
      //-----------------------------------------------
      QAListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            if (isQuestions) {
               // Obj
               JSON qObj = QAArray.get(position);

               Intent i = new Intent(ctx, QuestionScreen.class);
               Bundle extras = new Bundle();
               extras.putString("object", String.valueOf(qObj));
               i.putExtras(extras);
               startActivity(i);

            } else {
               // Obj
               JSON aObj = QAArray.get(position);

               // Get Pointer
               XSGetPointer((Activity)ctx, aObj.key(ANSWERS_QUESTION_POINTER).stringValue(), QUESTIONS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                  @Override public void done(final JSON questionPointer, String e) {
                     if (questionPointer != null) {

                        Intent i = new Intent(ctx, QuestionScreen.class);
                        Bundle extras = new Bundle();
                        extras.putString("object", String.valueOf(questionPointer));
                        i.putExtras(extras);
                        startActivity(i);

                     // error
                     } else { simpleAlert(e, ctx);
               }}}); // ./ XSGetPointer

            }// :/ If

      }});
   }







   //-----------------------------------------------
   // MARK - REFRESH DATA
   //-----------------------------------------------
   @Override
   public void onRefresh() {
      callQuery();
      if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
   }


}// ./ end
