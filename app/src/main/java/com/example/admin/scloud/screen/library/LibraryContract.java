package com.example.admin.scloud.screen.library;

import com.example.admin.scloud.data.model.Track;

import java.util.List;

public interface LibraryContract {
    interface View {
        void showListTrack(List<Track> tracks);

        void showNoTrack();

        void showErrorNotLoadedTrack();
    }

    interface Presenter {
        void loadListSong();

        void cannotLoadListSong();

        void setView(LibraryContract.View view);
    }
}
