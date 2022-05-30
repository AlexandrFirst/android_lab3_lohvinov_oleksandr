package oleksandr.lohvinov.lab3.di;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import oleksandr.lohvinov.lab3.R;
import oleksandr.lohvinov.lab3.adapters.SwipeSongAdapter;
import oleksandr.lohvinov.lab3.exoplayer.MusicServiceConnection;

@Module
@InstallIn(SingletonComponent.class)
public final class AppModule {

    @Singleton
    @Provides
    public static MusicServiceConnection provideMusicServiceConnection(@ApplicationContext Context context){
        return new MusicServiceConnection(context);
    }

    @Singleton
    @Provides
    public static SwipeSongAdapter provideSwipeSongAdapter(){
        return new SwipeSongAdapter();
    }

    @Singleton
    @Provides
    public static RequestManager provideGlideInstance(@ApplicationContext Context context){
        return Glide.with(context).setDefaultRequestOptions(
                new RequestOptions().placeholder(R.drawable.ic_baseline_alarm_24)
                .error(R.drawable.ic_baseline_alarm_24)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        );
    }
}
