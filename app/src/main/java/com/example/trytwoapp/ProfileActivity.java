package com.example.trytwoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID, current_State, senderUserId;

    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton, declineMessageRequestButton;

    private DatabaseReference userRef, chatRequestRef, contactsRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();

        Toast.makeText(this, receiverUserID, Toast.LENGTH_SHORT).show();

        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileName= findViewById(R.id.visit_user_name);
        userProfileStatus = findViewById(R.id.visit_user_status);
        sendMessageRequestButton = findViewById(R.id.add_to_friends_button);
        declineMessageRequestButton = findViewById(R.id.decline_friends_button);
        current_State = "new";

        RetrieveUserInfo();
        

    }


    private void RetrieveUserInfo() {

        userRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && (snapshot.hasChild("image"))){
                    String userImage = snapshot.child("image").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    MessageChatRequest();
                }
                else{
                    String userName = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    MessageChatRequest();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void MessageChatRequest() {
        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(receiverUserID)){
                            String request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                current_State = "request_sent";
                                sendMessageRequestButton.setText("Cancele a petición de chat");
                            }
                            else if (request_type.equals("received")){
                                current_State = "request_received";
                                sendMessageRequestButton.setText("Acepta a petición de amizade.");

                                declineMessageRequestButton.setVisibility(View.VISIBLE);
                                declineMessageRequestButton.setEnabled(true);

                                declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelChatRequest();
                                    }
                                });
                            }

                        }
                        else{
                            contactsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(receiverUserID)){
                                        current_State = "friends";
                                        sendMessageRequestButton.setText("Elimine este amigo");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (!senderUserId.equals(receiverUserID)){
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessageRequestButton.setEnabled(false);

                    if(current_State.equals("new")){
                        sendChatRequest();
                    }
                    if(current_State.equals("request_sent")){
                        CancelChatRequest();
                    }
                    if(current_State.equals("request_received")){
                        AcceptChatRequest();
                    }
                    if (current_State.equals("friends")){
                        removeSingleContact();
                    }

                }
            });

        }
        else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);

        }

    }

    private void removeSingleContact() {
        contactsRef.child(senderUserId).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    contactsRef.child(receiverUserID).child(senderUserId)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                current_State = "new";
                                sendMessageRequestButton.setText("Fagámonos amigos!");

                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                declineMessageRequestButton.setEnabled(false);
                            }

                        }
                    });

                }

            }
        });
    }

    private void AcceptChatRequest() {
        contactsRef.child(senderUserId).child(receiverUserID).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactsRef.child(receiverUserID).child(senderUserId).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                chatRequestRef.child(senderUserId).child(receiverUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    chatRequestRef.child(receiverUserID).child(senderUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    current_State = "friends";
                                                                                    sendMessageRequestButton.setText("Elimina este amigo.");
                                                                                    declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    declineMessageRequestButton.setEnabled(false);



                                                                                }
                                                                            });
                                                                }



                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }
                    }
                });

    }


    private void sendChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserID)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatRequestRef.child(receiverUserID).child(senderUserId)
                            .child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        sendMessageRequestButton.setEnabled(true);
                                        current_State = "request_sent";
                                        sendMessageRequestButton.setText("Cancele a petición de chat");
                                    }

                                }
                            });

            }
        }
    });

}

    private void CancelChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatRequestRef.child(receiverUserID).child(senderUserId)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                current_State = "new";
                                sendMessageRequestButton.setText("Fagámonos amigos!");

                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                declineMessageRequestButton.setEnabled(false);
                            }

                        }
                    });

                }

            }
        });
    }



}