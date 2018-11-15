package com.example.admin.scloud.screen.library;

import com.example.admin.scloud.data.repository.TrackRepository;
import com.example.admin.scloud.data.source.TrackDataSource;

import java.util.List;

public class LibraryPresenter implements LibraryContract.Presenter,
        TrackDataSource.OnFetchDataListener {

    private LibraryContract.View mView;
    private TrackRepository mRepository;

    public LibraryPresenter(TrackRepository repository) {
        mRepository = repository;
    }

    @Override
    public void loadListSong() {
        mRepository.getTrackLocal(this);
    }

    @Override
    public void cannotLoadListSong() {
        mView.showErrorNotLoadedTrack();
    }

    public void setView(LibraryContract.View view) {
        mView = view;
    }

    @Override
    public void onFectDataSuccess(List data) {
        if (data == null || data.isEmpty()) {
            mView.showNoTrack();
        } else {
            mView.showListTrack(data);
        }
    }

    @Override
    public void onFectDataFailure(String massage) {
        mView.showErrorNotLoadedTrack();
    }
}
