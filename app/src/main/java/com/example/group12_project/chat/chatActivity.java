package com.example.group12_project.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group12_project.MainActivity;
import com.example.group12_project.NotificationBuilder;
import com.example.group12_project.R;
import com.example.group12_project.friendlist.FriendDataDisplay;
import com.example.group12_project.friendlist.FriendListActivity;
import com.example.group12_project.friendlist.LocalUser;
import com.example.group12_project.friendlist.UserCloud;
import com.example.group12_project.friendlist.UserCloudMediator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chatActivity extends AppCompatActivity {

    String TAG = chatActivity.class.getSimpleName();

    String MESSAGES_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;
    CollectionReference opponentChat;
    String from;

    Button send_button;


    LocalUser localUser;
    UserCloud userCloud;
    UserCloudMediator userCloudMediator;

    String opponentId;

    FloatingActionButton checkActivityBtn;

    private final String Tag = "user id is ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        checkActivityBtn = findViewById(R.id.check_data);


        opponentId = getIntent().getExtras().getString("userid");

        localUser = LocalUser.getLocalUser();
        Log.d(Tag, "user id is" + opponentId);
        userCloud = UserCloud.getUserCloud();
        userCloudMediator = new UserCloudMediator(localUser, userCloud);
        localUser.register(userCloudMediator);
        userCloud.register(userCloudMediator);

        final SharedPreferences sharedpreferences = getSharedPreferences("FirebaseLabApp", Context.MODE_PRIVATE);

        checkActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendDataDisplay.class);
                startActivity(intent);
            }
        });

        /**
         * put same message in both user's history
         */
        chat = userCloud.passInstance()
                .collection(localUser.getId())
                .document(MESSAGES_KEY)
                .collection(opponentId);

        opponentChat = userCloud.passInstance()
                .collection(opponentId)
                .document(MESSAGES_KEY)
                .collection(localUser.getId());

        initMessageUpdateListener();


        send_button = findViewById(R.id.btn_send);
        send_button.setOnClickListener(view -> sendMessage());

        EditText nameView = findViewById((R.id.user_name));
        nameView.setText(localUser.getId());
    }

    private void sendMessage() {


        final EditText messageView = findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, localUser.getId());
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        chat.add(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });

        opponentChat.add(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
            NotificationBuilder note = new NotificationBuilder(getApplicationContext(), "New message from " + opponentId, "Click to read message in Personal Best", "01");
            note.createNotification();
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });

    }

    private void initMessageUpdateListener() {
        chat.addSnapshotListener((newChatSnapShot, error) -> {
            if (error != null) {
                Log.e(TAG, error.getLocalizedMessage());
                return;
            }

            if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                for (DocumentChange documentChange : documentChanges) {
                    QueryDocumentSnapshot document = documentChange.getDocument();
                    sb.append(document.get(FROM_KEY));
                    sb.append(":\n");
                    sb.append(document.get(TEXT_KEY));
                    sb.append("\n");
                    sb.append("---\n");
                };

                TextView chatView = findViewById(R.id.chat);
                chatView.append(sb.toString());
            }
        });
    }



}
