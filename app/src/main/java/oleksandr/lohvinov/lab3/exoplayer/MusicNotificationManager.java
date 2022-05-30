package oleksandr.lohvinov.lab3.exoplayer;

import static oleksandr.lohvinov.lab3.other.Constants.NOTIFICATION_CHANNEL_ID;
import static oleksandr.lohvinov.lab3.other.Constants.NOTIFICATION_ID;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import oleksandr.lohvinov.lab3.R;

public class MusicNotificationManager {
    private Context context;
    private MediaSessionCompat.Token sessionToken;
    private PlayerNotificationManager.NotificationListener notificationListener;
    private NewSongCallback newSongCallback;

    private PlayerNotificationManager notificationManager;

    public MusicNotificationManager(Context context,
                                    MediaSessionCompat.Token sessionToken,
                                    PlayerNotificationManager.NotificationListener notificationListener,
                                    NewSongCallback newSongCallback) {
        this.context = context;
        this.sessionToken = sessionToken;
        this.notificationListener = notificationListener;
        this.newSongCallback = newSongCallback;


        MediaControllerCompat mediaController = new MediaControllerCompat(context, sessionToken);

        notificationManager = PlayerNotificationManager.createWithNotificationChannel(context, NOTIFICATION_CHANNEL_ID,
                R.string.notification_channel_name, R.string.notification_channel_description, NOTIFICATION_ID,
                new DescriptionAdapter(mediaController), notificationListener);

        notificationManager.setSmallIcon(R.drawable.ic_music_note_24);
        notificationManager.setMediaSessionToken(sessionToken);
    }

    public void showNotification(Player player){
        notificationManager.setPlayer(player);
    }


    private class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {
        private MediaControllerCompat mediaController;

        public DescriptionAdapter(MediaControllerCompat mediaController) {

            this.mediaController = mediaController;
        }

        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            newSongCallback.NewSongCallback();
            return mediaController.getMetadata().getDescription().getTitle();
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return mediaController.getSessionActivity();
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return mediaController.getMetadata().getDescription().getSubtitle();
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            Glide.with(context).asBitmap()
                    .load(mediaController.getMetadata().getDescription().getIconUri())
                    .into(new CustomTarget<Bitmap>(){

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            callback.onBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
            return null;
        }
    }
}
