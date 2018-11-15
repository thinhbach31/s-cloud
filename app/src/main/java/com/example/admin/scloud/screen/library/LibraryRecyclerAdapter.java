package com.example.admin.scloud.screen.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.s_cloud.R;
import com.example.admin.scloud.data.model.Track;

import java.util.List;

public class LibraryRecyclerAdapter extends
        RecyclerView.Adapter<LibraryRecyclerAdapter.ViewHolder> {

    private List<Track> mTracks;
    private OnClickSongListener mListener;
    private LayoutInflater mInflater;
    private Context mContext;

    public LibraryRecyclerAdapter(OnClickSongListener listener, Context context) {
        mListener = listener;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setTracks(List<Track> tracks) {
        mTracks = tracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_track_local, viewGroup, false);

        return new ViewHolder(view, mListener, mTracks);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.bindData(mTracks.get(i));

    }

    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextSongName, mTextArtist;
        private ImageView mImageSong;
        private OnClickSongListener mListener;
        private Track mCurentTrack;
        private List<Track> mTracks;
        private Context mContext;


        private ViewHolder(@NonNull final View itemView, OnClickSongListener listener, List<Track> tracks) {
            super(itemView);

            this.mListener = listener;
            this.mTracks = tracks;
            this.mContext = itemView.getContext();
            mTextSongName = itemView.findViewById(R.id.text_local_song);
            mTextArtist = itemView.findViewById(R.id.text_local_artist);
            mImageSong = itemView.findViewById(R.id.image_local_song);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener == null) return;
                    mListener.onItemClickSong(mTracks, getLayoutPosition());
                }
            });

        }

        private void bindData(Track track) {
            mTextSongName.setText(track.getTitle());
            mTextArtist.setText(track.getArtist().getName());
        }
    }
}
