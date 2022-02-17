package com.example.yot.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yot.Model.ContentModel;
import com.example.yot.R;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ContentModel> contentModelArrayList;
    private BottomSheetDialog bottomSheetDialog;

    private SimpleExoPlayer simpleExoPlayer;
    private DatabaseReference databaseReference;
    private ContentAdapter contentAdapter;

    public ContentAdapter(Context context, ArrayList<ContentModel> contentModelArrayList) {
        this.context = context;
        this.contentModelArrayList = contentModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentModel contentModel = contentModelArrayList.get(position);
        if (contentModel != null) {
            Glide.with(context).asBitmap().load(contentModel.getVideo_uri()).into(holder.thumbnail);
            holder.userVideoTitle.setText(contentModel.getVideo_title());
            holder.userViewsCount.setText(String.valueOf(contentModel.getViews() + " просмотров"));
            holder.userVideoDate.setText(contentModel.getDate());

            setDate(contentModel.getPublisher(), holder.userProfileVideo, holder.userChannelName);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        bottomSheetDialog = new BottomSheetDialog(context);
                        bottomSheetDialog.setCancelable(true);
                        bottomSheetDialog.setContentView(R.layout.activity_player);
                        bottomSheetDialog.setCanceledOnTouchOutside(true);
                        bottomSheetDialog.show();

//                    if (Build.VERSION.SDK_INT >= 21) {
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//                        Window window = getWindow();
//                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                        window.setStatusBarColor(Color.TRANSPARENT);
//                    }
                        showSettingInformation(bottomSheetDialog, contentModel);


//                    Intent intent = new Intent(context, PlayerActivity.class);
//                    intent.putExtra("pos", position);
//                    context.startActivity(intent);
                }
            });
        }
    }

    private void showSettingInformation(BottomSheetDialog bottomSheetDialog, ContentModel contentModel) {
        PlayerView playView = bottomSheetDialog.findViewById(R.id.playView);
        TextView playerVideoViewsCount = bottomSheetDialog.findViewById(R.id.playerVideoViewsCount);
        TextView playerVideoDate = bottomSheetDialog.findViewById(R.id.playerVideoDate);
        TextView playerVideoTitle = bottomSheetDialog.findViewById(R.id.playerVideoTitle);
        RecyclerView recyclerPlayer = bottomSheetDialog.findViewById(R.id.recyclerPlayer);
        getAllVideos(recyclerPlayer);

        playerVideoViewsCount.setText(contentModel.getViews() + " просмотров");
        playerVideoDate.setText(contentModel.getDate());
        playerVideoTitle.setText(contentModel.getVideo_title());

        try {
            simpleExoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(context);
            Uri video = Uri.parse(contentModel.getVideo_uri());
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            playView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
        }


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Channels");
        databaseReference.child(contentModel.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cName = snapshot.child("channel_name").getValue().toString();
                    String cLogo = snapshot.child("channel_logo").getValue().toString();

                    TextView playerName = bottomSheetDialog.findViewById(R.id.playerName);
                    playerName.setText(cName);
                    CircleImageView playerProfile = bottomSheetDialog.findViewById(R.id.playerProfile);
                    Picasso.get().load(cLogo).into(playerProfile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomSheetDialog.show();
    }

    private void getAllVideos(RecyclerView recyclerPlayer) {
        ArrayList<ContentModel> list = new ArrayList<>();
        recyclerPlayer.setLayoutManager(new LinearLayoutManager(context));
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
                    contentAdapter = new ContentAdapter(context, contentModelArrayList);
                    recyclerPlayer.setAdapter(contentAdapter);
                    contentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDate(String publisher, CircleImageView logo, TextView channel_name) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Channels");
        databaseReference.child(publisher).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cName = snapshot.child("channel_name").getValue().toString();
                    String cLogo = snapshot.child("channel_logo").getValue().toString();

                    channel_name.setText(cName);
                    Picasso.get().load(cLogo).into(logo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return contentModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        CircleImageView userProfileVideo;
        TextView userVideoTitle, userChannelName, userViewsCount, userVideoDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            userProfileVideo = itemView.findViewById(R.id.userProfileVideo);
            userVideoTitle = itemView.findViewById(R.id.userVideoTitle);
            userChannelName = itemView.findViewById(R.id.userChannelName);
            userViewsCount = itemView.findViewById(R.id.userViewsCount);
            userVideoDate = itemView.findViewById(R.id.userVideoDate);
        }
    }
}
