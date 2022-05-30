package oleksandr.lohvinov.lab3.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import oleksandr.lohvinov.lab3.R;
import oleksandr.lohvinov.lab3.adapters.SongAdapter;
import oleksandr.lohvinov.lab3.ui.viewmodels.MainViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    MainViewModel mainViewModel;

    @Inject
    SongAdapter songAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupRecyclerView();
        subscribeToObserves();
        songAdapter.setOnItemClickListener(song -> {
            mainViewModel.playOrToggleSong(song);
        });
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.rvAllSongs);
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void subscribeToObserves(){
        mainViewModel.getMediaItems().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()){
                case SUCCESS:
                    getView().findViewById(R.id.allSongsProgressBar).setVisibility(View.GONE);
                    songAdapter.setSongs(result.getData());
                    break;
                case ERROR:
                    break;
                case LOADING:
                    getView().findViewById(R.id.allSongsProgressBar).setVisibility(View.VISIBLE);
                    break;
            }
        });
    }
}
