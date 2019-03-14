package com.example.group12_project.friendlist;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.group12_project.MainActivity;
import com.example.group12_project.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FriendListActivity extends AppCompatActivity {

    ListView friendList;
    LocalUser user;
    UserCloud cloud;
    UserCloudMediator userCloudMediator;

    private Map<String, Object> friendlist;

    ArrayList<SelfData> friendArrayList;

    private EditText inputFriend;

    ActionBar actionBar;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        friendList = (ListView)findViewById(R.id.friendslist);

        user = LocalUser.getLocalUser();
        cloud = UserCloud.getUserCloud();
        userCloudMediator = new UserCloudMediator(user, cloud);
        user.register(userCloudMediator);
        cloud.register(userCloudMediator);

        cloud.updateRequest();
        cloud.updateFriends();

        friendlist= user.getFriendList();


        friendArrayList = new ArrayList<>();

        //support for toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.friend_toolbar);
        setSupportActionBar(myToolbar);
        //set up back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Load list with current friend list
        for(Object data : friendlist.values()){
//            SelfData data = (SelfData)friendlist.get(s);
//            long goal = (long)data.getGoal().get("goal");
//
//            Date date = Calendar.getInstance().getTime();
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//            String strDate = dateFormat.format(date);
//
//            long currentStep = (long)data.getDaily_steps().get(strDate);
//            Friend friend = new Friend(s,goal,currentStep);

            Map dataMap = (Map) data;
            String id = (String)dataMap.get("id");
            long goalNum = Long.parseLong((String)dataMap.get("goal"));
            Map<String, Object> daily_steps = (Map)dataMap.get("daily_steps");
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String strDate = dateFormat.format(date);
            long stepNum = (long)daily_steps.get(strDate);


            SelfData friendData = new SelfData(id, goalNum, stepNum);
            friendArrayList.add(friendData);
        }


        FriendListViewAdapter friendListViewAdapter = new FriendListViewAdapter(this, R.layout.friendslist_adapter_layout, friendArrayList);
        friendList.setAdapter(friendListViewAdapter);

        FloatingActionButton addFriendButton = (FloatingActionButton)findViewById(R.id.add_friend);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFriendDialog(FriendListActivity.this);
            }
        });

    }

    private void showAddFriendDialog(Context context){
        inputFriend = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Add Friend")
                .setMessage("Input friend's google email address here")
                .setView(inputFriend)
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        cloud.updateRequest();

                        userEmail = String.valueOf(inputFriend.getText());
                        user.addFriend(userEmail);
//                        if(user.addFriend(userEmail)){
//                            SelfData data = (SelfData) friendlist.get(userEmail);
//                            long goal = (long) (data.getGoal().get("goal"));
//                            Date date = Calendar.getInstance().getTime();
//                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                            String strDate = dateFormat.format(date);
//
////                            long currentStep = (long) data.getDaily_steps().get(strDate);
//                            Friend friend = (Friend)friendlist.get(userEmail);
//                            friendArrayList.add(friend);
////                            friendArrayList.add(new Friend(userEmail,goal,currentStep));
//                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();


    }
}
