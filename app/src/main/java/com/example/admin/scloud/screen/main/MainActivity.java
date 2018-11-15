package com.example.admin.scloud.screen.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.s_cloud.R;
import com.example.admin.scloud.data.model.Track;
import com.example.admin.scloud.screen.TrackListener;
import com.example.admin.scloud.screen.home.HomeFragment;
import com.example.admin.scloud.screen.playing.PlayingActivity;
import com.example.admin.scloud.screen.search.SearchFragment;
import com.example.admin.scloud.service.MusicService;

public class MainActivity extends AppCompatActivity implements MainContract.View, View.OnClickListener,
        SearchView.OnQueryTextListener, TabLayout.OnTabSelectedListener, TrackListener, MusicService.OnMusicChangeListener {

    private TextView mTextTitle;
    private ImageButton mButtonChangeState;

    private ConstraintLayout mLayoutPlaying;
    private FrameLayout mFrameLayoutContainFragment;
    private HomeFragment mHomeFragment;
    private SearchFragment mSearchFragment;
    private ProgressBar mProgressBar;
    private TabLayout mTabLayout;
    private MusicService mMusicService;
    private MainPresenter mMainPresenter;
    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIsBound = true;
            MusicService.MediaBinder mediaBinder = (MusicService.MediaBinder) iBinder;
            mMusicService = mediaBinder.getMediaService();
            mMusicService.setMusicChangeListener(MainActivity.this);
            mTextTitle.setText(mMusicService.getCurrentTrack().getTitle());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        requestPermission();
        addFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        initializeSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeSearchView(Menu menu) {
        mTabLayout = mHomeFragment.getTabLayout();
        MenuItem searchMenu = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setQueryHint(getString(R.string.message_finding_tracks));
        searchView.setOnQueryTextListener(this);
        searchMenu.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                hideTabLayout();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                showTabLayout();
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
//        Intent getIntent = getIntent();

        switch (view.getId()) {

            //click at playing layout
            case R.id.constraint_playing:
                Intent intent = new Intent(this, MusicService.class);
                startService(intent);
                Intent intentPlaying = new Intent(this, PlayingActivity.class);
                startActivity(intentPlaying);
                break;
            //click at button play/pause
            case R.id.button_main_change_state:

                if (mMusicService.isPlaying()) {
                    mMusicService.pause();
                    mButtonChangeState.setImageResource(R.drawable.ic_play_arrow_24dp);
                } else {
                    mMusicService.resume();
                    mButtonChangeState.setImageResource(R.drawable.ic_pause_black_24dp);
                }
                break;
        }
    }

    private void addFragment() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(mFrameLayoutContainFragment.getId(), mHomeFragment)
                .commit();
    }

    private void initViews() {
        mLayoutPlaying = findViewById(R.id.constraint_playing);
        mFrameLayoutContainFragment = findViewById(R.id.frame_fragment);
        mLayoutPlaying.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress_loading);
        mTextTitle = findViewById(R.id.text_local_song_name);
        mButtonChangeState = findViewById(R.id.button_main_change_state);
        mMainPresenter = new MainPresenter(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (mSearchFragment == null) return false;
        mSearchFragment.submitQueryText(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void showTabLayout() {
        mTabLayout.setVisibility(View.VISIBLE);
        closeSearchFragment();
    }

    private void closeSearchFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void hideTabLayout() {
        mTabLayout.setVisibility(View.GONE);
        openSearchFragment();
    }

    @Override
    public void onDownloadError() {
        Toast.makeText(this, getString(R.string.message_download_error),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloading() {
        Toast.makeText(this, getString(R.string.message_downloading),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadSuccess() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    private void openSearchFragment() {
        mSearchFragment = SearchFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_main, mSearchFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
    }

    public void requestPermission() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onDownloadTrack(Track track) {
        mMainPresenter.onDownloadTrack(track);
    }

    @Override
    public void onMusicPlay() {
        mButtonChangeState.setImageResource(R.drawable.ic_play_arrow_24dp);

    }

    @Override
    public void onMusicPause() {
        mButtonChangeState.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    @Override
    public void setTrack(Track track) {
        mTextTitle.setText(mMusicService.getCurrentTrack().getTitle());
    }
}
