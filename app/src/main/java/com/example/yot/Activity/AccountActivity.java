package com.example.yot.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    private ImageView backAccount;
    private CircleImageView accountProfile;
    private TextView accountName;
    private LinearLayout linearCreateChannel;

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        inite();
        getData();
    }

    private void getData() {
        reference.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    profile = snapshot.child("profile").getValue().toString();

                    accountName.setText(name);

                    try {
                        Picasso.get().load(profile).into(accountProfile);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inite() {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        backAccount = findViewById(R.id.backAccount);
        accountProfile = findViewById(R.id.accountProfile);
        accountName = findViewById(R.id.accountName);
        linearCreateChannel = findViewById(R.id.linearCreateChannel);

        backAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linearCreateChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChannel();
            }
        });
    }

    private void createChannel() {
        reference.child("Channels").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent intent = new Intent(AccountActivity.this,MainActivity.class);
                    intent.putExtra("type","channel");
                    startActivity(intent);
                } else {
                    showDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog() {
        Dialog dialog = new Dialog(AccountActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.channel_bialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        EditText input_channel_name = dialog.findViewById(R.id.input_channel_name);
        EditText input_channel_description = dialog.findViewById(R.id.input_channel_description);
        TextView create_channel = dialog.findViewById(R.id.create_channel);

        create_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input_channel_name.getText().toString();
                String description = input_channel_description.getText().toString();

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Пустое поля", Toast.LENGTH_SHORT).show();
                } else {
                    createNewChannel(name, description, dialog);
                }
            }
        });

        dialog.create();
        dialog.show();
    }

    private void createNewChannel(String name, String description, Dialog dialog) {
        ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this);
        progressDialog.setMessage("Загрузка");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> map = new HashMap<>();
        map.put("channel_name", name);
        map.put("description", description);
        map.put("joined", date);
        map.put("uid", user.getUid());
        map.put("channel_logo", profile);

        reference.child("Channels").child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, "Успех", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}