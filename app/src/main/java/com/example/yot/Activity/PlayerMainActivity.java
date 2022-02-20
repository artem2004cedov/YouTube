package com.example.yot.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yot.Adapter.CommentAdapter;
import com.example.yot.Adapter.ContentAdapter;
import com.example.yot.Model.Comment;
import com.example.yot.Model.ContentModel;
import com.example.yot.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.yot.Adapter.ContentAdapter.contentModelArrayList;

public class PlayerMainActivity extends AppCompatActivity {
    private PlayerView playView;
    private ContentModel contentModel;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private SimpleExoPlayer simpleExoPlayer;
    boolean fullscreen = false;
    private CircleImageView playerProfile;
    private TextView playerVideoTitle, playerName, playerVideoViewsCount, playerVideoDate,
            textLike, textDisLike, playerFollowing, playerViewsCount, textCommentUser, commentContUser;
    private RecyclerView recyclerPlayer;
    private ContentAdapter contentAdapter;
    private ImageView playerExpand, likeImage, dislikeImage, notificationVideo;
    private LinearLayout linearComment;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);
        init();
        getData();
        setLike();
        setFollowing();
        setComment();
    }

    private void setComment() {


        // установка последнего сообщение
        FirebaseDatabase.getInstance().
                getReference().
                child("Comments")
                .child(contentModel.getId())
                .orderByChild("timestamp")
                .limitToLast(1).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                textCommentUser.setText(dataSnapshot.child("comment").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Comments").child(contentModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentContUser.setText("Комментарарии" + " " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        linearComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PlayerMainActivity.this);
                bottomSheetDialog.setContentView(R.layout.comment_dialog);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setCanceledOnTouchOutside(true);

                EditText edComment = bottomSheetDialog.findViewById(R.id.edComment);
                CircleImageView profileComment = bottomSheetDialog.findViewById(R.id.profileComment);
                ImageView closeComment = bottomSheetDialog.findViewById(R.id.closeComment);
                ImageView putComment = bottomSheetDialog.findViewById(R.id.putComment);
                TextView textCommentCont = bottomSheetDialog.findViewById(R.id.textCommentCont);

                FirebaseDatabase.getInstance().getReference().child("Comments").child(contentModel.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentContUser.setText("Комментарарии" + " " + dataSnapshot.getChildrenCount());
                        textCommentCont.setText("" + dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                commentList = new ArrayList<>();
                commentAdapter = new CommentAdapter(PlayerMainActivity.this, commentList, contentModel.getId());
                RecyclerView recyclerComment = bottomSheetDialog.findViewById(R.id.recyclerComment);
                recyclerComment.setHasFixedSize(true);
                recyclerComment.setLayoutManager(new LinearLayoutManager(PlayerMainActivity.this));
                recyclerComment.setAdapter(commentAdapter);

                FirebaseDatabase.getInstance().getReference().child("Comments").child(contentModel.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comment comment = snapshot.getValue(Comment.class);
                            commentList.add(comment);
                        }

                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                putComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(edComment.getText().toString())) {
                            HashMap<String, Object> map = new HashMap<>();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(contentModel.getId());
                            // id коммента
                            String id = ref.push().getKey();

                            map.put("id", id);
                            map.put("comment", edComment.getText().toString());
                            map.put("publisher", user.getUid());

                            edComment.setText("");

                            ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PlayerMainActivity.this, "Комментарий добавлен", Toast.LENGTH_SHORT).show();
                                    } else {
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(PlayerMainActivity.this, "Пустое поле", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                closeComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Picasso.get().load(snapshot.child("profile").getValue().toString()).into(profileComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                bottomSheetDialog.show();
            }
        });
    }

    private void setFollowing() {
        playerFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // если нажать пописаться
                if (playerFollowing.getText().toString().equals(("Подписаться"))) {

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((user.getUid())).child("following").child(contentModel.getPublisher()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(contentModel.getPublisher()).child("followers").child(user.getUid()).setValue(true);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((user.getUid())).child("following").child(contentModel.getPublisher()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(contentModel.getPublisher()).child("followers").child(user.getUid()).removeValue();
                }
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(contentModel.getPublisher()).exists()) {
                    playerFollowing.setText("Вы подписаны");
                    notificationVideo.setVisibility(View.VISIBLE);
                    playerFollowing.setTextColor(getColor(R.color.black94));
                } else {
                    playerFollowing.setText("Подписаться");
                    notificationVideo.setVisibility(View.GONE);
                    playerFollowing.setTextColor(getColor(R.color.red));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(contentModel.getPublisher());

        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerViewsCount.setText(dataSnapshot.getChildrenCount() + " подписчиков");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLike() {
        likeImage = findViewById(R.id.likeImage);
        dislikeImage = findViewById(R.id.dislikeImage);

        FirebaseDatabase.getInstance().getReference().child("Likes").child(contentModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUid()).exists()) {
                    likeImage.setImageResource(R.drawable.likefullicon);
                    likeImage.setTag("liked");
                } else {
                    likeImage.setImageResource(R.drawable.likeicon);
                    likeImage.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("DisLikes").child(contentModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user.getUid()).exists()) {
                    dislikeImage.setImageResource(R.drawable.dislikefullicon);
                    dislikeImage.setTag("disliked");
                } else {
                    dislikeImage.setImageResource(R.drawable.dislikeicon);
                    dislikeImage.setTag("dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dislikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dislikeImage.getTag().equals("dislike")) {
                    FirebaseDatabase.getInstance().getReference().child("DisLikes")
                            .child(contentModel.getId()).child(user.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("DisLikes")
                            .child(contentModel.getId()).child(user.getUid()).removeValue();
                }
            }
        });

        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeImage.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(contentModel.getId()).child(user.getUid()).setValue(true);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(contentModel.getId()).child(user.getUid()).removeValue();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Likes").child(contentModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textLike.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("DisLikes").child(contentModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textDisLike.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getData() {
        try {
            simpleExoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(PlayerMainActivity.this);
            Uri video = Uri.parse(contentModel.getVideo_uri());
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            playView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
        }

        playerExpand = playView.findViewById(R.id.playerExpand);
        playerExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullscreen) {
                    playerExpand.setBackground(getDrawable(R.drawable.crop_freeicon));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playView.setLayoutParams(params);
                    fullscreen = false;
                } else {
                    playerExpand.setBackground(getDrawable(R.drawable.fullscreen_exiticon));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playView.getLayoutParams();
                    params.height = params.MATCH_PARENT;
                    playView.setLayoutParams(params);
                    fullscreen = true;
                }
            }
        });

        playerVideoViewsCount.setText(contentModel.getViews() + " просмотров");
        playerVideoDate.setText(contentModel.getDate());
        playerVideoTitle.setText(contentModel.getVideo_title());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Channels");
        databaseReference.child(contentModel.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cName = snapshot.child("channel_name").getValue().toString();
                    String cLogo = snapshot.child("channel_logo").getValue().toString();

                    playerName.setText(cName);
                    Picasso.get().load(cLogo).into(playerProfile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        likeImage = findViewById(R.id.likeImage);
        commentContUser = findViewById(R.id.commentContUser);
        textCommentUser = findViewById(R.id.textCommentUser);
        linearComment = findViewById(R.id.linearComment);
        playerViewsCount = findViewById(R.id.playerViewsCount);
        notificationVideo = findViewById(R.id.notificationVideo);
        playerFollowing = findViewById(R.id.playerFollowing);
        textDisLike = findViewById(R.id.textDisLike);
        textLike = findViewById(R.id.textLike);
        playView = findViewById(R.id.playView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerPlayer = findViewById(R.id.recyclerPlayer);
        playerVideoDate = findViewById(R.id.playerVideoDate);
        playerVideoViewsCount = findViewById(R.id.playerVideoViewsCount);
        playerVideoTitle = findViewById(R.id.playerVideoTitle);
        playerProfile = findViewById(R.id.playerProfile);
        playerName = findViewById(R.id.playerName);
        contentModel = contentModelArrayList.get(getIntent().getIntExtra("pos", 0));
        getAllVideos(recyclerPlayer);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
    }


    private void getAllVideos(RecyclerView recyclerPlayer) {
        ArrayList<ContentModel> list = new ArrayList<>();
        recyclerPlayer.setLayoutManager(new LinearLayoutManager(PlayerMainActivity.this));
        recyclerPlayer.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Content");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ContentModel model = dataSnapshot.getValue(ContentModel.class);

                        list.add(model);
                    }

                    Collections.shuffle(contentModelArrayList);
                    contentAdapter = new ContentAdapter(PlayerMainActivity.this, contentModelArrayList);
                    recyclerPlayer.setAdapter(contentAdapter);
                    contentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
            playView.setLayoutParams(params);
            fullscreen = false;
        } else {
            startActivity(new Intent(PlayerMainActivity.this, MainActivity.class));
        }
    }
}