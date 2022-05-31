package oleksandr.lohvinov.lab3.ui.fragments;

import static oleksandr.lohvinov.lab3.other.Constants.STRAIGHT_SONG_LIST;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.RequestManager;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import oleksandr.lohvinov.lab3.R;
import oleksandr.lohvinov.lab3.data.entities.Song;
import oleksandr.lohvinov.lab3.exoplayer.MediaMetadataCompatExt;
import oleksandr.lohvinov.lab3.exoplayer.PlaybackStateCompatExt;
import oleksandr.lohvinov.lab3.ui.viewmodels.MainViewModel;
import oleksandr.lohvinov.lab3.ui.viewmodels.SongViewModel;

@AndroidEntryPoint
public class SongFragment extends Fragment {

    @Inject
    public RequestManager glide;

    private MainViewModel mainViewModel;
    private SongViewModel songViewModel;

    private Song curPlayingSong = null;

    TextView tvSongName;
    ImageView ivSongImage;
    ImageView ivPlayPauseDetail;
    ImageView ivSkipPrevious;
    ImageView ivSkip;
    SeekBar seekBar;
    MaterialTextView tvCurTime;
    MaterialTextView tvSongDuration;

    private PlaybackStateCompat playbackState;

    private boolean shouldUpdateSeekbar = true;

    private String songOrderType = "";

    public SongFragment() {
        super(R.layout.fragment_song);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSongName = getView().findViewById(R.id.tvSongName);
        ivSongImage = getView().findViewById(R.id.ivSongImage);
        ivPlayPauseDetail = getView().findViewById(R.id.ivPlayPauseDetail);
        ivSkipPrevious = getView().findViewById(R.id.ivSkipPrevious);
        ivSkip = getView().findViewById(R.id.ivSkip);
        seekBar = getView().findViewById(R.id.seekBar);
        tvCurTime = getView().findViewById(R.id.tvCurTime);
        tvSongDuration = getView().findViewById(R.id.tvSongDuration);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        subscribeToObserves();

        ivPlayPauseDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curPlayingSong != null) {
                    mainViewModel.playOrToggleSong(curPlayingSong, true);
                }
            }
        });

        ivSkipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.skipToPreviousSong();
            }
        });

        ivSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.skipToNextSong();

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setCurrentPlayerTimeToTextView((long) progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                shouldUpdateSeekbar = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar != null) {
                    mainViewModel.seekTo((long) seekBar.getProgress());
                    shouldUpdateSeekbar = true;
                }
            }
        });

    }

    private void updateTitleAnsSongImage(Song song) {
        String title = song.title + " - " + song.subtitle;

        tvSongName.setText(title);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt = null;
        mmr.setDataSource(getContext(), Uri.parse(curPlayingSong.imageUrl));
        rawArt = mmr.getEmbeddedPicture();
        if (rawArt != null) {
            glide.load(rawArt).into(ivSongImage);
        } else {
            ivSongImage.setImageResource(R.drawable.default_music_icon);
        }


    }

    private void subscribeToObserves() {

        mainViewModel.songOrderType.observe(getViewLifecycleOwner(), it -> {
            if (it == null || it.isEmpty()) {
                mainViewModel.SetMode(STRAIGHT_SONG_LIST);
                return;
            }
            songOrderType = it;
            mainViewModel.SetMode(songOrderType);

        });

        mainViewModel.getMediaItems().observe(getViewLifecycleOwner(), it -> {
            if (it != null) {
                switch (it.getStatus()) {
                    case SUCCESS:
                        if (it.getData() != null) {
                            if (curPlayingSong == null && !it.getData().isEmpty()) {
                                curPlayingSong = it.getData().get(0);
                                updateTitleAnsSongImage(it.getData().get(0));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        mainViewModel.curPlayingSong.observe(getViewLifecycleOwner(), it -> {
            if (it == null) {
                return;
            }
            curPlayingSong = MediaMetadataCompatExt.toSong(it);
            if (curPlayingSong != null) {
                updateTitleAnsSongImage(curPlayingSong);
            }
        });

        mainViewModel.playbackState.observe(getViewLifecycleOwner(), it -> {
            playbackState = it;
            if (PlaybackStateCompatExt.isPlaying(playbackState)) {
                ivPlayPauseDetail.setImageResource(R.drawable.ic_baseline_pause_24);
            } else {
                ivPlayPauseDetail.setImageResource(R.drawable.ic_play);
            }
            if (it == null) {
                seekBar.setProgress(0);
            } else {
                seekBar.setProgress((int) it.getPosition());
            }
        });

        songViewModel.curPlayerPosition().observe(getViewLifecycleOwner(), it -> {
            if (shouldUpdateSeekbar) {
                seekBar.setProgress(it.intValue());
                setCurrentPlayerTimeToTextView(it);
            }
        });

        songViewModel.curSongDuration().observe(getViewLifecycleOwner(), it -> {
            seekBar.setMax(it.intValue());
            DateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
            tvSongDuration.setText(dateFormat.format(it));
        });
    }

    private void setCurrentPlayerTimeToTextView(Long ms) {
        DateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        tvCurTime.setText(dateFormat.format(ms));

    }
}
