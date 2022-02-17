package com.example.yot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yot.Model.PlayListModel;
import com.example.yot.R;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PlayListModel> playListModels;
    private onClickItemListener listener;

    public PlayListAdapter(Context context, ArrayList<PlayListModel> playListModels, onClickItemListener listener) {
        this.context = context;
        this.playListModels = playListModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.blind(playListModels.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return playListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tet_playlist_name, tet_playlist_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tet_playlist_name = itemView.findViewById(R.id.tet_playlist_name);
            tet_playlist_count = itemView.findViewById(R.id.tet_playlist_count);
        }

        public void blind(final PlayListModel model, final onClickItemListener listener) {
            tet_playlist_count.setText(model.getVideos() + " видео");
            tet_playlist_name.setText(model.getPlaylist_name());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(model);
                }
            });
        }
    }

    public interface onClickItemListener {
        void onItemClick(PlayListModel model);
    }
}
