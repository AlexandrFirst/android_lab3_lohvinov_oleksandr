package oleksandr.lohvinov.lab3.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;

import com.bumptech.glide.RequestManager;
import com.google.android.material.textview.MaterialTextView;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import oleksandr.lohvinov.lab3.R;
import oleksandr.lohvinov.lab3.data.entities.Song;

public class SongAdapter extends BaseSongAdapter {

    private RequestManager glide;
    private Context context;

    @Inject
    public SongAdapter(RequestManager glide, @ApplicationContext Context context) {
        super(R.layout.list_item);
        this.glide = glide;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = getSongs().get(position);
        View view = holder.itemView;

        ImageView ivItemImage = view.findViewById(R.id.ivItemImage);
        MaterialTextView tvPrimary = view.findViewById(R.id.tvPrimary);
        MaterialTextView tvSecondary = view.findViewById(R.id.tvSecondary);

        tvPrimary.setText(song.title);
        tvSecondary.setText(song.subtitle);


        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt = null;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        mmr.setDataSource(context, Uri.parse(song.imageUrl));
        rawArt = mmr.getEmbeddedPicture();

        if (rawArt!=null) {
            glide.load(rawArt).into(ivItemImage);
        }



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.ClickListener(song);
                }
            }
        });
    }

    @Override
    AsyncListDiffer differ() {
        if (differ == null) {
            differ = new AsyncListDiffer<>(this, diffCallback);
        }
        return differ;
    }
}
