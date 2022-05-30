package oleksandr.lohvinov.lab3.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;

import com.google.android.material.textview.MaterialTextView;

import oleksandr.lohvinov.lab3.R;
import oleksandr.lohvinov.lab3.data.entities.Song;

public class SwipeSongAdapter extends BaseSongAdapter {

    public SwipeSongAdapter() {
        super(R.layout.swap_item);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = getSongs().get(position);
        View view = holder.itemView;

        MaterialTextView tvPrimary = view.findViewById(R.id.tvPrimary);

        String text = song.title + " - " + song.subtitle;

        tvPrimary.setText(text);

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
