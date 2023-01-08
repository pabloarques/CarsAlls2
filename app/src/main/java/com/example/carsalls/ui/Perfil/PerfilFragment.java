package com.example.carsalls.ui.Perfil;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.carsalls.databinding.FragmentPerfilBinding;
import com.google.firebase.storage.StorageReference;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    String photo = "photo";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PerfilViewModel notificationsViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        StorageReference storageReference;
        String storage_path = "Perfil/*";




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}