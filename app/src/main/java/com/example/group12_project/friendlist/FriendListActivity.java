package com.example.group12_project.friendlist;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.group12_project.MainActivity;
import com.example.group12_project.R;

import java.util.ArrayList;
import java.util.Collection;

public class FriendListActivity extends AppCompatActivity {

    ListView friendList;
    LocalUser user;
    Collection<String> friendlist;

    ArrayList<Friend> friendArrayList;

    private EditText inputFriend;

    ActionBar actionBar;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        friendList = (ListView)findViewById(R.id.friendslist);
        user = new LocalUser("user1");
        friendlist= user.getFriendList();

        friendArrayList = new ArrayList<Friend>();

        //support for toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.friend_toolbar);
        setSupportActionBar(myToolbar);
        //set up back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Pupulate array with friend object
        for(String s : friendlist){
            Friend friend = new Friend(s,100,1000);
            friendArrayList.add(friend);
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
                        userEmail = String.valueOf(inputFriend.getText());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        //TODO: use the userEmail to call addfriend
    }


}
