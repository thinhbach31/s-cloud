package com.example.admin.scloud.screen.library;

import com.example.admin.scloud.data.model.Track;

import java.util.List;

public interface OnClickSongListener {
    void onItemClickSong(List<Track> tracks, int possition);
}
