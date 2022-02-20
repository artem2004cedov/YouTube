package com.example.yot.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yot.Activity.PlayerMainActivity;
import com.example.yot.Model.ContentModel;
import com.example.yot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private Context context;
    public static ArrayList<ContentModel> contentModelArrayList;

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
//                    FirebaseDatabase.getInstance().getReference().child("Content").child(contentModel.getViews())
                    Intent intent = new Intent(context, PlayerMainActivity.class);
                    intent.putExtra("pos", position);
                    context.startActivity(intent);
                }
            });
        }

        holder.settingsVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentModel.getPublisher().equals(FirebaseAuth.getInstance().getUid())) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    bottomSheetDialog.setContentView(R.layout.settingsvideo_dialog_master);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);

                    LinearLayout linearClose = bottomSheetDialog.findViewById(R.id.linearClose);
                    LinearLayout linearDelete = bottomSheetDialog.findViewById(R.id.linearDelete);
                    linearClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });

                    linearDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_delete);
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);

                            TextView textDelete = dialog.findViewById(R.id.textDelete);
                            textDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference().child("Content").child(contentModel.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Видео удалено", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                            TextView textClose = dialog.findViewById(R.id.textClose);
                            textClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    });

                    bottomSheetDialog.show();
                } else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    bottomSheetDialog.setContentView(R.layout.settingsvideo_dialog);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    bottomSheetDialog.show();
                }
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
        ImageView thumbnail, settingsVideo;
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
            settingsVideo = itemView.findViewById(R.id.settingsVideo);
        }
    }
}
