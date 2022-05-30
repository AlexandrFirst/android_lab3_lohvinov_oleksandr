package oleksandr.lohvinov.lab3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import oleksandr.lohvinov.lab3.data.entities.Song;

public abstract class BaseSongAdapter extends RecyclerView.Adapter<BaseSongAdapter.SongViewHolder> {
    private int layoutId;


    protected DiffUtil.ItemCallback diffCallback;


    public BaseSongAdapter(int layoutId) {
        this.layoutId = layoutId;

        diffCallback = new DiffUtil.ItemCallback<Song>() {
            @Override
            public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
                return oldItem.mediaId.equals(newItem.mediaId);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
                return newItem.hashCode() == oldItem.hashCode();
            }
        };
    }

    @NonNull
    @Override
    public BaseSongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseSongAdapter.
                SongViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(layoutId, parent, false));
    }


    protected OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return getSongs().size();
    }


    class SongViewHolder extends RecyclerView.ViewHolder {

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected AsyncListDiffer<Song> differ = null;

    abstract AsyncListDiffer differ();

    public List<Song> getSongs() {
        return differ().getCurrentList();
    }

    public void setSongs(List<Song> value) {
        differ().submitList(value);
    }
}
