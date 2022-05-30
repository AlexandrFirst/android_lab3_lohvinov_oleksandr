package oleksandr.lohvinov.lab3.ui;

import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.RequestManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import oleksandr.lohvinov.lab3.R;
import oleksandr.lohvinov.lab3.adapters.SwipeSongAdapter;
import oleksandr.lohvinov.lab3.data.entities.Song;
import oleksandr.lohvinov.lab3.exoplayer.MediaMetadataCompatExt;
import oleksandr.lohvinov.lab3.exoplayer.PlaybackStateCompatExt;
import oleksandr.lohvinov.lab3.other.Status;
import oleksandr.lohvinov.lab3.ui.viewmodels.MainViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {


    MainViewModel mainViewModel;

    @Inject
    SwipeSongAdapter swipeSongAdapter;

    @Inject
    RequestManager glide;

    private Song curPlayingSong;

    private ViewPager2 pageViewer;

    private PlaybackStateCompat playbackState = null;

    ImageView ivCurSongImage;
    ViewPager2 vpSong;
    ImageView ivPlayPause;
    View navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivCurSongImage = findViewById(R.id.ivCurSongImage);
        vpSong = findViewById(R.id.vpSong);
        ivPlayPause = findViewById(R.id.ivPlayPause);
        navHostFragment = findViewById(R.id.navHostFragment);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        subscribeToObserves();

        pageViewer = findViewById(R.id.vpSong);
        pageViewer.setAdapter(swipeSongAdapter);

        ViewPager2 vpSong = findViewById(R.id.vpSong);
        vpSong.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (PlaybackStateCompatExt.isPlaying(playbackState) == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.getSongs().get(position), true);
                } else {
                    curPlayingSong = swipeSongAdapter.getSongs().get(position);
                }
            }
        });

        findViewById(R.id.ivPlayPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curPlayingSong != null) {
                    mainViewModel.playOrToggleSong(curPlayingSong, true);
                }
            }
        });

        swipeSongAdapter.setOnItemClickListener(it -> {
            Navigation.findNavController(navHostFragment).navigate(R.id.globalActionToSongFragment);
        });

        Navigation.findNavController(navHostFragment).addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()){
                case R.id.songFragment:
                    hideBottomBar();
                    break;
                case R.id.homeFragment:
                    showBottomBar();
                    break;
                default:
                    showBottomBar();
                    break;
            }
        });
    }

    private void switchViewPagerToCurrentSong(Song song) {
        int newItemIndex = swipeSongAdapter.getSongs().indexOf(song);
        if (newItemIndex != -1) {
            pageViewer.setCurrentItem(newItemIndex);
            curPlayingSong = song;
        }
    }

    private void hideBottomBar() {
        ivCurSongImage.setVisibility(View.GONE);
        vpSong.setVisibility(View.GONE);
        ivPlayPause.setVisibility(View.GONE);

    }

    private void showBottomBar() {
        ivCurSongImage.setVisibility(View.VISIBLE);
        vpSong.setVisibility(View.VISIBLE);
        ivPlayPause.setVisibility(View.VISIBLE);

    }

    private void subscribeToObserves() {
        ImageView ivCurSongImage = findViewById(R.id.ivCurSongImage);
        mainViewModel.getMediaItems().observe(this, it -> {
            if (it != null) {
                switch (it.getStatus()) {
                    case SUCCESS:
                        List<Song> songs = it.getData();
                        swipeSongAdapter.setSongs(songs);

                        if (!songs.isEmpty()) {
                            if (curPlayingSong == null) {
                                glide.load(songs.get(0).imageUrl).into(ivCurSongImage);
                            } else {
                                glide.load(curPlayingSong.imageUrl).into(ivCurSongImage);
                            }
                            if (curPlayingSong == null) {
                                return;
                            }
                            switchViewPagerToCurrentSong(curPlayingSong);
                        }
                        break;
                    case ERROR:
                    case LOADING:
                        break;
                }
            }
        });

        mainViewModel.curPlayingSong.observe(this, it -> {
            if (it == null) {
                return;
            }

            curPlayingSong = MediaMetadataCompatExt.toSong(it);
            if (curPlayingSong == null) {
                return;
            }
            glide.load(curPlayingSong.imageUrl).into(ivCurSongImage);
            switchViewPagerToCurrentSong(curPlayingSong);
        });

        mainViewModel.playbackState.observe(this, it -> {
            playbackState = it;
            ImageView playStateImage = findViewById(R.id.ivPlayPause);
            if (PlaybackStateCompatExt.isPlaying(playbackState)) {
                playStateImage.setImageResource(R.drawable.ic_baseline_pause_24);
            } else {
                playStateImage.setImageResource(R.drawable.ic_play);
            }
        });

        mainViewModel.isConnected.observe(this, it -> {
            if (it != null) {
                Status s = it.getContentIfNotHandled().getStatus();
                switch (s) {
                    case ERROR:
                        String defaultMessage = "An unknown error occurred";
                        if (it.getContentIfNotHandled().getMessage() != null) {
                            defaultMessage = it.getContentIfNotHandled().getMessage();
                        }
                        Snackbar.make(findViewById(R.id.rootLayout), defaultMessage, Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
        mainViewModel.networkError.observe(this, it -> {
            if (it != null) {
                Status s = it.getContentIfNotHandled().getStatus();
                switch (s) {
                    case ERROR:
                        String defaultMessage = "An unknown error occurred";
                        if (it.getContentIfNotHandled().getMessage() != null) {
                            defaultMessage = it.getContentIfNotHandled().getMessage();
                        }
                        Snackbar.make(findViewById(R.id.rootLayout), defaultMessage, Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }
}