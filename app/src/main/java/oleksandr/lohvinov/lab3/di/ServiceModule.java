package oleksandr.lohvinov.lab3.di;


import android.content.Context;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ServiceScoped;
import oleksandr.lohvinov.lab3.data.remote.MusicDatabase;

@Module
@InstallIn(ServiceComponent.class)
public final class ServiceModule {

    @ServiceScoped
    @Provides
    public static MusicDatabase provideMusicDatabase(){
        return new MusicDatabase();
    }

    @ServiceScoped
    @Provides
    public static AudioAttributes provideAudioAttributes(){
       return new AudioAttributes.Builder().setContentType(C.CONTENT_TYPE_MUSIC).setUsage(C.USAGE_MEDIA).build();
    }

    @ServiceScoped
    @Provides
    public static SimpleExoPlayer provideExoPlayer(@ApplicationContext Context context, AudioAttributes audioAttributes){
        SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
        simpleExoPlayer.setAudioAttributes(audioAttributes, true);
        simpleExoPlayer.setHandleAudioBecomingNoisy(true);
        return simpleExoPlayer;
    }

    @ServiceScoped
    @Provides
    public static DefaultDataSourceFactory provideDataSourceFactory(@ApplicationContext Context context){
        return new DefaultDataSourceFactory(context, Util.getUserAgent(context, "lab3"));
    }
}
