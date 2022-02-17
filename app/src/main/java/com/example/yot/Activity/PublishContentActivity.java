package com.example.yot.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.yot.Adapter.PlayListAdapter;
import com.example.yot.Model.PlayListModel;
import com.example.yot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublishContentActivity extends AppCompatActivity {
    private ImageView publishBack;
    private Button publishNext;
    private VideoView viewViewPublish;
    private CircleImageView profilePublish;
    private TextView namePublish;
    private EditText publishTitle, publishDescription;
    private String type;
    private Uri videoUri;
    private MediaController mediaController;
    private Dialog dialog;
    private ProgressBar progressBarPublish;
    private LinearLayout linearAddPlayList;

    private FirebaseUser user;
    private DatabaseReference databaseReference,reference;
    private StorageReference storageReference;
    private String selectedPlaylist;
    private int videoCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_content);
        init();
        getDataUser();
    }

    private void init() {
        publishBack = findViewById(R.id.publishBack);
        linearAddPlayList = findViewById(R.id.linearAddPlayList);
        publishNext = findViewById(R.id.publishNext);
        progressBarPublish = findViewById(R.id.progressBarPublish);
        viewViewPublish = findViewById(R.id.viewViewPublish);
        profilePublish = findViewById(R.id.profilePublish);
        namePublish = findViewById(R.id.namePublish);
        publishTitle = findViewById(R.id.publishTitle);
        publishDescription = findViewById(R.id.publishDescription);
        mediaController = new MediaController(PublishContentActivity.this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Content");
        storageReference = FirebaseStorage.getInstance().getReference().child("Content");
        reference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        if (intent != null) {
            videoUri = intent.getData();
            viewViewPublish.setVideoURI(videoUri);
            viewViewPublish.setMediaController(mediaController);
            viewViewPublish.start();
        }

        publishBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        publishNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = publishTitle.getText().toString();
                String description = publishDescription.getText().toString();
                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(PublishContentActivity.this, "Пустое поля", Toast.LENGTH_SHORT).show();
                } else {
                    uploadVideoToStorage(title, description);
                }
            }
        });

        linearAddPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void uploadVideoToStorage(String title, String description) {
        final StorageReference sRef = storageReference.child(user.getUid())
                .child(System.currentTimeMillis() + "," + getFileExtension(videoUri));
        sRef.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String videoUri = uri.toString();
                        saveDataToFirebase(title, description, videoUri);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                progressBarPublish.setVisibility(View.VISIBLE);
                progressBarPublish.setProgress((int) progress);
            }
        });



    }

    private void getDataUser() {
        reference.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue().toString();
                    String profile = snapshot.child("profile").getValue().toString();

                    namePublish.setText(name);

                    try {
                        Glide.with(PublishContentActivity.this)
                                .load(profile)
                                .into(profilePublish);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveDataToFirebase(String title, String description, String videoUri) {
        progressBarPublish.setVisibility(View.VISIBLE);
        String currentBase = DateFormat.getDateInstance().format(new Date());
        String id = databaseReference.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("video_title", title);
        map.put("video_description", description);
        map.put("playlist", selectedPlaylist);
        map.put("video_uri", videoUri);
        map.put("publisher", user.getUid());
        map.put("type", "video");
        map.put("views", 0);
        map.put("date", currentBase);

        databaseReference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBarPublish.setVisibility(View.GONE);
                    Toast.makeText(PublishContentActivity.this, "Видео загружено", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PublishContentActivity.this, MainActivity.class).
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();

                    updateVideoCount();
                } else {
                    progressBarPublish.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateVideoCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Playlist");

        int update = videoCount + 1;

        HashMap<String, Object> map = new HashMap<>();
        map.put("videos", update);

        databaseReference.child(user.getUid()).child(selectedPlaylist).updateChildren(map);
    }

    private void showDialog() {
        dialog = new Dialog(PublishContentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.play_list_dialog);
        dialog.setCancelable(true);

        EditText inputPlaylistName = dialog.findViewById(R.id.inputPlaylistName);
        TextView textAdd = dialog.findViewById(R.id.textAdd);

        ArrayList<PlayListModel> playListModels = new ArrayList<>();
        PlayListAdapter playListAdapter;

        RecyclerView recyclerInput = dialog.findViewById(R.id.recyclerInput);
        recyclerInput.setHasFixedSize(true);
        recyclerInput.setLayoutManager(new LinearLayoutManager(PublishContentActivity.this));

        playListAdapter = new PlayListAdapter(PublishContentActivity.this, playListModels, new PlayListAdapter.onClickItemListener() {
            @Override
            public void onItemClick(PlayListModel model) {
                dialog.dismiss();
//              Берет информацию нажатого item
                selectedPlaylist = model.getPlaylist_name();
//              Берет количетво
                videoCount = model.getVideos();
            }
        });

        recyclerInput.setAdapter(playListAdapter);
        showAllPlayList(playListAdapter, playListModels);

        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = inputPlaylistName.getText().toString();
                if (value.isEmpty()) {
                    Toast.makeText(PublishContentActivity.this, "Пустое поля", Toast.LENGTH_SHORT).show();
                } else {
                    createNewPlayList(value);
                }
            }
        });

        checkUserAlrebyHavePlayList(recyclerInput);
        dialog.show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void showAllPlayList(PlayListAdapter playListAdapter, ArrayList<PlayListModel> playListModels) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlist");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    playListModels.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PlayListModel playListModel = dataSnapshot.getValue(PlayListModel.class);
                        playListModels.add(playListModel);
                    }
                    playListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNewPlayList(String value) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Playlist");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("playlist_name", value);
        hashMap.put("videos", 0);
        hashMap.put("uid", firebaseUser.getUid());

        databaseReference.child(firebaseUser.getUid()).child(value).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PublishContentActivity.this, "Новый пейлист создан", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserAlrebyHavePlayList(RecyclerView recyclerInput) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Playlist");
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    recyclerInput.setVisibility(View.VISIBLE);
                } else {
                    recyclerInput.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}