package com.example.admin.scloud.screen.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.admin.s_cloud.R;
import com.example.admin.scloud.OnPlayAtMainListener;
import com.example.admin.scloud.data.model.Track;
import com.example.admin.scloud.screen.TrackListener;
import com.example.admin.scloud.screen.genre.genre_detail.GenreDetailAdapter;
import com.example.admin.scloud.screen.library.OnClickSongListener;
import com.example.admin.scloud.service.MusicService;
import com.example.admin.scloud.utils.ConstantNetwork;
import com.example.admin.scloud.utils.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View, OnClickSongListener, OnPlayAtMainListener {

    private SearchContract.Presenter mPresenter;
    private GenreDetailAdapter mAdapter;
    private RecyclerView mRecyclerSearch;
    private ProgressBar mProgressLoading;
    private String mQuery;
    private TrackListener mTrackListener;
    private boolean mIsBound;
    private MusicService mMusicService;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       Intent intent = new Intent(getContext(),MusicService.class);
        getActivity().bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
        return inflater.inflate(R.layout.fragment_genre_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new SearchPresenter(this);
        setupComponents(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TrackListener) {
            mTrackListener = (TrackListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTrackListener = null;
    }

    @Override
    public void showNoTrackAvailable() {
        Toast.makeText(getContext(), R.string.message_no_track, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTracks(ArrayList<Track> trackArrayList) {
        mAdapter.updateListTrack(trackArrayList);
    }

    @Override
    public void showLoadingIndicator() {
        mProgressLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        mProgressLoading.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingTrackError(String err) {
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    public void setupComponents(View view) {
        mProgressLoading = view.findViewById(R.id.progress_loading);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new GenreDetailAdapter(getContext(), mTrackListener, this);
        mRecyclerSearch = view.findViewById(R.id.recycler_genres_detail);
        mRecyclerSearch.setLayoutManager(linearLayoutManager);
        mRecyclerSearch.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerSearch.setAdapter(mAdapter);
        mRecyclerSearch.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            protected void onLoadMore(int offset) {
                mPresenter.searchTracks(mQuery, ConstantNetwork.LIMIT_DEFAULT, offset);
            }
        });
    }

    public void submitQueryText(String query) {
        mQuery = query;
        mAdapter.clearData();
        mPresenter.searchTracks(mQuery, ConstantNetwork.LIMIT_DEFAULT, ConstantNetwork.OFFSET_DEFAULT);
    }


    ServiceConnection mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mIsBound = true;
                MusicService.MediaBinder binder = (MusicService.MediaBinder) iBinder;
                mMusicService = binder.getMediaService();
                mMusicService.setMainListener(SearchFragment.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
    };

    @Override
    public void onItemClickSong(List<Track> tracks, int possition) {
        mMusicService.setPossition(possition);
        mMusicService.setTracks(tracks,possition);
        mMusicService.play(possition);
    }

    @Override
    public void setTitle(String title) {

    }
}

