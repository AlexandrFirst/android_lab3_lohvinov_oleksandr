package oleksandr.lohvinov.lab3.adapters;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;

import com.bumptech.glide.RequestManager;
import com.google.android.material.textview.MaterialTextView;

import javax.inject.Inject;

import oleksandr.lohvinov.lab3.R;
import oleksandr.lohvinov.lab3.data.entities.Song;

public class SongAdapter extends BaseSongAdapter {

    private RequestManager glide;

    @Inject
    public SongAdapter(RequestManager glide) {
        super(R.layout.list_item);
        this.glide = glide;
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
        glide.load(song.imageUrl).into(ivItemImage);
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
        if(differ == null){
            differ = new AsyncListDiffer<>(this, diffCallback);
        }
        return differ;
    }
}
